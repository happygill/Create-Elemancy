package org.madscientists.createelemancy.content.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import org.madscientists.createelemancy.content.registry.ElemancyElement;

import java.util.*;

import static org.madscientists.createelemancy.content.registry.ElemancyElement.PRIMARY_ELEMENT_LENGTH;

public class MixedSpiceItem extends Item implements IAdditionalCreativeItems {
	public static final String ELEMENTS_TAG_KEY = "Elemancy_Elements";

	public MixedSpiceItem(Item.Properties props) {
		super(props);
	}


	public static int getElementValue(ItemStack pStack, ElemancyElement elemancyElement) {
		CompoundTag tag = pStack.getOrCreateTag();
		if (!tag.contains(ELEMENTS_TAG_KEY)) return 0;
		int[] elements = tag.getIntArray(ELEMENTS_TAG_KEY);
		if(elemancyElement.ordinal() >= elements.length) return 0;
		return tag.getIntArray(ELEMENTS_TAG_KEY)[elemancyElement.ordinal()];
	}

	public static ItemStack milkNullify(ItemStack pStack) {
		CompoundTag tag = pStack.getOrCreateTag();
		if(tag.contains(ELEMENTS_TAG_KEY)) {
			int[] elements = tag.getIntArray(ELEMENTS_TAG_KEY);

			for (int i = 0; i < 6; i++)
				if(elements[i] > 0)
					elements[i]--;

			tag.putIntArray(ELEMENTS_TAG_KEY,elements);

		}
		return isSingleElement(pStack) ? convertRefined(pStack) : pStack;
	}
	public static ItemStack distill(ItemStack pStack) {
		ItemStack spice = pStack.copy();
		if(pStack.getOrCreateTag().contains(ELEMENTS_TAG_KEY)) {
			assert pStack.getTag() != null;
			int[] elements = pStack.getTag().getIntArray(ELEMENTS_TAG_KEY);
			for (int i = 0; i < PRIMARY_ELEMENT_LENGTH; i++) {
				if(elements[i] > 0) {
					elements[i]--;
					break;
				}
			}
			spice.getOrCreateTag().putIntArray(ELEMENTS_TAG_KEY, elements);
		}
		return isSpiceEmpty(spice) ? ItemStack.EMPTY: spice;
	}

	public static FluidStack getDistillResults(ItemStack pStack) {
		int multiplier = 100;
		RandomSource rand = RandomSource.create();
		boolean shouldReduce = rand.nextDouble() < 0.75;

		if(shouldReduce)
			multiplier -= rand.nextInt(75);

		for (int i = 0; i < PRIMARY_ELEMENT_LENGTH; i++)
			if(getElementValue(pStack, ElemancyElement.values()[i]) >= 1)
				return new FluidStack(ElemancyElement.values()[i].getLiquid(), pStack.getCount() * multiplier);

		return new FluidStack(FluidStack.EMPTY,0);
	}

	public static ItemStack removeDistillResult(ItemStack pStack) {
		CompoundTag tag = pStack.getOrCreateTag();

		if(tag.contains(ELEMENTS_TAG_KEY)) {
			boolean hasRemoved = false;
			boolean hasNext = false;

			int[] elementData = tag.getIntArray(ELEMENTS_TAG_KEY);

			for (int i = 0; i < elementData.length; i++) {
				int elementCount = elementData[i];
				if (elementCount == 0) continue;

				if (!hasRemoved)
					elementData[i] -= 1;
				if (elementData[i] > 0)
					hasNext = true;
				break;
			}

			if (!hasNext) return ItemStack.EMPTY;

			tag.putIntArray(ELEMENTS_TAG_KEY, elementData);
			pStack.setTag(tag);
		}

		return pStack;
	}

	private static ItemStack convertRefined(ItemStack pStack) {
		int elementValue=0;
		for (int i = 0; i < PRIMARY_ELEMENT_LENGTH; i++) {
			elementValue = getElementValue(pStack, ElemancyElement.values()[i]);
			if (elementValue > 0)
				return new ItemStack(ElemancyElement.values()[i].getSpice(), elementValue);
		}
		return new ItemStack(Items.SUGAR);
	}


	public static boolean isSingleElement(ItemStack pStack) {
		CompoundTag tag = pStack.getOrCreateTag();
		if(isSpiceEmpty(pStack))
			return true;

		int[] elements = tag.getIntArray(ELEMENTS_TAG_KEY);

		return Arrays.stream(elements)
				.filter(element -> element > 0)
				.count() == 1;
	}

	public static boolean isSpiceEmpty(ItemStack pStack) {
		CompoundTag tag = pStack.getOrCreateTag();
		if(!tag.contains(ELEMENTS_TAG_KEY))
			return true;

		int[] elements = tag.getIntArray(ELEMENTS_TAG_KEY);

		return Arrays.stream(elements)
				.filter(element -> element > 0)
				.count() == 0;
	}

	@Override
	public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
		CompoundTag tag = pStack.getOrCreateTag();
		if(tag.contains(ELEMENTS_TAG_KEY)) {
			int[] elements = tag.getIntArray(ELEMENTS_TAG_KEY);
			for (int i = 0; i < PRIMARY_ELEMENT_LENGTH; i++)
				if(elements[i] > 0)
					pTooltipComponents.add(Component.literal(ElemancyElement.values()[i].name()+ ":" + elements[i])
							.withStyle(ElemancyElement.values()[i].getColorFormatting()));
		}
		super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
	}

	@Override
	public void addCreativeDevItems(List<ItemStack> pItems) {
		ItemStack mixed = new ItemStack(this);
		ItemStack mixed2 = new ItemStack(this);
		ItemStack mixed3 = new ItemStack(this);
		mixed.getOrCreateTag().putIntArray(ELEMENTS_TAG_KEY, new int[]{4, 2, 1, 0, 0, 0});
		mixed2.getOrCreateTag().putIntArray(ELEMENTS_TAG_KEY, new int[]{0, 0, 0, 8, 7, 6});
		mixed3.getOrCreateTag().putIntArray(ELEMENTS_TAG_KEY, new int[]{3, 0, 0, 0, 0, 0});
		Collections.addAll(pItems, mixed, mixed2, mixed3);
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return 1;
	}
}
