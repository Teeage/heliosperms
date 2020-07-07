package de.heliosdevelopment.heliosperms;

import de.heliosdevelopment.heliosperms.database.DatabaseHandler;
import de.heliosdevelopment.heliosperms.manager.GroupManager;
import de.heliosdevelopment.heliosperms.manager.PlayerManager;
import de.heliosdevelopment.heliosperms.listener.MessageListener;
import de.heliosdevelopment.heliosperms.listener.PlayerListener;
import de.heliosdevelopment.sqlconnector.SQLClient;
import de.heliosdevelopment.sqlconnector.SQLInfo;
import de.heliosdevelopment.sqlconnector.util.SQLConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private DatabaseHandler databaseHandler;
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;

        if (!getConfig().contains("settings.tablist.active"))
            getConfig().set("settings.tablist.active", true);
        if (!getConfig().contains("settings.chat.active"))
            getConfig().set("settings.chat.active", true);
        if (!getConfig().contains("settings.tablist.format"))
            getConfig().set("settings.tablist.format", "%colorCode%%prefix% &7× %player%");
        if (!getConfig().contains("settings.chat.format"))
            getConfig().set("settings.chat.format", "%colorCode%%name% &8┃ &7%player% &e»&f");
        saveConfig();

        try {
            SQLConfig config = new SQLConfig(getDataFolder() + "/sql.json");
            SQLInfo sqlInfo = config.getSqlInfo();
            SQLClient client = new SQLClient(sqlInfo, "com.mysql.jdbc.Driver", "jdbc:mysql", 5);
            databaseHandler = new DatabaseHandler(client);
            if (!databaseHandler.bootstrap()) {
                client.doShutdown();
                System.out.println("[HeliosPerms] Could not connect to your mysql database.");
            }

            GroupManager groupManager = new GroupManager(databaseHandler);
            PlayerManager playerManager = new PlayerManager(databaseHandler, groupManager);
            new HeliosPerms(databaseHandler, playerManager, false);
            Bukkit.getMessenger().registerIncomingPluginChannel(this, "HeliosPerms", new MessageListener(playerManager));
            Bukkit.getPluginManager().registerEvents(new PlayerListener(playerManager, getConfig().getBoolean("settings.tablist.active"), getConfig().getBoolean("settings.chat.active"), getConfig().getString("settings.tablist.format"), getConfig().getString("settings.chat.format")), this);

            for (Player player : Bukkit.getOnlinePlayers()) {
                playerManager.loadPlayer(player.getUniqueId(), player.getName(), false);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        databaseHandler.getSqlClient().doShutdown();
    }

    public static Main getInstance() {
        return instance;
    }

    public Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
