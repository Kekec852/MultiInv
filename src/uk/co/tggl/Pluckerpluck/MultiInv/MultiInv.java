package uk.co.tggl.Pluckerpluck.MultiInv;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.permissions.PermissionHandler;
/**
 * MultiInv for Bukkit
 *
 * @author Pluckerpluck
 */
public class MultiInv extends JavaPlugin{

     final MultiInvPlayerListener playerListener = new MultiInvPlayerListener(this);
     final MultiInvPlayerData playerInventory = new MultiInvPlayerData(this);
     final MultiInvWorldListener worldListener = new MultiInvWorldListener(this); 
     final MultiInvDebugger debugger = new MultiInvDebugger(this);
     final MultiInvReader fileReader = new MultiInvReader(this);
     final MultiInvProperties properties = new MultiInvProperties();
     ConcurrentHashMap<String, MultiInvInventory> currentInventories = new ConcurrentHashMap<String, MultiInvInventory>();
     ConcurrentHashMap<String, String> sharesMap = new ConcurrentHashMap<String, String>();
     ArrayList<String> ignoreList = new ArrayList<String>();
     static PermissionHandler Permissions = null;
     static final Logger log = Logger.getLogger("Minecraft");
     static String pluginName;
     boolean permissionsEnabled = false;
     public boolean segregateHealth = true;
    
     public void onLoad(){
    	
     }
     
    @Override
    public void onDisable() {
    	for (Player player : this.getServer().getOnlinePlayers()){
    		playerInventory.storeCurrentInventory(player);
    	}
    	
    	debugger.saveDebugLog();
        log.info("["+ pluginName + "] Plugin disabled.");
    }

    @Override
    public void onEnable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        pluginName = pdfFile.getName();
        Boolean shares = fileReader.parseShares();
        if (shares == false){
            MultiInv.log.info("["+ MultiInv.pluginName + "] Failed to load shared worlds");
            MultiInv.log.info("["+ MultiInv.pluginName + "] Plugin on standby until new world found.");
        }else{
            MultiInv.log.info("["+ MultiInv.pluginName + "] Shared worlds loaded succesfully");
        }
        if (shares){
        	//cleanWorldInventories();
        }
        log.info( "["+ pluginName + "] version " + pdfFile.getVersion() + " is enabled!" );
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.WORLD_SAVE, worldListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.WORLD_LOAD, worldListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_LOGIN, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Monitor, this);
        setupPermissions();
    }

    
    public void setupPermissions() {
        Plugin perm = this.getServer().getPluginManager().getPlugin("Permissions");
        if(Permissions == null) {
            if(perm != null) {
                Permissions = ((Permissions)perm).getHandler();
            } else {
                log.info("["+ pluginName + "] Permission system not enabled. Using ops.txt");
                permissionsEnabled = false;
            }
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        String[] trimmedArgs = args;
        String commandName = command.getName().toLowerCase();
        if (commandName.equals("multiinv")) {
            return performCheck(sender, trimmedArgs);
        }
        return false;
    }
    private boolean permissionCheck(Player player, String node){
    	if (permissionsEnabled == true && !Permissions.has(player, "MultiInv.delete" )){
    		player.sendMessage("You do not have permission to use this command");
            return false;
        }else if(!player.isOp()){
        	player.sendMessage("You do not have permission to use this command");
            return false;
        }   
    	return true;
    }
     private boolean performCheck(CommandSender sender, String[] split) { 
    	 if (split.length == 0) {
             sender.sendMessage("Use '/MultiInv delete <playerName>' to remove inventories'");
             return true;
    	 }
    	 String Str = split[0];
         if (sender instanceof Player){
             if(Str.equalsIgnoreCase("delete")){
            	 if (!permissionCheck((Player) sender, "MultiInv.delete"))
            	 	return true;
                 if(split.length==1){
                     sender.sendMessage("Please name a player to delete");
                     return true;
                 }
                 int invs = deletePlayerInventories(split[1]);
                 if (invs != 0){
                	 if (invs == 1){
                		 sender.sendMessage("Deleted 1 invetory for player " + split[1]);
                	 }else{
                		 sender.sendMessage("Deleted " + invs + " invetories for player " + split[1]);
                	 }
                     return true;
                 }else{
                     sender.sendMessage("Player " + split[1] + " does not exist");
                     return true;
                 }
             }else if (Str.equalsIgnoreCase("debug")){
            	 if (!permissionCheck((Player) sender, "MultiInv.debug"))
             	 	return true;
            	if (split.length >= 2){
	            	if (split[1].equalsIgnoreCase("start")){
	            		if (split.length >= 3 && split[2].equalsIgnoreCase("show")){
    	            		debugger.addDebugger((Player)sender);
    	            		sender.sendMessage("Debugging started (shown)");
    	            		return true;
    	            	}else{
    	            		debugger.startDebugging();
    	            		sender.sendMessage("Debugging started (hidden)");
    	            		return true;
    	            	}
	            	}else if (split[1].equalsIgnoreCase("stop")){
	            		debugger.stopDebugging();
	            		sender.sendMessage("Debugging stopped");
	            		return true;
	            	}
	            	else if (split[1].equalsIgnoreCase("save")){
	            		debugger.saveDebugLog();
	            		sender.sendMessage("Debugging saved");
	            		return true;
	            	}
	             }
            	sender.sendMessage("Please use a correct command");
            	return true;
             }else if(Str.equalsIgnoreCase("ignore")){
            	 if (!permissionCheck((Player) sender, "MultiInv.ignore"))
              	 	return true;
            	 if (split.length >= 2){
 	            	Player playerObject = getServer().getPlayer(split[1]);
 	            	if (playerObject != null){
 	            		String playerName = playerObject.getName();
 	            		if (ignoreList.contains(playerName)){
 	            			sender.sendMessage("Player is already being ignored");
 	            			return true;
 	            		}
 	            		ignoreList.add(playerName);
 	            		sender.sendMessage(playerName + " is now being ignored");
 	            		return true;
 	            	}
 	            	sender.sendMessage("Player cannot be found. He must be online");
 	            	return true;
 	             }
             }else if(Str.equalsIgnoreCase("unignore")){
            	 if (!permissionCheck((Player) sender, "MultiInv.ignore"))
               	 	return true;
             	 if (split.length >= 2){
             		String playerName = split[1];
             		
             		if (ignoreList.contains(playerName)){
            			ignoreList.remove(playerName);
            			sender.sendMessage(playerName + " is no longer ignored");
            			return true;
            		}
             		
  	            	Player playerObject = getServer().getPlayer(split[1]);
  	            	if (playerObject != null){
  	            		playerName = playerObject.getName();        		
	  	            	if (ignoreList.contains(playerName)){
	            			ignoreList.remove(playerName);
	            			sender.sendMessage(playerName + " is no longer ignored");
	            			return true;
	            		}
  	            	}
            		sender.sendMessage(playerName + " was not being ignored");
            		return true;
  	             }
              }
         }else{
            	 if (Str.equalsIgnoreCase("debug")){
	    			 if (split.length >= 2){
	        			if (split[1].equalsIgnoreCase("stop")){
	 	            		debugger.stopDebugging();
	 	            		sender.sendMessage("Debugging stopped");
	 	            	}else if (split[1].equalsIgnoreCase("start")){
	 	            		debugger.startDebugging();
	 	            		sender.sendMessage("Debugging started");
		            	}else if (split[1].equalsIgnoreCase("save")){
		            		debugger.saveDebugLog();
		            		sender.sendMessage("Debugging saved");
		            		return true;
		            	}
	    			 }
	        	 }
             }
            return true;
         }
     public int deletePlayerInventories(String name){
    	 int count = 0;
    	 File file = new File("plugins" + File.separator + "MultiInv" + File.separator + 
 		"Worlds");
    	 count = searchFolders(file, name + ".data");
    	 return count;
     }
     
	 private int searchFolders(File file, String search){
		int count = 0;
    	if (file.isDirectory()){
    		String internalNames[] = file.list();
    		for (String name : internalNames){
    			count = count + searchFolders(new File(file.getAbsolutePath() + File.separator + name), search);
    		}
    	}else{
    		if (file.getName().equals(search)){
    			file.delete();
    			return count + 1;
    		}
    	}
    	return count;
    }
}
