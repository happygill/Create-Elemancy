package org.madscientists.createelemancy.foundation.compat.jei.category.animations;

import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.minecraft.client.gui.GuiGraphics;
import org.madscientists.createelemancy.content.registry.ElemancyPartials;

public class AnimatedGrindingWheels extends AnimatedKinetics {

	@Override
	public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
		PoseStack matrixStack = guiGraphics.pose();
		matrixStack.pushPose();
		matrixStack.translate(xOffset, yOffset, 100);
		matrixStack.mulPose(Axis.YP.rotationDegrees(25f));
		int scale = 22;

		blockElement(ElemancyPartials.GRINDING_WHEEL_PARTIAL)
				.rotateBlock(-getCurrentAngle(), -90, 0)
				.scale(scale)
				.render(guiGraphics);

		blockElement(ElemancyPartials.GRINDING_WHEEL_PARTIAL)
				.rotateBlock(getCurrentAngle(), 90, 0)
				.atLocal(1.5, 0, 0)
				.scale(scale)
				.render(guiGraphics);

		matrixStack.popPose();
	}


}
