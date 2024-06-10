package org.madscientists.createelemancy.content.block.grinder;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.madscientists.createelemancy.content.registry.ElemancyBlockEntities;
import org.madscientists.createelemancy.content.registry.ElemancyBlocks;


public class GrindingWheelBlock extends HorizontalKineticBlock implements IBE<GrindingWheelBlockEntity> {

	public GrindingWheelBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return AllShapes.CRUSHING_WHEEL_COLLISION_SHAPE;
	}

	@Override
	public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onPlace(state, worldIn, pos, oldState, isMoving);
		addControllers(state, worldIn, pos);
	}

	private void addControllers(BlockState state, Level worldIn, BlockPos pos) {
		BlockPos otherWheelPos = pos.relative(state.getValue(HORIZONTAL_FACING),2);
		BlockState otherWheelState = worldIn.getBlockState(otherWheelPos);

		if(!otherWheelState.is(ElemancyBlocks.GRINDING_WHEEL.get())
				||otherWheelState.getValue(HORIZONTAL_FACING)!=state.getValue(HORIZONTAL_FACING).getOpposite())
			return;

		BlockPos controllerPos = pos.relative(state.getValue(HORIZONTAL_FACING));
		BlockState controllerState = worldIn.getBlockState(controllerPos);

		if(controllerState.is(Blocks.AIR)) {
			worldIn.setBlock(controllerPos, ElemancyBlocks.GRINDING_WHEEL_CONTROLLER.get().defaultBlockState(), 3);
			updateControllerSpeed(state, worldIn, pos);
		}
	}

	public void updateControllerSpeed(BlockState state, Level worldIn, BlockPos pos) {

		BlockPos controllerPos = pos.relative(state.getValue(HORIZONTAL_FACING));

		BlockPos otherWheelPos = pos.relative(state.getValue(HORIZONTAL_FACING),2);
		BlockState otherWheelState = worldIn.getBlockState(otherWheelPos);
		float speed = getBlockEntity(worldIn, pos).getSpeed();
		float otherSpeed = 0;

		if(otherWheelState.is(ElemancyBlocks.GRINDING_WHEEL.get())
				&&otherWheelState.getValue(HORIZONTAL_FACING)==state.getValue(HORIZONTAL_FACING).getOpposite())
			otherSpeed = getBlockEntity(worldIn, otherWheelPos).getSpeed();

		if(speed>0==otherSpeed>0)
			speed = 0;

		if (worldIn.getBlockEntity(controllerPos) instanceof GrindingWheelControllerBlockEntity controller) {
			controller.grindingSpeed = Math.min(Math.abs(speed), Math.abs(otherSpeed));
			controller.notifyUpdate();
		}
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		super.onRemove(state, worldIn, pos, newState, isMoving);
		removeControllers(state, worldIn, pos);
	}

	private void removeControllers(BlockState state, Level worldIn, BlockPos pos) {
			BlockPos controllerPos = pos.relative(state.getValue(HORIZONTAL_FACING));
			BlockState controllerState = worldIn.getBlockState(controllerPos);
			if (controllerState.is(ElemancyBlocks.GRINDING_WHEEL_CONTROLLER.get()))
				worldIn.setBlock(controllerPos, Blocks.AIR.defaultBlockState(), 3);
	}



	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.getValue(HORIZONTAL_FACING).getAxis();
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face == state.getValue(HORIZONTAL_FACING).getOpposite();
	}

	@Override
	public float getParticleTargetRadius() {
		return 1.125f;
	}

	@Override
	public float getParticleInitialRadius() {
		return 1f;
	}

	@Override
	public Class<GrindingWheelBlockEntity> getBlockEntityClass() {
			return GrindingWheelBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends GrindingWheelBlockEntity> getBlockEntityType() {
		return ElemancyBlockEntities.GRINDING_WHEEL.get();
	}


}
