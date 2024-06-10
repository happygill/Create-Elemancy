package org.madscientists.createelemancy.content.block.grinder;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.simibubi.create.content.kinetics.base.BackHalfShaftInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.foundation.render.AllMaterialSpecs;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.madscientists.createelemancy.content.registry.ElemancyPartials;

import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;

public class GrindingWheelInstance extends BackHalfShaftInstance<GrindingWheelBlockEntity> implements DynamicInstance {

	protected final RotatingData wheel;

	private final GrindingWheelBlockEntity wheelTE;

	final Direction direction;


    public GrindingWheelInstance(MaterialManager modelManager, GrindingWheelBlockEntity tile) {
		super(modelManager, tile);
		this.wheelTE = tile;

		direction = tile.getBlockState().getValue(HORIZONTAL_FACING);
        Direction opposite = direction.getOpposite();

		wheel = modelManager.defaultCutout()
				.material(AllMaterialSpecs.ROTATING)
				.getModel(ElemancyPartials.GRINDING_WHEEL_PARTIAL, blockState, opposite)
				.createInstance();

		setup(wheel);

		float renderedWheelOffset = getRenderedWheelOffset();
		transformWheel(renderedWheelOffset);
	}

	@Override
	public void beginFrame() {
		float renderedWheelOffset = getRenderedWheelOffset();
		transformWheel(renderedWheelOffset);
	}

	private void transformWheel(float renderedWheelOffset) {
		float speed = wheelTE.getSpeed();
		wheel.setPosition(getInstancePosition());
		Vec3 offset = new Vec3(direction.getStepX(), direction.getStepY(), direction.getStepZ()).scale(renderedWheelOffset);
		wheel.nudge((float) offset.x(), (float) offset.y(), (float) offset.z());
		wheel.setRotationalSpeed(speed);
	}

	private float getRenderedWheelOffset() {
		return wheelTE.getOffset();
	}

	@Override
	public void updateLight() {
		super.updateLight();
		relight(pos, wheel);
	}

	@Override
	public void remove() {
		super.remove();
		wheel.delete();
	}

	@Override
	protected Direction getShaftDirection() {
		return blockState.getValue(HORIZONTAL_FACING).getOpposite();
	}

}
