package uk.co.tggl.Pluckerpluck.MultiInv;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitScheduler;

import uk.co.tggl.Pluckerpluck.MultiInv.MultiInvEnums.MultiInvEvent;

public class MultiInvPlayerListener extends PlayerListener{
    
    public final MultiInv plugin;
    public BukkitScheduler tasks;
    public MultiInvPlayerListener(MultiInv instance) {
        plugin = instance;
    }
    
    public void onPlayerJoin(PlayerJoinEvent event){
    	Player player = event.getPlayer();
    	String playerName = player.getName();
    	String world = player.getWorld().getName();
    	plugin.debugger.debugEvent(MultiInvEvent.PLAYER_LOGIN, new String[]{playerName});
        plugin.playerInventory.loadWorldInventory(player, world);
    }
    public void onPlayerQuit(PlayerQuitEvent event){
    	Player player = event.getPlayer();
    	String playerName = player.getName();
    	String world = player.getWorld().getName();
    	plugin.debugger.debugEvent(MultiInvEvent.PLAYER_LOGOUT, new String[]{playerName});
        plugin.playerInventory.storeWorldInventory(player, world);
    }
    
    public void onPlayerTeleport(PlayerTeleportEvent event){
    	String worldTo = event.getTo().getWorld().getName();
    	Player player = event.getPlayer();
    	String worldFrom = event.getFrom().getWorld().getName();
    	plugin.debugger.debugEvent(MultiInvEvent.WORLD_CHANGE, 
	   			 new String[]{player.getName(), worldFrom, worldTo});
    	String sharedWorld = plugin.sharesMap.get(worldTo);
    	if (sharedWorld != null){
    		worldTo = sharedWorld;
    	}
    	if (!(worldTo.equals(worldFrom))){
		   	 plugin.playerInventory.storeWorldInventory(player, worldFrom);
		   	 plugin.playerInventory.loadWorldInventory(player, worldTo);
    	}
    }
    
}
