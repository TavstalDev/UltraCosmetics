package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.treasurechests.TreasureRandomizer;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SubCommandReward extends SubCommand {

    public SubCommandReward(UltraCosmetics ultraCosmetics) {
        super("reward", "Commands.Reward.Description", "Commands.Reward.Usage", ultraCosmetics);
    }

    @Override
    protected void onExeAnyone(CommandSender sender, String[] args) {
        if (args.length < 3 && !(sender instanceof Player)) {
            MessageManager.send(sender, "Commands.Player-Required");
            return;
        }
        Player target;
        int n = 1;
        if (args.length > 1) {
            try {
                n = Integer.parseInt(args[1]);
                if (n < 1) n = 1;
            } catch (NumberFormatException e) {
                var valuePlaceholder = Placeholder.unparsed("value", args[1]);
                MessageManager.send(sender, "Commands.Invalid-Number", valuePlaceholder);
                return;
            }
        }
        if (args.length > 2) {
            target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                var playerPlaceholder = Placeholder.unparsed("player", args[2]);
                MessageManager.send(sender, "Commands.Player-Not-Found", playerPlaceholder);
                return;
            }
        } else {
            target = (Player) sender;
        }
        TreasureRandomizer tr = new TreasureRandomizer(target, target.getLocation().subtract(1, 0, 1), true);
        for (int i = 0; i < n; i++) {
            tr.giveRandomThing(null, false);
        }
    }

}
