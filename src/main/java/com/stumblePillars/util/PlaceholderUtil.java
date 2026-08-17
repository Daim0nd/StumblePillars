package com.stumblePillars.util;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;
import org.bukkit.entity.Player;
import java.util.Optional;

public class PlaceholderUtil {

    public static String apply(String text, Player player, StumblePillars pl){
        Optional<Game> opGame = pl.getGameManager().getGame(player);
        if (opGame.isEmpty()) return text;
        Game game = opGame.get();
        return text.replace("<minPlayers>",String.valueOf(game.getMinPlayers()))
                .replace("<maxPlayers>", String.valueOf(game.getMaxPlayers()))
                .replace("<player>",player.getName())
                .replace("<gameSize>",String.valueOf(game.getPlayers().size()))
                .replace("<gameMode>",String.valueOf(game.getCurrentGameStyle() == null ? "" : game.getCurrentGameStyle().getName()))
                .replace("<countdown>",TimerUtil.refactor(game.getGameFinishTimer() == null ? 0 : game.getGameFinishTimer().getCountdown()));
    }

}
