package com.stumblePillars.game.style;

import com.stumblePillars.StumblePillars;
import com.stumblePillars.game.Game;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class RandomPotionStyle extends GameStyle{

    private List<PotionEffectType> potionTypes = Arrays.asList(PotionEffectType.values());

    public RandomPotionStyle(StumblePillars pl, Game game) {
        super(pl, game);
    }

    @Override
    public String getName() {
        return "<gradient:#FF00C4:#E29FFF:#FF00C4>ᴘᴏçõᴇꜱ ᴅᴀ ꜱᴏʀᴛᴇ</gradient>";
    }

    @Override
    public void tick() {
        for (UUID uuid : getGame().getPlayers()){
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            PotionEffect potion = getRandomPotion();
            player.addPotionEffect(potion);
            player.sendMessage(potion.getType().getKey().getKey());
        }
    }

    @Override
    public void onStart() {
        getGame().broadcastPlayers(MiniMessage.miniMessage().deserialize(getName()));
    }

    @Override
    public void onEnd() {

    }

    @Override
    public int getTickCooldown() {
        return 20*20;
    }

    private PotionEffect getRandomPotion(){
        Random random = new Random();
        int size = potionTypes.size();
        int luckyNumber = random.nextInt(size);
        return new PotionEffect(potionTypes.get(luckyNumber),20*10,1);
    }

}
