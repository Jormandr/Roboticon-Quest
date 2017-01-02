package io.github.teamfractal.entity;

import io.github.teamfractal.entity.enums.PurchaseStatus;
import io.github.teamfractal.entity.enums.ResourceType;
import io.github.teamfractal.exception.InvalidResourceTypeException;
import io.github.teamfractal.exception.NotEnoughResourceException;

import java.util.ArrayList;

public class Player {
	private int money = 100;
	private int ore = 0;
	private int energy = 0;
	private int food = 0;
	ArrayList<Roboticon> roboticonList;
	
	int getMoney() { return money; }
	int getOre() { return ore; }
	int getEnergy() { return energy; }
	int getFood() { return food; }

	synchronized void setMoney(int money) {
		if (money < 0) {
			throw new IllegalArgumentException("Error: Money can't be negative.");
		}

		this.money = money;
	}

	synchronized void setOre(int ore) {
		if (ore < 0) {
			throw new IllegalArgumentException("Error: Ore can't be negative.");
		}

		this.ore = ore;
	}
	synchronized void setEnergy(int energy) {
		if (energy < 0) {
			throw new IllegalArgumentException("Error: Energy can't be negative.");
		}

		this.energy = energy;
	}
	synchronized void setFood(int food) {
		if (food < 0) {
			throw new IllegalArgumentException("Error: Food can't be negative.");
		}

		this.food = food;
	}

	void setResource(ResourceType type, int amount) {
		switch (type) {
			case ENERGY:
				setEnergy(amount);
				break;

			case ORE:
				setOre(amount);
				break;

			case FOOD:
				setFood(amount);
				break;

			default:
				throw new InvalidResourceTypeException(type);
		}
	}

	int getResource(ResourceType type) {
		switch (type) {
			case ENERGY:
				return getEnergy();

			case ORE:
				return getOre();

			case FOOD:
				return getFood();


			default:
				throw new InvalidResourceTypeException(type);
		}
	}

	/**
	 * Action for player to purchase resources from the market.
	 *
	 * @param amount     Amount of resources to purchase.
	 * @param market     The market instance.
	 * @param resource   The resource type.
	 * @return           If the purchase was success or not.
	 */
	public PurchaseStatus purchaseResourceFromMarket(int amount, Market market, ResourceType resource) {
		if (!market.hasEnoughResources(resource, amount)) {
			return PurchaseStatus.FailMarketNotEnoughResource;
		}

		int cost = amount * market.getSellPrice(resource);
		int money = getMoney();
		if (cost > money) {
			return PurchaseStatus.FailPlayerNotEnoughMoney;
		}

		market.sellResource(resource, amount);
		setMoney(money - cost);
		setResource(resource, getResource(resource) + amount);
		return PurchaseStatus.Success;
	}

	/**
	 * Action for player to sell resources to the market.
	 *
	 * @param amount    Amount of resources to sell.
	 * @param market    The market instance.
	 * @param resource  The resource type.
	 */
	public void sellResourceToMarket(int amount, Market market, ResourceType resource) {
		int resourcePrice = market.getBuyPrice(resource);

		if (getResource(resource) >= amount) {
			market.buyResource(resource, amount);
			setResource(resource, getResource(resource) - amount);
			setMoney(getMoney() + amount * resourcePrice);
		} else {
			throw new NotEnoughResourceException("Player.sellResourceToMarket", resource, amount, getResource(resource));
		}
	}

	/**
	 * Apply roboticon customisation
	 * @param roboticon  The roboticon to be customised
	 * @param type       The roboticon customisation type.
	 * @return           The roboticon
	 */
	public Roboticon customiseRoboticon(Roboticon roboticon, ResourceType type) {
		roboticon.setCustomisation(type);
		return roboticon;
	}
	
}