package com.gibraltar.iberia.challenge;

import java.util.Iterator;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import com.gibraltar.iberia.blocks.BlockHardStone;
import com.gibraltar.iberia.challenge.HardStoneChallenge;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class HardStoneChallenge extends Challenge {
	public static Block hard_stone;
	private float woodSlowdown;
	private float stoneSlowdown;
	private float ironSlowdown;
	private float goldSlowdown;
	private float diamondSlowdown;	
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		hard_stone = new BlockHardStone();
	}

	@Override
	public void loadConfig(Configuration config) {
		super.loadConfig(config);

		Property prop = config.get(name, "WoodSlowdown", 10.0D);
        woodSlowdown = (float)prop.getDouble(10.0D);
        prop = config.get(name, "StoneSlowdown", 10.0D);
		stoneSlowdown = (float)prop.getDouble(10.0D);
        prop = config.get(name, "IronSlowdown", 10.0D);
		ironSlowdown = (float)prop.getDouble(10.0D);
        prop = config.get(name, "GoldSlowdown", 1.0D);
		goldSlowdown = (float)prop.getDouble(1.0D);
        prop = config.get(name, "DiamondSlowdown", 1.0D);
		diamondSlowdown = (float)prop.getDouble(1.0D);
	}

	@Override
	protected boolean hasSubscriptions() {
		return true;
	}

    protected int updateLCG = (new Random()).nextInt();

	@SubscribeEvent
	public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		if (event.getState().getBlock() != HardStoneChallenge.hard_stone) {
			return;
		}

		ItemStack heldItemStack = event.getEntityPlayer().getHeldItemMainhand();
		if (heldItemStack == null || !(heldItemStack.getItem() instanceof ItemPickaxe)) {
			return;
		}

		ItemPickaxe pickaxe = (ItemPickaxe)heldItemStack.getItem();
		switch (pickaxe.getToolMaterial()) {
			case WOOD:
				event.setNewSpeed(event.getOriginalSpeed() / woodSlowdown);
				break;
			case STONE:
				event.setNewSpeed(event.getOriginalSpeed() / stoneSlowdown);
				break;
			case IRON:
				event.setNewSpeed(event.getOriginalSpeed() / ironSlowdown);
				break;
			case GOLD:
				event.setNewSpeed(event.getOriginalSpeed() / goldSlowdown);
				break;
			case DIAMOND:
				event.setNewSpeed(event.getOriginalSpeed() / diamondSlowdown);
				break;					
		}
	}

	private boolean isCompressingBlock(Block block) {
		return block == Blocks.STONE ||
			block == HardStoneChallenge.hard_stone ||
			block == Blocks.BEDROCK ||
			block == Blocks.DIRT ||
			block == Blocks.SANDSTONE ||
			block == Blocks.RED_SANDSTONE ||
			block == Blocks.STAINED_HARDENED_CLAY ||
			block == Blocks.HARDENED_CLAY ||
			(block instanceof BlockOre);
	}

	private boolean isSurroundedByCompressingBlocks(World world, BlockPos pos)
	{
		return isCompressingBlock(world.getBlockState(pos.up()).getBlock()) &&
			isCompressingBlock(world.getBlockState(pos.down()).getBlock()) &&
			isCompressingBlock(world.getBlockState(pos.north()).getBlock()) &&
			isCompressingBlock(world.getBlockState(pos.south()).getBlock()) &&
			isCompressingBlock(world.getBlockState(pos.east()).getBlock()) &&
			isCompressingBlock(world.getBlockState(pos.west()).getBlock());
	}


	@SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
		if (event.phase == TickEvent.Phase.START || event.side == Side.CLIENT)
			return;

		int i = event.world.getGameRules().getInt("randomTickSpeed");

		if (i > 0)
		{
			Iterator<Chunk> chunkIterator = ((WorldServer)event.world).getPlayerChunkMap().getChunkIterator();
			Iterator<Chunk> iterator = net.minecraftforge.common.ForgeChunkManager.getPersistentChunksIterableFor(event.world, chunkIterator);

			// The following loop mimics the built in random tick loop to provide random ticks for stone and hard stone blocks
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

							if (block == Blocks.STONE || block == HardStoneChallenge.hard_stone)
							{
								BlockPos pos = new BlockPos(k1 + j, i2 + extendedblockstorage.getYLocation(), l1 + k);
								boolean hard = block == HardStoneChallenge.hard_stone;
								boolean shouldBeHard = isSurroundedByCompressingBlocks(event.world, pos);

								if (hard != shouldBeHard)
								{
									Block newBlock = shouldBeHard ? HardStoneChallenge.hard_stone : Blocks.STONE;
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