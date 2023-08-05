package de.jandev.falldown.listener.handler.specialitem;

import de.jandev.falldown.Falldown;
import de.jandev.falldown.model.GameState;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.block.Action;

import java.util.Objects;

public class InteractFriendlyFoeHandler {

    private final Falldown plugin;

    public InteractFriendlyFoeHandler(Falldown plugin) {
        this.plugin = plugin;
    }

    public void handle(Player p, Action action) {
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (plugin.getState() != GameState.IN_GAME) {
                p.sendMessage(plugin.getConfigString("message.specialitem.notingrace"));
                return;
            }
            p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);

            for (int i = 0; i < plugin.getConfig().getInt("specialitem.wolfamount"); i++) {
                Wolf wolf = (Wolf) p.getLocation().getWorld().spawnEntity(p.getLocation(), EntityType.WOLF);
                wolf.setCanPickupItems(false);
                wolf.setAdult();
                wolf.setOwner(p);
                Objects.requireNonNull(wolf.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                        .addModifier(new AttributeModifier("GENERIC_MOVEMENT_SPEED",
                                plugin.getConfig().getDouble("specialitem.wolfspeedadd"),
                                AttributeModifier.Operation.ADD_NUMBER));
                plugin.getWolfList().put(wolf, plugin.getConfig().getInt("specialitem.wolftime"));
            }

            p.sendMessage(plugin.getConfigString("message.specialitem.wolf"));
            p.playSound(p.getLocation(), Sound.ENTITY_WOLF_GROWL, 1, 1);
        }
    }

}
