package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.menu.buttons.RenamePetButton;
import be.isach.ultracosmetics.player.UltraPlayer;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.StringJoiner;

public class SubCommandRename extends SubCommand {

    public SubCommandRename(UltraCosmetics ultraCosmetics) {
        super("rename", "Commands.Rename.Description", "Commands.Rename.Usage", ultraCosmetics);
    }

    @Override
    protected void onExeAnyone(CommandSender sender, String[] args) {
        notAllowed(sender);
    }

    @Override
    protected void onExePlayer(Player player, String[] args) {
        if (!SettingsManager.getConfig().getBoolean("Pets-Rename.Enabled")) {
            MessageManager.send(player, "Commands.Rename.Disabled");
            return;
        }
        UltraPlayer up = ultraCosmetics.getPlayerManager().getUltraPlayer(player);
        if (up.getCurrentPet() == null) {
            MessageManager.send(player, "Commands.Rename.No-Pet");
            return;
        }
        String newName;
        if (args.length < 2) {
            newName = "";
        } else {
            StringJoiner sj = new StringJoiner(" ");
            for (int i = 1; i < args.length; i++) {
                sj.add(args[i]);
            }
            newName = sj.toString();
        }
        String stripped = MessageManager.getMiniMessage().stripTags(newName);
        int maxLength = SettingsManager.getConfig().getInt("Max-Pet-Name-Length", -1);
        if (maxLength != -1 && stripped.length() > maxLength) {
            var maxPlaceholder = Placeholder.unparsed("max", String.valueOf(maxLength));
            MessageManager.send(player, "Commands.Rename.Too-Long", maxPlaceholder);
            return;
        }

        if (!newName.isEmpty() && ultraCosmetics.getEconomyHandler().isUsingEconomy()
                && SettingsManager.getConfig().getBoolean("Pets-Rename.Requires-Money.Enabled")) {
            player.openInventory(RenamePetButton.buyRenamePet(up, newName, null));
        } else {
            up.setPetName(up.getCurrentPet().getType(), newName);
            MessageManager.send(player, "Commands.Success");
        }
    }

}
