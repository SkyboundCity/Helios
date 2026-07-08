package city.skybound.helios.server;

import city.skybound.helios.Helios;
import city.skybound.helios.Permission;
import city.skybound.helios.config.LangConfig;
import com.google.inject.Inject;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.Source;
import org.spongepowered.configurate.NodePath;

import static org.incendo.cloud.description.Description.description;

public final class HeliosCommand {

	private final Helios plugin;
	private final LangConfig langConfig;

	@Inject
	public HeliosCommand(
			final Helios plugin,
			final LangConfig langConfig
	) {
		this.plugin = plugin;
		this.langConfig = langConfig;
	}

	public void register(final PaperCommandManager<Source> commandManager) {
		final var main = commandManager.commandBuilder("helios")
				.commandDescription(description("Core commands for Helios."));

		final var reload = main.literal("reload", description("Reload the plugin's configs."))
				.permission(Permission.RELOAD)
				.handler(c -> {
					if (this.plugin.loadConfiguration()) {
						c.sender().source().sendMessage(this.langConfig.c(NodePath.path("reload", "successful")));
					} else {
						c.sender().source().sendMessage(this.langConfig.c(NodePath.path("reload", "unsuccessful")));
					}
				});

		commandManager.command(main);
		commandManager.command(reload);
	}

}
