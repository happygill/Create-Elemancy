package org.madscientists.createelemancy.content.block.press;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.madscientists.createelemancy.content.registry.ElemancyBlockEntities;

public class PrintingPressBlock extends HorizontalKineticBlock implements IBE<PrintingPressBlockEntity> {

    public PrintingPressBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext
                && ((EntityCollisionContext) context).getEntity() instanceof Player)
            return AllShapes.CASING_14PX.get(Direction.DOWN);

        return AllShapes.MECHANICAL_PROCESSOR_SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        return !AllBlocks.BASIN.has(worldIn.getBlockState(pos.below()));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction prefferedSide = getPreferredHorizontalFacing(context);
        if (prefferedSide != null)
            return defaultBlockState().setValue(HORIZONTAL_FACING, prefferedSide);
        return super.getStateForPlacement(context);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_FACING)
                .getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue(HORIZONTAL_FACING)
                .getAxis();
    }

    @Override
    public Class<PrintingPressBlockEntity> getBlockEntityClass() {
        return PrintingPressBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PrintingPressBlockEntity> getBlockEntityType() {
        return null;
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
        return false;
    }

}
