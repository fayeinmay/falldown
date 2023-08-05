package de.jandev.falldown.task;

import de.jandev.falldown.Falldown;
import org.bukkit.EntityEffect;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class InGameTask extends BukkitRunnable {

    private final Falldown plugin;
    private int time;

    public InGameTask(Falldown plugin) {
        this.plugin = plugin;
        time = plugin.getConfig().getInt("setting.totalgametime");
    }

    @Override
    public void run() {
        if (time <= 0) {
            plugin.startEnding();
        } else if (time == 120 || time == 60 || time == 30 || time == 10 || time <= 5) {
            plugin.broadcast(plugin.getConfigString("message.task.ingameends")
                    .replace("%number%", Integer.toString(time)));
        }

        List<Player> toRemove = new ArrayList<>();
        plugin.getInvisibleList().entrySet().forEach(entry -> {
            if (entry.getValue() <= 0) {
                for (Player p : plugin.getActivePlayers()) {
                    p.showPlayer(plugin, entry.getKey());
                }
                toRemove.add(entry.getKey());
                entry.getKey().playSound(entry.getKey().getLocation(), Sound.ITEM_ARMOR_EQUIP_ELYTRA, 1, 1);
                entry.getKey().getWorld().strikeLightningEffect(entry.getKey().getLocation());
            } else {
                entry.setValue(entry.getValue() - 1);
            }
        });
        plugin.getInvisibleList().keySet().removeAll(toRemove);

        List<Player> toRemove2 = new ArrayList<>();
        plugin.getShieldList().entrySet().forEach(entry -> {
            if (entry.getValue() <= 0) {
                toRemove2.add(entry.getKey());
                entry.getKey().playSound(entry.getKey().getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1, 1);
            } else {
                entry.setValue(entry.getValue() - 1);
            }
        });
        plugin.getShieldList().keySet().removeAll(toRemove2);

        List<Entity> toRemove3 = new ArrayList<>();
        plugin.getWolfList().entrySet().forEach(e -> {
            if (e.getValue() <= 0) {
                e.getKey().playEffect(EntityEffect.ENTITY_POOF);
                e.getKey().remove();
                toRemove3.add(e.getKey());
            } else {
                e.setValue(e.getValue() - 1);
            }
        });
        plugin.getWolfList().keySet().removeAll(toRemove3);

        plugin.updateScoreboard(time);
        time--;
    }
}
