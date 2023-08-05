package de.jandev.falldown.listener.handler.specialitem;

import de.jandev.falldown.Falldown;
import de.jandev.falldown.model.GameState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class InteractLightningAttackHandler {

    private final Falldown plugin;

    public InteractLightningAttackHandler(Falldown plugin) {
        this.plugin = plugin;
    }

    public void handle(Player p) {
        if (plugin.getState() != GameState.IN_GAME) {
            p.sendMessage(plugin.getConfigString("message.specialitem.notingrace"));
            return;
        }
        Entity target = p.getTargetEntity(50);

        if (target != null) {
            p.getWorld().strikeLightning(target.getLocation());
            p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
        }
    }

}
