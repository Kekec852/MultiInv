package uk.co.tggl.Pluckerpluck.MultiInv;

import org.bukkit.entity.Player;

import uk.co.tggl.Pluckerpluck.MultiInv.MultiInvEnums.MultiInvEvent;

public class MultiInvPlayerData {

    public final MultiInv plugin; 
    
    public MultiInvPlayerData(MultiInv instance) {
        plugin = instance;
    }
    
    private void loadNewInventory(Player player, String world){
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        plugin.debugger.debugEvent(MultiInvEvent.INVENTORY_NEW, new String[]{player.getName()});
        storeWorldInventory(player, world);
    }
    public void storeWorldInventory(Player player, String world){
    	if (plugin.sharesMap.containsKey(world)){
    		world = plugin.sharesMap.get(world);
    	}
        String inventoryName = player.getName() + "\" \"w:" + world;
        MultiInvInventory inventory = new MultiInvInventory(player);
        plugin.inventories.put(inventoryName, inventory);
        plugin.debugger.debugEvent(MultiInvEvent.INVENTORY_SAVE, new String[]{inventoryName});
        plugin.serialize();
    }
    
    public void loadWorldInventory(Player player, String world){
    	boolean newMember = true;
    	if (plugin.sharesMap.containsKey(world)){
    		world = plugin.sharesMap.get(world);
    	}
    	String worldCheckName = "w:" + world;
        for (String inventory : plugin.inventories.keySet()){
            String[] parts = inventory.split("\" \"");
            if (parts[0].equals(player.getName())){
            	newMember = false;
            	if (parts[1].equals(worldCheckName)){
            		plugin.inventories.get(inventory).getInventory(player);
                    plugin.debugger.debugEvent(MultiInvEvent.INVENTORY_LOAD, new String[]{inventory});
                    return;
            	}
            }
            
        }
        if (!newMember){
        	loadNewInventory(player, world);
        	plugin.debugger.debugEvent(MultiInvEvent.INVENTORY_LOAD_NEW, new String[]{player.getName()});
        }
    }
}
