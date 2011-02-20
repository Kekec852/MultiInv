package uk.co.tggl.Pluckerpluck.MultiInv;

import org.bukkit.event.server.PluginEvent;
import org.bukkit.event.server.ServerListener;


public class MultiInvServerListener extends ServerListener{
	
	public final MultiInv plugin;
	
	public MultiInvServerListener(MultiInv instance) {
        plugin = instance;
    }
	
	public void onPluginEnabled(PluginEvent event){
		String name = event.getPlugin().getDescription().getName();
		for (String plugins : plugin.worldPlugins){
			if (name.equalsIgnoreCase(plugins)){
				MultiInv.log.info("["+ MultiInv.pluginName + "] Detected " + name + ". Reloading shares.txt");
				Boolean shares = plugin.fileReader.parseShares();
				if (shares == false){
					MultiInv.log.info("["+ MultiInv.pluginName + "] Failed to load shared worlds");
					return;
				}
				MultiInv.log.info("["+ MultiInv.pluginName + "] Shared worlds loaded succesfully");
			}
		}
		
	}
}
