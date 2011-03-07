package uk.co.tggl.Pluckerpluck.MultiInv;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.scheduler.BukkitScheduler;

import uk.co.tggl.Pluckerpluck.MultiInv.MultiInvEnums.MultiInvEvent;

public class MultiInvPlayerListener extends PlayerListener{
    
    public final MultiInv plugin;
    public BukkitScheduler tasks;
    public MultiInvPlayerListener(MultiInv instance) {
        plugin = instance;
    }
    
    public void onPlayerJoin(PlayerEvent event){
    	Player player = event.getPlayer();
    	String playerName = player.getName();
    	String world = player.getWorld().getName();
    	plugin.debugger.debugEvent(MultiInvEvent.PLAYER_LOGIN, new String[]{playerName});
        plugin.prevWorlds.put(playerName, world);
        plugin.playerInventory.loadWorldInventory(player, world);
    }
    public void onPlayerQuit(PlayerEvent event){
    	Player player = event.getPlayer();
    	String playerName = player.getName();
    	String world = player.getWorld().getName();
    	plugin.debugger.debugEvent(MultiInvEvent.PLAYER_LOGOUT, new String[]{playerName});
        plugin.playerInventory.storeWorldInventory(player, world);
        plugin.prevWorlds.remove(playerName);
        plugin.debugger.removeDebugger(player);
    }
    
}
