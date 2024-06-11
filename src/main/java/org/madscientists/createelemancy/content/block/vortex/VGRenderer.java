package org.madscientists.createelemancy.content.block.vortex;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraftforge.fluids.FluidStack;

public class VGRenderer extends SmartBlockEntityRenderer<VortexGeneratorBlockEntity> {
    public VGRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(VortexGeneratorBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay);
        if (!blockEntity.isPrimary())
            return;
        renderWaste(blockEntity, partialTicks, ms, buffer, light);
    }


    protected void renderWaste(VortexGeneratorBlockEntity vg, float partialTicks, PoseStack ms,
                               MultiBufferSource buffer, int light) {
        FluidStack waste = vg.output.getFluid();
        float fill = waste.getAmount() / (float) vg.output.getCapacity();
        if (waste.isEmpty())
            return;

        boolean multi = vg.size > vg.height;
        float min = 1 / 8f;
        float max = multi ? 23 / 8f : 7 / 8f;
        float ymax = fill * vg.height - min;
        if (multi)
            ms.translate(-1, 0, -1);

        FluidRenderer.renderFluidBox(waste,
                min, min, min,
                max, ymax, max,
                buffer, ms, light, false);

    }
}
