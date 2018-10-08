package de.heliosdevelopment.heliosperms.manager;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.heliosdevelopment.heliosperms.utils.PermissionType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class BungeeUpdater {

    public static void updateGroup(String uuid, Integer groupId) {
        for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
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
        for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
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
