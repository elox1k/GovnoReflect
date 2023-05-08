package com.refleks.modules;

import org.lwjgl.input.Keyboard;

import com.refleks.Category;
import com.refleks.Module;

import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class Fly extends Module {

    private boolean isFlying = false;

    public Fly() {
        super("Fly", Keyboard.KEY_F, Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        if (!isFlying) {
            EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
            player.capabilities.allowFlying = true;
            player.capabilities.isFlying = true;
            player.sendPlayerAbilities();
            isFlying = true;
        }
    }

    @Override
    public void onDisable() {
        if (isFlying) {
            EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
            player.capabilities.allowFlying = false;
            player.capabilities.isFlying = false;
            player.sendPlayerAbilities();
            isFlying = false;
        }
    }

    @Override
    public void onTick(ClientTickEvent event) {
        // TODO Auto-generated method stub
        
    }

	@Override
	public void settings() {
		// TODO Auto-generated method stub
		
	}

}