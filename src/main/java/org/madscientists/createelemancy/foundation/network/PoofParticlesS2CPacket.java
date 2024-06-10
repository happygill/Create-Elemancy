package org.madscientists.createelemancy.foundation.network;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraftforge.network.NetworkEvent;

import static net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT;

public class PoofParticlesS2CPacket extends SimplePacketBase {
    private final BlockPos pos;

    public PoofParticlesS2CPacket(BlockPos pos) {
        this.pos = pos;
    }

    public PoofParticlesS2CPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        if(context.getDirection() != PLAY_TO_CLIENT)
            return false;
        context.enqueueWork(() -> {
            for (int i = 0; i < 10; i++) {
                final float idivpi = (float) i / Mth.PI;
                Minecraft.getInstance().level.addParticle(ParticleTypes.CLOUD,
                        pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f,
                        Mth.cos(idivpi) / 10, 0.1d, Mth.sin(idivpi) / 10);
            }
        });
        return true;
    }
}
