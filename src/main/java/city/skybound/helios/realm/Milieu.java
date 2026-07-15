package city.skybound.helios.realm;


import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

/**
 * The set of rules that govern a realm.
 * <p>
 * World behavior is based on this setting (rather than being
 * realm-specific or habitat-specific) to allow for sharing of
 * rules between distinct realms.
 */
public enum Milieu {
	CANON,
	ONEROUS,
	DOCILE;

	public static Milieu of(final World world) {
		return Realm.from(world).milieu();
	}

	public static Milieu of(final Location location) {
		return of(location.getWorld());
	}

	public static Milieu of(final Entity entity) {
		return of(entity.getWorld());
	}
}
