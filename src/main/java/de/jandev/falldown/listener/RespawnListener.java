package de.jandev.falldown.listener;

import de.jandev.falldown.Falldown;
import de.jandev.falldown.model.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnListener implements Listener {

    private final Falldown plugin;

    public RespawnListener(Falldown plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        if (plugin.getState() != GameState.LOBBY) {
            Player p = e.getPlayer();

            p.setAllowFlight(true);
            p.setFlying(true);
            p.getInventory().clear();

            for (Player player : plugin.getServer().getOnlinePlayers()) {
                player.hidePlayer(plugin, p);
            }

            p.getInventory().addItem(plugin.getCompass());

            e.setRespawnLocation(plugin.getCurrentMap().getSpecspawn().getLocation());
        }
    }

}
