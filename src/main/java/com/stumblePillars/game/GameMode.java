package com.stumblePillars.game;

public interface GameMode {
    default void onStart(Game game) {}
    default void onStop(Game game) {}
}
