package org.madscientists.createelemancy.content.recipe;

import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.madscientists.createelemancy.content.item.IncompletePrintingItem;

public class PrintingPressRecipe extends PressingRecipe {

    public PrintingPressRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(params);
    }

    @Override
    public boolean matches(RecipeWrapper inv, Level level) {
        if (!super.matches(inv, level)) return false;
        ItemStack stack = inv.getItem(0).copy();
        enforceNextResult(() -> IncompletePrintingItem.applyPress(stack));

        return true;
    }


}