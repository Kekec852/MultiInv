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
        MultiInvItem[][] inventory = inventoryClass.saveInventory(player);
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
            		inventoryClass.loadInventory(plugin.inventories.get(inventory), player);
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
    
    public void storePrivateInventory(Player player, String name){
    	String inventoryName = player.getName() + "\" \"p:" + name;
        MultiInvItem[][] inventory = inventoryClass.saveInventory(player);
        plugin.inventories.put(inventoryName, inventory);
        plugin.debugger.debugEvent(MultiInvEvent.INVENTORY_SAVE, new String[]{inventoryName});
        plugin.serialize();
    }
    
    public boolean loadPrivateInventory(Player player, String name){
    	String worldCheckName = "p:" + name;
        for (String inventory : plugin.inventories.keySet()){
            String[] parts = inventory.split("\" \"");
            if (parts[0].equals(player.getName())){
            	if (parts[1].equals(worldCheckName)){
            		inventoryClass.loadInventory(plugin.inventories.get(inventory), player);
                    plugin.debugger.debugEvent(MultiInvEvent.INVENTORY_LOAD, new String[]{inventory});
                    return true;
            	}
            }
            
        }
        return false;
    }
}
