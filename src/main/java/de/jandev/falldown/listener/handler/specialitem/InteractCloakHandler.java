package de.jandev.falldown.listener.handler.specialitem;

import de.jandev.falldown.Falldown;
import de.jandev.falldown.model.GameState;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class InteractCloakHandler {

    private final Falldown plugin;

    public InteractCloakHandler(Falldown plugin) {
        this.plugin = plugin;
    }

    public void handle(Player p, Action action) {
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (plugin.getState() != GameState.IN_GAME) {
                p.sendMessage(plugin.getConfigString("message.specialitem.notingrace"));
                return;
            }
            p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);

            for (Player online : plugin.getActivePlayers()) {
                online.hidePlayer(plugin, p);
            }

            plugin.getInvisibleList().merge(p, plugin.getConfig().getInt("specialitem.cloaktime"), Integer::sum);
            p.sendMessage(plugin.getConfigString("message.specialitem.cloakactivate"));
            p.playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_ELYTRA, 1, 1);
        }
    }
}
