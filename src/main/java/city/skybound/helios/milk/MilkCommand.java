package city.skybound.helios.milk;

import city.skybound.helios.Permission;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.paper.util.sender.PlayerSource;
import org.incendo.cloud.paper.util.sender.Source;

import static org.incendo.cloud.description.Description.description;

public final class MilkCommand {

	public void register(final PaperCommandManager<Source> commandManager) {
		final var main = commandManager.commandBuilder("milk")
				.commandDescription(description("Milk."))
				.permission(Permission.MILK)
				.senderType(PlayerSource.class)
				.handler(c -> {
					final var sender = c.sender().source();
					sender.getInventory().addItem(Milk.regular());
				});

		commandManager.command(main);
	}

}
