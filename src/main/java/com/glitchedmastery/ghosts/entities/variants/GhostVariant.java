package com.wanmine.ghosts.entities.variants;

import java.util.Arrays;
import java.util.Comparator;

public enum GhostVariant {
    NORMAL_40(0),
    NORMAL_80(1),
    MUSHROOM_40(2),
    MUSHROOM_80(3);

    private static final GhostVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(GhostVariant::getId)).toArray(GhostVariant[]::new);
    private final int id;

    GhostVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static GhostVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
