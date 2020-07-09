package de.heliosdevelopment.heliosperms.spigot.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import de.heliosdevelopment.heliosperms.api.HeliosPerms;
import de.heliosdevelopment.heliosperms.spigot.events.GroupChangeEvent;
import de.heliosdevelopment.heliosperms.api.manager.PlayerManager;
import de.heliosdevelopment.heliosperms.spigot.Main;
import de.heliosdevelopment.heliosperms.api.utils.Permissible;
import de.heliosdevelopment.heliosperms.api.utils.PermissionPlayer;
import de.heliosdevelopment.heliosperms.api.utils.PermissionType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

public class MessageListener implements PluginMessageListener {

    private final PlayerManager playerManager;

    public MessageListener(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("HeliosPerms")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if (subchannel.equals("UpdatePermissions")) {
                PermissionType type = PermissionType.valueOf(in.readUTF().toUpperCase());
                if (type == PermissionType.GROUP)
                    HeliosPerms.getInstance().getGroupManager().updatePermissions();
                else
                    HeliosPerms.getInstance().getPlayerManager().updatePermissions();

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    Optional<PermissionPlayer> permissionPlayer = playerManager.getPlayer(onlinePlayer.getUniqueId());

                    if (!permissionPlayer.isPresent()) return;

                    try {
                        Field field = Main.getInstance().getNMSClass("entity.CraftHumanEntity").getDeclaredField("perm");
                        field.setAccessible(true);
                        field.set(onlinePlayer, new Permissible(onlinePlayer, permissionPlayer.get()));
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            } else if (subchannel.equals("UpdateGroup")) {
                String uuid = in.readUTF();
                Player target = Bukkit.getPlayer(UUID.fromString(uuid));
                String oldGroupId = in.readUTF();
                String newGroupId = in.readUTF();
                if (target != null) {
                    Bukkit.getPluginManager().callEvent(new GroupChangeEvent(player.getUniqueId(), Integer.parseInt(oldGroupId), Integer.parseInt(newGroupId)));
                }
            }
        }, 100);
    }
}
