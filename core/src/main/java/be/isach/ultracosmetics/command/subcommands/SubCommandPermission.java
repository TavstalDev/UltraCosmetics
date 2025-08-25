package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SubCommandPermission extends SubCommand {

    public SubCommandPermission(UltraCosmetics ultraCosmetics) {
        super("permission", "Commands.Permission.Description", "Commands.Permission.Usage", ultraCosmetics);
    }

    @Override
    protected void onExeAnyone(CommandSender sender, String[] args) {
        if (args.length < 4) {
            badUsage(sender);
            return;
        }

        boolean unlock;
        if (args[1].equalsIgnoreCase("add")) {
            unlock = true;
        } else if (args[1].equalsIgnoreCase("remove")) {
            if (!ultraCosmetics.getPermissionManager().isUnsetSupported()) {
                // ChatColor.RED to remove bold
                MessageManager.send(sender, "Commands.Permission.Warning");
                return;
            }
            unlock = false;
        } else {
            MessageManager.send(sender, "Commands.Permission.Arg-Required");
            return;
        }

        Player target;
        if (args.length == 4) {
            if (sender instanceof Player) {
                target = (Player) sender;
            } else {
                MessageManager.send(sender, "Commands.Player-Required");
                return;
            }
        } else {
            target = Bukkit.getPlayer(args[4]);
            if (target == null) {
                var playerPlaceholder = Placeholder.unparsed("player", args[4]);
                MessageManager.send(sender, "Commands.Player-Not-Found", playerPlaceholder);
                return;
            }
        }
        Set<CosmeticType<?>> cosmetics = new HashSet<>();
        if (args[2].equals("*")) {
            if (!args[3].equals("*")) {
                MessageManager.send(sender, "Wildcard-Error");
                return;
            }
            Category.forEachCosmetic(cosmetics::add);
        } else {
            Category cat = Category.fromString(args[2]);
            if (cat == null) {
                MessageManager.send(sender, "Invalid-Category");
                return;
            }
            if (args[3].equals("*")) {
                cosmetics.addAll(cat.getValues());
            } else {
                CosmeticType<?> type = cat.valueOfType(args[3]);
                if (type == null) {
                    MessageManager.send(sender, "Invalid-Cosmetic");
                    return;
                }
                cosmetics.add(type);
            }
        }

        if (unlock) {
            ultraCosmetics.getPermissionManager().setPermissions(target, cosmetics);
        } else {
            ultraCosmetics.getPermissionManager().unsetPermissions(target, cosmetics);
        }
        MessageManager.send(sender, "Commands.Success");
    }

    @Override
    protected void tabComplete(CommandSender sender, String[] args, List<String> options) {
        if (args.length == 2) {
            options.add("add");
            options.add("remove");
        } else if (args.length == 3) {
            addCategories(options);
            options.add("*");
        } else if (args.length == 4) {
            Category cat = Category.fromString(args[2]);
            if (cat == null || !cat.isEnabled()) return;
            for (CosmeticType<?> type : cat.getEnabled()) {
                options.add(type.getConfigName());
            }
            options.add("*");
        } else if (args.length == 5) {
            addPlayers(options);
        }
    }

}
