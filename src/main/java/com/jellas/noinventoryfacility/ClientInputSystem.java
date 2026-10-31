package com.jellas.noinventoryfacility;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;

/**
 * Client-only input handling. Registered from {@link NoInventoryFacilityClient}
 * so it is never loaded on a dedicated server.
 */
public class ClientInputSystem {

    @SubscribeEvent
    public static void onHotbarScroll(InputEvent.MouseScrollingEvent event) {

        Player player = PlayerStatus.getPlayer();

        if (player == null) {
            return;
        }

        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        event.setCanceled(true);
    }

    /**
     * Runs before vanilla's handleKeybinds() in the same tick: swallowing the
     * hotbar key clicks here means keys 1-9 never change the selected slot.
     */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null) {
            return;
        }

        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        for (KeyMapping key : minecraft.options.keyHotbarSlots) {
            while (key.consumeClick()) {
                // discard
            }
        }

        if (player.getInventory().getSelectedSlot() != InventorySystem.MAIN_HAND_SLOT) {
            player.getInventory().setSelectedSlot(InventorySystem.MAIN_HAND_SLOT);
        }
    }
}
