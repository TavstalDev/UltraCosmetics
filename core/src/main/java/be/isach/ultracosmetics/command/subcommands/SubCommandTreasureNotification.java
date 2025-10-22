package be.isach.ultracosmetics.command.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.player.UltraPlayer;

public class SubCommandTreasureNotification extends SubCommand {

    public SubCommandTreasureNotification(UltraCosmetics ultraCosmetics) {
        super("treasurenotification", "Commands.TreasureNotification.Description", "Commands.TreasureNotification.Usage", ultraCosmetics, true);
    }

    @Override
    protected void onExePlayer(Player sender, String[] args) {
        UltraPlayer player = ultraCosmetics.getPlayerManager().getUltraPlayer(sender);
        boolean newValue = !player.isTreasureNotifying();
        player.setTreasureNotifying(newValue);
        if (newValue) {
            player.sendMessage(MessageManager.getMessage("Enable-Treasure-Notification"));
        } else {
            player.sendMessage(MessageManager.getMessage("Disable-Treasure-Notification"));
        }
    }

    @Override
    protected void onExeAnyone(CommandSender sender, String[] args) {
        notAllowed(sender);
    }
}
