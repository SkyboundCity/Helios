package city.skybound.helios.fun;

import city.skybound.helios.Permission;
import city.skybound.helios.config.LangConfig;
import com.google.inject.Inject;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.Source;
import org.incendo.cloud.suggestion.SuggestionProvider;
import org.spongepowered.configurate.NodePath;

import static org.incendo.cloud.description.Description.description;
import static org.incendo.cloud.parser.standard.StringParser.greedyStringParser;
import static org.incendo.cloud.suggestion.SuggestionProvider.blockingStrings;

public final class FunCommands {

  private final LangConfig langConfig;

  @Inject
  public FunCommands(
      final LangConfig langConfig
  ) {
    this.langConfig = langConfig;
  }

  public void register(final PaperCommandManager<Source> commandManager) {
    final SuggestionProvider<Source> playerSuggestionsProvider = blockingStrings((c, i) ->
        c.sender().source().getServer().getOnlinePlayers().stream().map(Player::getName).toList());

    final var unreadable = commandManager.commandBuilder("unreadable")
        .commandDescription(description("Untransparent. Is that a word? Opaque?"))
        .permission(Permission.UNREADABLE)
        .handler(c -> c.sender().source().getServer().sendMessage(this.langConfig.c(
            NodePath.path("fun", "unreadable"),
            Placeholder.unparsed("player", c.sender().source().getName())
        )));

    final var shrug = commandManager.commandBuilder("shrug")
        .commandDescription(description("You don't know. They don't know."))
        .permission(Permission.SHRUG)
        .handler(c -> c.sender().source().getServer().sendMessage(this.langConfig.c(
            NodePath.path("fun", "shrug"),
            Placeholder.unparsed("player", c.sender().source().getName())
        )));

    final var spook = commandManager.commandBuilder("spook")
        .commandDescription(description("OoooOOooOoOOoOOoo"))
        .permission(Permission.SPOOK)
        .handler(c -> c.sender().source().getServer().sendMessage(this.langConfig.c(
            NodePath.path("fun", "spook"),
            Placeholder.unparsed("player", c.sender().source().getName())
        )));

    final var hug = commandManager.commandBuilder("hug")
        .commandDescription(description("D'aww that's so cute!"))
        .permission(Permission.HUG)
        .required("text", greedyStringParser(), playerSuggestionsProvider)
        .handler(c -> c.sender().source().getServer().sendMessage(this.langConfig.c(
            NodePath.path("fun", "hug"),
            TagResolver.resolver(
                Placeholder.unparsed("player", c.sender().source().getName()),
                Placeholder.unparsed("text", c.get("text"))
            )
        )));

    final var smooch = commandManager.commandBuilder("smooch")
        .commandDescription(description("Give 'em a smooch."))
        .permission(Permission.SMOOCH)
        .required("text", greedyStringParser(), playerSuggestionsProvider)
        .handler(c -> c.sender().source().getServer().sendMessage(this.langConfig.c(
            NodePath.path("fun", "smooch"),
            TagResolver.resolver(
                Placeholder.unparsed("player", c.sender().source().getName()),
                Placeholder.unparsed("text", c.get("text"))
            )
        )));

    final var blame = commandManager.commandBuilder("blame")
        .commandDescription(description("It's their fault, not yours."))
        .permission(Permission.BLAME)
        .required("text", greedyStringParser(), playerSuggestionsProvider)
        .handler(c -> c.sender().source().getServer().sendMessage(this.langConfig.c(
            NodePath.path("fun", "blame"),
            TagResolver.resolver(
                Placeholder.unparsed("player", c.sender().source().getName()),
                Placeholder.unparsed("text", c.get("text"))
            )
        )));

    final var highfive = commandManager.commandBuilder("highfive")
        .commandDescription(description("Up high! Down low! Too slow!"))
        .permission(Permission.HIGHFIVE)
        .required("text", greedyStringParser(), playerSuggestionsProvider)
        .handler(c -> c.sender().source().getServer().sendMessage(this.langConfig.c(
            NodePath.path("fun", "highfive"),
            TagResolver.resolver(
                Placeholder.unparsed("player", c.sender().source().getName()),
                Placeholder.unparsed("text", c.get("text"))
            )
        )));

    final var sue = commandManager.commandBuilder("sue")
        .permission(Permission.SUE)
        .commandDescription(description("Court fixes everything.. right?"))
        .optional("text", greedyStringParser(), playerSuggestionsProvider)
        .handler(c -> c.<String>optional("text").ifPresentOrElse(
            (text) -> c
                .sender().source().getServer()
                .sendMessage(this.langConfig.c(
                    NodePath.path("fun", "sue-extra"),
                    TagResolver.resolver(
                        Placeholder.unparsed("player", c.sender().source().getName()),
                        Placeholder.unparsed("text", text)
                    )
                )),
            () -> c
                .sender().source().getServer()
                .sendMessage(this.langConfig.c(
                    NodePath.path("fun", "sue"),
                    Placeholder.unparsed("player", c.sender().source().getName())
                ))
        ));

    commandManager.command(unreadable);
    commandManager.command(shrug);
    commandManager.command(spook);
    commandManager.command(hug);
    commandManager.command(smooch);
    commandManager.command(blame);
    commandManager.command(highfive);
    commandManager.command(sue);
  }

}
