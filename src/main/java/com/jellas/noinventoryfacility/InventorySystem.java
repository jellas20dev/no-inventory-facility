package com.jellas.noinventoryfacility;

import net.minecraft.client.Minecraft;
import net.minecraft.util.TriState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

public class InventorySystem {

    @SubscribeEvent
    public static void onHotbarScroll(InputEvent.MouseScrollingEvent event) {

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null) {
            return;
        }

        if (player.isCreative()) {
            return;
        }

        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Player player = Minecraft.getInstance().player;

        if (player == null) {
            return;
        }

        if (player.isCreative()) {
            return;
        }

        int selected = player.getInventory().getSelectedSlot();

        if (selected > 0) {
            player.getInventory().setSelectedSlot(0);
        }
    }

    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Pre event) {
        event.setCanPickup(TriState.FALSE);
    }

    public static boolean canPickup(Player player, ItemStack stack) {

        boolean twoHanded =
                ItemClassificationSystem.isTwoHanded(
                        stack,
                        player.level()
                );

        boolean mainEmpty =
                player.getInventory().getItem(0).isEmpty();

        boolean offhandEmpty =
                player.getOffhandItem().isEmpty();

        boolean mainIsTwoHanded =
                ItemClassificationSystem.isTwoHanded(
                        player.getInventory().getItem(0),
                        player.level()
                );

        boolean offhandIsTwoHanded =
                ItemClassificationSystem.isTwoHanded(
                        player.getOffhandItem(),
                        player.level()
                );

        if (mainIsTwoHanded || offhandIsTwoHanded) {
            return false;
        }

        if (twoHanded) {
            return mainEmpty && offhandEmpty;
        }

        return mainEmpty || offhandEmpty;
    }

    public static void pickupItem(
            Player player,
            ItemEntity itemEntity) {

        ItemStack stack = itemEntity.getItem();

        if (stack.isEmpty()) {
            return;
        }

        boolean twoHanded =
                ItemClassificationSystem.isTwoHanded(
                        stack,
                        player.level()
                );

        if (!canPickup(player, stack)) {
            return;
        }

        ItemStack pickedUp = stack.split(1);

        if (twoHanded) {

            player.getInventory().setItem(0, pickedUp);

        } else if (player.getInventory().getItem(0).isEmpty()) {

            player.getInventory().setItem(0, pickedUp);

        } else {

            player.setItemInHand(
                    InteractionHand.OFF_HAND,
                    pickedUp
            );
        }

        if (stack.isEmpty()) {
            itemEntity.discard();
        }
    }

    public static void init() {
    }
}