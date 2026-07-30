package city.skybound.helios.realm;

import city.skybound.helios.loop.RealmPositions;
import com.google.inject.Inject;
import dev.wyck.biome.Biomes;
import dev.wyck.level.LevelCreator;
import dev.wyck.level.LevelType;
import dev.wyck.worldgen.chunk.ChunkGenerator;
import dev.wyck.worldgen.chunk.flat.FlatLevelGeneratorSettings;
import org.bukkit.GameRules;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.util.Set;

import static city.skybound.helios.realm.BiomeSets.BLACK_BIOMES;
import static city.skybound.helios.realm.BiomeSets.RED_BIOMES;
import static city.skybound.helios.realm.BiomeSets.WHITE_BIOMES;
import static com.google.common.base.Preconditions.checkState;

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

	public static Location defaultWorldSpawn(final World world) {
		return new Location(world, 0.5D, 65D, 0.5D);
	}

	public World getWorld(final Realm realm) {
		final World world = this.plugin.getServer().getWorld(realm.key());
		if (world == null) {
			throw new IllegalStateException("Could not find world for realm " + realm);
		}
		return world;
	}

	public void init() {
		this.createWorlds();
		this.verifyWorldPositions();
		this.configureWorlds();
	}

	private Set<dev.wyck.biome.Biome> getRealmBiomes(final Realm realm) {
		return switch (realm.habitat()) {
			case WHITE -> WHITE_BIOMES;
			case RED -> RED_BIOMES;
			case BLACK -> BLACK_BIOMES;
		};
	}

	private void createWorlds() {
		for (final Realm realm : Realm.values()) {
			this.logger.info("Creating world for realm {}", realm.toString());

			final var flatLevelGeneratorSettings = FlatLevelGeneratorSettings.builder()
					.layer(Material.AIR, 1)
					.biome(Biomes.THE_VOID)
					.decoration(false)
					.structures(Set.of())
					.build();

			final var biomeSource = new RandomBiomeSource(this.getRealmBiomes(realm), realm.seed());

			final var chunkGenerator = ChunkGenerator.flat()
					.settings(flatLevelGeneratorSettings)
					.biomeSource(biomeSource)
					.build();

			LevelCreator.builder()
					.resourceKey(realm.wyckKey())
					.name(realm.toString())
					.dimension(realm.wyckKey())
					.seed(realm.seed())
					.generateStructures(false)
					// lower black horizon in overworld
					// FLAT worlds turn black below min_y whereas NORMAL worlds turn black below Y=63
					// see ClientLevel#getHorizonHeight
					.type(LevelType.FLAT)
					// used in VoidGenerator to determine biome set
					// TODO: don't do this?
					.environment(realm.habitat().environment())
					.generator(chunkGenerator)
					.create();
		}

		this.logger.info("Finished creating worlds");
	}

	private void verifyWorldPositions() {
		for (final Realm realm : Realm.values()) {
			final World world = this.getWorld(realm);

			checkState(
					world.getMinHeight() == RealmPositions.technicalMinY(realm),
					"%s has min Y %s, expected %s",
					realm,
					world.getMinHeight(),
					RealmPositions.technicalMinY(realm)
			);

			checkState(
					world.getMaxHeight() == RealmPositions.maxYExclusive(realm),
					"%s has max Y %s, expected %s",
					realm,
					world.getMaxHeight(),
					RealmPositions.maxYExclusive(realm)
			);
		}
	}

	private void configureWorlds() {
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
			world.setGameRule(GameRules.COMMAND_BLOCKS_WORK, false);

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
