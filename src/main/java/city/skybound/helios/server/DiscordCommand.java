package city.skybound.helios.server;

import city.skybound.helios.config.LangConfig;
import com.google.inject.Inject;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.Source;
import org.spongepowered.configurate.NodePath;

import static org.incendo.cloud.description.Description.description;

public final class DiscordCommand {

	private final LangConfig langConfig;

	@Inject
	public DiscordCommand(final LangConfig langConfig) {
		this.langConfig = langConfig;
	}

	public void register(final PaperCommandManager<Source> commandManager) {
		final var main = commandManager.commandBuilder("discord")
				.commandDescription(description("Links you to our Discord."))
				.handler(c -> c.sender().source().sendMessage(this.langConfig.c(NodePath.path("discord"))));

		commandManager.command(main);
	}

}
