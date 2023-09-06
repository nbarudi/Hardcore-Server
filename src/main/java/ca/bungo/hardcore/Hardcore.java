package ca.bungo.hardcore;

import ca.bungo.hardcore.commands.AnnounceCommand;
import ca.bungo.hardcore.commands.InfoCommand;
import ca.bungo.hardcore.commands.TestingCommand;
import ca.bungo.hardcore.events.PlayerConnectionEvents;
import ca.bungo.hardcore.events.PlayerLevelEvents;
import ca.bungo.hardcore.events.TickerHandlerEvents;
import ca.bungo.hardcore.managers.*;
import ca.bungo.hardcore.modules.utility.XRayUtility;
import ca.bungo.hardcore.types.HardcorePlayer;
import ca.bungo.hardcore.utility.TeleportUtility;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.logging.Logger;


public final class Hardcore extends JavaPlugin {

    static {
        ConfigurationSerialization.registerClass(HardcorePlayer.class, "HardcorePlayer");
    }

    public static Logger LOGGER;
    public static Hardcore instance;

    public FileManager fileManager;
    public PlayerManager playerManager;
    public CustomItemManager customItemManager;
    public ModuleManager moduleManager;
    public FeatureManager featureManager;
    public BossManager bossManager;

    public TeleportUtility teleportUtility;

    public ScoreboardManager scoreboardManager;
    public Scoreboard mainScoreboard;

    @Override
    public void onEnable() {
        LOGGER = this.getLogger();
        instance = this;

        fileManager = new FileManager(this);
        loadConfigs();

        customItemManager = new CustomItemManager();
        moduleManager = new ModuleManager();
        playerManager = new PlayerManager();
        featureManager = new FeatureManager();
        bossManager = new BossManager();

        teleportUtility = new TeleportUtility();

        this.scoreboardManager = Bukkit.getScoreboardManager();
        this.mainScoreboard = this.scoreboardManager.getMainScoreboard();

        registerEvents();
        registerCommands();
        registerPlayers();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        playerManager.unload();
        moduleManager.saveCustomBlocks();
    }


    private void registerCommands(){
        this.getServer().getCommandMap().register("hardcore", new TestingCommand("test"));
        this.getServer().getCommandMap().register("hardcore", new InfoCommand("info"));
        this.getServer().getCommandMap().register("hardcore", new AnnounceCommand("announce"));
    }

    private void registerEvents(){
        this.getServer().getPluginManager().registerEvents(new PlayerConnectionEvents(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerLevelEvents(), this);
        this.getServer().getPluginManager().registerEvents(new XRayUtility(), this);
        this.getServer().getPluginManager().registerEvents(teleportUtility, this);
        this.getServer().getPluginManager().registerEvents(new TickerHandlerEvents(), this);
    }

    private void registerPlayers(){
        for(Player player : Bukkit.getOnlinePlayers()){
            player.registerHardcorePlayer();
        }
    }

    private void loadConfigs(){
        fileManager.saveConfig("homes.yml").save();
    }


}
