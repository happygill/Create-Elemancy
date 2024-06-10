package org.madscientists.createelemancy.content.block.scone;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;

public class SpiritSconeRenderer extends SmartBlockEntityRenderer<SpiritSconeBlockEntity> {
    EntityRenderDispatcher entityRender;

    public SpiritSconeRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
        entityRender = context.getEntityRenderer();
    }

    @Override
    protected void renderSafe(SpiritSconeBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay);
        if (blockEntity.elemancyElement == null) return;

    }
}
