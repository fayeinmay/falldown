package de.jandev.falldown.listener.handler;

import de.jandev.falldown.Falldown;
import de.jandev.falldown.utility.EnchantmentHelper;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InteractEnchantmentTableHandler {

    private final Falldown plugin;

    public InteractEnchantmentTableHandler(Falldown plugin) {
        this.plugin = plugin;
    }

    public void handle(Player p) {
        int cost = plugin.getConfig().getInt("setting.levelcostperenchant");
        if (p.getLevel() >= cost) {
            ItemStack hand = p.getInventory().getItemInMainHand();
            Enchantment enchantment = null;

            if (EnchantmentHelper.isSword(hand.getType())) {
                enchantment = EnchantmentHelper.enchant(EnchantmentHelper.getSwordEnchantments(), hand);
            } else if (EnchantmentHelper.isArmor(hand.getType())) {
                enchantment = EnchantmentHelper.enchant(EnchantmentHelper.isBoots(
                        hand.getType()) ?
                        EnchantmentHelper.getArmorEnchantments(true)
                        : EnchantmentHelper.getArmorEnchantments(false), hand);
            } else if (hand.getType() == Material.BOW) {
                enchantment = EnchantmentHelper.enchant(EnchantmentHelper.getBowEnchantments(), hand);
            } else if (hand.getType() == Material.CROSSBOW) {
                enchantment = EnchantmentHelper.enchant(EnchantmentHelper.getCrossbowEnchantments(), hand);
            }

            if (enchantment != null) {
                p.sendActionBar(new ComponentBuilder("+ ")
                        .color(net.md_5.bungee.api.ChatColor.GREEN)
                        .bold(true)
                        .append(WordUtils.capitalizeFully(enchantment.getKey().getKey().replace("_", " ")))
                        .color(net.md_5.bungee.api.ChatColor.AQUA)
                        .bold(true)
                        .append(" Enchantment").color(net.md_5.bungee.api.ChatColor.GRAY)
                        .create());
                p.setLevel(p.getLevel() - cost);
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            } else {
                p.sendMessage(plugin.getConfigString("message.event.notenchantable"));
            }
        } else {
            p.sendMessage(plugin.getConfigString("message.event.notenoughlevel")
                    .replace("%number%", String.valueOf(cost)));
        }
    }

}
