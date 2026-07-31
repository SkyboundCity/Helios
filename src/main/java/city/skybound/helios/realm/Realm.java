package city.skybound.helios.realm;

import dev.wyck.keys.ResourceKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

/**
 * The "worlds" that exist in Skybound City.
 * <p>
 * Each realm is tied to a distinct Minecraft world.
 */
public enum Realm {
	OVERWORLD(851215225L), // standard.
	NETHER(851214520L), // hellishly difficult.
	END(85125144L); // carefree. allows elytras and ender pearls.

	private final long seed;

	Realm(
			final long seed
	) {
		this.seed = seed;
	}

	public static @Nullable Realm find(final World world) {
		return switch (world.key().asString()) {
			case "helios:overworld" -> Realm.OVERWORLD;
			case "helios:nether" -> Realm.NETHER;
			case "helios:end" -> Realm.END;
			default -> null;
		};
	}

	public static Realm from(final World world) {
		final var realm = find(world);
		if (realm == null) {
			throw new IllegalStateException("Could not find realm for world " + world.key());
		}
		return realm;
	}

	public static Realm of(final Location location) {
		return from(location.getWorld());
	}

	public static Realm of(final Block block) {
		return from(block.getWorld());
	}

	public static Realm of(final BlockState blockState) {
		return from(blockState.getWorld());
	}

	public static Realm of(final Entity entity) {
		return from(entity.getWorld());
	}

	@Override
	public String toString() {
		return this.name().toLowerCase(Locale.ROOT);
	}

	public Key key() {
		return Key.key("helios", this.toString());
	}

	public ResourceKey wyckKey() {
		return ResourceKey.of("helios", this.toString());
	}

	public long seed() {
		return this.seed;
	}

	public World.Environment environment() {
		return switch (this) {
			case OVERWORLD -> World.Environment.NORMAL;
			case NETHER -> World.Environment.NETHER;
			case END -> World.Environment.THE_END;
		};
	}
}
