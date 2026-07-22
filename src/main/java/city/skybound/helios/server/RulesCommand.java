package city.skybound.helios.server;

import city.skybound.helios.HeliosPlugin;
import city.skybound.helios.Permission;
import city.skybound.helios.config.BookDeserializer;
import city.skybound.helios.config.BooksConfig;
import city.skybound.helios.config.ConfigConfig;
import city.skybound.helios.config.LangConfig;
import com.google.inject.Inject;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.NodePath;

import static org.incendo.cloud.component.DefaultValue.constant;
import static org.incendo.cloud.description.Description.description;
import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;

public final class RulesCommand {

	private final HeliosPlugin helios;
	private final BooksConfig booksConfig;
	private final LangConfig langConfig;
	private final ConfigConfig configConfig;

	@Inject
	public RulesCommand(
			final HeliosPlugin helios,
			final BooksConfig booksConfig,
			final LangConfig langConfig,
			final ConfigConfig configConfig
	) {
		this.helios = helios;
		this.booksConfig = booksConfig;
		this.langConfig = langConfig;
		this.configConfig = configConfig;
	}

	public void register(final PaperCommandManager<Source> commandManager) {
		final var main = commandManager.commandBuilder("rules")
				.commandDescription(description("The rules for the server."));

		final var page = main
				// FIXME: max won't adjust with plugin reload.
				.optional("page", integerParser(1, BookDeserializer.pageCount(this.bookNode())), constant(1))
				.handler(c -> c.sender().source().sendMessage(
						BookDeserializer.deserializePage(this.bookNode(), c.<Integer>get("page"))
				));

		final var accept = main.literal("accept", description("Whew, that was a lot of reading."))
				.senderType(PlayerSource.class)
				.handler(c -> {
					final var sender = c.sender().source();
					if (sender.hasPermission(Permission.REALM_OVERWORLD)) {
						sender.sendMessage(this.langConfig.c(NodePath.path("rules", "already-accepted")));
					} else {
						// yes, we're going to send a command as the console to promote the
						// player instead of programmatically doing it with the LuckPerms API.
						// if you feel extreme grievance about this, feel free to hire me to
						// remedy this grave issue. my rate is $250/hr.

						// /lp user <player> parent settrack player passenger
						this.setPlayerParent(sender, "boarding");

						sender.sendMessage(this.langConfig.c(NodePath.path("rules", "accept")));
					}
				});

		commandManager.command(main);
		commandManager.command(page);
		commandManager.command(accept);
	}

	private CommentedConfigurationNode bookNode() {
		return this.booksConfig.rootNode().node("rules");
	}

	private void setPlayerParent(final Player player, final String parentName) {
		this.executeCommand("lp user %s parent settrack player %s".formatted(player.getName(), parentName));
	}

	private void executeCommand(final String command) {
		final Server server = this.helios.getServer();
		server.dispatchCommand(server.getConsoleSender(), command);
	}

}
