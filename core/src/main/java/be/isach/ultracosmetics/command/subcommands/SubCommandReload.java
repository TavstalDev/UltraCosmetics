package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.MessageManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SubCommandReload extends SubCommand {

    public SubCommandReload(UltraCosmetics ultraCosmetics) {
        super("reload", "Commands.Reload.Description", "Commands.Reload.Usage", ultraCosmetics);
    }

    @Override
    protected void onExeAnyone(CommandSender sender, String[] args) {
        MessageManager.send(sender, "Commands.Reload.Start");
        ultraCosmetics.reload();
        MessageManager.send(sender, "Commands.Reload.Finish");
    }

}
