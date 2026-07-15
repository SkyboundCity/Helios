package city.skybound.helios.transportation;

import city.skybound.helios.Helios;
import city.skybound.helios.Permission;
import city.skybound.helios.config.LangConfig;
import city.skybound.helios.realm.Realm;
import city.skybound.helios.realm.Transposer;
import com.google.inject.Inject;
import io.papermc.paper.event.entity.EntityInsideBlockEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.spongepowered.configurate.NodePath;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Sends players to the correct realms upon entering a portal.
 * <p>
 * <b>This task functions under the assumption that allow-end and
 * enable-nether are false in bukkit.yml and paper-global.yml respectively.</b>
 */
public final class PortalListener implements Listener {

	private final Map<Player, Integer> lastPortalAttempt = new HashMap<>();

	private final Helios helios;
	private final Transposer transposer;

	@Inject
	public PortalListener(
			final Helios helios,
			final Transposer transposer
	) {
		this.helios = helios;
		this.transposer = transposer;
	}

	public void attemptPortal(final Player player) {
		this.lastPortalAttempt.put(player, this.helios.getServer().getCurrentTick());
	}

	@EventHandler
	public void onPortal(final EntityInsideBlockEvent event) {
		final Material portal = event.getBlock().getType();
		if (portal == Material.NETHER_PORTAL || portal == Material.END_PORTAL) {
			event.setCancelled(true);
		}

		if (!(event.getEntity() instanceof final Player player)) {
			return;
		}

		if (this.lastPortalAttempt.containsKey(player)
				&& this.helios.getServer().getCurrentTick() - this.lastPortalAttempt.get(player) < 5) {
			this.attemptPortal(player);
			return;
		}
		this.attemptPortal(player);

		if (portal == Material.NETHER_PORTAL) {
			this.onNetherPortal(player);
		} else if (portal == Material.END_PORTAL) {
			this.onEndPortal(player);
		}
	}

	@EventHandler
	public void onNetherPortalCreate(final PlayerPortalEvent event) {
		// this event is called when a nether portal would be created in the overworld.
		event.setCancelled(true);
	}

	private void onNetherPortal(final Player player) {
		if (Realm.of(player) == Realm.NETHER) {
			this.transposer.transpose(player, Realm.OVERWORLD);
		} else {
			this.transposer.transpose(player, Realm.NETHER);
		}
	}

	private void onEndPortal(final Player player) {
		if (Realm.of(player) == Realm.END) {
			this.transposer.transpose(player, Realm.OVERWORLD);
		} else {
			this.transposer.transpose(player, Realm.END);
		}
	}

}
