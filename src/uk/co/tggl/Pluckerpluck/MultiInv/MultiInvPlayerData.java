package uk.co.tggl.Pluckerpluck.MultiInv;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import uk.co.tggl.Pluckerpluck.MultiInv.MultiInvEnums.MultiInvEvent;

public class MultiInvPlayerData {

    public final MultiInv plugin;
    
    public MultiInvPlayerData(MultiInv instance) {
        plugin = instance;
    }
    
    private MultiInvPlayerItem[] itemStackToObject(ItemStack[] stacks){
        MultiInvPlayerItem[] items = new MultiInvPlayerItem[stacks.length];
        int i = 0;
        for (ItemStack stack : stacks){
                MultiInvPlayerItem item = new MultiInvPlayerItem();
                item.setId(stack.getTypeId());
                item.setQuanitity(stack.getAmount());
                item.setDurability(stack.getDurability());
                items[i] = item;
                i++;
            }
        return items;
    }
    
    private ItemStack[] objectToItemStack(MultiInvPlayerItem[] itemArray){
        ItemStack[] items = new ItemStack[itemArray.length];
        int i = 0;
        for (MultiInvPlayerItem item : itemArray){
                int id = item.getId();
                int amount = item.getQuanitity();
                short damage = item.getDurability();
                ItemStack stack = new ItemStack(id, amount, damage);
                items[i] = stack;
                i++;
        }
        return items;
    }
    
    private MultiInvPlayerItem[] armourSlotsToObject(Player player){
        return itemStackToObject(player.getInventory().getArmorContents());
    }
    
    private MultiInvPlayerItem[] inventorySlotsToObject(Player player){
        return itemStackToObject(player.getInventory().getContents());
    }
    
    private ItemStack[] objectToInventorySlots(MultiInvPlayerItem[] itemArray){
        return objectToItemStack(itemArray);
    }
    
    private ItemStack[] objectToArmourSlots(MultiInvPlayerItem[] itemArray){
        return objectToItemStack(itemArray);
    }
    
    private MultiInvPlayerItem[][] saveInventory(Player player){
        MultiInvPlayerItem[] armourO = armourSlotsToObject(player);
        MultiInvPlayerItem[] inventoryO = inventorySlotsToObject(player);
        MultiInvPlayerItem[][] inventory = new MultiInvPlayerItem[2][];
        inventory[0] = new MultiInvPlayerItem[inventoryO.length];
        inventory[1] = new MultiInvPlayerItem[armourO.length];
        inventory[0] = inventoryO;
        inventory[1] = armourO;
        return inventory;
    }
    private void loadInventory(MultiInvPlayerItem[][] inventory, Player player){
        ItemStack[] inventoryS = objectToInventorySlots(inventory[0]);
        ItemStack[] armourS = objectToArmourSlots(inventory[1]);
        player.getInventory().setContents(inventoryS);
        for (int i= 0; i<armourS.length; i++){
            if (armourS[i].getAmount()==0){
                armourS[i] = null;
            }
        }
        player.getInventory().setHelmet(armourS[3]);
        player.getInventory().setChestplate(armourS[2]);
        player.getInventory().setLeggings(armourS[1]);
        player.getInventory().setBoots(armourS[0]);
    }
    public void loadNewInventory(Player player){
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        plugin.debugger.debugEvent(MultiInvEvent.INVENTORY_NEW, new String[]{player.getName()});
        storeWorldInventory(player, player.getWorld().getName());
    }
    public void storeWorldInventory(Player player, String world){
    	if (plugin.sharesMap.containsKey(world)){
    		world = plugin.sharesMap.get(world);
    	}
        String inventoryName = player.getName() + "\" \"w:" + world;
        MultiInvPlayerItem[][] inventory = saveInventory(player);
        plugin.inventories.put(inventoryName, inventory);
        plugin.debugger.debugEvent(MultiInvEvent.INVENTORY_SAVE, new String[]{inventoryName});
        plugin.serialize();
    }
    
    public void loadWorldInventory(Player player, String world){
    	boolean newMember = true;
    	if (plugin.sharesMap.containsKey(world)){
    		world = plugin.sharesMap.get(world);
    	}
    	String worldCheckName = "w:" + world;
        for (String inventory : plugin.inventories.keySet()){
            String[] parts = inventory.split("\" \"");
            if (parts[0].equals(player.getName())){
            	newMember = false;
            	if (parts[1].equals(worldCheckName)){
            		loadInventory(plugin.inventories.get(inventory), player);
                    plugin.debugger.debugEvent(MultiInvEvent.INVENTORY_LOAD, new String[]{inventory});
                    return;
            	}
            }
            
        }
        if (!newMember){
        	loadNewInventory(player);
        }
    }
}
