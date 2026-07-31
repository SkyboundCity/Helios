package city.skybound.helios.realm;

import dev.wyck.biome.Biome;
import dev.wyck.keys.ResourceKey;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.keys.tags.BiomeTagKeys;
import net.kyori.adventure.key.Keyed;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

public final class BiomeSets {

	public static final Set<Biome> OVERWORLD_BIOMES = RegistryAccess
			.registryAccess()
			.getRegistry(RegistryKey.BIOME)
			.getTagValues(BiomeTagKeys.IS_OVERWORLD)
			.stream()
			.map(BiomeSets::toBiome)
			.collect(toSet());

	public static final Set<Biome> NETHER_BIOMES = RegistryAccess
			.registryAccess()
			.getRegistry(RegistryKey.BIOME)
			.getTagValues(BiomeTagKeys.IS_NETHER)
			.stream()
			.map(BiomeSets::toBiome)
			.collect(toSet());

	public static final Set<Biome> END_BIOMES = RegistryAccess
			.registryAccess()
			.getRegistry(RegistryKey.BIOME)
			.getTagValues(BiomeTagKeys.IS_END)
			.stream()
			.map(BiomeSets::toBiome)
			.collect(toSet());

	private BiomeSets() {
	}

	private static Biome toBiome(final Keyed biome) {
		return Biome.reference(ResourceKey.minecraft(biome.key().value())).wrap();
	}

	public static Set<Biome> getBiomes(final Realm realm) {
		return switch (realm) {
			case OVERWORLD -> OVERWORLD_BIOMES;
			case NETHER -> NETHER_BIOMES;
			case END -> END_BIOMES;
		};
	}

}
