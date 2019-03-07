/*
 * Copyright (c) 02.2017
 */

package com.mikhail.mj82.Tree;

import java.io.Serializable;

/**
 * ����� ������ � ���� ��� ��������� ������� ������.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class RTrees implements Serializable {

	private static final long serialVersionUID = -913258810754491398L;
	
	// ��������� ������� ��� ���� ����� �������� �� �����
	private RTree highway_tree = new RTree(); // ������
	private RTree barrier_tree = new RTree(); // �������
	private RTree building_tree = new RTree(); // ������
	private RTree building_part_tree = new RTree(); // ����� ������
	private RTree waterway_tree = new RTree(); // ����������� ��������� ���
	private RTree railway_tree = new RTree(); // ��������� ����
	private RTree aeroway_tree = new RTree(); // ��������� ���������
	private RTree aerialway_tree = new RTree(); // �������� ������
	private RTree emergency_tree = new RTree(); // ���������� ������
	private RTree natural_tree = new RTree(); // ��������� �����������
	private RTree power_tree = new RTree(); // ����������
	private RTree landuse_tree = new RTree(); // ����������������, ��������� ����������
	private RTree man_made_tree = new RTree(); // ������������� ����������
	private RTree leisure_tree = new RTree(); // ����� ���������� ������
	private RTree amenity_tree = new RTree(); // ��������������, ���������������
	private RTree office_tree = new RTree(); // �����
	private RTree shop_tree = new RTree(); // ��������, ������
	private RTree craft_tree = new RTree(); // ����������
	private RTree sport_tree = new RTree(); // �����
	private RTree tourism_tree = new RTree(); // ������
	private RTree historic_tree = new RTree(); // ������������ �����
	private RTree military_tree = new RTree(); // ������� �������
	private RTree place_tree = new RTree(); // ���������� �����
	private RTree boundary_tree = new RTree(); // �������
	
	/**
	 * ���������� ������ ������ ��� ���� ������.
	 * 
	 * @return ������ ������ ��� ���� ������
	 */
	
	public RTree getHighwayTree() {
		return highway_tree;
	}
	
	/**
	 * ���������� ������ ������ ��� ���� �����������.
	 * 
	 * @return ������ ������ ��� ���� �����������
	 */
	
	public RTree getBarrierTree() {
		return barrier_tree;
	}
	
	/**
	 * ���������� ������ ������ ��� ���� ������.
	 * 
	 * @return ������ ������ ��� ���� ������
	 */
	
	public RTree getBuildingTree() {
		return building_tree;
	}
	
	/**
	 * ���������� ������ ������ ��� ���� ����� ������.
	 * 
	 * @return ������ ������ ��� ���� ����� ������
	 */
	
	public RTree getBuildingPartTree() {
		return building_part_tree;
	}
	
	/**
	 * ���������� ������ ������ ��� ���� ����������� ��������� ���.
	 * 
	 * @return ������ ������ ��� ���� ����������� ��������� ���
	 */
	
	public RTree getWaterwayTree() {
		return waterway_tree;
	}
	
	/**
	 * ���������� ������ ������ ��� ���� ��������� ����.
	 * 
	 * @return ������ ������ ��� ���� ��������� ����
	 */
	
	public RTree getRailwayTree() {
		return railway_tree;
	}
	
	/**
	 * ���������� ������ ������ ��� ���� ��������� ���������.
	 * 
	 * @return ������ ������ ��� ���� ��������� ���������
	 */
	
	public RTree getAerowayTree() {
		return aeroway_tree;
	}
	
	/**
	 * ���������� ������ ������ ��� ���� �������� ������.
	 * 
	 * @return ������ ������ ��� ���� �������� ������
	 */
	
	public RTree getAerialwayTree() {
		return aerialway_tree;
	}
	
	/**
	 * ���������� ������ ������ ��� ���� ���������� ������.
	 * 
	 * @return ������ ������ ��� ���� ���������� ������
	 */
	
	public RTree getEmergencyTree() {
		return emergency_tree;
	}
	
	/**
	 * ���������� ������ ������ ��� ���� ��������� �������.
	 * 
	 * @return ������ ������ ��� ���� ��������� �������
	 */
	
	public RTree getNaturalTree() {
		return natural_tree;
	}
	
	/**
	 * ���������� ������ ������ ��� ���� ����������.
	 * 
	 * @return ������ ������ ��� ���� ����������
	 */
	
	public RTree getPowerTree() {
		return power_tree;
	}
	
	/**
	 * ���������� ������ ������ ��� ���� ����������������.
	 * 
	 * @return ������ ������ ��� ���� ����������������
	 */
	
	public RTree getLanduseTree() {
		return landuse_tree;
	}
	
	/**
	 * ���������� ������ ������ ��� ���� ������������� ����������.
	 * 
	 * @return ������ ������ ��� ���� ������������� ����������
	 */
	
	public RTree getManMadeTree() {
		return man_made_tree;
	}
	
	/**
	 * ���������� ������ ������ ��� ���� ����� ���������� ������.
	 * 
	 * @return ������ ������ ��� ���� ����� ���������� ������
	 */
	
	public RTree getLeisureTree() {
		return leisure_tree;
	}
	
	/**
	 * ���������� ������ ������ ��� ���� ��������������.
	 * 
	 * @return ������ ������ ��� ���� ��������������
	 */
	
	public RTree getAmenityTree() {
		return amenity_tree;
	}
	
	/**
	 * ���������� ������ ������ ��� ���� �����.
	 * 
	 * @return ������ ������ ��� ���� �����
	 */
	
	public RTree getOfficeTree() {
		return office_tree;
	}
	
	/**
	 * ���������� ������ ������ ��� ���� ��������.
	 * 
	 * @return ������ ������ ��� ���� ��������
	 */
	
	public RTree getShopTree() {
		return shop_tree;
	}
	
	/**
	 * ���������� ������ ������ ��� ���� ����������.
	 * 
	 * @return ������ ������ ��� ���� ����������
	 */
	
	public RTree getCraftTree() {
		return craft_tree;
	}
	
	/**
	 * ���������� ������ ������ ��� ���� �����.
	 * 
	 * @return ������ ������ ��� ���� �����
	 */
	
	public RTree getSportTree() {
		return sport_tree;
	}
	
	/**
	 * ���������� ������ ������ ��� ���� ������.
	 * 
	 * @return ������ ������ ��� ���� ������
	 */
	
	public RTree getTourismTree() {
		return tourism_tree;
	}
	
	/**
	 * ���������� ������ ������ ��� ���� ������������ �����.
	 * 
	 * @return ������ ������ ��� ���� ���� ������������ �����
	 */
	
	public RTree getHistoricTree() {
		return historic_tree;
	}
	
	/**
	 * ���������� ������ ������ ��� ���� ������� �������.
	 * 
	 * @return ������ ������ ��� ���� ������� �������
	 */
	
	public RTree getMilitaryTree() {
		return military_tree;
	}
	
	/**
	 * ���������� ������ ������ ��� ���� ���������� �����.
	 * 
	 * @return ������ ������ ��� ���� ���������� �����
	 */
	
	public RTree getPlaceTree() {
		return place_tree;
	}
	
	/**
	 * ���������� ������ ������ ��� ���� �������.
	 * 
	 * @return ������ ������ ��� ���� �������
	 */
	
	public RTree getBoundaryTree() {
		return boundary_tree;
	}
}
