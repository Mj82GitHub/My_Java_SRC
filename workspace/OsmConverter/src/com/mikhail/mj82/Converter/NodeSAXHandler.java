/*
 * Copyright (c) 12.2016
 */

package com.mikhail.mj82.Converter;

import java.io.IOException;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import OsmConverter.OsmConverter;

/**
 * ����� ������ xml ���� osm �����, ��������� ������ � ������ ����� � ���������� � �������� ����
 * ������������ �������.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class NodeSAXHandler extends DefaultHandler {
	
	private RndAccessFile raf; // ���������� � ��������� ������ �/�� �����
	private Types objects; // ���� �������� �� �����
	
	// ����
	private boolean isNode = false; // �����
	// �������� �����
	private boolean isHighway = false; // ������
	private boolean isFord = false; // ����
	private boolean isName = false; // ��������
	private boolean isBuilding = false; // ������
	private boolean isEntrance = false; // ���� � ������
	private boolean isOffice = false; // ����
	private boolean isHistoric = false; // ������������ �����
	private boolean isHealthcare = false; // ��������������, ���������������
	private boolean isShop = false; // �������
	private boolean isCraft = false; // ����������
	private boolean isPlace = false; // ���������� �����
	private boolean isEmergency = false; // ���������� ������
	private boolean isTag = false; // ������� ���� <tag>
	private boolean isOther = false; // ��� ���������, ���� ��� � ���� ��������� ���������
	
	// ��� ���������� �������� ������
	private double minLat = 0; // ����������� �������� ������ ��� �����
	private double minLon = 0; // ����������� �������� ������� ��� �����
	private double maxLat = 0; // ������������ �������� ������ ��� �����
	private double maxLon = 0; // ������������ �������� ������� ��� �����
	private long node_id = -1; // ������������� �����
	private boolean isNodeDelete = false; // �������, ��� ����� � OSM ����� ������� � �� ������������
	private double lat = 0; // ������ �����
	private double lon = 0; // ������� �����
	
	private long node_seek = -1; // ��������� ��������. ������������ ��� ���� ������� ������
	private int k_position = -1; // ������� ����������� � ��������� ��������
	private ArrayList<String> str_k; // ����� ���� tag
	private ArrayList<String> str_v; // �������� ���� tag	

	/**
	 * ������� ���� �����.
	 */
	
	public void createNewFiles() {
		str_k = new ArrayList<>();
		str_v = new ArrayList<>();
		
		raf = new RndAccessFile();
		raf.createNewFiles();
	}
		
	// ���� � ���
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes attr) throws SAXException {
		// �������� ����
		for(int i = 0; i < attr.getLength(); i++) {
			// ������� �����
			if(qName.equals("bounds")) {
				// ����������� ������
				if(attr.getQName(i).equals("minlat")) {
					minLat = Double.valueOf(attr.getValue(i));					
				} 
				
				// ����������� �������
				if(attr.getQName(i).equals("minlon")) {
					minLon = Double.valueOf(attr.getValue(i));
				} 
				
				// ������������ ������
				if(attr.getQName(i).equals("maxlat")) {
					maxLat = Double.valueOf(attr.getValue(i));
				} 
				
				// ������������ �������
				if(attr.getQName(i).equals("maxlon")) {
					maxLon = Double.valueOf(attr.getValue(i));
					
					raf.setMapBounds(minLat, minLon, maxLat, maxLon);
				} 
			}
			
			// �����
			if(qName.equals("node")) {
				if(!isNode) {
					isNode = true; // �� � ���� �����
				}
				
				// ������������� �����
				if(attr.getQName(i).equals("id")) {	
					try {
						node_id = Long.valueOf(attr.getValue(i));
						node_seek = raf.getLengthFile(); // �������� � �����, � �������� ���������� ��� node
												
						// ��������� ������� ������������ id � �������� � ����� �����
						Param.seek_nodes.put(node_id, node_seek);
						
						// ���� ����� ������� ������������� � �����
						if(node_id > Param.maxNodeId) {
							Param.maxNodeId = node_id;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				// ����� ������� (�� ����� �� ������������)
				if(attr.getQName(i).equals("action")) {
					if(attr.getValue(i).equals("delete")) {
						isNodeDelete = true;
						Param.seek_nodes.remove(node_id);
					}
				}
				
				// ������
				if(attr.getQName(i).equals("lat")) {
					lat = Double.valueOf(attr.getValue(i));
				}
				
				// �������
				if(attr.getQName(i).equals("lon")) {
					lon = Double.valueOf(attr.getValue(i));
				}
			}
			
			// ��� tag � ������� ���� �����
		    if(qName.equals("tag") && isNode && !isNodeDelete) {
		    	if(!isTag)
		    		isTag = true;
			
		    	// ����
		    	if(attr.getQName(i).equals("k")) {
		    		// ������
				    if(attr.getValue(i).equals("highway")) {
			    		isHighway = true;
					
				    	k_position = rewriteTag("highway");
			    	}
				
			    	// ����
			     	if(attr.getValue(i).equals("ford")) {
				    	isFord = true;
					
			    		k_position = rewriteTag("highway");
			    	}
				
			    	// ������
			    	if(attr.getValue(i).equals("building")) {
						isBuilding = true;
						
						k_position = rewriteTag("building");
					}
					
					// ���� � ������
					if(attr.getValue(i).equals("entrance")) {
						isEntrance = true;
						
						k_position = rewriteTag("building");
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
					
					// ���������� �����
					if(attr.getValue(i).equals("place")) {
						isPlace = true;
						
						str_k.add(attr.getValue(i));
					}
					
					// ��������������, ���������������
					if(attr.getValue(i).equals("healthcare")) {
						isHealthcare = true;
						
						k_position = rewriteTag("amenity");
					}
					
					// ���������� ������
					if(attr.getValue(i).equals("emergency")) {
						isEmergency = true;
						
						k_position = rewriteTag("emergency");
					}
					
					// ������������ �����
					if(attr.getValue(i).equals("historic")) {
						isHistoric = true;
						
						str_k.add(attr.getValue(i));
					}
					
					// ��������, ����������� ��������� ���, �/� ������, ��������� ���������,
					// �������� ������, ����������, ����������������, ��������� �����������,
					// ������������� ����������, ����� ���������� ������, ��������������,
					// ������, ������� �������, �����, �����, ������, �������� �����
					if(attr.getValue(i).equals("barrier") ||
					   attr.getValue(i).equals("waterway") ||
					   attr.getValue(i).equals("railway") ||
					   attr.getValue(i).equals("aeroway") ||
					   attr.getValue(i).equals("aerialway") ||
					   attr.getValue(i).equals("power") ||
					   attr.getValue(i).equals("landuse") ||
					   attr.getValue(i).equals("natural") ||
					   attr.getValue(i).equals("man_made") ||
					   attr.getValue(i).equals("leisure") ||
					   attr.getValue(i).equals("amenity") || 
					   attr.getValue(i).equals("tourism") ||				   
					   attr.getValue(i).equals("military") ||					   				   
					   attr.getValue(i).equals("sport") ||
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
						if(isHighway) {
							if(k_position < 0) {
								str_v.add(attr.getValue(i));
							} else {
								str_v.set(k_position, attr.getValue(i));
								k_position = -1;
							}
							
							isHighway = false;
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
						
						if(isName) {
							if(k_position < 0) {
								str_v.add(attr.getValue(i));
							} else {
								str_v.set(k_position, attr.getValue(i));
								k_position = -1;
							}
							
							isName = false;
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
						
						if(isEntrance) {
							if(k_position < 0) {
								str_v.add("entrance");
							} else {
								str_v.set(k_position, "entrance");
								k_position = -1;
							}
							
							isEntrance = false;
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
						
						if(isPlace) {
							str_v.add("place");
							isPlace = false;
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
						
						if(isEmergency) {
							if(attr.getValue(i).equals("no") && str_k.size() > 1) {
								str_k.remove(str_k.indexOf("emergency"));
							} else {
								if(attr.getValue(i).equals("no")) {
									str_k.remove(i - 1);
								} else {
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
		if(qName.equals("node")) {
			if(isNode) {
				if(!isNodeDelete) {
					checkNodesUsed(node_id);
	
					// ���������� � ���� ����� ��������� �����
					raf.setNodeParam(node_id, lat, lon);				
				
					if(isTag) {
						objects = new TypesOfObjects();
					
						if(str_k != null && str_k.size() != 0) {
							writeNodeProperties(str_k, str_v, objects);	
						}
			
						str_k.clear();
						str_v.clear();
						str_k.trimToSize();
						str_v.trimToSize();
						objects = null;
						isTag = false;
					}
				}
				
				isNode = false; // �� �� � ���� �����
			}
			
			isNodeDelete = false;
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
	 * ���������� �������� ����� � ���� ����� map.hnvg.
	 * 
	 * @param tmp_str_k - ��������� ������ ���� <tag>
	 * @param tmp_str_v - ��������� �������� ������ ���� <tag>
	 * @param objects - ���� �������� �� �����
	 */
	
	private void writeNodeProperties(ArrayList<String> tmp_str_k, ArrayList<String> tmp_str_v, Types objects) {
		// ���������� ��� ������� �����
		boolean mainTag = false; // ���������� ��� ���������� �� ��� ������� �� �����
		boolean addTag = false; // �������������� ��� ������� ����� (�������)
		// �������� �������������� �������� �����
		ArrayList<String> add_str_k = null; 
		ArrayList<String> add_str_v = null;	
		
		// ���������� ��� ������� �� ����� ����� �������� �����
		for(int i = 0; i < tmp_str_k.size(); i++) {
			switch(tmp_str_k.get(i)) {
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
						raf.setTypeOfObject(node_seek, Param.unknownType); // ��� ����, �� �� ���������
						// ���� ���� ��� ������� �� �����, �� ����� �������� ������������ �� �����
						createNodesUsed(node_id);
					} else {
						raf.setTypeOfObject(node_seek, objects.getTypeOfObject(tmp_str_k.get(i), tmp_str_v.get(i)));
						// ���� ���� ��� ������� �� �����, �� ����� �������� ������������ �� �����
						createNodesUsed(node_id);
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
			case "place":
				if(!mainTag) {
					mainTag = true;	
					
					if(objects.getTypeOfObject(tmp_str_k.get(i), tmp_str_v.get(i)) == (short) 0) {
						raf.setTypeOfObject(node_seek, Param.unknownType); // ��� ����, �� �� ���������
						// ���� ���� ��� ������� �� �����, �� ����� �������� ������������ �� �����
						createNodesUsed(node_id);
					} else {
						raf.setTypeOfObject(node_seek, objects.getTypeOfObject(tmp_str_k.get(i), tmp_str_v.get(i)));
						// ���� ���� ��� ������� �� �����, �� ����� �������� ������������ �� �����
						createNodesUsed(node_id);
					}					
				} else {
					if(!addTag) 
						addTag = true;
					
					if(objects.getTypeOfObject(tmp_str_k.get(i), tmp_str_v.get(i)) == (short) 0) {
						raf.setAdditionalTypeOfObject(node_seek, Param.unknownType);
					} else {
						raf.setAdditionalTypeOfObject(node_seek, objects.getTypeOfObject(tmp_str_k.get(i), tmp_str_v.get(i)));
					}					
				}
				break;
			}
		}
		
		// ��������� �� ������� �������������� ������� (bridge, tunnel).
		for(int i = 0; i < tmp_str_k.size(); i++) {					
			//����
			if(tmp_str_k.get(i).equals("bridge")) {
				raf.setPropertyType(node_seek, Param.property_bridge);
			}
			
			//������
			if(tmp_str_k.get(i).equals("tunnel")) {
				raf.setPropertyType(node_seek, Param.property_tunnel);
			}
		}
		
		// �������� ��������
		for(int i = 0; i < tmp_str_k.size(); i++) {
			if(tmp_str_k.get(i).equals("name") && !tmp_str_v.get(i).equals("")) 
				raf.setName(node_seek, tmp_str_v.get(i));
		}
		
		// ���� ��� �������� ���� � �����
		createNewObjects(add_str_k,	add_str_v);
							
		// ������� � ��� �������������� ���� �������� �� �����
		checkTypeOfObject(mainTag, addTag);
	}
	
	/**
	 * ��� ������� ���������� �������� ����� ������� ����� ������� �����.
	 * 
	 * @param tmp_str_k ��������� ������ ���� <tag>
	 * @param tmp_str_v ��������� �������� ������ ���� <tag>
	 */
	
	private void createNewObjects(ArrayList<String> add_str_k, ArrayList<String> add_str_v) {
		if(add_str_k != null && add_str_k.size() != 0) {
			for(int i = 0; i < add_str_k.size(); i++) {
				long newSeek = raf.createNewNode(node_seek);
				
				if(newSeek >= 0) {
					Param.seek_nodes.put(Param.maxNodeId, newSeek);
					Param.seek_nodes_used.put(Param.maxNodeId, newSeek);
					
					// �������������� ��� ������
					if(objects.getTypeOfObject(add_str_k.get(i), add_str_v.get(i)) == (short) 0) {
						raf.setTypeOfObject(newSeek, Param.unknownType); // ��� ����, �� �� ���������
					} else {
						raf.setTypeOfObject(newSeek, objects.getTypeOfObject(add_str_k.get(i), add_str_v.get(i)));
					}
				} else {
					OsmConverter.printLog("�� ������� ������� ����� �����.");
				}
			}
		}
	}
	
	/**
	 * ������� ������, � ������� ��������� �������� ����� ��� �������������� �����.
	 * 
	 * @param node_id - ������������� ������
	 */
	
	private void createNodesUsed(long node_id) {
		Param.seek_nodes_used.put(node_id, Param.seek_nodes.get(node_id));
	}
	
	/**
	 * ��������� �������������� ����� ����� ��� ���, � ���� ��������������, ��
	 * ������� ����� �����. 
	 * 
	 * @param node_id - ������������� �����
	 */
	
	private void checkNodesUsed(long node_id) {
		long seek = -1;
		
		// �������������� ����� �����
		try {
			seek = Param.seek_nodes_used.get(node_id);
			
			if(seek >= 0) {
				// ���� ������������� ��� �������������, �� �������� ��� �� �����
				long newId = Param.maxNodeId++; // ����� ������������� �����				
				raf.renameId(seek, newId);
				
				Param.seek_nodes.remove(node_id);
				Param.seek_nodes_used.remove(node_id);				
				Param.seek_nodes.put(Param.maxNodeId, seek);
				Param.seek_nodes_used.put(Param.maxNodeId, seek);
				
				Param.seek_nodes.put(node_id, node_seek);
				Param.seek_nodes_used.put(node_id, node_seek);
			}
		} catch(NullPointerException ex) {
			// ���� ����� ��� � ������, ���� ������
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
					                   !str_k.get(i).equals("area")) {
				OsmConverter.printLog("� ����� TypesOfObject.java ����������� �������� ������� �����:");
				OsmConverter.printLog("   ID: " + node_id);
				OsmConverter.printLog("   TYPE: " + str_k.get(i) + " = " + str_v.get(i));
				OsmConverter.printLog("   MAIN TYPE = " + main_tag + ",\n   ADD TYPE = " + add_tag + "\r\n");
			}
		}
	}
}
