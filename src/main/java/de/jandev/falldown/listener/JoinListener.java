package de.jandev.falldown.listener;

import de.jandev.falldown.Falldown;
import de.jandev.falldown.model.GameState;
import de.jandev.falldown.model.player.PlayerType;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

public class JoinListener implements Listener {

    private final Falldown plugin;

    public JoinListener(Falldown plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.setExp(0);
        p.setLevel(0);
        p.getInventory().clear();
        p.setHealth(20.0);
        p.setFoodLevel(20);
        p.setFireTicks(0);
        p.setFlying(false);
        p.setGameMode(GameMode.ADVENTURE);
        p.setDisplayName(plugin.getPlayerColor(p) + p.getName() + ChatColor.WHITE);

        plugin.setScoreboard(p);

        if (plugin.getState() == GameState.LOBBY) {
            e.setJoinMessage(plugin.getConfigString("message.event.joinmessage")
                    .replace("%player%", p.getDisplayName()));
            p.sendMessage(plugin.getConfigString("message.event.joinmessagetoplayer"));

            plugin.getPlayers().put(p, PlayerType.ACTIVE);

            if (plugin.getMapConfiguration().get("lobby") != null) {
                Location loc = new Location(plugin.getServer().getWorld(Objects.requireNonNull(
                        plugin.getMapConfiguration().getString("lobby.world"))),
                        plugin.getMapConfiguration().getDouble("lobby.x"),
                        plugin.getMapConfiguration().getDouble("lobby.y"),
                        plugin.getMapConfiguration().getDouble("lobby.z"));
                p.teleport(loc);
            }

            p.getInventory().setItem(0, plugin.getVote());
            p.getInventory().setItem(2, plugin.getInfos());
        } else {
            e.setJoinMessage("");

            plugin.getPlayers().put(p, PlayerType.SPECTATOR);

            p.setAllowFlight(true);
            p.setFlying(true);

            for (Player player : plugin.getServer().getOnlinePlayers()) {
                player.hidePlayer(plugin, p);
            }

            p.teleport(plugin.getCurrentMap().getSpecspawn().getLocation());
            p.getInventory().addItem(plugin.getCompass());
        }
    }

}
