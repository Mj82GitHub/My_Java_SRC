/*
 * Copyright (c) 02.2017
 */

package com.mikhail.mj82.Tree;

import java.io.Serializable;

/**
 * Класс хранит в себе все возможные деревья поиска.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class RTrees implements Serializable {

	private static final long serialVersionUID = -913258810754491398L;
	
	// Поисковые деревья для всех типов объектов на карте
	private RTree highway_tree = new RTree(); // Дорога
	private RTree barrier_tree = new RTree(); // Барьеры
	private RTree building_tree = new RTree(); // Здания
	private RTree building_part_tree = new RTree(); // Части здания
	private RTree waterway_tree = new RTree(); // Гидрография проточных вод
	private RTree railway_tree = new RTree(); // Рельсовые пути
	private RTree aeroway_tree = new RTree(); // Воздушный транспорт
	private RTree aerialway_tree = new RTree(); // Канатная дорога
	private RTree emergency_tree = new RTree(); // Экстренные службы
	private RTree natural_tree = new RTree(); // Природные образования
	private RTree power_tree = new RTree(); // Энергетика
	private RTree landuse_tree = new RTree(); // Землепользование, назачение территории
	private RTree man_made_tree = new RTree(); // Искусственные сооружения
	private RTree leisure_tree = new RTree(); // Места проведения досуга
	private RTree amenity_tree = new RTree(); // Инфраструктура, благоустройство
	private RTree office_tree = new RTree(); // Офисы
	private RTree shop_tree = new RTree(); // Магазины, услуги
	private RTree craft_tree = new RTree(); // Мастерские
	private RTree sport_tree = new RTree(); // Спорт
	private RTree tourism_tree = new RTree(); // Туризм
	private RTree historic_tree = new RTree(); // Исторические места
	private RTree military_tree = new RTree(); // Военные объекты
	private RTree place_tree = new RTree(); // Населенный пункт
	private RTree boundary_tree = new RTree(); // Границы
	
	/**
	 * Возвращает дерево поиска для типа дороги.
	 * 
	 * @return дерево поиска для типа дороги
	 */
	
	public RTree getHighwayTree() {
		return highway_tree;
	}
	
	/**
	 * Возвращает дерево поиска для типа препятствия.
	 * 
	 * @return дерево поиска для типа препятствия
	 */
	
	public RTree getBarrierTree() {
		return barrier_tree;
	}
	
	/**
	 * Возвращает дерево поиска для типа здания.
	 * 
	 * @return дерево поиска для типа здания
	 */
	
	public RTree getBuildingTree() {
		return building_tree;
	}
	
	/**
	 * Возвращает дерево поиска для типа часть здания.
	 * 
	 * @return дерево поиска для типа часть здания
	 */
	
	public RTree getBuildingPartTree() {
		return building_part_tree;
	}
	
	/**
	 * Возвращает дерево поиска для типа гидрография проточных вод.
	 * 
	 * @return дерево поиска для типа гидрография проточных вод
	 */
	
	public RTree getWaterwayTree() {
		return waterway_tree;
	}
	
	/**
	 * Возвращает дерево поиска для типа рельсовые пути.
	 * 
	 * @return дерево поиска для типа рельсовые пути
	 */
	
	public RTree getRailwayTree() {
		return railway_tree;
	}
	
	/**
	 * Возвращает дерево поиска для типа воздушный транспорт.
	 * 
	 * @return дерево поиска для типа воздушный транспорт
	 */
	
	public RTree getAerowayTree() {
		return aeroway_tree;
	}
	
	/**
	 * Возвращает дерево поиска для типа канатная дорога.
	 * 
	 * @return дерево поиска для типа канатная дорога
	 */
	
	public RTree getAerialwayTree() {
		return aerialway_tree;
	}
	
	/**
	 * Возвращает дерево поиска для типа экстренные службы.
	 * 
	 * @return дерево поиска для типа экстренные службы
	 */
	
	public RTree getEmergencyTree() {
		return emergency_tree;
	}
	
	/**
	 * Возвращает дерево поиска для типа природные объекты.
	 * 
	 * @return дерево поиска для типа природные объекты
	 */
	
	public RTree getNaturalTree() {
		return natural_tree;
	}
	
	/**
	 * Возвращает дерево поиска для типа энергетика.
	 * 
	 * @return дерево поиска для типа энергетика
	 */
	
	public RTree getPowerTree() {
		return power_tree;
	}
	
	/**
	 * Возвращает дерево поиска для типа землепользование.
	 * 
	 * @return дерево поиска для типа землепользование
	 */
	
	public RTree getLanduseTree() {
		return landuse_tree;
	}
	
	/**
	 * Возвращает дерево поиска для типа искусственные сооружения.
	 * 
	 * @return дерево поиска для типа искусственные сооружения
	 */
	
	public RTree getManMadeTree() {
		return man_made_tree;
	}
	
	/**
	 * Возвращает дерево поиска для типа места проведения досуга.
	 * 
	 * @return дерево поиска для типа места проведения досуга
	 */
	
	public RTree getLeisureTree() {
		return leisure_tree;
	}
	
	/**
	 * Возвращает дерево поиска для типа инфраструктура.
	 * 
	 * @return дерево поиска для типа инфраструктура
	 */
	
	public RTree getAmenityTree() {
		return amenity_tree;
	}
	
	/**
	 * Возвращает дерево поиска для типа офисы.
	 * 
	 * @return дерево поиска для типа офисы
	 */
	
	public RTree getOfficeTree() {
		return office_tree;
	}
	
	/**
	 * Возвращает дерево поиска для типа магазины.
	 * 
	 * @return дерево поиска для типа магазины
	 */
	
	public RTree getShopTree() {
		return shop_tree;
	}
	
	/**
	 * Возвращает дерево поиска для типа мастерские.
	 * 
	 * @return дерево поиска для типа мастерские
	 */
	
	public RTree getCraftTree() {
		return craft_tree;
	}
	
	/**
	 * Возвращает дерево поиска для типа спорт.
	 * 
	 * @return дерево поиска для типа спорт
	 */
	
	public RTree getSportTree() {
		return sport_tree;
	}
	
	/**
	 * Возвращает дерево поиска для типа туризм.
	 * 
	 * @return дерево поиска для типа туризм
	 */
	
	public RTree getTourismTree() {
		return tourism_tree;
	}
	
	/**
	 * Возвращает дерево поиска для типа исторические места.
	 * 
	 * @return дерево поиска для типа типа исторические места
	 */
	
	public RTree getHistoricTree() {
		return historic_tree;
	}
	
	/**
	 * Возвращает дерево поиска для типа военные объекты.
	 * 
	 * @return дерево поиска для типа военные объекты
	 */
	
	public RTree getMilitaryTree() {
		return military_tree;
	}
	
	/**
	 * Возвращает дерево поиска для типа населенный пункт.
	 * 
	 * @return дерево поиска для типа населенный пункт
	 */
	
	public RTree getPlaceTree() {
		return place_tree;
	}
	
	/**
	 * Возвращает дерево поиска для типа граница.
	 * 
	 * @return дерево поиска для типа граница
	 */
	
	public RTree getBoundaryTree() {
		return boundary_tree;
	}
}
