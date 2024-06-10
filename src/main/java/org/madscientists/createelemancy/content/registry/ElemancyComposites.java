package org.madscientists.createelemancy.content.registry;

import org.madscientists.createelemancy.content.ability.api.AbilityType;

import java.util.ArrayList;
import java.util.List;

public class ElemancyComposites {
    public static final List<ProcInteraction> INTERACTIONS = new ArrayList<>();

    public static ProcInteraction UNTRUE_NULL_HEAT= addProcInteraction(ElemancyElement.HEAT, ElemancyElement.FRIGID, ElemancyAbilityTypes.UNTRUE_NULL_COMPOSITE);
    public static ProcInteraction UNTRUE_NULL_STABILITY= addProcInteraction(ElemancyElement.STABILITY, ElemancyElement.REACTIVITY, ElemancyAbilityTypes.UNTRUE_NULL_COMPOSITE);
    public static ProcInteraction UNTRUE_NULL_DENSITY= addProcInteraction(ElemancyElement.DENSITY, ElemancyElement.FEATHERWEIGHT, ElemancyAbilityTypes.UNTRUE_NULL_COMPOSITE);

    public static ProcInteraction HEALING_POWER_HEAT= addProcInteraction(ElemancyElement.HEAT, ElemancyElement.POWER, ElemancyAbilityTypes.HEALING_POWER_COMPOSITE);
    public static ProcInteraction HEALING_POWER_FRIGID= addProcInteraction(ElemancyElement.FRIGID, ElemancyElement.POWER, ElemancyAbilityTypes.HEALING_POWER_COMPOSITE);
    public static ProcInteraction HEALING_POWER_STABILITY= addProcInteraction(ElemancyElement.STABILITY, ElemancyElement.POWER, ElemancyAbilityTypes.HEALING_POWER_COMPOSITE);
    public static ProcInteraction HEALING_POWER_REACTIVITY= addProcInteraction(ElemancyElement.REACTIVITY, ElemancyElement.POWER, ElemancyAbilityTypes.HEALING_POWER_COMPOSITE);
    public static ProcInteraction HEALING_POWER_DENSITY= addProcInteraction(ElemancyElement.DENSITY, ElemancyElement.POWER, ElemancyAbilityTypes.HEALING_POWER_COMPOSITE);
    public static ProcInteraction HEALING_POWER_FEATHERWEIGHT= addProcInteraction(ElemancyElement.FEATHERWEIGHT, ElemancyElement.POWER, ElemancyAbilityTypes.HEALING_POWER_COMPOSITE);
    public static ProcInteraction HEALING_POWER_NULL= addProcInteraction(ElemancyElement.NULL, ElemancyElement.POWER, ElemancyAbilityTypes.HEALING_POWER_COMPOSITE);


    public static ProcInteraction COMBUSTION= addProcInteraction(ElemancyElement.HEAT, ElemancyElement.REACTIVITY, ElemancyAbilityTypes.COMBUSTION_COMPOSITE);
    public static ProcInteraction CLEANSING_FIRE= addProcInteraction(ElemancyElement.HEAT, ElemancyElement.STABILITY, ElemancyAbilityTypes.CLEANSING_FIRE_COMPOSITE);
    public static ProcInteraction SCORCHED_EARTH= addProcInteraction(ElemancyElement.HEAT, ElemancyElement.DENSITY, ElemancyAbilityTypes.SCORCHED_EARTH_COMPOSITE);

    public static ProcInteraction addProcInteraction(ElemancyElement elemancyElement1, ElemancyElement elemancyElement2, AbilityType composite) {
        ProcInteraction interaction = new ProcInteraction(elemancyElement1, elemancyElement2, composite);
        INTERACTIONS.add(interaction);
        return interaction;
    }
    public record ProcInteraction(ElemancyElement elemancyElement1, ElemancyElement elemancyElement2, AbilityType composite) {}
}
