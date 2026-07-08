package city.skybound.helios.server;

import city.skybound.helios.Permission;
import city.skybound.helios.config.LangConfig;
import com.google.inject.Inject;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.GameMode;
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

	public void register(final PaperCommandManager<Source> commandManager) {
		final var main = commandManager.commandBuilder("gamemode", "gm")
				.permission(Permission.GAMEMODE)
				.senderType(PlayerSource.class)
				.commandDescription(description("Change your game mode."));

		final var survival = main.literal("survival", "s")
				.handler(c -> {
					final var sender = c.sender().source();
					sender.setGameMode(GameMode.SURVIVAL);
					sender.sendMessage(this.langConfig.c(
							NodePath.path("gamemode", "change"),
							Placeholder.unparsed("gamemode", "Survival")
					));
				});

		final var creative = main.literal("creative", "c")
				.handler(c -> {
					final var sender = c.sender().source();
					sender.setGameMode(GameMode.CREATIVE);
					sender.sendMessage(this.langConfig.c(
							NodePath.path("gamemode", "change"),
							Placeholder.unparsed("gamemode", "Creative")
					));
				});

		final var adventure = main.literal("adventure", "a")
				.handler(c -> {
					final var sender = c.sender().source();
					sender.setGameMode(GameMode.ADVENTURE);
					sender.sendMessage(this.langConfig.c(
							NodePath.path("gamemode", "change"),
							Placeholder.unparsed("gamemode", "Adventure")
					));
				});

		commandManager.command(main);
		commandManager.command(survival);
		commandManager.command(creative);
		commandManager.command(adventure);
	}

}
