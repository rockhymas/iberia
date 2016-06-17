package com.gibraltar.iberia.init;

import com.gibraltar.iberia.Reference;
import com.gibraltar.iberia.blocks.BlockCompressedStone;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.init.Blocks;

public class blocks {
	public static Block compressed_stone;
	//public static Block exposed_stone;
	
	public static void init() {
		//exposed_stone = (new BlockStone()).setHardness(1.5F).setResistance(10.0F).setUnlocalizedName("exposedstone");
		compressed_stone = (new BlockCompressedStone()).setHardness(15F).setResistance(10.0F).setUnlocalizedName("compressedstone");
	}

	public static void register() {
		//GameRegistry.registerBlock(exposed_stone, exposed_stone.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(compressed_stone, compressed_stone.getUnlocalizedName().substring(5));
	}
	
	public static void registerRenders() {
		//registerRender(exposed_stone);
		registerRender(compressed_stone);
	}
	
	public static void registerRender(Block block) {
		Item item = Item.getItemFromBlock(block);
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(Reference.MODID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
	}

	@SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        System.out.println("DIRT BLOCK >> " + Blocks.dirt.getUnlocalizedName());
    }
}
