package city.skybound.helios.ascension;

import city.skybound.helios.DurationFormat;
import city.skybound.helios.config.LangConfig;
import com.google.inject.Inject;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;
import org.spongepowered.configurate.NodePath;

import java.util.concurrent.TimeUnit;

import static org.incendo.cloud.bukkit.parser.PlayerParser.playerParser;
import static org.incendo.cloud.description.Description.description;

public final class PlaytimeCommand {

  private final LangConfig langConfig;

  @Inject
  public PlaytimeCommand(
      final LangConfig langConfig
  ) {
    this.langConfig = langConfig;
  }

  public void register(final PaperCommandManager<Source> commandManager) {
    final var main = commandManager.commandBuilder("playtime")
        .commandDescription(description("Check how long you've played."))
        .senderType(PlayerSource.class)
        .optional("player", playerParser())
        .handler(c -> {
          final var sender = c.sender().source();

          c.<Player>optional("player").ifPresentOrElse(
              (target) -> sender.sendMessage(this.langConfig.c(
                  NodePath.path("playtime", "other"),
                  TagResolver.resolver(
                      Placeholder.unparsed(
                          "time_in_hours",
                          DurationFormat.fancifyTime(Playtime.getTimePlayed(target), TimeUnit.HOURS)
                      ),
                      Placeholder.unparsed("time", DurationFormat.fancifyTime(Playtime.getTimePlayed(target))),
                      Placeholder.unparsed("player", target.getName())
                  )
              )), () -> sender.sendMessage(this.langConfig.c(
                  NodePath.path("playtime", "self"),
                  TagResolver.resolver(
                      Placeholder.unparsed(
                          "time_in_hours",
                          DurationFormat.fancifyTime(Playtime.getTimePlayed(sender), TimeUnit.HOURS)
                      ),
                      Placeholder.unparsed("time", DurationFormat.fancifyTime(Playtime.getTimePlayed(sender)))
                  )
              ))
          );
        });

    commandManager.command(main);
  }

}
