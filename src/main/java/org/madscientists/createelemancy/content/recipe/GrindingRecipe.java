package org.madscientists.createelemancy.content.recipe;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.madscientists.createelemancy.content.registry.ElemancyRecipes;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class GrindingRecipe extends ProcessingRecipe<RecipeWrapper> {

	public GrindingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
		super(ElemancyRecipes.GRINDING, params);
	}

	@Override
	public boolean matches(RecipeWrapper inv, Level worldIn) {
		if (inv.isEmpty()) return false;

		return ingredients.get(0).test(inv.getItem(0));
	}

	@Override
	protected int getMaxInputCount() {
		return 1;
	}

	@Override
	protected int getMaxOutputCount() {
		return 7;
	}

}
