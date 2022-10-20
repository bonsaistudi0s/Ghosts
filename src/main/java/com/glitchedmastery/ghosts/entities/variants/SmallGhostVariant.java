package com.wanmine.ghosts.entities.variants;

import java.util.Arrays;
import java.util.Comparator;

public enum SmallGhostVariant {
    NORMAL_40(0),
    NORMAL_80(1),
    PLANT_40(2),
    PLANT_80(3);

    private static final SmallGhostVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(SmallGhostVariant::getId)).toArray(SmallGhostVariant[]::new);
    private final int id;

    SmallGhostVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static SmallGhostVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
