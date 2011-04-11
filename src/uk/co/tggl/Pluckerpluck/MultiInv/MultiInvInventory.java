package uk.co.tggl.Pluckerpluck.MultiInv;
import java.io.Serializable;


import org.bukkit.block.Block;
import org.bukkit.block.ContainerBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class MultiInvInventory implements Serializable{

	
    private static final long serialVersionUID = -9100540910611570679L;
	/*
	 * Inventory object
	 * inventory[0] = main inventory's contents
	 * inventory[1] = armour slots contents (null for non-player inventories)
	 */
	private MultiInvItem[][] storedInventory = new MultiInvItem[2][];
	
	MultiInvInventory(Inventory inventory){
		if (inventory != null){
			setInventory(inventory);
		}
	}
	
	MultiInvInventory(Player player){
		PlayerInventory inventory = player.getInventory();
		if (inventory != null){
			setInventory(inventory);
		}
	}
	
	/**
	 * Store an inventory in the MultiInvInventory
	 *
	 * @param inventory
	 **/
	public void setInventory(Inventory inventory){
		setContents(inventory.getContents());
		if (inventory instanceof PlayerInventory){
			setArmourContents(((PlayerInventory) inventory).getArmorContents());
		}
	}
	
	/**
	 * Gets the MultiInvInventory and stores it in a player
	 *
	 * @param Player of which to set the inventory
	 * @throws IllegalArgumentException if incorrect ItemStack length is stored
	 **/
	public void getInventory(Player player){
		PlayerInventory inventory = player.getInventory();
		inventory.setContents(getContents());
		ItemStack[] armourS = getArmourContents();
		if (armourS != null){
			inventory.setHelmet(armourS[3]);
			inventory.setChestplate(armourS[2]);
			inventory.setLeggings(armourS[1]);
			inventory.setBoots(armourS[0]);
		}
	}
	
	/**
	 * Gets the MultiInvInventory and stores it in a block
	 *
	 * @param inventory
	 * @return true if the inventory was set
	 * @throws IllegalArgumentException if incorrect ItemStack length is stored
	 * 
	 **/
	public boolean getInventory(Block block){
		if(block instanceof ContainerBlock){
			ContainerBlock container = (ContainerBlock)block;
			container.getInventory().setContents(getContents());
			return true;
		}
		return false;
	}
	

	private void setContents(ItemStack[] itemstacks){
		storedInventory[0] = itemStackToObject(itemstacks);	
	}
	
	private ItemStack[] getContents(){
		if (storedInventory[0] != null){
			return objectToItemStack(storedInventory[0]);
		}
		return null;
	}
	
	private void setArmourContents(ItemStack[] itemstacks){
		storedInventory[0] = itemStackToObject(itemstacks);	
	}
	
	private ItemStack[] getArmourContents(){
		if (storedInventory[1] != null){
			return objectToItemStack(storedInventory[1]);
		}
		return null;
	}
	
	private MultiInvItem[] itemStackToObject(ItemStack[] stacks){
        MultiInvItem[] items = new MultiInvItem[stacks.length];
        int i = 0;
        for (ItemStack stack : stacks){
        	if (stack == null || stack.getAmount() == 0){
        		items[i] = null;
        		i++;
        		continue;
        	}
    		MultiInvItem item = new MultiInvItem();
            item.setId(stack.getTypeId());
            item.setQuanitity(stack.getAmount());
            item.setDurability(stack.getDurability());
            items[i] = item;
            i++;
       }
        return items;
    }

	private ItemStack[] objectToItemStack(MultiInvItem[] itemArray){
        ItemStack[] items = new ItemStack[itemArray.length];
        int i = 0;
        for (MultiInvItem item : itemArray){
        	if (item == null || item.getQuanitity() == 0){
        		items[i] = null;
        		i++;
        		continue;
        	}
            int id = item.getId();
            int amount = item.getQuanitity();
            short damage = item.getDurability();
            ItemStack stack = new ItemStack(id, amount, damage);
            items[i] = stack;
            i++;
        }
        return items;
    }

}
