package de.heliosdevelopment.heliosperms.events;


import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class GroupChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final UUID uuid;
    private final int oldGroupId, newGroupId;

    public GroupChangeEvent(UUID uuid, int oldGroupId, int newGroupId) {
        this.uuid = uuid;
        this.oldGroupId = oldGroupId;
        this.newGroupId = newGroupId;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public int getNewGroupId() {
        return newGroupId;
    }

    public int getOldGroupId() {
        return oldGroupId;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
