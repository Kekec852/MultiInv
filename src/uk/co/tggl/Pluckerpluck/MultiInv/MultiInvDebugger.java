package uk.co.tggl.Pluckerpluck.MultiInv;

import org.bukkit.entity.Player;

import uk.co.tggl.Pluckerpluck.MultiInv.MultiInvEnums.MultiInvEvent;

public class MultiInvDebugger {
	
public final MultiInv plugin;

	private boolean debugging = false;

	
    
    public MultiInvDebugger(MultiInv instance) {
        plugin = instance;
    }
    
    public void addDebuger (Player player){
    	
    }
    
    public void debug(MultiInvEvent event, String[] args){
    	if (debugging == true){
    		switch (event){
    			case WORLD_CHANGE:
    				break;
    		}
    	}
    }
}
