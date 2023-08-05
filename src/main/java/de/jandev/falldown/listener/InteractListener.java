package de.jandev.falldown.listener;

import de.jandev.falldown.Falldown;
import de.jandev.falldown.listener.handler.InteractBrewingStandHandler;
import de.jandev.falldown.listener.handler.InteractEnchantmentTableHandler;
import de.jandev.falldown.listener.handler.InteractSpectatorCompassHandler;
import de.jandev.falldown.listener.handler.specialitem.*;
import de.jandev.falldown.model.GameState;
import de.jandev.falldown.model.item.SpecialItem;
import de.jandev.falldown.model.player.PlayerType;
import de.jandev.falldown.utility.VotingHelper;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;

public class InteractListener implements Listener {

    private final Falldown plugin;

    public InteractListener(Falldown plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        boolean cancelled = false;
        Player p = e.getPlayer();
        Block clickedBlock = e.getClickedBlock();

        if ((e.getAction() == Action.PHYSICAL
                && clickedBlock != null
                && clickedBlock.getType() == Material.FARMLAND)

                || (e.getHand() != null
                && e.getHand() != EquipmentSlot.HAND)

                || (clickedBlock != null
                && (clickedBlock.getState() instanceof InventoryHolder
                || clickedBlock.getType() == Material.ENDER_CHEST
                || clickedBlock.getType() == Material.BEACON
                || clickedBlock.getType() == Material.FURNACE_MINECART
                || clickedBlock.getType() == Material.TRAPPED_CHEST
                || clickedBlock.getType() == Material.FLOWER_POT
                || clickedBlock.getType() == Material.ITEM_FRAME)
                && clickedBlock.getType() != Material.BREWING_STAND)

                || (plugin.getState() != GameState.GRACE_PERIOD
                && plugin.getState() != GameState.IN_GAME)

                || (plugin.getPlayers().get(p) != PlayerType.ACTIVE)) {
            e.setCancelled(true);
            cancelled = true;
        }

        if (plugin.getState() == GameState.LOBBY
                && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (p.getInventory().getItemInMainHand().getType() == Material.WRITTEN_BOOK) {
                e.setUseItemInHand(Event.Result.ALLOW);
                e.setUseInteractedBlock(Event.Result.DENY);
            } else if (p.getInventory().getItemInMainHand().getType() == Material.BOOK) {
                new VotingHelper(plugin).openInventory(p);
            }
            return;
        }

        if (plugin.getPlayers().get(p) == PlayerType.SPECTATOR
                && e.getMaterial() == Material.COMPASS) {
            new InteractSpectatorCompassHandler(plugin).handle(p);
            return;
        }

        if (!cancelled) {
            if (clickedBlock != null) {
                if (clickedBlock.getType() == Material.ENCHANTING_TABLE) {
                    e.setCancelled(true);
                    new InteractEnchantmentTableHandler(plugin).handle(p);
                    return;
                } else if (clickedBlock.getType() == Material.BREWING_STAND) {
                    e.setCancelled(true);
                    new InteractBrewingStandHandler(plugin).handle(p, clickedBlock);
                    return;
                }
            }
            tryMatchSpecialItem(p, e.getAction());
        }
    }

    private void tryMatchSpecialItem(Player p, Action action) {
        // Set cancelled if item requires it
        if (p.getInventory().getItemInMainHand().isSimilar(SpecialItem.INVISIBILITY.getItemEntity().getItemStack())) {
            new InteractCloakHandler(plugin).handle(p, action);
        } else if (p.getInventory().getItemInMainHand().isSimilar(SpecialItem.BANDAGE.getItemEntity().getItemStack())) {
            new InteractBandageHandler(plugin).handle(p, action);
        } else if (p.getInventory().getItemInMainHand().isSimilar(SpecialItem.LIGHTNING_ATTACK.getItemEntity().getItemStack())) {
            new InteractLightningAttackHandler(plugin).handle(p);
        } else if (p.getInventory().getItemInMainHand().isSimilar(SpecialItem.SPEED_BOOST.getItemEntity().getItemStack())) {
            new InteractSpeedBoostHandler(plugin).handle(p, action);
        } else if (p.getInventory().getItemInMainHand().isSimilar(SpecialItem.FRIENDLY_FOE.getItemEntity().getItemStack())) {
            new InteractFriendlyFoeHandler(plugin).handle(p, action);
        } else if (p.getInventory().getItemInMainHand().isSimilar(SpecialItem.SHIELD.getItemEntity().getItemStack())) {
            new InteractShieldHandler(plugin).handle(p, action);
        } else if (p.getInventory().getItemInMainHand().isSimilar(SpecialItem.GRENADE.getItemEntity().getItemStack())) {
            new InteractGrenadeHandler(plugin).handle(p, action);
        }
    }

}
