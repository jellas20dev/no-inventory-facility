package com.jellas.noinventoryfacility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Hud;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class PlayerStatus {

    public static Minecraft getMinecraft() {
        return Minecraft.getInstance();
    }

    public static Player getPlayer() {
        return getMinecraft().player;
    }

    public static boolean isPoisoned() {
        Player player = getPlayer();

        return player != null
                && player.hasEffect(MobEffects.POISON);
    }

    public static boolean isWithered() {
        Player player = getPlayer();

        return player != null
                && player.hasEffect(MobEffects.WITHER);
    }

    public static boolean isFrozen() {
        Player player = getPlayer();

        return player != null
                && player.isFullyFrozen();
    }

    public static Hud.HeartType getHeartType() {

        if (isPoisoned()) {
            return Hud.HeartType.POISIONED;
        }

        if (isWithered()) {
            return Hud.HeartType.WITHERED;
        }

        if(isFrozen()) {
            return Hud.HeartType.FROZEN;
        }

        return Hud.HeartType.NORMAL;
    }
}
