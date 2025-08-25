package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.CommandManager;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class SubCommandHelp extends SubCommand {
    private final CommandManager commandManager;

    public SubCommandHelp(UltraCosmetics ultraCosmetics, CommandManager commandManager) {
        super("help", "Commands.Help.Description", "Commands.Help.Usage", ultraCosmetics);
        this.commandManager = commandManager;
    }

    @Override
    protected void onExeAnyone(CommandSender sender, String[] args) {
        if (args.length > 1) {
            try {
                showHelp(sender, Math.min(Integer.parseInt(args[1]), getMaxPages(commandManager.getCommands().size())));
                return;
            } catch (NumberFormatException ignored) {
            }
        }
        showHelp(sender, 1);
    }

    public void showHelp(CommandSender sender, int page) {
        List<SubCommand> available = new ArrayList<>();
        commandManager.getCommands().stream().filter(c -> sender.hasPermission(c.getPermission())).forEach(available::add);
        if (available.isEmpty()) {
            CommandManager.sendNoPermissionMessage(sender);
            return;
        }
        sender.sendMessage("");
        TagResolver.Single pagePlaceholder = Placeholder.unparsed("currentpage", String.valueOf(page));
        TagResolver.Single maxPagePlaceholder = Placeholder.unparsed("totalpages", String.valueOf(getMaxPages(available.size())));
        MessageManager.send(sender, "Commands.Help.Title", pagePlaceholder, maxPagePlaceholder);
        int from = 8 * (page - 1);
        int to = 8 * page;
        for (int i = from; i < to; i++) {
            if (i >= available.size()) break;
            SubCommand sub = available.get(i);
            TagResolver.Single usagePlaceholder = Placeholder.component("usage", sub.getUsageComponent());
            TagResolver.Single descriptionPlaceholder = Placeholder.component("description", sub.getDescriptionComponent());
            MessageManager.send(sender, "Commands.Help.Command", usagePlaceholder, descriptionPlaceholder);
        }
    }

    /**
     * Gets the max amount of pages.
     *
     * @return the maximum amount of pages.
     */
    private int getMaxPages(int commands) {
        int max = 8;
        // test cases:
        // 8 commands: cmds - 1 = 7, 7 / 8 = 0, 0 + 1 = 1
        // 9 commands: cmds - 1 = 8, 8 / 8 = 1, 1 + 1 = 2
        // 0 commands: cmds - 1 = -1, -1 / 8 = 0, 0 + 1 = 1
        return ((commands - 1) / max) + 1;
    }
}
