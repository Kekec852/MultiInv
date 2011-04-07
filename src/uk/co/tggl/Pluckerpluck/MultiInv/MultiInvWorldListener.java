package uk.co.tggl.Pluckerpluck.MultiInv;

import org.bukkit.entity.Player;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;


public class MultiInvWorldListener extends WorldListener{
    
    public final MultiInv plugin;
    
    public MultiInvWorldListener(MultiInv instance) {
        plugin = instance;
    }

    @Override
    public void onWorldSave(WorldSaveEvent event) {
        for (Player player : plugin.getServer().getOnlinePlayers()){
            plugin.playerInventory.storeWorldInventory(player, player.getWorld().getName());
        }
    }

    @Override
    public void onWorldLoad(WorldLoadEvent event){
            MultiInv.log.info("["+ MultiInv.pluginName + "] Detected " + event.getWorld().getName() + ". Reloading shares.txt");
            Boolean shares = plugin.fileReader.parseShares();
            if (shares == false){
                MultiInv.log.info("["+ MultiInv.pluginName + "] Failed to load shared worlds.");
                MultiInv.log.info("["+ MultiInv.pluginName + "] Plugin on standby until new world found.");
                return;
            }
            MultiInv.log.info("["+ MultiInv.pluginName + "] Shared worlds loaded succesfully");
            plugin.cleanWorldInventories();
        
    }
    
    
}
