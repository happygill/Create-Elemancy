package org.madscientists.createelemancy.content.nullspace;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.madscientists.createelemancy.Elemancy;
import org.madscientists.createelemancy.content.registry.ElemancyPartials;

public class NullSpaceRenderer extends SmartBlockEntityRenderer<NullSpaceBlockEntity> {
    public NullSpaceRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(NullSpaceBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.beaconBeam(Elemancy.asResource("textures/block/space.png"),true));
        SuperByteBuffer nullspace= CachedBufferer.partial(ElemancyPartials.NULL_SPACE, blockEntity.getBlockState());
        int size = blockEntity.getSize();
        ms.translate(0.5, 0.5, 0.5);
        ms.scale(size, size, size);
        ms.translate(-0.5, -0.5, -0.5);
        nullspace.light(light).renderInto(ms, builder);
    }
}
