package org.madscientists.createelemancy.foundation.advancement;

import net.minecraft.advancements.CriteriaTriggers;

import java.util.LinkedList;
import java.util.List;

public class ElemancyTriggers {

    private static final List<CriterionTriggerBase<?>> triggers = new LinkedList<>();

    public static SimpleCreateTrigger addSimple(String id) {
        return add(new SimpleCreateTrigger(id));
    }

    private static <T extends CriterionTriggerBase<?>> T add(T instance) {
        triggers.add(instance);
        return instance;
    }

    public static void register() {
        triggers.forEach(CriteriaTriggers::register);
    }

}
