package org.madscientists.createelemancy.foundation.network;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.madscientists.createelemancy.content.ability.capability.Abilities;
import org.madscientists.createelemancy.content.attributes.ExtraJumpHandler;

public class InputC2S extends SimplePacketBase {
        int inputAction;
        int inputKey;

        public InputC2S(FriendlyByteBuf buffer) {
            inputAction= buffer.readInt();
            inputKey= buffer.readInt();
        }

        public InputC2S(int inputAction, int inputKey) {
            this.inputAction = inputAction;
            this.inputKey = inputKey;
        }


        @Override
        public void write(FriendlyByteBuf buffer) {
            buffer.writeInt(inputKey);
            buffer.writeInt(inputAction);
        }

        @Override
        public boolean handle(NetworkEvent.Context context) {
            context.enqueueWork(() -> {
                ServerPlayer player = context.getSender();
                if(player == null)
                    return;
                Abilities.handleKeyInput(player, inputKey, inputAction, false);
                ExtraJumpHandler.handleKeyInput(player, inputKey, inputAction);

            });
            return true;
        }

}
