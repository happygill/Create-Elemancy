package org.madscientists.createelemancy.content.procs;

import net.minecraft.world.entity.LivingEntity;
import org.madscientists.createelemancy.content.ability.api.Ability;
import org.madscientists.createelemancy.content.ability.api.AbilityType;
import org.madscientists.createelemancy.content.ability.api.AbilityUtils;
import org.madscientists.createelemancy.content.ability.capability.Abilities;
import org.madscientists.createelemancy.content.procs.composites.Composite;
import org.madscientists.createelemancy.content.procs.procs.Proc;
import org.madscientists.createelemancy.content.registry.ElemancyComposites;
import org.madscientists.createelemancy.content.registry.ElemancyElement;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ProcUtils {
    public static void removeAllProcs(LivingEntity entity) {
        Abilities.getAbilities(entity).removeIf(ability -> ability instanceof Proc || ability instanceof Composite);
    }

    public static void addProc(LivingEntity entity, AbilityType proc) {
        AbilityUtils.addPlayerAbility(entity, proc);
    }


    public static void applyComposites(LivingEntity entity, List<Ability> abilities) {
        List<Proc> procs = filterProcs(abilities);
        applyInteractions(entity, filterInteractions(procs), procs);
    }

    private static List<Proc> filterProcs(List<Ability> abilities) {
        return abilities.stream()
                .filter(Proc.class::isInstance)
                .map(Proc.class::cast)
                .collect(Collectors.toList());
    }

    private static List<ElemancyComposites.ProcInteraction> filterInteractions(List<Proc> procs) {
        return ElemancyComposites.INTERACTIONS.stream()
                .filter(matchingComposites(procs))
                .collect(Collectors.toList());
    }

    private static void applyInteractions(LivingEntity entity, List<ElemancyComposites.ProcInteraction> interactions, List<Proc> procs) {
        interactions.forEach(interaction -> {
            int duration = getMinDurationForElements(interaction, procs);
            AbilityUtils.addPlayerAbilityDuration(entity, interaction.composite(), duration);
        });
    }

    private static int getMinDurationForElements(ElemancyComposites.ProcInteraction interaction, List<Proc> procs) {
        return Math.min(getMinDurationForElement(interaction.elemancyElement1(), procs), getMinDurationForElement(interaction.elemancyElement2(), procs));
    }

    private static int getMinDurationForElement(ElemancyElement elemancyElement, List<Proc> procs) {
        return procs.stream()
                .filter(proc -> proc.getElement() == elemancyElement)
                .mapToInt(Proc::getDurationTicks)
                .min()
                .orElse(0);
    }

    private static Predicate<? super ElemancyComposites.ProcInteraction> matchingComposites(List<Proc> procs) {
        return interaction -> procs.stream().anyMatch(proc -> proc.getElement() == interaction.elemancyElement1())
                && procs.stream().anyMatch(proc -> proc.getElement() == interaction.elemancyElement2());
    }
}
