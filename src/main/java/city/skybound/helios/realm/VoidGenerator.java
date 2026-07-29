package city.skybound.helios.realm;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import java.util.List;
import java.util.Random;

import static city.skybound.helios.realm.WorldService.defaultWorldSpawn;

public final class VoidGenerator extends ChunkGenerator {

	private final VoidBiomeProvider biomeProvider = new VoidBiomeProvider();

	@Override
	public Location getFixedSpawnLocation(final World world, final Random random) {
		return defaultWorldSpawn(world);
	}

	@Override
	public BiomeProvider getDefaultBiomeProvider(final WorldInfo worldInfo) {
		return this.biomeProvider;
	}

	public static final class VoidBiomeProvider extends BiomeProvider {

		private static final List<Biome> BIOMES = List.of(Biome.THE_VOID);

		@Override
		public Biome getBiome(final WorldInfo worldInfo, final int x, final int y, final int z) {
			return BIOMES.getFirst();
		}

		@Override
		public List<Biome> getBiomes(final WorldInfo worldInfo) {
			return BIOMES;
		}

	}

}
