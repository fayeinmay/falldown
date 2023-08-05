package de.jandev.falldown.utility;

import de.jandev.falldown.Falldown;
import de.jandev.falldown.model.map.MapEntity;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class VotingHelper implements Listener {

    private final Falldown plugin;
    private final Inventory inv;

    public VotingHelper(Falldown plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        inv = Bukkit.createInventory(null, 9, plugin.getConfigString("setting.votebooktitle"));
        initializeItems();
    }

    public void openInventory(final HumanEntity ent) {
        ent.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getInventory() != inv) return;
        e.setCancelled(true);
        final ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        final Player p = (Player) e.getWhoClicked();

        if (e.getClick() == ClickType.LEFT) {
            // For now this is fine, we can rewrite this to just put votes in the list if we increase the max map size
            if ((e.getRawSlot() + 1) == 1) {
                p.performCommand("fd vote 1");
                p.closeInventory();
            } else if ((e.getRawSlot() + 1) == 3) {
                p.performCommand("fd vote 2");
                p.closeInventory();
            } else if ((e.getRawSlot() + 1) == 5) {
                p.performCommand("fd vote 3");
                p.closeInventory();
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent e) {
        if (e.getInventory() == inv) {
            e.setCancelled(true);
        }
    }

    private void initializeItems() {
        int counter = 0;
        for (MapEntity map : plugin.getMapHelper().getMapSelection()) {
            inv.setItem(counter, createGuiItem(Material.PAPER, map.getName()));
            counter += 2;
        }
    }

    private ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);

        return item;
    }
}
