package de.heliosdevelopment.heliosperms.events;


import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class GroupChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final UUID uuid;
    private final int groupId;

    public GroupChangeEvent(UUID uuid, int groupId) {
        this.uuid = uuid;
        this.groupId = groupId;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public int getGroupId() {
        return groupId;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
