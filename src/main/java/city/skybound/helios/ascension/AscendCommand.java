package city.skybound.helios.ascension;

import city.skybound.helios.DurationFormat;
import city.skybound.helios.LuckPermsService;
import city.skybound.helios.config.LangConfig;
import com.google.inject.Inject;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.luckperms.api.model.group.Group;
import org.bukkit.entity.Player;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;
import org.spongepowered.configurate.NodePath;

import java.util.concurrent.TimeUnit;

import static org.incendo.cloud.description.Description.description;

public final class AscendCommand {

  private final LangConfig langConfig;
  private final LuckPermsService luckPermsService;

  @Inject
  public AscendCommand(
      final LangConfig langConfig,
      final LuckPermsService luckPermsService
  ) {
    this.langConfig = langConfig;
    this.luckPermsService = luckPermsService;
  }

  public void register(final PaperCommandManager<Source> commandManager) {
    final var main = commandManager.commandBuilder("ascend")
        .commandDescription(description("Arise to a higher level."))
        .senderType(PlayerSource.class)
        .handler(c -> {
          final var sender = c.sender().source();

          final Group nextGroup = this.luckPermsService.getNextGroupInTrack(sender, "player");
          if (nextGroup == null) {
            sender.sendMessage(this.langConfig.c(NodePath.path("ascend", "max")));
            return;
          }
          final Rank nextRank = Rank.from(nextGroup.getName());

          if (this.isEligibleForRank(sender, nextRank)) {
            this.luckPermsService.promoteInTrack(sender, "player");
            sender.sendMessage(this.langConfig.c(
                NodePath.path("ascend", "ascended"),
                Placeholder.unparsed("group", nextGroup.getName())
            ));
          } else {
            final var timeRequired = nextRank.playtimeRequired();

            if (timeRequired == null) {
              sender.sendMessage(this.langConfig.c(NodePath.path("ascend", "unattainable")));
              return;
            }

            final String fancyTime = DurationFormat.fancifyTime(timeRequired, TimeUnit.HOURS);
            sender.sendMessage(this.langConfig.c(
                NodePath.path("ascend", "ineligible"),
                TagResolver.resolver(
                    Placeholder.unparsed("group", nextGroup.getName()),
                    Placeholder.unparsed("time", fancyTime)
                )
            ));
          }
        });

    commandManager.command(main);
  }

  private boolean isEligibleForRank(final Player player, final Rank rank) {
    final var timePlayed = Playtime.getTimePlayed(player);
    final var timeRequired = rank.playtimeRequired();
    if (timeRequired == null) {
      return false; // cannot be attained through time.
    }
    return timePlayed.compareTo(timeRequired) > 0;
  }

}
