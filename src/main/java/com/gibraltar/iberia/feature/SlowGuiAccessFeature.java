package com.gibraltar.iberia.feature;

import com.gibraltar.iberia.blocks.BlockHardStone;
import com.gibraltar.iberia.Reference;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.item.ItemBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.client.event.GuiScreenEvent;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor;

public class SlowGuiAccessFeature {
	private long timeGuiOpened;
	private long armorDelayMs;

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new SlowGuiAccessFeature());
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
	    if (event.getGui() instanceof GuiContainer && !(event.getGui() instanceof InventoryEffectRenderer)) {
			timeGuiOpened = Minecraft.getSystemTime();
			Iterable<ItemStack> armorInventory = Minecraft.getMinecraft().thePlayer.getArmorInventoryList();
			armorDelayMs = 0;

			for (Object item : armorInventory)
            {
                ItemStack stack = (ItemStack) item;
                if (stack == null)
                {
                    continue;
                }
				if (stack.getItem() instanceof ItemArmor)
                {
                    ItemArmor armor = (ItemArmor)stack.getItem();
					switch (armor.getArmorMaterial())
					{
						case LEATHER:
							armorDelayMs += 200;
							break;
						case IRON:
							armorDelayMs += 600;
							break;
						case CHAIN:
							armorDelayMs += 600;
							break;
						case GOLD:
							armorDelayMs += 200;
							break;
						case DIAMOND:
							armorDelayMs += 1200;
							break;

					}
				}
			}
	    }
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
	    if (event.getGui() instanceof GuiContainer && !(event.getGui() instanceof InventoryEffectRenderer)) {
			if (timeGuiOpened + armorDelayMs > Minecraft.getSystemTime()) {
				double visibility = (Minecraft.getSystemTime() - timeGuiOpened) / (double)armorDelayMs;
				int alpha = (int)Math.round(64.0D - visibility * 64.0D);

				GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableTexture2D();
				GlStateManager.enableBlend();

				Tessellator tessellator = Tessellator.getInstance();
                VertexBuffer vertexbuffer = tessellator.getBuffer();
				int guiWidth = event.getGui().width;
				int guiHeight = event.getGui().height;
                int guiLeft = (guiWidth - 176) / 2;
				int guiTop = (guiHeight - 166) / 2;
				this.draw(vertexbuffer, guiLeft, guiTop, 176, 166, 255, 255, 255, 128);
				this.draw(vertexbuffer, guiLeft+80, guiTop+80, 16, 2, 0, 0, 0, 128);
				this.draw(vertexbuffer, guiLeft+80, guiTop+80, (int)Math.round(16.0D * visibility), 2, 255, 255, 255, 255);

                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();				
			}
	    }
	}

    private void draw(VertexBuffer renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha)
    {
        renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        renderer.pos((double)(x + 0), (double)(y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos((double)(x + 0), (double)(y + height), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos((double)(x + width), (double)(y + height), 0.0D).color(red, green, blue, alpha).endVertex();
        renderer.pos((double)(x + width), (double)(y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
        Tessellator.getInstance().draw();
    }

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) {
	    if (event.getGui() instanceof GuiContainer && !(event.getGui() instanceof InventoryEffectRenderer)) {
			if (timeGuiOpened + armorDelayMs > Minecraft.getSystemTime()) {
				event.setCanceled(true);
			}
	    }
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onKeyboardInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
	    if (event.getGui() instanceof GuiContainer && !(event.getGui() instanceof InventoryEffectRenderer)) {
			if (timeGuiOpened + armorDelayMs > Minecraft.getSystemTime() && (Keyboard.getEventKey() != 1)) {
				event.setCanceled(true);
			}
	    }
	}
}