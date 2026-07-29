package city.skybound.helios.realm;

import dev.wyck.biome.Biome;
import dev.wyck.worldgen.biome.custom.BiomeSourceContext;
import dev.wyck.worldgen.biome.custom.CustomBiomeSource;

import java.util.Set;

public final class RandomBiomeSource extends CustomBiomeSource {

	private static final int BIOME_CHUNK_SIZE = 96;

	private static final long X_SALT = 0x9E3779B97F4A7C15L;
	private static final long Z_SALT = 0xC2B2AE3D27D4EB4FL;

	private final long seed;

	public RandomBiomeSource(final Set<? extends Biome> possibleBiomes, final long seed) {
		super(possibleBiomes);
		this.seed = seed;
	}

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
	public Biome biome(final BiomeSourceContext context) {
		final long cellX = Math.floorDiv(context.blockX(), BIOME_CHUNK_SIZE);
		final long cellZ = Math.floorDiv(context.blockZ(), BIOME_CHUNK_SIZE);

		final long hash = mix64(this.seed ^ cellX * X_SALT ^ cellZ * Z_SALT);

		final Set<Biome> biomes = this.possibleBiomes();
		final int biomeIndex = (int) Math.floorMod(hash, (long) biomes.size());
		return biomes.stream().skip(biomeIndex).findFirst().orElseThrow();
	}

}
