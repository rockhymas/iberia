package com.gibraltar.iberia.feature;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.item.EntityArmorStand;
import org.lwjgl.input.Keyboard;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.EnumHand;

public class QuickArmorSwapFeature {
	public static void init() {
		MinecraftForge.EVENT_BUS.register(new QuickArmorSwapFeature());
	}

    private final EntityEquipmentSlot[] slotsToSwap;

    public QuickArmorSwapFeature() {
        slotsToSwap = new EntityEquipmentSlot[5];
        slotsToSwap[0] = EntityEquipmentSlot.OFFHAND;
        slotsToSwap[1] = EntityEquipmentSlot.HEAD;
        slotsToSwap[2] = EntityEquipmentSlot.CHEST;
        slotsToSwap[3] = EntityEquipmentSlot.LEGS;
        slotsToSwap[4] = EntityEquipmentSlot.FEET;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
	public void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getTarget() instanceof EntityArmorStand && !event.getTarget().worldObj.isRemote && !event.getEntityPlayer().isSpectator()) {
            EntityArmorStand armorStand = (EntityArmorStand) event.getTarget();
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                event.setCanceled(true);
                EntityPlayer player = event.getEntityPlayer();
                for (EntityEquipmentSlot slot : slotsToSwap) {
                    ItemStack playerItem = player.getItemStackFromSlot(slot);
                    ItemStack armorStandItem = armorStand.getItemStackFromSlot(slot);
                    player.setItemStackToSlot(slot, armorStandItem);
                    armorStand.setItemStackToSlot(slot, playerItem);
                }
            }
            else {
                boolean isSmall = armorStand.isSmall();
                Vec3d vec = event.getLocalPos();
                double d4 = isSmall ? vec.yCoord * 2.0D : vec.yCoord;
                ItemStack stack = event.getItemStack();

                if (stack == null || !(stack.getItem() instanceof ItemElytra) || event.getHand() != EnumHand.MAIN_HAND) {
                    return;
                }

                if (d4 >= 0.9D + (isSmall ? 0.3D : 0.0D) && d4 < 0.9D + (isSmall ? 1.0D : 0.7D)) {
                    if (armorStand.getItemStackFromSlot(EntityEquipmentSlot.CHEST) != null) {
                        return;
                    }

                    event.setCanceled(true);
                    armorStand.setItemStackToSlot(EntityEquipmentSlot.CHEST, stack);
                    event.getEntityPlayer().setItemStackToSlot(EntityEquipmentSlot.MAINHAND, null);
                }
            }
        }
    }
}