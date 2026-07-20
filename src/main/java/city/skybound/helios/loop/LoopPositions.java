package city.skybound.helios.loop;

import org.bukkit.World;
import org.bukkit.event.Listener;

/**
 * Provides the positions at which the void loop will engage for each habitat.
 * <p>
 * These positions are a function of the habitat due to the habitats' different
 * fog distances/visual block cutoffs.
 */
public final class LoopPositions implements Listener {

	private LoopPositions() {
	}

	// trouble understanding? no worries, I got you. here's a drawing.
	// https://i.imgur.com/OubxQoa.jpeg

	public static int lowEngage(final World world) {
		return highTo(world) - 10;
	}

	public static int lowTo(final World world) {
		return world.getMaxHeight() + 60;
	}

	public static int highEngage(final World world) {
		return lowTo(world) + 10;
	}

	public static int highTo(final World world) {
		return world.getMinHeight() - 60;
	}

}
