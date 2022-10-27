package com.wanmine.ghosts.entities.variants;

public enum GhostVariant {
    NORMAL(0),
    MUSHROOM(2);

    private static final GhostVariant[] BY_ID = new GhostVariant[]{NORMAL, NORMAL, MUSHROOM, MUSHROOM};
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
