package org.madscientists.createelemancy.foundation.network;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent.Context;

import static net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT;

public class MultiTankFluidSyncS2CPacket extends SimplePacketBase {
	private final FluidStack fluidStack;
	private final BlockPos pos;
	private final int tankId;

	public MultiTankFluidSyncS2CPacket(FluidStack fluidStack, BlockPos pos, int tankId) {
		this.fluidStack = fluidStack;
		this.pos = pos;
		this.tankId = tankId;
	}

	public MultiTankFluidSyncS2CPacket(FriendlyByteBuf buf) {
		this(buf.readFluidStack(), buf.readBlockPos(), buf.readInt());
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeFluidStack(fluidStack);
		buf.writeBlockPos(pos);
		buf.writeInt(tankId);
	}

	@Override
	public boolean handle(Context context) {
		if(context.getDirection() != PLAY_TO_CLIENT)
			return false;
		context.enqueueWork(() -> {
			if(Minecraft.getInstance().level.getBlockEntity(pos) instanceof FluidMultiTankSyncBlockEntity blockEntity) {
				blockEntity.setFluid(this.tankId, this.fluidStack);
			}
		});
		return true;
	}

	public interface FluidMultiTankSyncBlockEntity {
		void setFluid(int tank, FluidStack stack);

		default void syncFluid(int tank, FluidStack stack, BlockPos blockPos) {
			ElemancyMessages.sendToClients(new MultiTankFluidSyncS2CPacket(stack, blockPos, tank));
		}
	}
}