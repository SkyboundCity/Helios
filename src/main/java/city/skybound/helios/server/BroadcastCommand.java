package city.skybound.helios.server;

import city.skybound.helios.ChatFormat;
import city.skybound.helios.Permission;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.Source;

import static org.incendo.cloud.description.Description.description;
import static org.incendo.cloud.parser.standard.StringParser.greedyStringParser;

public final class BroadcastCommand {

  public void register(final PaperCommandManager<Source> commandManager) {
    final var main = commandManager.commandBuilder("broadcast")
        .commandDescription(description("Broadcast a message to the server."))
        .permission(Permission.BROADCAST)
        .required("message", greedyStringParser())
        .handler(c -> c.sender().source().getServer().sendMessage(ChatFormat.miniMessage(c.<String>get("message"))));

    commandManager.command(main);
  }

}
