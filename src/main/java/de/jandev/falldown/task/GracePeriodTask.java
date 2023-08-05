package de.jandev.falldown.task;

import de.jandev.falldown.Falldown;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GracePeriodTask extends BukkitRunnable {

    private final Falldown plugin;
    private int time;

    public GracePeriodTask(Falldown plugin) {
        this.plugin = plugin;
        time = plugin.getConfig().getInt("setting.gracetime");
    }

    @Override
    public void run() {
        if (time <= 0) {
            plugin.broadcast(plugin.getConfigString("message.task.graceended"));
            plugin.startInGame();
        } else if (time == 30 || time == 10 || time <= 3) {
            if (time <= 3) {
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                }
            }

            plugin.broadcast(plugin.getConfigString("message.task.graceends")
                    .replace("%number%", Integer.toString(time)));
        }

        plugin.updateScoreboard(time);
        time--;
    }
}
