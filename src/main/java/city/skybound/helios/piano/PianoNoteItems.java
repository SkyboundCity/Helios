package city.skybound.helios.piano;

import com.google.inject.Inject;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.Nullable;

import static dev.tehbrian.agna.paper.FluentItem.createItem;
import static dev.tehbrian.agna.paper.FluentItem.editItem;
import static java.util.Objects.requireNonNull;

public final class PianoNoteItems {

	private final NamespacedKey pitchKey;

	@Inject
	public PianoNoteItems(
			final JavaPlugin javaPlugin
	) {
		this.pitchKey = new NamespacedKey(javaPlugin, "piano-pitch");
	}

	public @Nullable Float getPitch(final ItemStack item) {
		return editItem(item).pdcGet(this.pitchKey, PersistentDataType.FLOAT);
	}

	public ItemStack createPianoNote(final Material material, final Component name, final float pitch) {
		final var itemType = requireNonNull(material.asItemType());
		return createItem(itemType)
				.itemName(name)
				.pdcSet(this.pitchKey, PersistentDataType.FLOAT, pitch)
				.item();
	}

}
