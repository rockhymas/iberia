package com.gibraltar.iberia.challenge;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.gibraltar.iberia.blocks.BlockHardStone;
import com.gibraltar.iberia.challenge.HardStoneSwitcher;

public class HardStoneChallenge extends Challenge {
	public static Block hard_stone;
	
	public void preInit(FMLPreInitializationEvent event) {
		hard_stone = new BlockHardStone();
		MinecraftForge.EVENT_BUS.register(new HardStoneSwitcher());
	}
}