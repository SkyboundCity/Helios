package city.skybound.helios.realm;

import city.skybound.helios.Permission;
import city.skybound.helios.config.LangConfig;
import city.skybound.helios.transportation.PortalListener;
import com.google.inject.Inject;
import org.bukkit.entity.Player;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;
import org.spongepowered.configurate.NodePath;

import static org.incendo.cloud.description.Description.description;

public final class TransposeCommands {

	private final LangConfig langConfig;
	private final Transposer transposer;
	private final PortalListener portalListener;

	@Inject
	public TransposeCommands(
			final LangConfig langConfig,
			final Transposer transposer,
			final PortalListener portalListener
	) {
		this.langConfig = langConfig;
		this.transposer = transposer;
		this.portalListener = portalListener;
	}

	public void register(final PaperCommandManager<Source> commandManager) {
		final var madlands = commandManager.commandBuilder("madlands")
				.commandDescription(description("Transpose to the madlands."))
				.permission(Permission.REALM_MADLANDS)
				.senderType(PlayerSource.class)
				.handler(c -> this.tryTranspose(c.sender().source(), Realm.MADLANDS));

		final var overworld = commandManager.commandBuilder("overworld")
				.commandDescription(description("Transpose to the overworld."))
				.permission(Permission.REALM_OVERWORLD)
				.senderType(PlayerSource.class)
				.handler(c -> this.tryTranspose(c.sender().source(), Realm.OVERWORLD));

		final var nether = commandManager.commandBuilder("nether")
				.commandDescription(description("Transpose to the nether."))
				.permission(Permission.REALM_NETHER)
				.senderType(PlayerSource.class)
				.handler(c -> this.tryTranspose(c.sender().source(), Realm.NETHER));

		final var end = commandManager.commandBuilder("end")
				.commandDescription(description("Transpose to the end."))
				.permission(Permission.REALM_END)
				.senderType(PlayerSource.class)
				.handler(c -> this.tryTranspose(c.sender().source(), Realm.END));

		final var backrooms = commandManager.commandBuilder("backrooms")
				.commandDescription(description("Transpose to the backrooms."))
				.permission(Permission.REALM_BACKROOMS)
				.senderType(PlayerSource.class)
				.handler(c -> this.tryTranspose(c.sender().source(), Realm.BACKROOMS));

		commandManager.command(overworld);
		commandManager.command(nether);
		commandManager.command(end);
		commandManager.command(madlands);
		commandManager.command(backrooms);
	}

	private void tryTranspose(final Player player, final Realm destination) {
		final Realm current = Realm.of(player);
		if (current == destination) {
			player.sendMessage(this.langConfig.c(NodePath.path("transpose", "already-there")));
			return;
		}
		// prevent players from instantly teleporting back if they were previously in a portal.
		this.portalListener.attemptPortal(player);
		this.transposer.transpose(player, destination);
	}

}
