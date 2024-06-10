package org.madscientists.createelemancy.content.block.distiller;

import com.jozufozu.flywheel.backend.Backend;
import com.jozufozu.flywheel.util.Color;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import org.madscientists.createelemancy.content.registry.ElemancyPartials;

import java.util.function.Function;

import static com.simibubi.create.foundation.fluid.FluidRenderer.*;

public class DistillerRenderer extends SafeBlockEntityRenderer<DistillerBlockEntity> {


	public static int COLUMN_FLUID_TRANSPARENCY =75;
	public DistillerRenderer(BlockEntityRendererProvider.Context context) {}

	@Override
	protected void renderSafe(DistillerBlockEntity distillerBlock, float partialTicks, PoseStack ms,
							  MultiBufferSource buffer, int light, int overlay) {

		renderTopChamber(distillerBlock, partialTicks, ms, buffer, light);
		renderBottomChamber(distillerBlock, partialTicks, ms, buffer, light);


		if (Backend.canUseInstancing(distillerBlock.getLevel())) return;

		BlockState blockState = distillerBlock.getBlockState();
		VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());

		SuperByteBuffer columnRender = CachedBufferer.partial(ElemancyPartials.DISTILLER_COLUMN, blockState);
		columnRender.translate(0, -1.00f, 0)
				.light(light)
				.renderInto(ms, vb);
	}

	protected void renderTopChamber(DistillerBlockEntity distillerBlock, float partialTicks, PoseStack ms,
									MultiBufferSource buffer, int light) {

		SmartFluidTankBehaviour tank = distillerBlock.getInternalTank();
		if(tank == null)
			return;

		SmartFluidTankBehaviour.TankSegment primaryTank = tank.getPrimaryTank();
		FluidStack fluidStack = primaryTank.getRenderedFluid();
		float level = primaryTank.getFluidLevel().getValue(partialTicks);

		if (!fluidStack.isEmpty() && level != 0) {
			boolean top = fluidStack.getFluid()
					.getFluidType()
					.isLighterThanAir();

			level = Math.max(level, 0.175f);
			float min = 2.5f / 16f;
			float max = min + (11 / 16f);
			float yOffset = (11 / 16f) * level;

			ms.pushPose();
			if(top)
				ms.translate(0, max - min, 0);
			else ms.translate(0, yOffset, 0);

			FluidRenderer.renderFluidBox(fluidStack,
					min, min - yOffset, min,
					max, min, max,
					buffer, ms, light, top);
			ms.popPose();
		}
	}

	protected void renderBottomChamber(DistillerBlockEntity distillerBlock, float partialTicks, PoseStack ms,
									MultiBufferSource buffer, int light) {
		if (distillerBlock == null || distillerBlock.getBasin().isEmpty())
			return;

		FluidStack fluidStack = distillerBlock.getFluid(distillerBlock.getBasin().get());
		if(fluidStack.isEmpty())
			return;


		float value = (float) distillerBlock.processingTicks * 2.0F / DistillerBlockEntity.PROCESSING_TIME;
		if(value == 0.0)
			return;

		ms.translate(0, -1f, 0);
		renderFluidStream(fluidStack, Direction.UP, 2.9f / 16f, value, true, buffer.getBuffer(RenderType.translucent()), ms, light);
	}

	@Override
	public int getViewDistance() {
		return 16;
	}

	public static void renderFluidStream(FluidStack fluidStack, Direction direction, float radius, float progress,
										 boolean inbound, VertexConsumer builder, PoseStack ms, int light) {
		Fluid fluid = fluidStack.getFluid();
		IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(fluid);
		FluidType fluidAttributes = fluid.getFluidType();
		Function<ResourceLocation, TextureAtlasSprite> spriteAtlas = Minecraft.getInstance()
				.getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
		TextureAtlasSprite flowTexture = spriteAtlas.apply(clientFluid.getFlowingTexture(fluidStack));
		TextureAtlasSprite stillTexture = spriteAtlas.apply(clientFluid.getStillTexture(fluidStack));

		Color color = new Color(255, 255, 255, COLUMN_FLUID_TRANSPARENCY);


		int blockLightIn = (light >> 4) & 0xF;
		int luminosity = Math.max(blockLightIn, fluidAttributes.getLightLevel(fluidStack));
		light = (light & 0xF00000) | luminosity << 4;

		if (inbound)
			direction = direction.getOpposite();

		TransformStack msr = TransformStack.cast(ms);
		ms.pushPose();
		msr.centre()
				.rotateY(AngleHelper.horizontalAngle(direction))
				.rotateX(direction == Direction.UP ? 180 : direction == Direction.DOWN ? 0 : 270)
				.unCentre();
		ms.translate(0.5, 0, 0.5);

		float h = radius;
		float hMin = -radius;
		float hMax = radius;
		float y = inbound ? 1 : 0.5f;
		float yMin = y - Mth.clamp(progress * 0.5f, 0, 1);
		float yMax = y;

		for (int i = 0; i < 4; i++) {
			ms.pushPose();
			renderFlowingTiledFace(Direction.SOUTH, hMin, yMin, hMax, yMax, h, builder, ms, light, 	color.getRGB(), flowTexture);
			ms.popPose();
			msr.rotateY(90);
		}

		if (progress != 1)
			renderStillTiledFace(Direction.DOWN, hMin, hMin, hMax, hMax, yMin, builder, ms, light, 	color.getRGB(), stillTexture);

		ms.popPose();
	}
}
