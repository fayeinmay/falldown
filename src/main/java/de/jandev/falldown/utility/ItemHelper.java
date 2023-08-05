package de.jandev.falldown.utility;

import de.jandev.falldown.model.item.ItemEntity;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class ItemHelper {

    private ItemHelper() {
    }

    public static List<ItemEntity> getSwords() {
        List<ItemEntity> entities = new ArrayList<>();

        entities.add(new ItemEntity(Material.WOODEN_SWORD, 1));
        entities.add(new ItemEntity(Material.GOLDEN_SWORD, 1));
        entities.add(new ItemEntity(Material.STONE_SWORD, 1));
        entities.add(new ItemEntity(Material.IRON_SWORD, 1));
        entities.add(new ItemEntity(Material.DIAMOND_SWORD, 1));
        entities.add(new ItemEntity(Material.NETHERITE_SWORD, 1));

        return entities;
    }

    public static List<ItemEntity> getArmor() {
        List<ItemEntity> entities = new ArrayList<>();

        entities.add(new ItemEntity(Material.LEATHER_HELMET, 1));
        entities.add(new ItemEntity(Material.LEATHER_CHESTPLATE, 1));
        entities.add(new ItemEntity(Material.LEATHER_LEGGINGS, 1));
        entities.add(new ItemEntity(Material.LEATHER_BOOTS, 1));
        entities.add(new ItemEntity(Material.GOLDEN_HELMET, 1));
        entities.add(new ItemEntity(Material.GOLDEN_CHESTPLATE, 1));
        entities.add(new ItemEntity(Material.GOLDEN_LEGGINGS, 1));
        entities.add(new ItemEntity(Material.GOLDEN_BOOTS, 1));
        entities.add(new ItemEntity(Material.CHAINMAIL_HELMET, 1));
        entities.add(new ItemEntity(Material.CHAINMAIL_CHESTPLATE, 1));
        entities.add(new ItemEntity(Material.CHAINMAIL_LEGGINGS, 1));
        entities.add(new ItemEntity(Material.CHAINMAIL_BOOTS, 1));
        entities.add(new ItemEntity(Material.IRON_HELMET, 1));
        entities.add(new ItemEntity(Material.IRON_CHESTPLATE, 1));
        entities.add(new ItemEntity(Material.IRON_LEGGINGS, 1));
        entities.add(new ItemEntity(Material.IRON_BOOTS, 1));
        entities.add(new ItemEntity(Material.DIAMOND_HELMET, 1));
        entities.add(new ItemEntity(Material.DIAMOND_CHESTPLATE, 1));
        entities.add(new ItemEntity(Material.DIAMOND_LEGGINGS, 1));
        entities.add(new ItemEntity(Material.DIAMOND_BOOTS, 1));
        entities.add(new ItemEntity(Material.NETHERITE_HELMET, 1));
        entities.add(new ItemEntity(Material.NETHERITE_CHESTPLATE, 1));
        entities.add(new ItemEntity(Material.NETHERITE_LEGGINGS, 1));
        entities.add(new ItemEntity(Material.NETHERITE_BOOTS, 1));

        return entities;
    }
}
