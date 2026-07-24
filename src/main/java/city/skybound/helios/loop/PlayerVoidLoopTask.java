package city.skybound.helios.loop;

import city.skybound.helios.HeliosPlugin;
import city.skybound.helios.realm.Realm;
import com.google.inject.Inject;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public final class PlayerVoidLoopTask {

	private final HeliosPlugin plugin;

	@Inject
	public PlayerVoidLoopTask(
			final HeliosPlugin plugin
	) {
		this.plugin = plugin;
	}

	public void start() {
		final Server server = this.plugin.getServer();
		server.getScheduler().runTaskTimer(
				this.plugin, () -> {
					for (final Player player : server.getOnlinePlayers()) {
						final Location loc = player.getLocation();
						final var realm = Realm.of(player);
						if (loc.getY() <= RealmPositions.loopMinEngageY(realm)) { // they're too low.
							loc.setY(RealmPositions.loopMinToY(realm));
							Teleport.relative(player, loc);
						} else if (loc.getY() >= RealmPositions.loopMaxEngageY(realm)) { // they're too high.
							loc.setY(RealmPositions.loopMaxToY(realm));
							Teleport.relative(player, loc);
						}
					}
				}, 1, 1
		);
	}

}
