package de.jandev.falldown.command;

import de.jandev.falldown.Falldown;
import de.jandev.falldown.model.GameState;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class VoteCommand {

    private final Falldown plugin;

    public VoteCommand(Falldown plugin) {
        this.plugin = plugin;
    }

    public boolean handleCommand(Player p, String[] args) {
        if (args.length == 1) {
            p.sendMessage(plugin.getConfigString("message.vote.help"));
            return true;
        } else if (args.length >= 2
                && Falldown.isStringInteger(args[1])
                && Integer.parseInt(args[1]) <= plugin.getMapHelper().getMapSelection().size()
                && Integer.parseInt(args[1]) >= 1
                && plugin.getState() == GameState.LOBBY
                && plugin.getMapHelper().isVotingActive()) {
            addVote(p, args[1]);
            return true;
        }
        return true;
    }

    private void addVote(Player p, String arg) {
        int vote = Integer.parseInt(arg);
        if (plugin.getVoted().contains(p)) {
            p.sendMessage(plugin.getConfigString("message.vote.alreadyvoted"));
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
        } else {
            if (plugin.getPlayerRole(p).getPower() > 0) {
                plugin.getVotes().merge(vote, 3, Integer::sum);
            } else {
                plugin.getVotes().merge(vote, 1, Integer::sum);
            }
            plugin.getVoted().add(p);
            p.sendMessage(plugin.getConfigString("message.vote.voted"));
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);
        }
    }
}
