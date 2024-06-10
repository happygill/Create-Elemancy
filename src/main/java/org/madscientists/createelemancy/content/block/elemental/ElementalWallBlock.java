package org.madscientists.createelemancy.content.block.elemental;

import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.madscientists.createelemancy.content.registry.ElemancyBlockEntities;

import static org.madscientists.createelemancy.content.registry.ElemancyBlocks.FRIGID_WALL;
import static org.madscientists.createelemancy.content.registry.ElemancyBlocks.STABILITY_WALL;

public class ElementalWallBlock extends Block implements IBE<ElementalBlockEntity> {

    public ElementalWallBlock(Properties pProperties) {
        super(pProperties.isSuffocating((pState, pLevel, pPos) -> false).isViewBlocking((pState, pLevel, pPos) -> false).destroyTime(50.0F));

    }



    @Override
    public Class<ElementalBlockEntity> getBlockEntityClass() {
        return ElementalBlockEntity.class;
    }


    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (pContext instanceof EntityCollisionContext entityContext) {
            Entity entity = entityContext.getEntity();

            if (pLevel.getBlockEntity(pPos) instanceof ElementalBlockEntity wall) {
                if (pState.is(FRIGID_WALL.get())
                        &&!(entity instanceof Projectile || (entity instanceof Player player && wall.isSpellCaster(player))))
                     return Shapes.block();


                if ((entity instanceof Projectile)&&pState.is(STABILITY_WALL.get()))
                    return Shapes.block();
            }
        }
        if(pState.is(FRIGID_WALL.get())||pState.is(STABILITY_WALL.get()))
            return Shapes.empty();
        return super.getCollisionShape(pState,pLevel,pPos,pContext);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        if(pPlacer!=null){
            withBlockEntityDo(pLevel,pPos,elementalBlockEntity -> elementalBlockEntity.setSpellCasterUUID(pPlacer));
        }
    }

    @Override
    public BlockEntityType<? extends ElementalBlockEntity> getBlockEntityType() {
        return ElemancyBlockEntities.ELEMENTAL_WALL.get();
    }

}
