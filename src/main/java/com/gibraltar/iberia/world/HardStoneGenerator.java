/**
 * This class was created by Rock Hymas. It's distributed as
 * part of the Iberia Mod. Get the Source Code in github:
 * https://github.com/rockhymas/iberia
 *
 * Iberia is Open Source and distributed under the
 * CC-BY-NC-SA 4.0 License: https://creativecommons.org/licenses/by-nc-sa/4.0/
 */
package com.gibraltar.iberia.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.IWorldGenerator;

import com.gibraltar.iberia.blocks.BlockHardStone;
import com.gibraltar.iberia.challenge.StoneChallenge;

public class HardStoneGenerator implements IWorldGenerator {
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if(!(world instanceof WorldServer)) {
            return;
        }

		int x = chunkX * 16;
		int z = chunkZ * 16;

        for (int x1 = x; x1 < x + 16; x1++) {
            for (int z1 = z; z1 < z + 16; z1++) {
                int y = world.getTopSolidOrLiquidBlock(new BlockPos(x1, 0, z1)).getY();
                for (int y1 = y; y1 >= 0; y1--) {
                    BlockPos pos = new BlockPos(x1, y1, z1);
                    IBlockState state = world.getBlockState(pos);
                    Block block = state.getBlock();

                    if (block == Blocks.STONE && BlockHardStone.isSurroundedByCompressingBlocks(world, pos, true)) {
                        IBlockState newBlockState = StoneChallenge.hardStone.getStateFromMeta(block.getMetaFromState(state));
                        world.setBlockState(pos, newBlockState, 20 /*no block update, no observer checks*/);
                    }
                }
            }            
        }
    }
}