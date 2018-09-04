package de.heliosdevelopment.heliosperms.spigot;

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


        if (!getConfig().contains("mysql.host"))
            getConfig().set("mysql.host", "localhost");
        if (!getConfig().contains("mysql.port"))
            getConfig().set("mysql.port", "3306");
        if (!getConfig().contains("mysql.database"))
            getConfig().set("mysql.database", "database");
        if (!getConfig().contains("mysql.user"))
            getConfig().set("mysql.user", "user");
        if (!getConfig().contains("mysql.password"))
            getConfig().set("mysql.password", "password");
        saveConfig();


        mysql = new MySQL(getConfig().getString("mysql.host"), getConfig().getString("mysql.port"), getConfig().getString("mysql.database")
                , getConfig().getString("mysql.user"), getConfig().getString("mysql.password"));

        GroupManager groupManager = new GroupManager(mysql);
        PlayerManager playerManager = new PlayerManager(mysql, groupManager);
        new HeliosPerms(mysql, playerManager, false);
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "HeliosPerms", new MessageListener(playerManager));
        Bukkit.getPluginManager().registerEvents(new PlayerListener(playerManager, getConfig().getBoolean("settings.tablist.active"), getConfig().getBoolean("settings.chat.active"), getConfig().getString("settings.tablist.format"), getConfig().getString("settings.chat.format")), this);

        for (Player player : Bukkit.getOnlinePlayers()) {
            playerManager.loadPlayer(player.getUniqueId(), player.getName(), false);
        }
    }

    @Override
    public void onDisable() {
        mysql.close();
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
