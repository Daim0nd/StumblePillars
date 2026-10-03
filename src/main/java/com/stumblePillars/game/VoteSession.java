package com.stumblePillars.game;

import java.util.*;

public final class VoteSession<T> {
    private List<T> options = List.of();
    private final Map<UUID, T> votes = new HashMap<>();
    private boolean active;

    public void start(List<T> available, Random random) {
        clear();
        List<T> shuffled = new ArrayList<>(new LinkedHashSet<>(available));
        if (shuffled.size() < 3) throw new IllegalArgumentException("At least three distinct styles required");
        Collections.shuffle(shuffled, random);
        options = List.copyOf(shuffled.subList(0, 3));
        active = true;
    }
    public boolean cast(UUID player, T option, Collection<UUID> participants) {
        if (!active || !participants.contains(player) || !options.contains(option)) return false;
        votes.put(player, option);
        return true;
    }
    public void remove(UUID player) { votes.remove(player); }
    public T selected(UUID player) { return votes.get(player); }
    public List<T> options() { return options; }
    public boolean active() { return active; }
    public int count(T option) { return (int) votes.values().stream().filter(option::equals).count(); }
    public T finish(Collection<UUID> participants, Random random) {
        if (!active) throw new IllegalStateException("Voting is closed");
        votes.keySet().retainAll(participants);
        Map<T, Integer> counts = new HashMap<>();
        for (T option : options) counts.put(option, count(option));
        T winner = VoteResult.choose(options, counts, random);
        clear();
        return winner;
    }
    public void clear() { active = false; votes.clear(); options = List.of(); }
}
