package city.skybound.helios.loop;

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

	@EventHandler
	public void onVoidDamage(final EntityDamageEvent event) {
		if (event.getCause() != EntityDamageEvent.DamageCause.VOID) {
			return;
		}

		// void damage? no such thing.
		event.setCancelled(true);

		final Entity entity = event.getEntity();
		if (entity instanceof Player) {
			// players will be handled in PlayerVoidLoop, so no need to handle them here.
			return;
		}

		if (entity.getFallDistance() > 10_000) {
			// lore-wise, they burnt up due to friction.
			// practically, they're probably abandoned.
			entity.remove();
			return;
		}

		final Location loc = entity.getLocation();
		final var world = entity.getWorld();
		if (loc.getY() <= LoopPositions.lowEngage(world)) {
			loc.setY(LoopPositions.lowTo(world));
			Teleport.relative(entity, loc);
		}
	}

}
