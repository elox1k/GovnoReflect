package com.refleks;
import java.lang.reflect.Method;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import org.lwjgl.input.Keyboard;
import net.minecraft.network.NetHandlerPlayServer;
import cpw.mods.fml.common.gameevent.TickEvent;

public abstract class Module {
    private String name;
    private int bind;
    private boolean isEnabled;
    private boolean toggled;
    private long lastEnableTime;
    private long lastDisableTime;
    protected Category category;
    protected Minecraft mc = Minecraft.getMinecraft();
    protected boolean state;

    public Module(String name, int bind, Category category) {
        this.name = name;
        this.bind = bind;
        this.isEnabled = false;
        this.toggled = false;
        this.category = category;

        this.settings();
    }

    public void toggle() {
        if (this.toggled) {
            long i = System.currentTimeMillis() - this.lastEnableTime;
            this.lastDisableTime = System.currentTimeMillis() - (i < 300L ? 300L - i : 0L);
            this.toggled = false;
            this.isEnabled = false;
            this.onDisable();
        } else {
            long j = System.currentTimeMillis() - this.lastDisableTime;
            this.lastEnableTime = System.currentTimeMillis() - (j < 300L ? 300L - j : 0L);
            this.toggled = true;
            this.isEnabled = true;
            this.onEnable();
        }
    }

    public abstract void settings();

    public abstract void onEnable();

    public abstract void onDisable();

    public abstract void onTick(TickEvent.ClientTickEvent event);

    public void onRenderText(RenderGameOverlayEvent.Text event) {}

    public void onRender() {}

    public void onUpdate(LivingUpdateEvent event) {}

    public void onRender3D(RenderWorldLastEvent event) {}

    public void onRender2D(RenderGameOverlayEvent.Pre event) {}

    public void onWorldRender(RenderWorldLastEvent event) {}

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getToggled() {
        return this.toggled;
    }

    public int getBind() {
        return bind;
    }

    public void setBind(int bind) {
        this.bind = bind;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public void toggleModule() {
        this.isEnabled = (!this.isEnabled);
        onToggle(this.isEnabled);
    }
    private void onToggle(boolean enabled) {
        if (enabled)
            onEnable();

        if (!enabled)
            onDisable();

    }
    public void reset() {
        this.onEnable();
        this.onDisable();
    }

    public boolean doSendPacket(Packet packet) {
        return true;
    }
	
    public String Keybind() {
        String keybindstring = "";
        try {
            Class<?> keyboardClass = Class.forName("org.lwjgl.input.Keyboard");
            Object keyNameMethod = keyboardClass.getMethod("getKeyName", int.class);
            keybindstring = (String)((Method)keyNameMethod).invoke(null, this.bind);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keybindstring;
    }

    public boolean onPacket(Packet packet) {
        try {
            Class<?> packetClass = packet.getClass();
            Method[] methods = packetClass.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getParameterTypes().length == 1 && method.getParameterTypes()[0] == NetHandlerPlayServer.class) {
                    method.setAccessible(true);
                    method.invoke(packet, mc.getNetHandler());
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}