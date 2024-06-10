package org.madscientists.createelemancy.content.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.madscientists.createelemancy.content.insignia.InsigniaUtils;
import org.madscientists.createelemancy.content.registry.ElemancyElement;

import java.util.Random;

import static org.madscientists.createelemancy.content.item.RunicInsigniaItem.INSIGNIA_NBT_KEY;

public class RunicItemRenderer extends CustomRenderedItemModelRenderer {
    private static final ResourceLocation ALT_FONT = new ResourceLocation("minecraft", "alt");
    private static final Style ROOT_STYLE=Style.EMPTY.withFont(ALT_FONT);

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        renderer.render(model.getOriginalModel(), RenderType.cutoutMipped(), light);
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(INSIGNIA_NBT_KEY)) return;

        ElemancyElement elemancyElement = ElemancyElement.getByInsigniaCode(InsigniaUtils.loadLinesFromNBT(tag.getCompound(INSIGNIA_NBT_KEY)).get(0));
        if (elemancyElement == null) return;
        ms.pushPose();
        ms.translate(0,0,.05);
        ms.mulPose(Axis.YP.rotationDegrees(180));
        ms.scale(.075f,.075f,.075f);
        ms.translate(-2,-3,0);
        Random r = new Random();
        char c = (char)(r.nextInt(26) + 'a');
        if(tag.contains("letter"))
            c = tag.getString("letter").charAt(0);
        else
            tag.putString("letter", String.valueOf(c));

        Font fontRenderer = Minecraft.getInstance().font;
        fontRenderer.drawInBatch(FormattedCharSequence.forward(String.valueOf(c),ROOT_STYLE), 0, 0, elemancyElement.getColor(), false, ms.last()
                .pose(), buffer, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
        ms.popPose();
    }

}
