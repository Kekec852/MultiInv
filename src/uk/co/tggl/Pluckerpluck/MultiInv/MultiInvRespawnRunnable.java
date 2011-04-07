package uk.co.tggl.Pluckerpluck.MultiInv;

import org.bukkit.entity.Player;

public class MultiInvRespawnRunnable implements Runnable{
	public String worldTo;
	public String worldFrom;
	public String player;
	public MultiInv plugin;
	
	public MultiInvRespawnRunnable(String worldTo, String worldFrom, String player, MultiInv plugin){
		this.worldTo = worldTo;
		this.worldFrom = worldFrom;
		this.player = player;
		this.plugin = plugin;
	}
	
	@Override
    public void run() {
		Player playerObj = plugin.getServer().getPlayer(this.player);
		plugin.playerInventory.storeWorldInventory(playerObj, worldFrom);
		plugin.playerInventory.loadWorldInventory(playerObj, worldTo);
    }

}
