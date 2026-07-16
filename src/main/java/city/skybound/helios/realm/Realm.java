package city.skybound.helios.realm;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;

/**
 * The "worlds" that exist in Skybound City.
 * <p>
 * Each realm is tied to a distinct Minecraft world.
 */
public enum Realm {
	OVERWORLD(Milieu.CANON, Habitat.WHITE), // standard.
	NETHER(Milieu.ONEROUS, Habitat.RED), // hellishly difficult.
	END(Milieu.DOCILE, Habitat.BLACK); // carefree. allows elytras and ender pearls.

	private final Milieu milieu;
	private final Habitat habitat;

	Realm(final Milieu milieu, final Habitat habitat) {
		this.milieu = milieu;
		this.habitat = habitat;
	}

	public static Realm from(final World world) {
		return switch (world.key().asString()) {
			case "helios:overworld" -> Realm.OVERWORLD;
			case "helios:nether" -> Realm.NETHER;
			case "helios:end" -> Realm.END;
			default -> throw new RuntimeException("Could not find realm for world `" + world.key().asString() + "`.");
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

	public NamespacedKey key(final JavaPlugin plugin) {
		return new NamespacedKey(plugin, this.toString());
	}

	public Milieu milieu() {
		return this.milieu;
	}

	public Habitat habitat() {
		return this.habitat;
	}
}
