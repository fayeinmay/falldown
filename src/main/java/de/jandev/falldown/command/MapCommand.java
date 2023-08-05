package de.jandev.falldown.command;

import de.jandev.falldown.Falldown;
import de.jandev.falldown.model.map.MapEntity;
import de.jandev.falldown.model.map.MapLocationEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class MapCommand {

    private final Falldown plugin;

    public MapCommand(Falldown plugin) {
        this.plugin = plugin;
    }

    public boolean handleCommand(Player p, String[] args) {
        if (args.length == 1) {
            p.sendMessage(plugin.getConfigString("message.map.help"));
            return true;
        } else if (args.length == 2) {
            if (args[1].equalsIgnoreCase("setlobby")) {
                setLobby(p);
                return true;
            } else if (args[1].equalsIgnoreCase("refreshmaps")) {
                refreshMaps(p);
                return true;
            }
        } else if (args.length == 3 && Falldown.isStringInteger(args[1])) {
            if (args[2].equalsIgnoreCase("setdrop")) {
                setDrop(p, args[1]);
                return true;
            } else if (args[2].equalsIgnoreCase("setendpoint")) {
                setEndpoint(p, args[1]);
                return true;
            } else if (args[2].equalsIgnoreCase("setspecspawn")) {
                setSpecSpawn(p, args[1]);
                return true;
            } else if (args[2].equalsIgnoreCase("listspawn")) {
                listSpawn(p, args[1]);
                return true;
            } else if (args[2].equalsIgnoreCase("setspawn")) {
                setSpawn(p, args[1]);
                return true;
            } else if (args[2].equalsIgnoreCase("setcrystal")) {
                setCrystal(p, args[1]);
                return true;
            } else if (args[2].equalsIgnoreCase("removemap")) {
                saveMap(p, args[1], null, "message.map.mapremoved");
                return true;
            }
        } else if (args.length == 4 && Falldown.isStringInteger(args[1])) {
            if (args[2].equalsIgnoreCase("setname")) {
                setName(p, args);
                return true;
            } else if (args[2].equalsIgnoreCase("removespawn")
                    && Falldown.isStringInteger(args[3])) {
                return removeSpawn(p, args);
            }
        }
        return false;
    }

    private void refreshMaps(Player p) {
        plugin.getMapHelper().updateAvailableMaps();
        p.sendMessage(plugin.getConfigString("message.map.refreshmaps"));
    }

    private boolean removeSpawn(Player p, String[] args) {
        MapEntity mapEntity = getMapOrNewMap(args[1]);
        if (mapEntity.getSpawn().size() >= Integer.parseInt(args[3])) {
            mapEntity.getSpawn().remove(Integer.parseInt(args[3]) - 1);
            saveMap(p, args[1], mapEntity, "message.map.spawnremoved");
            return true;
        }
        return false;
    }

    private void setName(Player p, String[] args) {
        MapEntity mapEntity = getMapOrNewMap(args[1]);
        mapEntity.setName(args[3]);
        saveMap(p, args[1], mapEntity, "message.map.nameset");
    }

    private void saveMap(Player p, String arg, MapEntity map, String message) {
        plugin.getMapConfiguration().set("map." + arg, map);
        plugin.saveMapConfiguration();
        p.sendMessage(plugin.getConfigString(message));
    }

    private void setCrystal(Player p, String arg) {
        MapEntity mapEntity = getMapOrNewMap(arg);
        mapEntity.setCrystal(new MapLocationEntity(p.getLocation()));
        saveMap(p, arg, mapEntity, "message.map.crystalset");
    }

    private void setSpawn(Player p, String arg) {
        MapEntity mapEntity = getMapOrNewMap(arg);
        mapEntity.getSpawn().add(new MapLocationEntity(p.getLocation()));
        saveMap(p, arg, mapEntity, "message.map.spawnset");
    }

    private void listSpawn(Player p, String arg) {
        MapEntity mapEntity = getMapOrNewMap(arg);
        List<MapLocationEntity> spawns = mapEntity.getSpawn();
        int counter = 1;
        for (MapLocationEntity spawn : spawns) {
            p.sendMessage(plugin.getConfigString("message.map.spawnlist")
                    .replace("%number%", String.valueOf(counter))
                    .replace("%x%", String.valueOf((int) spawn.getX()))
                    .replace("%y%", String.valueOf((int) spawn.getY()))
                    .replace("%z%", String.valueOf((int) spawn.getZ())));
            counter++;
        }
    }

    private void setSpecSpawn(Player p, String arg) {
        MapEntity mapEntity = getMapOrNewMap(arg);
        mapEntity.setSpecspawn(new MapLocationEntity(p.getLocation()));
        saveMap(p, arg, mapEntity, "message.map.specspawnset");
    }

    private void setEndpoint(Player p, String arg) {
        MapEntity mapEntity = getMapOrNewMap(arg);
        mapEntity.setEndpoint(new MapLocationEntity(p.getLocation()));
        saveMap(p, arg, mapEntity, "message.map.endpointset");
    }

    private void setDrop(Player p, String arg) {
        MapEntity mapEntity = getMapOrNewMap(arg);
        MapLocationEntity mapLocationEntity = new MapLocationEntity(p.getLocation());
        mapLocationEntity.setY(750); // 750: Player-Spawn, 500: Crystal-Spawn, 450: Reset
        mapEntity.setDrop(mapLocationEntity);
        saveMap(p, arg, mapEntity, "message.map.dropset");
    }

    private void setLobby(Player p) {
        plugin.getMapConfiguration().set("lobby.world", p.getLocation().getWorld().getName());
        plugin.getMapConfiguration().set("lobby.x", p.getLocation().getX());
        plugin.getMapConfiguration().set("lobby.y", p.getLocation().getY());
        plugin.getMapConfiguration().set("lobby.z", p.getLocation().getZ());
        plugin.saveMapConfiguration();
        p.sendMessage(plugin.getConfigString("message.map.lobbyset"));
    }

    private MapEntity getMapOrNewMap(String mapId) {
        Object map = plugin.getMapConfiguration().get("map." + mapId);
        if (map != null) {
            return (MapEntity) map;
        } else {
            return new MapEntity();
        }
    }
}
