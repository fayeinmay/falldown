package de.jandev.falldown.listener;

import de.jandev.falldown.Falldown;
import de.jandev.falldown.model.GameState;
import de.jandev.falldown.model.item.SpecialItem;
import de.jandev.falldown.model.player.PlayerType;
import de.jandev.falldown.task.DamageTask;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

    private final Falldown plugin;

    public DamageListener(Falldown plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (plugin.getState() != GameState.IN_GAME) {
            e.setCancelled(true);
        }
        if (e.getEntity() instanceof Zombie) {
            e.setCancelled(true);
            if (e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
                e.getEntity().setFireTicks(0);
            }
        }
        if (e.getEntity() instanceof Player && plugin.getPlayers().get(e.getEntity()) == PlayerType.SPECTATOR) {
            e.getEntity().setFireTicks(0);
            e.setCancelled(true);
        }
        if (e.getEntity() instanceof EnderCrystal) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player
                && plugin.getPlayers().get(e.getDamager()) == PlayerType.SPECTATOR) {
            e.setCancelled(true);
            return;
        }
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Zombie) {
            e.setCancelled(true);
            return;
        }
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player
                && plugin.getShieldList().containsKey(e.getEntity())) {
            e.setDamage(e.getDamage() * plugin.getConfig().getDouble("specialitem.shielddamagemultiplier"));
            return;
        }

        if (e.getEntity() instanceof Player && e.getDamager() instanceof TNTPrimed) {
            e.setDamage(e.getDamage() * plugin.getConfig().getDouble("specialitem.explosiondamagemultiplier"));
            return;
        }

        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player
                && plugin.getState() == GameState.IN_GAME
                && ((Player) e.getDamager()).getInventory().getItemInMainHand().isSimilar(SpecialItem.DAMAGE_SPAM.getItemEntity().getItemStack())) {
            e.setCancelled(true);
            Player damager = (Player) e.getDamager();
            damager.getInventory().getItemInMainHand().setAmount(damager.getInventory().getItemInMainHand().getAmount() - 1);
            new DamageTask((Player) e.getEntity(), plugin.getConfig().getInt("specialitem.damagespamtime") * 6.66).runTaskTimer(plugin, 0L, 3L);
        }
    }

}
