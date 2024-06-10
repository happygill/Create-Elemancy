package org.madscientists.createelemancy.content.nullspace;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.madscientists.createelemancy.content.nullspace.api.NullSpaceType;
import org.madscientists.createelemancy.content.registry.ElemancyBlocks;

import java.util.concurrent.atomic.AtomicBoolean;

public class NullSpaceHandler {
    public static void castNullEffect(Player caster, BlockPos pos, NullSpaceType type) {
        castNullEffect(caster, pos, type, -1, 0);
    }

    public static void castNullEffect(Player caster, BlockPos pos, NullSpaceType type, int sizeChange) {
        castNullEffect(caster, pos, type, -1, sizeChange);
    }

    public static void castNullEffect(Player caster, BlockPos pos, NullSpaceType type, int duration, int sizeChange) {
        AtomicBoolean found = new AtomicBoolean(false);
        NullSpaceBlockEntity.ALL_NULL_SPACES.stream()
                .filter(nullSpacePos -> caster.level().getBlockEntity(nullSpacePos) instanceof NullSpaceBlockEntity)
                .map(nullSpacePos -> (NullSpaceBlockEntity) caster.level().getBlockEntity(nullSpacePos))
                .filter(nullSpace -> nullSpace.getBlockPos().closerThan(pos, nullSpace.getSize()/2.0))
                .forEach(nullSpace -> {
                    nullSpace.addNullEffect(type, caster, duration);
                    nullSpace.setSize(nullSpace.getSize()+sizeChange);
                    nullSpace.notifyUpdate();
                    found.set(true);
                });

        if(!found.get()){
            if(sizeChange<=0) return;
            Level level = caster.level();
            BlockState state = level.getBlockState(pos);
            if(state.isFaceSturdy(level, pos, Direction.UP)&&level.getBlockEntity(pos)==null) {
                level.setBlockAndUpdate(pos, ElemancyBlocks.NULL_SPACE.getDefaultState());
                if(level.getBlockEntity(pos) instanceof NullSpaceBlockEntity nullSpace) {
                    nullSpace.addNullEffect(type, caster, duration);
                    nullSpace.setSize(nullSpace.getSize()+sizeChange);
                    nullSpace.setState(state);
                    nullSpace.notifyUpdate();
                }
            }
        }
    }
}
