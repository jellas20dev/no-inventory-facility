package com.jellas.noinventoryfacility.hud;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForgeMod;

public class OxygenHud {

    private static final Identifier AIR_SPRITE =
            Identifier.withDefaultNamespace("hud/air");

    private static final Identifier AIR_BURSTING_SPRITE =
            Identifier.withDefaultNamespace("hud/air_bursting");

    public static void renderOxygen(
            GuiGraphicsExtractor graphics,
            Player player
    ) {

        int air =
                player.getAirSupply();

        if (!player.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value())
                && air >= 300) {
            return;
        }

        int full =
                Mth.ceil((double) (air - 2) * 10.0D / 300.0D);

        int partial =
                Mth.ceil((double) air * 10.0D / 300.0D) - full;

        int screenWidth =
                graphics.guiWidth();

        for (int i = 0; i < 10; i++) {

            int x =
                    screenWidth - 10 - ((9 - i) * 8) - 9;

            int y = 20;

            if (i < full) {

                graphics.blitSprite(
                        RenderPipelines.GUI_TEXTURED,
                        AIR_SPRITE,
                        x,
                        y,
                        9,
                        9
                );

            } else if (i < full + partial) {

                graphics.blitSprite(
                        RenderPipelines.GUI_TEXTURED,
                        AIR_BURSTING_SPRITE,
                        x,
                        y,
                        9,
                        9
                );
            }
        }
    }
}