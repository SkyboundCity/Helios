package city.skybound.helios.server;

import city.skybound.helios.Permission;
import city.skybound.helios.config.LangConfig;
import com.google.inject.Inject;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.GameMode;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;
import org.spongepowered.configurate.NodePath;

import static org.incendo.cloud.description.Description.description;

public final class GameModeCommands {

	private final LangConfig langConfig;

	@Inject
	public GameModeCommands(
			final LangConfig langConfig
	) {
		this.langConfig = langConfig;
	}

	public void register(final PaperCommandManager<Source> cm) {
		final var main = cm.commandBuilder("gamemode", "gm")
				.permission(Permission.GAMEMODE)
				.senderType(PlayerSource.class)
				.commandDescription(description("Change your game mode."));

		final var cSurvival = main.literal("survival").handler(this::executeSurvival);
		final var cCreative = main.literal("creative").handler(this::executeCreative);
		final var cAdventure = main.literal("adventure").handler(this::executeAdventure);
		final var cSpectator = main.literal("spectator").handler(this::executeSpectator);

		cm.command(cSurvival);
		cm.command(cCreative);
		cm.command(cAdventure);
		cm.command(cSpectator);

		cm.command(cm.commandBuilder("gms").proxies(cSurvival.build()));
		cm.command(cm.commandBuilder("gmc").proxies(cCreative.build()));
		cm.command(cm.commandBuilder("gma").proxies(cAdventure.build()));
	}


	private void executeSurvival(final CommandContext<PlayerSource> c) {
		execute(c, GameMode.SURVIVAL, "Survival");
	}

	private void executeCreative(final CommandContext<PlayerSource> c) {
		execute(c, GameMode.CREATIVE, "Creative");
	}

	private void executeAdventure(final CommandContext<PlayerSource> c) {
		execute(c, GameMode.ADVENTURE, "Adventure");
	}

	private void executeSpectator(final CommandContext<PlayerSource> c) {
		execute(c, GameMode.SPECTATOR, "Spectator");
	}

	private void execute(final CommandContext<PlayerSource> c, final GameMode gameMode, final String gameModeString) {
		final var sender = c.sender().source();
		if (sender.getGameMode().equals(gameMode)) {
			return;
		}

		sender.setGameMode(gameMode);
		sender.sendMessage(this.langConfig.c(
				NodePath.path("gamemode", "change"),
				Placeholder.unparsed("gamemode", gameModeString)
		));
	}
}
