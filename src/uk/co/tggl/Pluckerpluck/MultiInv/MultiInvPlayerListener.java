package uk.co.tggl.Pluckerpluck.MultiInv;

import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.scheduler.BukkitScheduler;

public class MultiInvPlayerListener extends PlayerListener{
	
	public final MultiInv plugin;
	public BukkitScheduler tasks;
	public MultiInvPlayerListener(MultiInv instance) {
        plugin = instance;
    }
	
	public void onPlayerJoin(PlayerEvent event){
		plugin.prevWorlds.put(event.getPlayer().getName(), event.getPlayer().getWorld());
		plugin.playerInventory.loadWorldInventory(event.getPlayer(), event.getPlayer().getWorld());
	}
	public void onPlayerQuit(PlayerEvent event){
		plugin.playerInventory.storeWorldInventory(event.getPlayer(), event.getPlayer().getWorld());
		plugin.prevWorlds.remove(event.getPlayer().getName());
	}
	
}
