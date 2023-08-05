package de.jandev.falldown.command;

import de.jandev.falldown.Falldown;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class CommandHandler implements CommandExecutor {

    private final Falldown plugin;
    private final MapCommand mapCommand;
    private final VoteCommand voteCommand;

    public CommandHandler(Falldown plugin) {
        this.plugin = plugin;
        this.mapCommand = new MapCommand(plugin);
        this.voteCommand = new VoteCommand(plugin);
        Objects.requireNonNull(plugin.getCommand("falldown")).setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("falldown")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getConfigString("message.general.console"));
                return true;
            }

            Player p = (Player) sender;

            if (args.length == 0) {
                if (plugin.getPlayerRole(p).getPower() >= 10) {
                    p.sendMessage(plugin.getConfigString("message.general.help"));
                    return true;
                }
            } else {
                if (args[0].equalsIgnoreCase("map")
                        && plugin.getPlayerRole(p).getPower() >= 20) {
                    return mapCommand.handleCommand(p, args);
                } else if (args[0].equalsIgnoreCase("vote")) {
                    return voteCommand.handleCommand(p, args);
                } else if (args[0].equalsIgnoreCase("forcestart")
                        && plugin.getPlayerRole(p).getPower() >= 10) {
                    plugin.startDropPhase();
                    p.sendMessage(plugin.getConfigString("message.general.forcestart"));
                    return true;
                } else if (args[0].equalsIgnoreCase("premium")) {
                    p.sendMessage(plugin.getConfigString("message.general.premium"));
                    return true;
                } else if (args[0].equalsIgnoreCase("disable")
                        && plugin.getPlayerRole(p).getPower() >= 20) {
                    p.sendMessage(plugin.getConfigString("message.general.disable"));
                    plugin.getServer().getPluginManager().disablePlugin(plugin);
                    return true;
                }
            }
        }
        return false;
    }
}
