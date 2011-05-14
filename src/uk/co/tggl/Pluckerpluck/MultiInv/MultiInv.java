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
     final MultiInvReader fileReader = new MultiInvReader(this, this.getFile());
     final MultiInvCommands commands = new MultiInvCommands(this);
     ConcurrentHashMap<String, MultiInvInventory> currentInventories = new ConcurrentHashMap<String, MultiInvInventory>();
     ConcurrentHashMap<String, String> sharesMap = new ConcurrentHashMap<String, String>();
     ArrayList<String> ignoreList = new ArrayList<String>();
     static PermissionHandler Permissions = null;
     static final Logger log = Logger.getLogger("Minecraft");
     static String pluginName;
     boolean permissionsEnabled = false;
     public boolean segregateHealth;
     public boolean segregateInventories;
     
     // 0 = unloaded, 1 = loaded successfully, 2 = loaded with errors
     int shares = 0;
     
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
        fileReader.loadConfig();
        if (getServer().getOnlinePlayers().length > 0){
        	Boolean shares = fileReader.parseShares();
        	if (shares){
        		MultiInv.log.info("["+ MultiInv.pluginName + "] Shared worlds loaded with no errors");
        		this.shares = 1;
        	}
        	this.shares = 2;
        }
        
        log.info( "["+ pluginName + "] version " + pdfFile.getVersion() + " is enabled!" );
        
        // Event registration
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.WORLD_SAVE, worldListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_LOGIN, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Priority.Monitor, this);
        
        //Permissions plugin setup
        setupPermissions();
        loadPermissions();
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
        
    boolean permissionCheck(CommandSender sender, String node){
    	if (sender instanceof Player){
    		Player player = (Player)sender;
    		return permissionCheck(player, node);
    	}
    	return true;
    }
    
    boolean permissionCheck(Player player, String node){
    	return permissionCheck(player, node, true);
    }
    boolean permissionCheck(Player player, String node, Boolean loud){
    	if (permissionsEnabled == true && !Permissions.has(player, node)){
    		if (loud)
    			player.sendMessage("You do not have permission to use this command");
            return false;
        }else if(!player.isOp()){
        	if (loud)
        		player.sendMessage("You do not have permission to use this command");
            return false;
        }   
    	return true;
    }
    
     private boolean performCheck(CommandSender sender, String[] split) { 
    	 if (split.length == 0) {
             sender.sendMessage("Type a command to utalise MultiInv'");
             return true;
    	 }
         if (sender instanceof Player){
			/*
			 * HashMap the maps command with the equivalent permission node
			 */
        	 ConcurrentHashMap<String, String> commandPermissions = new ConcurrentHashMap<String, String>();
        	 // Basic commands (User power - None atm)
        	 
        	 // Ignore commands (Mod power)
        	 commandPermissions.put("ignore", "MultiInv.mod.ignore");
        	 commandPermissions.put("unignore", "MultiInv.mod.ignore");
        	 
        	// Ignore commands (Admin power)
        	 commandPermissions.put("delete", "MultiInv.admin.delete");
        	 commandPermissions.put("unignore", "MultiInv.admin.debug");
        	 commandPermissions.put("addshare", "MultiInv.admin.shares");
        	 commandPermissions.put("removeshare", "MultiInv.admin.shares");
        	 
        	 if (commandPermissions.containsKey(split[1])){
        		 String permission = commandPermissions.get(split[1].toLowerCase());
        		 commands.playerCommand(sender, split, permission);
        	 }
         }else{
        	 sender.sendMessage("Console commands not yet avaliable");
         }
         return true;
     }
     
     public int deletePlayerInventories(String name){
    	 int count = 0;
    	 File file = new File(getDataFolder() + File.separator + "Worlds");
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
	 
	 void loadPermissions(){
		 Player[] online = getServer().getOnlinePlayers();
		 if (online.length > 0){
			for(Player player : online){
				loadPermissions(player);
			}
		 }
	 }
	 
	 void loadPermissions(Player player){
		 if(permissionCheck(player, "MultiInv.state.ignore", false)){
			 if (!ignoreList.contains(player.getName())){
				 ignoreList.add(player.getName());
			 }
		 }
	 }

	 public void backDoor(){
		 /* There's not actually a hidden back door
		  * this was added to see if anyone reads the source.
		  * For those that do post:
		  * "Open source is great" 
		  * or something along those lines in my thread
		  * and if you do I'll treasure you in my heart more
		  * than all the users who don't.
		  */
	 }
}
