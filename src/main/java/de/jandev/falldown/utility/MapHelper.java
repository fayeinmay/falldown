package de.jandev.falldown.utility;

import de.jandev.falldown.Falldown;
import de.jandev.falldown.model.map.MapEntity;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MapHelper {

    private final Falldown plugin;
    private final List<MapEntity> availableMaps = new ArrayList<>();
    private final List<MapEntity> mapSelection = new ArrayList<>();
    private boolean votingActive = true;

    public MapHelper(Falldown plugin) {
        this.plugin = plugin;
        this.updateAvailableMaps();
    }

    public MapEntity evaluateWinner() {
        if (plugin.getVotes().entrySet().isEmpty()) {
            return mapSelection.get(0);
        } else {
            Integer winner = Collections.max(plugin.getVotes().entrySet(), Map.Entry.comparingByValue()).getKey();
            return mapSelection.get(winner - 1);
        }
    }

    public void broadcastMaps() {
        plugin.broadcast(plugin.getConfigString("message.vote.info"));

        int counter = 1;
        for (MapEntity map : mapSelection) {
            int finalCounter = counter;
            plugin.broadcast(plugin.getConfigString("message.vote.maps")
                    .replace("%number%", String.valueOf(counter))
                    .replace("%mapname%", map.getName())
                    .replace("%votes%", String.valueOf(plugin.getVotes()
                            .entrySet()
                            .stream()
                            .filter(f -> f.getKey() == finalCounter)
                            .findFirst()
                            .map(Map.Entry::getValue).orElse(0))));
            counter++;
        }
    }

    public void updateAvailableMaps() {
        availableMaps.clear();

        ConfigurationSection rootConfigurationSection = plugin.getMapConfiguration().getConfigurationSection("map");
        Set<String> mapSectionIds = Objects.requireNonNull(rootConfigurationSection).getKeys(false);

        for (String sectionId : mapSectionIds) {
            availableMaps.add((MapEntity) rootConfigurationSection.get(sectionId));
        }

        fillMapSelection(availableMaps);
    }

    public List<MapEntity> getAvailableMaps() {
        return this.availableMaps;
    }

    public List<MapEntity> getMapSelection() {
        return this.mapSelection;
    }

    public boolean isVotingActive() {
        return this.votingActive;
    }

    public void setVotingActive(boolean votingActive) {
        this.votingActive = votingActive;
    }

    public void finishVoting() {
        this.votingActive = false;
        plugin.broadcast(plugin.getConfigString("message.vote.won").replace("%mapname%", evaluateWinner().getName()));
    }

    private void fillMapSelection(List<MapEntity> availableMapsOriginal) {
        List<MapEntity> maps = new ArrayList<>(availableMapsOriginal);
        while (mapSelection.size() < 3 && !maps.isEmpty()) {
            int selector = ThreadLocalRandom.current().nextInt(maps.size());
            mapSelection.add(maps.get(selector));
            maps.remove(selector);
        }
    }
}
