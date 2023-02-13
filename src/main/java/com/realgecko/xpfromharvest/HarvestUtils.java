package com.realgecko.xpfromharvest;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.realgecko.xpfromharvest.config.CropConfig;
import com.realgecko.xpfromharvest.config.HarvestConfig;
import com.realgecko.xpfromharvest.config.data.CropCondition;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("deprecation")
public class HarvestUtils 
{
	public static ResourceLocation getRegistryName(Block block) 
	{
		return Registry.BLOCK.getKey(block);
	}
	
	public static ResourceLocation getRegistryName(Item item) 
	{
		return Registry.ITEM.getKey(item);
	}
	
	public static Block getBlock(String registryName) 
	{
		return Registry.BLOCK.get(new ResourceLocation(registryName));
	}
	
	public static Item getItem(String registryName) 
	{
		return Registry.ITEM.get(new ResourceLocation(registryName));
	}
	
	public static void giveExperience(ServerLevel level, Block block, BlockPos pos) 
	{
    	if (level.getRandom().nextDouble() <= HarvestConfig.xpChance.get())
    		block.popExperience(level, pos, HarvestConfig.xpAmount.get());
    }
    
	public static void dropItems(ServerLevel level, Block block, BlockPos pos, BlockState state, Player player, CropCondition condition) 
	{
    	ItemStack tool = player.getItemBySlot(EquipmentSlot.MAINHAND);
        List<ItemStack> drops = Block.getDrops(state, level, pos, null, player, tool);
        List<ItemStack> toRemove = new ArrayList<ItemStack>();
        ItemStack seed = block instanceof CropBlock ? ((CropBlock)block).getCloneItemStack(level, pos, state) : ItemStack.EMPTY;
        
        boolean foundSeed = false;
        for (ItemStack stack : drops) 
        {
            // Check if seed
        	if (!foundSeed) 
        	{
        		// Check if we got seed from CropBlock. If not, then we assume that
        		// Seeds are BlockNamedItem whose block is equal to crop it's able to produce
        		if ((!seed.isEmpty() && stack.getItem() == seed.getItem()) ||
        			(stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() == block)) 
        		{
            		stack.shrink(1);
            		foundSeed = true;
        		}
        	}
        	
            // Check blacklist
    		if (condition.isDropBlacklisted(stack.getItem()))
    			toRemove.add(stack);
        }

        drops.removeAll(toRemove);

        // Now let's spawn remaining drops
        for (ItemStack stack : drops) 
        	if (!stack.isEmpty())
		    {
		        ItemEntity entityItem = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), stack);
		        level.addFreshEntity(entityItem);
		    }
    }
	
	public static void ifHarvestable(BlockState state, Player player, Consumer<CropCondition> consumer) 
    {
		ResourceLocation rLoc = HarvestUtils.getRegistryName(state.getBlock());
		
    	if (!CropConfig.hasCropData(rLoc))
    		return;
    	
    	for (CropCondition condition : CropConfig.getCropConditions(rLoc))
    	{
    		if (condition.isHarvestable(state, player))
    		{
    			if (consumer != null)
    				consumer.accept(condition);

				// We break when executed once
    			break;
    		}
    	}
    }
}
