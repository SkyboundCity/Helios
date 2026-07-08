package city.skybound.helios.server;

import city.skybound.helios.Helios;
import city.skybound.helios.Permission;
import city.skybound.helios.config.LangConfig;
import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.inject.Inject;
import org.bukkit.command.CommandSender;
import org.spongepowered.configurate.NodePath;

public final class HeliosCommand {

  private final Helios plugin;
  private final LangConfig langConfig;

  @Inject
  public HeliosCommand(
      final Helios plugin,
      final LangConfig langConfig
  ) {
    this.plugin = plugin;
    this.langConfig = langConfig;
  }

  public void register(final PaperCommandManager<CommandSender> commandManager) {
    final var main = commandManager.commandBuilder("helios")
        .meta(CommandMeta.DESCRIPTION, "Core commands for Helios.");

    final var reload = main.literal("reload", ArgumentDescription.of("Reload the plugin's configs."))
        .permission(Permission.RELOAD)
        .handler(c -> {
          if (this.plugin.loadConfiguration()) {
            c.getSender().sendMessage(this.langConfig.c(NodePath.path("reload", "successful")));
          } else {
            c.getSender().sendMessage(this.langConfig.c(NodePath.path("reload", "unsuccessful")));
          }
        });

    commandManager.command(main);
    commandManager.command(reload);
  }

}
