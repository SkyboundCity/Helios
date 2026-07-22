package city.skybound.helios.realm;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.keys.tags.BiomeTagKeys;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import java.util.List;
import java.util.Random;

public final class VoidGenerator extends ChunkGenerator {

	private final MixedBagBiomeProvider mixedBagBiomeProvider = new MixedBagBiomeProvider();

	@Override
	public Location getFixedSpawnLocation(final World world, final Random random) {
		return new Location(world, 0.5D, 65.0D, 0.5D);
	}

	@Override
	public BiomeProvider getDefaultBiomeProvider(final WorldInfo worldInfo) {
		return this.mixedBagBiomeProvider;
	}

	public static final class MixedBagBiomeProvider extends BiomeProvider {

		private static final List<Biome> WHITE_BIOMES = List.copyOf(RegistryAccess
				.registryAccess()
				.getRegistry(RegistryKey.BIOME)
				.getTagValues(BiomeTagKeys.IS_OVERWORLD));

		private static final List<Biome> RED_BIOMES = List.copyOf(RegistryAccess
				.registryAccess()
				.getRegistry(RegistryKey.BIOME)
				.getTagValues(BiomeTagKeys.IS_NETHER));

		private static final List<Biome> BLACK_BIOMES = List.copyOf(RegistryAccess
				.registryAccess()
				.getRegistry(RegistryKey.BIOME)
				.getTagValues(BiomeTagKeys.IS_END));

		private static final int BIOME_CHUNK_SIZE = 96;

		private static final long X_SALT = 0x9E3779B97F4A7C15L;
		private static final long Z_SALT = 0xC2B2AE3D27D4EB4FL;

		/**
		 * Scrambles similar input values into thoroughly different output values.
		 * <p>
		 * Overflow is intentional: Java's long arithmetic wraps predictably,
		 * making this deterministic across machines.
		 */
		private static long mix64(long value) {
			value = (value ^ value >>> 30) * 0xBF58476D1CE4E5B9L;
			value = (value ^ value >>> 27) * 0x94D049BB133111EBL;
			return value ^ value >>> 31;
		}

		@Override
		public Biome getBiome(final WorldInfo worldInfo, final int x, final int y, final int z) {
			final long cellX = Math.floorDiv(x, BIOME_CHUNK_SIZE);
			final long cellZ = Math.floorDiv(z, BIOME_CHUNK_SIZE);

			final long hash = mix64(
					worldInfo.getSeed()
							^ cellX * X_SALT
							^ cellZ * Z_SALT
			);

			final List<Biome> biomes = this.getBiomes(worldInfo);
			final int biomeIndex = (int) Math.floorMod(hash, (long) biomes.size());
			return biomes.get(biomeIndex);
		}

		@Override
		public List<Biome> getBiomes(final WorldInfo worldInfo) {
			return switch (Habitat.from(worldInfo.getEnvironment())) {
				case WHITE -> WHITE_BIOMES;
				case RED -> RED_BIOMES;
				case BLACK -> BLACK_BIOMES;
			};
		}

	}

}
