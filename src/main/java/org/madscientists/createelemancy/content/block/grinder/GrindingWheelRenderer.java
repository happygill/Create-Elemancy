package org.madscientists.createelemancy.content.block.grinder;


import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.madscientists.createelemancy.content.registry.ElemancyPartials;

import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

public class GrindingWheelRenderer extends KineticBlockEntityRenderer<GrindingWheelBlockEntity> {



	public GrindingWheelRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}



	@Override
	protected void renderSafe(GrindingWheelBlockEntity te, float partialTicks, PoseStack ms,
                              MultiBufferSource buffer, int light, int overlay) {
		if (Backend.canUseInstancing(te.getLevel())) return;

		BlockState blockState = te.getBlockState();
		GrindingWheelBlockEntity wheel = te;
		Direction direction = wheel.getBlockState().getValue(HORIZONTAL_FACING);
		VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());

		// half-shaft
		SuperByteBuffer shaft = CachedBufferer
				.partialFacing(AllPartialModels.SHAFT_HALF, blockState, direction.getOpposite());
		standardKineticRotationTransform(shaft, te, light).renderInto(ms, vb);


		// grinding wheel
		float renderedWheelOffset = te.getOffset();
		float speed = wheel.getSpeed();
		float time = AnimationTickHolder.getRenderTime(te.getLevel());
		float angle = ((time * speed * 6 / 10f) % 360) / 180 * Mth.PI;

		SuperByteBuffer wheelRender = CachedBufferer
				.partialFacing(ElemancyPartials.GRINDING_WHEEL_PARTIAL, blockState, direction.getOpposite());

		transformWheelRender(wheelRender, direction, renderedWheelOffset, angle);
		wheelRender.light(light).renderInto(ms, vb);
	}

	private void transformWheelRender(SuperByteBuffer wheelRender, Direction direction, float offset, float angle) {
		Vec3 offsetVector = new Vec3(direction.getStepX(), direction.getStepY(), direction.getStepZ()).scale(offset);
		wheelRender.translate(offsetVector.x(), offsetVector.y(), offsetVector.z());
		boolean isPositive = direction.getAxisDirection() == Direction.AxisDirection.POSITIVE;
		wheelRender.rotateCentered(direction, isPositive ? angle : -angle);
	}


}
