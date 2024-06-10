package org.madscientists.createelemancy.content.ponder;

import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import org.madscientists.createelemancy.Elemancy;
import org.madscientists.createelemancy.content.registry.ElemancyBlocks;
import org.madscientists.createelemancy.content.registry.ElemancyItems;

public class ElemancyPonderIndex {

    static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(Elemancy.ID);

    public static void register() {}

    public static void registerTags() {
        PonderRegistry.TAGS.forTag(ElemancyPonderTag.MIXED_SPICE)
                .add(ElemancyItems.MIXED_SPICE)
                .add(ElemancyBlocks.DISTILLER)
                .add(ElemancyBlocks.GRINDING_WHEEL);
    }
}
