package city.skybound.helios.loop;

import city.skybound.helios.realm.Realm;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Offers a cheap, low-engage-only void loop for non-player entities. This is
 * useful because {@link PlayerVoidLoopTask} is player-only.
 * <p>
 * Also prevents void damage for all entities.
 */
public final class VoidDamageListener implements Listener {

	// lore-wise, they burnt up due to friction.
	// practically, they're probably abandoned.
	private static final int CRISP_THRESHOLD = 10_000;

	@EventHandler
	public void onVoidDamage(final EntityDamageEvent event) {
		if (event.getCause() != EntityDamageEvent.DamageCause.VOID) {
			return;
		}

		final Entity entity = event.getEntity();
		final var realm = Realm.find(entity.getWorld());
		if (realm == null) {
			return;
		}

		// void damage? no such thing
		event.setCancelled(true);

		if (entity instanceof Player) {
			// players will be handled in PlayerVoidLoop, so no need to handle them here
			return;
		}

		if (entity.getFallDistance() > CRISP_THRESHOLD) {
			entity.remove();
			return;
		}

		final Location loc = entity.getLocation();
		if (loc.getY() <= RealmPositions.loopMinEngageY(realm)) {
			loc.setY(RealmPositions.loopMinToY(realm));
			Teleport.relative(entity, loc);
		}
	}

}
