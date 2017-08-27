package de.heliosdevelopment.heliosperms.bungee;

import de.heliosdevelopment.heliosperms.HeliosPerms;
import de.heliosdevelopment.heliosperms.MySQL;
import de.heliosdevelopment.heliosperms.bungee.commands.PermissionCommand;
import de.heliosdevelopment.heliosperms.bungee.listener.BungeeListener;
import de.heliosdevelopment.heliosperms.listener.PermissionListener;
import de.heliosdevelopment.heliosperms.manager.ExpirationHandler;
import de.heliosdevelopment.heliosperms.manager.GroupManager;
import de.heliosdevelopment.heliosperms.manager.PlayerManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Main extends Plugin {
    private MySQL mysql;
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        File file = new File("../../../permissionconfig.yml");
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
        if (!cfg.contains("mysql.host"))
            cfg.set("mysql.host", "localhost");
        if (!cfg.contains("mysql.port"))
            cfg.set("mysql.port", "3306");
        if (!cfg.contains("mysql.database"))
            cfg.set("mysql.database", "database");
        if (!cfg.contains("mysql.user"))
            cfg.set("mysql.user", "user");
        if (!cfg.contains("mysql.password"))
            cfg.set("mysql.password", "password");
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(cfg, file);
        } catch (IOException e) {
            e.printStackTrace();
        }


        mysql = new MySQL(cfg.getString("mysql.host"), cfg.getString("mysql.port"), cfg.getString("mysql.database")
                , cfg.getString("mysql.user"), cfg.getString("mysql.password"));
        GroupManager groupManager = new GroupManager(mysql);
        PlayerManager playerManager = new PlayerManager(mysql, groupManager);
        new HeliosPerms(mysql, playerManager);
        ProxyServer.getInstance().getPluginManager().registerListener(this, new BungeeListener(playerManager));
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PermissionListener(playerManager));
        getProxy().getPluginManager().registerCommand(this, new PermissionCommand(mysql, playerManager));
        new ExpirationHandler(playerManager);
        getProxy().registerChannel("HeliosPerms");
        HeliosPerms.setBungee(true);
    }

    @Override
    public void onDisable() {
        mysql.close();
    }

    public static Main getInstance() {
        return instance;
    }

    public MySQL getMysql() {
        return mysql;
    }

}