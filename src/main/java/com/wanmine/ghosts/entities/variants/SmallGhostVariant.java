package com.wanmine.ghosts.entities.variants;

public enum SmallGhostVariant {
    NORMAL(0),
    PLANT(2);

    private static final SmallGhostVariant[] BY_ID = new SmallGhostVariant[]{NORMAL, NORMAL, PLANT, PLANT};
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
