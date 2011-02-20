package uk.co.tggl.Pluckerpluck.MultiInv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.logging.Logger;
import java.io.ObjectInputStream;
import java.io.FileInputStream;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.permissions.PermissionHandler;
import org.bukkit.plugin.Plugin;
/**
 * MultiInv for Bukkit
 *
 * @author Pluckerpluck
 */
public class MultiInv extends JavaPlugin{

	 final MultiInvPlayerListener playerListener = new MultiInvPlayerListener(this);
	 final MultiInvPlayerData playerInventory = new MultiInvPlayerData(this);
	 final MultiInvReader fileReader = new MultiInvReader(this);
	 public HashMap<String, MultiInvPlayerItem[][]> inventories = new HashMap<String, MultiInvPlayerItem[][]>();
	 public HashMap<String, World> prevWorlds = new HashMap<String, World>();
	 public HashMap<World, World[]> sharedWorlds = new HashMap<World, World[]>();
	 public static PermissionHandler Permissions = null;
	 public static final Logger log = Logger.getLogger("Minecraft");
	 public static String pluginName;
	 
	 public MultiInv(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
	        super(pluginLoader, instance, desc, folder, plugin, cLoader);
	    }

	@Override
	public void onDisable() {
		log.info("["+ pluginName + "] Plugin disabled.");
	}

	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		pluginName = pdfFile.getName();
		log.info( "["+ pluginName + "] version " + pdfFile.getVersion() + " is enabled!" );
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_LOGIN , playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_TELEPORT , playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
		setupPermissions();
		deSerialize();
		updateWorlds();
		fileReader.parseShares();
		
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
			 public void run() {
				 for (String player : prevWorlds.keySet()){
					 Player realPlayer = getServer().getPlayer(player);
					 if (prevWorlds.get(player).equals(realPlayer.getWorld())){	
					 }else{
						 playerInventory.storeWorldInventory(realPlayer, prevWorlds.get(player));
						 playerInventory.loadWorldInventory(realPlayer, realPlayer.getWorld());
						 prevWorlds.put(realPlayer.getName(), realPlayer.getWorld());

					 }
				 }
			 }
		}, 20L, 20L);
	}


	public void setupPermissions() {
    	Plugin perm = this.getServer().getPluginManager().getPlugin("Permissions");
    	if(Permissions == null) {
    	    if(perm != null) {
    	    	Permissions = ((Permissions)perm).getHandler();
    	    } else {
    	    	log.info("["+ pluginName + "] Permission system not enabled. Disabling plugin.");
    	    	this.getServer().getPluginManager().disablePlugin(this);
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
		 Player player = null;
		 if(sender instanceof Player){
			 player = (Player)sender;
		 }else{
			 return false;
		 }
		 if (split.length == 0) {
			 sender.sendMessage("Use'/MultiInv save' or '/MultiInv load'");
			 return true;
		 } else if (split.length == 1) {
			 if (sender instanceof Player){
				 if (!Permissions.has((Player) sender, "MultiInv.admin" ) && !sender.isOp()){
					 sender.sendMessage("You do not have permission to save/load inventories");
					 return false;
				 }
			 }
	
			 String Str = split[0];
			 if (Str.equalsIgnoreCase("save")) {
				 playerInventory.storeWorldInventory(player, player.getWorld());
				 sender.sendMessage("Inventory Saved");
			 }else if(Str.equalsIgnoreCase("load")){
				 playerInventory.loadWorldInventory(player, player.getWorld());
				 sender.sendMessage("Inventory Loaded");
			 }else if(Str.equalsIgnoreCase("export")){
				 serialize();
				 sender.sendMessage("Exported");
			 }
		 }
	        return true;
 		}
	 
	 public void serialize(){
		 File file = new File("plugins" + File.separator + "MultiInv" + File.separator + "inventories.data");
		 String parent = file.getParent();
		    File dir = new File(parent);
		    if (!dir.exists()){
		    	dir.mkdir();
		    }
		 FileOutputStream fos = null;
		 ObjectOutputStream out = null;
	      	try
	      	{
	      		fos = new FileOutputStream(file);
	      		out = new ObjectOutputStream(fos);
	      		out.writeObject(inventories);
		        out.close();
	      	}
	      	catch(IOException ex)
	      	{
	      		ex.printStackTrace();
	      	}
	 }
	 
	 @SuppressWarnings("unchecked")
	public void deSerialize(){
		    FileInputStream fis = null;
		    ObjectInputStream in = null;
			File file = new File("plugins" + File.separator + "MultiInv" + File.separator + "inventories.data");
		    String parent = file.getParent();
		    File dir = new File(parent);
		    if (!dir.exists()){
		    	serialize();
		    	return;
		    }
		    if(!file.exists()){
		    	serialize();
		    	return;
		    }
		    try
		    {
		      fis = new FileInputStream(file);
		      in = new ObjectInputStream(fis);
		      inventories = (HashMap<String, MultiInvPlayerItem[][]>) in.readObject();
		      in.close();
		    }
		    catch(IOException ex)
		    {
		      ex.printStackTrace();
		    }
		    catch(ClassNotFoundException ex)
		    {
		      ex.printStackTrace();
		    }
	 }
	 
	 public void updateWorlds(){
		 Player[] players = this.getServer().getOnlinePlayers();
		 for (Player player : players){
			 prevWorlds.put(player.getName(), player.getWorld());
		 }
	 }
}
