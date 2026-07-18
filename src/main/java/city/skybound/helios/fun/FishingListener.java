package city.skybound.helios.fun;

import city.skybound.helios.realm.Realm;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemType;

import static dev.tehbrian.agna.paper.FluentItem.createItem;

public final class FishingListener implements Listener {

	@EventHandler
	public void onVoidFish(final PlayerFishEvent event) {
		if (event.getState() != PlayerFishEvent.State.REEL_IN) {
			return;
		}

		final FishHook hook = event.getHook();
		if (!(hook.getY() < hook.getWorld().getMinHeight())) { // below world.
			return;
		}

		final Player player = event.getPlayer();
		switch (Realm.of(player)) {
			case OVERWORLD -> player.getInventory().addItem(createItem(ItemType.COD)
					.customName(Component.text("Floaty Fish").color(NamedTextColor.AQUA))
					.loreList(
							Component.text("\"It's just.. floating there.\"").color(NamedTextColor.WHITE),
							Component.empty(),
							Component.text("This fish seems important.").color(NamedTextColor.GRAY)
					)
					.item());
			case NETHER -> player.getInventory().addItem(createItem(ItemType.TROPICAL_FISH)
					.customName(Component.text("Fiery Fish").color(NamedTextColor.RED))
					.loreList(
							Component.text("Likes to set things ablaze.").color(NamedTextColor.WHITE),
							Component.empty(),
							Component.text("This fish seems important.").color(NamedTextColor.GRAY)
					)
					.item());
			case END -> player.getInventory().addItem(createItem(ItemType.SALMON)
					.customName(Component.text("Abyss Fish").color(NamedTextColor.DARK_PURPLE))
					.loreList(
							Component.text("Has a mystical, purple-ish aura.").color(NamedTextColor.WHITE),
							Component.empty(),
							Component.text("This fish seems important.").color(NamedTextColor.GRAY)
					)
					.item());
			default -> player.getInventory().addItem(createItem(ItemType.PUFFERFISH)
					.customName(Component.text("Odd Fish").color(NamedTextColor.GOLD))
					.loreList(
							Component.text("It doesn't look very appetizing.").color(NamedTextColor.WHITE),
							Component.empty(),
							Component.text("You can't do anything with this fish.").color(NamedTextColor.GRAY)
					).item());
		}
	}

}
