/**
 * This class was created by Rock Hymas. It's distributed as
 * part of the Iberia Mod. Get the Source Code in github:
 * https://github.com/rockhymas/iberia
 *
 * Iberia is Open Source and distributed under the
 * CC-BY-NC-SA 4.0 License: https://creativecommons.org/licenses/by-nc-sa/4.0/
 */
package com.gibraltar.iberia.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import javax.annotation.Nullable;

import com.gibraltar.iberia.network.MessageRegistry;
import com.gibraltar.iberia.network.MessageGetPlayerSpawn;
import com.gibraltar.iberia.Reference;

public class ItemPersonalCompass extends Item {

    private class WobbleData {
        private double rotation;
        private double rota;
        private long lastUpdateTick;
    }

    public ItemPersonalCompass() {
        addPropertyOverride(new ResourceLocation("angle"), new IItemPropertyGetter() {
            @SideOnly(Side.CLIENT)
            HashMap<BlockPos, WobbleData> wobbleData;

            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                if (entityIn == null && !stack.isOnItemFrame()) {
                    return 0.0F;
                }
                else {
                    boolean flag = entityIn != null;
                    Entity entity = flag ? entityIn : stack.getItemFrame();

                    if (worldIn == null) {
                        worldIn = entity.world;
                    }

                    double d0;
                    BlockPos destinationPos = getCompassSpawn(stack);
                    if (destinationPos == null) {
                        MessageRegistry.network.sendToServer(new MessageGetPlayerSpawn());
                        return 0F;
                    }

                    if (worldIn.provider.isSurfaceWorld()) {
                    	double d1 = flag ? (double) entity.rotationYaw : getFrameRotation((EntityItemFrame) entity);
                    	d1 = d1 % 360.0D;
                    	double d2 = getPosToAngle(worldIn, entity, destinationPos);
                    	d0 = Math.PI - ((d1 - 90.0D) * 0.01745329238474369D - d2);
                    }
                    else {
                        d0 = (Math.random() - 0.5D) * 2D * Math.PI;
                        flag = false;
                    }

                    if (flag) {
                        d0 = wobble(worldIn, d0, destinationPos);
                    }

                    float f = (float) (d0 / (Math.PI * 2D));
                    return MathHelper.positiveModulo(f, 1.0F);
                }
            }

            @SideOnly(Side.CLIENT)
            private double wobble(World world, double angle, BlockPos destinationPos) {
                if (wobbleData == null) {
                    wobbleData = new HashMap<>();
                }

                WobbleData w;
                if (wobbleData.containsKey(destinationPos)) {
                    w = wobbleData.get(destinationPos);
                }
                else {
                    w = new WobbleData();
                    wobbleData.put(destinationPos, w);
                }

                if (world.getTotalWorldTime() != w.lastUpdateTick) {
                    w.lastUpdateTick = world.getTotalWorldTime();
                    double d0 = angle - w.rotation;
                    d0 = d0 % (Math.PI * 2D);
                    d0 = MathHelper.clamp(d0, -1.0D, 1.0D);
                    w.rota += d0 * 0.1D;
                    w.rota *= 0.8D;
                    w.rotation += w.rota;
                }

                return w.rotation;
            }

            @SideOnly(Side.CLIENT)
            private double getFrameRotation(EntityItemFrame itemFrame) {
                return (double) MathHelper.wrapDegrees(180 + itemFrame.facingDirection.getHorizontalIndex() * 90);
            }

            @SideOnly(Side.CLIENT)
            private double getPosToAngle(World world, Entity entity, BlockPos blockpos) {
                return Math.atan2((double) blockpos.getZ() - entity.posZ, (double) blockpos.getX() - entity.posX);
            }
        });

        setUnlocalizedName("compass_personal");
        setCreativeTab(CreativeTabs.TOOLS);
        setRegistryName(Reference.MOD_PREFIX + "compass_personal");
        ForgeRegistries.ITEMS.register(this);
    }

    public static void setCompassSpawn(ItemStack stack, int x, int z) {
		if (verifyNBT(stack) && !stack.getTagCompound().getBoolean("SpawnSet")) {
            FMLLog.info("setting spawn for compass: " + x + ", " + z);
            stack.getTagCompound().setBoolean("SpawnSet", true);
			stack.getTagCompound().setInteger("SpawnX", x);
			stack.getTagCompound().setInteger("SpawnZ", z);
		}
	}

    public static BlockPos getCompassSpawn(ItemStack stack) {
		if (verifyNBT(stack) && stack.getTagCompound().hasKey("SpawnSet")) {
            return new BlockPos(stack.getTagCompound().getInteger("SpawnX"), 0, stack.getTagCompound().getInteger("SpawnZ"));
		}

        return null;
	}

    public static boolean verifyNBT(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
            return false;
        }
        if (!(stack.getItem() instanceof ItemPersonalCompass)) {
			return false;
		} else if (stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
		}

		return true;
	}
}