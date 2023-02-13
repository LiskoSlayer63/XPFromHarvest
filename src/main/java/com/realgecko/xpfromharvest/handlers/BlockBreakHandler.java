package com.realgecko.xpfromharvest.handlers;

import com.realgecko.xpfromharvest.HarvestUtils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Handles harvest attempts with left click (block breaking)
 */

public class BlockBreakHandler 
{
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) 
    {
        if (event.getPlayer() == null || event.getLevel().isClientSide())
            return;

        Player player = event.getPlayer();
        BlockState state = event.getLevel().getBlockState(event.getPos());
        Block block = state.getBlock();
        
        HarvestUtils.ifHarvestable(state, player, condition ->
        {
        	if (!condition.isRightClickable())
    			HarvestUtils.giveExperience((ServerLevel) event.getLevel(), block, event.getPos());
        });
    }
}
