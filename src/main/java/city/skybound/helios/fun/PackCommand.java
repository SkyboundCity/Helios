package city.skybound.helios.fun;

import city.skybound.helios.config.ConfigConfig;
import city.skybound.helios.config.LangConfig;
import com.google.inject.Inject;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;
import org.spongepowered.configurate.NodePath;

import static org.incendo.cloud.description.Description.description;

public final class PackCommand {

	private final LangConfig langConfig;
	private final ConfigConfig configConfig;

	@Inject
	public PackCommand(
			final LangConfig langConfig,
			final ConfigConfig configConfig
	) {
		this.langConfig = langConfig;
		this.configConfig = configConfig;
	}

	public void register(final PaperCommandManager<Source> commandManager) {
		final var main = commandManager.commandBuilder("pack")
				.senderType(PlayerSource.class)
				.commandDescription(description("Get the fancy server resource pack."))
				.handler(c -> {
					final var sender = c.sender().source();
					sender.setResourcePack(
							this.configConfig.data().resourcePackUrl(),
							this.configConfig.data().resourcePackHash()
					);
					sender.sendMessage(this.langConfig.c(NodePath.path("resource-pack", "sending")));
				});

		commandManager.command(main);
	}

}
