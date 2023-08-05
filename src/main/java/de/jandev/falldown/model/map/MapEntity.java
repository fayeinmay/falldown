package de.jandev.falldown.model.map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("MapEntity")
public class MapEntity implements ConfigurationSerializable {

    private String name;
    private MapLocationEntity drop;
    private MapLocationEntity endpoint;
    private MapLocationEntity specspawn;
    private MapLocationEntity crystal;
    private List<MapLocationEntity> spawn = new ArrayList<>();

    public MapEntity() {
    }

    public MapEntity(String name, MapLocationEntity drop, MapLocationEntity endpoint, MapLocationEntity specspawn, MapLocationEntity crystal, List<MapLocationEntity> spawn) {
        this.name = name;
        this.drop = drop;
        this.endpoint = endpoint;
        this.specspawn = specspawn;
        this.crystal = crystal;
        this.spawn = spawn;
    }

    public static MapEntity deserialize(Map<String, Object> args) {
        Object name = args.get("name");
        Object drop = args.get("drop");
        Object endpoint = args.get("endpoint");
        Object specspawn = args.get("specspawn");
        Object crystal = args.get("crystal");
        Object spawn = args.get("spawn");

        MapEntity mapEntity = new MapEntity();
        if (name != null) {
            mapEntity.name = name.toString();
        }
        if (drop != null) {
            mapEntity.drop = (MapLocationEntity) drop;
        }
        if (endpoint != null) {
            mapEntity.endpoint = (MapLocationEntity) endpoint;
        }
        if (specspawn != null) {
            mapEntity.specspawn = (MapLocationEntity) specspawn;
        }
        if (crystal != null) {
            mapEntity.crystal = (MapLocationEntity) crystal;
        }
        if (spawn != null) {
            mapEntity.spawn = (List<MapLocationEntity>) spawn;
        }

        return mapEntity;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("drop", drop);
        map.put("endpoint", endpoint);
        map.put("specspawn", specspawn);
        map.put("crystal", crystal);
        map.put("spawn", spawn);
        return map;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MapLocationEntity getDrop() {
        return drop;
    }

    public void setDrop(MapLocationEntity drop) {
        this.drop = drop;
    }

    public MapLocationEntity getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(MapLocationEntity endpoint) {
        this.endpoint = endpoint;
    }

    public MapLocationEntity getSpecspawn() {
        return specspawn;
    }

    public void setSpecspawn(MapLocationEntity specspawn) {
        this.specspawn = specspawn;
    }

    public MapLocationEntity getCrystal() {
        return crystal;
    }

    public void setCrystal(MapLocationEntity crystal) {
        this.crystal = crystal;
    }

    public List<MapLocationEntity> getSpawn() {
        return spawn;
    }

    public void setSpawn(List<MapLocationEntity> spawn) {
        this.spawn = spawn;
    }

    @Override
    public String toString() {
        return "MapEntity{" +
                "name='" + name + '\'' +
                ", drop=" + drop +
                ", endpoint=" + endpoint +
                ", specspawn=" + specspawn +
                ", crystal=" + crystal +
                ", spawn=" + spawn +
                '}';
    }
}
