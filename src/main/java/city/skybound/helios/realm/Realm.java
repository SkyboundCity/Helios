package city.skybound.helios.realm;

import dev.wyck.keys.ResourceKey;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.Locale;

/**
 * The "worlds" that exist in Skybound City.
 * <p>
 * Each realm is tied to a distinct Minecraft world.
 */
public enum Realm {
	OVERWORLD(Milieu.CANON, Habitat.WHITE, 851215225L), // standard.
	NETHER(Milieu.ONEROUS, Habitat.RED, 851214520L), // hellishly difficult.
	END(Milieu.DOCILE, Habitat.BLACK, 85125144L); // carefree. allows elytras and ender pearls.

	private final Milieu milieu;
	private final Habitat habitat;
	private final long seed;

	Realm(
			final Milieu milieu,
			final Habitat habitat,
			final long seed
	) {
		this.milieu = milieu;
		this.habitat = habitat;
		this.seed = seed;
	}

	public static Realm from(final World world) {
		return switch (world.key().asString()) {
			case "helios:overworld" -> Realm.OVERWORLD;
			case "helios:nether" -> Realm.NETHER;
			case "helios:end" -> Realm.END;
			default -> throw new IllegalStateException("Could not find realm for world " + world.key().asString());
		};
	}

	public static Realm of(final Location location) {
		return from(location.getWorld());
	}

	public static Realm of(final Entity entity) {
		return from(entity.getWorld());
	}

	@Override
	public String toString() {
		return this.name().toLowerCase(Locale.ROOT);
	}

	public NamespacedKey key() {
		return new NamespacedKey("helios", this.toString());
	}

	public ResourceKey wyckKey() {
		return ResourceKey.of("helios", this.toString());
	}

	public Milieu milieu() {
		return this.milieu;
	}

	public Habitat habitat() {
		return this.habitat;
	}

	public long seed() {
		return this.seed;
	}
}
