package de.heliosdevelopment.heliosperms.bungee;

import de.heliosdevelopment.heliosperms.api.utils.PermissionType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BungeeUpdater {

    public static void updateGroup(String uuid, Integer oldGroupId, Integer newGroupId) {
        for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(stream);
            try {
                out.writeUTF("UpdateGroup");
                out.writeUTF(uuid);
                out.writeUTF(oldGroupId.toString());
                out.writeUTF(newGroupId.toString());
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
