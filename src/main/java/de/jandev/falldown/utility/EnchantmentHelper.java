package de.jandev.falldown.utility;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class EnchantmentHelper {

    private EnchantmentHelper() {
    }

    public static boolean isSword(Material material) {
        return material == Material.DIAMOND_SWORD
                || material == Material.IRON_SWORD
                || material == Material.STONE_SWORD
                || material == Material.WOODEN_SWORD
                || material == Material.NETHERITE_SWORD
                || material == Material.GOLDEN_SWORD;
    }

    public static boolean isArmor(Material material) {
        return material == Material.LEATHER_LEGGINGS
                || material == Material.GOLDEN_LEGGINGS
                || material == Material.DIAMOND_LEGGINGS
                || material == Material.IRON_LEGGINGS
                || material == Material.CHAINMAIL_LEGGINGS
                || material == Material.NETHERITE_LEGGINGS
                || material == Material.LEATHER_HELMET
                || material == Material.GOLDEN_HELMET
                || material == Material.DIAMOND_HELMET
                || material == Material.IRON_HELMET
                || material == Material.CHAINMAIL_HELMET
                || material == Material.NETHERITE_HELMET
                || material == Material.LEATHER_CHESTPLATE
                || material == Material.GOLDEN_CHESTPLATE
                || material == Material.DIAMOND_CHESTPLATE
                || material == Material.IRON_CHESTPLATE
                || material == Material.CHAINMAIL_CHESTPLATE
                || material == Material.NETHERITE_CHESTPLATE
                || material == Material.LEATHER_BOOTS
                || material == Material.GOLDEN_BOOTS
                || material == Material.DIAMOND_BOOTS
                || material == Material.IRON_BOOTS
                || material == Material.CHAINMAIL_BOOTS
                || material == Material.NETHERITE_BOOTS;
    }

    public static boolean isBoots(Material material) {
        return material == Material.LEATHER_BOOTS
                || material == Material.GOLDEN_BOOTS
                || material == Material.DIAMOND_BOOTS
                || material == Material.IRON_BOOTS
                || material == Material.CHAINMAIL_BOOTS
                || material == Material.NETHERITE_BOOTS;
    }

    public static List<Enchantment> getSwordEnchantments() {
        return new LinkedList<>(Arrays.asList(Enchantment.FIRE_ASPECT,
                Enchantment.DAMAGE_ALL,
                Enchantment.KNOCKBACK));
    }

    public static List<Enchantment> getArmorEnchantments(boolean isBoots) {
        if (isBoots) {
            return new LinkedList<>(Arrays.asList(Enchantment.PROTECTION_ENVIRONMENTAL,
                    Enchantment.PROTECTION_EXPLOSIONS,
                    Enchantment.PROTECTION_FIRE,
                    Enchantment.PROTECTION_PROJECTILE,
                    Enchantment.PROTECTION_FALL));
        } else {
            return new LinkedList<>(Arrays.asList(Enchantment.PROTECTION_ENVIRONMENTAL,
                    Enchantment.PROTECTION_EXPLOSIONS,
                    Enchantment.PROTECTION_FIRE,
                    Enchantment.PROTECTION_PROJECTILE));
        }
    }

    public static List<Enchantment> getBowEnchantments() {
        return new LinkedList<>(Arrays.asList(Enchantment.ARROW_DAMAGE,
                Enchantment.ARROW_FIRE,
                Enchantment.ARROW_INFINITE,
                Enchantment.ARROW_KNOCKBACK));
    }

    public static List<Enchantment> getCrossbowEnchantments() {
        return new LinkedList<>(Arrays.asList(Enchantment.QUICK_CHARGE,
                Enchantment.MULTISHOT,
                Enchantment.PIERCING));
    }

    public static Enchantment enchant(List<Enchantment> enchantments, ItemStack item) {
        Enchantment enchantment = enchantments.get(ThreadLocalRandom.current().nextInt(enchantments.size()));
        int level = ThreadLocalRandom.current().nextInt(enchantment.getMaxLevel()) + 1;

        Map<Enchantment, Integer> enchantmentsOnItem = item.getEnchantments();

        while ((enchantmentsOnItem.containsKey(enchantment) && enchantmentsOnItem.get(enchantment) >= level)) {
            // This is inefficient, but I don't want to map the already tried levels right now.
            if (enchantmentsOnItem.get(enchantment) == enchantment.getMaxLevel()) {
                enchantments.remove(enchantment);
            }

            if (!enchantments.isEmpty()) {
                if (enchantments.size() != 1) {
                    enchantment = enchantments.get(ThreadLocalRandom.current().nextInt(enchantments.size()));
                } else {
                    enchantment = enchantments.get(0);
                }

                level = ThreadLocalRandom.current().nextInt(enchantment.getMaxLevel()) + 1;
            } else {
                enchantment = null;
                break;
            }
        }

        if (enchantment != null) {
            item.addEnchantment(enchantment, level);
        }
        return enchantment;
    }
}
