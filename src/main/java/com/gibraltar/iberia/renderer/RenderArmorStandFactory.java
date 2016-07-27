package com.gibraltar.iberia.renderer;

import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.client.renderer.entity.RenderArmorStand;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;

public class RenderArmorStandFactory implements IRenderFactory<EntityArmorStand> {
    public Render<EntityArmorStand> createRenderFor(RenderManager manager) {
        return new RenderArmorStandWithElytra(manager);
    }
}