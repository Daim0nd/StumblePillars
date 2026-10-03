package com.stumblePillars.game;

import java.util.List;
import java.util.Map;
import java.util.Random;

public final class VoteResult {
    private VoteResult() {}
    public static <T> T choose(List<T> options, Map<T, Integer> votes, Random random) {
        if (options.isEmpty()) throw new IllegalArgumentException("No voting options");
        int highest = options.stream().mapToInt(option -> votes.getOrDefault(option, 0)).max().orElse(0);
        List<T> tied = options.stream().filter(option -> votes.getOrDefault(option, 0) == highest).toList();
        return tied.get(random.nextInt(tied.size()));
    }
}
