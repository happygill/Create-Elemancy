package org.madscientists.createelemancy.content.registry;

import com.jozufozu.flywheel.core.PartialModel;
import org.madscientists.createelemancy.Elemancy;

public class ElemancyPartials {
	public static void init() {
		Elemancy.LOGGER.info("Registering Block Partials!");
	}

	private static PartialModel block(String path) {
		return new PartialModel(Elemancy.asResource("block/" + path));
	}

	public static final PartialModel

			GRINDING_WHEEL_PARTIAL = block("grinding_wheel/wheel"),
			GRINDING_WHEEL_PISTON = block("grinding_wheel/block"),
			TURBINE = block("vortex_generator/turbine2"),
			VCP_LARGE_COGWHEEL = block("vortex_generator/large_vcp_cogwheel"),
			NULL_SPACE = block("null_space"),
			DISTILLER_COLUMN = block("distiller/column");
}