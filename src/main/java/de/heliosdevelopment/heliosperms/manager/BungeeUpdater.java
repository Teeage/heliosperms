package de.heliosdevelopment.heliosperms.manager;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.heliosdevelopment.heliosperms.utils.PermissionType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeUpdater {

    public static void updateGroup(String uuid, Integer groupId) {
        List<ServerInfo> servers = new ArrayList<>();
        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            if (!servers.contains(p.getServer().getInfo()))
                servers.add(p.getServer().getInfo());
        }
        for (ServerInfo server : servers) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(stream);
            try {
                out.writeUTF("UpdateGroup");
                out.writeUTF(uuid);
                out.writeUTF(groupId.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            server.sendData("HeliosPerms", stream.toByteArray());
        }
    }

    public static void updatePermissions(PermissionType type) {
        List<ServerInfo> servers = new ArrayList<>();
        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            if (!servers.contains(p.getServer().getInfo()))
                servers.add(p.getServer().getInfo());
        }
        for (ServerInfo server : servers) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(stream);
            try {
                out.writeUTF("UpdatePermissions");
                out.writeUTF(type.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            server.sendData("HeliosPerms", stream.toByteArray());
        }
    }
}
