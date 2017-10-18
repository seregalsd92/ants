package com.ibm.sk.engine;

import static com.ibm.sk.WorldConstans.INITIAL_ANT_COUNT;
import static com.ibm.sk.WorldConstans.POPULATION_WAR_FACTOR;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import com.ibm.sk.WorldConstans;
import com.ibm.sk.ant.facade.AntFactory;
import com.ibm.sk.dto.AbstractAnt;
import com.ibm.sk.dto.AbstractWarrior;
import com.ibm.sk.dto.Food;
import com.ibm.sk.dto.Hill;
import com.ibm.sk.dto.IAnt;
import com.ibm.sk.dto.IWorldObject;
import com.ibm.sk.dto.Vision;
import com.ibm.sk.dto.enums.Direction;
import com.ibm.sk.dto.enums.ObjectType;
import com.ibm.sk.engine.exceptions.MoveException;
import com.ibm.sk.ff.gui.client.GUIFacade;
import com.ibm.sk.ff.gui.common.objects.operations.CreateGameData;
import com.ibm.sk.models.WorldBorder;

public final class ProcessExecutor {

	public static GuiConnector guiConnector;

	private final MovementHandler movementHandler;

	private final World world;

	private final PopulationHandler populationHandler;

	public ProcessExecutor(final GUIFacade FACADE, final AntFactory[] implementations) {
		guiConnector = new GuiConnector(FACADE);
		this.world = new World();
		this.populationHandler = new PopulationHandler(this.world, implementations);
		this.movementHandler = new MovementHandler(this.world, this.populationHandler);
	}

	public void execute(final Hill firstHill, final Hill secondHill, final int turn) {
		System.out.println("Turn: " + turn);
		final Iterator<IAnt> first = firstHill.getAnts().iterator();
		final Iterator<IAnt> second = secondHill == null ? Collections.emptyIterator()
				: secondHill.getAnts().iterator();
		guiConnector.placeGuiObjects(this.world.getAllFoods());

		while (first.hasNext() || second.hasNext()) {
			IAnt ant = null;
			if (first.hasNext()) {
				ant = first.next();
				singleStep(ant);
			}
			if (second.hasNext()) {
				ant = second.next();
				singleStep(ant);
			}
		}
		guiConnector.placeGuiObjects(this.world.getWorldObjectsToMove());
		guiConnector.removeGuiObjects(this.world.getDeadObjects());
		this.world.getDeadObjects().clear();
		guiConnector.showScore(firstHill.getName(), firstHill.getFood(), turn + 1, WorldConstans.TURNS);
		if (secondHill != null) {
			guiConnector.showScore(secondHill.getName(), secondHill.getFood(), turn + 1, WorldConstans.TURNS);
		}
	}

	public void initGame(final Hill team1, final Hill team2) {
		final CreateGameData gameData = new CreateGameData();
		gameData.setWidth(WorldConstans.X_BOUNDRY);
		gameData.setHeight(WorldConstans.Y_BOUNDRY);
		gameData.setTeams(new String[] { team1.getName(), team2 != null ? team2.getName() : "" });
		guiConnector.initGame(gameData);
		guiConnector.placeGuiObjects(this.world.getWorldObjects());
		initAnts(team1);
		guiConnector.placeGuiObject(team1);
		guiConnector.placeGuiObjects(new ArrayList<>(team1.getAnts()));
		if (team2 != null) {
			initAnts(team2);
			guiConnector.placeGuiObject(team2);
			guiConnector.placeGuiObjects(new ArrayList<>(team2.getAnts()));
		}
	}

	private void initAnts(final Hill hill) {
		for (int i = 0; i < Math.ceil(INITIAL_ANT_COUNT * (1.0 - POPULATION_WAR_FACTOR)); i++) {
			hill.getAnts().add(this.populationHandler.breedAnt(hill));
		}
		for (int i = 0; i < Math.floor(INITIAL_ANT_COUNT * POPULATION_WAR_FACTOR); i++) {
			hill.getAnts().add(this.populationHandler.breedWarrior(hill));
		}
	}

	private void singleStep(final IAnt ant) {
		System.out.println("Ant " + ant.getId() + " said:");
		final Vision vision = new Vision(createVisionGrid(ant));
		final Direction direction = ant.move(vision);

		if (Direction.NO_MOVE.equals(direction)) {
			System.out.println("I'm not moving. I like this place!");
		} else {
			try {
				// boolean hadFood = false;

				// if (ant instanceof AbstractAnt) {
				// hadFood = ant.hasFood();
				// }

				this.movementHandler.makeMove(ant, direction);

				// if (ant instanceof AbstractAnt) {
				// if (!hadFood && ant.hasFood()) {
				// guiConnector.removeGuiObject(ant.getFood());
				// }
				// }
			} catch (final MoveException e) {
				System.out.println("I cannot move to " + direction.name() + "! That would hurt me!");
			}
		}
	}

	private Map<Direction, ObjectType> createVisionGrid(final IAnt ant) {
		final Map<Direction, ObjectType> visionGrid = new EnumMap<>(Direction.class);

		for (final Direction visionDirection : Direction.values()) {
			visionGrid.put(visionDirection, checkField(visionDirection, ant));
		}

		return visionGrid;
	}

	private ObjectType checkField(final Direction direction, final IAnt ant) {
		ObjectType result = ObjectType.EMPTY_SQUARE;
		final Point point = new Point(ant.getPosition());
		point.translate(direction.getPositionChange().x, direction.getPositionChange().y);
		final IWorldObject foundObject = this.world.getWorldObject(point);
		if (foundObject instanceof AbstractAnt) {
			final AbstractAnt otherAnt = (AbstractAnt) foundObject;
			if (ant.isEnemy(otherAnt) && otherAnt.hasFood()) {
				result = ObjectType.ENEMY_ANT_WITH_FOOD;
			} else if (ant.isEnemy(otherAnt)) {
				result = ObjectType.ENEMY_ANT;
			} else if (otherAnt.hasFood()) {
				result = ObjectType.ANT_WITH_FOOD;
			} else {
				result = ObjectType.ANT;
			}
		} else if (foundObject instanceof AbstractWarrior) {
			final AbstractWarrior warrior = (AbstractWarrior) foundObject;
			if (ant.isEnemy(warrior)) {
				result = ObjectType.ENEMY_WARRIOR;
			} else {
				result = ObjectType.WARRIOR;
			}
		} else if (foundObject instanceof Hill) {
			final Hill hill = (Hill) foundObject;
			if (hill.equals(ant)) {
				result = ObjectType.HILL;
			} else {
				result = ObjectType.ENEMY_HILL;
			}
		} else if (foundObject instanceof Food) {
			result = ObjectType.FOOD;
		} else if (foundObject instanceof WorldBorder) {
			result = ObjectType.BORDER;
		}
		return result;
	}
}
