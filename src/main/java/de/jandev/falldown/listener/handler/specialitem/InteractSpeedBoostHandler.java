package de.jandev.falldown.listener.handler.specialitem;

import de.jandev.falldown.Falldown;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffectType;

public class InteractSpeedBoostHandler {

    private final Falldown plugin;

    public InteractSpeedBoostHandler(Falldown plugin) {
        this.plugin = plugin;
    }

    public void handle(Player p, Action action) {
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);

            p.addPotionEffect(PotionEffectType.SPEED.createEffect((plugin.getConfig().getInt("specialitem.speedduration") * 20), plugin.getConfig().getInt("specialitem.speedlevel")));

            p.sendMessage(plugin.getConfigString("message.specialitem.speed"));
            p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_DRINK, 1, 1);
        }
    }

}
