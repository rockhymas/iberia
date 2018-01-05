/**
 * This class was created by Rock Hymas. It's distributed as
 * part of the Iberia Mod. Get the Source Code in github:
 * https://github.com/rockhymas/iberia
 *
 * Iberia is Open Source and distributed under the
 * CC-BY-NC-SA 4.0 License: https://creativecommons.org/licenses/by-nc-sa/4.0/
 */
package com.gibraltar.iberia.challenge;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.config.ConfigCategory; 
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import com.gibraltar.iberia.blocks.BlockHardStone;
import com.gibraltar.iberia.world.HardStoneGenerator;



public class StoneChallenge extends Challenge {
	public static Block hard_stone;
	public static boolean slowdown_enabled;
	Map<String, Float> slowdowns;
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		hard_stone = new BlockHardStone();

		GameRegistry.registerWorldGenerator(new HardStoneGenerator(), 0);
	}

	@Override
	public void loadConfig(Configuration config) {
		super.loadConfig(config);

		Property prop = config.get(name, "WoodSlowdown", 10.0D);
        prop = config.get(name, "StoneSlowdown", 10.0D);
        prop = config.get(name, "IronSlowdown", 10.0D);
        prop = config.get(name, "GoldSlowdown", 1.0D);
        prop = config.get(name, "DiamondSlowdown", 2.0D);
        prop = config.get(name, "SlowdownEnabled", true);
        slowdown_enabled = prop.getBoolean(); 

		slowdowns = new TreeMap<String, Float>();
		ConfigCategory category = config.getCategory(name);
		for (Map.Entry<String, Property> entry : category.getValues().entrySet()) {
			String key = entry.getKey().trim().toLowerCase();
			if (!key.endsWith("slowdown")) {
				continue;
			}
			key = key.substring(0, key.length() - 8);
			slowdowns.put(key, (float)entry.getValue().getDouble(10.0D));
		}
	}

	@Override
	protected boolean hasSubscriptions() {
		return true;
	}

    protected int updateLCG = (new Random()).nextInt();

	@SubscribeEvent
	public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
		if (event.getState().getBlock() != StoneChallenge.hard_stone || slowdown_enabled == false) {
			return;
		}

		ItemStack heldItemStack = event.getEntityPlayer().getHeldItemMainhand();
		if (heldItemStack == null || !(heldItemStack.getItem() instanceof ItemPickaxe)) {
			return;
		}

		ItemPickaxe pickaxe = (ItemPickaxe)heldItemStack.getItem();
		String material = pickaxe.getToolMaterial().toString().toLowerCase();
		float slowdown = 10.0F;
		if (slowdowns.containsKey(material)) {
			slowdown = slowdowns.get(material);
		}
		else {
			FMLLog.info("[Iberia] New pickaxe type: %s", pickaxe.getToolMaterial());
		}

		event.setNewSpeed(event.getOriginalSpeed() / slowdown);
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

							if (block == Blocks.STONE || block == StoneChallenge.hard_stone)
							{
								BlockPos pos = new BlockPos(k1 + j, i2 + extendedblockstorage.getYLocation(), l1 + k);
								boolean hard = block == StoneChallenge.hard_stone;
								boolean shouldBeHard = BlockHardStone.isSurroundedByCompressingBlocks(event.world, pos, false);

								if (hard != shouldBeHard)
								{
									Block newBlock = shouldBeHard ? StoneChallenge.hard_stone : Blocks.STONE;
									event.world.setBlockState(pos, newBlock.getStateFromMeta(block.getMetaFromState(iblockstate)), 6 /*no block update, no re-render*/);
									if (!shouldBeHard) {
										event.world.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_CHORUS_FLOWER_DEATH, SoundCategory.BLOCKS, 1.0F, 1.0F);
									}
								}
							}
						}
					}
				}
			}
		}
    }
}