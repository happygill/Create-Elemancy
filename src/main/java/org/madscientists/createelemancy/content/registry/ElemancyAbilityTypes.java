package org.madscientists.createelemancy.content.registry;

import net.minecraft.resources.ResourceLocation;
import org.madscientists.createelemancy.Elemancy;
import org.madscientists.createelemancy.content.ability.*;
import org.madscientists.createelemancy.content.ability.api.AbilityType;
import org.madscientists.createelemancy.content.ability.api.AbilityUtils;
import org.madscientists.createelemancy.content.ability.api.Ability;
import org.madscientists.createelemancy.content.ability.enchant.TempEnchantAbility;
import org.madscientists.createelemancy.content.procs.composites.*;
import org.madscientists.createelemancy.content.procs.procs.*;

import java.util.function.Supplier;
import static org.madscientists.createelemancy.Elemancy.LOGGER;
import static org.madscientists.createelemancy.Elemancy.toHumanReadable;

public class ElemancyAbilityTypes {
    public static final AbilityType NULL_STAR = register(Elemancy.asResource("null_star"), NullStarAbility::new);
    public static final AbilityType HEIGHTENED_PHYSIQUE = register(Elemancy.asResource("heightened_physique"), HeightenedPhysiqueAbility::new);
    public static final AbilityType TEMP_ENCHANT = register(Elemancy.asResource("temp_enchant"), TempEnchantAbility::new);
    public static final AbilityType ENERGIZED = register(Elemancy.asResource("energized"), EnergizedAbility::new);
    public static final AbilityType SCORCHED_EARTH = register(Elemancy.asResource("scorched_earth"), ScorchedEarthAbility::new);
    public static final AbilityType LIGHTWEIGHT_AURA = register(Elemancy.asResource("lightweight_aura"), LightWeightAuraAbility::new);
    public static final AbilityType AVIAN_WINGS = register(Elemancy.asResource("avian_wings"), AvianWingsAbility::new);
    public static final AbilityType DOUBLE_JUMP = register(Elemancy.asResource("double_jump"), DoubleJumpAbility::new);
    public static final AbilityType VAST_RELOCATION = register(Elemancy.asResource("vast_relocation"), VastRelocationAbility::new);
    public static final AbilityType HEAT_PROC = register(Elemancy.asResource("heat_proc"), HeatProc::new);
    public static final AbilityType FRIGID_PROC = register(Elemancy.asResource("frigid_proc"), FrigidProc::new );
    public static final AbilityType REACTIVITY_PROC = register(Elemancy.asResource("reactivity_proc"), ReactivityProc::new);
    public static final AbilityType STABILITY_PROC = register(Elemancy.asResource("stability_proc"), StabilityProc::new);
    public static final AbilityType FEATHERWEIGHT_PROC = register(Elemancy.asResource("featherweight_proc"), FeatherweightProc::new);
    public static final AbilityType DENSITY_PROC = register(Elemancy.asResource("density_proc"),DensityProc::new);
    public static final AbilityType POWER_PROC = register(Elemancy.asResource("power_proc"), PowerProc::new);
    public static final AbilityType NULL_PROC = register(Elemancy.asResource("null_proc"),NullProc::new);
    public static final AbilityType UNTRUE_NULL_COMPOSITE = register(Elemancy.asResource("untrue_null_composite"), UntrueNullComposite::new);
    public static final AbilityType COMBUSTION_COMPOSITE = register(Elemancy.asResource("combustion_composite"), CombustionComposite::new);
    public static final AbilityType SCORCHED_EARTH_COMPOSITE = register(Elemancy.asResource("scorched_earth_composite"), ScorchedEarthComposite::new);
    public static final AbilityType CLEANSING_FIRE_COMPOSITE = register(Elemancy.asResource("cleansing_fire_composite"), CleansingFireComposite::new);
    public static final AbilityType HEALING_POWER_COMPOSITE = register(Elemancy.asResource("healing_power_composite"), HealingPowerComposite::new);

    public static void init() {
        LOGGER.info("Registering Elemancy abilities");
    }

    public static AbilityType register(ResourceLocation id, Supplier<Ability> factory) {
        Elemancy.registrate().addRawLang("ability." + id.getNamespace() + "." + id.getPath(),toHumanReadable(id.getPath()));
        AbilityType type = new AbilityType(id, factory);
        AbilityUtils.registerAbility(type);
        return type;
    }
}
