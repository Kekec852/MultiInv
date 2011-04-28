package uk.co.tggl.Pluckerpluck.MultiInv;

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
     ConcurrentHashMap<String, MultiInvInventory> currentInventories = new ConcurrentHashMap<String, MultiInvInventory>();
     ConcurrentHashMap<String, String> sharesMap = new ConcurrentHashMap<String, String>();
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
    		playerInventory.storeWorldInventory(player, player.getWorld().getName());
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

     private boolean performCheck(CommandSender sender, String[] split) { 
    	 if (split.length == 0) {
             sender.sendMessage("Use '/MultiInv delete <playerName>' to remove inventories'");
             return true;
    	 }
    	 String Str = split[0];
         if (sender instanceof Player){
            if (permissionsEnabled == true && !Permissions.has((Player) sender, "MultiInv.delete" )){
                 sender.sendMessage("You do not have permission to manipulate inventories");
                 return true;
             }else if(!sender.isOp()){
                 sender.sendMessage("You do not have permission to manipulate inventories");
                 return true;
             }            	 
             if(Str.equalsIgnoreCase("delete")){
                 if(split.length==1){
                     sender.sendMessage("Please name a player to delete");
                     return true;
                 }
                 if (getServer().getPlayer(split[1]) != null){
                	 sender.sendMessage("Matched " + getServer().getPlayer(split[1]).getName());
                 }
                // int invs = deletePlayerInventories(split[1]);
                 int invs = 0;
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
     	            if (permissionsEnabled == false || Permissions.has((Player) sender, "MultiInv.debug" )){
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
     		 		}
     	            sender.sendMessage("You do not have permissions to do this");
 	            	return true;
                 }}else{
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
     /*
     public int deletePlayerInventories(String name){
    	 int i = 0;
         for (String inventory : inventories.keySet()){
                String[] parts = inventory.split("\" \".:");
                if (parts[0].equalsIgnoreCase(name)){
                	debugger.debugEvent(MultiInvEvent.INVENTORY_DELETE, new String[]{inventory});
                    inventories.remove(inventory);
                    i++;
                }
         }
         serialize();
         return i;
     }
     public void deleteIfUnused(String inventory){
    	 String[] parts = inventory.split("\" \"");
    	 if (parts[1].matches("^(w:)")){
	    	 if (parts.length != 2 || this.sharesMap.containsKey(parts[1]) ){
	    		inventories.remove(inventory);
	 		 	debugger.debugEvent(MultiInvEvent.INVENTORY_DELETE_UNUSED, new String[]{inventory});
	    	 }
    	 }
     }

     
     public void cleanWorldInventories(){
    	 for (String inventory : inventories.keySet()){
    		 deleteIfUnused(inventory);
    	 }
    	 serialize();
         return;
     }
     */
}
