package city.skybound.helios.server;

import city.skybound.helios.config.LangConfig;
import com.google.inject.Inject;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.Source;
import org.spongepowered.configurate.NodePath;

import static org.incendo.cloud.description.Description.description;

public final class VoteCommand {

	private final LangConfig langConfig;

	@Inject
	public VoteCommand(final LangConfig langConfig) {
		this.langConfig = langConfig;
	}

	public void register(final PaperCommandManager<Source> commandManager) {
		final var main = commandManager.commandBuilder("vote")
				.commandDescription(description("Show a list of voting sites."))
				.handler(c -> c.sender().source().sendMessage(this.langConfig.c(NodePath.path("vote"))));

		commandManager.command(main);
	}

}
