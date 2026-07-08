package city.thefloating.helios.piano;

import com.google.inject.Inject;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.Nullable;

import static love.broccolai.corn.minecraft.item.ItemBuilder.itemBuilder;

public final class PianoNoteItems {

  private final NamespacedKey pitchKey;

  @Inject
  public PianoNoteItems(
      final JavaPlugin javaPlugin
  ) {
    this.pitchKey = new NamespacedKey(javaPlugin, "piano-pitch");
  }

  public @Nullable Float getPitch(final ItemStack item) {
    return itemBuilder(item).data(this.pitchKey, PersistentDataType.FLOAT);
  }

  public ItemStack createItem(final Material material, final Component name, final float pitch) {
    return itemBuilder(material)
        .name(name)
        .data(this.pitchKey, PersistentDataType.FLOAT, pitch)
        .build();
  }

}
