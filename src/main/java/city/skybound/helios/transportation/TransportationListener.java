package city.skybound.helios.transportation;

import city.skybound.helios.Helios;
import city.skybound.helios.PotEff;
import city.skybound.helios.config.LangConfig;
import city.skybound.helios.realm.Milieu;
import city.skybound.helios.soul.Charon;
import com.google.inject.Inject;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.NodePath;

import java.util.List;

public final class TransportationListener implements Listener {

	private static final List<PotionEffectType> ONEROUS_BANNED_EFFECTS = List.of(
			PotionEffectType.SPEED,
			PotionEffectType.JUMP_BOOST,
			PotionEffectType.SLOW_FALLING,
			PotionEffectType.LEVITATION
	);

	private final LangConfig langConfig;
	private final Helios plugin;
	private final Charon charon;

	@Inject
	public TransportationListener(
			final LangConfig langConfig,
			final Helios plugin,
			final Charon charon
	) {
		this.langConfig = langConfig;
		this.plugin = plugin;
		this.charon = charon;
	}

	/**
	 * Prevents spectator mode.
	 */
	@EventHandler
	public void onModeToSpectator(final PlayerGameModeChangeEvent event) {
		if (event.getNewGameMode() == GameMode.SPECTATOR) {
			event.setCancelled(true);
			final var player = event.getPlayer();
			player.setGameMode(GameMode.ADVENTURE);
			player.setFireTicks(100);
			player.getWorld().strikeLightning(player.getLocation());
		}
	}

	/**
	 * Prevents ender pearls and chorus fruit teleportation except in docile realms.
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

		if (event.getEntered() instanceof final Player player) {
			final Vehicle vehicle = event.getVehicle();

			if (vehicle.getType() == EntityType.TEXT_DISPLAY) {
				// allow sitting in the nether.
				// (chairs plugin uses text display vehicles.)
				return;
			}

			vehicle.getWorld().createExplosion(vehicle, 2, true, false);
			vehicle.remove();
		} else {
			event.setCancelled(true);
		}
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
		if (ONEROUS_BANNED_EFFECTS.contains(type)) {
			event.setCancelled(true);
			player.setGameMode(GameMode.ADVENTURE);
			player.addPotionEffect(PotEff.visible(PotionEffectType.WITHER, 160, 100));
		}
	}

	/**
	 * Remove leftover or banned effects.
	 */
	@EventHandler
	public void onWorldChange(final PlayerChangedWorldEvent event) {
		final Player player = event.getPlayer();

		// remove leftover blindness from the nether.
		final @Nullable PotionEffect blindness = player.getPotionEffect(PotionEffectType.BLINDNESS);
		if (blindness != null && blindness.isInfinite()) {
			player.removePotionEffect(PotionEffectType.BLINDNESS);
		}

		// remove banned effects when going into nether.
		if (Milieu.of(player) == Milieu.ONEROUS) {
			for (final PotionEffectType type : ONEROUS_BANNED_EFFECTS) {
				player.removePotionEffect(type);
			}
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
		player.playSound(player.getLocation(), Sound.ENTITY_TURTLE_EGG_CRACK, SoundCategory.MASTER, 100, 2);

		// remove their elytra.
		final PlayerInventory inventory = player.getInventory();
		if (inventory.getChestplate() != null && inventory.getChestplate().getType() == Material.ELYTRA) {
			inventory.setChestplate(new ItemStack(Material.AIR));
		}
	}

}
