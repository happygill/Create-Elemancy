package org.madscientists.createelemancy.content.block.grinder;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import javax.annotation.Nullable;

import static com.simibubi.create.content.kinetics.base.HorizontalKineticBlock.HORIZONTAL_FACING;


@EventBusSubscriber
public class GrindingWheelBlockEntity extends KineticBlockEntity {
	private static final float MAX_OFFSET = .4f;
	private static final float MAX_OCCUPIED_OFFSET = .65f;
	private static final float OFFSET_INCREMENT = .05f;
	private float offset;

	public GrindingWheelBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public float getOffset() {
		return offset;
	}

	@Override
	public void tick() {
		super.tick();
		updateOffset();
	}

	private void updateOffset() {
		GrindingWheelControllerBlockEntity controller = getController();
		float maxOffset = (controller != null) ? (controller.isOccupied() ? MAX_OCCUPIED_OFFSET : MAX_OFFSET) : 0;
		offset = (offset < maxOffset) ? Math.min(maxOffset, offset + OFFSET_INCREMENT) : Math.max(maxOffset, offset - OFFSET_INCREMENT);
	}


	@Override
	public void onSpeedChanged(float prevSpeed) {
		super.onSpeedChanged(prevSpeed);
		GrindingWheelBlock block = (GrindingWheelBlock) getBlockState().getBlock();
		block.updateControllerSpeed(getBlockState(), getLevel(), getBlockPos());
	}

	@Nullable
	public GrindingWheelControllerBlockEntity getController() {
		if(level.getBlockEntity(this.getBlockPos().relative(getBlockState().getValue(HORIZONTAL_FACING))) instanceof GrindingWheelControllerBlockEntity controller) {
			return controller;
		}
		return null;
	}

	@Override
	protected AABB createRenderBoundingBox() {
		return new AABB(worldPosition).inflate(1);
	}




}
