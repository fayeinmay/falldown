package de.jandev.falldown.model.item;

import de.jandev.falldown.Falldown;
import de.jandev.falldown.model.item.enchantment.EnchantmentEntity;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("ItemEntity")
public class ItemEntity implements ConfigurationSerializable {

    private String name;
    private Material material;
    private int amount;
    private List<String> lore;
    private List<EnchantmentEntity> enchantments = new ArrayList<>();

    public ItemEntity() {
    }

    public ItemEntity(Material material, int amount) {
        this.material = material;
        this.amount = amount;
    }

    public ItemEntity(Material material, int amount, List<String> lore) {
        this.material = material;
        this.amount = amount;
        this.lore = lore;
    }

    public ItemEntity(String name, Material material, int amount, List<String> lore) {
        this.name = name;
        this.material = material;
        this.amount = amount;
        this.lore = lore;
    }

    public ItemEntity(String name, Material material, int amount, List<String> lore, List<EnchantmentEntity> enchantments) {
        this.name = name;
        this.material = material;
        this.amount = amount;
        this.lore = lore;
        this.enchantments = enchantments;
    }

    public static ItemEntity deserialize(Map<String, Object> args) {
        Object name = args.get("name");
        Object material = args.get("material");
        Object amount = args.get("amount");
        Object lore = args.get("lore");
        Object enchantments = args.get("enchantments");

        ItemEntity itemEntity = new ItemEntity();
        if (name != null) {
            itemEntity.name = name.toString();
        }
        if (material != null) {
            itemEntity.material = Material.valueOf(material.toString().toUpperCase());
        }
        if (amount != null) {
            itemEntity.amount = (int) amount;
        }
        if (lore != null) {
            itemEntity.lore = (List<String>) lore;
        }
        if (enchantments != null) {
            itemEntity.enchantments = (List<EnchantmentEntity>) enchantments;
        }

        return itemEntity;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        if (name != null) {
            map.put("name", name);
        }
        map.put("material", material.toString());
        map.put("amount", amount);
        if (lore != null) {
            map.put("lore", lore);
        }
        if (enchantments != null && !enchantments.isEmpty()) {
            map.put("enchantments", enchantments);
        }
        return map;
    }

    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemStack.setAmount(amount);

        if (name != null) {
            itemMeta.setDisplayName(Falldown.translateColor(name));
        }

        if (lore != null) {
            itemMeta.setLore(Falldown.translateColorList(lore));
        }

        // Note: Always set itemMeta before adding Enchantments, otherwise they will get overwritten
        itemStack.setItemMeta(itemMeta);

        if (enchantments != null) {
            for (EnchantmentEntity enchantmentEntity : enchantments) {
                itemStack.addEnchantment(enchantmentEntity.getEnchantment(), enchantmentEntity.getLevel());
            }
        }


        return itemStack;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public List<EnchantmentEntity> getEnchantments() {
        return enchantments;
    }

    public void setEnchantments(List<EnchantmentEntity> enchantments) {
        this.enchantments = enchantments;
    }

    @Override
    public String toString() {
        return "ItemEntity{" +
                "name='" + name + '\'' +
                ", material=" + material +
                ", amount=" + amount +
                ", lore=" + lore +
                ", enchantments=" + enchantments +
                '}';
    }
}
