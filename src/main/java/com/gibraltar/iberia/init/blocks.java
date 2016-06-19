package com.gibraltar.iberia.init;

import com.gibraltar.iberia.Reference;
import com.gibraltar.iberia.blocks.BlockCompressedStone;

import java.util.Iterator;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.WorldServer;
import net.minecraft.block.state.IBlockState;
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

    protected int updateLCG = (new Random()).nextInt();

	// public static final IUnlistedProperty<boolboolean> CompressedProperty = new IUnlistedProperty<boolean>()
	// {
    //     public String getName() { return "iberia_compressed"; }
    //     public boolean isValid(boolean state) { return true; }
    //     public Class<boolean> getType() { return boolean.class; }
    //     public String valueToString(boolean state) { return state.toString(); }
    // };

	@SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
		if (event.phase == TickEvent.Phase.START || event.side == Side.CLIENT)
			return;

		// int i = event.world.getGameRules().getInt("randomTickSpeed");

		// if (i > 0)
		// {
		// 	Iterator<Chunk> chunkIterator = ((WorldServer)event.world).getPlayerChunkManager().getChunkIterator();
		// 	Iterator<Chunk> iterator = net.minecraftforge.common.ForgeChunkManager.getPersistentChunksIterableFor(event.world, chunkIterator);

		// 	while (iterator.hasNext())
		// 	{
		// 		Chunk chunk = (Chunk)iterator.next();
		// 		int j = chunk.xPosition * 16;
		// 		int k = chunk.zPosition * 16;
		// 		for (ExtendedBlockStorage extendedblockstorage : chunk.getBlockStorageArray())
		// 		{
		// 			if (extendedblockstorage != Chunk.NULL_BLOCK_STORAGE)
		// 			{
		// 				for (int i1 = 0; i1 < i; ++i1)
		// 				{
		// 					this.updateLCG = this.updateLCG * 3 + 1013904223;
		// 					int j1 = this.updateLCG >> 2;
		// 					int k1 = j1 & 15;
		// 					int l1 = j1 >> 8 & 15;
		// 					int i2 = j1 >> 16 & 15;
		// 					System.out.println("k1 >> " + k1 + ", i2 >> " + i2 + ", l1 >> " + l1);
		// 					IBlockState iblockstate = extendedblockstorage.get(k1, i2, l1);
		// 					Block block = iblockstate.getBlock();

		// 					if (block == Blocks.stone)
		// 					{
		// 						BlockPos pos = new BlockPos(k1 + j, i2 + extendedblockstorage.getYLocation(), l1 + k);
		// 						System.out.println("Position >> " + pos);
		// 						// Get compressed value
		// 						bool compressed = false;
		// 						if (iblockstate instanceof IExtendedBlockState)
		// 						{
		// 							if(((IExtendedBlockState)iblockstate).getUnlistedNames().contains(CompressedProperty))
		// 							{
		// 								compressed = ((IExtendedBlockState)iblockstate).getValue(CompressedProperty);
		// 							}
		// 						}

		// 						bool shouldBeCompressed = true;
		// 						if (extendedblockstorage.get(k1-1, i2, l1).getBlock() != Blocks.stone ||
		// 							extendedblockstorage.get(k1+1, i2, l1).getBlock() != Blocks.stone ||
		// 							extendedblockstorage.get(k1, i2-1, l1).getBlock() != Blocks.stone ||
		// 							extendedblockstorage.get(k1, i2+1, l1).getBlock() != Blocks.stone ||
		// 							extendedblockstorage.get(k1, i2, l1-1).getBlock() != Blocks.stone ||
		// 							extendedblockstorage.get(k1, i2, l1+1).getBlock() != Blocks.stone)
		// 						{
		// 							shouldBeCompressed = false;
		// 						}

		// 						// if (compressed != shouldBeCompressed)
		// 						// {
		// 						// 	extendedblockstorage.set(k1, i2, l1, )
		// 						// }
		// 						//block.randomTick(this, new BlockPos(k1 + j, i2 + extendedblockstorage.getYLocation(), l1 + k), iblockstate, this.rand);
		// 						// Update extended properties based on surrounding blocks and random value
		// 					}
		// 				}
		// 			}
		// 		}
		// 	}
		// }
    }
}
