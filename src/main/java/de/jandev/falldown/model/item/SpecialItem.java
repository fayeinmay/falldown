package de.jandev.falldown.model.item;

import org.bukkit.Material;

import java.util.Collections;

public enum SpecialItem {
    INVISIBILITY(new ItemEntity("&7&lCloaking device", Material.COAL, 1, Collections.singletonList("&8Now you see me, now you don't"))),
    BANDAGE(new ItemEntity("&4&lBandage", Material.PAPER, 1, Collections.singletonList("&8First aid kit"))),
    LIGHTNING_ATTACK(new ItemEntity("&lPocket Thunderstorm Attack", Material.BLAZE_ROD, 1, Collections.singletonList("&1SMITE EM!"))),
    SPEED_BOOST(new ItemEntity("&9&lSpeed Boost", Material.SUGAR, 1, Collections.singletonList("&7Even faster than sprinting..."))),
    TELEPORT(new ItemEntity("&5&lTeleport", Material.ENDER_PEARL, 1, Collections.singletonList("&dGet out of here."))),
    FRIENDLY_FOE(new ItemEntity("&lSummon a friendly foe", Material.BONE, 1, Collections.singletonList("&7Call some allies to help you in the fight"))),
    SHIELD(new ItemEntity("&8&lShield", Material.IRON_INGOT, 1, Collections.singletonList("&7Gives you an additional shield for some time"))),
    GRENADE(new ItemEntity("&7&lGrenade", Material.FIREWORK_STAR, 1, Collections.singletonList("&lBOOM"))),
    DAMAGE_SPAM(new ItemEntity("&lEarthquake simulator", Material.WOODEN_SHOVEL, 1, Collections.singletonList("Enemy feels like there's an earthquake. Maybe there is?")));

    private final ItemEntity itemEntity;

    SpecialItem(ItemEntity itemEntity) {
        this.itemEntity = itemEntity;
    }

    public ItemEntity getItemEntity() {
        return itemEntity;
    }
}
