/*
 * Copyright (c) 12.2016
 */

package com.mikhail.mj82.nvg.Converter;

import java.io.IOException;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import OsmConverter.OsmConverter;

/**
 * Класс парсит xml файл osm карты, считывает данные о точках карты и записывает в выходной файл
 * собственного формата.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class NodeSAXHandler extends DefaultHandler {
	
	private RndAccessFile raf; // Записывает и считывает данные в/из файла
	private Types objects; // Типы объектов на карте
	
	// Теги
	private boolean isNode = false; // Точка
	// Атрибуты тегов
	private boolean isHighway = false; // Дорога
	private boolean isFord = false; // Брод
	private boolean isName = false; // Описание
	private boolean isBuilding = false; // Здание
	private boolean isEntrance = false; // Вход в здание
	private boolean isOffice = false; // Офис
	private boolean isHistoric = false; // Историческое место
	private boolean isHealthcare = false; // Инфраструктура, благоустройство
	private boolean isShop = false; // Магазин
	private boolean isCraft = false; // Мастерская
	private boolean isPlace = false; // Населенный пункт
	private boolean isEmergency = false; // Экстренные службы
	private boolean isTag = false; // Наличие тега <tag>
	private boolean isOther = false; // Все остальное, чего нет в выше описанных атрибутах
	
	// Для временного хранения данных
	private double minLat = 0; // Минимальное значение широты для карты
	private double minLon = 0; // Минимальное значение долготы для карты
	private double maxLat = 0; // Максимальное значение широты для карты
	private double maxLon = 0; // Максимальное значение долготы для карты
	private long node_id = -1; // Идентификатор точки
	private boolean isNodeDelete = false; // Признак, что точка в OSM карте удалена и не отображается
	private double lat = 0; // Широта точки
	private double lon = 0; // Долгота точки
	
	private long node_seek = -1; // Временное смещение. Используется для пост вставки данных
	private int k_position = -1; // Позиция заменяемого в коллекции элемента
	private ArrayList<String> str_k; // Ключи тега tag
	private ArrayList<String> str_v; // Значения тега tag	

	/**
	 * Создает файл карты.
	 */
	
	public void createNewFiles() {
		str_k = new ArrayList<>();
		str_v = new ArrayList<>();
		
		raf = new RndAccessFile();
		raf.createNewFiles();
	}
		
	// Вход в тег
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes attr) throws SAXException {
		// Атрибуты тега
		for(int i = 0; i < attr.getLength(); i++) {
			// Граница карты
			if(qName.equals("bounds")) {
				// Минимальная широта
				if(attr.getQName(i).equals("minlat")) {
					minLat = Double.valueOf(attr.getValue(i));					
				} 
				
				// Минимальная долгота
				if(attr.getQName(i).equals("minlon")) {
					minLon = Double.valueOf(attr.getValue(i));
				} 
				
				// Максимальная широта
				if(attr.getQName(i).equals("maxlat")) {
					maxLat = Double.valueOf(attr.getValue(i));
				} 
				
				// Максимальная долгота
				if(attr.getQName(i).equals("maxlon")) {
					maxLon = Double.valueOf(attr.getValue(i));
					
					raf.setMapBounds(minLat, minLon, maxLat, maxLon);
				} 
			}
			
			// Точка
			if(qName.equals("node")) {
				if(!isNode) {
					isNode = true; // Мы в теге точки
				}
				
				// Идентификатор точки
				if(attr.getQName(i).equals("id")) {	
					try {
						node_id = Long.valueOf(attr.getValue(i));
						node_seek = raf.getLengthFile(); // Смещение в файле, с которого начинается тег node
												
						// Заполняем таблицу соответствия id и смещения в файле карты
						Param.seek_nodes.put(node_id, node_seek);
						
						// Ищем самый большой идентификатор у точек
						if(node_id > Param.maxNodeId) {
							Param.maxNodeId = node_id;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				// Точка удалена (на карте не отображается)
				if(attr.getQName(i).equals("action")) {
					if(attr.getValue(i).equals("delete")) {
						isNodeDelete = true;
						Param.seek_nodes.remove(node_id);
					}
				}
				
				// Широта
				if(attr.getQName(i).equals("lat")) {
					lat = Double.valueOf(attr.getValue(i));
				}
				
				// Долгота
				if(attr.getQName(i).equals("lon")) {
					lon = Double.valueOf(attr.getValue(i));
				}
			}
			
			// Тег tag в составе тега точки
		    if(qName.equals("tag") && isNode && !isNodeDelete) {
		    	if(!isTag)
		    		isTag = true;
			
		    	// Ключ
		    	if(attr.getQName(i).equals("k")) {
		    		// Дорога
				    if(attr.getValue(i).equals("highway")) {
			    		isHighway = true;
					
				    	k_position = rewriteTag("highway");
			    	}
				
			    	// Брод
			     	if(attr.getValue(i).equals("ford")) {
				    	isFord = true;
					
			    		k_position = rewriteTag("highway");
			    	}
				
			    	// Здание
			    	if(attr.getValue(i).equals("building")) {
						isBuilding = true;
						
						k_position = rewriteTag("building");
					}
					
					// Вход в здание
					if(attr.getValue(i).equals("entrance")) {
						isEntrance = true;
						
						k_position = rewriteTag("building");
					}
					
					// Офис
					if(attr.getValue(i).equals("office")) {
						isOffice = true;
						
						str_k.add(attr.getValue(i));
					}
					
					// Магазин
					if(attr.getValue(i).equals("shop")) {
						isShop = true;
						
						str_k.add(attr.getValue(i));
					}
					
					// Ремесленная мастерская
					if(attr.getValue(i).equals("craft")) {
						isCraft = true;
						
						str_k.add(attr.getValue(i));
					}
					
					// Населенный пункт
					if(attr.getValue(i).equals("place")) {
						isPlace = true;
						
						str_k.add(attr.getValue(i));
					}
					
					// Инфраструктура, благоустройство
					if(attr.getValue(i).equals("healthcare")) {
						isHealthcare = true;
						
						k_position = rewriteTag("amenity");
					}
					
					// Экстренные службы
					if(attr.getValue(i).equals("emergency")) {
						isEmergency = true;
						
						k_position = rewriteTag("emergency");
					}
					
					// Историческое место
					if(attr.getValue(i).equals("historic")) {
						isHistoric = true;
						
						str_k.add(attr.getValue(i));
					}
					
					// Преграда, гидрография проточных вод, ж/д дорога, воздушный транспорт,
					// канатная дорога, энергетика, землепользование, природные образования,
					// искусственные сооружения, места проведения досуга, инфраструктура,
					// туризм, военные объекты, спорт, мосты, тунели, описание точки
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
				
					// Описание точки на русском языке
					if(attr.getValue(i).equals("name:ru")) {
						isName = true;
						
						k_position = rewriteTag("name");
					}					
				}
				
				// Значение ключа
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
	
	// Содержание тега
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {}
	
	// Выход из тега
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		// Точка
		if(qName.equals("node")) {
			if(isNode) {
				if(!isNodeDelete) {
					checkNodesUsed(node_id);
	
					// Записываем в файл карты параметры точки
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
				
				isNode = false; // Мы не в теге точки
			}
			
			isNodeDelete = false;
		}
	}
	
	/**
	 * Заменяет элемент списка на новый.
	 * 
	 * @param tag - элемент списка, который необходимо заменить
	 * @param newTag - новый элемент списка
	 * @return позиция элемента в списке, который заменили
	 */
	
	private int rewriteTag(String tag, String newTag) {
		int position = -1; // Позиция элемента в списке, который заменили
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
	 * Заменяет элемент списка на новый. В данном случае старый элемент и новый элемент
	 * одинаковые.
	 * 
	 * @param tag - элемент списка, который необходимо заменить
	 * @return позиция элемента в списке, который заменили
	 */
	
	private int rewriteTag(String tag) {
		return rewriteTag(tag, tag);
	}
	
	/**
	 * Записываем свойства точки в файл карты map.hnvg.
	 * 
	 * @param tmp_str_k - коллекция ключей тега <tag>
	 * @param tmp_str_v - коллекция значений ключей тега <tag>
	 * @param objects - типы объектов на карте
	 */
	
	private void writeNodeProperties(ArrayList<String> tmp_str_k, ArrayList<String> tmp_str_v, Types objects) {
		// Записываем тип объекта карты
		boolean mainTag = false; // Определяем тег отвечающий за тип объекта на карте
		boolean addTag = false; // Дополнительный тип объекта карты (наличие)
		// Описание дополнительных основных тегов
		ArrayList<String> add_str_k = null; 
		ArrayList<String> add_str_v = null;	
		
		// Определяем тип объекта на карте среди основных тегов
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
						raf.setTypeOfObject(node_seek, Param.unknownType); // Тег есть, но не определен
						// Если есть тип объекта на карте, то точку повторно использовать не нужно
						createNodesUsed(node_id);
					} else {
						raf.setTypeOfObject(node_seek, objects.getTypeOfObject(tmp_str_k.get(i), tmp_str_v.get(i)));
						// Если есть тип объекта на карте, то точку повторно использовать не нужно
						createNodesUsed(node_id);
					}
				} else { // Есть еще осовные теги у линии
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
				
		// Определяем тип объекта на карте среди неосновных тегов
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
						raf.setTypeOfObject(node_seek, Param.unknownType); // Тег есть, но не определен
						// Если усть тип объекта на карте, то точку повторно использовать не нужно
						createNodesUsed(node_id);
					} else {
						raf.setTypeOfObject(node_seek, objects.getTypeOfObject(tmp_str_k.get(i), tmp_str_v.get(i)));
						// Если есть тип объекта на карте, то точку повторно использовать не нужно
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
		
		// Проверяем на наличие дополнительных свойств (bridge, tunnel).
		for(int i = 0; i < tmp_str_k.size(); i++) {					
			//Мост
			if(tmp_str_k.get(i).equals("bridge")) {
				raf.setPropertyType(node_seek, Param.property_bridge);
			}
			
			//Тунель
			if(tmp_str_k.get(i).equals("tunnel")) {
				raf.setPropertyType(node_seek, Param.property_tunnel);
			}
		}
		
		// Описание элемента
		for(int i = 0; i < tmp_str_k.size(); i++) {
			if(tmp_str_k.get(i).equals("name") && !tmp_str_v.get(i).equals("")) 
				raf.setName(node_seek, tmp_str_v.get(i));
		}
		
		// Есть еще основные теги у точки
		createNewObjects(add_str_k,	add_str_v);
							
		// Выводим в лог неопределенные типы объектов на карте
		checkTypeOfObject(mainTag, addTag);
	}
	
	/**
	 * При наличии нескольких основных тегов создает новые объекты карты.
	 * 
	 * @param tmp_str_k коллекция ключей тега <tag>
	 * @param tmp_str_v коллекция значений ключей тега <tag>
	 */
	
	private void createNewObjects(ArrayList<String> add_str_k, ArrayList<String> add_str_v) {
		if(add_str_k != null && add_str_k.size() != 0) {
			for(int i = 0; i < add_str_k.size(); i++) {
				long newSeek = raf.createNewNode(node_seek);
				
				if(newSeek >= 0) {
					Param.seek_nodes.put(Param.maxNodeId, newSeek);
					Param.seek_nodes_used.put(Param.maxNodeId, newSeek);
					
					// Перезаписываем тип бъекта
					if(objects.getTypeOfObject(add_str_k.get(i), add_str_v.get(i)) == (short) 0) {
						raf.setTypeOfObject(newSeek, Param.unknownType); // Тег есть, но не определен
					} else {
						raf.setTypeOfObject(newSeek, objects.getTypeOfObject(add_str_k.get(i), add_str_v.get(i)));
					}
				} else {
					OsmConverter.printLog("Не удалось создать новую точку.");
				}
			}
		}
	}
	
	/**
	 * Создает список, в котором храняться смещения точек уже использованных ранее.
	 * 
	 * @param node_id - идентификатор точеки
	 */
	
	private void createNodesUsed(long node_id) {
		Param.seek_nodes_used.put(node_id, Param.seek_nodes.get(node_id));
	}
	
	/**
	 * Проверяет использовались точки ранее или нет, и если использовались, то
	 * создает копию точки. 
	 * 
	 * @param node_id - идентификатор точки
	 */
	
	private void checkNodesUsed(long node_id) {
		long seek = -1;
		
		// Использовались точки ранее
		try {
			seek = Param.seek_nodes_used.get(node_id);
			
			if(seek >= 0) {
				// Если идентификатор уже использовался, то заменяем его на новый
				long newId = Param.maxNodeId++; // Новый идентификатор точки				
				raf.renameId(seek, newId);
				
				Param.seek_nodes.remove(node_id);
				Param.seek_nodes_used.remove(node_id);				
				Param.seek_nodes.put(Param.maxNodeId, seek);
				Param.seek_nodes_used.put(Param.maxNodeId, seek);
				
				Param.seek_nodes.put(node_id, node_seek);
				Param.seek_nodes_used.put(node_id, node_seek);
			}
		} catch(NullPointerException ex) {
			// Если точки нет в списке, идем дальше
		}
	}
	
	/**
	 * Проверяет есть ли описание объекта карты или нет, если нет, то выводит данные об этом.
	 * 
	 * @param main_tag - наличие типа объекта на карте
	 * @param add_tag - наличие дополнительного типа объекта карты 
	 */
	
	private void checkTypeOfObject(boolean main_tag, boolean add_tag) {
		for(int i = 0; i < str_k.size(); i++) {
			if(objects.getTypeOfObject(str_k.get(i), str_v.get(i)) == 0 && 
					                   !str_k.get(i).equals("name") &&
					                   !str_k.get(i).equals("bridge") &&
					                   !str_k.get(i).equals("tunnel") &&
					                   !str_k.get(i).equals("area")) {
				OsmConverter.printLog("В файле TypesOfObject.java отсутствует описание объекта карты:");
				OsmConverter.printLog("   ID: " + node_id);
				OsmConverter.printLog("   TYPE: " + str_k.get(i) + " = " + str_v.get(i));
				OsmConverter.printLog("   MAIN TYPE = " + main_tag + ",\n   ADD TYPE = " + add_tag + "\r\n");
			}
		}
	}
}
