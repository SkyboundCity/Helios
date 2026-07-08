package city.skybound.helios.piano;

import city.skybound.helios.Helios;
import city.skybound.helios.Permission;
import city.skybound.helios.config.BookDeserializer;
import city.skybound.helios.config.BooksConfig;
import city.skybound.helios.config.LangConfig;
import city.skybound.helios.soul.Charon;
import com.google.inject.Inject;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.NodePath;

import java.util.List;

import static org.incendo.cloud.component.DefaultValue.constant;
import static org.incendo.cloud.description.Description.description;
import static org.incendo.cloud.parser.standard.EnumParser.enumParser;
import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;

public final class PianoCommand {

	private final Helios plugin;
	private final Charon charon;
	private final PianoMenuProvider pianoMenuProvider;
	private final BooksConfig booksConfig;
	private final LangConfig langConfig;

	@Inject
	public PianoCommand(
			final Helios plugin,
			final Charon charon,
			final PianoMenuProvider pianoMenuProvider,
			final BooksConfig booksConfig,
			final LangConfig langConfig
	) {
		this.plugin = plugin;
		this.charon = charon;
		this.pianoMenuProvider = pianoMenuProvider;
		this.booksConfig = booksConfig;
		this.langConfig = langConfig;
	}

	public void register(final PaperCommandManager<Source> commandManager) {
		final var main = commandManager.commandBuilder("piano")
				.commandDescription(description("A fancy playable piano."))
				.permission(Permission.PIANO)
				.handler(c -> c.sender().source().sendMessage(
						BookDeserializer.deserializePage(this.getBookNode(), 1)
				));

		final var help = main.literal("help")
				.optional("page", integerParser(1, BookDeserializer.pageCount(this.getBookNode())), constant(1))
				.handler(c -> c.sender().source().sendMessage(
						BookDeserializer.deserializePage(this.getBookNode(), c.<Integer>get("page"))
				));

		final var toggle = main.literal("toggle", description("Toggle your piano on and off."))
				.senderType(PlayerSource.class)
				.handler(c -> {
					final var sender = c.sender().source();
					if (this.charon.grab(sender).piano().toggleEnabled()) {
						sender.sendMessage(this.langConfig.c(NodePath.path("piano", "enabled")));
					} else {
						sender.sendMessage(this.langConfig.c(NodePath.path("piano", "disabled")));
					}
				});

		final var collection = main.literal("collection", description("Get a collection of notes!"))
				.senderType(PlayerSource.class)
				.required("note_collection", enumParser(PianoMenuProvider.NoteCollection.class))
				.optional("max", integerParser(1), constant(9))
				.handler(c -> {
					final PianoMenuProvider.NoteCollection noteCollection = c.get("note_collection");
					final int max = c.get("max");
					final var sender = c.sender().source();

					var delay = 0;
					final List<ItemStack> noteItems = this.pianoMenuProvider.getCollection(noteCollection);
					for (final ItemStack item : noteItems.subList(0, Math.min(max, noteItems.size()))) {
						sender.getServer().getScheduler().runTaskLater(
								this.plugin,
								() -> {
									final var unaddedItem = sender.getInventory().addItem(item).get(0);
									if (unaddedItem != null) {
										sender.getWorld().dropItem(sender.getLocation(), unaddedItem);
										sender.playSound(sender.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.7F, 0.7F);
									} else {
										sender.playSound(sender.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.7F, 1);
									}
								},
								delay
						);
						delay = delay + 1;
					}
				});

		final var instrument = main.literal("instrument", description("Pick your instrument!"))
				.senderType(PlayerSource.class)
				.required("instrument", enumParser(Instrument.class))
				.handler(c -> {
					final Instrument inst = c.get("instrument");
					final var sender = c.sender().source();

					this.charon.grab(sender).piano().instrument(inst);
					sender.sendMessage(this.langConfig.c(
							NodePath.path("piano", "instrument-change"),
							Placeholder.unparsed("instrument", inst.toString())
					));
				});

		final var menu = main.literal("menu", description("Pick your notes!"))
				.senderType(PlayerSource.class)
				.handler(c -> (c.sender().source()).openInventory(this.pianoMenuProvider.generate()));

		commandManager.command(main);
		commandManager.command(help);
		commandManager.command(toggle);
		commandManager.command(collection);
		commandManager.command(instrument);
		commandManager.command(menu);
	}

	public CommentedConfigurationNode getBookNode() {
		return this.booksConfig.rootNode().node("piano-manual");
	}

}
