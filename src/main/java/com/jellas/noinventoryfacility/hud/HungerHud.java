package com.jellas.noinventoryfacility.hud;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class HungerHud {

    private static final Identifier FOOD_EMPTY =
            Identifier.withDefaultNamespace("hud/food_empty");

    private static final Identifier FOOD_HALF =
            Identifier.withDefaultNamespace("hud/food_half");

    private static final Identifier FOOD_FULL =
            Identifier.withDefaultNamespace("hud/food_full");

    private static final Identifier FOOD_EMPTY_HUNGER =
            Identifier.withDefaultNamespace("hud/food_empty_hunger");

    private static final Identifier FOOD_HALF_HUNGER =
            Identifier.withDefaultNamespace("hud/food_half_hunger");

    private static final Identifier FOOD_FULL_HUNGER =
            Identifier.withDefaultNamespace("hud/food_full_hunger");

    public static void renderHunger(
            GuiGraphicsExtractor graphics,
            Player player
    ) {
        int food =
                player.getFoodData().getFoodLevel();

        boolean hasHunger =
                player.hasEffect(MobEffects.HUNGER);

        Identifier empty =
                hasHunger
                        ? FOOD_EMPTY_HUNGER
                        : FOOD_EMPTY;

        Identifier half =
                hasHunger
                        ? FOOD_HALF_HUNGER
                        : FOOD_HALF;

        Identifier full =
                hasHunger
                        ? FOOD_FULL_HUNGER
                        : FOOD_FULL;

        int screenWidth =
                graphics.guiWidth();

        for (int i = 0; i < 10; i++) {

            int x =
                    screenWidth - 10 - (9 * 8) - 9 + (i * 8);

            int y = 10;

            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    empty,
                    x,
                    y,
                    9,
                    9
            );

            int foodForIcon =
                    (9 - i) * 2 + 1;

            if (foodForIcon < food) {

                graphics.blitSprite(
                        RenderPipelines.GUI_TEXTURED,
                        full,
                        x,
                        y,
                        9,
                        9
                );

            } else if (foodForIcon == food) {

                graphics.blitSprite(
                        RenderPipelines.GUI_TEXTURED,
                        half,
                        x,
                        y,
                        9,
                        9
                );
            }
        }
    }
}
