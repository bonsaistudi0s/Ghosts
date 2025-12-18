package dev.xylonity.bonsai.ghosts.util;

import dev.xylonity.bonsai.ghosts.common.entity.AbstractGhostEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class GhostOwnerTracker {

    private static final Map<UUID, Set<UUID>> owner_to_ghost = new ConcurrentHashMap<>();

    private static final GhostOwnerTracker instance = new GhostOwnerTracker();

    private GhostOwnerTracker() { ;; }

    public static GhostOwnerTracker getInstance() {
        return instance;
    }

    public void addGhost(AbstractGhostEntity ghost) {
        UUID ownerUUID = ghost.getOwnerUUID();
        if (ownerUUID != null) {
            owner_to_ghost.computeIfAbsent(ownerUUID, k -> ConcurrentHashMap.newKeySet()).add(ghost.getUUID());
        }

    }

    public void removeGhost(AbstractGhostEntity ghost) {
        UUID ownerUUID = ghost.getOwnerUUID();
        if (ownerUUID != null) {
            owner_to_ghost.remove(ownerUUID);
        }

    }

    @Nullable
    public AbstractGhostEntity findGhostWithTotem(ServerLevel level, UUID playerUUID) {
        Set<UUID> ghostUUIDs = owner_to_ghost.get(playerUUID);
        if (ghostUUIDs == null || ghostUUIDs.isEmpty()) {
            return null;
        }

        for (ServerLevel serverLevel : level.getServer().getAllLevels()) {
            for (UUID uuid : ghostUUIDs) {
                Entity entity = serverLevel.getEntity(uuid);
                if (entity instanceof AbstractGhostEntity ghost && ghost.getMainHandItem().is(Items.TOTEM_OF_UNDYING)) {
                    return ghost;
                }

            }

        }

        return null;
    }

}
