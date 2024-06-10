package org.madscientists.createelemancy.content.insignia;

import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.madscientists.createelemancy.content.registry.ElemancyBlockEntities;

@SuppressWarnings("NullableProblems")
public class InsigniaBlock extends Block implements IBE<InsigniaBlockEntity> {

	private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 0.01D, 16.0D);

	@Override
	public void attack(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
		if(pLevel.getBlockEntity(pPos) instanceof InsigniaBlockEntity insignia&&insignia.erase(pPlayer))
			return;
		super.attack(pState, pLevel, pPos, pPlayer);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return super.getVisualShape(state, level, pos, context);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if(level.getBlockEntity(pos) instanceof InsigniaBlockEntity insignia&&insignia.hasCenter()) {
			insignia.removeInsigniaPart(insignia.getCenter());
		}
		super.onRemove(state, level, pos, newState, isMoving);
	}
	

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}


	public InsigniaBlock(Properties properties) {
		super(properties);
	}

	@Override
	public Class<InsigniaBlockEntity> getBlockEntityClass() {
		return InsigniaBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends InsigniaBlockEntity> getBlockEntityType() {
		return ElemancyBlockEntities.INSIGNIA.get();
	}
}
