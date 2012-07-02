package com.miraclem4n.mchat.configs;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class LocaleUtil {
    static YamlConfiguration config;
    static File file;

    public static void initialize() {
        load();
    }

    public static void load() {
        file = new File("plugins/mChatSuite/locale.yml");

        config = YamlConfiguration.loadConfiguration(file);

        config.options().indent(4);
        config.options().header("mChatSuite Locale");

        loadDefaults();
    }

    private static void loadDefaults() {
        checkOption("format.forward", "[F]");
        checkOption("format.local", "[L]");
        checkOption("format.pm.received", "%sender% &1-&2-&3-&4> &fMe: %message%");
        checkOption("format.pm.sent", "&fMe &1-&2-&3-&4> &4%recipient%&f: %message%");
        checkOption("format.say", "&6[Server]&e");
        checkOption("format.shout", "[Shout]");
        checkOption("format.spy", "[Spy]");

        checkOption("message.afk.afk", "AFK");
        checkOption("message.afk.default", "Away From Keyboard");
        checkOption("message.config.reloaded", "%config% Reloaded.");
        checkOption("message.config.updated", "%config% has been updated.");
        checkOption("message.convo.accepted", "Convo request with &5'&4%player%&5'&4 has been accepted.");
        checkOption("message.convo.convo", "&4[Convo] ");
        checkOption("message.convo.denied", "You have denied a Convo request from &5'&4%player%&5'&4.");
        checkOption("message.convo.ended", "Conversation with '%player%' has ended.");
        checkOption("message.convo.hasRequest", "&5'&4%player%&5'&4 Already has a Convo request.");
        checkOption("message.convo.inviteSent", "You have invited &5'&4%player%&5'&4 to have a Convo.");
        checkOption("message.convo.invited", "You have been invited to a Convo by &5'&4%player%&5'&4 use /pmchataccept to accept.");
        checkOption("message.convo.left", "You have left the Conversation with '%player%'.");
        checkOption("message.convo.noPending", "No pending Convo request.");
        checkOption("message.convo.notIn", "You are not currently in a Convo.");
        checkOption("message.convo.notStarted", "Convo request with &5'&4%player%&5'&4 has been denied.");
        checkOption("message.convo.started", "You have started a Convo with &5'&4%player%&5'&4.");
        checkOption("message.general.mute", "Target '%player%' successfully %muted%. To %mute% use this command again.");
        checkOption("message.general.noPerms", "You do not have '%permission%'.");
        checkOption("message.info.alteration", "Info Alteration Successful.");
        checkOption("message.list.header", "&6-- There are &8%players%&6 out of the maximum of &8%max%&6 Players online. --");
        checkOption("message.player.afk", "%player% is now AFK. [%reason%]");
        checkOption("message.player.notAfk", "%player% is no longer AFK.");
        checkOption("message.player.notFound", "");
        checkOption("message.player.notOnline", "");
        checkOption("message.player.stillAfk", "You are still AFK.");
        checkOption("message.pm.noPm", "No one has yet PM'd you.");
        checkOption("message.shout.noInput", "You can't shout nothing!");
        checkOption("message.spout.colour", "dark_red");
        checkOption("message.spout.pmFrom", "[PMChat] From:");
        checkOption("message.spout.typing", "*Typing*");
    }

    public static void set(String key, Object obj) {
        config.set(key, obj);

        save();
    }

    public static Boolean save() {
        try {
            config.save(file);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static YamlConfiguration getConfig() {
        return config;
    }

    private static void checkOption(String option, Object defValue) {
        if (!config.isSet(option))
            set(option, defValue);
    }
}
