package org.madscientists.createelemancy.content.block.vortex;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS;

public class VCPLargeCogWheelBlock extends VCPBlock implements ICogWheel {
	public VCPLargeCogWheelBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(AXIS, Direction.Axis.X));
	}

	@Override
	public Direction.Axis getRotationAxis(BlockState state) {
		return state.getValue(AXIS);
	}

	private Direction.Axis cycleAxis(BlockState state) {
		return (state.getValue(AXIS) == Direction.Axis.X)
				? Direction.Axis.Z : Direction.Axis.X;
	}

	@Override
	public boolean isLargeCog() {
		return true;
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {

		if(player.getItemInHand(hand).getItem().equals(AllItems.WRENCH.get())) {
			level.setBlockAndUpdate(pos, state.setValue(AXIS,cycleAxis(state)));
			((VCPBlockEntity) level.getBlockEntity(pos)).updateGeneratedRotation();
			return InteractionResult.CONSUME;
		}
		return InteractionResult.PASS;
	}
}
