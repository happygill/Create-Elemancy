package org.madscientists.createelemancy.content.recipe;

import com.google.gson.JsonObject;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.utility.Couple;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.madscientists.createelemancy.content.block.vortex.ElemancyFluidTank;
import org.madscientists.createelemancy.content.registry.ElemancyRecipes;

public class VortexGenRecipe extends ProcessingRecipe<RecipeWrapper> {

    public int suOutput=0;
    public int burnTicks=0;
    public int rpm=0;

    public VortexGenRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(ElemancyRecipes.VORTEX_GEN,params);
    }

    @Override
    protected int getMaxInputCount() {
        return 0;
    }

    @Override
    protected int getMaxOutputCount() {
        return 0;
    }

    @Override
    protected int getMaxFluidInputCount() {
        return 2;
    }

    @Override
    protected int getMaxFluidOutputCount() {
        return 1;
    }

    @Override
    public boolean matches(RecipeWrapper container, Level level) {
        return false;
    }


    public boolean fuelMatches(Couple<ElemancyFluidTank> inputs) {
        FluidIngredient firstIngredient = getFluidIngredients().get(0);
        FluidIngredient secondIngredient = getFluidIngredients().get(1);
        FluidStack firstFluid = inputs.getFirst().getFluid();
        FluidStack secondFluid = inputs.getSecond().getFluid();

        return (validFluid(firstIngredient, firstFluid) && validFluid(secondIngredient, secondFluid)) ||
                (validFluid(firstIngredient, secondFluid) && validFluid(secondIngredient, firstFluid));
    }

    private boolean validFluid(FluidIngredient ingredient, FluidStack stack) {
        return ingredient.test(stack) && stack.getAmount() >= ingredient.getRequiredAmount();
    }

    @Override
    public void readAdditional(JsonObject json) {
        super.readAdditional(json);
        suOutput = GsonHelper.getAsInt(json, "suOutput",256);
        burnTicks = GsonHelper.getAsInt(json, "burnTicks",20);
        rpm = GsonHelper.getAsInt(json, "rpm",32);
    }

    @Override
    public void readAdditional(FriendlyByteBuf buffer) {
        super.readAdditional(buffer);
        suOutput = buffer.readInt();
        burnTicks = buffer.readInt();
        rpm = buffer.readInt();
    }

    @Override
    public void writeAdditional(JsonObject json) {
        super.writeAdditional(json);
        json.addProperty("suOutput", suOutput);
        json.addProperty("burnTicks", burnTicks);
        json.addProperty("rpm", rpm);
    }

    @Override
    public void writeAdditional(FriendlyByteBuf buffer) {
        super.writeAdditional(buffer);
        buffer.writeInt(suOutput);
        buffer.writeInt(burnTicks);
        buffer.writeInt(rpm);
    }


}
