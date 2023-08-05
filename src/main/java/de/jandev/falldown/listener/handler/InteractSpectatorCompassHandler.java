package de.jandev.falldown.listener.handler;

import de.jandev.falldown.Falldown;
import org.bukkit.entity.Player;

public class InteractSpectatorCompassHandler {

    private final Falldown plugin;

    public InteractSpectatorCompassHandler(Falldown plugin) {
        this.plugin = plugin;
    }

    public void handle(Player p) {
        int index = 0;
        int pindex = 0;
        if (plugin.getSpectatorIndex().containsKey(p)) {
            pindex = plugin.getSpectatorIndex().get(p);
        }

        for (Player player : plugin.getActivePlayers()) {
            if (pindex != plugin.getActivePlayers().size() && index < pindex) {
                index++;
                continue;
            }
            p.teleport(player);
            plugin.getSpectatorIndex().put(p, ++index);
            return;
        }

        // Player was not teleported, so they reached the end of the active players, resetting.
        plugin.getSpectatorIndex().remove(p);
    }

}
