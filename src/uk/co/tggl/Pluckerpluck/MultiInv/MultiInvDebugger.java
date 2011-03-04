package uk.co.tggl.Pluckerpluck.MultiInv;

import java.util.ArrayList;

import org.bukkit.World;
import org.bukkit.entity.Player;

import uk.co.tggl.Pluckerpluck.MultiInv.MultiInvEnums.MultiInvEvent;

public class MultiInvDebugger {
	
public final MultiInv plugin;

	private boolean debugging = false;
	private ArrayList<Player> debuggers;
	private String dividerStart = "#-----";
	private String dividerEnd = "-----#";
	
   
    public MultiInvDebugger(MultiInv instance) {
        plugin = instance;
    }
    
    public void addDebuger (Player player){
    	
    }
    
    public void debug(MultiInvEvent event, String[] args){
    	if (debugging == true){
    		switch (event){
    			case WORLD_CHANGE:
    				String message = dividerStart + args[0] + " changed world" + dividerEnd;
    				int shareNumber = shareCheck(args[1], args[2]);
    				String message2 = "";
    				switch (shareNumber){
    					case 0:
    						message2 = "Moved from " + args[1] + " to " + args[2];
    						break;
    					case 1:
    						message2 = "Moved from " + args[1] + "* to " + args[2];
    						break;
    					case 2:
    						message2 = "Moved from " + args[1] + " to " + args[2] + "*";
    						break;
    					case 3:
    						message2 = "Moved from " + args[1] + "* to " + args[2] + "*";
    						break;
    					case 4:
    						message2 = "Moved from" + args[1] + " to " + args[2] + " (Shared)";
    						break;
    				}
    				sendDebuggersMessage(message);
    				sendDebuggersMessage(message2);
    				break;
    		}
    	}
    }
    
    private void sendDebuggersMessage(String message){
    	for (Player player : debuggers){
    		player.sendMessage(message);
    	}
    }
    
    private int shareCheck(String world1, String world2){
    	if (plugin.sharedNames.contains(world1)){
    		if (plugin.sharedNames.contains(world2)){
    			if (plugin.sharedWorlds.containsKey(plugin.getServer().getWorld(world1))){
    				for (World world : plugin.sharedWorlds.get(world1)){
    					if (world.getName().equals(world2)){
    						return 4;
    					}
    				}
    			}
    			if (plugin.sharedWorlds.containsKey(plugin.getServer().getWorld(world2))){
    				for (World world : plugin.sharedWorlds.get(world2)){
    					if (world.getName().equals(world1)){
    						return 4;
    					}
    				}
    			}
    			return 3;
    		}
    		return 1;
    	}
    	if (plugin.sharedNames.contains(world2)){
			return 2;
		}
    	return 0;
    }
}
