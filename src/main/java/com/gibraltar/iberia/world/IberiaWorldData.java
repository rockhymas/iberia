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

public class IberiaWorldData extends WorldSavedData {
    private int spawnX;
    private int spawnY;
    private int spawnZ;
    private long lastSleepTime;

    public IberiaWorldData(String dataIdentifier) {
        super(dataIdentifier);
    }

    public void readFromNBT(NBTTagCompound nbt) {
        spawnX = nbt.getInteger("SpawnX");
        spawnY = nbt.getInteger("SpawnY");
        spawnZ = nbt.getInteger("SpawnZ");
        FMLLog.info("reading iberia world spawn data: " + getSpawn());

        if (nbt.hasKey("lastSleepTime", 99)) {
            lastSleepTime = nbt.getLong("lastSleepTime");
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        FMLLog.info("writing iberia world spawn data");
        nbt.setInteger("SpawnX", spawnX);
        nbt.setInteger("SpawnY", spawnY);
        nbt.setInteger("SpawnZ", spawnZ);
        nbt.setLong("lastSleepTime", lastSleepTime);
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

    public long getLastSleepTime() {
        return lastSleepTime;
    }

    public void setLastSleepTime(long lastSleepTime) {
        this.lastSleepTime = lastSleepTime;
        markDirty();
    }

    public static IberiaWorldData get(World world) {
        MapStorage storage = world.getMapStorage();
        IberiaWorldData instance = (IberiaWorldData) storage.getOrLoadData(IberiaWorldData.class, Reference.MODID);

        if (instance == null) {
            FMLLog.info("creating iberia world spawn data");
            instance = new IberiaWorldData(Reference.MODID);
            BlockPos spawnPoint = world.getSpawnPoint();
            instance.setSpawn(spawnPoint);
            instance.setLastSleepTime(0);
            storage.setData(Reference.MODID, instance);
        }

        return instance;
    }
}