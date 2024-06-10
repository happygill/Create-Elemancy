package org.madscientists.createelemancy.content.block.vortex;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.madscientists.createelemancy.content.registry.ElemancyPartials;

import static com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer.getAngleForTe;
import static com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer.standardKineticRotationTransform;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS;

public class VCPRenderer  extends SmartBlockEntityRenderer<VCPBlockEntity> {
    public VCPRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }


    @Override
    protected void renderSafe(VCPBlockEntity blockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay);
        VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());
        BlockState blockState = blockEntity.getBlockState();
        SuperByteBuffer kinetic = getKineticModel(blockState);
        if(kinetic != null) {
            standardKineticRotationTransform(kinetic, blockEntity, light).renderInto(ms, vb);
        }
        VortexGeneratorBlockEntity primary = blockEntity.getVortex();
        if(primary == null)
            return;
        int vcpCount = primary.getVCPCount();
        boolean top = blockState.getValue(VCPBlock.TOP);
        if(vcpCount==0)
            return;
        for (int i = 0; i < blockEntity.height/vcpCount; i++) {
            SuperByteBuffer turbine = CachedBufferer.partial(ElemancyPartials.TURBINE, blockState);
            turbine.translate(0, (1f+i*.5f)*(top?-1:1), 0);
            if(!top)
                turbine.rotateCentered(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.X), (float) Math.PI);
            turbine.rotateCentered(Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.Y), getAngleForTe(blockEntity,blockEntity.getBlockPos(), Direction.Axis.Y));
            turbine.light(light).renderInto(ms, vb);
        }

    }

    private static SuperByteBuffer getKineticModel(BlockState state) {
        boolean top = state.getValue(VCPBlock.TOP);
        Direction.Axis axis = state.getValue(AXIS);

        if(state.getValue(VCPBlock.VCP_SHAFT))
           return CachedBufferer
                    .partialFacing(AllPartialModels.SHAFT_HALF, state, top ? Direction.UP : Direction.DOWN);

        if(state.getBlock() instanceof VCPCogWheelBlock) {
            return CachedBufferer
                    .partialFacing(AllPartialModels.SHAFTLESS_COGWHEEL, state, Direction.NORTH);
        }
        if(state.getBlock() instanceof VCPLargeCogWheelBlock) {
            return axis == Direction.Axis.Z ? CachedBufferer
                    .partialFacing(ElemancyPartials.VCP_LARGE_COGWHEEL, state, Direction.NORTH) : CachedBufferer
                    .partialFacing(ElemancyPartials.VCP_LARGE_COGWHEEL, state, Direction.EAST);
        }
        return null;
    }
}
