package city.skybound.helios.loop;

import city.skybound.helios.HeliosPlugin;
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
						final var world = player.getWorld();
						if (loc.getY() <= LoopPositions.lowEngage(world)) { // they're too low.
							loc.setY(LoopPositions.lowTo(world));
							Teleport.relative(player, loc);
						} else if (loc.getY() >= LoopPositions.highEngage(world)) { // they're too high.
							loc.setY(LoopPositions.highTo(world));
							Teleport.relative(player, loc);
						}
					}
				}, 1, 1
		);
	}

}
