package org.madscientists.createelemancy.content.registry;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.foundation.gui.UIRenderHelper;
import com.simibubi.create.foundation.gui.element.ScreenElement;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.madscientists.createelemancy.Elemancy;

public enum ElemancyGUI implements ScreenElement {
    INSIGNIA_GUIDE_BACKGROUND("insignia_guide_background", 256, 256);


    public final ResourceLocation location;
    public final int width;
    public final int height;
    public final int startX;
    public final int startY;

    ElemancyGUI(String location, int width, int height) {
        this(location, 0, 0, width, height);
    }

    ElemancyGUI(String location, int startX, int startY, int width, int height) {
        this(Elemancy.ID, location, startX, startY, width, height);
    }

    ElemancyGUI(String namespace, String location, int startX, int startY, int width, int height) {
        this.location = new ResourceLocation(namespace, "textures/gui/" + location + ".png");
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
    }

    @OnlyIn(Dist.CLIENT)
    public void bind() {
        RenderSystem.setShaderTexture(0, location);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y) {
        graphics.blit(location, x, y, startX, startY, width, height);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, Color c) {
        bind();
        UIRenderHelper.drawColoredTexture(graphics, c, x, y, startX, startY, width, height);
    }

}
