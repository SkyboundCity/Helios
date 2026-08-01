package city.skybound.helios.realm;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import static city.skybound.helios.realm.BiomeSets.END_BIOMES;
import static city.skybound.helios.realm.BiomeSets.NETHER_BIOMES;
import static city.skybound.helios.realm.BiomeSets.OVERWORLD_BIOMES;

public final class BiomeUpdateListener implements Listener {

	private static final RandomBiomeSource OVERWORLD_BIOME_SOURCE = new RandomBiomeSource(OVERWORLD_BIOMES, Realm.OVERWORLD.seed());
	private static final RandomBiomeSource NETHER_BIOME_SOURCE = new RandomBiomeSource(NETHER_BIOMES, Realm.NETHER.seed());
	private static final RandomBiomeSource END_BIOME_SOURCE = new RandomBiomeSource(END_BIOMES, Realm.END.seed());

	private RandomBiomeSource getBiomeSource(final Realm realm) {
		return switch (realm) {
			case OVERWORLD -> OVERWORLD_BIOME_SOURCE;
			case NETHER -> NETHER_BIOME_SOURCE;
			case END -> END_BIOME_SOURCE;
		};
	}

	@EventHandler
	public void onChunkLoad(final ChunkLoadEvent event) {
		final var world = event.getWorld();
		final var realm = Realm.find(world);
		if (realm == null) {
			return;
		}

		final var chunk = event.getChunk();
		final var biomeSource = this.getBiomeSource(realm);

		for (int quartX = 0; quartX < 4; quartX++) {
			for (int quartZ = 0; quartZ < 4; quartZ++) {
				final int worldQuartX = chunk.getX() * 4 + quartX;
				final int worldQuartZ = chunk.getZ() * 4 + quartZ;

				final var biome = biomeSource.biome(worldQuartX, worldQuartZ).bukkitBiome();

				final int blockX = worldQuartX * 4;
				final int blockZ = worldQuartZ * 4;

				for (int blockY = world.getMinHeight();
				     blockY < world.getMaxHeight();
				     blockY += 4) {
					world.setBiome(blockX, blockY, blockZ, biome);
				}
			}
		}
	}

}
