package city.skybound.helios.server;

import city.skybound.helios.config.LangConfig;
import city.skybound.helios.soul.Charon;
import com.google.inject.Inject;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;
import org.spongepowered.configurate.NodePath;

import static org.incendo.cloud.description.Description.description;

public final class MarkdownCommand {

  private final LangConfig langConfig;
  private final Charon charon;

  @Inject
  public MarkdownCommand(
      final LangConfig langConfig,
      final Charon charon
  ) {
    this.langConfig = langConfig;
    this.charon = charon;
  }

  public void register(final PaperCommandManager<Source> commandManager) {
    final var main = commandManager.commandBuilder("markdown")
        .commandDescription(description("Toggle markdown chat formatting."))
        .senderType(PlayerSource.class)
        .handler(c -> {
          final var sender = c.sender().source();
          if (this.charon.grab(sender).toggleMarkdown()) {
            sender.sendMessage(this.langConfig.c(NodePath.path("markdown", "enabled")));
          } else {
            sender.sendMessage(this.langConfig.c(NodePath.path("markdown", "disabled")));
          }
        });

    commandManager.command(main);
  }

}
