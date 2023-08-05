package de.jandev.falldown.utility;

import de.jandev.falldown.model.item.ItemCombinationEntity;
import de.jandev.falldown.model.item.ItemEntity;
import de.jandev.falldown.model.item.SpecialItem;
import de.jandev.falldown.model.item.enchantment.EnchantmentEntity;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DefaultItemHelper {

    private static final String ITEM_INGREDIENT = "&lItem ingredient";

    // I have no clue why, but this only works when it's defined as class fields, otherwise the yaml won't connect item to combinations
    private static final ItemEntity ingredient1 = new ItemEntity(Material.GHAST_TEAR, 1, Collections.singletonList(ITEM_INGREDIENT));
    private static final ItemEntity ingredient2 = new ItemEntity(Material.BLAZE_POWDER, 1, Collections.singletonList(ITEM_INGREDIENT));
    private static final ItemEntity ingredient3 = new ItemEntity(Material.STRING, 1, Collections.singletonList(ITEM_INGREDIENT));
    private static final ItemEntity ingredient4 = new ItemEntity(Material.SUGAR_CANE, 1, Collections.singletonList(ITEM_INGREDIENT));
    private static final ItemEntity ingredient5 = new ItemEntity(Material.REDSTONE, 1, Collections.singletonList(ITEM_INGREDIENT));
    private static final ItemEntity ingredient6 = new ItemEntity(Material.FEATHER, 1, Collections.singletonList(ITEM_INGREDIENT));
    private static final ItemEntity ingredient7 = new ItemEntity(Material.ENDER_EYE, 1, Collections.singletonList(ITEM_INGREDIENT));
    private static final ItemEntity ingredient8 = new ItemEntity(Material.BONE_MEAL, 1, Collections.singletonList(ITEM_INGREDIENT));
    private static final ItemEntity ingredient9 = new ItemEntity(Material.WHEAT, 1, Collections.singletonList(ITEM_INGREDIENT));
    private static final ItemEntity ingredient10 = new ItemEntity(Material.CLAY_BALL, 1, Collections.singletonList(ITEM_INGREDIENT));
    private static final ItemEntity ingredient11 = new ItemEntity(Material.GUNPOWDER, 1, Collections.singletonList(ITEM_INGREDIENT));
    private static final ItemEntity ingredient12 = new ItemEntity(Material.GOLD_NUGGET, 1, Collections.singletonList(ITEM_INGREDIENT));

    private DefaultItemHelper() {
    }

    public static List<ItemEntity> getDefaultItems() {
        List<ItemEntity> entities = new ArrayList<>();

        // Ingredients
        entities.add(ingredient1);
        entities.add(ingredient2);
        entities.add(ingredient3);
        entities.add(ingredient4);
        entities.add(ingredient5);
        entities.add(ingredient6);
        entities.add(ingredient7);
        entities.add(ingredient8);
        entities.add(ingredient9);
        entities.add(ingredient10);
        entities.add(ingredient11);
        entities.add(ingredient12);

        // Swords
        entities.addAll(ItemHelper.getSwords());

        // Bows
        entities.add(new ItemEntity(Material.BOW, 1));
        entities.add(new ItemEntity("Fast Crossbow", Material.CROSSBOW, 1, Collections.singletonList("Shoot them harder."), Collections.singletonList(new EnchantmentEntity(Enchantment.QUICK_CHARGE, 2))));

        // Armor
        entities.addAll(ItemHelper.getArmor());

        // Shield
        entities.add(new ItemEntity(Material.SHIELD, 1));

        // Arrows
        entities.add(new ItemEntity(Material.ARROW, 16));

        // Other
        entities.add(new ItemEntity(Material.FISHING_ROD, 1));
        entities.add(new ItemEntity(Material.FLINT_AND_STEEL, 1));
        entities.add(new ItemEntity(Material.WATER_BUCKET, 1));
        entities.add(new ItemEntity(Material.LAVA_BUCKET, 1));
        entities.add(new ItemEntity(Material.GOLDEN_APPLE, 1));

        return entities;
    }

    public static List<ItemCombinationEntity> getDefaultCombinations() {
        List<ItemCombinationEntity> combinationEntities = new ArrayList<>();

        // Default item combinations
        combinationEntities.add(new ItemCombinationEntity(Arrays.asList(getDefaultItems().get(0), getDefaultItems().get(1), getDefaultItems().get(2)), SpecialItem.INVISIBILITY));
        combinationEntities.add(new ItemCombinationEntity(Arrays.asList(getDefaultItems().get(3), getDefaultItems().get(4)), SpecialItem.BANDAGE));
        combinationEntities.add(new ItemCombinationEntity(Collections.singletonList(getDefaultItems().get(5)), SpecialItem.SPEED_BOOST));
        combinationEntities.add(new ItemCombinationEntity(Collections.singletonList(getDefaultItems().get(6)), SpecialItem.TELEPORT));
        combinationEntities.add(new ItemCombinationEntity(Arrays.asList(getDefaultItems().get(7), getDefaultItems().get(8), getDefaultItems().get(5)), SpecialItem.FRIENDLY_FOE));
        combinationEntities.add(new ItemCombinationEntity(Arrays.asList(getDefaultItems().get(9), getDefaultItems().get(2)), SpecialItem.SHIELD));
        combinationEntities.add(new ItemCombinationEntity(Arrays.asList(getDefaultItems().get(10), getDefaultItems().get(4)), SpecialItem.GRENADE));
        combinationEntities.add(new ItemCombinationEntity(Arrays.asList(getDefaultItems().get(11), getDefaultItems().get(5)), SpecialItem.LIGHTNING_ATTACK));
        combinationEntities.add(new ItemCombinationEntity(Collections.singletonList(getDefaultItems().get(1)), SpecialItem.DAMAGE_SPAM));

        return combinationEntities;
    }

    public static ItemEntity getSuperItem() {
        return new ItemEntity("&3Excalibur",
                Material.DIAMOND_SWORD,
                1,
                Collections.singletonList("&bFresh from the stone"),
                Arrays.asList(new EnchantmentEntity(Enchantment.DAMAGE_ALL, 5),
                        new EnchantmentEntity(Enchantment.FIRE_ASPECT, 2)));
    }

}
