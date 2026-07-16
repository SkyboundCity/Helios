package city.skybound.helios;

import city.skybound.helios.ascension.AscendCommand;
import city.skybound.helios.ascension.PlaytimeCommand;
import city.skybound.helios.config.BooksConfig;
import city.skybound.helios.config.ConfigConfig;
import city.skybound.helios.config.EmotesConfig;
import city.skybound.helios.config.LangConfig;
import city.skybound.helios.config.PianoNotesConfig;
import city.skybound.helios.fun.ActCommands;
import city.skybound.helios.fun.ElevatorMusicJockey;
import city.skybound.helios.fun.FishingListener;
import city.skybound.helios.fun.FlingerListener;
import city.skybound.helios.fun.FunCommands;
import city.skybound.helios.fun.HatCommand;
import city.skybound.helios.fun.PackCommand;
import city.skybound.helios.fun.RainMusicListener;
import city.skybound.helios.inject.PluginModule;
import city.skybound.helios.inject.SingletonModule;
import city.skybound.helios.loop.PlayerVoidLoopTask;
import city.skybound.helios.loop.VoidDamageListener;
import city.skybound.helios.loop.WarpTask;
import city.skybound.helios.piano.PianoCommand;
import city.skybound.helios.piano.PianoPlayListener;
import city.skybound.helios.realm.InvalidWorldListener;
import city.skybound.helios.realm.PlayerSpawnListener;
import city.skybound.helios.realm.TransposeCommands;
import city.skybound.helios.realm.VoidGenerator;
import city.skybound.helios.realm.WorldProtectionListener;
import city.skybound.helios.realm.WorldService;
import city.skybound.helios.realm.WorldSpawnProtectionListener;
import city.skybound.helios.server.BroadcastCommand;
import city.skybound.helios.server.ChatListener;
import city.skybound.helios.server.DiscordCommand;
import city.skybound.helios.server.GameModeCommands;
import city.skybound.helios.server.HeliosCommand;
import city.skybound.helios.server.JoinQuitListener;
import city.skybound.helios.server.MarkdownCommand;
import city.skybound.helios.server.RulesCommand;
import city.skybound.helios.server.ServerPingListener;
import city.skybound.helios.server.VoteCommand;
import city.skybound.helios.soul.Charon;
import city.skybound.helios.soul.Otzar;
import city.skybound.helios.tag.TagCommand;
import city.skybound.helios.tag.TagListener;
import city.skybound.helios.transportation.FlightListener;
import city.skybound.helios.transportation.FlyCommand;
import city.skybound.helios.transportation.PortalListener;
import city.skybound.helios.transportation.TransportationListener;
import city.skybound.helios.transportation.TransportationTask;
import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.tehbrian.agna.configurate.ConfigLoader;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.Source;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;

import java.util.List;

import static dev.tehbrian.agna.paper.PluginUtils.disableSelf;
import static dev.tehbrian.agna.paper.PluginUtils.registerListeners;
import static org.incendo.cloud.execution.ExecutionCoordinator.simpleCoordinator;
import static org.incendo.cloud.paper.util.sender.PaperSimpleSenderMapper.simpleSenderMapper;

/**
 * The main class for the Helios plugin.
 */
public final class Helios extends JavaPlugin {

	private @MonotonicNonNull PaperCommandManager<Source> commandManager;
	private @MonotonicNonNull Injector injector;

	@Override
	public void onEnable() {
		try {
			this.injector = Guice.createInjector(
					new PluginModule(this),
					new SingletonModule()
			);
		} catch (final Exception e) {
			this.getSLF4JLogger().error("Something went wrong while creating the injector. Disabling plugin");
			disableSelf(this);
			this.getSLF4JLogger().error("Printing stack trace. Please send this to the developers", e);
			return;
		}

		if (!this.injector.getInstance(LuckPermsService.class).load()) {
			this.getSLF4JLogger().error("LuckPerms dependency not found. Disabling plugin");
			disableSelf(this);
			return;
		}

		if (!this.loadConfiguration()) {
			disableSelf(this);
			return;
		}

		if (!this.initCommands()) {
			disableSelf(this);
			return;
		}

		this.initListeners();
		this.initTasks();

		// world creation must occur as a delayed init task.
		this.getServer().getScheduler().runTask(this, () -> this.injector.getInstance(WorldService.class).init());
	}

	@Override
	public void onDisable() {
		try {
			this.injector.getInstance(Charon.class).save();
		} catch (final ConfigurateException e) {
			this.getSLF4JLogger().error(
					"An error occurred while saving config file {}. Please ensure that the file is valid.",
					this.injector.getInstance(Otzar.class).wrapper().path()
			);
			this.getSLF4JLogger().error("Printing stack trace:", e);
		}

		this.getServer().getScheduler().cancelTasks(this);
	}

	/**
	 * Loads the plugin's configuration.
	 * <p>
	 * If there is an error while loading a config file, the exception is logged
	 * and the file is skipped.
	 *
	 * @return whether all config files were successfully loaded
	 */
	public boolean loadConfiguration() {
		return new ConfigLoader(this).load(List.of(
				ConfigLoader.Loadable.of("otzar.hocon", this.injector.getInstance(Otzar.class)),
				ConfigLoader.Loadable.of("books.hocon", this.injector.getInstance(BooksConfig.class)),
				ConfigLoader.Loadable.of("config.hocon", this.injector.getInstance(ConfigConfig.class)),
				ConfigLoader.Loadable.of("emotes.hocon", this.injector.getInstance(EmotesConfig.class)),
				ConfigLoader.Loadable.of("lang.hocon", this.injector.getInstance(LangConfig.class)),
				ConfigLoader.Loadable.of("piano-notes.hocon", this.injector.getInstance(PianoNotesConfig.class))
		));
	}

	/**
	 * @return whether it was successful
	 */
	private boolean initCommands() {
		if (this.commandManager != null) {
			throw new IllegalStateException("The CommandManager is already instantiated");
		}

		this.commandManager = PaperCommandManager
				.builder(simpleSenderMapper())
				.executionCoordinator(simpleCoordinator())
				.buildOnEnable(this);

		this.injector.getInstance(ActCommands.class).register(this.commandManager);
		this.injector.getInstance(AscendCommand.class).register(this.commandManager);
		this.injector.getInstance(BroadcastCommand.class).register(this.commandManager);
		this.injector.getInstance(DiscordCommand.class).register(this.commandManager);
		this.injector.getInstance(HeliosCommand.class).register(this.commandManager);
		this.injector.getInstance(FlyCommand.class).register(this.commandManager);
		this.injector.getInstance(FunCommands.class).register(this.commandManager);
		this.injector.getInstance(GameModeCommands.class).register(this.commandManager);
		this.injector.getInstance(HatCommand.class).register(this.commandManager);
		this.injector.getInstance(MarkdownCommand.class).register(this.commandManager);
		this.injector.getInstance(PackCommand.class).register(this.commandManager);
		this.injector.getInstance(PianoCommand.class).register(this.commandManager);
		this.injector.getInstance(PlaytimeCommand.class).register(this.commandManager);
		this.injector.getInstance(RulesCommand.class).register(this.commandManager);
		this.injector.getInstance(TagCommand.class).register(this.commandManager);
		this.injector.getInstance(TransposeCommands.class).register(this.commandManager);
		this.injector.getInstance(VoteCommand.class).register(this.commandManager);

		return true;
	}

	private void initListeners() {
		registerListeners(
				this,
				this.injector.getInstance(ChatListener.class),
				this.injector.getInstance(FishingListener.class),
				this.injector.getInstance(FlightListener.class),
				this.injector.getInstance(FlingerListener.class),
				this.injector.getInstance(JoinQuitListener.class),
				this.injector.getInstance(InvalidWorldListener.class),
				this.injector.getInstance(VoidDamageListener.class),
				this.injector.getInstance(PianoPlayListener.class),
				this.injector.getInstance(RainMusicListener.class),
				this.injector.getInstance(PlayerSpawnListener.class),
				this.injector.getInstance(ServerPingListener.class),
				this.injector.getInstance(WorldSpawnProtectionListener.class),
				this.injector.getInstance(TagListener.class),
				this.injector.getInstance(PortalListener.class),
				this.injector.getInstance(TransportationListener.class),
				this.injector.getInstance(WorldProtectionListener.class)
		);
	}

	private void initTasks() {
		this.injector.getInstance(ElevatorMusicJockey.class).start();
		this.injector.getInstance(PlayerVoidLoopTask.class).start();
		this.injector.getInstance(TransportationTask.class).start();
		this.injector.getInstance(WarpTask.class).start();
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(
			final @NotNull String worldName,
			final @Nullable String id
	) {
		return new VoidGenerator();
	}

}
