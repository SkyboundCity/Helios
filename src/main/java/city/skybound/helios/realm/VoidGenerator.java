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
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

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

		@Override
		public Biome getBiome(final WorldInfo worldInfo, final int x, final int y, final int z) {
			final long seed = Long.parseLong(
					""
							// depend on signs of x and z because we take absolute values below.
							+ (x >= 0 ? 1 : 0)
							+ (z >= 0 ? 1 : 0)
							// concatenate x and z to avoid additive commutativity.
							// note the integer (floored) division.
							+ (Math.abs(x) / BIOME_CHUNK_SIZE)
							+ (Math.abs(z) / BIOME_CHUNK_SIZE)
			);

			final RandomGenerator random = RandomGeneratorFactory.getDefault().create(seed);

			final List<Biome> biomes = this.getBiomes(worldInfo);
			return biomes.get(random.nextInt(biomes.size()));
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
