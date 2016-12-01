package io.github.teamfractal.entity;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

public class Market {
	Market() {
		setFood(16);
		setEnergy(16);
		setOre(0);
		setRoboticon(12);
	}



	private int food;
	private int energy;
	private int ore;
	private int roboticon;

	int getFood() {
		return food;
	}

	int getEnergy() {
		return energy;
	}

	int getOre() {
		return ore;
	}

	int getRoboticon() {
		return roboticon;
	}

	void setFood(int amount) {
		food = amount;
	}

	void setEnergy(int amount) {
		energy = amount;
	}

	void setOre(int amount) {
		ore = amount;
	}

	void setRoboticon(int amount) {
		roboticon = amount;
	}



	public int getResource(ResourceType type) {
		switch (type) {
			case ORE:
				return getOre();

			case ENERGY:
				return getEnergy();

			case ROBOTICON:
				return getRoboticon();

			default:
				throw new IllegalArgumentException("Unknown Resource type used.");
		}
	}

	/**
	 * Method to ensure the market have enough resources for user to purchase.
	 * @param type    the resource type.
	 * @param amount  the amount of resource to check.
	 */
	void checkResourcesMoreThanAmount (ResourceType type, int amount) {
		int resource = getResource(type);

		if (amount > resource){
			throw new ValueException("Error: not enough resources in the market.");
		}
	}

	/**
	 * Method to return the buy price
	 * @param type the resource
	 * @return the amount for the buy price
	 */
	int getBuyPrice(ResourceType type) {
		int price;
		switch (type) {
			case ORE:
				price = 20;
				return price;

			case ENERGY:
				price = 30;
				return price;

			case FOOD:
				price = 40;
				return price;

			case ROBOTICON:
				price = 100;
				return price;

			default:
				throw new IllegalArgumentException("Error: Resource type is incorrect.");
		}
	}

	int getSellPrice(ResourceType type) {
		int price = 1;
		int food2 = getFood();
		int ore2 = getOre();
		int energy2 = getEnergy();
		int roboticon2 = getRoboticon();

		int total = food2+ore2+energy2+roboticon2;
		int minprice = 10;
		int maxprice = 50;

		switch (type) {
			case ORE:
				int oreNew = price * (1-ore2/total);
				if (oreNew >= 50){
					oreNew = 50;
				}
				else if (oreNew <= 10){
					oreNew = 10;
				}
				return oreNew;

			case ENERGY:
				int energyNew = price * (1-energy2/total);
				if (energyNew >= 50){
					energyNew = 50;
				}
				else if (energyNew <= 10){
					energyNew = 10;
				}
				return energyNew;

			case FOOD:
				int foodNew = price * (1-food2/total);
				if (foodNew >= 50){
					foodNew = 50;
				}
				else if (foodNew <= 10){
					foodNew = 10;
				}
				return foodNew;


			case ROBOTICON:
				int roboticonNew = price * (1-roboticon2/total);
				if (roboticonNew >= 50){
					roboticonNew = 50;
				}
				else if (roboticonNew <= 10){
					roboticonNew = 10;
				}
				return roboticonNew;

			default:
				throw new IllegalArgumentException("Error: Resource type is incorrect.");

		}
	}

	public void sellReduceResourses(int amount, ResourceType type){

		switch (type) {
			case ORE:
				ore += amount;
				break;
			case ENERGY:
				energy += amount;
				break;
			case FOOD:
				food += amount;
				break;
			case ROBOTICON:
				roboticon += roboticon;
				break;

		}
	}

}








