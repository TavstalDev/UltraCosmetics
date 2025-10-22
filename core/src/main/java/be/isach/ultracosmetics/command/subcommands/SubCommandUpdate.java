package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.util.UpdateManager;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

public class SubCommandUpdate extends SubCommand {

    public SubCommandUpdate(UltraCosmetics ultraCosmetics) {
        super("update", "Commands.Update.Description", "Commands.Update.Usage", ultraCosmetics);
    }

    @Override
    protected void onExeAnyone(CommandSender sender, String[] args) {
        UpdateManager updateManager = ultraCosmetics.getUpdateChecker();
        if (updateManager == null) {
            MessageManager.send(sender, "Commands.Update.Must-Be-Enabled");
            return;
        }

        var currentVersionPlaceholder = Placeholder.unparsed("version", updateManager.getCurrentVersion().versionClassifierCommit());
        MessageManager.send(sender, "Commands.Update.Current-Version", currentVersionPlaceholder);
        if (args.length > 1 && args[1].equalsIgnoreCase("force")) {
            MessageManager.send(sender, "Commands.Update.Force-Update");
        } else {
            var statusPlaceholder = Placeholder.unparsed("status", updateManager.getStatus());
            MessageManager.send(sender, "Commands.Update.Update-Status", statusPlaceholder);
            if (!updateManager.isOutdated()) {
                return;
            }
        }
        var versionPlaceholder = Placeholder.unparsed("version", String.valueOf(updateManager.getSpigotVersion()));
        MessageManager.send(sender, "Commands.Update.Update-Available", versionPlaceholder);
       MessageManager.send(sender, "Commands.Update.Requesting");

        ultraCosmetics.getScheduler().runAsync((task) -> {
            boolean success = updateManager.update();
            if (success) {
                MessageManager.send(sender, "Commands.Update.Success");
            } else {
                MessageManager.send(sender, "Commands.Update.Failure");
            }
        });
    }

}
