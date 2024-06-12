package org.madscientists.createelemancy.content.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.madscientists.createelemancy.content.insignia.InsigniaPattern;
import org.madscientists.createelemancy.content.insignia.InsigniaUtils;
import org.madscientists.createelemancy.content.registry.ElemancyGUI;

import java.util.Arrays;
import java.util.List;

import static org.madscientists.createelemancy.content.insignia.InsigniaUtils.lineMappings;
import static org.madscientists.createelemancy.content.insignia.InsigniaUtils.textureList5x5;

public class InsigniaScreen extends AbstractSimiScreen {
    private final ElemancyGUI background = ElemancyGUI.INSIGNIA_GUIDE_BACKGROUND;
    InsigniaPattern pattern;

    @Override
    public void init() {
        setWindowSize(background.width, background.height);
        super.init();
    }

    public InsigniaScreen(InsigniaPattern guide) {
        super(Component.literal("Insignia Guide"));
        pattern = guide;
    }


    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;
        background.render(graphics, x, y);


        graphics.drawString(font, String.valueOf(pattern.getName()), (float) (x + (background.width - 8) / 2 - font.width(String.valueOf(pattern.getName())) / 2), (float) y + 29, 0x000000, false);
        renderDescription(graphics, x, y, pattern.getDescription());
    }


    private void renderDescription(GuiGraphics graphics, int x, int y, String description) {
        List<String> words = Arrays.asList(description.split(" "));
        StringBuilder line = new StringBuilder();
        int lineHeight = 10; // adjust this value based on your font size
        int lineWidth = 0;
        int maxLineWidth = background.width - 8; // maximum line width
        int lineCount = 0;

        for (String word : words) {
            int wordWidth = font.width(word + " ");
            if (lineWidth + wordWidth > maxLineWidth) {
                graphics.drawString(font, line.toString(), (float) x + 20, (float) y + 49 + lineCount * lineHeight, 0x000000, false);
                line = new StringBuilder();
                lineWidth = 0;
                lineCount++;
            }
            line.append(word).append(" ");
            lineWidth += wordWidth;
        }
        // Draw the last line
        graphics.drawString(font, line.toString(), (float) x + 20, (float) y + 49 + lineCount * lineHeight, 0x000000, false);

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.scale(30, 30, 30);
        poseStack.translate((double) Minecraft.getInstance().getWindow().getGuiScaledWidth() / 58 - 1, (double) Minecraft.getInstance().getWindow().getGuiScaledHeight() / 59 + 1, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        for (String code : pattern.getCodes()) {
            poseStack.pushPose();
            transformByte(code, poseStack);
            Minecraft.getInstance().getItemRenderer().renderModelLists(InsigniaUtils.getModel(code).get(), ItemStack.EMPTY, 1000, OverlayTexture.NO_OVERLAY, poseStack, graphics.bufferSource().getBuffer(RenderType.cutoutMipped()));
            poseStack.popPose();
        }
        poseStack.popPose();

    }

    private static void transformByte(String code, PoseStack byteBuffer) {
        byteBuffer.translate(0, .01, 0);

        if (textureList5x5.contains(lineMappings.get(code.substring(0, 2))))
            byteBuffer.translate(0.5, 0, 0.5);


        //l1
        if (code.startsWith("DI")) {
            byteBuffer.translate(.5f, .5f, .5f);
            byteBuffer.mulPose(Axis.YP.rotation(Mth.PI / 2));
            byteBuffer.translate(-.5f, -.5f, -.5f);
        }

        //l2
        if (code.startsWith("SY")) {
            byteBuffer.translate(.5f, .5f, .5f);
            byteBuffer.mulPose(Axis.YP.rotation(Mth.PI / 2));
            byteBuffer.translate(-.5f, -.5f, -.5f);
        }

        //l3
        if (code.startsWith("DH")) {
            byteBuffer.translate(.5f, .5f, .5f);
            byteBuffer.mulPose(Axis.YP.rotation(Mth.PI / 2));
            byteBuffer.translate(-.5f, -.5f, -.5f);

        }
        if (code.startsWith("DF")) {
            byteBuffer.translate(.5f, .5f, .5f);
            byteBuffer.mulPose(Axis.YP.rotation(-Mth.PI / 2));
            byteBuffer.translate(-.5f, -.5f, -.5f);
        }
        if (code.startsWith("DG")) {
            byteBuffer.translate(.5f, .5f, .5f);
            byteBuffer.mulPose(Axis.YP.rotation(Mth.PI));
            byteBuffer.translate(-.5f, -.5f, -.5f);
        }

        //l4
        if (code.startsWith("SM")) {
            byteBuffer.translate(.5f, .5f, .5f);
            byteBuffer.mulPose(Axis.YP.rotation(Mth.PI / 2));
            byteBuffer.translate(-.5f, -.5f, -.5f);
        }
        if (code.startsWith("SS")) {
            byteBuffer.translate(.5f, .5f, .5f);
            byteBuffer.mulPose(Axis.YP.rotation(-Mth.PI / 2));
            byteBuffer.translate(-.5f, -.5f, -.5f);
        }
        if (code.startsWith("SV")) {
            byteBuffer.translate(.5f, .5f, .5f);
            byteBuffer.mulPose(Axis.YP.rotation(Mth.PI));
            byteBuffer.translate(-.5f, -.5f, -.5f);
        }

        //l5
        if (code.startsWith("SO")) {
            byteBuffer.translate(.5f, .5f, .5f);
            byteBuffer.mulPose(Axis.YP.rotation(Mth.PI / 2));
            byteBuffer.translate(-.5f, -.5f, -.5f);
        }
        if (code.startsWith("SU")) {
            byteBuffer.translate(.5f, .5f, .5f);
            byteBuffer.mulPose(Axis.YP.rotation(-Mth.PI / 2));
            byteBuffer.translate(-.5f, -.5f, -.5f);

        }
        if (code.startsWith("SX")) {
            byteBuffer.translate(.5f, .5f, .5f);
            byteBuffer.mulPose(Axis.YP.rotation(Mth.PI));
            byteBuffer.translate(-.5f, -.5f, -.5f);
        }
        //l6
        if (code.startsWith("SQ")) {
            byteBuffer.translate(.5f, .5f, .5f);
            byteBuffer.mulPose(Axis.YP.rotation(Mth.PI / 2));
            byteBuffer.translate(-.5f, -.5f, -.5f);
        }
        if (code.startsWith("SW")) {
            byteBuffer.translate(.5f, .5f, .5f);
            byteBuffer.mulPose(Axis.YP.rotation(-Mth.PI / 2));
            byteBuffer.translate(-.5f, -.5f, -.5f);
        }
        if (code.startsWith("SN")) {
            byteBuffer.translate(.5f, .5f, .5f);
            byteBuffer.mulPose(Axis.YP.rotation(Mth.PI));
            byteBuffer.translate(-.5f, -.5f, -.5f);
        }

        //center ^

        //l7
        if (code.startsWith("SC")) {
            byteBuffer.mulPose(Axis.YP.rotation(Mth.PI / 2));

        }
        if (code.startsWith("SI")) {
            byteBuffer.mulPose(Axis.YP.rotation(-Mth.PI / 2));
        }
        if (code.startsWith("SL")) {
            byteBuffer.mulPose(Axis.YP.rotation(Mth.PI));
        }

        //l8
        if (code.startsWith("SD")) {
            byteBuffer.mulPose(Axis.YP.rotation(Mth.PI / 2));

        }
        if (code.startsWith("SJ")) {
            byteBuffer.mulPose(Axis.YP.rotation(-Mth.PI / 2));
        }
        if (code.startsWith("SA")) {
            byteBuffer.mulPose(Axis.YP.rotation(Mth.PI));
        }

        //l9
        if (code.startsWith("QD")) {
            byteBuffer.mulPose(Axis.YP.rotation(Mth.PI / 2));

        }
        if (code.startsWith("QL")) {
            byteBuffer.mulPose(Axis.YP.rotation(-Mth.PI / 2));
        }
        if (code.startsWith("QP")) {
            byteBuffer.mulPose(Axis.YP.rotation(Mth.PI));
        }

        //l12
        if (code.startsWith("QE")) {
            byteBuffer.mulPose(Axis.YP.rotation(Mth.PI / 2));

        }
        if (code.startsWith("QM")) {
            byteBuffer.mulPose(Axis.YP.rotation(-Mth.PI / 2));
        }
        if (code.startsWith("QA")) {
            byteBuffer.mulPose(Axis.YP.rotation(Mth.PI));
        }

        //l13
        if (code.startsWith("SE")) {
            byteBuffer.mulPose(Axis.YP.rotation(Mth.PI / 2));

        }
        if (code.startsWith("SK")) {
            byteBuffer.mulPose(Axis.YP.rotation(-Mth.PI / 2));
        }
        if (code.startsWith("SB")) {
            byteBuffer.mulPose(Axis.YP.rotation(Mth.PI));
        }

        //l10
        if (code.startsWith("QC")) {
            byteBuffer.mulPose(Axis.YP.rotation(Mth.PI / 2));

        }
        if (code.startsWith("QK")) {
            byteBuffer.mulPose(Axis.YP.rotation(-Mth.PI / 2));
        }
        if (code.startsWith("QO")) {
            byteBuffer.mulPose(Axis.YP.rotation(Mth.PI));
        }

        //l14
        if (code.startsWith("QF")) {
            byteBuffer.mulPose(Axis.YP.rotation(Mth.PI / 2));

        }
        if (code.startsWith("QN")) {
            byteBuffer.mulPose(Axis.YP.rotation(-Mth.PI / 2));
        }
        if (code.startsWith("QB")) {
            byteBuffer.mulPose(Axis.YP.rotation(Mth.PI));
        }

        //l11
        if (code.startsWith("DA")) {
            byteBuffer.mulPose(Axis.YP.rotation(Mth.PI / 2));

        }
        if (code.startsWith("DC")) {
            byteBuffer.mulPose(Axis.YP.rotation(-Mth.PI / 2));
        }
        if (code.startsWith("DD")) {
            byteBuffer.mulPose(Axis.YP.rotation(Mth.PI));
        }
    }

}
