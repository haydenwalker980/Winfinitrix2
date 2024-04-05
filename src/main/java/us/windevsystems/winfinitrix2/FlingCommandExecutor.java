package us.windevsystems.winfinitrix2;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

class FlingCommandExecutor implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("fling")) {
            if (args.length < 1) {
                sender.sendMessage("I can't fling the air, silly! You need to specify a player!");
                return false;
            }
            Player target = Bukkit.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("That player is not online or does not exist!");
                return false;
            }
            Vector newVelocity = target.getVelocity();
            newVelocity.setY(5);
            target.setVelocity(newVelocity);
            return true;
        }
        return false;
    }
}
