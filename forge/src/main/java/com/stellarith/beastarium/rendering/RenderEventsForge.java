package com.stellarith.beastarium.rendering;

import com.mojang.blaze3d.vertex.*;
import com.stellarith.beastarium.Constants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class RenderEventsForge {
    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if(event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS)
            return;

        ModRendering.renderLevelEventHook(event.getPoseStack(), event.getProjectionMatrix());
    }
}
