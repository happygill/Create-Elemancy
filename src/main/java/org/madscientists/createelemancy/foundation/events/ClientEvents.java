package org.madscientists.createelemancy.foundation.events;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.contraptions.minecart.CouplingRenderer;
import com.simibubi.create.content.trains.entity.CarriageCouplingRenderer;
import com.simibubi.create.content.trains.track.TrackBlockOutline;
import com.simibubi.create.content.trains.track.TrackTargetingClient;
import com.simibubi.create.foundation.render.SuperRenderTypeBuffer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.madscientists.createelemancy.Elemancy;
import org.madscientists.createelemancy.content.ability.capability.Abilities;
import org.madscientists.createelemancy.foundation.config.ElemancyConfig;
import org.madscientists.createelemancy.foundation.network.ElemancyMessages;
import org.madscientists.createelemancy.foundation.network.InputC2S;

@Mod.EventBusSubscriber(modid = Elemancy.ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void handleKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null)
            return;
        if(mc.screen != null)
            return;
        Abilities.handleKeyInput(mc.player, event.getKey(), event.getAction(), true);
        ElemancyMessages.sendToServer(new InputC2S(event.getKey(), event.getAction()));
    }

    @SubscribeEvent
    public static void onRenderWorld(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES)
            return;

        PoseStack ms = event.getPoseStack();
        ms.pushPose();
        SuperRenderTypeBuffer buffer = SuperRenderTypeBuffer.getInstance();
        float partialTicks = AnimationTickHolder.getPartialTicks();
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera()
                .getPosition();


        buffer.draw();
        RenderSystem.enableCull();
        ms.popPose();
    }

    @Mod.EventBusSubscriber(modid = Elemancy.ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void onLoadComplete(FMLLoadCompleteEvent event) {
            ModContainer container = ModList.get()
                    .getModContainerById(Elemancy.ID)
                    .orElseThrow(() -> new IllegalStateException("Elemancy mod container missing on LoadComplete"));

            container.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory(ElemancyConfig::createConfigScreen));
        }





    }
}
