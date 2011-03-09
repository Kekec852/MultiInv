package uk.co.tggl.Pluckerpluck.MultiInv;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MultiInvPrivateInventories {

	public final MultiInv plugin;
	
	public MultiInvPrivateInventories(MultiInv multiInv) {
		plugin = multiInv;
    }
	
	public void onPrivateCommand(CommandSender sender, String[] split){
		if (sender instanceof Player){
			onPrivatePlayerCommand((Player)sender, split);
		}else{
			onPrivateConsoleCommand(sender, split);
		}
		
	}
	private boolean checkPermission(String permission, Player player){
		if (plugin.permissionsEnabled == true && !MultiInv.Permissions.has(player, "MultiInv." + permission )){
			return true;
		}else if (player.isOp()){
			return true;
		}
		return false;
	}
	private void onPrivatePlayerCommand(Player player, String[] args){
		if (args[1].equalsIgnoreCase("create")){
			if (checkPermission("private.create", player)){
				
			}
		}else if (args[1].equalsIgnoreCase("remove")){
			if (checkPermission("private.create", player)){
				
			}
		}else if (args[1].equalsIgnoreCase("switch")){
			if (checkPermission("private.private", player)){
				
			}
		}else if (args[1].equalsIgnoreCase("list")){
			if (checkPermission("private.private", player)){
				
			}
		}
	}
	
	private void onPrivateConsoleCommand(CommandSender sender, String[] args){
		if (args[1].equalsIgnoreCase("list")){

		}else{
			for (Player player : plugin.getServer().getOnlinePlayers()){
				if (player.getName().equalsIgnoreCase(args[1])){
					if (args[2].equalsIgnoreCase("create")){
						
					}else if (args[1].equalsIgnoreCase("remove")){
						
					}else if (args[1].equalsIgnoreCase("switch")){
						
					}
				}
			}
		}
	}

}
