package de.jandev.falldown.model.item.enchantment;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("EnchantmentEntity")
public class EnchantmentEntity implements ConfigurationSerializable {

    private Enchantment enchantment;
    private int level;

    public EnchantmentEntity() {
    }

    public EnchantmentEntity(Enchantment enchantment, int level) {
        this.enchantment = enchantment;
        this.level = level;
    }

    public static EnchantmentEntity deserialize(Map<String, Object> args) {
        Object enchantment = args.get("enchantment");
        Object level = args.get("level");

        EnchantmentEntity enchantmentEntity = new EnchantmentEntity();
        if (enchantment != null) {
            enchantmentEntity.enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantment.toString().toLowerCase()));
        }
        if (level != null) {
            enchantmentEntity.level = (int) level;
        }

        return enchantmentEntity;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("enchantment", enchantment.getKey().getKey());
        map.put("level", level);
        return map;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    public void setEnchantment(Enchantment enchantment) {
        this.enchantment = enchantment;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "EnchantmentEntity{" +
                "enchantment=" + enchantment +
                ", level=" + level +
                '}';
    }
}
