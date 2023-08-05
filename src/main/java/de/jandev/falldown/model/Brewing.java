package de.jandev.falldown.model;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Brewing {

    private final Location location;
    private final List<ItemStack> items = new ArrayList<>();

    public Brewing(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return this.location;
    }

    public List<ItemStack> getItems() {
        return this.items;
    }
}
