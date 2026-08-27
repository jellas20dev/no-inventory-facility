package com.jellas.noinventoryfacility;

import com.jellas.noinventoryfacility.network.DropOffhandPayload;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.settings.KeyConflictContext;

import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class OffHandDrop {

    private static final KeyMapping DROP_OFF_HAND =
            new KeyMapping(
                    "key.noinventoryfacility.drop_offhand",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_R,
                    KeyMapping.Category.MISC
            );

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(DROP_OFF_HAND);
    }

    public static void tick(ClientTickEvent.Post event) {

        while (DROP_OFF_HAND.consumeClick()) {

            Minecraft minecraft = Minecraft.getInstance();

            if (minecraft.player == null) {
                return;
            }

            ClientPacketDistributor.sendToServer(
                    new DropOffhandPayload()
            );
        }
    }
}