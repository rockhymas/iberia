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
	
	public static void init() {
		compressed_stone = (new BlockCompressedStone()).setHardness(15F).setResistance(10.0F).setUnlocalizedName("compressedstone");
	}

	public static void register() {
		GameRegistry.registerBlock(compressed_stone, compressed_stone.getUnlocalizedName().substring(5));
	}
	
	public static void registerRenders() {
		registerRender(compressed_stone);
	}
	
	public static void registerRender(Block block) {
	}

    protected int updateLCG = (new Random()).nextInt();

	private boolean compressingBlock(Block block)
	{
		return block == Blocks.stone || block == compressed_stone;
	}

	@SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
		if (event.phase == TickEvent.Phase.START || event.side == Side.CLIENT)
			return;

		int i = event.world.getGameRules().getInt("randomTickSpeed");

		if (i > 0)
		{
			Iterator<Chunk> chunkIterator = ((WorldServer)event.world).getPlayerChunkManager().getChunkIterator();
			Iterator<Chunk> iterator = net.minecraftforge.common.ForgeChunkManager.getPersistentChunksIterableFor(event.world, chunkIterator);

			while (iterator.hasNext())
			{
				Chunk chunk = (Chunk)iterator.next();
				int j = chunk.xPosition * 16;
				int k = chunk.zPosition * 16;
				for (ExtendedBlockStorage extendedblockstorage : chunk.getBlockStorageArray())
				{
					if (extendedblockstorage != Chunk.NULL_BLOCK_STORAGE && !extendedblockstorage.isEmpty())
					{
						for (int i1 = 0; i1 < i; ++i1)
						{
							this.updateLCG = this.updateLCG * 3 + 1013904223;
							int j1 = this.updateLCG >> 2;
							int k1 = j1 & 15;
							int l1 = j1 >> 8 & 15;
							int i2 = j1 >> 16 & 15;
							IBlockState iblockstate = extendedblockstorage.get(k1, i2, l1);
							Block block = iblockstate.getBlock();

							if (block == Blocks.stone || block == compressed_stone)
							{
								BlockPos pos = new BlockPos(k1 + j, i2 + extendedblockstorage.getYLocation(), l1 + k);
								boolean compressed = block == compressed_stone;

								boolean shouldBeCompressed = true;
								if (!compressingBlock(event.world.getBlockState(pos.up()).getBlock()) ||
									!compressingBlock(event.world.getBlockState(pos.down()).getBlock()) ||
									!compressingBlock(event.world.getBlockState(pos.north()).getBlock()) ||
									!compressingBlock(event.world.getBlockState(pos.south()).getBlock()) ||
									!compressingBlock(event.world.getBlockState(pos.east()).getBlock()) ||
									!compressingBlock(event.world.getBlockState(pos.west()).getBlock()))
								{
									shouldBeCompressed = false;
								}

								//System.out.println("Position: " + pos + ", compressed: " + compressed + ", should be: " + shouldBeCompressed);
								if (compressed != shouldBeCompressed)
								{
									Block newBlock = shouldBeCompressed ? compressed_stone : Blocks.stone;
									//System.out.println("Making " + block.getUnlocalizedName() + " into " + newBlock.getUnlocalizedName());
									event.world.setBlockState(pos, newBlock.getStateFromMeta(block.getMetaFromState(iblockstate)), 6 /*no block update, no re-render*/);
								}
							}
						}
					}
				}
			}
		}
    }
}
