/*
 * Copyright (c) 05.2016
 */

package com.mikhail.mj82.nvg.Converter;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import OsmConverter.OsmConverter;

/**
 * ����� ������ xml ���� osm �����, ��������� ������ �� ���������� (relation) �������� �� �����, ����������� 
 * �� � ��� ������ � ���������� � �������� ����.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class RelationSAXHandler extends DefaultHandler {

	private RndAccessFile raf; // ���������� � ��������� ������ �/�� �����
	
	private Types objects;// ���� �������� �� �����
	
	private ArrayList<Long> ways_ids; // ������ id ����� � ���������
	private ArrayList<String> roles; // ������ ������� (role) ����� � ���������
	
	private boolean mainTag = false; // ���������� ��� ���������� �� ��� ���������
	private boolean addTag = false; // �������������� ���	
	private boolean noMainTag = false; // ������� ����, ��� �������� ��� ������� ����, �� �� ���������
	private boolean noAddTag = false; // ������� ����, ��� �������������� ��� ������� ����, �� �� ���������
	private short typeOfObject = Param.noType; // ��� ���������
	private short addType = 0; // �������������� ���
	
	private ArrayList<Long> outerWaysId; // �������������� ����� � ����� outer
	// �������������� ����� � ����� outer, ��� ��������� �������� (������ ����� - ��� ��������� �������)
	private ArrayList<ArrayList<Long>> outerWaysArray; // ������ ������� ��������
	private ArrayList<Long> innerWaysId; // �������������� ����� � ����� inner
	// �������������� ����� � ����� inner, ��� ��������� �������� (������ ����� - ��� ��������� �������)
	private ArrayList<ArrayList<Long>> innerWaysArray; // ������ ���������� ��������
	
	// ����� ��� ��������� ���������� �����������
	private short outer = 0; // �������
	private short inner = 1; // ����������
	
	private boolean area; // ������� ���������� �������
	private boolean bridge; // ������� ����, ��� ������ �������� ������
	private boolean tunnel; // ������� ����, ��� ������ �������� �������
	
	private String name; // �������� ������� �� �����
	
	private int mainTagPosition = -1; // ������� � ������� tmp_str_k ��������� ���� ������� �� �����
	
	// ���� ���������
	private boolean multipolygon = false; // ������� ����, ��� ����� �������� � ��������� �������� �������
	
	private long relation_id = -1; // ������������� ���������
	private boolean incomplete = false; // �� ��������� ����� � �������������� ����������, ������� �������
	private boolean isModify = false; // �������, ��� ��������� � OSM ����� ���������������� � �� ����� ��������� ������������
		
	// ����
	private boolean isRelation = false; // ���������
	private boolean isMember = false; // ������ �� �����
	
	// �������� �����
	private boolean isWay = false; // ��� ������� � ��������� (�����)	
	private boolean isNoWay = false; // ��� ������� � ��������� (�� �����, � ���-�� ������, ��������, ���������)
	private boolean isHighway = false; // ������
	private boolean isAreaHighway = false; // ������ ��� ��������� ������
	private boolean isSurface = false; // �������� ������
	private boolean isBuilding = false; // ������
	private boolean isBuildingPart = false; // ����� ������
	private boolean isHistoric = false; // ������������ �����
	private boolean isWaterway = false; // ��������� ����
	private boolean isAeroway = false; // ��������� ���������
	private boolean isAerialway = false; // �������� ������
	private boolean isLeisure = false; // ����� ���������� ������
	private boolean isAmenity = false; // ��������������, ���������������
	private boolean isHealthcare = false; // ��������������, ���������������
	private boolean isEmergency = false; // ���������� ������
	private boolean isBoundary = false; // �������
	private boolean isFord = false; // ����
	private boolean isName = false; // ��������
	private boolean isOffice = false; // ����
	private boolean isShop = false; // �������
	private boolean isCraft = false; // ����������
	private boolean isTag = false; // ������� ���� <tag>
	private boolean isCycleway = false; // ������������ �������
	private boolean isIceRoad = false; // ������ ������
	private boolean isWinterRoad = false; // ������ ������
	private boolean isOther = false; // ��� ���������, ���� ��� � ���� ��������� ���������
	
	// ��� ���������� �������� ������
	private ArrayList<String> str_k; // ����� ���� tag
	private ArrayList<String> str_v; // �������� ���� tag
	private int k_position = -1; // ������� ����������� � ��������� ��������
	
	public RelationSAXHandler() {
		raf = new RndAccessFile();
		
		str_k = new ArrayList<>();
		str_v = new ArrayList<>();
	}
	
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes attr) throws SAXException {
		// �������� ����
		for(int i = 0; i < attr.getLength(); i++) {
			// ���������
			if(qName.equals("relation")) {
				if(!isRelation) {
					isRelation = true; // �� � ���� �����
				}
			
				// ������������� ���������
				if(attr.getQName(i).equals("id")) {
					relation_id = Long.valueOf(attr.getValue(i));
				}
			
				// ��������� ���������������
				if(attr.getQName(i).equals("action")) {
					if(attr.getValue(i).equals("modify")) {
						isModify = true;
					}
				}
			}
			
			// ��� member � ������� ���� ���������
			if(qName.equals("member") && isRelation && !isModify) {
				if(!isMember) {
					isMember = true;
					ways_ids = new ArrayList<Long>();
					roles = new ArrayList<String>();
				}
			
			    // ��� ������� � ���������
				if(attr.getQName(i).equals("type")) {
					if(attr.getValue(i).equals("way")) {
						isWay = true;
						isNoWay = false;
					} else {
						isNoWay = true;
					}
				}
			
				// ������ �� �����, �� ������� ������� ���������
				if(attr.getQName(i).equals("ref") && isWay && !isNoWay)
					ways_ids.add(Long.valueOf(attr.getValue(i)));
			
				// ��� �������� (�����. ��� �����.)
				if(attr.getQName(i).equals("role") && isWay && !isNoWay)
					roles.add(attr.getValue(i));					
			}
		
			// ��� tag � ������� ���� ���������
			if(qName.equals("tag") && isRelation && isWay) {
				if(!isTag)
					isTag = true;
			
				// ����
				if(attr.getQName(i).equals("k")) {
					// ������ (��������� ������)
					if(attr.getValue(i).equals("area:highway")) {
						isAreaHighway = true;
					
						str_k.add("area:highway");
					}
				
					// ������
					if(attr.getValue(i).equals("highway")) {
						isHighway = true;
					
						str_k.add(attr.getValue(i));
					}
				
					// ����������� �����
					if(attr.getValue(i).equals("surface")) {
						isSurface = true;
					
						str_k.add(attr.getValue(i));
					}
				
					// ������
					if(attr.getValue(i).equals("building")) {
						isBuilding = true;
					
						k_position = rewriteTag("building");
					}
				
					// ����� ������
					if(attr.getValue(i).equals("building:part")) {
						isBuildingPart = true;
					
						str_k.add(attr.getValue(i));
					}
				
					// ������ � ������������ ���������
					if(attr.getValue(i).equals("historic:building")) {
						isBuilding = true;
					
						k_position = rewriteTag("building");
					}
				
					// ����������� ��������� ���
					if(attr.getValue(i).equals("waterway")) {
						isWaterway = true;
					
						str_k.add(attr.getValue(i));
					}
				
					// ��������� ���������
					if(attr.getValue(i).equals("aeroway")) {
						isAeroway = true;
					
						str_k.add(attr.getValue(i));
					}
				
					// �������� ������
					if(attr.getValue(i).equals("aerialway")) {
						isAerialway = true;
					
						str_k.add(attr.getValue(i));
					}
				
					// ����� ���������� ������
					if(attr.getValue(i).equals("leisure")) {
						isLeisure = true;
					
						str_k.add(attr.getValue(i));
					}
				
					// ��������������
					if(attr.getValue(i).equals("amenity")) {
						isAmenity = true;
					
						str_k.add(attr.getValue(i));
					}
						// ���������� ������
					if(attr.getValue(i).equals("emergency")) {
						isEmergency = true;
					
						str_k.add(attr.getValue(i));
					}
				
					// �������
					if(attr.getValue(i).equals("boundary")) {
						isBoundary = true;
					
						str_k.add(attr.getValue(i));
					}	
				
					// ����
					if(attr.getValue(i).equals("ford")) {
						isFord = true;
					
						k_position = rewriteTag("highway");
					}
				
					// ������������ �������
					if(attr.getValue(i).equals("cycleway")) {
						isCycleway = true;
					
						k_position = rewriteTag("highway");
					}
				
					// ������
					if(attr.getValue(i).equals("ice_road")) {
						isIceRoad = true;
					
						k_position = rewriteTag("highway");
					}
				
					// ������
					if(attr.getValue(i).equals("winter_road")) {
						isWinterRoad = true;
					
						k_position = rewriteTag("highway");
					}
				
					// ����
					if(attr.getValue(i).equals("office")) {
						isOffice = true;
					
						str_k.add(attr.getValue(i));
					}
				
					// �������
					if(attr.getValue(i).equals("shop")) {
						isShop = true;
					
						str_k.add(attr.getValue(i));
					}
				
					// ����������
					if(attr.getValue(i).equals("craft")) {
						isCraft = true;
					
						str_k.add(attr.getValue(i));
					}
				
					// ��������������, ���������������
					if(attr.getValue(i).equals("healthcare")) {
						isHealthcare = true;
					
						k_position = rewriteTag("amenity");
					}
				
					// ������������ �����
					if(attr.getValue(i).equals("historic")) {
						isHistoric = true;
					
						str_k.add(attr.getValue(i));
					}
				
					// ��������, �/� ������, ����������, ������������� ����������, ������, 
					// ����������������, ������� �������, ��������� �����������, �����, 
					// �������� ����������� �����, ��������� �������, �����, ������, �������� �����, 
					// ��� ���������
					if(attr.getValue(i).equals("barrier") ||
					   attr.getValue(i).equals("railway") ||
					   attr.getValue(i).equals("power") ||
					   attr.getValue(i).equals("man_made") ||  
					   attr.getValue(i).equals("tourism") ||
					   attr.getValue(i).equals("landuse") ||
					   attr.getValue(i).equals("military") ||
					   attr.getValue(i).equals("natural") ||					  				   				   
					   attr.getValue(i).equals("sport") ||
					   attr.getValue(i).equals("tracktype") ||
					   attr.getValue(i).equals("area") ||
					   attr.getValue(i).equals("bridge") ||
					   attr.getValue(i).equals("tunnel") ||
					   attr.getValue(i).equals("name") ||
					   attr.getValue(i).equals("type")) {
						isOther = true;
						
						str_k.add(attr.getValue(i));
					}
				
					// �������� ����� �� ������� �����
					if(attr.getValue(i).equals("name:ru")) {
						isName = true;
						
						k_position = rewriteTag("name");
					}					
				}
				
				// �������� �����
				if(attr.getQName(i).equals("v")) {
					if(isTag) {
						if(isAreaHighway) {
							str_v.add("area");	
							
							isAreaHighway = false;
						}
					
						if(isHighway) {
							if(attr.getValue(i).equals("motorway_link") ||
							   attr.getValue(i).equals("trunk") ||
							   attr.getValue(i).equals("trunk_link"))
								str_v.add("motorway");							
							else if(attr.getValue(i).equals("primary_link"))
								str_v.add("primary");							
							else if(attr.getValue(i).equals("secondary_link"))
								str_v.add("secondary");							
							else if(attr.getValue(i).equals("tertiary_link"))
								str_v.add("tertiary");							
							else if(attr.getValue(i).equals("service") ||
						         	attr.getValue(i).equals("living_street") ||
						            attr.getValue(i).equals("pedestrian"))
								str_v.add("residential");
							else
								str_v.add(attr.getValue(i));
							
							isHighway = false;
						}
					
						if(isSurface) {
							if(attr.getValue(i).equals("concrete:lanes") ||
							   attr.getValue(i).equals("concrete:plates"))
								str_v.add("concrete");
							else if(attr.getValue(i).equals("cobblestone:flattened"))
								str_v.add("cobblestone");
							else if(attr.getValue(i).equals("dirt") ||
								    attr.getValue(i).equals("earth") ||
								    attr.getValue(i).equals("grass") ||
								    attr.getValue(i).equals("grass_paver") ||
								    attr.getValue(i).equals("gravel_turf") ||
								    attr.getValue(i).equals("fine_gravel") ||
								    attr.getValue(i).equals("gravel") ||
								    attr.getValue(i).equals("ground") ||
								    attr.getValue(i).equals("mud") ||
								    attr.getValue(i).equals("pebblestone") ||
								    attr.getValue(i).equals("sand"))
								str_v.add("compacted");	
							else if(attr.getValue(i).equals("artificial_turf"))
								str_v.add("tartan");
							else if(attr.getValue(i).equals("snow"))
								str_v.add("ice");
							else
								str_v.add(attr.getValue(i));
							
							isSurface = false;
						}
					
						if(isBuilding) {
							if(k_position < 0) {
								if(attr.getValue(i).equals("no") && str_k.size() != 0) {
									str_k.remove(str_k.indexOf("building"));
								} else
									if(attr.getValue(i).equals("entrance"))
										str_v.add(attr.getValue(i));
									else
										str_v.add("building");
							} else {
								if(!attr.getValue(i).equals("no"))
									str_v.set(k_position, attr.getValue(i));
							}
							
							isBuilding = false;
						}
						
						if(isBuildingPart) {							
							if(checkBuilding()) {
								str_k.remove(str_k.indexOf("building:part"));
							} else {
								if(attr.getValue(i).equals("no") && str_k.size() > 1) {
									str_k.remove(str_k.indexOf("building:part"));
								} else {
									if(attr.getValue(i).equals("no")) {
										str_k.remove(i - 1);
									} else {
										str_v.add("yes");
									}
								}
							}
							
							isBuildingPart = false;
						}
						
						if(isWaterway) {
							if(attr.getValue(i).equals("boatyard"))
								str_v.add("dock");
							else
								str_v.add(attr.getValue(i));
							
							isWaterway = false;
						}
						
						if(isAeroway) {
							if(attr.getValue(i).equals("helipad"))
								str_v.add("heliport");
							else
								str_v.add(attr.getValue(i));
							
							isAeroway =false;
						}
						
						if(isAerialway) {
							if(attr.getValue(i).equals("gondola") ||
						       attr.getValue(i).equals("chair_lift") ||
							   attr.getValue(i).equals("mixed_lift") ||
							   attr.getValue(i).equals("drag_lift") ||
							   attr.getValue(i).equals("t-bar") ||
							   attr.getValue(i).equals("j-bar") ||
							   attr.getValue(i).equals("platter") ||
							   attr.getValue(i).equals("rope_tow") ||
							   attr.getValue(i).equals("magic_carpet"))
								str_v.add("cable_car");	
							else if(attr.getValue(i).equals("canopy"))
								str_v.add("zip_line");
							else
								str_v.add(attr.getValue(i));
							
							isAerialway = false;
						}
						
						if(isLeisure) {
							if(attr.getValue(i).equals("amusement_arcade"))
								str_v.add("adult_gaming_centre");
							else
								str_v.add(attr.getValue(i));
							
							isLeisure = false;
						}
						
						if(isAmenity) {
							if(attr.getValue(i).equals("gambling"))
								str_v.add("casino");
							else
								str_v.add(attr.getValue(i));
							
							isAmenity = false;
						}
						
						if(isEmergency) {
							if(attr.getValue(i).equals("no") && str_k.size() > 1) {
								str_k.remove(str_k.indexOf("emergency"));
							} else {
								if(attr.getValue(i).equals("no")) {
									str_k.remove(i - 1);
								} else {
									if(attr.getValue(i).equals("lifeguard_platform") ||
									   attr.getValue(i).equals("lifeguard_place"))
										str_v.add("lifeguard_tower");
									else
										str_v.add(attr.getValue(i));
								}
							}
							
							isEmergency = false;
						}
						
						if(isHistoric) {
							if(attr.getValue(i).equals("no") && str_k.size() > 1) {
								str_k.remove(str_k.indexOf("historic"));
							} else {
								if(attr.getValue(i).equals("no")) {
									str_k.remove(i - 1);
								} else {
									str_v.add(attr.getValue(i));
								}
							}
							
							isHistoric = false;
						}
						
						if(isBoundary) {
							str_v.add("boundary");
							
							isBoundary = false;
						}
						
						if(isFord) {
							if(k_position < 0) {
								str_v.add("ford");
							} else {
								str_v.set(k_position, "ford");
								k_position = -1;
							}
							
							isFord = false;
						}
						
						if(isCycleway) {
							if(k_position < 0) {
								str_v.add("cycleway");
							} else {
								str_v.set(k_position, "cycleway");
								k_position = -1;
							}
							
							isCycleway = false;
						}
						
						if(isIceRoad) {
							if(k_position < 0) {
								str_v.add("winter_road");
							} else {
								str_v.set(k_position, "winter_road");
								k_position = -1;
							}
							
							isIceRoad = false;
						}
						
						if(isWinterRoad) {
							if(k_position < 0) {
								str_v.add("winter_road");
							} else {
								str_v.set(k_position, "winter_road");
								k_position = -1;
							}
							
							isWinterRoad = false;
						}
						
						if(isName) {
							if(k_position < 0) {
								str_v.add(attr.getValue(i));
							} else {
								str_v.set(k_position, attr.getValue(i));
								k_position = -1;
							}
							
							isName = false;
						}
						
						if(isOffice) {
							str_v.add("office");
							isOffice = false;
						}
						
						if(isShop) {
							str_v.add("shop");
							isShop = false;
						}
						
						if(isCraft) {
							str_v.add("craft");
							isCraft = false;
						}
						
						if(isHealthcare) {
							if(k_position < 0) {
								str_v.add("healthcare");
							} else {
								str_v.set(k_position, "healthcare");
								k_position = -1;
							}
							
							isHealthcare = false;
						}
						
						if(isOther) {
							str_v.add(attr.getValue(i));
							
							isOther = false;
						}
					}
				}
			}
		}
	}
	
	// ���������� ����
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {}
	
	// ����� �� ����
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		// ���������
		if(qName.equals("relation")) {
			if(isRelation && isMember && isWay && ways_ids != null && ways_ids.size() != 0) {
				if(!isModify) {
					if(isTag) {
						if(str_k.size() != 0 && ways_ids.size() != 0) {
							multipolygon = false; // ������� ����, ��� ����� �������� � ��������� �������� �������
							if (relation_id == 2176173) {
								int y = 0;
							}	
							// ���������� ��� ���������
							for(int i = 0; i < str_k.size(); i++) {
								// �������������
								if(str_k.get(i).equals("type") && str_v.get(i).equals("multipolygon")) {
									multipolygon = true;
									
									// ���������� ��� ������� �� ����� � ���������
									checkTypeOfRelation(str_k, str_v);
								}
							}
							
							// ��� ��������� �������������
							if(multipolygon) {
								incomplete = checkWaysIds(ways_ids);
							
								createWaysUsed(ways_ids);
								checkWaysUsed(ways_ids);
							
								writeRelationProperties();
							}
						}
					
						str_k.clear();
						str_v.clear();
						str_k.trimToSize();
						str_v.trimToSize();
					
						if(objects != null)
							objects = null;
						
						isTag = false;
					}
				
					roles = null;
					ways_ids = null;
					isWay = false;
					isMember = false;
				}
				
				isRelation = false;
			}

			isModify = false;
		}
	}
	
	/**
	 * �������� ������� ������ �� �����.
	 * 
	 * @param tag - ������� ������, ������� ���������� ��������
	 * @param newTag - ����� ������� ������
	 * @return ������� �������� � ������, ������� ��������
	 */
	
	private int rewriteTag(String tag, String newTag) {
		int position = -1; // ������� �������� � ������, ������� ��������
		String tmpStr = "";
		
		if(str_k != null && str_k.size() != 0) {
			for(int i = 0; i < str_k.size(); i++) {
				if(str_k.get(i).equals(tag)) {
					tmpStr = str_k.get(i);
					position = i;
				}
			}
		}
		
		if(tmpStr.equals(tag)) {
			str_k.set(position, newTag);
		} else
			str_k.add(newTag);
		
		return position;
	}
	
	/**
	 * ��������� �� ������� ���������� �� ����� �� ����� ����, ���� ��������� ����� �����������
	 * ������ ������������� �������� � �� ���������.
	 * 
	 * @param ways_ids - �������������� �����
	 * @return
	 */
	
	private boolean checkWaysIds(ArrayList<Long> ways_ids) {
		boolean tmp_incomplete = false; // �� ��������� ����� � �������������� ����������, ������� �������
		
		for(int i = 0; i < ways_ids.size(); i++) {
			try {
				long id = Param.my_ways_ids.get(ways_ids.get(i));
				
				@SuppressWarnings("unused")
				long seek = Param.seek_ways.get(id);
			} catch(NullPointerException ex) {
				if(!tmp_incomplete && roles.get(i).equals("outer"))
					tmp_incomplete = true; // ������������� ��������
				
				ways_ids.remove(i);
				roles.remove(i);
				i -= 1;
				
				if(i < -1)
					i = -1;
				
				continue;
			}
		}
		
		return tmp_incomplete;
	}
	
	/**
	 * �������� ������� ������ �� �����. � ������ ������ ������ ������� � ����� �������
	 * ����������.
	 * 
	 * @param tag - ������� ������, ������� ���������� ��������
	 * @return ������� �������� � ������, ������� ��������
	 */
	
	private int rewriteTag(String tag) {
		return rewriteTag(tag, tag);
	}
	
	/**
	 * ���������� ��� ������� �� ����� ��� ���������.
	 * 
	 * @param tmp_str_k ��������� ������ ���� <tag>
	 * @param tmp_str_v ��������� �������� ������ ���� <tag>
	 */
	
	private void checkTypeOfRelation(ArrayList<String> tmp_str_k, ArrayList<String> tmp_str_v) {
		noMainTag = false; // ������� ����, ��� �������� ��� ������� ����, �� �� ���������
		noAddTag = false; // ������� ����, ��� �������������� ��� ������� ����, �� �� ���������
		
		mainTag = false; // ���������� ��� ���������� �� ��� ���������
		addTag = false; // �������������� ���	
		
		mainTagPosition = -1; // ������� � ������� tmp_str_k ��������� ���� ������� �� �����
		
		typeOfObject = Param.noType; // ��� ���������
		addType = Param.noType; // �������������� ���
		
		objects = new TypesOfObjects();
		
		// ���������� ��� ������� ��������� ����� �������� �����
		for(int i = 0; i < tmp_str_k.size(); i++) {
			switch(tmp_str_k.get(i)) {
			case "area:highway":
			case "highway":
			case "barrier":
			case "building":
			case "building:part":
			case "waterway":
			case "railway":
			case "aeroway":
			case "aerialway":
			case "emergency":
			case "natural":
				if(!mainTag) {
					mainTag = true;
								
					if(objects.getTypeOfObject(tmp_str_k.get(i), tmp_str_v.get(i)) == (short) 0) {
						noMainTag = true; // ��� ����, �� �� ���������
					} else {							
						typeOfObject = objects.getTypeOfObject(tmp_str_k.get(i), tmp_str_v.get(i));
						mainTagPosition = i;
					}
				}
			}
		}
					
		// ���������� ��� ������� ��������� �� ����� ����� ���������� �����
		for(int i  = 0; i < tmp_str_k.size(); i++) {
			switch(tmp_str_k.get(i)) {
			case "power":
			case "landuse":
			case "man_made":
			case "leisure":
			case "amenity":
			case "office":
			case "shop":
			case "craft":
			case "tourism":
			case "historic":
			case "military":
			case "sport":
			case "boundary":
				if(!mainTag) {
					mainTag = true;
								
					if(objects.getTypeOfObject(tmp_str_k.get(i), tmp_str_v.get(i)) == (short) 0) {
						noMainTag = true; // ��� ����, �� �� ���������
					} else {
						typeOfObject = objects.getTypeOfObject(tmp_str_k.get(i), tmp_str_v.get(i));
						mainTagPosition = i;
					}
				} else {
					if(!addTag)
						addTag = true;
								
					if(objects.getTypeOfObject(tmp_str_k.get(i), tmp_str_v.get(i)) == (short) 0) {
						noAddTag = true; // ��� ����, �� �� ���������
					} else {
						addType = objects.getTypeOfObject(tmp_str_k.get(i), tmp_str_v.get(i));
					}
				}
			}
		}
	}
	
	/**
	 * ���������� �������� ��������� � ���� �����.
	 */
	
	private void writeRelationProperties() {
		area = false; // ������� ���������� �������
		bridge = false; // ������� ����, ��� ������ �������� ������
		tunnel = false; // ������� ����, ��� ������ �������� �������
		
		outerWaysId = new ArrayList<>(); // �������������� ����� � ����� outer
		// �������������� ����� � ����� outer, ��� ��������� �������� (������ ����� - ��� ��������� �������)
		outerWaysArray = new ArrayList<>(); // ������ ������� ��������
		innerWaysId = new ArrayList<>(); // �������������� ����� � ����� inner
		// �������������� ����� � ����� inner, ��� ��������� �������� (������ ����� - ��� ��������� �������)
		innerWaysArray = new ArrayList<>(); // ������ ���������� ��������
		
		name = ""; // �������� ������� �� �����
				
		// �������� ��������
		for(int i = 0; i < str_k.size(); i++) {
			if(str_k.get(i).equals("name") && !str_v.get(i).equals("")) 
				name = str_v.get(i);
		}
			
		// ��������� �� ������� �������������� ������� (area, bridge, tunnel).
		for(int i = 0; i < str_k.size(); i++) {
			// ��������� ������
			if(str_k.get(i).equals("area") && str_v.get(i).equals("yes")) 
				area = true;
			
			// ��������� ������
			if(str_k.get(i).equals("area:highway") && str_v.get(i).equals("area")) 
				area = true;
				
			//����
			if(str_k.get(i).equals("bridge")) 
				bridge = true;
				
			//������
			if(str_k.get(i).equals("tunnel"))
				tunnel = true;
		}
			
		// ������� � ��� �������������� ���� �������� �� ����� 
		checkTypeOfObject(mainTag, addTag);
			
		// ��������� �������� �� ������� � ����������
		for(int i = 0; i < ways_ids.size(); i++) {
			long id = Param.my_ways_ids.get(ways_ids.get(i));
			
			// ������ ����� ����������� � �������� �������� � ���������
			if( roles.get(i).equals("outer"))
				outerWaysId.add(id);
			
			// ������ ���������� ��������� � ���������
			if( roles.get(i).equals("inner")) 
				innerWaysId.add(id);
		}
		
		// ��������� ������� �������� �� ������� ���� ������� �� ����� � ��������� ���
		if(typeOfObject == Param.noType)
			typeOfObject = checkTypeOfObjectInOuter(outerWaysId);
		
		// ���� ��� ���� ������� �� �����, �� ������ �� ����������
		if(typeOfObject != Param.noType) {
			// ��������� ���������� �������� �� ��������� ��������
			sortPolygons(inner);				
			
			// ��������� ������� �������� �� ��������� ��������
			sortPolygons(outer);
		}
	}
	
	/**
	 * ��������� ���� �� �������� ������� ����� ��� ���, ���� ���, �� ������� ������ �� ����.
	 * 
	 * @param main_tag - ������� ���� ������� �� �����
	 * @param add_tag - ������� ��������������� ���� ������� ����� 
	 */
	
	private void checkTypeOfObject(boolean main_tag, boolean add_tag) {
		for(int i = 0; i < str_k.size(); i++) {
			if(objects.getTypeOfObject(str_k.get(i), str_v.get(i)) == 0 && 
					                   !str_k.get(i).equals("name") &&
					                   !str_k.get(i).equals("bridge") &&
					                   !str_k.get(i).equals("tunnel") &&
					                   !str_k.get(i).equals("area") &&
					                   !str_k.get(i).equals("type")) {
				OsmConverter.printLog("� ����� TypesOfObject.java ����������� �������� ������� �����:");
				OsmConverter.printLog("   ID: " + relation_id);
				OsmConverter.printLog("   TYPE: " + str_k.get(i) + " = " + str_v.get(i));
				OsmConverter.printLog("   MAIN TYPE = " + main_tag + ",\n   ADD TYPE = " + add_tag + "\r\n");
			}
		}
	}
	
	/**
	 * ��������� ������������ ����� � ������ ���������� ��� ���, � ���� ��������������, ��
	 * ������� ����� �����.
	 * 
	 * @param ways_ids �������������� �����
	 */
	
	private void checkWaysUsed(ArrayList<Long> ways_ids) {
		long seek = -1;
//		Param.outer_ways_used_in_relations.clear();
		
		// ������������ � ������ ������
		for(int i = 0; i < ways_ids.size(); i++) {
			try {
				long id = Param.my_ways_ids.get(ways_ids.get(i));
				
				seek = Param.seek_ways_used.get(id);
				
				if(seek >= 0) {
					long newNodeId = Param.maxNodeId;
					newNodeId++;
					if (Param.newIndex == 4828315301l) {
						int y = 0;
					}
					long newSeek = raf.createNewWay(raf.getNodeIdsFromWay(id));
					
					if(newSeek >= 0) {
						ways_ids.set(i, newNodeId);
						Param.seek_ways_used.put(newNodeId, newSeek);
						Param.outer_ways_used_in_relations.put(newNodeId, id);
					} else
						OsmConverter.printLog("�� ������� ������� ����� �����.");
				}
					
			} catch(NullPointerException ex) {
				continue;
			}
		}
	}
	
	/**
	 * ������� ������, � ������� ��������� �������� ������ ����� ����� ��� �������������� 
	 * ����� � ����������.
	 * 
	 * @param way_ids �������������� �����
	 */
	
	private void createWaysUsed(ArrayList<Long> way_ids) {
		for(int i = 0; i < way_ids.size(); i++) {
			long id = Param.my_ways_ids.get(way_ids.get(i));
			
			Param.seek_ways_used.put(id, Param.seek_ways.get(id));
//			Param.seek_ways_used_in_relations.put(id, Param.seek_ways.get(id));
		}
	}
	
	/**
	 * ��������� ����� �����, ��������� �� ���������� �����.
	 * ������ � ������� ���� ����� �� ������ ����� ������� (����� ������ �� ������ �����).
	 * 
	 * @param ways_ids �������������� �����
	 * @return ������������� ����� �����
	 */
	
	private long saveNewWay(ArrayList<Long> ways_ids) {
		if(ways_ids.size() > 1) {
			// ������� ���������, ����� ������ � ��������� �����, ���� ����� �����
			raf.deleteDublicatNodes(ways_ids);
			
			int new_num_size = 0;
			
			for(int i = 0; i < ways_ids.size(); i++) {
				new_num_size += raf.getNodeIdsFromWay(ways_ids.get(i)).size();
			}
			
			for(int i = 1; i < ways_ids.size(); i++) {
				Param.num_points.remove(ways_ids.get(i));
				Param.seek_ways.remove(ways_ids.get(i));
				Param.seek_ways_used.remove(ways_ids.get(i));
//				Param.seek_ways_used_in_relations.remove(ways_ids.get(i));
			}
			
			Param.num_points.put(ways_ids.get(0), new_num_size);
//			raf.setSeekFirstNodeInWay(ways_ids.get(0));
			
			// ��� ��� ��������� �� ��������� �����
			raf.deleteDublicatNodes(ways_ids.get(0));
		}
		
		return ways_ids.get(0);
	}
	
	/**
	 * ��������� ������ �� ������� ���� building.
	 * 
	 * @return true, ���� ������ �������� ��� building, ����� - false
	 */
	
	private boolean checkBuilding() {
		if(str_k.size() != 0) {
			for(int i = 0; i < str_k.size(); i++) {
				if(str_k.get(i).equals("building"))
					return true;
			}
		}
		
		return false;
	}
	
	/**
	 * ��������� ��������� �������� ���������.
	 * 
	 * @param way_id ������������� ����� (��������)
	 * @param typeOfObject ��� ������� �� �����
	 * @param addType �������������� ��� ������� �� �����
	 * @param area ������� ���������� �������
	 * @param isClosed ������� ����, ��� ������� ������� ������� ��� �� ���� (������, ���������)
	 * @param bridge ������� ����, ��� ������ �������� ������
	 * @param tunnel ������� ����, ��� ������ �������� �������
	 * @param noMainTag ������� ����, ��� ��� ������� �� ����� ����, �� �� �� ���������
	 * @param noAddTag ������� ����, ��� �������������� ��� ������� �� ����� ����, �� �� �� ���������
	 * @param isOuter ������� ����, ��� ������� ������� ��� ����������
	 */
	
	private void setPropertiesInPolygons(long way_id,
			                             short typeOfObject,
			                             short addType,
			                             boolean isClosed,
			                             boolean area,			                            
			                             boolean bridge,
			                             boolean tunnel,
			                             boolean noMainTag,
			                             boolean noAddTag,
			                             boolean isOuter) {		
		// ���� ������� �������
		if(isOuter) {
			if (way_id == 4828315301l) {
				int y = 0;
			}
			// ������� ���� �������� ��������
    		raf.clearAttrSeek(Param.seek_ways.get(way_id));
    		
    		// ���������� ��� ������� �� ����� � ��� ��� �� ������� �������
    		// ���������� ������� �� �������. � ���� ���� ��� ������� � ��� �������
		
    		if(typeOfObject > 0) { // ����������� �������� ��� ���������
    			raf.setTypeOfObject(Param.seek_ways.get(way_id), typeOfObject);    			
//    			raf.setAttrSeek(Param.seek_ways.get(way_id), Param.seek_ways.get(way_id), attrSeek);
    		} else if(noMainTag) { // ����������� ����������� ��� ���������
				raf.setTypeOfObject(Param.seek_ways.get(way_id), Param.unknownType);				
//				raf.setAttrSeek(Param.seek_ways.get(way_id), Param.seek_ways.get(way_id), attrSeek);		
			} else {
				raf.setTypeOfObject(Param.seek_ways.get(way_id), Param.noType);
//				raf.setAttrSeek(Param.seek_ways.get(way_id), Param.seek_ways.get(way_id), attrSeek);
			}
		
    		if(noAddTag) {
    			raf.setAdditionalTypeOfObject(Param.seek_ways.get(way_id), Param.unknownType);	
    		} else {
     			raf.setAdditionalTypeOfObject(Param.seek_ways.get(way_id), addType);	
    		}			
				
    		// ��������� ������
    		if(area) 
    			raf.setType(Param.poligon_outer, Param.seek_ways.get(way_id));
		
	    	//����
    		if(bridge) 
	    		raf.setPropertyType(Param.seek_ways.get(way_id), Param.property_bridge);
		
    		//������
    		if(tunnel)
    			raf.setPropertyType(Param.seek_ways.get(way_id), Param.property_tunnel);
    		
    		// ���� ������� ��������, �� ����������� ��� �������� ����� - �����
    		if(!isClosed) {
    			raf.setType(Param.line, Param.seek_ways.get(way_id));
    		}
    	} else {  
    		// ������� ���� �������� ��������
    		raf.clearAttrSeek(Param.seek_ways.get(way_id));
    		
    		// ���� ���������� ������� ����� ��� ������� �����
 /*   		if(raf.getTypeOfObject(Param.seek_ways.get(way_id)) > 0) {
    			// ������� ���� ��� � ������ ���������� ������� ��� ���� ������� �� �����
    			raf.setTypeOfObject(Param.seek_ways.get(way_id), Param.noType);
    		}*/
    	} 
	}
	
	/**
	 * ��������� ������� � ���������� �������� �� ��������� ��������.
	 * 
	 * @param typeOfPolygon ��� �������� ��� ����������
	 */
	
	private void sortPolygons(short typeOfPolygon) {
		int firstOuterWay = 0; // ������ �����, � ������� ���������� ��������� ����� �����
		ArrayList<Long> tmpWaysIds = new ArrayList<>(); // �������������� ����� � ����� outer
		boolean firstElement = false; // ������ ������� � ����� ��������
		
		if(typeOfPolygon == outer) {
			for(int i = 0; i < outerWaysId.size(); i++) {
				// ������ ����� ����������� � �������� �������� � ���������
				if(raf.checkFirstAndLastNodes(outerWaysId.get(firstOuterWay))) {
					tmpWaysIds.add(outerWaysId.get(i));
					outerWaysArray.add((new ArrayList<Long>(tmpWaysIds)));
					tmpWaysIds.clear();
						
					firstOuterWay++;
				} else {
					if(outerWaysId.size() > 1 && (i + 1) < outerWaysId.size()) {
						if(raf.checkNodesIdInWays(outerWaysId.get(firstOuterWay), outerWaysId.get(i + 1))) {
							tmpWaysIds.add(outerWaysId.get(i + 1));
						} else {
							if(raf.checkNodesIdInWays(outerWaysId.get(i), outerWaysId.get(i + 1))) {
								tmpWaysIds.add(outerWaysId.get(i + 1));
							} else {
								tmpWaysIds.add(0, outerWaysId.get(firstOuterWay));
								outerWaysArray.add((new ArrayList<Long>(tmpWaysIds)));
								tmpWaysIds.clear();
									
								firstOuterWay = i + 1;
							}
						}
					} else {
						if(tmpWaysIds.size() != 0) {
							tmpWaysIds.add(0, outerWaysId.get(firstOuterWay));
							outerWaysArray.add((new ArrayList<Long>(tmpWaysIds)));
							tmpWaysIds.clear();
						} else {
							tmpWaysIds.add(outerWaysId.get(firstOuterWay));
							outerWaysArray.add((new ArrayList<Long>(tmpWaysIds)));
							tmpWaysIds.clear();
						}
					}
				}
			}
			
			// ��� ��� ��������� ��������� ������� �������� �� ����� �����
			if(outerWaysArray.size() > 1) {
				outerWaysArray = raf.checkNodesIdInOuterWays(outerWaysArray);
			}	
			
			// ������������ ������ ������������ ��������������� ����� ������� ��������� 
			// � ����� ���� �� �����
//			modifyOuterWaysUsedInRelations();
			
			// ������������� ������� ��������
			raf.sortWays(outerWaysArray);
			
			// ��������� �������� �������� �� �������������� � ������ ������� ����� 
			// �, ���� ��� ���, ������ ����� ������� (������) 
			if(incomplete && !objects.getTypeOfObject(typeOfObject).equals("boundary")
					      && !objects.getTypeOfObject(typeOfObject).equals("barrier")
					      && !objects.getTypeOfObject(typeOfObject).equals("building:part")) {
				outerWaysArray = raf.checkNodesIdInIncompleteOuterWays(outerWaysArray);
			}
			
			// ��������� ������� ������� ��������
			for(int k = 0; k < outerWaysArray.size(); k++) {
				tmpWaysIds = outerWaysArray.get(k);
				byte type = Param.poligon_outer;
				firstElement = false;
				// ������� ����, ��� ������� ������� (������)
				boolean isClosed = raf.checkFirstAndLastNodesInWays(tmpWaysIds);
				
				// ������������� ������ (���������)
				if(isClosed) {						
					for(int i = 0; i < tmpWaysIds.size(); i++) {
						// ������� �������
						if(tmpWaysIds.size() != 0) {							
							// ������� � ������� �� �������� ���������� ���������
							if(objects.getTypeOfObject(typeOfObject).equals("boundary") ||
							   objects.getTypeOfObject(typeOfObject).equals("barrier") ||
							   objects.getTypeOfObject(typeOfObject).equals("building:part"))
								type = Param.poligon_line;
							
							if(!firstElement) {
								raf.setType(type, Param.seek_ways.get(tmpWaysIds.get(i)));
								firstElement = true;
							} else {
								raf.setNextInLastElement(Param.seek_ways.get(tmpWaysIds.get(i)),
								    	                 Param.seek_ways.get(tmpWaysIds.get(i - 1)));
								raf.setType(type, Param.seek_ways.get(tmpWaysIds.get(i)));								
							}
						}
					}
					
					// ������� ��������� ����� � ��������
					raf.deleteDublicatNodes(tmpWaysIds);					
				} else { // ������������� �������� (�� ���������)
					for(int i = 0; i < tmpWaysIds.size(); i++) {
						// ������� �������
						if(tmpWaysIds.size() != 0) {
							if(!firstElement) {
								raf.setType(Param.line, Param.seek_ways.get(tmpWaysIds.get(i)));
								firstElement = true;
							} else {
								raf.setNextInLastElement(Param.seek_ways.get(tmpWaysIds.get(i)),
									                     Param.seek_ways.get(tmpWaysIds.get(i - 1)));
								raf.setType(Param.line, Param.seek_ways.get(tmpWaysIds.get(i)));								
							}
						}
					}
				
					// ������� ��������� ����� � ��������
					raf.deleteDublicatNodes(tmpWaysIds);
				}
				
				// ������� �������� ���� ����� ����� ������
				for(int i = 1; i < tmpWaysIds.size(); i++) {
					long attrSeek = raf.getArrtSeek(Param.seek_ways.get(tmpWaysIds.get(i)));
					int index = Param.delete_attrs.indexOf(attrSeek);
					
					if(index == -1)
						Param.delete_attrs.add(attrSeek);
						
				}
				
				// ���������� ��-�� ��������
				setPropertiesInPolygons(tmpWaysIds.get(0),
		                                typeOfObject, 
		                                addType, isClosed,
		                                area, bridge, tunnel,
		                                noMainTag, noAddTag, true);				
								
				// �������������� �������� �������� ��������
				if(!name.equals("") && tmpWaysIds.size() != 0)
					raf.setName(Param.seek_nodes.get(tmpWaysIds.get(0)), name);
				
				// �������� �����
				if(mainTagPosition >= 0) {
					if(str_k.get(mainTagPosition).equals("highway") || str_k.get(mainTagPosition).equals("area:highway")) {
						// ��������� �� ������� �������� ������
						for(int i = 0; i < str_k.size(); i++) {
							if(str_k.get(i).equals("surface")) {
								raf.setAdditionalTypeOfObject(Param.seek_ways.get(tmpWaysIds.get(0)), 
	   								                          objects.getTypeOfObject(str_k.get(i), 
										                        		              str_v.get(i)));								
								// ��������
								if(str_v.get(i).equals("decoturf") && isClosed) 
									raf.setType(Param.poligon_outer, Param.seek_ways.get(tmpWaysIds.get(0)));
							}
							
							if(str_k.get(i).equals("tracktype"))
								raf.setAdditionalTypeOfObject(Param.seek_ways.get(tmpWaysIds.get(0)), 
										                      objects.getTypeOfObject(str_k.get(i), 
										                        		              str_v.get(i)));
						}
					}
				} else {
					// ��������� �� ������� �������� ������ � ���������� ���� ������� �� �����
					for(int i = 0; i < str_k.size(); i++) {
						if(str_k.get(i).equals("surface")) {
							raf.setTypeOfObject(Param.seek_ways.get(tmpWaysIds.get(0)), (short) 37); // ��� ������ surface
							raf.setAdditionalTypeOfObject(Param.seek_ways.get(tmpWaysIds.get(0)), 
   								                          objects.getTypeOfObject(str_k.get(i), 
									                        		              str_v.get(i)));								
							// ��������
							if(str_v.get(i).equals("decoturf") && isClosed) 
								raf.setType(Param.poligon_outer, Param.seek_ways.get(tmpWaysIds.get(0)));
						}
						
						if(str_k.get(i).equals("tracktype")) {
							raf.setTypeOfObject(Param.seek_ways.get(tmpWaysIds.get(0)), (short) 37); // ��� ������ surface
							raf.setAdditionalTypeOfObject(Param.seek_ways.get(tmpWaysIds.get(0)), 
									                      objects.getTypeOfObject(str_k.get(i), 
									                        		              str_v.get(i)));
						}
					}
				}
					
				// �� ���������� ����� � ��������� ���� ������� ���� ����� ����.
				// �������� �� � ������� ���� �����.
				saveNewWay(tmpWaysIds);
				
				// ����������� �������� �������� ����������
				if(innerWaysId.size() != 0 && isClosed) {
					if(outerWaysArray.size() > 1) {
						raf.assignedOuterWaysInnerWays(tmpWaysIds.get(0), innerWaysId, Param.ray);
					} else {
						raf.assignedOuterWaysInnerWays(tmpWaysIds.get(0), innerWaysId, Param.bound);
					}
				}
				
				if(type == Param.poligon_line)
					Param.seek_ways_with_poligon_line_type.add(Param.seek_ways.get(tmpWaysIds.get(0)));
				
				tmpWaysIds.clear();
			}
		}
		
		if(typeOfPolygon == inner) {
			for(int i = 0; i < innerWaysId.size(); i++) {
				// ������ ����� ����������� � ����������� �������� � ���������
				if(raf.checkFirstAndLastNodes(innerWaysId.get(firstOuterWay))) {
					tmpWaysIds.add(innerWaysId.get(i));
					innerWaysArray.add((new ArrayList<Long>(tmpWaysIds)));
					tmpWaysIds.clear();
						
					firstOuterWay++;
				} else {
					if(innerWaysId.size() > 1 && (i + 1) < innerWaysId.size()) {
						if(raf.checkNodesIdInWays(innerWaysId.get(firstOuterWay), innerWaysId.get(i + 1))) {
							tmpWaysIds.add(innerWaysId.get(i + 1));
						} else {
							if(raf.checkNodesIdInWays(innerWaysId.get(i), innerWaysId.get(i + 1))) {
								tmpWaysIds.add(innerWaysId.get(i + 1));
							} else {
								tmpWaysIds.add(0, innerWaysId.get(firstOuterWay));
								innerWaysArray.add((new ArrayList<Long>(tmpWaysIds)));
								tmpWaysIds.clear();
									
								firstOuterWay = i + 1;
							}
						}
					} else {
						if(tmpWaysIds.size() != 0) {
							tmpWaysIds.add(0, innerWaysId.get(firstOuterWay));
							innerWaysArray.add((new ArrayList<Long>(tmpWaysIds)));
							tmpWaysIds.clear();
						} else {
							tmpWaysIds.add(innerWaysId.get(firstOuterWay));
							innerWaysArray.add((new ArrayList<Long>(tmpWaysIds)));
							tmpWaysIds.clear();
						}
					}
				}
			}
			
			// ������� ������ ���������� ���������, ����� �������� ���� �����
			innerWaysId.clear();
					
			// ������������� ���������� ��������
			raf.sortWays(innerWaysArray);
						
			// ��������� ������� ���������� ��������
			for(int k = 0; k < innerWaysArray.size(); k++) {
				tmpWaysIds = innerWaysArray.get(k);
				firstElement = false;
				
				// ��������� ���������� �������� �� ����������� (�� �������), ���� ������� �� ������ (�� ����������),
				// �� ������� ��� �� ������ ���������� ���������	
				if(!raf.checkFirstAndLastNodesInWays(tmpWaysIds)) {
					// ������� ��� ������� �� ����� � �� ���������� (�� �������) �������� 
					for(int i = 0; i < tmpWaysIds.size(); i++) {
						raf.setTypeOfObject(Param.seek_ways.get(tmpWaysIds.get(i)), Param.noType);
					}
					
					tmpWaysIds.clear();					
					continue;
				}
				
				for(int i = 0; i < tmpWaysIds.size(); i++) {
					// ���������� �������
					if(tmpWaysIds.size() != 0) {
						if(!firstElement) {							
							raf.setType(Param.poligon_inner, Param.seek_ways.get(tmpWaysIds.get(i)));
							firstElement = true;
						} else {
							raf.setNextInLastElement(Param.seek_ways.get(tmpWaysIds.get(i)),
							    	                 Param.seek_ways.get(tmpWaysIds.get(i - 1)));
							raf.setType(Param.poligon_inner, Param.seek_ways.get(tmpWaysIds.get(i)));								
						}
					}			
				}
				
				// ������� ��������� ����� � ��������
				raf.deleteDublicatNodes(tmpWaysIds);
					
				// ������� �������� ���� ����� ����� ������
				for(int i = 1; i < tmpWaysIds.size(); i++) {
					long attrSeek = raf.getArrtSeek(Param.seek_ways.get(tmpWaysIds.get(i)));
					int index = Param.delete_attrs.indexOf(attrSeek);
					
					if(index == -1)
						Param.delete_attrs.add(attrSeek);
						
				}
				
				// ���������� ��-�� ��������
				setPropertiesInPolygons(tmpWaysIds.get(0),
						                typeOfObject, 
						                addType, false,
						                area, bridge, tunnel,
			                            noMainTag, noAddTag, false);
				// �� ���������� ����� � ��������� ���� ������� ���� ����� ����.
				// �������� �� � ������� ���� �����.
				innerWaysId.add(saveNewWay(tmpWaysIds));// ���������� �������� ����� ����������
				
				tmpWaysIds.clear();
			}
		}
	}
	
	/**
	 * ���������� ������� ���� ������� �� ����� � ��������������� �������
	 * ����������.
	 * 
	 * @param outerWaysId �������������� �����, ������������� ������� ���������
	 * @return ��� ������� �� �����
	 */
	private short checkTypeOfObjectInOuter(ArrayList<Long> outerWaysId) {
		short typeOfObject = Param.noType;
		
		for(int i = 0; i < outerWaysId.size(); i++) {
			typeOfObject = raf.getTypeOfObject(Param.seek_ways.get(outerWaysId.get(i)));
			
			// ������ ���������� ��� ������� �� �����
			if(typeOfObject > Param.noType) {
				Param.ids_outer_ways_used_in_relation_for_delete.add(outerWaysId.get(i));
				break;
			}
		}
		
		return typeOfObject;
	}
}
