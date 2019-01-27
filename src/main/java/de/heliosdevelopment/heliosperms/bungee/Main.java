package de.heliosdevelopment.heliosperms.bungee;

import de.heliosdevelopment.heliosperms.HeliosPerms;
import de.heliosdevelopment.heliosperms.bungee.commands.PermissionCommand;
import de.heliosdevelopment.heliosperms.bungee.listener.BungeeListener;
import de.heliosdevelopment.heliosperms.database.DatabaseHandler;
import de.heliosdevelopment.heliosperms.listener.PermissionListener;
import de.heliosdevelopment.heliosperms.manager.ExpirationHandler;
import de.heliosdevelopment.heliosperms.manager.GroupManager;
import de.heliosdevelopment.heliosperms.manager.PlayerManager;
import de.heliosdevelopment.sqlconnector.SQLClient;
import de.heliosdevelopment.sqlconnector.SQLInfo;
import de.heliosdevelopment.sqlconnector.util.SQLConfig;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Main extends Plugin {
    private static Main instance;
    private String administrator;
    private DatabaseHandler databaseHandler;

    @Override
    public void onEnable() {
        instance = this;
        File folder = new File("plugins//HeliosPerms");
        File file = new File(folder, "config.yml");
        if (!folder.exists())
            folder.mkdirs();
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        Configuration cfg = null;
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        try {
            cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert cfg != null;
        if (!cfg.contains("settings.administrator"))
            cfg.set("settings.administrator", "HierDeinenMinecraftNamenEintragen");
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(cfg, file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        administrator = cfg.getString("settings.administrator");

        try {
            SQLConfig config = new SQLConfig("plugins//HeliosPerms//sql.json");
            SQLInfo sqlInfo = config.getSqlInfo();
            SQLClient client = new SQLClient(sqlInfo, "com.mysql.jdbc.Driver", "jdbc:mysql", 5);
            databaseHandler = new DatabaseHandler(client);
            if (!databaseHandler.bootstrap()) {
                client.doShutdown();
                System.out.println("[HeliosPerms] Could not connect to your mysql database.");
            }
            GroupManager groupManager = new GroupManager(databaseHandler);
            PlayerManager playerManager = new PlayerManager(databaseHandler, groupManager);
            new HeliosPerms(databaseHandler, playerManager, true);
            ProxyServer.getInstance().getPluginManager().registerListener(this, new BungeeListener(playerManager, databaseHandler));
            ProxyServer.getInstance().getPluginManager().registerListener(this, new PermissionListener(playerManager));
            getProxy().getPluginManager().registerCommand(this, new PermissionCommand(databaseHandler, playerManager));
            new ExpirationHandler(playerManager);
            getProxy().registerChannel("HeliosPerms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        databaseHandler.getSqlClient().doShutdown();
    }

    public static Main getInstance() {
        return instance;
    }

    public String getAdministrator() {
        return administrator;
    }

    public DatabaseHandler getDatabaseHandler() {
        return databaseHandler;
    }
}
