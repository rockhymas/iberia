package com.gibraltar.iberia.feature;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraft.entity.item.EntityArmorStand;

public class ArmorStandElytraRenderFeature {	
    public static void init() {
        RenderingRegistry.registerEntityRenderingHandler(EntityArmorStand.class, new RenderArmorStandFactory());
    }
}