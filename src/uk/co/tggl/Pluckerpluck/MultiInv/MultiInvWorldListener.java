package uk.co.tggl.Pluckerpluck.MultiInv;

import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;


public class MultiInvWorldListener extends WorldListener{
    
    public final MultiInv plugin;
    
    public MultiInvWorldListener(MultiInv instance) {
        plugin = instance;
    }
    
    public void onWorldLoad(WorldLoadEvent event){
            MultiInv.log.info("["+ MultiInv.pluginName + "] Detected " + event.getWorld().getName() + ". Reloading shares.txt");
            Boolean shares = plugin.fileReader.parseShares();
            if (shares == false){
                MultiInv.log.info("["+ MultiInv.pluginName + "] Failed to load shared worlds");
                return;
            }
            MultiInv.log.info("["+ MultiInv.pluginName + "] Shared worlds loaded succesfully");
            plugin.cleanWorldInventories();
        
    }
}
