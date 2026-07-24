package city.skybound.helios;

import city.skybound.helios.realm.Realm;
import dev.wyck.keys.ResourceKey;
import dev.wyck.level.dimension.Dimension;
import dev.wyck.registry.bootstrap.BootstrapDimensionRegistry;
import dev.wyck.registry.bootstrap.Composer;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import static city.skybound.helios.loop.RealmPositions.technicalHeight;
import static city.skybound.helios.loop.RealmPositions.technicalMinY;

public final class HeliosBootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(final BootstrapContext context) {
		// register custom dimension types during the bootstrap phase as if they were in a datapack.
		final BootstrapDimensionRegistry registry = BootstrapDimensionRegistry.compose(context, Composer.DATAPACK);

		for (final Realm realm : Realm.values()) {
			context.getLogger().info("Creating dimension for realm {}", realm.toString());

			final var dimBase = switch (realm.habitat()) {
				case WHITE -> ResourceKey.minecraft("overworld");
				case RED -> ResourceKey.minecraft("the_nether");
				case BLACK -> ResourceKey.minecraft("the_end");
			};

			// queue dimension type for registration.
			registry.queue(Dimension.reference(dimBase)
					.toBuilder()
					.resourceKey(realm.wyckKey())
					.minY(technicalMinY(realm))
					.height(technicalHeight(realm))
					.logicalHeight(technicalHeight(realm))
					.build());
		}

		context.getLogger().info("Finished registering dimension types");

		context.getLifecycleManager().registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY.newHandler(
				event -> {
					try {
						// retrieve the URI of the datapack folder.
						final URI uri = Objects.requireNonNull(
								this.getClass().getResource("/helios_datapack"),
								"Bundled Helios datapack is missing"
						).toURI();

						// discover the pack. the ID is set to "provided", which indicates to
						// a server owner that your plugin includes this data pack.
						event.registrar().discoverPack(uri, "provided");
					} catch (URISyntaxException | IOException e) {
						throw new RuntimeException(e);
					}
				}
		));
	}

}
