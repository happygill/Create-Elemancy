package org.madscientists.createelemancy.content.fluid;

import com.simibubi.create.content.fluids.OpenEndedPipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fluids.FluidStack;
import org.madscientists.createelemancy.content.registry.ElemancyFluids;

import java.util.Random;

public class ElemancyOpenPipeBehavior {

    public static void register() {
        OpenEndedPipe.registerEffectHandler(new GarbledSpiceOpenPipeBehavior());
    }

    public static class GarbledSpiceOpenPipeBehavior implements OpenEndedPipe.IEffectHandler {
        @Override
        public boolean canApplyEffects(OpenEndedPipe pipe, FluidStack fluid) {
            return fluid.getFluid().isSame(ElemancyFluids.GARBLED_SPICE.get());
        }


        @Override
        public void applyEffects(OpenEndedPipe pipe, FluidStack fluid) {
            Random random = new Random();
            AABB aabb = pipe.getAOE();
            if (random.nextInt(10) == 0) {
                double yRandom = (aabb.minY + random.nextInt((int) aabb.getYsize()));
                double xRandom = (aabb.minX + random.nextInt((int) aabb.getXsize() + 1));
                double zRandom = aabb.minZ + random.nextInt((int) aabb.getZsize() + 1);
                pipe.getWorld().explode(null, xRandom, yRandom, zRandom, 1, Level.ExplosionInteraction.NONE);
            }
        }
    }
}
