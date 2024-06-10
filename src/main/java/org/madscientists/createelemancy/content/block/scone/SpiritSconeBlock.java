package org.madscientists.createelemancy.content.block.scone;

import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.madscientists.createelemancy.content.registry.ElemancyBlockEntities;

public class SpiritSconeBlock extends Block implements IBE<SpiritSconeBlockEntity> {
    protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    public SpiritSconeBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Class<SpiritSconeBlockEntity> getBlockEntityClass() {
        return SpiritSconeBlockEntity.class;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public BlockEntityType<? extends SpiritSconeBlockEntity> getBlockEntityType() {
        return null;
    }
}
