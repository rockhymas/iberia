package com.gibraltar.iberia.init;

import com.gibraltar.iberia.blocks.BlockHardStone;
import com.gibraltar.iberia.Reference;

import java.util.Iterator;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;

public class HardStoneFeature {
	public static Block hard_stone;
	
	public static void init() {
		hard_stone = (new BlockHardStone()).setHardness(BlockHardStone.HARDNESS_MULTIPLE * 1.5F).setResistance(10.0F).setUnlocalizedName("hardstone");
		GameRegistry.registerBlock(hard_stone, hard_stone.getUnlocalizedName().substring(5));
		MinecraftForge.EVENT_BUS.register(new HardStoneSwitcher());
	}
}