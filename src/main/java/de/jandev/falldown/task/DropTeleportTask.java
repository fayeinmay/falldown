package de.jandev.falldown.task;

import de.jandev.falldown.Falldown;
import de.jandev.falldown.listener.SuperCrystalListener;
import de.jandev.falldown.model.map.MapLocationEntity;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class DropTeleportTask extends BukkitRunnable {

    private final Falldown plugin;
    private final Map<Player, Integer> falldowns = new HashMap<>();
    private boolean end = false;

    public DropTeleportTask(Falldown plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        // remove player if they went offline in drop phase
        falldowns.keySet().removeIf(p -> !p.isOnline());

        // -1 because of the original teleport
        if (!falldowns.isEmpty() && Collections.frequency(falldowns.values(),
                plugin.getConfig().getInt("setting.crystalrounds") - 1) == falldowns.size()) {
            end = true;
        }

        for (Player p : plugin.getServer().getOnlinePlayers()) {
            if (p.getLocation().getY() < plugin.getCurrentMap().getDrop().getY() - 300) { // 750: Player-Spawn, 500: Crystal-Spawn, 450: Reset
                if (!end) {
                    teleport(p);
                    p.addPotionEffect(PotionEffectType.LEVITATION.createEffect(60, -128)
                            .withParticles(false)
                            .withAmbient(false)
                            .withIcon(false));
                    // Prevents endless loop on heavy lagging players, but will allow other non-lagging players to gain more crystals -> change behavior to spawn player solo?
                    if (falldowns.containsKey(p)) {
                        if (falldowns.get(p) < plugin.getConfig().getInt("setting.crystalrounds") - 1) {
                            falldowns.merge(p, 1, Integer::sum);
                        }
                    } else {
                        falldowns.merge(p, 1, Integer::sum);
                    }

                    plugin.getWarnList().remove(p);
                } else {
                    startNextTask();
                    break;
                }
            }
        }
    }

    private void startNextTask() {
        plugin.broadcast(plugin.getConfigString("message.task.gracestarts")
                .replace("%number%", Objects.requireNonNull(plugin.getConfig().getString("setting.gracetime"))));

        for (Entity e : plugin.getCrystals().keySet()) {
            e.remove();
        }
        plugin.getCrystals().clear();

        // We remove items here in case a player disconnected on drop and dropped their items
        plugin.removeEntities(plugin.getCurrentMap().getEndpoint().getLocation().getWorld().getEntities());

        int counter = 0;
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            p.removePotionEffect(PotionEffectType.LEVITATION);
            p.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(100, 0));
            p.teleport(plugin.getCurrentMap().getSpawn().get(counter).getLocation());
            p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);

            if (!hasSword(p)) {
                p.getInventory().addItem(plugin.getMinimalSword());
            }

            counter++;
        }

        setWorldBorder();

        spawnSuperCrystal();
        plugin.startGracePeriod();
    }

    private void setWorldBorder() {
        WorldBorder worldBorder = plugin.getCurrentMap().getEndpoint().getLocation().getWorld().getWorldBorder();
        worldBorder.setCenter(plugin.getCurrentMap().getEndpoint().getLocation());
        worldBorder.setSize(getFarthestSpawnFromEndpoint(), 0);
        worldBorder.setDamageBuffer(-1);
        worldBorder.setDamageAmount(4);
        worldBorder.setSize(plugin.getConfig().getInt("setting.worldborderendsizediameter"),
                plugin.getConfig().getInt("setting.worldbordertime"));
    }

    private void spawnSuperCrystal() {
        new SuperCrystalListener(plugin, plugin.getCurrentMap().getCrystal().getLocation());
    }

    private int getFarthestSpawnFromEndpoint() {
        Location endpoint = plugin.getCurrentMap().getEndpoint().getLocation();
        double farthest = 0;

        for (MapLocationEntity loc : plugin.getCurrentMap().getSpawn()) {
            double distance = endpoint.distance(loc.getLocation());

            if (distance > farthest) {
                farthest = distance;
            }
        }

        return (int) farthest * 2;
    }

    private void teleport(Player p) {
        Location loc = p.getLocation();
        loc.setY(plugin.getCurrentMap().getDrop().getY());
        p.teleport(loc);
    }

    private boolean hasSword(Player p) {
        for (ItemStack itemStack : p.getInventory().getContents()) {
            if (itemStack != null && itemStack.getType().toString().contains("SWORD")) {
                return true;
            }
        }
        return false;
    }
}
