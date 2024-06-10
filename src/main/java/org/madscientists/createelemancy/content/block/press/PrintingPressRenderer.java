package org.madscientists.createelemancy.content.block.press;

import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class PrintingPressRenderer extends KineticBlockEntityRenderer<PrintingPressBlockEntity> {
    public PrintingPressRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(PrintingPressBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        renderTopChamber(be, partialTicks, ms, buffer, light);
        FilteringRenderer.renderOnBlockEntity(be, partialTicks, ms, buffer, light, overlay);
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        if (Backend.canUseInstancing(be.getLevel()))
            return;

        BlockState blockState = be.getBlockState();
        PressingBehaviour pressingBehaviour = be.getPressingBehaviour();
        float renderedHeadOffset =
                pressingBehaviour.getRenderedHeadOffset(partialTicks) * pressingBehaviour.mode.headOffset;

        SuperByteBuffer headRender = CachedBufferer.partialFacing(AllPartialModels.MECHANICAL_PRESS_HEAD, blockState,
                blockState.getValue(HORIZONTAL_FACING));
        headRender.translate(0, -renderedHeadOffset, 0)
                .light(light)
                .renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }

    protected void renderTopChamber(PrintingPressBlockEntity distillerBlock, float partialTicks, PoseStack ms,
                                    MultiBufferSource buffer, int light) {

        SmartFluidTankBehaviour tank = distillerBlock.getInternalTank();
        if (tank == null)
            return;

        SmartFluidTankBehaviour.TankSegment primaryTank = tank.getPrimaryTank();
        FluidStack fluidStack = primaryTank.getRenderedFluid();
        float level = primaryTank.getFluidLevel().getValue(partialTicks);

        if (!fluidStack.isEmpty() && level != 0) {
            boolean top = fluidStack.getFluid()
                    .getFluidType()
                    .isLighterThanAir();

            level = Math.max(level, 0.175f);
            float min = 2.5f / 16f;
            float max = min + (11 / 16f);
            float yOffset = (11 / 16f) * level;

            ms.pushPose();
            if (top)
                ms.translate(0, max - min, 0);
            else ms.translate(0, yOffset, 0);

            FluidRenderer.renderFluidBox(fluidStack,
                    min, min - yOffset, min,
                    max, min, max,
                    buffer, ms, light, top);
            ms.popPose();
        }
    }
}
