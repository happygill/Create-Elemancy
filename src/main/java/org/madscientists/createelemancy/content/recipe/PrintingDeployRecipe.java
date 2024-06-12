package org.madscientists.createelemancy.content.recipe;

import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.madscientists.createelemancy.content.item.IncompletePrintingItem;

import static org.madscientists.createelemancy.content.insignia.InsigniaUtils.isComplexInsignia;

public class PrintingDeployRecipe extends DeployerApplicationRecipe {

    public PrintingDeployRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(params);
    }

    @Override
    public boolean matches(RecipeWrapper inv, Level level) {
        if (!super.matches(inv, level)) return false;
        ItemStack stack = inv.getItem(0).copy();
        ItemStack guide = inv.getItem(1).copy();
        if (isComplexInsignia(guide))
            return false;
        enforceNextResult(() -> IncompletePrintingItem.applyDeployer(guide, stack));

        return true;
    }


}