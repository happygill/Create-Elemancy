package org.madscientists.createelemancy.content.recipe;


import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import org.madscientists.createelemancy.content.registry.ElemancyRecipes;

public class DistillerRecipe extends BasinRecipe {

	public DistillerRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
		super(ElemancyRecipes.DISTILLING, params);
	}

	@Override
	protected int getMaxFluidOutputCount() {
		return 6;
	}

	@Override
	protected int getMaxFluidInputCount() {
		return 1;
	}

}
