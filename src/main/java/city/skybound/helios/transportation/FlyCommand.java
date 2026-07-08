package city.skybound.helios.transportation;

import city.skybound.helios.Permission;
import city.skybound.helios.config.LangConfig;
import city.skybound.helios.soul.Charon;
import com.google.inject.Inject;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;
import org.spongepowered.configurate.NodePath;

import static org.incendo.cloud.description.Description.description;

public final class FlyCommand {

  private final Charon charon;
  private final LangConfig langConfig;
  private final FlightService flightService;

  @Inject
  public FlyCommand(
      final Charon charon,
      final LangConfig langConfig,
      final FlightService flightService
  ) {
    this.charon = charon;
    this.langConfig = langConfig;
    this.flightService = flightService;
  }

  public void register(final PaperCommandManager<Source> commandManager) {
    final var main = commandManager.commandBuilder("fly")
        .commandDescription(description("Bends the space-time continuum."))
        .permission(Permission.FLY)
        .senderType(PlayerSource.class)
        .handler(c -> {
          final var sender = c.sender().source();
          if (this.charon.grab(sender).toggleFlyBypassEnabled()) {
            sender.sendMessage(this.langConfig.c(NodePath.path("fly", "enabled")));
            this.flightService.enableFlight(sender);
          } else {
            sender.sendMessage(this.langConfig.c(NodePath.path("fly", "disabled")));
            this.flightService.disableFlight(sender);
          }
        });

    commandManager.command(main);
  }

}
