package uk.co.tggl.Pluckerpluck.MultiInv;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
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
        plugin.playerInventory.storeWorldInventory(player, world);
    }
    public void onPlayerQuit(PlayerQuitEvent event){
    	Player player = event.getPlayer();
    	String playerName = player.getName();
    	String world = player.getWorld().getName();
    	plugin.debugger.debugEvent(MultiInvEvent.PLAYER_LOGOUT, new String[]{playerName});
        plugin.playerInventory.storeWorldInventory(player, world);
    }
    
    public void onPlayerTeleport(PlayerTeleportEvent event){
    	if(!(event.isCancelled())){
	    	String worldTo = event.getTo().getWorld().getName();
	    	Player player = event.getPlayer();
	    	player.setHealth(10);
	    	String worldFrom = event.getFrom().getWorld().getName();
	    	plugin.debugger.debugEvent(MultiInvEvent.WORLD_CHANGE, 
		   			 new String[]{player.getName(), worldFrom, worldTo});
	    	if (plugin.sharesMap.containsKey(worldTo)){
	    		worldTo = plugin.sharesMap.get(worldTo);
	    	}
	    	if (plugin.sharesMap.containsKey(worldFrom)){
	    		worldFrom = plugin.sharesMap.get(worldFrom);
	    	}
	    	if (!(worldTo.equals(worldFrom))){
	    		plugin.playerInventory.storeWorldInventory(player, worldFrom);
	    		plugin.playerInventory.loadWorldInventory(player, worldTo);
	    	}
    	}
    }
    
    public void onPlayerRespawn(PlayerRespawnEvent event){
    	String worldTo = event.getRespawnLocation().getWorld().getName();
    	String worldFrom = event.getPlayer().getWorld().getName();
    	String player = event.getPlayer().getName();
    	
    	if (plugin.sharesMap.containsKey(worldTo)){
    		worldTo = plugin.sharesMap.get(worldTo);
    	}
    	if (plugin.sharesMap.containsKey(worldFrom)){
    		worldFrom = plugin.sharesMap.get(worldFrom);
    	}
    	if (!(worldTo.equals(worldFrom))){
    		MultiInvRespawnRunnable respawnWait = new MultiInvRespawnRunnable(worldTo, worldFrom, player, plugin);
    		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, respawnWait, 10);
    	}

    }    
}
