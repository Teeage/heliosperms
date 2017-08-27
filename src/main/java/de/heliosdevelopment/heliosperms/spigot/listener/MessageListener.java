package de.heliosdevelopment.heliosperms.spigot.listener;

import de.heliosdevelopment.heliosperms.HeliosPerms;
import de.heliosdevelopment.heliosperms.events.GroupChangeEvent;
import de.heliosdevelopment.heliosperms.utils.PermissionType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import java.util.UUID;

public class MessageListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("HeliosPerms")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (subchannel.equals("UpdatePermissions")) {
            PermissionType type = PermissionType.valueOf(in.readUTF().toUpperCase());
            if (type == PermissionType.GROUP)
                HeliosPerms.getGroupManager().updateGroups();
            else
                HeliosPerms.getPlayerManager().updatePermissions();
        }
        if (subchannel.equals("UpdateGroup")) {
            String uuid = in.readUTF();
            Player p = Bukkit.getPlayer(UUID.fromString(uuid));
            String groupId = in.readUTF();
            if (p != null) {
                Bukkit.getPluginManager().callEvent(new GroupChangeEvent(player.getUniqueId(), Integer.valueOf(groupId)));
                HeliosPerms.getGroupManager().updatePermissions();
            }
        }
    }
}
