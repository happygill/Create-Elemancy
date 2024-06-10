package org.madscientists.createelemancy.content.block.distiller;


import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;
import org.madscientists.createelemancy.content.registry.ElemancyPartials;

public class DistillerInstance extends BlockEntityInstance<DistillerBlockEntity> {

	private final OrientedData distillerColumn;

	private final DistillerBlockEntity distiller;


	public DistillerInstance(MaterialManager dispatcher, DistillerBlockEntity tile) {
		super(dispatcher, tile);
		this.distiller = tile;

		distillerColumn = getOrientedMaterial().getModel(ElemancyPartials.DISTILLER_COLUMN, blockState).createInstance();
		distillerColumn.setPosition(getInstancePosition());
		distillerColumn.nudge(0, -1f, 0);
	}

	@Override
	public void updateLight() {
		super.updateLight();
		relight(pos, distillerColumn);
	}

	@Override
	public void remove() {
		distillerColumn.delete();
	}
}
