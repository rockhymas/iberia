/**
 * This class was created by Rock Hymas. It's distributed as
 * part of the Iberia Mod. Get the Source Code in github:
 * https://github.com/rockhymas/iberia
 *
 * Iberia is Open Source and distributed under the
 * CC-BY-NC-SA 4.0 License: https://creativecommons.org/licenses/by-nc-sa/4.0/
 */
package com.gibraltar.iberia.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.fml.common.FMLLog;

import com.gibraltar.iberia.Reference;

public class IberiaSpawnData extends WorldSavedData {
    private int spawnX;
    private int spawnY;
    private int spawnZ;

    public IberiaSpawnData(String dataIdentifier) {
        super(dataIdentifier);
    }

    public void readFromNBT(NBTTagCompound nbt) {
        spawnX = nbt.getInteger("SpawnX");
        spawnY = nbt.getInteger("SpawnY");
        spawnZ = nbt.getInteger("SpawnZ");
        FMLLog.info("reading iberia world spawn data: " + getSpawn());
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        FMLLog.info("writing iberia world spawn data");
        nbt.setInteger("SpawnX", spawnX);
        nbt.setInteger("SpawnY", spawnY);
        nbt.setInteger("SpawnZ", spawnZ);
        return nbt;
    }

    public BlockPos getSpawn() {
        return new BlockPos(spawnX, spawnY, spawnZ);
    }

    public void setSpawn(BlockPos spawn) {
        spawnX = spawn.getX();
        spawnY = spawn.getY();
        spawnZ = spawn.getZ();
        markDirty();
    }

    public static IberiaSpawnData get(World world) {
        MapStorage storage = world.getMapStorage();
        IberiaSpawnData instance = (IberiaSpawnData) storage.getOrLoadData(IberiaSpawnData.class, Reference.MODID);

        if (instance == null) {
            FMLLog.info("creating iberia world spawn data");
            instance = new IberiaSpawnData(Reference.MODID);
            BlockPos spawnPoint = world.getSpawnPoint();
            instance.setSpawn(spawnPoint);
            storage.setData(Reference.MODID, instance);
        }

        return instance;
    }
}