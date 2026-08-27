package com.jellas.noinventoryfacility.network;

import com.jellas.noinventoryfacility.InventorySystem;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class NetworkHandler {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {

        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                PickupItemPayload.TYPE,
                PickupItemPayload.STREAM_CODEC,
                NetworkHandler::handlePickup
        );

        registrar.playToServer(
                DropOffhandPayload.TYPE,
                DropOffhandPayload.STREAM_CODEC,
                NetworkHandler::handleDropOffhand
        );
    }

    private static void handlePickup(
            PickupItemPayload payload,
            IPayloadContext context
    ) {
        context.enqueueWork(() -> {

            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            Entity entity = player.level().getEntity(payload.entityId());

            if (!(entity instanceof ItemEntity itemEntity)) {
                return;
            }

            if (player.distanceToSqr(itemEntity) > 25.0) {
                return;
            }

            InventorySystem.pickupItem(player, itemEntity);
        });
    }

    private static void handleDropOffhand(
            DropOffhandPayload payload,
            IPayloadContext context
    ) {
        context.enqueueWork(() -> {

            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            ItemStack stack = player.getOffhandItem();

            if (stack.isEmpty()) {
                return;
            }

            player.setItemInHand(
                    InteractionHand.OFF_HAND,
                    ItemStack.EMPTY
            );

            player.drop(stack, true);
        });
    }
}