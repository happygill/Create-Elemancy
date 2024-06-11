package org.madscientists.createelemancy.content.screen;

import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.madscientists.createelemancy.content.insignia.InsigniaPattern;
import org.madscientists.createelemancy.content.registry.ElemancyGUI;

import java.util.Arrays;
import java.util.List;

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
        String ground = "Right-click ground for pattern";
        graphics.drawString(font, ground, (float) (x + (background.width - 8) / 2 - font.width(ground) / 2), (float) y + 79 + lineCount * lineHeight, 0x000000, false);


    }

}
