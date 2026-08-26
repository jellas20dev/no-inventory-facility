package com.jellas.noinventoryfacility;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class PlayerStatus {

    public static Minecraft getMinecraft() {
        return Minecraft.getInstance();
    }

    public static Player getPlayer() {
        return getMinecraft().player;
    }

}
