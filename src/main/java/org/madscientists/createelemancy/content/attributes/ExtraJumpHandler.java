package org.madscientists.createelemancy.content.attributes;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.madscientists.createelemancy.content.registry.ElemancyAttributes;

@Mod.EventBusSubscriber
public class ExtraJumpHandler {
    public static void handleKeyInput(ServerPlayer player, int inputKey, int inputAction) {
        if(inputKey == InputConstants.KEY_SPACE && inputAction == InputConstants.PRESS){
            int jumpCountUsed = 0;
            if(player.getPersistentData().contains("jumpCountUsed"))
                jumpCountUsed = player.getPersistentData().getInt("jumpCountUsed");
             int totalJumpCounts= (int) player.getAttributeValue(ElemancyAttributes.EXTRA_JUMPS.get());
            if(!player.onGround()&&jumpCountUsed < totalJumpCounts){
                player.jumpFromGround();
                player.getPersistentData().putInt("jumpCountUsed",jumpCountUsed+1);
                player.connection.send(new ClientboundSetEntityMotionPacket(player));
            }
        }
    }
    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if(event.side.isClient())
            return;
        Player player = event.player;
        CompoundTag playerData = player.getPersistentData();
        if(player.onGround()&&playerData.contains("jumpCountUsed"))
            playerData.remove("jumpCountUsed");
    }
}
