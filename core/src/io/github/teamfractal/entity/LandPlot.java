package io.github.teamfractal.entity;

public class LandPlot {
	int[] productionModifiers = {0, 0, 0};
	int[] productionAmounts = {3, 0, 0};
	
	public void installRoboticon(ResourceType type) {
		switch(type) {
		case ORE:    productionModifiers[0] += 1;
		             break;
		case ENERGY: productionModifiers[1] += 1;
		             break;
		}
	}

	public int[] produceResources() {
		int[] produced = new int[3];
		for(int i = 0; i < 3; i++) {
			produced[i] = productionAmounts[i] * productionModifiers[i];
		}
		return produced;
	}
}