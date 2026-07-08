package city.skybound.helios.loop;

import io.papermc.paper.entity.TeleportFlag;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public final class Teleport {

	private Teleport() {
	}

	public static void relative(
			final Entity entity, final Location loc
	) {
		entity.teleport(
				loc,
				TeleportFlag.Relative.VELOCITY_ROTATION,
				TeleportFlag.Relative.VELOCITY_X,
				TeleportFlag.Relative.VELOCITY_Y,
				TeleportFlag.Relative.VELOCITY_Z
		);
	}

}
