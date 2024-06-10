package org.madscientists.createelemancy.content.block.vortex;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class ElemancyFluidTank extends FluidTank {

	protected boolean extractionAllowed = true;
	protected boolean insertionAllowed = true;
	public ElemancyFluidTank(int capacity) {
		super(capacity);
	}

	@Override
	public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
		return extractionAllowed ? super.drain(resource, action) : FluidStack.EMPTY;
	}

	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
		return extractionAllowed ? super.drain(maxDrain, action) : FluidStack.EMPTY;
	}

	@Override
	public boolean isFluidValid(FluidStack stack) {
		return insertionAllowed || super.isFluidValid(stack);
	}

	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
		return insertionAllowed || super.isFluidValid(tank, stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return insertionAllowed ? super.fill(resource, action) : 0;
	}

	public ElemancyFluidTank allowInsertion() {
		insertionAllowed = true;
		return this;
	}

	public ElemancyFluidTank allowExtraction() {
		extractionAllowed = true;
		return this;
	}

	public ElemancyFluidTank forbidInsertion() {
		insertionAllowed = false;
		return this;
	}

	public ElemancyFluidTank forbidExtraction() {
		extractionAllowed = false;
		return this;
	}
}
