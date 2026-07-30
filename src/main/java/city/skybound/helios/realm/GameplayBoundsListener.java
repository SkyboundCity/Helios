package city.skybound.helios.realm;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.List;

import static city.skybound.helios.loop.RealmPositions.inGameplayArea;

/**
 * Purposefully left out:
 * - BlockFertilizeEvent
 * - BlockFormEvent
 * - EntityChangeBlockEvent
 * - BlockDispenseEvent
 * - PortalCreateEvent
 */
public final class GameplayBoundsListener implements Listener {

	private static boolean isForbidden(final Location loc) {
		final var realm = Realm.find(loc.getWorld());
		if (realm != null) {
			return !inGameplayArea(realm, loc.getBlockY());
		} else {
			return false;
		}
	}

	private static boolean isForbidden(final Block block) {
		final var realm = Realm.find(block.getWorld());
		if (realm != null) {
			return !inGameplayArea(realm, block.getY());
		} else {
			return false;
		}
	}

	private static boolean isForbidden(final BlockState blockState) {
		final var realm = Realm.find(blockState.getWorld());
		if (realm != null) {
			return !inGameplayArea(realm, blockState.getY());
		} else {
			return false;
		}
	}

	private static void cancelIfForbidden(final Cancellable cancellable, final Location loc) {
		if (isForbidden(loc)) {
			cancellable.setCancelled(true);
		}
	}

	private static void cancelIfForbidden(final Cancellable cancellable, final Block block) {
		if (isForbidden(block)) {
			cancellable.setCancelled(true);
		}
	}

	private static void cancelIfForbidden(final Cancellable cancellable, final List<Block> blocks) {
		if (blocks.stream().anyMatch(GameplayBoundsListener::isForbidden)) {
			cancellable.setCancelled(true);
		}
	}

	private static void cancelIfForbiddenStates(final Cancellable cancellable, final List<BlockState> blockStates) {
		if (blockStates.stream().anyMatch(GameplayBoundsListener::isForbidden)) {
			cancellable.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(final BlockPlaceEvent event) {
		cancelIfForbidden(event, event.getBlockPlaced());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockMultiPlace(final BlockMultiPlaceEvent event) {
		cancelIfForbiddenStates(event, event.getReplacedBlockStates());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event) {
		cancelIfForbidden(event, event.getBlock());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockFromTo(final BlockFromToEvent event) {
		cancelIfForbidden(event, event.getToBlock());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPistonExtend(final BlockPistonExtendEvent event) {
		if (event.getBlocks().stream().anyMatch(block -> isForbidden(block.getRelative(event.getDirection())))
				|| isForbidden(event.getBlock().getRelative(event.getDirection()))) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPistonRetract(final BlockPistonRetractEvent event) {
		if (event.getBlocks().stream().anyMatch(block -> isForbidden(block.getRelative(event.getDirection().getOppositeFace())))) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockSpread(final BlockSpreadEvent event) {
		cancelIfForbidden(event, event.getNewState().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockGrow(final BlockGrowEvent event) {
		cancelIfForbidden(event, event.getNewState().getLocation());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onStructureGrow(final StructureGrowEvent event) {
		cancelIfForbiddenStates(event, event.getBlocks());
	}

}
