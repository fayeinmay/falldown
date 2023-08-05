package de.jandev.falldown.listener;

import de.jandev.falldown.Falldown;
import de.jandev.falldown.model.GameState;
import de.jandev.falldown.model.player.PlayerRole;
import de.jandev.falldown.model.player.PlayerType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final Falldown plugin;

    public ChatListener(Falldown plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        PlayerRole playerRole = plugin.getPlayerRole(p);
        if (playerRole == PlayerRole.DEVELOPER
                || playerRole == PlayerRole.OWNER
                || playerRole == PlayerRole.ADMINISTRATOR) {
            e.setMessage(Falldown.translateColor(e.getMessage()));
        }

        if (plugin.getPlayers().get(p) == PlayerType.SPECTATOR) {
            if (plugin.getState() != GameState.ENDING) {
                e.getRecipients().clear();
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (plugin.getPlayers().get(player) == PlayerType.SPECTATOR) {
                        e.getRecipients().add(player);
                    }
                }
            }

            e.setFormat(ChatColor.DARK_RED + "DEAD" +
                    ChatColor.GRAY + " | " +
                    ChatColor.DARK_GRAY + p.getDisplayName() +
                    ChatColor.GRAY + " > " +
                    ChatColor.WHITE + e.getMessage());
        } else {
            e.setFormat(ChatColor.DARK_GRAY + p.getDisplayName() +
                    ChatColor.GRAY + " > " +
                    ChatColor.WHITE + e.getMessage());
        }
    }

}
