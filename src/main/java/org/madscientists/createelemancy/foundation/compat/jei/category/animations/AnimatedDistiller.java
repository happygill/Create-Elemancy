package org.madscientists.createelemancy.foundation.compat.jei.category.animations;

import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.minecraft.client.gui.GuiGraphics;
import org.madscientists.createelemancy.content.registry.ElemancyBlocks;
import org.madscientists.createelemancy.content.registry.ElemancyPartials;

public class AnimatedDistiller extends AnimatedKinetics {

	@Override
	public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
		PoseStack matrixStack = guiGraphics.pose();
		matrixStack.pushPose();
		matrixStack.translate(xOffset, yOffset, 200);
		matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
		matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
		int scale = 23;


		blockElement(ElemancyBlocks.DISTILLER.getDefaultState())
				.atLocal(0, 0-.25, 0)
				.scale(scale)
				.render(guiGraphics);

		blockElement(ElemancyPartials.DISTILLER_COLUMN)
				.atLocal(0, .75, 0)
				.scale(scale)
				.render(guiGraphics);

		blockElement(AllBlocks.BASIN.getDefaultState())
				.atLocal(0, 1.65, 0)
				.scale(scale)
				.render(guiGraphics);

		matrixStack.popPose();
	}



}
