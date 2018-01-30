/**
 * This class was created by Rock Hymas based on code in
 * Quark written by Vascos Lavos. It's distributed as
 * part of the Iberia Mod. Get the Source Code in github:
 * https://github.com/rockhymas/iberia
 *
 * Iberia is Open Source and distributed under the
 * CC-BY-NC-SA 4.0 License: https://creativecommons.org/licenses/by-nc-sa/4.0/
 */
package com.gibraltar.iberia.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import com.gibraltar.iberia.Reference;
import com.gibraltar.iberia.proxy.CommonProxy;

public class GuiFactory implements IModGuiFactory {

	@Override
	public void initialize(Minecraft instance) {
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return Collections.emptySet();
	}

	public static class IberiaGuiConfig extends GuiConfig {

		public IberiaGuiConfig(GuiScreen parentScreen) {
			super(parentScreen, getAllElements(), Reference.MODID, false, false, GuiConfig.getAbridgedConfigPath(CommonProxy.config.toString()));
		}

		public static List<IConfigElement> getAllElements() {
			List<IConfigElement> list = new ArrayList();

			Set<String> categories = CommonProxy.config.getCategoryNames();
			for(String s : categories)
				if(!s.contains("."))
					list.add(new DummyConfigElement.DummyCategoryElement(s, s, new ConfigElement(CommonProxy.config.getCategory(s)).getChildElements()));

			return list;
		}

	}

	@Override
	public boolean hasConfigGui() {
		return true;
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		return new IberiaGuiConfig(parentScreen);
	}

}