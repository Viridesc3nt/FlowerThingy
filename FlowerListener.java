package me.justinjaques.flowerthingy;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class FlowerListener implements Listener {
    @EventHandler
    private void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
        if(bPlayer.canBend(CoreAbility.getAbility(FlowerThingy.class))) {
            new FlowerThingy(player);
        }
     }
}
