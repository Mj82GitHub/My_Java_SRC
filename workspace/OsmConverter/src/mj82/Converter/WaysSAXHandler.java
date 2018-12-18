/*
 * Copyright (c) 12.2016
 */

package mj82.Converter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import OsmConverter.OsmConverter;

/**
 * ����� ������ xml ���� osm �����, ��������� ������ � ������ ����� � � ������������ � ����
 * ������ ��������� � �������� ����� �����.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class WaysSAXHandler extends DefaultHandler { 
	
	private RndAccessFile raf; // ���������� � ��������� ������ �/�� �����
	private Types objects; // ���� �������� �� �����	
	
	private ArrayList<Long> nodes_ids; // ������ id ����� � �����	
	
	// ����
	private boolean isWay = false; // �����
	private boolean isNd = false; // ������� ������ �� �����
	
	// �������� �����
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

	private long way_id = -1; // ������������� ����� � OSM �����
	private boolean isWayDelete = false; // �������, ��� ����� � OSM ����� ������� � �� ������������
	private boolean incomplete = false; // �� ��������� � ����� ������������ ��� �����
	
/*	FileWriter wf;
	public WaysSAXHandler(FileWriter fwf) {
		wf = fwf;
		str_k = new ArrayList<>();
		str_v = new ArrayList<>();
		
		raf = new RndAccessFile();
	}*/
	
	public WaysSAXHandler() {
		str_k = new ArrayList<>();
		str_v = new ArrayList<>();
		
		raf = new RndAccessFile();
	}
	
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes attr) throws SAXException {
		// �������� ����
		for(int i = 0; i < attr.getLength(); i++) {
			// �����
			if(qName.equals("way")) {
				if(!isWay) {
					isWay = true; // �� � ���� �����
				}
			
				// ������������� �����
				if(attr.getQName(i).equals("id")) {
					way_id = Long.valueOf(attr.getValue(i));
				
					 // ���� ����� ������� ������������� � �����
					if(way_id > Param.maxNodeId) {
						Param.maxNodeId = way_id;
					}
				}
			
				// ����� ������� (�� ����� �� ������������)
				if(attr.getQName(i).equals("action")) {
					if(attr.getValue(i).equals("delete")) {
						isWayDelete = true;
					}
				}		
			}
			
			// ��� nd � ������� ���� �����
    		if(qName.equals("nd") && isWay && !isWayDelete) {
    			if(!isNd) {
    				isNd = true;
    				nodes_ids = new ArrayList<Long>();
    			}
				
    			// ������ �� �����, �� ������� ������� �����
	    		if(attr.getQName(i).equals("ref")) {
    				nodes_ids.add(Long.valueOf(attr.getValue(i)));
    			} 
    		}
		
	    	// ��� tag � ������� ���� �����
    		if(qName.equals("tag") && isWay && isNd) {
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
				
	    			// ����������� ������
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
					
    					k_position = rewriteTag("emergency");
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
			
			    		// ����������� ����������
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
	    			// �������� ����������� ������, ��������� ������, �����, ������, �������� �����
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
	    			   attr.getValue(i).equals("name")) {
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
		// �����
		if(qName.equals("way")) {
			if(isWay && isNd && nodes_ids != null && nodes_ids.size() != 0) {
				if(!isWayDelete) {
					
					if(way_id == 226050754) {
						int y = 0;
					}
					incomplete = checkNodesIds(nodes_ids);
//					createOnlyNodes(nodes_ids);
					
//					createFile(); // ���������� ����� ����� � ��������� ����
					
					checkNodesUsed(nodes_ids);				
					createNodesUsed(nodes_ids);		
					
					// ���������� � ������ �������������� ����� �� �������������� ������ ����� � �����
					Param.seek_ways.put(nodes_ids.get(0), Param.seek_nodes.get(nodes_ids.get(0)));
				
					// ��������� ������ ������������� �������������� ����� � ����� OSM ��������������
					// ������ ����� � �����
					Param.my_ways_ids.put(way_id, nodes_ids.get(0));				
										
					createNumPoints(nodes_ids.get(0), nodes_ids.size());
											
					if(isTag) {
						if(str_k.size() != 0 && nodes_ids.size() != 0) {
							objects = new TypesOfObjects();
							writeWayProperties(nodes_ids, str_k, str_v, objects);
						} else
							writeWayProperties(nodes_ids, null, null, null);
						
						str_k.clear();
						str_v.clear();
						str_k.trimToSize();
						str_v.trimToSize();
					
						if(objects != null)
							objects = null;
						
						isTag = false;
					} else
						writeWayProperties(nodes_ids, null, null, null);
				
					nodes_ids = null;
					isNd = false;
				}
				
				isWay = false;
			}
			
			isWayDelete = false;
		}
	}
	
	/** 
	 * ��������� �� ������� ���������� �� ����� � ����� ����� �� ����� ����.
	 * 
	 * @param node_ids �������������� �����
	 */
	
	private boolean checkNodesIds(ArrayList<Long> node_ids) {
		boolean tmp_incomplete = false; // �� ��������� � ����� ������������ ��� �����
		
		for(int i = 0; i < node_ids.size(); i++) {
			try {
				@SuppressWarnings("unused")
				long tmp = Param.seek_nodes.get(node_ids.get(i));
			} catch(NullPointerException ex) {
				if(!tmp_incomplete)
					tmp_incomplete = true; // ����� ��������
				
				node_ids.remove(i);
				i -= 1;
				
				if(i < -1)
					i = -1;
				
				continue;
			}
		}
		
		return tmp_incomplete;
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
	 * ���������� �������� ����� � ���� �����.
	 * 
	 * @param nodes_ids ������ id ����� � �����
	 * @param tmp_str_k ��������� ������ ���� <tag>
	 * @param tmp_str_v ��������� �������� ������ ���� <tag>
	 * @param objects ���� �������� �� �����
	 */
	
	private void writeWayProperties(ArrayList<Long> nodes_ids, 
			                        ArrayList<String> tmp_str_k, ArrayList<String> tmp_str_v, 
			                        Types objects) {
		
		long firstSeek = Param.seek_nodes.get(nodes_ids.get(0)); // �������� ������� �������� � �����
		
		// ���� ��� ����� ����������� �����
		if(tmp_str_k == null) {
			// ��������� �������� ����� (��� �������)
			for(int i = 0; i < nodes_ids.size(); i++) {
				// ������ ��� �������� (�����)						
				if(i == (nodes_ids.size() - 1)) {
					if(!nodes_ids.get(i).equals(nodes_ids.get(0))) // ������ ����� �� ����� ���������
						raf.setWay(Param.line, Param.seek_nodes.get(nodes_ids.get(i)), (long) 0);
				} else {
					raf.setWay(Param.line, Param.seek_nodes.get(nodes_ids.get(i)), 
							   Param.seek_nodes.get(nodes_ids.get(i + 1)));
				}
			}
			
			// ���� ����� �� ����� ���� ������� ����� (��� ��������), �� ������� ������ ������� ��� 
			// �������� ������ �� ������ ����� �����
			if(raf.getArrtSeek(Param.seek_ways.get(nodes_ids.get(0))) == 0) {
				raf.setTypeOfObject(Param.seek_ways.get(nodes_ids.get(0)), Param.noType);
//				raf.setAttrSeek(Param.seek_ways.get(nodes_ids.get(0)), Param.seek_ways.get(nodes_ids.get(0)), 
//						        raf.getArrtSeek(Param.seek_ways.get(nodes_ids.get(0))));
			}
		// ���� ���� ���� ����������� �����
		} else {
			boolean mainTag = false; // ���������� ��� ���������� �� ��� ������� �� �����
			boolean addTag = false; // �������������� ��� ������� ����� (�������)
			// �������� �������������� �������� �����
			ArrayList<String> add_str_k = null; 
			ArrayList<String> add_str_v = null;			
			
			int mainTagPosition = -1; // ������� � ������� tmp_str_k ��������� ���� ������� �� �����
			
			// ��������� �������� ����� (��� �������)
			for(int i = 0; i < nodes_ids.size(); i++) {
				// ������ ��� �������� (�����)						
				if(i == (nodes_ids.size() - 1)) {
					if(!nodes_ids.get(i).equals(nodes_ids.get(0))) // ������ ����� �� ����� ���������
						raf.setWay(Param.line, Param.seek_nodes.get(nodes_ids.get(i)), (long) 0);
				} else {
					raf.setWay(Param.line, Param.seek_nodes.get(nodes_ids.get(i)), 
							   Param.seek_nodes.get(nodes_ids.get(i + 1)));
				}
			}
			
			// ���������� ��� ������� �� ����� ����� �������� �����
			for(int i = 0; i < tmp_str_k.size(); i++) {
				switch(tmp_str_k.get(i)) {
//				case "area:highway":
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
							raf.setTypeOfObject(firstSeek, Param.unknownType); // ��� ����, �� �� ���������
						} else {
							raf.setTypeOfObject(firstSeek, objects.getTypeOfObject(tmp_str_k.get(i), tmp_str_v.get(i)));
							mainTagPosition = i;
						}
					
						// ���� ����� ������ (� ������� ��� �����), �� ����������
						if(!incomplete) {
							// � ����������� �� ���� ������� �� ����� ������ ��� ��������
							setNewType(tmp_str_k.get(i), tmp_str_v.get(i), firstSeek);
						}
					} else { // ���� ��� ������� ���� � �����
						if(add_str_k == null) {
							add_str_k = new ArrayList<String>();
							add_str_v = new ArrayList<String>();
						}
						
						add_str_k.add(tmp_str_k.get(i));
						add_str_v.add(tmp_str_v.get(i));
					}
					break;
				}
			}

			// ���������� ��� ������� �� ����� ����� ���������� �����
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
							raf.setTypeOfObject(firstSeek, Param.unknownType); // ��� ����, �� �� ���������
						} else {
							raf.setTypeOfObject(firstSeek, objects.getTypeOfObject(tmp_str_k.get(i), tmp_str_v.get(i)));
							mainTagPosition = i;
						}
						
						// ���� ����� ������ (� ������� ��� �����), �� ����������
						if(!incomplete) {
							// � ����������� �� ���� ������� �� ����� ������ ��� ��������
							setAddNewType(tmp_str_k.get(i), tmp_str_v.get(i), firstSeek);
						}
					} else {
						if(!addTag) 
							addTag = true;
						
						if(objects.getTypeOfObject(tmp_str_k.get(i), tmp_str_v.get(i)) == (short) 0) {
							raf.setAdditionalTypeOfObject(firstSeek, Param.unknownType);
						} else {
							raf.setAdditionalTypeOfObject(firstSeek, objects.getTypeOfObject(tmp_str_k.get(i), tmp_str_v.get(i)));
						}						
						
						// � ����������� �� ���� ������� �� ����� ������ ��� ��������
//						setAddNewType(tmp_str_k.get(i), tmp_str_v.get(i), firstSeek);
					}
					break;
				}
			}
					
			// ��������� �� ������� �������������� ������� (area, bridge, tunnel).
			for(int i = 0; i < tmp_str_k.size(); i++) {
				// ��������� ������
				if(tmp_str_k.get(i).equals("area") && tmp_str_v.get(i).equals("yes")) 
					raf.setType(Param.poligon_outer, firstSeek);
				
				// ��������� ������
//				if(str_k.get(i).equals("area:highway") && str_v.get(i).equals("area")) 
//					raf.setType(Param.poligon_outer, firstSeek);
				
				//����
				if(tmp_str_k.get(i).equals("bridge")) 
					raf.setPropertyType(firstSeek, Param.property_bridge);
				
				//������
				if(tmp_str_k.get(i).equals("tunnel"))
					raf.setPropertyType(firstSeek, Param.property_tunnel);
			}
			
			// �������� �����
			if(mainTagPosition >= 0) {
				if(tmp_str_k.get(mainTagPosition).equals("highway")) {
					// ��������� �� ������� �������� ������
					for(int i = 0; i < tmp_str_k.size(); i++) {
						if(tmp_str_k.get(i).equals("surface")) {
							raf.setAdditionalTypeOfObject(firstSeek, objects.getTypeOfObject(tmp_str_k.get(i), tmp_str_v.get(i)));
						
							// ��������
							if(tmp_str_v.get(i).equals("decoturf")) 
								raf.setType(Param.poligon_outer, firstSeek);
						}
					
						if(tmp_str_k.get(i).equals("tracktype"))
							raf.setAdditionalTypeOfObject(firstSeek, objects.getTypeOfObject(tmp_str_k.get(i), tmp_str_v.get(i)));
					}
				}
			}
			
			// �������� ��������
			for(int i = 0; i < tmp_str_k.size(); i++) {
				if(tmp_str_k.get(i).equals("name") && !tmp_str_v.get(i).equals("")) 
					raf.setName(firstSeek, tmp_str_v.get(i));
			}
			
			// ���� ��� �������� ���� � �����
			createNewObjects(add_str_k,	add_str_v);
			
			// ������� � ��� �������������� ���� �������� �� �����
			checkTypeOfObject(mainTag, addTag);
			
			// ���� ����� �� ����� ���� ������� ����� (��� ��������), �� ������� ������ ������� ��� 
			// �������� ������ �� ������ ����� �����
			if(raf.getArrtSeek(Param.seek_ways.get(nodes_ids.get(0))) == 0) {
				raf.setTypeOfObject(Param.seek_ways.get(nodes_ids.get(0)), Param.noType);
			}
		}
		
		// ��������� ����� �� ������� ���� �������� ����� � ����� � ����������� ������������
		raf.checkDublicatCoords(firstSeek);
	}
	
	/**
	 * ������������� ��� ��������� ��� �������� �����.
	 * 
	 * @param tmp_str_k ��������� ������ ���� <tag>
	 * @param tmp_str_v ��������� �������� ������ ���� <tag>
	 * @param firstSeek �������� ������� �������� � �����
	 */
	
	private void setNewType(String tmp_str_k, String tmp_str_v, long firstSeek) {
		// ������
		if(tmp_str_k.equals("building") && objects.getTypeOfObject(tmp_str_k, tmp_str_v) != (short) 0)
			if(nodes_ids.size() > 2)
				raf.setType(Param.poligon_outer, firstSeek);
			else
				raf.setTypeOfObject(firstSeek, Param.unknownType);
		
		// ����� �����
		if(tmp_str_k.equals("building:part") && objects.getTypeOfObject(tmp_str_k, tmp_str_v) != (short) 0)
			if(raf.checkFirstAndLastNodes(Param.my_ways_ids.get(way_id))) {
				raf.setType(Param.poligon_line, firstSeek);
				
				Param.seek_ways_with_poligon_line_type.add(firstSeek);
			}
	
		// ��������� ����
		if(tmp_str_k.equals("waterway") && tmp_str_v.equals("riverbank") )
			if(raf.checkFirstAndLastNodes(Param.my_ways_ids.get(way_id)))
				raf.setType(Param.poligon_outer, firstSeek);
	
		// �������� ������
		if(tmp_str_k.equals("railway") && tmp_str_v.equals("roundhouse"))
			if(raf.checkFirstAndLastNodes(Param.my_ways_ids.get(way_id)))
				raf.setType(Param.poligon_outer, firstSeek);
		
		// ��������� ���������
		if(tmp_str_k.equals("aeroway") && tmp_str_v.equals("apron"))
			if(raf.checkFirstAndLastNodes(Param.my_ways_ids.get(way_id)))
				raf.setType(Param.poligon_outer, firstSeek);
			
		// ��������� �����������
		if(tmp_str_k.equals("natural") && (!tmp_str_v.equals("tree_row") &&
                                           !tmp_str_v.equals("tree") &&
                                           !tmp_str_v.equals("coastline") &&
                                           !tmp_str_v.equals("spring") &&
                                           !tmp_str_v.equals("hot_spring") &&
                                           !tmp_str_v.equals("geyser") &&
                                           !tmp_str_v.equals("peak") &&
                                           !tmp_str_v.equals("volcano") &&
                                           !tmp_str_v.equals("valley") &&
                                           !tmp_str_v.equals("river_terrace") &&
                                           !tmp_str_v.equals("ridge") &&
                                           !tmp_str_v.equals("arete") &&
                                           !tmp_str_v.equals("cliff") &&
                                           !tmp_str_v.equals("saddle") &&
                                           !tmp_str_v.equals("stone") &&
                                           objects.getTypeOfObject(tmp_str_k, tmp_str_v) != (short) 0)) 
			if(raf.checkFirstAndLastNodes(Param.my_ways_ids.get(way_id)))
				raf.setType(Param.poligon_outer, firstSeek);
	}
	
	/** 
	 * ������������� ��� ��������� ��� ���������� �����.
	 * 
	 * @param tmp_str_k ��������� ������ ���� <tag>
	 * @param tmp_str_v ��������� �������� ������ ���� <tag>
	 * @param firstSeek �������� ������� �������� � �����
	 */
	
	private void setAddNewType(String tmp_str_k, String tmp_str_v, long firstSeek) {
		// �����
		if(tmp_str_k.equals("office") && objects.getTypeOfObject(tmp_str_k, tmp_str_v) != (short) 0) 
			if(raf.checkFirstAndLastNodes(Param.my_ways_ids.get(way_id)))
				raf.setType(Param.poligon_outer, firstSeek);
		
		// ��������
		if(tmp_str_k.equals("shop") && objects.getTypeOfObject(tmp_str_k, tmp_str_v) != (short) 0) 
			if(raf.checkFirstAndLastNodes(Param.my_ways_ids.get(way_id)))
				raf.setType(Param.poligon_outer, firstSeek);
		
		// ����������
		if(tmp_str_k.equals("craft") && objects.getTypeOfObject(tmp_str_k, tmp_str_v) != (short) 0) 
			if(raf.checkFirstAndLastNodes(Param.my_ways_ids.get(way_id)))
				raf.setType(Param.poligon_outer, firstSeek);
		
		// ������
		if(tmp_str_k.equals("tourism") && (!tmp_str_v.equals("artwork") &&
				                           !tmp_str_v.equals("viewpoint") &&
				                           objects.getTypeOfObject(tmp_str_k, tmp_str_v) != (short) 0)) 
			if(raf.checkFirstAndLastNodes(Param.my_ways_ids.get(way_id)))
				raf.setType(Param.poligon_outer, firstSeek);
		
		// �����
		if(tmp_str_k.equals("sport") && (!tmp_str_v.equals("archery") &&
		                                 !tmp_str_v.equals("athletics") &&
		                                 !tmp_str_v.equals("base") &&
		                                 !tmp_str_v.equals("bmx") &&
		                                 !tmp_str_v.equals("bobsleigh") &&
		                                 !tmp_str_v.equals("canoe") &&
		                                 !tmp_str_v.equals("cliff_diving") &&
		                                 !tmp_str_v.equals("cycling") &&
		                                 !tmp_str_v.equals("dog_racing") &&
		                                 !tmp_str_v.equals("equestrian") &&
		                                 !tmp_str_v.equals("golf") &&
		                                 !tmp_str_v.equals("horse_racing") &&
		                                 !tmp_str_v.equals("karting") &&
		                                 !tmp_str_v.equals("motocross") &&
		                                 !tmp_str_v.equals("motor") &&
		                                 !tmp_str_v.equals("multi") &&
		                                 !tmp_str_v.equals("obstacle_course_sport") &&
		                                 !tmp_str_v.equals("orienteering") &&
		                                 !tmp_str_v.equals("racquet") &&
		                                 !tmp_str_v.equals("rc_car") &&
		                                 !tmp_str_v.equals("roller_skating") &&
		                                 !tmp_str_v.equals("rowing") &&
		                                 !tmp_str_v.equals("sailing") &&
		                                 !tmp_str_v.equals("scuba_diving") &&
		                                 !tmp_str_v.equals("shooting") &&
		                                 !tmp_str_v.equals("skateboard") &&
		                                 !tmp_str_v.equals("surfing") &&
		                                 !tmp_str_v.equals("swimming") &&
		                                 !tmp_str_v.equals("toboggan") &&
		                                 !tmp_str_v.equals("water_ski") &&
		                                 !tmp_str_v.equals("skiing") && 
		                                 objects.getTypeOfObject(tmp_str_k, tmp_str_v) != (short) 0)) 
			if(raf.checkFirstAndLastNodes(Param.my_ways_ids.get(way_id)))
				raf.setType(Param.poligon_outer, firstSeek);
		
		// �������
		if(tmp_str_k.equals("boundary") && tmp_str_v.equals("administrative")) 
			if(raf.checkFirstAndLastNodes(Param.my_ways_ids.get(way_id))) {
				raf.setType(Param.poligon_line, firstSeek);
				
				Param.seek_ways_with_poligon_line_type.add(firstSeek);
			}
		
		// ������������ �����
		if(tmp_str_k.equals("historic") && (!tmp_str_v.equals("boundary_stone") &&
				                            !tmp_str_v.equals("cannon") &&
			                                !tmp_str_v.equals("citywalls") &&
			                                !tmp_str_v.equals("cannon") &&
			                                !tmp_str_v.equals("gallows") &&
			                                !tmp_str_v.equals("milestone") &&
			                                !tmp_str_v.equals("pillory") &&
			                                !tmp_str_v.equals("rune_stone") && 
			                                objects.getTypeOfObject(tmp_str_k, tmp_str_v) != (short) 0)) 
			if(raf.checkFirstAndLastNodes(Param.my_ways_ids.get(way_id)))
				raf.setType(Param.poligon_outer, firstSeek);
		
		// ����������������, ���������� ���������� 
		if(tmp_str_k.equals("landuse") && objects.getTypeOfObject(tmp_str_k, tmp_str_v) != (short) 0) 
			if(raf.checkFirstAndLastNodes(Param.my_ways_ids.get(way_id)))
				raf.setType(Param.poligon_outer, firstSeek);
		
		// ������� �������
		if(tmp_str_k.equals("military") && (!tmp_str_v.equals("checkpoint") &&
				                            !tmp_str_v.equals("obstacle_course") && 
				                            objects.getTypeOfObject(tmp_str_k, tmp_str_v) != (short) 0)) 
			if(raf.checkFirstAndLastNodes(Param.my_ways_ids.get(way_id)))
				raf.setType(Param.poligon_outer, firstSeek);
		
		// ����������
		if(tmp_str_k.equals("power") && (tmp_str_v.equals("plant") || 
		                                 tmp_str_v.equals("compensator") || 
		                                 tmp_str_v.equals("converter") ||
		                                 tmp_str_v.equals("generator") ||
		                                 tmp_str_v.equals("substation") ||
		                                 tmp_str_v.equals("sub_station"))) 
			if(raf.checkFirstAndLastNodes(Param.my_ways_ids.get(way_id)))
				raf.setType(Param.poligon_outer, firstSeek);
		
		// ������������� ����������
		if(tmp_str_k.equals("man_made") && (tmp_str_v.equals("adit") ||
		                                    tmp_str_v.equals("bridge") ||
		                                    tmp_str_v.equals("bunker_silo") ||
		                                    tmp_str_v.equals("communications_tower") ||
		                                    tmp_str_v.equals("crane") ||
		                                    tmp_str_v.equals("clearcut") ||
		                                    tmp_str_v.equals("kiln") ||
		                                    tmp_str_v.equals("mineshaft") ||
		                                    tmp_str_v.equals("monitoring_station") ||
		                                    tmp_str_v.equals("observatory") ||
		                                    tmp_str_v.equals("offshore_platform") ||
		                                    tmp_str_v.equals("reservoir_covered") ||
		                                    tmp_str_v.equals("silo") ||
		                                    tmp_str_v.equals("storage_tank") ||
		                                    tmp_str_v.equals("street_cabinet") ||
		                                    tmp_str_v.equals("surveillance") ||
		                                    tmp_str_v.equals("telescope") ||
		                                    tmp_str_v.equals("tower") ||
		                                    tmp_str_v.equals("wastewater_plant") ||
		                                    tmp_str_v.equals("watermill") ||
		                                    tmp_str_v.equals("water_tower") ||
		                                    tmp_str_v.equals("water_well") ||
		                                    tmp_str_v.equals("surveillance") ||
		                                    tmp_str_v.equals("telescope") ||
		                                    tmp_str_v.equals("water_tap") ||
		                                    tmp_str_v.equals("water_works") ||
		                                    tmp_str_v.equals("windmill") ||
		                                    tmp_str_v.equals("works"))) 
			if(raf.checkFirstAndLastNodes(Param.my_ways_ids.get(way_id)))
				raf.setType(Param.poligon_outer, firstSeek);
		
		// ����� ���������� ������
		if(tmp_str_k.equals("leisure") && (!tmp_str_v.equals("slipway") &&
				                           !tmp_str_v.equals("track") && 
				                           objects.getTypeOfObject(tmp_str_k, tmp_str_v) != (short) 0)) 
			if(raf.checkFirstAndLastNodes(Param.my_ways_ids.get(way_id)))
				raf.setType(Param.poligon_outer, firstSeek);
		
		// ��������������, ���������������
		if(tmp_str_k.equals("amenity") && (!tmp_str_v.equals("bbq") &&
		                                   !tmp_str_v.equals("drinking_water") &&
		                                   !tmp_str_v.equals("charging_station") &&
		                                   !tmp_str_v.equals("grit_bin") &&
		                                   !tmp_str_v.equals("parking_entrance") &&
		                                   !tmp_str_v.equals("atm") &&
		                                   !tmp_str_v.equals("bureau_de_change") &&
		                                   !tmp_str_v.equals("stripclub") &&
		                                   !tmp_str_v.equals("bench") &&
		                                   !tmp_str_v.equals("clock") &&
		                                   !tmp_str_v.equals("marketplace") &&
		                                   !tmp_str_v.equals("photo_booth") &&
		                                   !tmp_str_v.equals("post_box") &&
		                                   !tmp_str_v.equals("telephone") &&
		                                   !tmp_str_v.equals("vending_machine") &&
		                                   !tmp_str_v.equals("waste_basket") &&
		                                   !tmp_str_v.equals("watering_place") &&
		                                   !tmp_str_v.equals("water_point") &&
		                                   !tmp_str_v.equals("ev_charging") && 
		                                   objects.getTypeOfObject(tmp_str_k, tmp_str_v) != (short) 0)) 
			if(raf.checkFirstAndLastNodes(Param.my_ways_ids.get(way_id)))
				raf.setType(Param.poligon_outer, firstSeek);
		
		// ���������� ������
		if(tmp_str_k.equals("emergency") && (tmp_str_v.equals("ambulance_station") ||
		                                     tmp_str_v.equals("lifeguard_base") ||
		                                     tmp_str_v.equals("lifeguard_tower") ||
		                                     tmp_str_v.equals("lifeguard_base") ||
		                                     tmp_str_v.equals("assembly_point") ||
		                                     tmp_str_v.equals("ses_station"))) 
			if(raf.checkFirstAndLastNodes(Param.my_ways_ids.get(way_id)))
				raf.setType(Param.poligon_outer, firstSeek);
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
					                   !str_k.get(i).equals("area")) {
				OsmConverter.printLog("� ����� TypesOfObject.java ����������� �������� ������� �����:");
				OsmConverter.printLog("   ID: " + way_id);
				OsmConverter.printLog("   TYPE: " + str_k.get(i) + " = " + str_v.get(i));
				OsmConverter.printLog("   MAIN TYPE = " + main_tag + ",\n   ADD TYPE = " + add_tag + "\r\n");
			}
		}
	}
	
	/**
	 * ��������� ������������ ����� � ������ ������ ��� ���, � ���� ��������������, ��
	 * ������� ����� �����, � ����� ���� �� ���������� ����� � ����� �����.
	 * 
	 * @param nodes_ids �������������� �����
	 */
	
	private void checkNodesUsed(ArrayList<Long> nodes_ids) {
		long seek = -1;
		
		// ������������ � ������ ������
		for(int i = 0; i < nodes_ids.size(); i++) {
			try {
				seek = Param.seek_nodes_used.get(nodes_ids.get(i));
				
				if(seek >= 0) {						
					long newSeek = raf.createNewNode(seek);
					
					if(Param.maxNodeId == 4827366011l) {
						int y = 0;
					}
					if(newSeek >= 0) {
						nodes_ids.set(i, Param.maxNodeId);
						Param.seek_nodes.put(Param.maxNodeId, newSeek);
//						Param.seek_nodes_used.put(Param.maxNodeId, newSeek);
					} else
						OsmConverter.printLog("�� ������� ������� ����� �����.");
				}
			} catch(NullPointerException ex) {
				continue;
			}
		}
		
		// ���������� ����� � ����� ����� ����� ������ � ���������
		int size = nodes_ids.size(); // ������ ������� � ���������������� ����� � �����
		
		// ���� ������ � ��������� ����� �����, �� �� ���������� ��
		if(nodes_ids.get(0) == nodes_ids.get(nodes_ids.size() - 1))
			size -= 1;
			
		for(int i = 0; i < size; i++) {
			for(int j = i + 1; j < size; j++) {
				// ������������ �������� ���������������
				long node_id_1 = nodes_ids.get(i);
				long node_id_2 = nodes_ids.get(j);
				
				if(node_id_1 == node_id_2) {					
					long newSeek = raf.createNewNode(Param.seek_nodes.get(nodes_ids.get(j)));
					
					if(newSeek >= 0) {
						nodes_ids.set(j, Param.maxNodeId);
						Param.seek_nodes.put(Param.maxNodeId, newSeek);
						Param.seek_nodes_used.put(Param.maxNodeId, newSeek);
					} else
						OsmConverter.printLog("�� ������� ������� ����� �����.");
				}
			}
		}
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
	 * ������� ������, � ������� ��������� �������� ����� ��� �������������� ����� � ������.
	 * 
	 * @param nodes_ids - �������������� �����
	 */
	
	private void createNodesUsed(ArrayList<Long> nodes_ids) {
		for(int i = 0; i < nodes_ids.size(); i++)
			Param.seek_nodes_used.put(nodes_ids.get(i), Param.seek_nodes.get(nodes_ids.get(i)));
	}
	
	/**
	 * ������� ������, � ������� ��������� �������� ����� �� �������� � �����.
	 */
	
	@SuppressWarnings("unused")
	private void createOnlyNodes(ArrayList<Long> nodes_ids) {
		for(int i = 0; i < nodes_ids.size(); i++) {
			if(raf.getArrtSeek(Param.seek_nodes.get(nodes_ids.get(i))) == 0)
				Param.seek_nodes_without_ways.remove(nodes_ids.get(i));
		}
	}
		
	/**
	 * ��������� ����������� ���������� ����� � ����� (�� ����������� ����� � ���������).
	 * 
	 * @param way_id - ������������� �����
	 * @param size - ���������� ����� � �����
	 */
	
	private void createNumPoints(long way_id, int size) {
		Param.num_points.put(way_id, size);
	}
	
	/**
	 * ��� ������� ���������� �������� ����� ������� ����� ������� �����.
	 * 
	 * @param add_str_k ��������� ������ ���� <tag>
	 * @param add_str_v ��������� �������� ������ ���� <tag>
	 */
	
	private void createNewObjects(ArrayList<String> add_str_k, ArrayList<String> add_str_v) {
		if(add_str_k != null && add_str_k.size() != 0) {
			for(int i = 0; i < add_str_k.size(); i++) {
				long newfirstSeek = raf.createNewWay(nodes_ids);
				
				if(objects.getTypeOfObject(add_str_k.get(i), add_str_v.get(i)) == (short) 0) {
					raf.setTypeOfObject(newfirstSeek, Param.unknownType); // ��� ����, �� �� ���������
				} else {
					raf.setTypeOfObject(newfirstSeek, objects.getTypeOfObject(add_str_k.get(i), add_str_v.get(i)));
				}					
				
				// ���� ����� ������ (� ������� ��� �����), �� ����������
				if(!incomplete) {
					// ������������� ��� �������� (�������)
					setNewType(add_str_k.get(i), add_str_v.get(i), newfirstSeek);
				}
				
				// ��������� ����� �� ������� ���� �������� ����� � ����� � ����������� ������������
				raf.checkDublicatCoords(newfirstSeek);
			}
		}
	}
	
/*	private void createFile() {
		try {			
			for(int i = 0; i < nodes_ids.size(); i++) {
				if(!Param.osm_ways.contains(nodes_ids.get(i))) {
					Param.osm_ways.add(nodes_ids.get(i));
					
					double lat = 0;
					double lon = 0;
					
					String str = "<node id=\"" + nodes_ids.get(i) + "\" version=\"2\" timestamp=\"2014-01-14T01:37:33Z\" uid=\"731064\" user=\"AzurRu\" changeset=\"19982959\" lat=\"" + raf.getLatitude(nodes_ids.get(i)) +"\" lon=\"" + raf.getLongitude(nodes_ids.get(i)) + "\"/>" + "\r\n";
					
					// ���������� ���� Log.txt
					wf.write(str);
					wf.flush();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
}
