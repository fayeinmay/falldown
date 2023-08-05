package de.jandev.falldown.listener.handler;

import de.jandev.falldown.Falldown;
import de.jandev.falldown.model.Brewing;
import de.jandev.falldown.model.item.ItemCombinationEntity;
import de.jandev.falldown.model.item.ItemEntity;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class InteractBrewingStandHandler {

    private final Falldown plugin;

    public InteractBrewingStandHandler(Falldown plugin) {
        this.plugin = plugin;
    }

    public void handle(Player p, Block clickedBlock) {
        ItemStack handOriginal = p.getInventory().getItemInMainHand();
        ItemStack handCopy = p.getInventory().getItemInMainHand().clone();
        handCopy.setAmount(1); // Set the amount to 1 because future references will keep this amount

        // Populate the current Brewing helper
        List<Brewing> brewings = plugin.getPlayerBrewings().computeIfAbsent(p, k -> new ArrayList<>());
        Brewing brewing = brewings.stream()
                .filter(x -> x.getLocation().equals(clickedBlock.getLocation()))
                .findFirst()
                .orElse(new Brewing(clickedBlock.getLocation()));
        List<ItemStack> brewingItems = brewing.getItems();

        int cost = plugin.getConfig().getInt("setting.levelcostperbrewing");

        if (p.getLevel() >= cost || handCopy.getType() == Material.AIR) {
            if (handCopy.getType() == Material.STICK && !brewingItems.isEmpty()) {
                brew(p, brewings, brewing, brewingItems, cost);
            } else if (handCopy.getType() == Material.AIR && !brewingItems.isEmpty()) {
                removeAllFromBrewingStand(p, brewings, brewing, brewingItems);
            } else {
                handleItemAddToBrewingStand(p, handOriginal, handCopy, brewings, brewing, brewingItems);
            }
        } else {
            p.sendMessage(plugin.getConfigString("message.event.notenoughlevel")
                    .replace("%number%", String.valueOf(cost)));
        }

        // Send action bar and brewing stand status
        if (!brewingItems.isEmpty()) {
            sendActionBar(p, brewingItems);
        }

        // Always refresh brewing stand
        Bukkit.getScheduler().runTaskLater(plugin, () -> refreshBrewingStand(p, clickedBlock, brewingItems), 1L);
    }

    private void removeAllFromBrewingStand(Player p, List<Brewing> brewings, Brewing brewing, List<ItemStack> brewingItems) {
        for (ItemStack itemStack : brewingItems) {
            p.getInventory().addItem(itemStack);
        }

        brewingItems.clear();
        brewings.remove(brewing);
    }

    private void brew(Player p, List<Brewing> brewings, Brewing brewing, List<ItemStack> brewingItems, int cost) {
        for (ItemCombinationEntity recipe : plugin.getItemCombinationEntities()) {
            List<ItemStack> recipeItems = recipe.getCombination()
                    .stream()
                    .map(ItemEntity::getItemStack)
                    .collect(Collectors.toList());

            if (isRecipe(recipeItems, brewingItems)) {
                p.getInventory().addItem(recipe.getCombinesIntoItem().getItemEntity().getItemStack());
                p.setLevel(p.getLevel() - cost);
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                brewingItems.clear();
                brewings.remove(brewing);
                return;
            }
        }
        p.sendMessage(plugin.getConfigString("message.event.recipenotfound"));
    }

    private boolean isRecipe(List<ItemStack> recipeItems, List<ItemStack> brewingItems) {
        // This works because items are mutually exclusive, ItemStack#equals() checks for amount, type and ItemMeta
        return new HashSet<>(recipeItems).equals(new HashSet<>(brewingItems));
    }

    private void refreshBrewingStand(Player p, Block clickedBlock, List<ItemStack> brewingItems) {
        BrewingStand brewingStand = (BrewingStand) Material.BREWING_STAND.createBlockData();
        switch (brewingItems.size()) {
            case 1:
                brewingStand.setBottle(0, true);
                break;
            case 2:
                brewingStand.setBottle(0, true);
                brewingStand.setBottle(1, true);
                break;
            case 3:
                brewingStand.setBottle(0, true);
                brewingStand.setBottle(1, true);
                brewingStand.setBottle(2, true);
                break;
            default:
                break;
        }

        p.sendBlockChange(clickedBlock.getLocation(), brewingStand);
    }

    private void sendActionBar(Player p, List<ItemStack> brewingItems) {
        ComponentBuilder builder = new ComponentBuilder();
        for (ItemStack item : brewingItems) {
            if (builder.getParts().isEmpty()) {
                builder.append(item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : WordUtils.capitalizeFully(item.getType().getKey().getKey().replace("_", " ")));
            } else {
                builder.append(" || ").color(net.md_5.bungee.api.ChatColor.DARK_GRAY)
                        .append(item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : WordUtils.capitalizeFully(item.getType().getKey().getKey().replace("_", " ")));
            }
        }

        p.sendActionBar(builder.create());
    }

    private void handleItemAddToBrewingStand(Player p, ItemStack handOriginal, ItemStack handCopy, List<Brewing> brewings, Brewing brewing, List<ItemStack> brewingItems) {
        // If item is in any combination
        for (ItemCombinationEntity itemCombinationEntity : plugin.getItemCombinationEntities()) {
            for (ItemEntity itemEntity : itemCombinationEntity.getCombination()) {
                if (itemEntity.getMaterial() == handCopy.getType()) {
                    // If empty, new helper so add it to list
                    if (brewingItems.isEmpty()) {
                        addToBrewingItems(p, handOriginal, handCopy, brewingItems);

                        brewings.add(brewing);
                    } else {
                        if (brewingItems.size() <= 2) {
                            // This will eliminate the possibility of 2 items with different display names but the same type,
                            // we could change this, but it requires giving all items a display name
                            if (brewingItems.stream().noneMatch(c -> c.getType() == handCopy.getType())) {
                                addToBrewingItems(p, handOriginal, handCopy, brewingItems);
                            } else {
                                p.sendMessage(plugin.getConfigString("message.event.brewingcomponentalreadyadded"));
                            }
                        } else {
                            p.sendMessage(plugin.getConfigString("message.event.brewingfull"));
                        }
                    }

                    // No matter what's the result, break the for loop if the combination was found.
                    return;
                }
            }
        }
    }

    private void addToBrewingItems(Player p, ItemStack handOriginal, ItemStack handCopy, List<ItemStack> brewingItems) {
        handOriginal.setAmount(handOriginal.getAmount() - 1);
        p.playSound(p.getLocation(), Sound.BLOCK_BONE_BLOCK_PLACE, 1, 1);
        brewingItems.add(handCopy);
    }

}
