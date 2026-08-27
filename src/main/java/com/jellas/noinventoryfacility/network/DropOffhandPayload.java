package com.jellas.noinventoryfacility.network;

import com.jellas.noinventoryfacility.NoInventoryFacility;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record DropOffhandPayload() implements CustomPacketPayload {

    public static final Type<DropOffhandPayload> TYPE =
            new Type<>(
                    Identifier.fromNamespaceAndPath(
                            NoInventoryFacility.MODID,
                            "drop_offhand"
                    )
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, DropOffhandPayload> STREAM_CODEC =
            StreamCodec.unit(new DropOffhandPayload());

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}