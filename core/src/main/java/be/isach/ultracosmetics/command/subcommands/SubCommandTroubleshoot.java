package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.util.Problem;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class SubCommandTroubleshoot extends SubCommand {

    public SubCommandTroubleshoot(UltraCosmetics ultraCosmetics) {
        super("troubleshoot", "Commands.Troubleshoot.Description", "Commands.Troubleshoot.Usage", ultraCosmetics);
    }

    @Override
    protected void onExeAnyone(CommandSender sender, String[] args) {
        Set<Problem> problems = ultraCosmetics.getProblems();
        if (problems.isEmpty()) {
            MessageManager.send(sender, "Commands.Troubleshoot.Fine");
        } else {
            var problemPlaceholder = Placeholder.unparsed("issues", String.valueOf(problems.size()));
            MessageManager.send(sender, "Commands.Troubleshoot.Minor", problemPlaceholder);
            if (sender instanceof Player) {
                MessageManager.send(sender, "Commands.Troubleshoot.Note");
            }
            Audience audience = MessageManager.getAudiences().sender(sender);
            problems.forEach(p -> audience.sendMessage(p.getSummary().color(NamedTextColor.YELLOW)));
        }
        sendSupportMessage(sender);
    }

    public static void sendSupportMessage(CommandSender sender) {
        String version = UltraCosmeticsData.get().getPlugin().getUpdateChecker().getCurrentVersion().versionClassifierCommit();
        var versionPlaceholder = Placeholder.unparsed("version", version);
        var serverPlaceholder = Placeholder.unparsed("server", Bukkit.getName() + " " + Bukkit.getVersion());
        MessageManager.send(sender, "Commands.Troubleshoot.Version", versionPlaceholder, serverPlaceholder);
        MessageManager.send(sender, "Commands.Troubleshoot.Support");
        MessageManager.send(sender, "Commands.Troubleshoot.Screenshot");
    }
}
