/**
 * This class was created by Rock Hymas. It's distributed as
 * part of the Iberia Mod. Get the Source Code in github:
 * https://github.com/rockhymas/iberia
 *
 * Iberia is Open Source and distributed under the
 * CC-BY-NC-SA 4.0 License: https://creativecommons.org/licenses/by-nc-sa/4.0/
 */
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