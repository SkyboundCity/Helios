package city.skybound.helios.realm;

import com.google.inject.Inject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Teleports players who join the bootstrap world to the overworld.
 */
public final class InvalidWorldListener implements Listener {

	private final Transposer transposer;

	@Inject
	public InvalidWorldListener(
			final Transposer transposer
	) {
		this.transposer = transposer;
	}

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		try {
			Realm.of(player);
		} catch (RuntimeException _) {
			this.transposer.transposeNoPrevious(player, Realm.OVERWORLD);
		}
	}

}
