package city.skybound.helios.transportation;

import city.skybound.helios.PotEff;
import city.skybound.helios.realm.Milieu;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public final class TransportationListener implements Listener {

	private static final List<PotionEffectType> ONEROUS_BANNED_EFFECTS = List.of(
			PotionEffectType.SPEED,
			PotionEffectType.JUMP_BOOST,
			PotionEffectType.SLOW_FALLING,
			PotionEffectType.LEVITATION
	);

	/**
	 * Prevents spectator mode.
	 */
	@EventHandler
	public void onModeToSpectator(final PlayerGameModeChangeEvent event) {
		if (!event.getNewGameMode().equals(GameMode.SPECTATOR)) {
			return;
		}

		event.setCancelled(true);

		final var player = event.getPlayer();
		player.setGameMode(GameMode.ADVENTURE);
		player.setFireTicks(100);
		player.getWorld().strikeLightning(player.getLocation());
	}

	/**
	 * Prevents ender pearl and chorus fruit teleportation except in docile realms.
	 */
	@EventHandler
	public void onTeleport(final PlayerTeleportEvent event) {
		final var cause = event.getCause();
		if (cause != PlayerTeleportEvent.TeleportCause.CONSUMABLE_EFFECT
				&& cause != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
			return;
		}

		// allow teleportation in the end.
		if (Milieu.of(event.getPlayer()) == Milieu.DOCILE) {
			return;
		}

		event.setCancelled(true);

		event.getTo().getWorld().spawnParticle(Particle.SMOKE, event.getTo(), 200, 0.1, 0.1, 0.1, 0.1);
	}

	/**
	 * Prevents vehicle usage in onerous realms.
	 */
	@EventHandler
	public void onVehicleEnter(final VehicleEnterEvent event) {
		if (Milieu.of(event.getEntered()) != Milieu.ONEROUS) {
			return;
		}

		final Vehicle vehicle = event.getVehicle();

		if (vehicle.getType() == EntityType.TEXT_DISPLAY) {
			// allow sitting in the nether.
			// (chairs plugin uses text display vehicles.)
			return;
		}

		event.setCancelled(true);

		vehicle.getWorld().createExplosion(vehicle, 2, true, false);
		vehicle.remove();
	}

	/**
	 * Prevents movement-enhancing potions in onerous realms.
	 */
	@EventHandler
	public void onPotionEffect(final EntityPotionEffectEvent event) {
		if (!(event.getEntity() instanceof final Player player)
				|| Milieu.of(player) != Milieu.ONEROUS
				|| event.getNewEffect() == null) {
			return;
		}

		final PotionEffectType type = event.getNewEffect().getType();
		if (!ONEROUS_BANNED_EFFECTS.contains(type)) {
			return;
		}

		event.setCancelled(true);

		player.setGameMode(GameMode.ADVENTURE);
		player.addPotionEffect(PotEff.visible(PotionEffectType.WITHER, 160, 100));
	}

	/**
	 * Removes movement-enhancing potions when transposing into onerous realms.
	 */
	@EventHandler
	public void onWorldChange(final PlayerChangedWorldEvent event) {
		final Player player = event.getPlayer();

		if (Milieu.of(player) != Milieu.ONEROUS) {
			return;
		}

		for (final PotionEffectType type : ONEROUS_BANNED_EFFECTS) {
			player.removePotionEffect(type);
		}
	}

	/**
	 * Prevents elytra usage outside docile realms.
	 */
	@EventHandler
	public void onElytra(final EntityToggleGlideEvent event) {
		if (!(event.getEntity() instanceof final Player player)
				|| Milieu.of(player) == Milieu.DOCILE
				|| !event.isGliding()) {
			return;
		}

		player.setGliding(false);
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_TURTLE_EGG_CRACK, SoundCategory.MASTER, 1, 1.875F);

		// remove their elytra.
		final PlayerInventory inventory = player.getInventory();
		if (inventory.getChestplate().getType() == Material.ELYTRA) {
			inventory.setChestplate(ItemType.AIR.createItemStack());
		}
	}

}
