package org.madscientists.createelemancy.foundation.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.madscientists.createelemancy.foundation.compat.jei.category.animations.AnimatedDistiller;

public class DistillingCategory extends BasinCategory {

	private final AnimatedDistiller distiller = new AnimatedDistiller();
	private final AnimatedBlazeBurner heater = new AnimatedBlazeBurner();

	@Override
	public Component getTitle() {
		return Component.literal("Distilling");
	}

	public DistillingCategory(Info<BasinRecipe> distillerRecipeInfo) {
		super(distillerRecipeInfo, true);
	}

	@Override
	public void draw(BasinRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);
		HeatCondition requiredHeat = recipe.getRequiredHeat();

		if (requiredHeat != HeatCondition.NONE)
			heater.withHeat(requiredHeat.visualizeAsBlazeBurner())
					.draw(graphics, getBackground().getWidth() / 2 + 3, 55);
		distiller.draw(graphics, getBackground().getWidth() / 2 + 3, 34);
	}


}