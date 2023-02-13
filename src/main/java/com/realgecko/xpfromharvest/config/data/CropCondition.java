package com.realgecko.xpfromharvest.config.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.realgecko.xpfromharvest.HarvestUtils;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class CropCondition 
{
	protected Boolean rightClick;
	protected Boolean forceDrops;
	protected List<String> toolItems;
	protected List<String> toolBlacklist;
	protected List<String> dropBlacklist;
	protected Map<String, Object> tags;

	
	// SETTERS
	
	public CropCondition setRightClickable(boolean rightClick)
	{
		this.rightClick = rightClick;
		return this;
	}
	
	public CropCondition setForceDrops(boolean forceDrops)
	{
		this.forceDrops = forceDrops;
		return this;
	}
	
	public CropCondition setToolItem(Item... tools)
	{
		return setToolItem(Arrays.asList(tools));
	}
	
	public CropCondition setToolItem(Iterable<Item> toolItems)
	{
		this.toolItems = new ArrayList<String>();
		for (Item tool : toolItems)
			this.toolItems.add(HarvestUtils.getRegistryName(tool).toString());
		return this;
	}
	
	public CropCondition setToolBlacklist(Item... tools)
	{
		return setToolBlacklist(Arrays.asList(tools));
	}
	
	public CropCondition setToolBlacklist(Iterable<Item> toolBlacklist)
	{
		this.toolBlacklist = new ArrayList<String>();
		for (Item tool : toolBlacklist)
			this.toolBlacklist.add(HarvestUtils.getRegistryName(tool).toString());
		return this;
	}
	
	public CropCondition setDropBlacklist(Item... items)
	{
		return setDropBlacklist(Arrays.asList(items));
	}
	
	public CropCondition setDropBlacklist(Iterable<Item> dropBlacklist)
	{
		this.dropBlacklist = new ArrayList<String>();
		for (Item item : dropBlacklist)
			this.dropBlacklist.add(HarvestUtils.getRegistryName(item).toString());
		return this;
	}

	public CropCondition setTags(Map<String, Object> tags)
	{
		this.tags = tags;
		return this;
	}
	
	public CropCondition setTag(String name, Object value)
	{
		if (tags == null)
			tags = new HashMap<String, Object>();
		tags.put(name, value);
		return this;
	}
	
	
	// GETTERS
	
	public boolean isRightClickable()
	{
		return rightClick != null ? rightClick : false;
	}
	
	public boolean isDropsForced()
	{
		return forceDrops != null ? forceDrops : false;
	}
	
	public boolean isDropBlacklisted(Item item)
	{
		return dropBlacklist != null && dropBlacklist.contains(HarvestUtils.getRegistryName(item).toString());
	}
	
	public boolean isToolValid(Item tool)
	{
		String registryName = HarvestUtils.getRegistryName(tool).toString();
		
		if (toolBlacklist != null)
			for (String name : toolBlacklist)
				if (registryName.equals(name))
					return false;
		
		if (toolItems == null || toolItems.size() == 0)
			return true;

		for (String name : toolItems)
			if (registryName.equals(name))
				return true;
		
		return false;
	}
	
	public boolean isHarvestable(BlockState state, Player player)
	{
    	Item tool = player.getItemBySlot(EquipmentSlot.MAINHAND).getItem();
    	return isHarvestable(state, tool);
	}
	
	public boolean isHarvestable(BlockState state, Item tool) 
	{
		if (!isToolValid(tool))
			return false;
		
		if (tags != null)
		{
			for (Property<?> prop : state.getProperties())
			{
				if (tags.containsKey(prop.getName())) 
				{
					Object pVal = state.getValue(prop);
					Object tVal = tags.get(prop.getName());
					
					if (!tagMatches(pVal, tVal))
						return false;
				}
			}
		}
		
		return true;
	}
	
	
	// STATIC HELPER
	
	private static boolean tagMatches(Object propValue, Object tagValue)
	{
		if (tagValue instanceof Iterable<?> iterVal)
		{
			for (Object val : iterVal)
				if (tagMatches(propValue, val))
					return true;
			return false;
		}
		
		if (tagValue instanceof Long longVal)
			tagValue = (Integer)longVal.intValue();
		
		return propValue.equals(tagValue);
	}
	
	
	// STATIC CONSTRUCTOR
	
	public static CropCondition create()
	{
		return new CropCondition();
	}
}
