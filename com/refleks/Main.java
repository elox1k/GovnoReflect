package com.refleks;

import java.lang.reflect.Field;
import java.util.List;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = "flightmod", name = "Flight Mod", version = "1.0")
public class Main {

    @Mod.Instance("flightmod")
    public static Main instance;
    
    private boolean isFlying = false;
    private List<Module> modules;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // Используем рефлексию для получения списка модулей
        try {
            Class<?> clazz = Class.forName("com.refleks.ModuleRegistrator");
            Field modulesField = clazz.getDeclaredField("modules");
            modulesField.setAccessible(true);
            modules = (List<Module>) modulesField.get(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch(NoSuchFieldException e) {
        	e.printStackTrace();
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // Регистрируем мод в игре
        MinecraftForge.EVENT_BUS.register(new ForgeEvents());
        FMLCommonHandler.instance().bus().register(new ForgeEvents());
    }
}
