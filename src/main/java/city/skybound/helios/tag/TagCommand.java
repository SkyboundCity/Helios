package city.skybound.helios.tag;

import city.skybound.helios.config.LangConfig;
import com.google.inject.Inject;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;
import org.spongepowered.configurate.NodePath;

import static org.incendo.cloud.description.Description.description;
import static org.incendo.cloud.parser.standard.EnumParser.enumParser;

public final class TagCommand {

  private final LangConfig langConfig;
  private final TagGame tagGame;

  @Inject
  public TagCommand(
      final LangConfig langConfig,
      final TagGame tagGame
  ) {
    this.langConfig = langConfig;
    this.tagGame = tagGame;
  }

  public void register(final PaperCommandManager<Source> commandManager) {
    final var main = commandManager.commandBuilder("tag")
        .commandDescription(description("Joins/leaves the game of tag."))
        .senderType(PlayerSource.class)
        .handler(c -> {
          final var sender = c.sender().source();
          if (this.tagGame.togglePlaying(sender)) {
            if (this.tagGame.players().size() <= 1) {
              sender.sendMessage(this.langConfig.c(NodePath.path("tag", "join-first")));
              this.tagGame.it(sender);
            } else {
              sender.sendMessage(this.langConfig.c(NodePath.path("tag", "join")));
            }
          } else {
            sender.sendMessage(this.langConfig.c(NodePath.path("tag", "leave")));
          }
        });

    final var ntb = main.literal("ntb", description("Toggles no tag backs."))
        .handler(c -> {
          if (this.tagGame.toggleNoTagBacks()) {
            c.sender().source().sendMessage(this.langConfig.c(NodePath.path("tag", "no-tag-backs-enabled")));
          } else {
            c.sender().source().sendMessage(this.langConfig.c(NodePath.path("tag", "no-tag-backs-disabled")));
          }
        });

    final var glow = main.literal("glow", description("Sets the glow setting."))
        .required("glow_setting", enumParser(GlowSetting.class))
        .handler(c -> {
          final GlowSetting glowSetting = c.get("glow_setting");
          this.tagGame.glowSetting(glowSetting);

          c.sender().source().sendMessage(this.langConfig.c(
              NodePath.path("tag", "glow"),
              Placeholder.unparsed("glow_setting", glowSetting.name())
          ));
        });

    commandManager.command(main);
    commandManager.command(ntb);
    commandManager.command(glow);
  }

}
