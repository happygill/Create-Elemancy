package org.madscientists.createelemancy.content.ponder;

import com.simibubi.create.foundation.ponder.PonderTag;
import net.minecraft.resources.ResourceLocation;
import org.madscientists.createelemancy.Elemancy;
import org.madscientists.createelemancy.content.registry.ElemancyItems;

public class ElemancyPonderTag extends PonderTag {

	public static final PonderTag MIXED_SPICE = create("mixed_spice")
			.defaultLang("Mixed Spice Related", "Items and Components related to Mixed Spice")
			.item(ElemancyItems.MIXED_SPICE.get(), true, false).addToIndex();

	public ElemancyPonderTag(ResourceLocation id) {
		super(id);
	}

	private static PonderTag create(String id) {
		return new PonderTag(Elemancy.asResource(id));
	}
}
