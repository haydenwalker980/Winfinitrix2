package us.windevsystems.winfinitrix2;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class CtbCommandExecutor implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ctb")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Sorry, only players can use this command!");
                return true;
            }
            Player player = (Player)sender;
            player.setHealth(0.0D);
            player.sendMessage("You constricted your internal carotid arteries, and fucking died. Night-night.");
            return true;
        }
        return false;
    }
}
