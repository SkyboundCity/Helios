package city.skybound.helios.nextbot;

import city.skybound.helios.Permission;
import com.google.inject.Inject;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;

import static org.incendo.cloud.description.Description.description;
import static org.incendo.cloud.parser.standard.EnumParser.enumParser;

public final class NextbotCommand {

  private final Nate nate;

  @Inject
  public NextbotCommand(
      final Nate nate
  ) {
    this.nate = nate;
  }

  public void register(final PaperCommandManager<Source> commandManager) {
    final var nextbot = commandManager.commandBuilder("nextbot")
        .commandDescription(description("Manage nextbots."))
        .permission(Permission.NEXTBOT);

    final var killAll = nextbot.literal("kill-all", description("Kill all nextbots globally."))
        .handler(c -> this.nate.killNextbots());

    final var summon = nextbot.literal("summon", description("Summon a nextbot."))
        .senderType(PlayerSource.class)
        .required("type", enumParser(Nextbot.Type.class))
        .handler(c -> {
          final var sender = c.sender().source();
          this.nate.createNextbot(c.get("type"), sender.getLocation());
        });

    commandManager.command(killAll);
    commandManager.command(summon);
  }

}
