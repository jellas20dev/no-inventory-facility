package com.jellas.noinventoryfacility;

import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.TriState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Common (client + dedicated server) inventory rules.
 * Must not reference any client-only class.
 */
public class InventorySystem {

    public static final int MAIN_HAND_SLOT = 0;

    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Pre event) {
        event.setCanPickup(TriState.FALSE);
    }

    /**
     * Server-side enforcement: the only usable inventory slot is slot 0
     * (main hand). Anything that ends up in slots 1..35 — through chests,
     * furnaces, crafting results, commands, etc. — is dropped at the
     * player's feet.
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        Inventory inventory = player.getInventory();

        // A modified or lagging client could still select another hotbar
        // slot; force it back and tell the client.
        if (inventory.getSelectedSlot() != MAIN_HAND_SLOT) {
            inventory.setSelectedSlot(MAIN_HAND_SLOT);
            player.connection.send(
                    new ClientboundSetHeldSlotPacket(MAIN_HAND_SLOT)
            );
        }

        for (int slot = MAIN_HAND_SLOT + 1; slot < Inventory.INVENTORY_SIZE; slot++) {

            ItemStack stack = inventory.getItem(slot);

            if (stack.isEmpty()) {
                continue;
            }

            inventory.setItem(slot, ItemStack.EMPTY);
            player.drop(stack, false, false);
        }
    }

    public static boolean canPickup(Player player, ItemStack stack) {

        boolean twoHanded =
                ItemClassificationSystem.isTwoHanded(stack);

        ItemStack mainHand =
                player.getInventory().getItem(MAIN_HAND_SLOT);

        ItemStack offHand =
                player.getOffhandItem();

        boolean mainIsTwoHanded =
                ItemClassificationSystem.isTwoHanded(mainHand);

        boolean offhandIsTwoHanded =
                ItemClassificationSystem.isTwoHanded(offHand);

        if (mainIsTwoHanded || offhandIsTwoHanded) {
            return false;
        }

        if (twoHanded) {
            return mainHand.isEmpty() && offHand.isEmpty();
        }

        return mainHand.isEmpty() || offHand.isEmpty();
    }

    public static void pickupItem(
            Player player,
            ItemEntity itemEntity) {

        if (!itemEntity.isAlive() || itemEntity.hasPickUpDelay()) {
            return;
        }

        ItemStack stack = itemEntity.getItem();

        if (stack.isEmpty()) {
            return;
        }

        if (!canPickup(player, stack)) {
            return;
        }

        boolean twoHanded =
                ItemClassificationSystem.isTwoHanded(stack);

        // Work on a copy: mutating the entity's stack in place does not mark
        // the synched entity data dirty, so clients would keep showing the
        // old count. setItem() with a new instance forces the sync.
        ItemStack remaining = stack.copy();
        ItemStack pickedUp = remaining.split(1);

        if (twoHanded
                || player.getInventory().getItem(MAIN_HAND_SLOT).isEmpty()) {

            player.getInventory().setItem(MAIN_HAND_SLOT, pickedUp);

        } else {

            player.setItemInHand(
                    InteractionHand.OFF_HAND,
                    pickedUp
            );
        }

        if (remaining.isEmpty()) {
            itemEntity.discard();
        } else {
            itemEntity.setItem(remaining);
        }
    }
}
