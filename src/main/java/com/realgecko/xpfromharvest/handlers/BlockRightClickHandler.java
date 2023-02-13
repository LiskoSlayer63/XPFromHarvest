package com.realgecko.xpfromharvest.handlers;

import com.realgecko.xpfromharvest.HarvestUtils;
import com.realgecko.xpfromharvest.config.HarvestConfig;
import com.realgecko.xpfromharvest.config.data.CropCondition;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Adds easier crop harvesting and replanting with right click and adds XP on
 * successful harvest
 */

public class BlockRightClickHandler 
{
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) 
    {
        if (event.getEntity() == null || event.getEntity().isShiftKeyDown() || event.getLevel().isClientSide())
            return;

        ServerLevel level = (ServerLevel)event.getLevel();
        Player player = event.getEntity();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
		
		HarvestUtils.ifHarvestable(state, player, condition -> 
		{
			if (condition.isRightClickable())
				handleUse(level, block, pos, state, player, condition);
			else if (HarvestConfig.useSimpleHarvest.get())
				handleHarvest(level, block, pos, state, player, condition);
		});
		
		// Curiosity feature
		/*if (HarvestConfig.useCuriosity.get() && event.getEntity().isCrouching())
            player.displayClientMessage(Component.translatable(state.toString()), false);*/
    }
	
	static void handleUse(ServerLevel level, Block block, BlockPos pos, BlockState state, Player player, CropCondition condition) 
    {
    	if (condition.isDropsForced())
    		HarvestUtils.dropItems(level, block, pos, state, player, condition);
    	HarvestUtils.giveExperience(level, block, pos);
    }

    static void handleHarvest(ServerLevel level, Block block, BlockPos pos, BlockState state, Player player, CropCondition condition) 
    {
    	HarvestUtils.dropItems(level, block, pos, state, player, condition);
    	HarvestUtils.giveExperience(level, block, pos);
        
        // Reset block and play sound
        level.setBlockAndUpdate(pos, block.defaultBlockState());
        level.playSound(null, pos, state.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1F, 1F);
    }
}
