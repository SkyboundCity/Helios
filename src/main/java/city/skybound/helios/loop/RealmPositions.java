package city.skybound.helios.loop;

import city.skybound.helios.realm.Realm;

/**
 * Provides the positions at which the void loop will engage for each habitat.
 * <p>
 * These positions are a function of the habitat due to the habitats' different
 * fog distances/visual block cutoffs.
 */
public final class RealmPositions {

	private RealmPositions() {
	}

	// trouble understanding? no worries, I got you. here's a drawing.
	// https://i.imgur.com/OubxQoa.jpeg

	private static int visualClearance(final Realm realm) {
		return switch (realm.habitat()) {
			case WHITE -> 16 * 10;
			case RED, BLACK -> 16 * 3;
		};
	}

	public static int gameplayMinY(final Realm realm) {
		return switch (realm) {
			case OVERWORLD -> -64;
			case NETHER, END -> 0;
		};
	}

	public static int technicalMinY(final Realm realm) {
		return loopMinEngageY(realm) - 16 * 3;
	}

	public static int maxYExclusive(final Realm realm) {
		return switch (realm) {
			case OVERWORLD -> 320;
			case NETHER, END -> 256;
		};
	}

	public static int loopMinEngageY(final Realm realm) {
		return loopMaxToY(realm) - 16;
	}

	public static int loopMaxEngageY(final Realm realm) {
		return loopMinToY(realm) + 16;
	}

	public static int loopMinToY(final Realm realm) {
		return maxYExclusive(realm) + visualClearance(realm);
	}

	public static int loopMaxToY(final Realm realm) {
		return gameplayMinY(realm) - visualClearance(realm);
	}

	public static int technicalHeight(final Realm realm) {
		return maxYExclusive(realm) - technicalMinY(realm);
	}

	public static boolean inGameplayArea(final Realm realm, final int y) {
		return y >= gameplayMinY(realm) && y < maxYExclusive(realm);
	}

}
