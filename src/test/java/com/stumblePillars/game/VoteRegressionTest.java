package com.stumblePillars.game;

import java.util.*;

public final class VoteRegressionTest {
    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
    public static void main(String[] args) {
        UUID a = UUID.randomUUID(), b = UUID.randomUUID(), outsider = UUID.randomUUID();
        List<UUID> players = new ArrayList<>(List.of(a, b));
        VoteSession<String> first = new VoteSession<>(), second = new VoteSession<>();
        List<String> styles = List.of("meteor", "wormhole", "hook", "potion", "roulette");
        first.start(styles, new Random(1));
        second.start(styles, new Random(2));
        List<String> options = first.options();
        check(options.size() == 3 && new HashSet<>(options).size() == 3, "Three distinct options");
        String x = options.get(0), y = options.get(1);
        check(first.cast(a, x, players), "Participant can vote");
        check(first.cast(a, y, players) && first.count(x) == 0 && first.count(y) == 1, "Vote changes replace old votes");
        check(!first.cast(outsider, x, players), "Spectators and outsiders cannot vote");
        check(!first.cast(a, "unknown", players), "Reject styles outside the ballot");
        check(second.selected(a) == null, "Arenas remain independent");
        first.cast(b, x, players);
        first.remove(b);
        check(first.count(x) == 0, "Leaving removes votes");
        check(first.finish(players, new Random(3)).equals(y), "Most voted wins");
        check(!first.active() && first.options().isEmpty(), "Finishing clears ballot");
        check(!first.cast(a, y, players), "Closed ballots reject stale clicks");
        first.start(styles, new Random(4));
        check(first.selected(a) == null && first.options().size() == 3, "Restart creates fresh ballot");
        first.clear();
        check(!first.active(), "Cancellation and staff override close ballot");
        UUID late = UUID.randomUUID();
        players.add(late);
        String lateChoice = second.options().get(0);
        second.cast(late, lateChoice, players);
        check(second.selected(late) != null, "Late participants can vote");
        second.cast(a, second.options().get(1), players);
        players.remove(a);
        check(second.finish(players, new Random(0)).equals(lateChoice), "Disconnected votes are discarded at finish");
        Set<String> emptyWinners = new HashSet<>(), tiedWinners = new HashSet<>();
        Random sampleRandom = new Random(42);
        for (int sample = 0; sample < 1000; sample++) {
            emptyWinners.add(VoteResult.choose(List.of("a", "b", "c"), Map.of(), sampleRandom));
            tiedWinners.add(VoteResult.choose(List.of("a", "b", "c"), Map.of("a", 2, "b", 2, "c", 1), sampleRandom));
        }
        check(emptyWinners.equals(Set.of("a", "b", "c")), "No votes draws among all options");
        check(tiedWinners.equals(Set.of("a", "b")), "Ties draw only among leaders");
        System.out.println("Vote regression checks passed.");
    }
}


