package org.madscientists.createelemancy.content.insignia;

import com.jozufozu.flywheel.util.Color;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.madscientists.createelemancy.content.registry.ElemancyElement;

public class InsigniaRenderer extends SafeBlockEntityRenderer<InsigniaBlockEntity> {
	public InsigniaRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	protected void renderSafe(InsigniaBlockEntity be, float partialTicks,
							  PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {

		if(be.lineRenderingList.isEmpty()&&be.ghostLineRenderingList.isEmpty()&&be.guideLineRenderingList.isEmpty()) return;

		VertexConsumer vc = bufferSource.getBuffer(RenderType.translucentNoCrumbling());

		for (int i = 0; i < be.lineRenderingList.size(); i++) {
			SuperByteBuffer render = InsigniaUtils.getSuperByte(be.lineRenderingList.get(i), be.getBlockState());
			render.translate(0, .0001 * (i + 1), 0);
			color(render, be.lineRenderingList.get(i), false);
			render.light(light).renderInto(ms, vc);
		}

		for (int i = 0; i < be.ghostLineRenderingList.size(); i++) {
			SuperByteBuffer ghostRender = InsigniaUtils.getSuperByte(be.ghostLineRenderingList.get(i), be.getBlockState());
			ghostRender.translate(0, .0001 * (i + 3 + be.guideLineRenderingList.size() + be.lineRenderingList.size()), 0);
			ghostRender.color(0, 0, 0, 155);
			color(ghostRender, be.ghostLineRenderingList.get(i), true);
			if (be.isGhost()) {
				ghostRender.translate(be.getGhostOffset().x, 0.0001, be.getGhostOffset().z);
			}
			ghostRender.light(light).renderInto(ms, vc);
		}
		for (int i = 0; i < be.guideLineRenderingList.size(); i++) {
			SuperByteBuffer guideRender = InsigniaUtils.getSuperByte(be.guideLineRenderingList.get(i), be.getBlockState());
			guideRender.translate(0, .0001 * (i + 2 + be.lineRenderingList.size()), 0);
			color(guideRender, be.guideLineRenderingList.get(i), true);
			guideRender.light(light).renderInto(ms, vc);
		}


	}

	private void color(SuperByteBuffer model, String code, boolean transparent) {
		int element = Integer.parseInt(code.substring(2));
		if (element != 5 && element != 7&&element!=3&&element!=1) {
			Color c = new Color(ElemancyElement.getByInsigniaCode(code).getColor());
			if (transparent)
				model.color(c.getRed(), c.getGreen(), c.getBlue(), 125);
			else
				model.color(c.getRGB());
			return;
		}
		if (transparent)
			model.color(255, 255, 255, 125);
	}

}
