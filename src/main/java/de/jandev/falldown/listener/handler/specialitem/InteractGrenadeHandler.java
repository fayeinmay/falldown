package de.jandev.falldown.listener.handler.specialitem;

import de.jandev.falldown.Falldown;
import de.jandev.falldown.model.GameState;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class InteractGrenadeHandler {

    private final Falldown plugin;

    public InteractGrenadeHandler(Falldown plugin) {
        this.plugin = plugin;
    }

    public void handle(Player p, Action action) {
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (plugin.getState() != GameState.IN_GAME) {
                p.sendMessage(plugin.getConfigString("message.specialitem.notingrace"));
                return;
            }
            p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);

            final Item grenade = p.getWorld().dropItem(p.getEyeLocation(), new ItemStack(Material.FIREWORK_STAR));
            grenade.setVelocity(p.getLocation().getDirection().multiply(plugin.getConfig().getDouble("specialitem.grenadethrowpower")));
            grenade.setPickupDelay(1000);

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                TNTPrimed tnt = (TNTPrimed) p.getWorld().spawnEntity(grenade.getLocation(), EntityType.PRIMED_TNT);
                tnt.setFuseTicks(0);
                tnt.setYield(plugin.getConfig().getInt("specialitem.grenaderadius"));
                tnt.setIsIncendiary(false);
            }, plugin.getConfig().getLong("specialitem.grenadedelay") * 20);

            p.playSound(p.getLocation(), Sound.ENTITY_EGG_THROW, 1, 1);
        }
    }

}
