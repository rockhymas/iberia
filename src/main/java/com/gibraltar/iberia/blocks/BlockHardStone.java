/**
 * This class was created by Rock Hymas. It's distributed as
 * part of the Iberia Mod. Get the Source Code in github:
 * https://github.com/rockhymas/iberia
 *
 * Iberia is Open Source and distributed under the
 * CC-BY-NC-SA 4.0 License: https://creativecommons.org/licenses/by-nc-sa/4.0/
 */
package com.gibraltar.iberia.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockStone;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import com.gibraltar.iberia.Reference;

public class BlockHardStone extends Block {

    public static Block constructingBaseBlock;
    public static BlockHardStone create(Block baseBlock) {

		BlockHardStone.constructingBaseBlock = baseBlock;
		BlockHardStone hard_stone = new BlockHardStone(baseBlock);
		BlockHardStone.constructingBaseBlock = null;
        return hard_stone;
    }


    private Block baseBlock;
    private Block getBaseBlock() {
        return baseBlock == null ? constructingBaseBlock : baseBlock;
    }

    private IProperty getVariantProperty() {
        return getBaseBlock().getBlockState().getProperty("variant");
    }

	public BlockHardStone(Block baseBlock) {
		super(baseBlock.getMaterial(null));
        this.baseBlock = baseBlock;

        this.setDefaultState(this.blockState.getBaseState().withProperty(getVariantProperty(), getBaseBlock().getDefaultState().getValue(getVariantProperty())));

        setHardness(baseBlock.getBlockHardness(null, null, null));
        try {
            setResistance((float)ReflectionHelper.findField(baseBlock.getClass(), new String[] { "w", "field_149781_w", "blockResistance" }).get(baseBlock));            
        } catch (Exception e) {
        }
        setUnlocalizedName("hardstone");
        setRegistryName(Reference.MOD_PREFIX + "hard" + baseBlock.getUnlocalizedName());
		GameRegistry.register(this);
	}

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getVariantProperty());
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return ((Enum)state.getValue(getVariantProperty())).ordinal();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        Enum value = (Enum)getBaseBlock().getStateFromMeta(meta).getValue(getVariantProperty());
        return getDefaultState().withProperty(getVariantProperty(), value);
    }

	@Override
    protected ItemStack getSilkTouchDrop(IBlockState state)
    {
        Item item = Item.getItemFromBlock(baseBlock);

        if (item == null)
        {
            return null;
        }
        else
        {
            int i = 0;

            if (item.getHasSubtypes())
            {
                i = baseBlock.getMetaFromState(state);
            }

            return new ItemStack(item, 1, i);
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return baseBlock.getItemDropped(baseBlock.getStateFromMeta(getMetaFromState(state)), rand, fortune);
    }

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}
    
    public static boolean isCompressingBlock(Block block) {
		return block instanceof BlockHardStone ||
            block == Blocks.STONE ||
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
