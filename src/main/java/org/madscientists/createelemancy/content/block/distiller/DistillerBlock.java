package org.madscientists.createelemancy.content.block.distiller;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.madscientists.createelemancy.content.registry.ElemancyBlockEntities;

public class DistillerBlock extends Block implements IBE<DistillerBlockEntity> {
	public DistillerBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
		return !AllBlocks.BASIN.has(worldIn.getBlockState(pos.below()));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return context instanceof EntityCollisionContext
				&& ((EntityCollisionContext) context).getEntity() instanceof Player
				? AllShapes.CASING_14PX.get(Direction.DOWN)
				: AllShapes.MECHANICAL_PROCESSOR_SHAPE;
	}


	@Override
	public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
		return false;
	}


	@Override
	public Class<DistillerBlockEntity> getBlockEntityClass()  {
		return DistillerBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends DistillerBlockEntity> getBlockEntityType() {
		return ElemancyBlockEntities.DISTILLER.get();
	}
}
