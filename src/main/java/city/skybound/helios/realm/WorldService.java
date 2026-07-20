package city.skybound.helios.realm;

import com.google.inject.Inject;
import org.bukkit.GameRules;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

/**
 * Handles the creation of abstract realms into concrete worlds.
 */
public final class WorldService {

	private final JavaPlugin plugin;
	private final Logger logger;

	@Inject
	public WorldService(final JavaPlugin plugin, final Logger logger) {
		this.plugin = plugin;
		this.logger = logger;
	}

	public World getWorld(final Realm realm) {
		final World world = this.plugin.getServer().getWorld(realm.key(this.plugin));
		if (world == null) {
			throw new RuntimeException("Could not find world for realm `" + realm + "`.");
		}
		return world;
	}

	public void init() {
		this.createWorlds();
		this.setGameRules();
	}

	private void createWorlds() {
		for (final Realm realm : Realm.values()) {
			this.logger.info("Creating world `{}`.", realm.toString());

			final var world = this.plugin.getServer().createWorld(WorldCreator.ofKey(realm.key(this.plugin))
					// lower black horizon in overworld worlds.
					// FLAT worlds turn black below Y=-60. NORMAL worlds turn black below Y=60.
					.type(WorldType.FLAT)
					.environment(realm.habitat().environment())
					.generator(new VoidGenerator())
			);

			if (world == null) {
				throw new RuntimeException("Failed to create world for realm `" + realm + "`.");
			}
		}
	}

	private void setGameRules() {
		for (final Realm realm : Realm.values()) {
			final World world = this.getWorld(realm);

			world.setGameRule(GameRules.RESPAWN_RADIUS, 0);
			world.setGameRule(GameRules.FIRE_SPREAD_RADIUS_AROUND_PLAYER, 0);
			world.setGameRule(GameRules.MOB_GRIEFING, false);
			world.setGameRule(GameRules.SPREAD_VINES, false);
			world.setGameRule(GameRules.ADVANCE_WEATHER, true);
			world.setGameRule(GameRules.ADVANCE_TIME, true);
			world.setGameRule(GameRules.REDUCED_DEBUG_INFO, false);
			world.setGameRule(GameRules.KEEP_INVENTORY, true);

			// no mob spawning! >:(
			world.setGameRule(GameRules.SPAWN_MOBS, false);
			world.setGameRule(GameRules.SPAWN_PATROLS, false);
			world.setGameRule(GameRules.SPAWN_WANDERING_TRADERS, false);
			world.setGameRule(GameRules.SPAWN_WARDENS, false);
			world.setGameRule(GameRules.SPAWN_PHANTOMS, false);
			world.setGameRule(GameRules.RAIDS, false);
		}
	}

	public Location ornateSpawn(final Realm realm) {
		final var spawn = this.worldSpawn(realm);
		spawn.add(0, 0, -3);
		spawn.setPitch(3);
		return spawn;
	}

	private Location worldSpawn(final Realm realm) {
		return this.getWorld(realm).getSpawnLocation().add(0.5, 0, 0.5);
	}

}
