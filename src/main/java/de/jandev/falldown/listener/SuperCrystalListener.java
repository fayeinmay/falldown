package de.jandev.falldown.listener;

import de.jandev.falldown.Falldown;
import de.jandev.falldown.model.GameState;
import de.jandev.falldown.model.player.PlayerType;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

public class SuperCrystalListener implements Listener {

    private final Falldown plugin;
    private final Location location;

    public SuperCrystalListener(Falldown plugin, Location location) {
        this.plugin = plugin;
        this.location = location;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        location.getWorld().spawnEntity(location, EntityType.ENDER_CRYSTAL);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof EnderCrystal
                && e.getDamager() instanceof Player
                && plugin.getPlayers().get(e.getDamager()) != PlayerType.SPECTATOR
                && plugin.getState() == GameState.IN_GAME
                && e.getEntity().getLocation().toBlockKey() == location.toBlockKey()) {
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                p.playSound(p.getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 1);
                p.sendMessage(plugin.getConfigString("message.event.crystalcaptured")
                        .replace("%player%", ((Player) e.getDamager()).getDisplayName()));
            }
            location.createExplosion(e.getEntity(), plugin.getConfig().getInt("setting.supercrystaldamage"), false, false);
            location.getNearbyPlayers(plugin.getConfig().getInt("setting.supercrystalslowrange"))
                    .forEach(c -> c.addPotionEffect(PotionEffectType.SLOW.createEffect(plugin.getConfig().getInt("setting.supercrystalslowduration") * 20, 10)));
            e.getEntity().remove();

            if (plugin.getSuperItem() != null) {
                ((Player) e.getDamager()).getInventory().addItem(plugin.getSuperItem().getItemStack());
            }

            HandlerList.unregisterAll(this);
        }
    }
}
