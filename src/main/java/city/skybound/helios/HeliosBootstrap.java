package city.skybound.helios;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class HeliosBootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(final BootstrapContext context) {
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
