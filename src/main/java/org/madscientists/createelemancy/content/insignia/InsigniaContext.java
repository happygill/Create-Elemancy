package org.madscientists.createelemancy.content.insignia;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class InsigniaContext {
    private final InteractionHand hand;
    private final Level level;
    private final Vec3 clickLocation;
    private final ItemStack itemStack;
    @Nullable
    private final Player player;
    @Nullable
    private final CompoundTag insigniaNBT;
    private final boolean init;
    @Nullable
    private final InsigniaBlockEntity insignia;


    public InsigniaContext(UseOnContext pContext, InsigniaBlockEntity insignia,boolean init) {
        this(pContext.getLevel(), pContext.getPlayer(), pContext.getHand(), pContext.getItemInHand(), pContext.getClickLocation(), insignia, getCasterNBTFromPlayer(pContext.getPlayer()),init);
    }

    public InsigniaContext(Level pLevel, Player pPlayer, InteractionHand pUsedHand,boolean init) {
        this(pLevel, pPlayer, pUsedHand, pPlayer.getItemInHand(pUsedHand), pPlayer.pick(2, 0, true).getLocation(), null, getCasterNBTFromPlayer(pPlayer),init);
    }

    public InsigniaContext(Level pLevel, @Nullable Player pPlayer, InteractionHand pHand, ItemStack pItemStack, Vec3 clickLocation, @Nullable InsigniaBlockEntity insignia, @Nullable CompoundTag casterNBTData,boolean init) {
        this.player = pPlayer;
        this.hand = pHand;
        this.itemStack = pItemStack;
        this.level = pLevel;
        this.clickLocation = clickLocation;
        this.insignia = insignia;
        this.insigniaNBT = casterNBTData;

        this.init = init;
    }

    public static CompoundTag getCasterNBTFromPlayer(Player player) {
        CompoundTag tag = new CompoundTag();
        if (player == null) return tag;

        if(player.getPersistentData().contains("InsigniaData"))
            return player.getPersistentData().getCompound("InsigniaData");

        player.getPersistentData().put("InsigniaData", tag);
        return tag;
    }

    public @Nullable CompoundTag getCasterNBTData() {
        return insigniaNBT;
    }

    public boolean hasCasterNBTData() {
        return insigniaNBT != null;
    }

    public @Nullable InsigniaBlockEntity getInsignia() {
        return insignia;
    }

    public boolean hasInsignia() {
        return insignia != null;
    }

    public BlockPos getPreferredCenter() {
        if (hasInsignia())
            return getInsignia().getCenter();
        else if (hasPlayer())
            return getPlayer().getOnPos().above();
        else
            return getClickedPos();
    }


    public BlockPos getClickedPos() {
        return BlockPos.containing(this.clickLocation.x, this.clickLocation.y, this.clickLocation.z);
    }

    public Vec3 getClickLocation() {
        return this.clickLocation;
    }

    public ItemStack getItemInHand() {
        return this.itemStack;
    }

    @Nullable
    public Player getPlayer() {
        return this.player;
    }

    public boolean hasPlayer() {
        return player != null;
    }

    public InteractionHand getHand() {
        return this.hand;
    }

    public Level getLevel() {
        return this.level;
    }

    public Direction getHorizontalDirection() {
        return this.player == null ? Direction.NORTH : this.player.getDirection();
    }
    public float getRotation() {
        return this.player == null ? 0.0F : this.player.getYRot();
    }

    public boolean isInit() {
        return init;
    }

    public enum Result {
        FINISHED,
        CONTINUE
    }
}
