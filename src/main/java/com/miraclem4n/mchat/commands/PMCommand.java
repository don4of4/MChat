package com.miraclem4n.mchat.commands;

import com.miraclem4n.mchat.api.Parser;
import com.miraclem4n.mchat.types.config.ConfigType;
import com.miraclem4n.mchat.types.config.LocaleType;
import com.miraclem4n.mchat.util.MessageUtil;
import com.miraclem4n.mchat.util.MiscUtil;
import in.mDev.MiracleM4n.mChatSuite.mChatSuite;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.player.SpoutPlayer;

public class PMCommand implements CommandExecutor {
    mChatSuite plugin;

    public PMCommand(mChatSuite instance) {
        plugin = instance;
    }

    String message = "";

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("pmchat")
                || !MiscUtil.hasCommandPerm(sender, "mchat.pm.pm"))
            return true;

        //TODO Allow Console's to PM
        if (!(sender instanceof Player)) {
            MessageUtil.sendMessage(sender, "Console's can't send PM's.");
            return true;
        }

        Player player = (Player) sender;
        String pName = player.getName();
        String world = player.getWorld().getName();

        if (args.length < 2)
            return false;

        message = "";
        for (int i = 1; i < args.length; ++i)
            message += " " + args[i];

        if (!MiscUtil.isOnlineForCommand(sender, args[0]))
            return true;

        Player recipient = plugin.getServer().getPlayer(args[0]);
        String rName = recipient.getName();
        String senderName = Parser.parsePlayerName(pName, world);

        player.sendMessage(LocaleType.FORMAT_PM_SENT.getValue().replace("%recipient%", Parser.parsePlayerName(rName, recipient.getWorld().getName())).replace("%message%", message));

        if (plugin.spoutB) {
            if (ConfigType.MCHAT_SPOUT.getObject().toBoolean()) {
                final SpoutPlayer sRecipient = (SpoutPlayer) recipient;

                if (sRecipient.isSpoutCraftEnabled()) {
                    plugin.lastPMd.put(rName, pName);


                    Runnable runnable = new Runnable() {
                        public void run() {
                            for (int i = 0; i < ((message.length() / 40) + 1); i++) {
                                sRecipient.sendNotification(formatPM(message, ((40 * i) + 1), ((i * 40) + 20)), formatPM(message, ((i * 40) + 21), ((i * 40) + 40)), Material.PAPER);
                                waiting(2);
                            }
                        }
                    };

                    sRecipient.sendNotification(LocaleType.MESSAGE_SPOUT_PM.getValue(), player.getName(), Material.PAPER);
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, runnable, 2 * 20);
                    return true;
                }
            }
        }

        plugin.lastPMd.put(rName, pName);
        recipient.sendMessage(LocaleType.FORMAT_PM_RECEIVED.getValue().replace("%sender%", Parser.parsePlayerName(senderName, world)).replace("%message%", message));
        MessageUtil.log(LocaleType.FORMAT_PM_RECEIVED.getValue().replace("%sender%", Parser.parsePlayerName(senderName, world)).replace("%message%", message));
        return true;
    }

    void waiting(int n) {
        long t0, t1;

        t0 = System.currentTimeMillis();

        do {
            t1 = System.currentTimeMillis();
        } while ((t1 - t0) < n * 1000);
    }

    String formatPM(String message, Integer start, Integer finish) {
        while (message.length() <= finish) message += " ";
        return message.substring(start, finish);
    }
}
