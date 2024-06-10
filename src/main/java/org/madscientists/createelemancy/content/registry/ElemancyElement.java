package org.madscientists.createelemancy.content.registry;

import com.simibubi.create.foundation.utility.Lang;
import com.tterrag.registrate.util.entry.FluidEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.ChatFormatting.*;

public enum ElemancyElement implements StringRepresentable {
    REACTIVITY
            (LIGHT_PURPLE, ElemancyFluids.REACTIVITY, ElemancyItems.REACTIVITY_SPICE, 5),

    FRIGID
            (AQUA, ElemancyFluids.FRIGID, ElemancyItems.FRIGID_SPICE, 0x1CC0C5, 2),

    DENSITY
            (DARK_GREEN, ElemancyFluids.DENSITY, ElemancyItems.DENSITY_SPICE, 3),

    FEATHERWEIGHT
            (GOLD, ElemancyFluids.FEATHERWEIGHT, ElemancyItems.FEATHERWEIGHT_SPICE, 4),

    HEAT
            (RED, ElemancyFluids.HEAT, ElemancyItems.HEAT_SPICE, 1),

    STABILITY
            (DARK_BLUE, ElemancyFluids.STABILITY, ElemancyItems.STABILITY_SPICE, 6),
    POWER
            (WHITE, ElemancyFluids.POWER, ElemancyItems.POWER_SPICE, 15588792, 7),
    NULL
            (BLACK, ElemancyFluids.NULL, ElemancyItems.NULL_SPICE, 3618615, 8);

    static {
        REACTIVITY.setOpposite(STABILITY);
        FRIGID.setOpposite(HEAT);
        DENSITY.setOpposite(FEATHERWEIGHT);
        POWER.setOpposite(NULL);
        STABILITY.setOpposite(REACTIVITY);
        HEAT.setOpposite(FRIGID);
        FEATHERWEIGHT.setOpposite(DENSITY);
        NULL.setOpposite(POWER);
	}


    public static final int PRIMARY_ELEMENT_LENGTH = 6;
    private final ChatFormatting colorFormatting;
	private final int color;
	private final int insigniaMapping;
	private ElemancyElement opposite;

	private final FluidEntry<ForgeFlowingFluid.Flowing> liquid;
	private final ItemEntry<Item> spice;


	ElemancyElement(@NotNull ChatFormatting colorFormatting, FluidEntry<ForgeFlowingFluid.Flowing> liquid, ItemEntry<Item> spice, int insigniaMapping) {
		this(colorFormatting, liquid, spice, colorFormatting.getColor(),insigniaMapping);
	}

	ElemancyElement(ChatFormatting colorFormatting, FluidEntry<ForgeFlowingFluid.Flowing> liquid, ItemEntry<Item> spice, int color, int insigniaMapping) {
		if(!colorFormatting.isColor())
			throw new IllegalArgumentException("Element color formatting wasn't a color!");
		this.colorFormatting = colorFormatting;
		this.liquid = liquid;
		this.spice = spice;
		this.color = color;
		this.insigniaMapping = insigniaMapping;
	}

	private void setOpposite(ElemancyElement opposite) {
		this.opposite = opposite;
	}

	public ElemancyElement getOpposite() {
		return opposite;
	}

    public static ElemancyElement getByInsigniaCode(String insigniaCode) {
        for (ElemancyElement elemancyElement : ElemancyElement.values()) {
            if (elemancyElement.getInsigniaMapping() == Integer.parseInt(insigniaCode.substring(2)))
                return elemancyElement;
        }
        return null;
    }

    static String ElementNBTTag = "elemanacy_element";

    public static ElemancyElement getElement(int id) {
        return values()[id];
    }

    public static void saveElement(ItemStack stack, ElemancyElement elemancyElement) {
        CompoundTag tag = stack.getOrCreateTag();
        if (elemancyElement != null)
            tag.putInt(ElementNBTTag, elemancyElement.getId());
        else
            tag.putInt(ElementNBTTag, -1);
    }

    public static ElemancyElement getElement(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(ElementNBTTag)) {
            int e = tag.getInt(ElementNBTTag);
            if (e < values().length && e >= 0)
                return getElement(e);

        }
        return null;
    }

    public int getId() {
        return ordinal();
    }


    public int getColor() {
        return color;
    }

    public static int getLength() {
        return values().length;
    }

    public static ElemancyElement getByID(int id) {
        return values()[id];
    }

    public Fluid getLiquid() {
        return liquid.getSource();
    }

    public Item getSpice() {
        return spice.get();
    }

    public ChatFormatting getColorFormatting() {
        return colorFormatting;
    }

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    public int getInsigniaMapping() {
        return insigniaMapping;
    }

    @Override
    public String getSerializedName() {
        return Lang.asId(name());
    }

}
