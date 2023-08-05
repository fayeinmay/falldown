package de.jandev.falldown.task;

import de.jandev.falldown.Falldown;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class LobbyTask extends BukkitRunnable {

    private final Falldown plugin;
    private final int minplayers;
    private final int maxplayers;
    private int time = 120;

    public LobbyTask(Falldown plugin) {
        this.plugin = plugin;
        minplayers = plugin.getConfig().getInt("setting.minplayers");
        maxplayers = plugin.getConfig().getInt("setting.maxplayers");
    }

    @Override
    public void run() {
        if (time <= 0) {
            if (plugin.getServer().getOnlinePlayers().size() < minplayers) {
                plugin.broadcast(plugin.getConfigString("message.task.restartlobbynotenoughplayers")
                        .replace("%min%", Integer.toString(minplayers))
                        .replace("%current%", Integer.toString(plugin.getServer().getOnlinePlayers().size()))
                        .replace("%max%", Integer.toString(maxplayers)));

                plugin.getMapHelper().setVotingActive(true);
                plugin.startLobby();
                return;
            }
            plugin.startDropPhase();
        } else if (time == 105 || time == 75 || time == 45 || time == 15) {
            plugin.getMapHelper().broadcastMaps();
        } else if (time == 120 || time == 90 || time == 60 || time == 30 || time == 10 || time <= 3) {
            plugin.broadcast(plugin.getConfigString("message.task.gamestartsin")
                    .replace("%number%", Integer.toString(time)));

            if (time == 10) {
                plugin.getMapHelper().finishVoting();
            }
        }

        for (Player p : plugin.getServer().getOnlinePlayers()) {
            if (time <= 3) {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            }
            p.setLevel(time);
        }

        plugin.updateScoreboard(time);
        time--;
    }
}
