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
                    System.out.println("slot " + slot);
                    ItemStack playerItem = player.getItemStackFromSlot(slot);
                    ItemStack armorStandItem = armorStand.getItemStackFromSlot(slot);
                    System.out.println("player " + playerItem);
                    System.out.println("armor stand " + armorStandItem);
                    player.setItemStackToSlot(slot, armorStandItem);
                    armorStand.setItemStackToSlot(slot, playerItem);
                }
            }
        }
    }
}