package de.heliosdevelopment.heliosperms.spigot.listener;

import de.heliosdevelopment.heliosperms.HeliosPerms;
import de.heliosdevelopment.heliosperms.events.GroupChangeEvent;
import de.heliosdevelopment.heliosperms.manager.PlayerManager;
import de.heliosdevelopment.heliosperms.spigot.Main;
import de.heliosdevelopment.heliosperms.utils.Permissible;
import de.heliosdevelopment.heliosperms.utils.PermissionPlayer;
import de.heliosdevelopment.heliosperms.utils.PermissionType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
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
        new BukkitRunnable() {

            @Override
            public void run() {
                if (subchannel.equals("UpdatePermissions")) {
                    PermissionType type = PermissionType.valueOf(in.readUTF().toUpperCase());
                    if (type == PermissionType.GROUP)
                        HeliosPerms.getInstance().getGroupManager().updatePermissions();
                    else
                        HeliosPerms.getInstance().getPlayerManager().updatePermissions();

                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        PermissionPlayer permissionPlayer = playerManager.getPlayer(onlinePlayer.getUniqueId());

                        if (permissionPlayer == null) return;


                        try {
                            Field field = Main.getInstance().getNMSClass("entity.CraftHumanEntity").getDeclaredField("perm");
                            field.setAccessible(true);
                            field.set(onlinePlayer, new Permissible(onlinePlayer, permissionPlayer));
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.runTaskLater(Main.getInstance(), 100);
        if (subchannel.equals("UpdateGroup")) {
            String uuid = in.readUTF();
            Player p = Bukkit.getPlayer(UUID.fromString(uuid));
            String groupId = in.readUTF();
            if (p != null) {
                Bukkit.getPluginManager().callEvent(new GroupChangeEvent(player.getUniqueId(), Integer.valueOf(groupId)));
            }
        }


    }
}
