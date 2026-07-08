package city.skybound.helios.fun;

import city.skybound.helios.Permission;
import city.skybound.helios.config.LangConfig;
import com.google.inject.Inject;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;
import org.spongepowered.configurate.NodePath;

import static org.incendo.cloud.description.Description.description;

public final class HatCommand {

	private final LangConfig langConfig;

	@Inject
	public HatCommand(
			final LangConfig langConfig
	) {
		this.langConfig = langConfig;
	}

	public void register(final PaperCommandManager<Source> commandManager) {
		final var main = commandManager.commandBuilder("hat")
				.commandDescription(description("Put fancy things on your head!"))
				.permission(Permission.HAT)
				.senderType(PlayerSource.class)
				.handler(c -> {
					final var sender = c.sender().source();
					final PlayerInventory inventory = sender.getInventory();
					final ItemStack heldItem = inventory.getItemInMainHand();

					if (heldItem.getType() == Material.AIR) {
						if (inventory.getHelmet() == null) {
							sender.sendMessage(this.langConfig.c(NodePath.path("hat", "none")));
						} else {
							inventory.setHelmet(new ItemStack(Material.AIR));
							sender.sendMessage(this.langConfig.c(NodePath.path("hat", "removed")));
						}
					} else {
						inventory.setHelmet(heldItem);
						sender.sendMessage(this.langConfig.c(NodePath.path("hat", "set")));
					}
				});

		commandManager.command(main);
	}

}
