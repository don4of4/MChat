package com.miraclem4n.mchat;

import com.herocraftonline.heroes.Heroes;
import com.massivecraft.factions.Conf;
import com.miraclem4n.mchat.api.API;
import com.miraclem4n.mchat.api.Parser;
import com.miraclem4n.mchat.api.Writer;
import com.miraclem4n.mchat.commands.*;
import com.miraclem4n.mchat.configs.*;
import com.miraclem4n.mchat.events.*;
import com.miraclem4n.mchat.metrics.Metrics;
import com.miraclem4n.mchat.types.InfoType;
import com.miraclem4n.mchat.types.config.ConfigType;
import com.miraclem4n.mchat.util.MessageUtil;
import com.miraclem4n.mchat.util.TimerUtil;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class MChat extends JavaPlugin {
    // Default Plugin Data
    public PluginManager pm;
    public PluginDescriptionFile pdfFile;

    // Factions
    public Boolean factionsB = false;

    // Heroes
    public Heroes heroes;
    public Boolean heroesB = false;

    // Towny
    public Boolean townyB = false;

    // Debug
    Boolean debug = false;

    // Metrics
    public Metrics metrics;

    // Maps
    public static HashMap<String, Boolean> isShouting;
    public static HashMap<String, Boolean> isSpying;


    public void onEnable() {
        // Initialize and Start the Timer
        TimerUtil timer = new TimerUtil();

        // Initialize Plugin Data
        pm = getServer().getPluginManager();
        pdfFile = getDescription();

        // First we kill EssentialsChat
        killEss();

        // Setup Static Variables
        isShouting = new HashMap<String, Boolean>();
        isSpying = new HashMap<String, Boolean>();

        // Initialize Configs
        initializeConfigs();

        // Setup Plugins
        setupPlugins();

        // Initialize Classes
        initializeClasses();

        // Register Events
        registerEvents();

        // Setup Commands
        setupCommands();

        // Add All Players To Info.yml
        if (ConfigType.INFO_ADD_NEW_PLAYERS.getBoolean())
            for (Player players : getServer().getOnlinePlayers())
                if (InfoUtil.getConfig().get("users." + players.getName()) == null)
                    Writer.addBase(players.getName(), ConfigType.INFO_DEFAULT_GROUP.getString());

        // Stop the Timer
        timer.stop();

        // Calculate Startup Timer
        long diff = timer.difference();

        MessageUtil.log("[" + pdfFile.getName() + "] " + pdfFile.getName() + " v" + pdfFile.getVersion() + " is enabled! [" + diff + "ms]");
    }

    public void onDisable() {
        // Initialize and Start the Timer
        TimerUtil timer = new TimerUtil();

        getServer().getScheduler().cancelTasks(this);

        // Kill Static Variables
        isShouting = null;
        isSpying = null;

        // Kill Configs
        unloadConfigs();

        // Stop the Timer
        timer.stop();

        // Calculate Shutdown Timer
        long diff = timer.difference();

        MessageUtil.log("[" + pdfFile.getName() + "] " + pdfFile.getName() + " v" + pdfFile.getVersion() + " is disabled! [" + diff + "ms]");
    }

    void registerEvents() {
       if (!ConfigType.MCHAT_API_ONLY.getBoolean()) {
            pm.registerEvents(new PlayerListener(this), this);

            pm.registerEvents(new EntityListener(this), this);

            pm.registerEvents(new ChatListener(this), this);

            pm.registerEvents(new CommandListener(), this);
        }
    }

    Boolean setupPlugin(String pluginName) {
        Plugin plugin = pm.getPlugin(pluginName);

        if (plugin != null) {
            MessageUtil.log("[" + pdfFile.getName() + "] <Plugin> " + plugin.getDescription().getName() + " v" + plugin.getDescription().getVersion() + " hooked!.");
            return true;
        }

        return false;
    }

    void setupPlugins() {
        // Setup Factions
        factionsB = setupPlugin("Factions");

        if (factionsB)
            setupFactions();

        // Setup Heroes
        heroesB = setupPlugin("Heroes");

        if (heroesB)
            heroes = (Heroes) pm.getPlugin("Heroes");

        townyB = setupPlugin("Towny");
    }

    void setupFactions() {
        if (!(Conf.chatTagInsertIndex == 0))
            getServer().dispatchCommand(getServer().getConsoleSender(), "f config chatTagInsertIndex 0");
    }

    void killEss() {
        Plugin plugin = pm.getPlugin("EssentialsChat");

        if (plugin != null)
            pm.disablePlugin(plugin);
    }

    public void initializeConfigs() {
        ConfigUtil.initialize();
        InfoUtil.initialize();
        CensorUtil.initialize();
        LocaleUtil.initialize();
    }

    public void reloadConfigs() {
        ConfigUtil.initialize();
        InfoUtil.initialize();
        CensorUtil.initialize();
        LocaleUtil.initialize();
    }

    public void unloadConfigs() {
        ConfigUtil.dispose();
        InfoUtil.dispose();
        CensorUtil.dispose();
        LocaleUtil.dispose();
    }

    void setupCommands() {
        regCommands("mchat", new MChatCommand(this));

        regCommands("mchatuser", new InfoAlterCommand(this, "mchatuser", InfoType.USER));
        regCommands("mchatgroup", new InfoAlterCommand(this, "mchatgroup", InfoType.GROUP));

        regCommands("mchatme", new MeCommand(this));
    }

    void regCommands(String command, CommandExecutor executor) {
        if (getCommand(command) != null)
            getCommand(command).setExecutor(executor);
    }

    void initializeClasses() {
        API.initialize();
        Parser.initialize(this);
    }
}
