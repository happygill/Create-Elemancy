package org.madscientists.createelemancy.content.ponder.scenes;

import com.simibubi.create.foundation.ponder.ElementLink;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.Selection;
import com.simibubi.create.foundation.ponder.element.EntityElement;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.ponder.instruction.EmitParticlesInstruction;
import com.simibubi.create.foundation.utility.Pointing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.madscientists.createelemancy.content.registry.ElemancyItems;

public class MixedSpiceScenes {

	public static void grindingWheels(SceneBuilder scene, SceneBuildingUtil util) {
		scene.title("grinding_wheels", "Spice Grinding");
		scene.configureBasePlate(0, 0, 5);
		scene.scaleSceneView(.9f);

		Selection wheels = util.select.fromTo(2, 2, 1, 2, 2, 3);
		Selection kinetics = util.select.fromTo(0, 1, 5, 4, 1, 3);
		Selection kinetics2 = util.select.fromTo(0, 2, 5, 4, 2, 3);
		Selection beltCog = util.select.position(5, 0, 1);
		scene.world.setKineticSpeed(wheels, 0);
		scene.world.setBlock(util.grid.at(2, 3, 2), Blocks.AIR.defaultBlockState(), false);

		scene.world.showSection(util.select.layer(0)
				.substract(beltCog), Direction.UP);
		scene.idle(5);

		Selection belt = util.select.fromTo(4, 1, 2, 4, 4, 2)
				.add(util.select.fromTo(4, 3, 3, 4, 4, 3))
				.add(util.select.position(3, 3, 2))
				.add(util.select.position(2, 3, 2));
		Selection bottomBelt = util.select.fromTo(5, 1, 0, 2, 1, 0)
				.add(util.select.fromTo(2, 1, 2, 2, 1, 1));

		BlockPos center = util.grid.at(2, 2, 2);
		Selection wWheel = util.select.position(center.west());
		Selection eWheel = util.select.position(center.east());

		scene.world.showSection(wWheel, Direction.SOUTH);
		scene.idle(3);
		scene.world.showSection(eWheel, Direction.SOUTH);
		scene.idle(10);

		Vec3 centerTop = util.vector.topOf(center);
		scene.overlay.showText(60)
				.attachKeyFrame()
				.text("A pair of Grinding Wheels can grind items into mixed spice")
				.pointAt(centerTop)
				.placeNearTarget();
		scene.idle(70);

		scene.world.showSection(kinetics, Direction.DOWN);
		scene.idle(3);
		scene.world.showSection(kinetics2, Direction.DOWN);
		scene.world.setKineticSpeed(wWheel, -16);
		scene.world.setKineticSpeed(eWheel, 16);
		scene.idle(5);
		scene.effects.rotationDirectionIndicator(center.west());
		scene.effects.rotationDirectionIndicator(center.east());
		scene.idle(10);

		scene.overlay.showText(60)
				.attachKeyFrame()
				.text("Their Rotational Input has to make them spin in opposite directions")
				.pointAt(util.vector.blockSurface(center.west(), Direction.NORTH))
				.placeNearTarget();
		scene.idle(40);
		scene.effects.rotationDirectionIndicator(center.west());
		scene.effects.rotationDirectionIndicator(center.east());
		scene.idle(30);

		ItemStack input = new ItemStack(Items.GOLD_INGOT);
		ItemStack output = new ItemStack(ElemancyItems.MIXED_SPICE.get());
		Vec3 entitySpawn = util.vector.topOf(center.above(2));

		ElementLink<EntityElement> entity1 =
				scene.world.createItemEntity(entitySpawn, util.vector.of(0, 0.2, 0), input);
		scene.idle(18);
		scene.world.modifyEntity(entity1, Entity::discard);
		EmitParticlesInstruction.Emitter blockSpace =
				EmitParticlesInstruction.Emitter.withinBlockSpace(new ItemParticleOption(ParticleTypes.ITEM, input), util.vector.of(0, 0, 0));
		scene.effects.emitParticles(util.vector.centerOf(center)
				.add(0, -0.2, 0), blockSpace, 3, 40);
		scene.idle(10);
		scene.overlay.showControls(new InputWindowElement(centerTop, Pointing.DOWN).withItem(input), 30);
		scene.idle(7);

		scene.overlay.showText(50)
				.attachKeyFrame()
				.text("Items thrown or inserted into the top will be grinded")
				.pointAt(centerTop)
				.placeNearTarget();
		scene.idle(60);

		scene.world.createItemEntity(centerTop.add(0, -1.4, 0), util.vector.of(0, 0, 0), output);
		scene.idle(10);
		scene.world.createItemEntity(centerTop.add(0, -1.4, 0), util.vector.of(0, 0, 0), output);
		scene.overlay.showControls(new InputWindowElement(centerTop.add(0, -2, 0), Pointing.UP).withItem(output), 30);
		scene.idle(40);

		scene.world.restoreBlocks(util.select.position(2, 3, 2));
		scene.world.showSection(belt, Direction.DOWN);
		scene.idle(5);
		scene.world.showSection(beltCog, Direction.UP);
		scene.idle(5);
		scene.world.modifyEntities(ItemEntity.class, Entity::discard);
		scene.world.showSection(bottomBelt, Direction.SOUTH);
		scene.idle(5);

		scene.overlay.showText(50)
				.attachKeyFrame()
				.text("Items can be inserted and picked up through automated means as well")
				.pointAt(centerTop.add(0, .5, 0))
				.placeNearTarget();
		scene.idle(40);
		for (int i = 0; i < 5; i++) {
			if (i < 4)
				scene.world.createItemOnBelt(util.grid.at(4, 4, 2), Direction.EAST, input);
			scene.idle(15);
			if (i < 3)
				scene.world.createItemOnBelt(util.grid.at(4, 4, 2), Direction.EAST, input);
			scene.idle(15);
			if (i > 0) {
				scene.world.createItemOnBelt(center.below(), Direction.UP, output);
				scene.idle(15);
				scene.world.createItemOnBelt(center.below(), Direction.UP, output);
			}
			scene.world.removeItemsFromBelt(util.grid.at(3, 3, 2));
			if (i < 4)
				scene.effects.emitParticles(util.vector.centerOf(center)
						.add(0, -0.2, 0), blockSpace, 3, 28);
			if (i == 0)
				scene.markAsFinished();
		}
	}


}
