package com.jellas.noinventoryfacility.hud;

import com.jellas.noinventoryfacility.PlayerStatus;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public class HealthHud {

    private static float lastHealth = -1.0f;
    private static int healthBlinkTime = 0;

    public static void renderHealth(
            GuiGraphicsExtractor graphics,
            Player player
    ) {

        int currentTick =
                PlayerStatus.getMinecraft()
                        .gui.hud
                        .getGuiTicks();

        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float absorption =
                PlayerStatus.getAbsorption();

        boolean isHardcore =
                PlayerStatus.isHardcore();

        if (lastHealth < 0.0f) {
            lastHealth = health;
        }

        if (health < lastHealth) {
            healthBlinkTime =
                    currentTick + 20;
        }

        boolean isBlink =
                healthBlinkTime > currentTick
                        && (healthBlinkTime - currentTick) / 3L % 2L == 1L;

        lastHealth = health;

        Hud.HeartType heartType =
                PlayerStatus.getHeartType();

        int totalHearts =
                (int) Math.ceil(maxHealth / 2.0f);

        for (int i = 0; i < totalHearts; i++) {

            int x = 10 + i * 8;
            int y = 10;

            Identifier container =
                    Hud.HeartType.CONTAINER.getSprite(
                            isHardcore,
                            false,
                            isBlink
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
                    health - (i * 2.0f);

            if (heartHealth >= 2.0f) {

                Identifier full =
                        heartType.getSprite(
                                isHardcore,
                                false,
                                isBlink
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
                        heartType.getSprite(
                                isHardcore,
                                true,
                                isBlink
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

        int absorptionHearts =
                (int) Math.ceil(absorption / 2.0F);

        for (int i = 0; i < absorptionHearts; i++) {

            int x = 10 + (i % 10) * 8;
            int y = 20 + (i / 10) * 10;

            float heartAbsorption =
                    absorption - (i * 2.0F);

            Identifier container =
                    Hud.HeartType.CONTAINER.getSprite(
                            isHardcore,
                            false,
                            isBlink
                    );

            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    container,
                    x,
                    y,
                    9,
                    9
            );

            if (heartAbsorption >= 2.0F) {

                Identifier full =
                        Hud.HeartType.ABSORBING.getSprite(
                                isHardcore,
                                false,
                                isBlink
                        );

                graphics.blitSprite(
                        RenderPipelines.GUI_TEXTURED,
                        full,
                        x,
                        y,
                        9,
                        9
                );

            } else if (heartAbsorption > 0.0F) {

                Identifier half =
                        Hud.HeartType.ABSORBING.getSprite(
                                isHardcore,
                                true,
                                isBlink
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