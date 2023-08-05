package de.jandev.falldown.listener;

import de.jandev.falldown.Falldown;
import de.jandev.falldown.model.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PremiumJoinListener implements Listener {

    private final Falldown plugin;

    public PremiumJoinListener(Falldown plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        if (plugin.getState() == GameState.DROP || plugin.getState() == GameState.ENDING) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                    plugin.getConfigString("message.premiumjoin.notjoinabledroporending"));
        } else if (plugin.getState() == GameState.LOBBY) {
            int power = plugin.getPlayerRole(e.getPlayer()).getPower();
            if (plugin.getServer().getOnlinePlayers().size() >= plugin.getConfig().getInt("setting.maxplayers")) {
                if (power == 0) {
                    e.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                            plugin.getConfigString("message.premiumjoin.notjoinablefull"));
                } else {
                    int i = 0;
                    while (i < power) {
                        for (Player player : plugin.getServer().getOnlinePlayers()) {
                            int kickPlayerPower = plugin.getPlayerRole(player).getPower();
                            if (kickPlayerPower == i) {
                                if (kickPlayerPower == 0) {
                                    player.kickPlayer(plugin.getConfigString("message.premiumjoin.kickedbuypremium"));
                                    return;
                                }
                                player.kickPlayer(plugin.getConfigString("message.premiumjoin.kickedbyhigher"));
                                return;
                            }
                        }
                        i++;
                    }
                    e.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                            plugin.getConfigString("message.premiumjoin.notjoinablefullandnoonekickable"));
                }
            }
        }
    }
}
