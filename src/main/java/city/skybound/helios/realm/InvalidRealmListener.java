package city.skybound.helios.realm;

import com.google.inject.Inject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Teleports players who join the bootstrap world to the overworld.
 */
public final class InvalidRealmListener implements Listener {

	private final Transposer transposer;

	@Inject
	public InvalidRealmListener(
			final Transposer transposer
	) {
		this.transposer = transposer;
	}

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		if (Realm.find(player.getWorld()) == null) {
			this.transposer.transpose(player, Realm.OVERWORLD);
		}
	}

}
