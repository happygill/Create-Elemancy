package org.madscientists.createelemancy.content.block.elemental;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.world.phys.Vec3;
import org.madscientists.createelemancy.content.registry.ElemancyBlocks;

public class ElementalWallRenderer extends SafeBlockEntityRenderer<ElementalBlockEntity> {
	public ElementalWallRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	protected void renderSafe(ElementalBlockEntity be, float partialTicks,
							  PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
		if(be.getBlockState().is(ElemancyBlocks.SCORCHED_FIRE.get())) return;
		if(be.getDurationTicks()==-1) return;

		if (be.durationTicks < 200) {
			VertexConsumer vc = new SheetedDecalTextureGenerator(bufferSource.getBuffer(ModelBakery.DESTROY_TYPES.get(9 - Math.min(be.durationTicks / 20, 9))), ms.last().pose(), ms.last().normal(),1);
			net.minecraftforge.client.model.data.ModelData modelData = be.getLevel().getModelDataManager().getAt(be.getBlockPos());
			Minecraft.getInstance().getBlockRenderer().renderBreakingTexture(be.getBlockState(), be.getBlockPos(), be.getLevel(), ms, vc, modelData);
		}


	}

	@Override
	public boolean shouldRender(ElementalBlockEntity pBlockEntity, Vec3 pCameraPos) {
		return true;
	}

	@Override
	public boolean shouldRenderOffScreen(ElementalBlockEntity pBlockEntity) {
		return true;
	}
}
