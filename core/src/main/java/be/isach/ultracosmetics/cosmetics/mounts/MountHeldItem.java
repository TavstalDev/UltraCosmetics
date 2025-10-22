package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.type.MountType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.UnmovableItemProvider;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public abstract class MountHeldItem extends Mount implements UnmovableItemProvider {
    private final ItemStack heldItem;
    private final int slot = SettingsManager.getConfig().getInt("Gadget-Slot");
    private final boolean removeWithDrop = SettingsManager.getConfig().getBoolean("Remove-Gadget-With-Drop");

    public MountHeldItem(UltraPlayer ultraPlayer, MountType type, UltraCosmetics ultraCosmetics) {
        super(ultraPlayer, type, ultraCosmetics);

        heldItem = new ItemStack(getHeldItemMaterial());
        ItemMeta meta = heldItem.getItemMeta();
        String loreString = MessageManager.getLegacyMessage(getOptionPath("Held-Item-Lore"));
        meta.setLore(Arrays.asList(loreString.split("\n")));
        heldItem.setItemMeta(meta);
        ItemFactory.applyCosmeticMarker(heldItem);
    }

    @Override
    public void onEquip() {
        super.onEquip();
        getUltraCosmetics().getUnmovableItemListener().addProvider(this);
    }

    @Override
    public void clear(boolean sendMessage) {
        super.clear(sendMessage);
        getPlayer().getInventory().setItem(slot, null);
        getUltraCosmetics().getUnmovableItemListener().removeProvider(this);
    }

    @Override
    public boolean tryEquip() {
        getOwner().removeCosmetic(Category.GADGETS, true);
        getOwner().removeCosmetic(Category.MOUNTS, true);
        ItemStack current = getPlayer().getInventory().getItem(slot);
        if (current != null && current.getType() != Material.AIR) {
            MessageManager.send(getPlayer(), "Must-Remove.Mounts",
                    Placeholder.unparsed("slot", String.valueOf(slot + 1))
            );
            return false;
        }
        getPlayer().getInventory().setItem(slot, heldItem);
        getPlayer().getInventory().setHeldItemSlot(slot);
        return true;
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void handleDrop(PlayerDropItemEvent event) {
        if (removeWithDrop) {
            clear(true);
            event.getItemDrop().remove();
        } else {
            event.setCancelled(true);
        }
    }

    @Override
    public boolean itemMatches(ItemStack stack) {
        return ItemFactory.isSimilar(stack, heldItem);
    }

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public void moveItem(int slot, Player player) {
        clear(true);
    }

    @Override
    public void handleInteract(PlayerInteractEvent event) {
        event.setCancelled(true);
    }

    public ItemStack getHeldItem() {
        return heldItem.clone();
    }

    public abstract Material getHeldItemMaterial();
}
