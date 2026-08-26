package com.jellas.noinventoryfacility.network;


import com.jellas.noinventoryfacility.NoInventoryFacility;
import org.jetbrains.annotations.NotNull;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record PickupItemPayload(int entityId) implements CustomPacketPayload {

    public static final Type<PickupItemPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(NoInventoryFacility.MODID, "pickup_item"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PickupItemPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    PickupItemPayload::entityId,
                    PickupItemPayload::new
            );
    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
