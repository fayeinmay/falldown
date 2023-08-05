package de.jandev.falldown.listener.handler.specialitem;

import de.jandev.falldown.Falldown;
import de.jandev.falldown.model.GameState;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;


public class InteractShieldHandler {

    private final Falldown plugin;

    public InteractShieldHandler(Falldown plugin) {
        this.plugin = plugin;
    }

    public void handle(Player p, Action action) {
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (plugin.getState() != GameState.IN_GAME) {
                p.sendMessage(plugin.getConfigString("message.specialitem.notingrace"));
                return;
            }
            p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);

            int time = plugin.getConfig().getInt("specialitem.shieldtime");
            p.addPotionEffect(PotionEffectType.GLOWING.createEffect(time * 20, 1));
            plugin.getShieldList().put(p, time);

            p.sendMessage(plugin.getConfigString("message.specialitem.shield"));
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
        }
    }

}
