package com.jellas.noinventoryfacility.hud;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public class HealthHud {

    public static void renderHealth(
            GuiGraphicsExtractor graphics,
            Player player
    ) {
        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();

        int totalHearts =
                (int) Math.ceil(maxHealth / 2.0F);

        for (int i = 0; i < totalHearts; i++) {

            int x = 10 + i * 8;
            int y = 10;

            Identifier container =
                    Hud.HeartType.CONTAINER.getSprite(
                            false,
                            false,
                            false
                    );

            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    container,
                    x,
                    y,
                    9,
                    9
            );

            float heartHealth =
                    health - (i * 2.0F);

            if (heartHealth >= 2.0F) {

                Identifier full =
                        Hud.HeartType.NORMAL.getSprite(
                                false,
                                false,
                                false
                        );

                graphics.blitSprite(
                        RenderPipelines.GUI_TEXTURED,
                        full,
                        x,
                        y,
                        9,
                        9
                );

            } else if (heartHealth > 0.0F) {

                Identifier half =
                        Hud.HeartType.NORMAL.getSprite(
                                false,
                                true,
                                false
                        );

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
