package org.madscientists.createelemancy.content.block.grinder;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.item.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.madscientists.createelemancy.content.registry.ElemancyBlockEntities;

public class GrindingWheelControllerBlock extends Block implements IBE<GrindingWheelControllerBlockEntity> {

	public GrindingWheelControllerBlock(Properties props) {
		super(props);
	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
		return false;
	}

	@Override
	public boolean addRunningEffects(BlockState state, Level world, BlockPos pos, Entity entity) {
		return true;
	}

	@Override
	public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
		if (shouldStartGrinding(worldIn, pos, entityIn)) {
			getBlockEntity(worldIn, pos).startGrinding(entityIn);
		}
		makeEntityStuckIfProcessing(state, worldIn, pos, entityIn);
	}

	private boolean shouldStartGrinding(Level worldIn, BlockPos pos, Entity entityIn) {
		GrindingWheelControllerBlockEntity be = getBlockEntity(worldIn, pos);
		if (be == null || be.grindingSpeed == 0 || be.isOccupied()) {
			return false;
		}
		if (entityIn instanceof Player) {
			Player player = (Player) entityIn;
			if (player.isCreative() || player.level().getDifficulty() == Difficulty.PEACEFUL) {
				return false;
			}
		}
		return true;
	}

	private void makeEntityStuckIfProcessing(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
		withBlockEntityDo(worldIn, pos, te -> {
			if (te.processingEntity == entityIn) {
				entityIn.makeStuckInBlock(state, new Vec3(0.25D, 0.05F, 0.25D));
			}
		});
	}


	@Override
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
        if (rand.nextInt(1) != 0)
            return;
        double d0 = (float) pos.getX() + rand.nextFloat();
        double d1 = (float) pos.getY() + rand.nextFloat();
        double d2 = (float) pos.getZ() + rand.nextFloat();
        worldIn.addParticle(ParticleTypes.CRIT, d0, d1, d2, 0.0D, 0.0D, 0.0D);
    }


	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		VoxelShape standardShape = AllShapes.CRUSHING_WHEEL_CONTROLLER_COLLISION.get(Direction.DOWN);

		if (!(context instanceof EntityCollisionContext))
			return standardShape;
		Entity entity = ((EntityCollisionContext) context).getEntity();
		if (entity == null)
			return standardShape;

		GrindingWheelControllerBlockEntity te = getBlockEntity(worldIn, pos);
		if (te != null && te.processingEntity == entity)
			return Shapes.empty();

		return standardShape;
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.hasBlockEntity() || state.getBlock() == newState.getBlock())
			return;

		withBlockEntityDo(worldIn, pos, te -> ItemHelper.dropContents(worldIn, pos, te.inventory));
		worldIn.removeBlockEntity(pos);
	}



	@Override
	public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
		return false;
	}

	@Override
	public Class<GrindingWheelControllerBlockEntity> getBlockEntityClass() {
		return GrindingWheelControllerBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends GrindingWheelControllerBlockEntity> getBlockEntityType() {
		return ElemancyBlockEntities.GRINDING_WHEEL_CONTROLLER.get();
	}


}
