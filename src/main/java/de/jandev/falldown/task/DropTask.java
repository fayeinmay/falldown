package de.jandev.falldown.task;

import de.jandev.falldown.Falldown;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class DropTask extends BukkitRunnable {

    private final Falldown plugin;

    public DropTask(Falldown plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Map.Entry<Entity, Boolean> e : plugin.getCrystals().entrySet()) {
            List<Entity> nearbyEntities = e.getKey().getNearbyEntities(-0.3, 5, -0.3);

            for (Entity nearbyEntity : nearbyEntities) {
                if (nearbyEntity instanceof Player) {
                    Player p = (Player) nearbyEntity;
                    evaluateCrystalTrigger(e, p);
                }
            }
        }
    }

    private void evaluateCrystalTrigger(Map.Entry<Entity, Boolean> e, Player p) {
        if (Boolean.FALSE.equals(e.getValue())) {

            int level = ThreadLocalRandom.current().nextInt(plugin.getConfig().getInt("setting.levelgivemin"),
                    plugin.getConfig().getInt("setting.levelgivemax")) + 1;
            p.giveExpLevels(level);

            ItemStack item2 = null;
            if (ThreadLocalRandom.current().nextDouble() < plugin.getConfig().getDouble("setting.chancefordoublecrystalitem") / 100) {
                item2 = plugin.getItemEntities().get(ThreadLocalRandom.current().nextInt(plugin.getItemEntities().size())).getItemStack();
            }

            ItemStack item = plugin.getItemEntities().get(ThreadLocalRandom.current().nextInt(plugin.getItemEntities().size())).getItemStack();
            p.getInventory().addItem(item);

            if (item2 == null) {
                p.sendActionBar(new ComponentBuilder("+ ").color(ChatColor.GREEN).bold(true)
                        .append(item.getItemMeta().hasDisplayName() ?
                                item.getItemMeta().getDisplayName() :
                                WordUtils.capitalizeFully(item.getType().getKey().getKey().replace("_", " "))).color(ChatColor.GRAY)
                        .append(" || ").color(ChatColor.DARK_GRAY)
                        .append("+ ").color(ChatColor.GREEN).bold(true)
                        .append(String.valueOf(level)).color(ChatColor.AQUA).bold(true)
                        .append(" Level").color(ChatColor.GRAY)
                        .create());
            } else {
                p.getInventory().addItem(item2);

                p.sendActionBar(new ComponentBuilder("+ ").color(ChatColor.GREEN).bold(true)
                        .append(item.getItemMeta().hasDisplayName() ?
                                item.getItemMeta().getDisplayName() :
                                WordUtils.capitalizeFully(item.getType().getKey().getKey().replace("_", " "))).color(ChatColor.GRAY)
                        .append(" || ").color(ChatColor.DARK_GRAY)
                        .append(item2.getItemMeta().hasDisplayName() ? item2.getItemMeta().getDisplayName() :
                                WordUtils.capitalizeFully(item2.getType().getKey().getKey().replace("_", " "))).color(ChatColor.GRAY)
                        .append(" || ").color(ChatColor.DARK_GRAY)
                        .append("+ ").color(ChatColor.GREEN).bold(true)
                        .append(String.valueOf(level)).color(ChatColor.AQUA).bold(true)
                        .append(" Level").color(ChatColor.GRAY)
                        .create());
            }

            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            plugin.getCrystals().put(e.getKey(), true);

            if (!plugin.getWarnList().contains(p)) {
                plugin.getWarnList().add(p);
            }
        } else {
            // Because we check so fast so often we should not play the sound twice for the player
            if (!plugin.getWarnList().contains(p)) {
                p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT, 1, 1);
                plugin.getWarnList().add(p);
            }
        }
    }
}
