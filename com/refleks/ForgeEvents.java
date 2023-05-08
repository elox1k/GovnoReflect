package com.refleks;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import scala.Console;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.RenderWorldLastEvent;
public class ForgeEvents {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final boolean[] keybind = new boolean[256];
    private static long sleepTime = 0L;
    public static boolean cheatEnabled = true;
    public static int height, width;

    static {
        try {
            Class<?> clazz = Class.forName("net.minecraftforge.fml.common.FMLCommonHandler");
            Method instanceMethod = clazz.getDeclaredMethod("instance");
            Object instance = instanceMethod.invoke(null);
            Method getClientMethod = clazz.getDeclaredMethod("getClient");
            Object client = getClientMethod.invoke(instance);
            Method registerMethod = client.getClass().getDeclaredMethod("registerKeyBinding", KeyBinding.class);
            registerMethod.invoke(client, new KeyBinding("Key", Keyboard.KEY_F, "Category"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }
        try {
            int key = Keyboard.getEventKey();
            if (Keyboard.getEventKeyState()) {
                ModuleRegistrator.onKeyInput(key);
            }
        } catch (RuntimeException rex) {
            rex.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }
        try {
            ModuleRegistrator.onTick(event);
        } catch (RuntimeException rex) {
            rex.printStackTrace();
        }
    }


    @SubscribeEvent
    public void onRender2D(Text event) {
        height = event.resolution.getScaledHeight();
        width = event.resolution.getScaledWidth();

        //Main.eventManager.call(new EventRender2D(height, width));
    }

    @SubscribeEvent
    public void onUpdate(LivingUpdateEvent event) {
        if (FMLClientHandler.instance().getClient().currentScreen == null) {
            if (0L < System.currentTimeMillis()) {
                if (Keyboard.isKeyDown(29)) {
                    if (this.pressedKey(33)) {
                        if (FMLClientHandler.instance().getClient().objectMouseOver != null && FMLClientHandler.instance().getClient().objectMouseOver.entityHit != null && FMLClientHandler.instance().getClient().objectMouseOver.entityHit instanceof EntityPlayer) {
                            EntityPlayer ep = (EntityPlayer) FMLClientHandler.instance().getClient().objectMouseOver.entityHit;
                            String name = StringUtils.stripControlCodes(ep.getCommandSenderName());
                        }

                        return;
                    }
                }
            }
        }

        if (event.entity == mc.thePlayer) {
            //Main.eventManager.call(new EventUpdate());
        }
    }

    public boolean pressedKey(int key) {
        return FMLClientHandler.instance().getClient().currentScreen == null && Keyboard.isKeyDown(key) != keybind[key] && (keybind[key] = !keybind[key]);
    }
}
