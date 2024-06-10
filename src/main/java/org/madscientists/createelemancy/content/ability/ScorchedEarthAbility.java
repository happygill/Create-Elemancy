package org.madscientists.createelemancy.content.ability;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.madscientists.createelemancy.content.ability.api.AbilityType;
import org.madscientists.createelemancy.content.block.elemental.ScorchedFire;
import org.madscientists.createelemancy.content.ability.api.Ability;
import org.madscientists.createelemancy.content.registry.ElemancyAbilityTypes;

public class ScorchedEarthAbility extends Ability {
    boolean active = false;
    int delayCounter = 0;
    BlockPos pos=BlockPos.ZERO;
    @Override
    public void tick(LivingEntity player) {
        super.tick(player);
        if(active) {
            delayCounter++;
            if (delayCounter == 5) {
                genFirstRing(player,pos);
            }
            if (delayCounter == 20) {
                genSecondRing(player,pos);
            }
            if (delayCounter == 35) {
                genThirdRing(player,pos);
                markUsed();
                active = false;
            }
        }
    }

    @Override
    public boolean canAbilityBeAddedToEntity(LivingEntity entity) {
        return entity instanceof Player;
    }

    @Override
    public void onKeyInput(int inputKey, int action, Player player, boolean clientSide) {
        if(active||clientSide) return;
        if(inputKey== InputConstants.KEY_LSHIFT&&action==InputConstants.PRESS){
            active = true;
            delayCounter = 0;
            pos = player.getOnPos().above();
        }
    }

    @Override
    public void save(CompoundTag tag) {
        super.save(tag);
        tag.putBoolean("active",active);
        tag.putInt("delayCounter",delayCounter);
        tag.put("pos", NbtUtils.writeBlockPos(pos));
    }


    @Override
    public void read(CompoundTag tag) {
        super.read(tag);
        active = tag.getBoolean("active");
        delayCounter = tag.getInt("delayCounter");
        pos = NbtUtils.readBlockPos(tag.getCompound("pos"));
    }

    @Override
    public AbilityType getType() {
        return ElemancyAbilityTypes.SCORCHED_EARTH;
    }

    private static void genFirstRing(LivingEntity player, BlockPos pos) {
        Level level = player.level();
        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                BlockPos newPos = pos.offset(x,0,z);
                ScorchedFire.castScorchedFire(level, newPos, 25, player);
            }
        }
    }

    private static void genSecondRing(LivingEntity player, BlockPos pos) {
        Level level = player.level();
        for (int x = -3; x < 4; x++) {
            for (int z = -3; z < 4; z++) {
                if(Math.abs(x)<2&&Math.abs(z)<2) continue;
                BlockPos newPos = pos.offset(x,0,z);
                ScorchedFire.castScorchedFire(level, newPos, 50, player);
            }
        }
    }

    private static void genThirdRing(LivingEntity player, BlockPos pos) {
        Level level = player.level();
        for (int x = -4; x < 5; x++) {
            for (int z = -4; z < 5; z++) {
                if(Math.abs(x)<4&&Math.abs(z)<4) continue;
                BlockPos newPos = pos.offset(x,0,z);
                ScorchedFire.castScorchedFire(level, newPos, 75, player);
            }
        }
    }


}
