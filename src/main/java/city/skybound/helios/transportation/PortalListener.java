package city.skybound.helios.transportation;

import city.skybound.helios.realm.Realm;
import city.skybound.helios.realm.Transposer;
import com.google.inject.Inject;
import io.papermc.paper.event.entity.EntityInsideBlockEvent;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Sends players to the correct realms upon entering a portal.
 * <p>
 * <b>This task functions under the assumption that allow-end and
 * enable-nether are false in bukkit.yml and paper-global.yml respectively.</b>
 */
public final class PortalListener implements Listener {

	private final Map<Player, Integer> lastPortalAttempt = new HashMap<>();

	private final JavaPlugin plugin;
	private final Transposer transposer;

	@Inject
	public PortalListener(
			final JavaPlugin plugin,
			final Transposer transposer
	) {
		this.plugin = plugin;
		this.transposer = transposer;
	}

	public void attemptPortal(final Player player) {
		this.lastPortalAttempt.put(player, this.plugin.getServer().getCurrentTick());
	}

	@EventHandler
	public void onPortal(final EntityInsideBlockEvent event) {
		final Material portal = event.getBlock().getType();
		if (portal != Material.NETHER_PORTAL && portal != Material.END_PORTAL) {
			return;
		}

		event.setCancelled(true);

		if (!(event.getEntity() instanceof final Player player)) {
			return;
		}

		if (this.lastPortalAttempt.containsKey(player)
				&& this.plugin.getServer().getCurrentTick() - this.lastPortalAttempt.get(player) < 5) {
			this.attemptPortal(player);
			return;
		}

		this.attemptPortal(player);

		final Realm from = Realm.of(player);
		final Realm to;
		if (portal == Material.NETHER_PORTAL) {
			to = from == Realm.NETHER ? Realm.OVERWORLD : Realm.NETHER;
		} else {
			to = from == Realm.END ? Realm.OVERWORLD : Realm.END;
		}

		this.transposer.transpose(player, to);

		player.getScheduler().run(
				this.plugin,
				_ -> player.playSound(Sound.sound(
						org.bukkit.Sound.BLOCK_PORTAL_TRAVEL,
						Sound.Source.AMBIENT,
						0.4F,
						this.randomFloatBetween(0.9F, 1.1F)
				)),
				null
		);
	}

	@EventHandler
	public void onNetherPortalCreate(final PlayerPortalEvent event) {
		// this event is called when a nether portal would be created in the overworld.
		event.setCancelled(true);
	}

	private float randomFloatBetween(final float min, final float max) {
		return min + ThreadLocalRandom.current().nextFloat() * (max - min);
	}

}
