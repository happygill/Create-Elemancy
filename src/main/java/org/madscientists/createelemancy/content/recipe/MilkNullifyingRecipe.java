package org.madscientists.createelemancy.content.recipe;

import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.madscientists.createelemancy.content.item.MixedSpiceItem;

import java.util.List;

public class MilkNullifyingRecipe extends FillingRecipe {

	public MilkNullifyingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
		super(params);
	}

	@Override
	public boolean matches(RecipeWrapper inv, Level level) {
		if(!super.matches(inv, level)) return false;

		ItemStack spice = inv.getItem(0).copy();
		enforceNextResult(() -> MixedSpiceItem.milkNullify(spice));

		return true;
	}

}