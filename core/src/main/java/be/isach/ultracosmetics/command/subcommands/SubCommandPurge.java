package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.MessageManager;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

/**
 * Purge {@link SubCommand SubCommand}.
 *
 * @author RadBuilder
 * @since 11-14-2018
 */
public class SubCommandPurge extends SubCommand {

    public SubCommandPurge(UltraCosmetics ultraCosmetics) {
        super("purge", "Commands.Purge.Description", "Commands.Purge.Usage", ultraCosmetics);
    }

    @Override
    protected void onExeAnyone(CommandSender sender, String[] args) {
        if (args.length < 2 || !args[1].equalsIgnoreCase("confirm")) {
            MessageManager.send(sender, "Commands.Purge.Warning");
            return;
        }
        MessageManager.send(sender, "Commands.Purge.Start");
        ultraCosmetics.getScheduler().runAsync((task) -> {
            File dataFolder = new File(ultraCosmetics.getDataFolder(), "data");
            int deletedFiles = 0;
            int savedFiles = 0;
            if (!dataFolder.isDirectory()) {
                MessageManager.send(sender, "Commands.Purge.Error");
                return;
            }
            for (File file : dataFolder.listFiles()) {
                // this is 1 day in ms?
                if (file.lastModified() < System.currentTimeMillis() + 86400000) { // File old enough to check for config values set
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                    if (config.getInt("Keys") > 0 || config.contains("Pet-Names")) {
                        savedFiles++;
                    } else {
                        deletedFiles++;
                        file.delete();
                    }
                }
            }
            var deletedPlaceholder = Placeholder.unparsed("deletedfiles", String.valueOf(deletedFiles));
            var savedPlaceholder = Placeholder.unparsed("savedfiles", String.valueOf(savedFiles));
            MessageManager.send(sender, "Commands.Purge.Success", deletedPlaceholder, savedPlaceholder);
        });
    }
}
