package de.jandev.falldown.model.item;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("ItemCombinationEntity")
public class ItemCombinationEntity implements ConfigurationSerializable {

    private List<ItemEntity> combination = new ArrayList<>();
    private SpecialItem combinesIntoItem;

    public ItemCombinationEntity() {
    }

    public ItemCombinationEntity(List<ItemEntity> combination, SpecialItem combinesIntoItem) {
        this.combination = combination;
        this.combinesIntoItem = combinesIntoItem;
    }

    public static ItemCombinationEntity deserialize(Map<String, Object> args) {
        Object combination = args.get("combination");
        Object combinesIntoItem = args.get("combinesIntoItem");

        ItemCombinationEntity itemCombinationEntity = new ItemCombinationEntity();
        if (combination != null) {
            itemCombinationEntity.combination = (List<ItemEntity>) combination;
        }
        if (combinesIntoItem != null) {
            itemCombinationEntity.combinesIntoItem = SpecialItem.valueOf(combinesIntoItem.toString().toUpperCase());
        }

        return itemCombinationEntity;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("combination", combination);
        map.put("combinesIntoItem", combinesIntoItem.toString());
        return map;
    }

    public List<ItemEntity> getCombination() {
        return combination;
    }

    public void setCombination(List<ItemEntity> combination) {
        this.combination = combination;
    }

    public SpecialItem getCombinesIntoItem() {
        return combinesIntoItem;
    }

    public void setCombinesIntoItem(SpecialItem combinesIntoItem) {
        this.combinesIntoItem = combinesIntoItem;
    }

    @Override
    public String toString() {
        return "ItemCombinationEntity{" +
                "combination=" + combination +
                ", combinesIntoItem=" + combinesIntoItem +
                '}';
    }
}
