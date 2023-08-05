package de.jandev.falldown.listener.handler.specialitem;

import de.jandev.falldown.Falldown;
import de.jandev.falldown.model.GameState;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class InteractBandageHandler {

    private final Falldown plugin;

    public InteractBandageHandler(Falldown plugin) {
        this.plugin = plugin;
    }

    public void handle(Player p, Action action) {
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (plugin.getState() != GameState.IN_GAME) {
                p.sendMessage(plugin.getConfigString("message.specialitem.notingrace"));
                return;
            }
            p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);

            p.setHealth(Math.min(20, p.getHealth() + plugin.getConfig().getInt("specialitem.bandageheal")));

            p.sendMessage(plugin.getConfigString("message.specialitem.bandage"));
            p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_DRINK, 1, 1);
        }
    }

}
