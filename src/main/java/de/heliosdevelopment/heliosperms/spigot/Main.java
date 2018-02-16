package de.heliosdevelopment.heliosperms.spigot;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import de.heliosdevelopment.heliosperms.manager.GroupManager;
import de.heliosdevelopment.heliosperms.spigot.listener.PlayerListener;
import de.heliosdevelopment.heliosperms.manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

import de.heliosdevelopment.heliosperms.HeliosPerms;
import de.heliosdevelopment.heliosperms.MySQL;
import de.heliosdevelopment.heliosperms.spigot.listener.MessageListener;

public class Main extends JavaPlugin {
    private MySQL mysql;
    private static Main instance;
    public static final HashMap<UUID, PermissionAttachment> perms = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        org.bukkit.configuration.file.YamlConfiguration cfg = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(file);
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
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }


        mysql = new MySQL(cfg.getString("mysql.host"), cfg.getString("mysql.port"), cfg.getString("mysql.database")
                , cfg.getString("mysql.user"), cfg.getString("mysql.password"));

        GroupManager groupManager = new GroupManager(mysql);
        PlayerManager playerManager = new PlayerManager(mysql, groupManager);
        new HeliosPerms(mysql, playerManager);
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "HeliosPerms", new MessageListener());
        Bukkit.getPluginManager().registerEvents(new PlayerListener(playerManager), this);
        HeliosPerms.setBungee(false);
    }

    @Override
    public void onDisable() {
        mysql.close();
        for (Player p : Bukkit.getOnlinePlayers()) {
        }
    }

    public static Main getInstance() {
        return instance;
    }
}
