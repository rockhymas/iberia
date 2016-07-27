package com.gibraltar.iberia.renderer;

import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.client.renderer.entity.RenderArmorStand;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;

public class RenderArmorStandWithElytra extends RenderArmorStand {
    public RenderArmorStandWithElytra(RenderManager manager) {
        super(manager);
        this.addLayer(new LayerArmorStandElytra(this));
    }
}