package uk.co.tggl.Pluckerpluck.MultiInv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.bukkit.entity.Player;

import uk.co.tggl.Pluckerpluck.MultiInv.MultiInvEnums.MultiInvEvent;

public class MultiInvPlayerData {

    public final MultiInv plugin; 
    public ArrayList<String> existingPlayers = new ArrayList<String>();
    
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
        String inventoryName = "w:" + world;
        MultiInvInventory inventory = new MultiInvInventory(player, inventoryName, MultiInv.pluginName);
        saveStateToFile(player, inventory);
        plugin.debugger.debugEvent(MultiInvEvent.INVENTORY_SAVE, new String[]{inventoryName});
    }
    
    public void loadWorldInventory(Player player, String world){
    	/*
    	if (!existingPlayers.contains(player)){
    		MultiInv.log.info("["+ MultiInv.pluginName + "] New player detected.");
    		return;
    	}*/
    	
    	if (plugin.sharesMap.containsKey(world)){
    		world = plugin.sharesMap.get(world);
    	}
    	if (plugin.segregateHealth){
			int health = loadHealthFromFile(player.getName(), world);
			player.setHealth(health);
		}
    	
    	String inventoryName = "w:" + world;
    	File FileP = new File(
    			"plugins" + File.separator + "MultiInv" + File.separator + 
    			"Worlds" + File.separator + world + File.separator +  player.getName() + ".data");
    	File dir = new File(FileP.getParent());
    	Properties prop = new Properties();
    	if (!dir.exists()){
            dir.mkdirs();
        }
        if(!FileP.exists()){
            try {
            	FileP.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    	try {
            FileInputStream in = new FileInputStream(FileP);
            prop.load(in);
            if (prop.containsKey(inventoryName)) {
            	String tmpInventory = prop.getProperty(inventoryName);
            	MultiInvInventory inventory = new MultiInvInventory();
            	inventory.fromString(tmpInventory);
        		inventory.getInventory(player); //sets players inventory (this works)
        		plugin.currentInventories.put(player.getName(), inventory);
        		plugin.debugger.debugEvent(MultiInvEvent.INVENTORY_LOAD, new String[]{inventoryName});
        		in.close();
        		return;
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
        //calls if no inventory is found
        loadNewInventory(player, world);
		plugin.debugger.debugEvent(MultiInvEvent.INVENTORY_LOAD_NEW, new String[]{player.getName()});
    }
    
    public void saveStateToFile(Player player, MultiInvInventory inventory){
    	String name = player.getName();
    	String world = player.getWorld().getName();
    	if (plugin.segregateHealth)
    		saveHealthToFile(name, world, player.getHealth());
    	saveIntentoryToFile(name, world, inventory);
    }
    
    public int loadHealthFromFile(String player, String world){
    	File FileP = new File(
    			"plugins" + File.separator + "MultiInv" + File.separator + 
    			"Worlds" + File.separator + world + File.separator +  player + ".data");
    	Properties prop = new Properties();
    	int healthNumber = 20;
    	try {
            FileInputStream in = new FileInputStream(FileP);
            prop.load(in);
            if (prop.containsKey("h:" + world)) {
            	String health = prop.getProperty("h:" + world);
            	in.close();
            	healthNumber = Integer.parseInt(health);
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
		return healthNumber;
    }
    
    public void saveHealthToFile(String player, String world, int health){
    	File FileP = new File(
    			"plugins" + File.separator + "MultiInv" + File.separator + 
    			"Worlds" + File.separator + world + File.separator +  player + ".data");
    	Properties prop = new Properties();
    	File dir = new File(FileP.getParent());
    	if (!dir.exists()){
            dir.mkdirs();
        }
        if(!FileP.exists()){
            try {
            	FileP.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    	try {
            FileInputStream in = new FileInputStream(FileP);
            prop.load(in);
            String health2 = Integer.toString(health);
            prop.put("h:" + world, health2);
            prop.store(new FileOutputStream(FileP), "Health stored");
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
    
    public void saveIntentoryToFile(String player, String folderName, MultiInvInventory inventory){
    	File FileP = new File(
    			"plugins" + File.separator + "MultiInv" + File.separator + 
    			"Worlds" + File.separator + folderName + File.separator +  player + ".data");
    	Properties prop = new Properties();
    	File dir = new File(FileP.getParent());
    	if (!dir.exists()){
            dir.mkdirs();
        }
        if(!FileP.exists()){
            try {
            	FileP.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    	try {
            FileInputStream in = new FileInputStream(FileP);
            prop.load(in);
            prop.put(inventory.getName(), inventory.toString());
            prop.store(new FileOutputStream(FileP), "Inventory stored");
            in.close();
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }
}
