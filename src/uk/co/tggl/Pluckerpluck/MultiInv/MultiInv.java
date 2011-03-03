package uk.co.tggl.Pluckerpluck.MultiInv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.io.ObjectInputStream;
import java.io.FileInputStream;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.World;
import org.bukkit.plugin.PluginDescriptionFile;
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
     final MultiInvWorldListener worldListener = new MultiInvWorldListener(this); 
     final MultiInvReader fileReader = new MultiInvReader(this);
     public ConcurrentHashMap<String, MultiInvPlayerItem[][]> inventories = new ConcurrentHashMap<String, MultiInvPlayerItem[][]>();
     public ConcurrentHashMap<String, World> prevWorlds = new ConcurrentHashMap<String, World>();
     public ConcurrentHashMap<World, World[]> sharedWorlds = new ConcurrentHashMap<World, World[]>();
     public ArrayList<String> sharedNames = new ArrayList<String>();
     public static PermissionHandler Permissions = null;
     public static final Logger log = Logger.getLogger("Minecraft");
     public static String pluginName;
     public boolean permissionsEnabled = true;
     
    @Override
    public void onDisable() {
    	for (Player player : this.getServer().getOnlinePlayers()){
    		playerInventory.storeWorldInventory(player, player.getWorld());
    	}
        log.info("["+ pluginName + "] Plugin disabled.");
    }

    @Override
    public void onEnable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        pluginName = pdfFile.getName();
        Boolean shares = fileReader.parseShares();
        if (shares == false){
            MultiInv.log.info("["+ MultiInv.pluginName + "] Failed to load shared worlds");
        }else{
            MultiInv.log.info("["+ MultiInv.pluginName + "] Shared worlds loaded succesfully");
        }
        
        log.info( "["+ pluginName + "] version " + pdfFile.getVersion() + " is enabled!" );
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.WORLD_LOADED, worldListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_LOGIN , playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_TELEPORT , playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
        setupPermissions();
        deSerialize();
        updateWorlds();
        deleteUnusedInventories();
        renameIncompleteInventories();
        serialize();
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
         if (sender instanceof Player){
            if (permissionsEnabled == true && !Permissions.has((Player) sender, "MultiInv.delete" )){
                 sender.sendMessage("You do not have permission to manipulate inventories");
                 return true;
             }else if(!sender.isOp()){
                 sender.sendMessage("You do not have permission to manipulate inventories");
                 return true;
             }
             if (split.length == 0) {
                 sender.sendMessage("Use '/MultiInv delete <playerName>' to remove inventories'");
                 return true;
             } else{
        
                 String Str = split[0];
                 if(Str.equalsIgnoreCase("delete")){
                     if(split.length==1){
                         sender.sendMessage("Please name a player to delete");
                         return true;
                     }
                     int invs = deleteInventory(split[1]);
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
                 }
             }
         }else{
        	 String Str = split[0];
        	 if(Str.equalsIgnoreCase("list")){
        		 log.info("["+ pluginName + "] Current inventories saved are:");
        		 for (String inventory : inventories.keySet()){
        			 log.info("["+ pluginName + "] " + inventory);
        		 }
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
              inventories = (ConcurrentHashMap<String, MultiInvPlayerItem[][]>) in.readObject();
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
     
     public int deleteInventory(String name){
    	 int i = 0;
         for (String inventory : inventories.keySet()){
                String[] parts = inventory.split(" ");
                if (parts[0].equalsIgnoreCase(name)){
                    inventories.remove(inventory);
                    i++;
                }
         }
         serialize();
         return i;
     }
     public void deleteUnusedInventories(){
    	  for (String inventory : inventories.keySet()){
    		  String[] parts = inventory.split(" ");
    		  if ((!(sharedWorlds.keySet().contains(parts[1].replace("w:", ""))) && parts.length > 2 )|| (sharedWorlds.keySet().contains(parts[1].replace("w:", "")) && sharedWorlds.get(parts[1].replace("w:", "")).length==0)){
     			 String inventoryName = parts[0] + " " + parts[1];
     			 MultiInvPlayerItem[][] tempInv = inventories.get(inventory);
                  inventories.remove(inventory);
                  inventories.put(inventoryName,tempInv);
     			 continue;
     		 }
    		  for (World majorWorld : sharedWorlds.keySet()){
    			  for (World minorWorld : sharedWorlds.get(majorWorld)){
    				  String worldName = "w:" + minorWorld.getName();
    	              if (parts[1].equals(worldName)){
    	            	  inventories.remove(inventory);
    	                  break;
    	              }
    			  }
    		  }
       }
    	 
       return;
     }
     public void renameIncompleteInventories(){
    	 for (String inventory : inventories.keySet()){
    		 String[] parts = inventory.split(" ");
    		 for (World majorWorld : sharedWorlds.keySet()){
    			 if (parts[1].equals("w:" + majorWorld.getName())){
    				 String inventoryName = parts[0] + " w:" + majorWorld.getName();
                     for (World minorWorld : sharedWorlds.get(majorWorld)){
                         inventoryName = inventoryName + " w:" + minorWorld.getName();
                     }
                     MultiInvPlayerItem[][] tempInv = inventories.get(inventory);
                     inventories.remove(inventory);
                     inventories.put(inventoryName,tempInv);
                     break;
    			 }
    		 }
    	 }
    	 
     }
}
