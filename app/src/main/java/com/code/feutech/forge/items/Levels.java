package com.code.feutech.forge.items;

import java.util.ArrayList;

public class Levels {
    private ArrayList<Level> levels;

    public Levels() {
        this.levels = new ArrayList<>();
    }

    public boolean add(Level level) {
        return this.levels.add(level);
    }
}
