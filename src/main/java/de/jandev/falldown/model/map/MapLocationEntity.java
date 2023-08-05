package de.jandev.falldown.model.map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("MapLocationEntity")
public class MapLocationEntity implements ConfigurationSerializable {

    private String world;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public MapLocationEntity() {
    }

    public MapLocationEntity(Location loc) {
        this.world = loc.getWorld().getName();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
    }

    public MapLocationEntity(String world, double x, double y, double z, float yaw, float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public static MapLocationEntity deserialize(Map<String, Object> args) {
        Object world = args.get("world");
        Object x = args.get("x");
        Object y = args.get("y");
        Object z = args.get("z");
        Object yaw = args.get("yaw");
        Object pitch = args.get("pitch");

        MapLocationEntity mapLocationEntity = new MapLocationEntity();
        if (world != null) {
            mapLocationEntity.world = world.toString();
        }
        if (x != null) {
            mapLocationEntity.x = Double.parseDouble(x.toString());
        }
        if (y != null) {
            mapLocationEntity.y = Double.parseDouble(y.toString());
        }
        if (z != null) {
            mapLocationEntity.z = Double.parseDouble(z.toString());
        }
        if (yaw != null) {
            mapLocationEntity.yaw = Float.parseFloat(yaw.toString());
        }
        if (pitch != null) {
            mapLocationEntity.pitch = Float.parseFloat(pitch.toString());
        }

        return mapLocationEntity;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("world", world);
        map.put("x", x);
        map.put("y", y);
        map.put("z", z);
        map.put("yaw", yaw);
        map.put("pitch", pitch);
        return map;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    @Override
    public String toString() {
        return "MapLocationEntity{" +
                "world='" + world + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                '}';
    }
}
