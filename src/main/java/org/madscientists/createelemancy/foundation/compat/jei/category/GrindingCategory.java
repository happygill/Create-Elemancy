package org.madscientists.createelemancy.foundation.compat.jei.category;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.ponder.ui.LayoutHelper;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.madscientists.createelemancy.foundation.compat.jei.category.animations.AnimatedGrindingWheels;
import org.madscientists.createelemancy.content.recipe.GrindingRecipe;
import org.madscientists.createelemancy.content.registry.ElemancyElement;

import java.util.ArrayList;
import java.util.List;

import static org.madscientists.createelemancy.content.item.MixedSpiceItem.ELEMENTS_TAG_KEY;

public class GrindingCategory extends CreateRecipeCategory<GrindingRecipe> {

	private final AnimatedGrindingWheels grindingWheels = new AnimatedGrindingWheels();

	public GrindingCategory(Info<GrindingRecipe> info) {
		super(info);
	}

	@Override
	public Component getTitle() {
		return Component.literal("Grinding");
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, GrindingRecipe recipe, IFocusGroup focuses) {
		builder
				.addSlot(RecipeIngredientRole.INPUT, 31, 3)
				.setBackground(getRenderedSlot(), -1, -1)
				.addIngredients(recipe.getIngredients().get(0));

		int xOffset = getBackground().getWidth() / 2 -20;
		int yOffset = 86;

		layoutOutput(recipe).forEach(layoutEntry -> builder
				.addSlot(RecipeIngredientRole.OUTPUT, (xOffset) + layoutEntry.posX() + 1, yOffset + layoutEntry.posY() + 1)
				.setBackground(getRenderedSlot(layoutEntry.output()), -1, -1)
				.addItemStack(layoutEntry.output().getStack())
				.addTooltipCallback(addStochasticTooltip(layoutEntry.output()))
		);
	}

	private List<GrindingCategory.LayoutEntry> layoutOutput(GrindingRecipe recipe) {
		int size = recipe.getRollableResults().size();
		List<GrindingCategory.LayoutEntry> positions = new ArrayList<>(size);

		LayoutHelper layout = LayoutHelper.centeredHorizontal(size, 1, 18, 18, 1);
		for (ProcessingOutput result : recipe.getRollableResults()) {
			positions.add(new GrindingCategory.LayoutEntry(result, layout.getX(), layout.getY()));
			layout.next();
		}

		return positions;
	}

	private record LayoutEntry(
			ProcessingOutput output,
			int posX,
			int posY
	) {}

	@Override
	public void draw(GrindingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
		AllGuiTextures.JEI_DOWN_ARROW.render(guiGraphics, 52, 7);

		ItemStack pStack=recipe.getResultItem(Minecraft.getInstance().level.registryAccess());
		CompoundTag tag= pStack.getOrCreateTag();
		int elementCount = 0;
		int textCenterX = 80;
		int textCenterY = 75;
		if(tag.contains(ELEMENTS_TAG_KEY)) {
			int[] elements = tag.getIntArray(ELEMENTS_TAG_KEY);
			for (int i = 0; i < elements.length; i++) {
				if(elements[i] > 0) {
					String text = ElemancyElement.values()[i].toString()+ ":" + elements[i];
					int color = ElemancyElement.values()[i].getColor();
					guiGraphics.drawString(Minecraft.getInstance().font, text, textCenterX,
							textCenterY+(10*elementCount),color, false);
					elementCount++;
				}
			}
		}
		RenderSystem.setShaderColor(1, 1, 1, 1);
		grindingWheels.draw(guiGraphics, 42, 59);
	}

}
