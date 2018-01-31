/**
 * This class was created by Rock Hymas. It's distributed as
 * part of the Iberia Mod. Get the Source Code in github:
 * https://github.com/rockhymas/iberia
 *
 * Iberia is Open Source and distributed under the
 * CC-BY-NC-SA 4.0 License: https://creativecommons.org/licenses/by-nc-sa/4.0/
 */
package com.gibraltar.iberia.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import com.gibraltar.iberia.Reference;

public class BlockHardStone extends BlockStone {
	public BlockHardStone() {
		super();

        setHardness(1.5F);
        setResistance(10.0F);
        setUnlocalizedName("hardstone");
        setRegistryName(Reference.MOD_PREFIX + "hardstone");
        ForgeRegistries.BLOCKS.register(this);
	}

	@Override
    protected ItemStack getSilkTouchDrop(IBlockState state)
    {
        Item item = Item.getItemFromBlock(Blocks.STONE);

        if (item == null)
        {
            return ItemStack.EMPTY;
        }
        else
        {
            int i = 0;

            if (item.getHasSubtypes())
            {
                i = this.getMetaFromState(state);
            }

            return new ItemStack(item, 1, i);
        }
    }

    public static boolean isCompressingBlock(Block block) {
		return block instanceof BlockStone ||
			block == Blocks.BEDROCK ||
			block == Blocks.DIRT ||
			block == Blocks.SANDSTONE ||
			block == Blocks.RED_SANDSTONE ||
			block == Blocks.STAINED_HARDENED_CLAY ||
			block == Blocks.HARDENED_CLAY ||
			(block instanceof BlockOre);
	}

    public static boolean isSurroundedByCompressingBlocks(World world, BlockPos pos, boolean withinChunk)
	{
		return 
            isCompressingBlock(world.getBlockState(pos.up()).getBlock()) &&
			isCompressingBlock(world.getBlockState(pos.down()).getBlock()) &&
            // Don't check outside of chunk, may make some exposed stone hard, but it cleans itself up quickly
			((pos.getZ() % 16 == 0  && withinChunk) || isCompressingBlock(world.getBlockState(pos.north()).getBlock())) &&
			((pos.getZ() % 16 == 15 && withinChunk) || isCompressingBlock(world.getBlockState(pos.south()).getBlock())) &&
			((pos.getX() % 16 == 15 && withinChunk) || isCompressingBlock(world.getBlockState(pos.east() ).getBlock())) &&
			((pos.getX() % 16 == 0  && withinChunk) || isCompressingBlock(world.getBlockState(pos.west() ).getBlock()));
	}
}
