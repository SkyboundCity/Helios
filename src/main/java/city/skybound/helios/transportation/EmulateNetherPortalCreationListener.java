package city.skybound.helios.transportation;

import city.skybound.helios.HeliosPlugin;
import city.skybound.helios.realm.Realm;
import com.google.inject.Inject;
import org.bukkit.Axis;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Orientable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Emulates nether portal creation in custom Helios dimensions.
 *
 * <p>Vanilla only attempts to create nether portals in its built-in dimensions,
 * so custom dimensions need to recreate the logic after fire is placed.</p>
 */
public final class EmulateNetherPortalCreationListener implements Listener {

	private static final int MIN_INTERIOR_WIDTH = 2;
	private static final int MAX_INTERIOR_WIDTH = 21;
	private static final int MIN_INTERIOR_HEIGHT = 3;
	private static final int MAX_INTERIOR_HEIGHT = 21;

	private final Set<Block> pendingAttempts = new HashSet<>();

	private final HeliosPlugin plugin;

	@Inject
	public EmulateNetherPortalCreationListener(final HeliosPlugin plugin) {
		this.plugin = plugin;
	}

	private static boolean hasObsidianTop(
			final Block rowStart,
			final BlockFace positive,
			final int width
	) {
		for (int offset = 0; offset < width; offset++) {
			if (!isObsidian(rowStart.getRelative(positive, offset).getRelative(BlockFace.UP))) {
				return false;
			}
		}
		return true;
	}

	private static boolean isEmptyPortalBlock(final Block block) {
		return block.isEmpty() || isFire(block) || block.getType() == Material.NETHER_PORTAL;
	}

	private static boolean isFire(final Block block) {
		return block.getType() == Material.FIRE || block.getType() == Material.SOUL_FIRE;
	}

	private static boolean isObsidian(final Block block) {
		return block.getType() == Material.OBSIDIAN;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(final BlockPlaceEvent event) {
		this.scheduleAttempt(event.getBlockPlaced());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockIgnite(final BlockIgniteEvent event) {
		this.scheduleAttempt(event.getBlock());
	}

	private void scheduleAttempt(final Block block) {
		if (Realm.find(block.getWorld()) == null || !this.pendingAttempts.add(block)) {
			return;
		}

		this.plugin.getServer().getScheduler().runTask(
				this.plugin,
				() -> {
					this.pendingAttempts.remove(block);
					this.attemptPortalCreation(block);
				}
		);
	}

	private void attemptPortalCreation(final Block ignitionBlock) {
		if (!isFire(ignitionBlock)) {
			return;
		}

		final Optional<PortalShape> shape = this.findPortalShape(ignitionBlock, Axis.X)
				.or(() -> this.findPortalShape(ignitionBlock, Axis.Z));
		shape.ifPresent(this::createPortal);
	}

	private Optional<PortalShape> findPortalShape(final Block ignitionBlock, final Axis axis) {
		final BlockFace negative = axis == Axis.X ? BlockFace.WEST : BlockFace.NORTH;
		final BlockFace positive = negative.getOppositeFace();

		Block bottom = ignitionBlock;
		for (int distance = 0;
		     distance < MAX_INTERIOR_HEIGHT - 1 && isEmptyPortalBlock(bottom.getRelative(BlockFace.DOWN));
		     distance++) {
			bottom = bottom.getRelative(BlockFace.DOWN);
		}

		Block leftInterior = bottom;
		for (int distance = 0;
		     distance < MAX_INTERIOR_WIDTH && isEmptyPortalBlock(leftInterior.getRelative(negative));
		     distance++) {
			leftInterior = leftInterior.getRelative(negative);
		}

		if (!isObsidian(leftInterior.getRelative(negative))) {
			return Optional.empty();
		}

		int width = 0;
		Block cursor = leftInterior;
		while (width < MAX_INTERIOR_WIDTH && isEmptyPortalBlock(cursor)) {
			width++;
			cursor = cursor.getRelative(positive);
		}

		if (width < MIN_INTERIOR_WIDTH || !isObsidian(cursor)) {
			return Optional.empty();
		}

		for (int offset = 0; offset < width; offset++) {
			if (!isObsidian(leftInterior.getRelative(positive, offset).getRelative(BlockFace.DOWN))) {
				return Optional.empty();
			}
		}

		final List<Block> interior = new ArrayList<>();
		for (int height = 0; height < MAX_INTERIOR_HEIGHT; height++) {
			final Block rowStart = leftInterior.getRelative(BlockFace.UP, height);
			if (!isObsidian(rowStart.getRelative(negative))
					|| !isObsidian(rowStart.getRelative(positive, width))) {
				return Optional.empty();
			}

			for (int offset = 0; offset < width; offset++) {
				final Block interiorBlock = rowStart.getRelative(positive, offset);
				if (!isEmptyPortalBlock(interiorBlock)) {
					return Optional.empty();
				}
				interior.add(interiorBlock);
			}

			final int portalHeight = height + 1;
			if (portalHeight >= MIN_INTERIOR_HEIGHT && hasObsidianTop(rowStart, positive, width)) {
				return Optional.of(new PortalShape(axis, interior));
			}
		}

		return Optional.empty();
	}

	private void createPortal(final PortalShape shape) {
		final Orientable portalData = (Orientable) Material.NETHER_PORTAL.createBlockData();
		portalData.setAxis(shape.axis());

		for (final Block block : shape.interior()) {
			block.setBlockData(portalData, false);
		}
	}

	private record PortalShape(Axis axis, List<Block> interior) {
	}
}
