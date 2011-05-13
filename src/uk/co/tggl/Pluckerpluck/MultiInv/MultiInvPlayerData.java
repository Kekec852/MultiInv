package uk.co.tggl.Pluckerpluck.MultiInv;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.entity.Player;

import uk.co.tggl.Pluckerpluck.MultiInv.MultiInvEnums.MultiInvEvent;

public class MultiInvPlayerData {

    public final MultiInv plugin; 
    public ArrayList<String> existingPlayers = new ArrayList<String>();
    
    public MultiInvPlayerData(MultiInv instance) {
        plugin = instance;
        loadPlayers();
    }
    
    private void loadPlayers(){
    	File file = new File("plugins" + File.separator + "MultiInv" + File.separator + 
		"Worlds");
    	searchFolders(file);
    }
    
    private void searchFolders(File file){
    	if (file.isDirectory()){
    		String internalNames[] = file.list();
    		for (String name : internalNames){
    			searchFolders(new File(file.getAbsolutePath() + File.separator + name));
    		}
    	}else{
    		String fileName = file.getName().split("\\.")[0];
    		if (!existingPlayers.contains(fileName)){
    			existingPlayers.add(fileName);
    		}
    	}
    }
    
    private void loadNewInventory(Player player, String world){
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        plugin.debugger.debugEvent(MultiInvEvent.INVENTORY_NEW, new String[]{player.getName()});
        String inventoryName = "w:" + world;
        storeManualInventory(player, inventoryName);
    }
    public void storeCurrentInventory(Player player){
    	String inventoryName = "w:" + player.getWorld().getName();
    	if (plugin.currentInventories.containsKey(player.getName())){
    		inventoryName = plugin.currentInventories.get(player.getName()).getName();
    	}
    	MultiInvInventory inventory = new MultiInvInventory(player, inventoryName, MultiInv.pluginName);
        saveStateToFile(player, inventory);
        plugin.debugger.debugEvent(MultiInvEvent.INVENTORY_SAVE, new String[]{inventoryName});
    }
    
    public void storeManualInventory(Player player, String inventoryName){
    	MultiInvInventory inventory = new MultiInvInventory(player, inventoryName, MultiInv.pluginName);
    	String file = "plugins" + File.separator + "MultiInv" + File.separator + 
			"Other" + File.separator +  player.getName() + ".data";
    	plugin.currentInventories.put(player.getName(), inventory);
    	MultiInvProperties.saveToProperties(file, inventory.getName(), inventory.toString(), "Stored Inventory");
    }
    
    public void loadWorldInventory(Player player, String world){
    	if (!existingPlayers.contains(player.getName())){
    		MultiInv.log.info("["+ MultiInv.pluginName + "] New player detected: " + player.getName());
    		existingPlayers.add(player.getName());
    		return;
    	}    	
    	if (plugin.sharesMap.containsKey(world)){
    		world = plugin.sharesMap.get(world);
    	}
    	if (plugin.segregateHealth){
			int health = loadHealthFromFile(player.getName(), world);
			player.setHealth(health);
		}
    	
    	String inventoryName = "w:" + world;
    	String file = "plugins" + File.separator + "MultiInv" + File.separator + 
			"Worlds" + File.separator + world + File.separator +  player.getName() + ".data";
        String tmpInventory = MultiInvProperties.loadFromProperties(file, inventoryName);
        if (tmpInventory != null){
	        MultiInvInventory inventory = new MultiInvInventory();
	    	inventory.fromString(tmpInventory); // converts properties string to MultiInvInventory
			inventory.getInventory(player); //sets players inventory
			plugin.currentInventories.put(player.getName(), inventory);
			plugin.debugger.debugEvent(MultiInvEvent.INVENTORY_LOAD, new String[]{inventoryName});
			return;
        }
        loadNewInventory(player, world); //calls if no inventory is found
        plugin.debugger.debugEvent(MultiInvEvent.INVENTORY_LOAD_NEW, new String[]{player.getName()});
    }
    
    public void saveStateToFile(Player player, MultiInvInventory inventory){
    	String world = player.getWorld().getName();
    	String file = "plugins" + File.separator + "MultiInv" + File.separator + 
		"Worlds" + File.separator + world + File.separator +  player.getName() + ".data";
    	if (plugin.segregateHealth)
    		MultiInvProperties.saveToProperties(file, "h:" + world,Integer.toString(player.getHealth()));
    	
    	MultiInvProperties.saveToProperties(file, inventory.getName(), inventory.toString(), "Stored Inventory");
    }
    
    public int loadHealthFromFile(String player, String world){
    	String file = "plugins" + File.separator + "MultiInv" + File.separator + 
    			"Worlds" + File.separator + world + File.separator +  player + ".data";
    	String healthString = MultiInvProperties.loadFromProperties(file, "h:" + world, "20");
		return Integer.parseInt(healthString);
    }
}
