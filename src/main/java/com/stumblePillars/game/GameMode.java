package com.stumblePillars.game;

public interface GameMode {
    default void onCountdown(Game game) {}
    default void onCountdownCancelled(Game game) {}
    default void onCountdownFinished(Game game) {}
    default void onJoin(Game game, org.bukkit.entity.Player player) {}
    default void onLeave(Game game, org.bukkit.entity.Player player) {}
    default void onStart(Game game) {}
    default void onStop(Game game) {}
}
