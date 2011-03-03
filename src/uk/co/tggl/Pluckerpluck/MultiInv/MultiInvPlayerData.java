package uk.co.tggl.Pluckerpluck.MultiInv;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        storeWorldInventory(player, player.getWorld());
    }
    public void storeWorldInventory(Player player, World world){
        String worldName = world.getName();
        String inventoryName = player.getName() + " w:" + worldName;
        if (plugin.sharedNames.contains(worldName)){
            for (World majorWorld : plugin.sharedWorlds.keySet()){
                if (majorWorld.getName().equals(world.getName())){
                    inventoryName = player.getName() + " w:" + majorWorld.getName();
                    for (World minorWorld : plugin.sharedWorlds.get(majorWorld)){
                        inventoryName = inventoryName + " w:" + minorWorld.getName();
                    }
                    break;
                }else{
                    for (World minorWorld : plugin.sharedWorlds.get(majorWorld)){
                        if (minorWorld.getName().equals(world.getName())){
                            inventoryName = player.getName() + " w:" + majorWorld.getName();
                            for (World minorWorld2 : plugin.sharedWorlds.get(majorWorld)){
                                inventoryName = inventoryName + " w:" + minorWorld2.getName();
                            }
                            break;
                        }
                    }
                }
            }
        }
        MultiInvPlayerItem[][] inventory = saveInventory(player);
        plugin.inventories.put(inventoryName, inventory);
        plugin.serialize();
    }
    
    public void loadWorldInventory(Player player, World world){
    	boolean newMember = true;
        for (String inventory : plugin.inventories.keySet()){
            String[] parts = inventory.split(" ");
            if (parts[0].equals(player.getName())){
            	newMember = false;
                int i = 1;
                String worldCheck = "w:" + world.getName();
                while (i < parts.length){
                    if (parts[i].equals(worldCheck)){
                        loadInventory(plugin.inventories.get(inventory), player);
                        return;
                    }
                    i++;
                }
            }
            
        }
        if (!newMember){
        	loadNewInventory(player);
        }
    }
}
