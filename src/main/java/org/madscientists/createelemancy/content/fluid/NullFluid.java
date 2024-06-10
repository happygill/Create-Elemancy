package org.madscientists.createelemancy.content.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class NullFluid extends ForgeFlowingFluid.Source {

    static final int VAPORIZE_DELAY = 3;

    public NullFluid(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isRandomlyTicking() {
        return true;
    }


    @Override
    protected void randomTick(Level pLevel, BlockPos pPos, FluidState pState, RandomSource pRandom) {
        if (pRandom.nextInt(VAPORIZE_DELAY) == 1)
            pLevel.setBlockAndUpdate(pPos, Blocks.AIR.defaultBlockState());
    }
}
