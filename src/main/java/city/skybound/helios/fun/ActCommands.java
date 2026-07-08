package city.skybound.helios.fun;

import city.skybound.helios.Permission;
import city.skybound.helios.config.ConfigConfig;
import city.skybound.helios.config.LangConfig;
import com.google.inject.Inject;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;
import org.spongepowered.configurate.NodePath;

import java.util.Random;

import static org.incendo.cloud.bukkit.parser.PlayerParser.playerParser;
import static org.incendo.cloud.description.Description.description;

public final class ActCommands {

  private final LangConfig langConfig;
  private final ConfigConfig configConfig;

  @Inject
  public ActCommands(
      final LangConfig langConfig,
      final ConfigConfig configConfig
  ) {
    this.langConfig = langConfig;
    this.configConfig = configConfig;
  }

  public void register(final PaperCommandManager<Source> commandManager) {
    final var zap = commandManager.commandBuilder("zap")
        .senderType(PlayerSource.class)
        .permission(Permission.ZAP)
        .commandDescription(description("Kentucky Fried Player"))
        .optional("player", playerParser())
        .handler(c -> {
          final var sender = c.sender().source();
          final Player target = c.<Player>optional("player").orElse((sender));

          target.getWorld().strikeLightning(target.getLocation());

          if (c.<Player>optional("player").isPresent()) {
            sender.getServer().sendMessage(this.langConfig.c(
                NodePath.path("act", "zap-other"),
                TagResolver.resolver(
                    Placeholder.component("issuer", sender.displayName()),
                    Placeholder.component("target", target.displayName())
                )
            ));
          } else {
            sender.getServer().sendMessage(this.langConfig.c(
                NodePath.path("act", "zap-self"),
                TagResolver.resolver(
                    Placeholder.component("issuer", sender.displayName())
                )
            ));
          }
        });

    final var poke = commandManager.commandBuilder("poke")
        .senderType(PlayerSource.class)
        .permission(Permission.POKE)
        .commandDescription(description("Useful for annoying others."))
        .optional("player", playerParser())
        .handler(c -> {
          final var sender = c.sender().source();
          final Player target = c.<Player>optional("player").orElse((sender));

          final ConfigConfig.Data.PokeForce pokeForce = this.configConfig.data().pokeForce();
          final double maxY = pokeForce.maxY();
          final double minY = pokeForce.minY();
          final double maxXZ = pokeForce.maxXZ();
          final double minXZ = pokeForce.minXZ();

          final Random random = new Random();
          final double randX = minXZ + random.nextDouble() * (maxXZ - minXZ);
          final double randY = minY + random.nextDouble() * (maxY - minY);
          final double randZ = minXZ + random.nextDouble() * (maxXZ - minXZ);
          final Vector randomVector = new Vector(randX, randY, randZ);

          target.setVelocity(randomVector);

          if (c.<Player>optional("player").isPresent()) {
            sender.getServer().sendMessage(this.langConfig.c(
                NodePath.path("act", "poke-other"),
                TagResolver.resolver(
                    Placeholder.component("issuer", sender.displayName()),
                    Placeholder.component("target", target.displayName())
                )
            ));
          } else {
            sender.getServer().sendMessage(this.langConfig.c(
                NodePath.path("act", "poke-self"),
                TagResolver.resolver(
                    Placeholder.component("issuer", sender.displayName())
                )
            ));
          }
        });

    commandManager.command(zap);
    commandManager.command(poke);
  }

}
