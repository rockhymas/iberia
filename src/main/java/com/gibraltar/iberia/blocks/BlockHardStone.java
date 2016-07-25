package com.gibraltar.iberia.blocks;

import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.gibraltar.iberia.Reference;

public class BlockHardStone extends BlockStone {
    public final static float HARDNESS_MULTIPLE = 10F;

	public BlockHardStone() {
		super();

        setHardness(BlockHardStone.HARDNESS_MULTIPLE * 1.5F);
        setResistance(10.0F);
        setUnlocalizedName("hardstone");
        setRegistryName(Reference.MOD_PREFIX + "hardstone");
		GameRegistry.register(this);
	}

	@Override
    protected ItemStack createStackedBlock(IBlockState state)
    {
        Item item = Item.getItemFromBlock(Blocks.STONE);

        if (item == null)
        {
            return null;
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

}
