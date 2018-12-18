/*
 * Copyright (c) 05.2016
 */

package mj82.Converter;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import OsmConverter.OsmConverter;

/**
 * Класс парсит xml файл osm карты, считывает данные об отношениях (relation) объектов на карте, преобразует 
 * их в мой формат и записывает в выходной файл.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class RelationSAXHandler extends DefaultHandler {

	private RndAccessFile raf; // Записывает и считывает данные в/из файла
	
	private Types objects;// Типы объектов на карте
	
	private ArrayList<Long> ways_ids; // Массив id линий в отношении
	private ArrayList<String> roles; // Массив свойств (role) линий в отношении
	
	private boolean mainTag = false; // Определяем тег отвечающий за тип отношения
	private boolean addTag = false; // Дополнительный тип	
	private boolean noMainTag = false; // Признак того, что основной тип объекта есть, но не определен
	private boolean noAddTag = false; // Признак того, что дополнительный тип объекта есть, но не определен
	private short typeOfObject = Param.noType; // Тип отношения
	private short addType = 0; // Дополнительный тип
	
	private ArrayList<Long> outerWaysId; // Идентификаторы линий с ролью outer
	// Идентификаторы линий с ролью outer, как отдельные полигоны (каждая линия - это отдельный полигон)
	private ArrayList<ArrayList<Long>> outerWaysArray; // Хранит внешние полигоны
	private ArrayList<Long> innerWaysId; // Идентификаторы линий с ролью inner
	// Идентификаторы линий с ролью inner, как отдельные полигоны (каждая линия - это отдельный полигон)
	private ArrayList<ArrayList<Long>> innerWaysArray; // Хранит внутренние полигоны
	
	// Какой тип полигонов необходимо сортировать
	private short outer = 0; // Внешние
	private short inner = 1; // Внутренние
	
	private boolean area; // Признак площадного объекта
	private boolean bridge; // Признак того, что объект является мостом
	private boolean tunnel; // Признак того, что объект является тунелем
	
	private String name; // Описание объекта на карте
	
	private int mainTagPosition = -1; // Позиция в массиве tmp_str_k основного типа объекта на карте
	
	// Типы отношения
	private boolean multipolygon = false; // Признак того, что линии входящие в отношение образуют полигон
	
	private long relation_id = -1; // Идентификатор отношения
	private boolean incomplete = false; // По умолчанию линии в мультиполигоне замыкаются, образуя полигон
	private boolean isModify = false; // Признак, что отношение в OSM карте модифицировалось и не может корректно отобразиться
		
	// Теги
	private boolean isRelation = false; // Отношение
	private boolean isMember = false; // Ссылки на линии
	
	// Атрибуты тегов
	private boolean isWay = false; // Тип объекта в отношении (линия)	
	private boolean isNoWay = false; // Тип объекта в отношении (не линия, а что-то другое, например, отношение)
	private boolean isHighway = false; // Дорога
	private boolean isAreaHighway = false; // Дорога как площадной объект
	private boolean isSurface = false; // Покрытие дороги
	private boolean isBuilding = false; // Здание
	private boolean isBuildingPart = false; // Часть здания
	private boolean isHistoric = false; // Историческое место
	private boolean isWaterway = false; // Проточные воды
	private boolean isAeroway = false; // Воздушный транспорт
	private boolean isAerialway = false; // Канатная дорога
	private boolean isLeisure = false; // Места проведения досуга
	private boolean isAmenity = false; // Инфраструктура, благоустройство
	private boolean isHealthcare = false; // Инфраструктура, благоустройство
	private boolean isEmergency = false; // Экстренные службы
	private boolean isBoundary = false; // Границы
	private boolean isFord = false; // Брод
	private boolean isName = false; // Описание
	private boolean isOffice = false; // Офис
	private boolean isShop = false; // Магазин
	private boolean isCraft = false; // Мастерская
	private boolean isTag = false; // Наличие тега <tag>
	private boolean isCycleway = false; // Велосипедная дорожка
	private boolean isIceRoad = false; // Зимняя дорога
	private boolean isWinterRoad = false; // Зимняя дорога
	private boolean isOther = false; // Все остальное, чего нет в выше описанных атрибутах
	
	// Для временного хранения данных
	private ArrayList<String> str_k; // Ключи тега tag
	private ArrayList<String> str_v; // Значения тега tag
	private int k_position = -1; // Позиция заменяемого в коллекции элемента
	
	public RelationSAXHandler() {
		raf = new RndAccessFile();
		
		str_k = new ArrayList<>();
		str_v = new ArrayList<>();
	}
	
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes attr) throws SAXException {
		// Атрибуты тега
		for(int i = 0; i < attr.getLength(); i++) {
			// Отношение
			if(qName.equals("relation")) {
				if(!isRelation) {
					isRelation = true; // Мы в теге линии
				}
			
				// Идентификатор отношения
				if(attr.getQName(i).equals("id")) {
					relation_id = Long.valueOf(attr.getValue(i));
				}
			
				// Отношение модифицированно
				if(attr.getQName(i).equals("action")) {
					if(attr.getValue(i).equals("modify")) {
						isModify = true;
					}
				}
			}
			
			// Тег member в составе тега отношения
			if(qName.equals("member") && isRelation && !isModify) {
				if(!isMember) {
					isMember = true;
					ways_ids = new ArrayList<Long>();
					roles = new ArrayList<String>();
				}
			
			    // Тип объекта в отношении
				if(attr.getQName(i).equals("type")) {
					if(attr.getValue(i).equals("way")) {
						isWay = true;
						isNoWay = false;
					} else {
						isNoWay = true;
					}
				}
			
				// Ссылка на линию, из которой состоит отношение
				if(attr.getQName(i).equals("ref") && isWay && !isNoWay)
					ways_ids.add(Long.valueOf(attr.getValue(i)));
			
				// Тип полигона (внутр. или внешн.)
				if(attr.getQName(i).equals("role") && isWay && !isNoWay)
					roles.add(attr.getValue(i));					
			}
		
			// Тег tag в составе тега отношения
			if(qName.equals("tag") && isRelation && isWay) {
				if(!isTag)
					isTag = true;
			
				// Ключ
				if(attr.getQName(i).equals("k")) {
					// Дорога (площадной объект)
					if(attr.getValue(i).equals("area:highway")) {
						isAreaHighway = true;
					
						str_k.add("area:highway");
					}
				
					// Дорога
					if(attr.getValue(i).equals("highway")) {
						isHighway = true;
					
						str_k.add(attr.getValue(i));
					}
				
					// Поверхность дорог
					if(attr.getValue(i).equals("surface")) {
						isSurface = true;
					
						str_k.add(attr.getValue(i));
					}
				
					// Здание
					if(attr.getValue(i).equals("building")) {
						isBuilding = true;
					
						k_position = rewriteTag("building");
					}
				
					// Часть здания
					if(attr.getValue(i).equals("building:part")) {
						isBuildingPart = true;
					
						str_k.add(attr.getValue(i));
					}
				
					// Здание с исторической ценностью
					if(attr.getValue(i).equals("historic:building")) {
						isBuilding = true;
					
						k_position = rewriteTag("building");
					}
				
					// Гидрография проточных вод
					if(attr.getValue(i).equals("waterway")) {
						isWaterway = true;
					
						str_k.add(attr.getValue(i));
					}
				
					// Воздушный транспорт
					if(attr.getValue(i).equals("aeroway")) {
						isAeroway = true;
					
						str_k.add(attr.getValue(i));
					}
				
					// Канатная дорога
					if(attr.getValue(i).equals("aerialway")) {
						isAerialway = true;
					
						str_k.add(attr.getValue(i));
					}
				
					// Места проведения досуга
					if(attr.getValue(i).equals("leisure")) {
						isLeisure = true;
					
						str_k.add(attr.getValue(i));
					}
				
					// Инфраструктура
					if(attr.getValue(i).equals("amenity")) {
						isAmenity = true;
					
						str_k.add(attr.getValue(i));
					}
						// Экстренные службы
					if(attr.getValue(i).equals("emergency")) {
						isEmergency = true;
					
						str_k.add(attr.getValue(i));
					}
				
					// Граница
					if(attr.getValue(i).equals("boundary")) {
						isBoundary = true;
					
						str_k.add(attr.getValue(i));
					}	
				
					// Брод
					if(attr.getValue(i).equals("ford")) {
						isFord = true;
					
						k_position = rewriteTag("highway");
					}
				
					// Велосипедная дорожка
					if(attr.getValue(i).equals("cycleway")) {
						isCycleway = true;
					
						k_position = rewriteTag("highway");
					}
				
					// Зимник
					if(attr.getValue(i).equals("ice_road")) {
						isIceRoad = true;
					
						k_position = rewriteTag("highway");
					}
				
					// Зимник
					if(attr.getValue(i).equals("winter_road")) {
						isWinterRoad = true;
					
						k_position = rewriteTag("highway");
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
				
					// Мастерская
					if(attr.getValue(i).equals("craft")) {
						isCraft = true;
					
						str_k.add(attr.getValue(i));
					}
				
					// Инфраструктура, благоустройство
					if(attr.getValue(i).equals("healthcare")) {
						isHealthcare = true;
					
						k_position = rewriteTag("amenity");
					}
				
					// Историческое место
					if(attr.getValue(i).equals("historic")) {
						isHistoric = true;
					
						str_k.add(attr.getValue(i));
					}
				
					// Преграда, ж/д дорога, энергетика, искусственные сооружения, туризм, 
					// землепользование, военные объекты, природные образования, спорт, 
					// покрытие проселочных дорог, площадные объекты, мосты, тунели, описание точки, 
					// тип отношения
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
				
					// Описание точки на русском языке
					if(attr.getValue(i).equals("name:ru")) {
						isName = true;
						
						k_position = rewriteTag("name");
					}					
				}
				
				// Значение ключа
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
	
	// Содержание тега
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {}
	
	// Выход из тега
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		// Отношение
		if(qName.equals("relation")) {
			if(isRelation && isMember && isWay && ways_ids != null && ways_ids.size() != 0) {
				if(!isModify) {
					if(isTag) {
						if(str_k.size() != 0 && ways_ids.size() != 0) {
							multipolygon = false; // Признак того, что линии входящие в отношение образуют полигон
//							System.out.println("REL_ID: " + relation_id);	
							if(relation_id == 4828432785l) {
								int y = 0;
							}
								
							// Определяем тип отношения
							for(int i = 0; i < str_k.size(); i++) {
								// Мультиполигон
								if(str_k.get(i).equals("type") && str_v.get(i).equals("multipolygon")) {
									multipolygon = true;
									
									// Определяем тип объекта на карте у отношения
									checkTypeOfRelation(str_k, str_v);
								}
							}
							
							// Тип отношения мультиполигон
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
	 * Проверяет по ссылкам существуют ли линии на самом деле, если некоторые линии отсутствуют
	 * значит мультиполигон неполный и не замкнутый.
	 * 
	 * @param ways_ids - идентификаторы линий
	 * @return
	 */
	
	private boolean checkWaysIds(ArrayList<Long> ways_ids) {
		boolean tmp_incomplete = false; // По умолчанию линии в мультиполигоне замыкаются, образуя полигон
		
		for(int i = 0; i < ways_ids.size(); i++) {
			try {
				long id = Param.my_ways_ids.get(ways_ids.get(i));
				
				@SuppressWarnings("unused")
				long seek = Param.seek_ways.get(id);
			} catch(NullPointerException ex) {
				if(!tmp_incomplete && roles.get(i).equals("outer"))
					tmp_incomplete = true; // Мультиполигон неполный
				
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
	 * Определяет тип объекта на карте для отношения.
	 * 
	 * @param tmp_str_k коллекция ключей тега <tag>
	 * @param tmp_str_v коллекция значений ключей тега <tag>
	 */
	
	private void checkTypeOfRelation(ArrayList<String> tmp_str_k, ArrayList<String> tmp_str_v) {
		noMainTag = false; // Признак того, что основной тип объекта есть, но не определен
		noAddTag = false; // Признак того, что дополнительный тип объекта есть, но не определен
		
		mainTag = false; // Определяем тег отвечающий за тип отношения
		addTag = false; // Дополнительный тип	
		
		mainTagPosition = -1; // Позиция в массиве tmp_str_k основного типа объекта на карте
		
		typeOfObject = Param.noType; // Тип отношения
		addType = Param.noType; // Дополнительный тип
		
		objects = new TypesOfObjects();
		
		// Определяем тип объекта отношения среди основных тегов
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
						noMainTag = true; // Тег есть, но не определен
					} else {							
						typeOfObject = objects.getTypeOfObject(tmp_str_k.get(i), tmp_str_v.get(i));
						mainTagPosition = i;
					}
				}
			}
		}
					
		// Определяем тип объекта отношения на карте среди неосновных тегов
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
						noMainTag = true; // Тег есть, но не определен
					} else {
						typeOfObject = objects.getTypeOfObject(tmp_str_k.get(i), tmp_str_v.get(i));
						mainTagPosition = i;
					}
				} else {
					if(!addTag)
						addTag = true;
								
					if(objects.getTypeOfObject(tmp_str_k.get(i), tmp_str_v.get(i)) == (short) 0) {
						noAddTag = true; // Тег есть, но не определен
					} else {
						addType = objects.getTypeOfObject(tmp_str_k.get(i), tmp_str_v.get(i));
					}
				}
			}
		}
	}
	
	/**
	 * Записываем свойства отношений в файл карты.
	 */
	
	private void writeRelationProperties() {
		area = false; // Признак площадного объекта
		bridge = false; // Признак того, что объект является мостом
		tunnel = false; // Признак того, что объект является тунелем
		
		outerWaysId = new ArrayList<>(); // Идентификаторы линий с ролью outer
		// Идентификаторы линий с ролью outer, как отдельные полигоны (каждая линия - это отдельный полигон)
		outerWaysArray = new ArrayList<>(); // Хранит внешние полигоны
		innerWaysId = new ArrayList<>(); // Идентификаторы линий с ролью inner
		// Идентификаторы линий с ролью inner, как отдельные полигоны (каждая линия - это отдельный полигон)
		innerWaysArray = new ArrayList<>(); // Хранит внутренние полигоны
		
		name = ""; // Описание объекта на карте
				
		// Описание элемента
		for(int i = 0; i < str_k.size(); i++) {
			if(str_k.get(i).equals("name") && !str_v.get(i).equals("")) 
				name = str_v.get(i);
		}
			
		// Проверяем на наличие дополнительных свойств (area, bridge, tunnel).
		for(int i = 0; i < str_k.size(); i++) {
			// Площадной объект
			if(str_k.get(i).equals("area") && str_v.get(i).equals("yes")) 
				area = true;
			
			// Площадной объект
			if(str_k.get(i).equals("area:highway") && str_v.get(i).equals("area")) 
				area = true;
				
			//Мост
			if(str_k.get(i).equals("bridge")) 
				bridge = true;
				
			//Тунель
			if(str_k.get(i).equals("tunnel"))
				tunnel = true;
		}
			
		// Выводим в лог неопределенные типы объектов на карте 
		checkTypeOfObject(mainTag, addTag);
			
		// Сортируем полигоны на внешние и внутренние
		for(int i = 0; i < ways_ids.size(); i++) {
			long id = Param.my_ways_ids.get(ways_ids.get(i));
			
			// Список линий относящихся к внешнему полигону в отношении
			if( roles.get(i).equals("outer"))
				outerWaysId.add(id);
			
			// Список внутренних полигонов в отношении
			if( roles.get(i).equals("inner")) 
				innerWaysId.add(id);
		}
		
		// Проверяем внешние полигоны на наличие типа объекта на карте и сохраняем его
		if(typeOfObject == Param.noType)
			typeOfObject = checkTypeOfObjectInOuter(outerWaysId);
		
		// Если нет типа объекта на карте, то дальше не продолжаем
		if(typeOfObject != Param.noType) {
			// Сортируем внутренние полигоны на отдельные полигоны
			sortPolygons(inner);				
			
			// Сортируем внешние полигоны на отдельные полигоны
			sortPolygons(outer);
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
					                   !str_k.get(i).equals("area") &&
					                   !str_k.get(i).equals("type")) {
				OsmConverter.printLog("В файле TypesOfObject.java отсутствует описание объекта карты:");
				OsmConverter.printLog("   ID: " + relation_id);
				OsmConverter.printLog("   TYPE: " + str_k.get(i) + " = " + str_v.get(i));
				OsmConverter.printLog("   MAIN TYPE = " + main_tag + ",\n   ADD TYPE = " + add_tag + "\r\n");
			}
		}
	}
	
	/**
	 * Проверяет используются линии в других отношениях или нет, и если использовались, то
	 * создает копию линии.
	 * 
	 * @param ways_ids идентификаторы линий
	 */
	
	private void checkWaysUsed(ArrayList<Long> ways_ids) {
		long seek = -1;
//		Param.outer_ways_used_in_relations.clear();
		
		// Используются в других линиях
		for(int i = 0; i < ways_ids.size(); i++) {
			try {
				long id = Param.my_ways_ids.get(ways_ids.get(i));
				
				seek = Param.seek_ways_used.get(id);
				
				if(seek >= 0) {
					long newNodeId = Param.maxNodeId;
					newNodeId++;
					if(newNodeId == 4828245885l) {
						long idd = ways_ids.get(i);
						int y = 0;
					}
					long newSeek = raf.createNewWay(raf.getNodeIdsFromWay(id));
					
					if(newSeek >= 0) {
						ways_ids.set(i, newNodeId);
						Param.seek_ways_used.put(newNodeId, newSeek);
						Param.outer_ways_used_in_relations.put(newNodeId, id);
					} else
						OsmConverter.printLog("Не удалось создать новую линию.");
				}
					
			} catch(NullPointerException ex) {
				continue;
			}
		}
	}
	
	/**
	 * Создает список, в котором храняться смещения первых точек линий уже использованных 
	 * ранее в отношениях.
	 * 
	 * @param way_ids идентификаторы линий
	 */
	
	private void createWaysUsed(ArrayList<Long> way_ids) {
		for(int i = 0; i < way_ids.size(); i++) {
			long id = Param.my_ways_ids.get(way_ids.get(i));
			
			Param.seek_ways_used.put(id, Param.seek_ways.get(id));
//			Param.seek_ways_used_in_relations.put(id, Param.seek_ways.get(id));
		}
	}
	
	/**
	 * Сохраняет новую линию, состоящую из нескольких линий.
	 * Ссылки в массиве всех линий на другие линии удаляет (кроме ссылки на первую линию).
	 * 
	 * @param ways_ids идентификаторы линий
	 * @return идентификатор новой линии
	 */
	
	private long saveNewWay(ArrayList<Long> ways_ids) {
		if(ways_ids.size() > 1) {
			// Удаляем дубликаты, кроме первой и последней точек, если линий много
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
			
			// Еще раз проверяем на дубликаты точек
			raf.deleteDublicatNodes(ways_ids.get(0));
		}
		
		return ways_ids.get(0);
	}
	
	/**
	 * Проверяет список на наличие тега building.
	 * 
	 * @return true, если список содержит тег building, иначе - false
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
	 * Записывет некоторые свойства полигонов.
	 * 
	 * @param way_id идентификатор линии (полигона)
	 * @param typeOfObject тип объекта на карте
	 * @param addType дополнительный тип объекта на карте
	 * @param area признак площадного объекта
	 * @param isClosed признак того, что внешеий полигон замкнут сам на себя (полный, площадной)
	 * @param bridge признак того, что объект является мостом
	 * @param tunnel признак того, что объект является тунелем
	 * @param noMainTag признак того, что тип объекта на карте есть, но он не определен
	 * @param noAddTag признак того, что дополнительный тип объекта на карте есть, но он не определен
	 * @param isOuter признак того, что полигон внешний или внутренний
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
		// Если внешний полигон
		if(isOuter) {
			// Очищаем поле атрибута элемента
    		raf.clearAttrSeek(Param.seek_ways.get(way_id));
    		
    		// Записываем тип объекта на карте и доп тип во внешний полигон
    		// Внутренний полигон не трогаем. У него свой тип объекта и доп объекта
		
    		if(typeOfObject > 0) { // присваиваем заданный тип отношения
    			raf.setTypeOfObject(Param.seek_ways.get(way_id), typeOfObject);    			
//    			raf.setAttrSeek(Param.seek_ways.get(way_id), Param.seek_ways.get(way_id), attrSeek);
    		} else if(noMainTag) { // присваиваем неизвестный тип отношения
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
				
    		// Площадной объект
    		if(area) 
    			raf.setType(Param.poligon_outer, Param.seek_ways.get(way_id));
		
	    	//Мост
    		if(bridge) 
	    		raf.setPropertyType(Param.seek_ways.get(way_id), Param.property_bridge);
		
    		//Тунель
    		if(tunnel)
    			raf.setPropertyType(Param.seek_ways.get(way_id), Param.property_tunnel);
    		
    		// Если полигон неполный, то присваиваем тип элемента карты - линия
    		if(!isClosed) {
    			raf.setType(Param.line, Param.seek_ways.get(way_id));
    		}
    	} else {  
    		// Очищаем поле атрибута элемента
    		raf.clearAttrSeek(Param.seek_ways.get(way_id));
    		
    		// Если внутренний полигон имеет тип объекта карты
 /*   		if(raf.getTypeOfObject(Param.seek_ways.get(way_id)) > 0) {
    			// удаляем этот тип и делаем внутренний полигон без типа объекта на карте
    			raf.setTypeOfObject(Param.seek_ways.get(way_id), Param.noType);
    		}*/
    	} 
	}
	
	/**
	 * Сортируем внешние и внутренние полигоны на отдельные полигоны.
	 * 
	 * @param typeOfPolygon тип полигона для сортировки
	 */
	
	private void sortPolygons(short typeOfPolygon) {
		int firstOuterWay = 0; // Индекс линии, с которой начинается сравнение общих точек
		ArrayList<Long> tmpWaysIds = new ArrayList<>(); // Идентификаторы линий с ролью outer
		boolean firstElement = false; // Первый элемент в общем полигоне
		
		if(typeOfPolygon == outer) {
			for(int i = 0; i < outerWaysId.size(); i++) {
				// Список линий относящихся к внешнему полигону в отношении
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
			
			// Еще раз проверяем отдельные внешние полигоны на общие точки
			if(outerWaysArray.size() > 1) {
				outerWaysArray = raf.checkNodesIdInOuterWays(outerWaysArray);
			}	
			
			// Модифицируем список соответствия идентификаторов линий внешних полигонов 
			// и копий этих же линий
//			modifyOuterWaysUsedInRelations();
			
			// Упорядочиваем внешние полигоны
			raf.sortWays(outerWaysArray);
			
			// Проверяем неполные полигоны на принадлежность к одному объекту карты 
			// и, если это так, строим новый полигон (полный) 
			if(incomplete && !objects.getTypeOfObject(typeOfObject).equals("boundary")
					      && !objects.getTypeOfObject(typeOfObject).equals("barrier")
					      && !objects.getTypeOfObject(typeOfObject).equals("building:part")) {
				outerWaysArray = raf.checkNodesIdInIncompleteOuterWays(outerWaysArray);
			}
			
			// Заполняем данными внешние полигоны
			for(int k = 0; k < outerWaysArray.size(); k++) {
				tmpWaysIds = outerWaysArray.get(k);
				byte type = Param.poligon_outer;
				firstElement = false;
				// Признак того, что полигон замкнут (полный)
				boolean isClosed = raf.checkFirstAndLastNodesInWays(tmpWaysIds);
				
				// Мультиполигон полный (замкнутый)
				if(isClosed) {						
					for(int i = 0; i < tmpWaysIds.size(); i++) {
						// Внешний полигон
						if(tmpWaysIds.size() != 0) {							
							// Границы и барьеры не являются площадными объектами
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
					
					// Удаляем дубликаты точек в полигоне
					raf.deleteDublicatNodes(tmpWaysIds);					
				} else { // Мультиполигон неполный (не замкнутый)
					for(int i = 0; i < tmpWaysIds.size(); i++) {
						// Внешний полигон
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
				
					// Удаляем дубликаты точек в полигоне
					raf.deleteDublicatNodes(tmpWaysIds);
				}
				
				// Удаляем атрибуты всех линий кроме первой
				for(int i = 1; i < tmpWaysIds.size(); i++) {
					long attrSeek = raf.getArrtSeek(Param.seek_ways.get(tmpWaysIds.get(i)));
					int index = Param.delete_attrs.indexOf(attrSeek);
					
					if(index == -1)
						Param.delete_attrs.add(attrSeek);
						
				}
				
				// Записываем св-ва полигона
				setPropertiesInPolygons(tmpWaysIds.get(0),
		                                typeOfObject, 
		                                addType, isClosed,
		                                area, bridge, tunnel,
		                                noMainTag, noAddTag, true);				
								
				// Перезаписываем описание внешнего полигона
				if(!name.equals("") && tmpWaysIds.size() != 0)
					raf.setName(Param.seek_nodes.get(tmpWaysIds.get(0)), name);
				
				// Покрытие дорог
				if(mainTagPosition >= 0) {
					if(str_k.get(mainTagPosition).equals("highway") || str_k.get(mainTagPosition).equals("area:highway")) {
						// Проверяем на наличие покрытия дороги
						for(int i = 0; i < str_k.size(); i++) {
							if(str_k.get(i).equals("surface")) {
								raf.setAdditionalTypeOfObject(Param.seek_ways.get(tmpWaysIds.get(0)), 
	   								                          objects.getTypeOfObject(str_k.get(i), 
										                        		              str_v.get(i)));								
								// Покрытие
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
					// Проверяем на наличие покрытия дороги и отсутствия типа объекта на карте
					for(int i = 0; i < str_k.size(); i++) {
						if(str_k.get(i).equals("surface")) {
							raf.setTypeOfObject(Param.seek_ways.get(tmpWaysIds.get(0)), (short) 37); // Тип дороги surface
							raf.setAdditionalTypeOfObject(Param.seek_ways.get(tmpWaysIds.get(0)), 
   								                          objects.getTypeOfObject(str_k.get(i), 
									                        		              str_v.get(i)));								
							// Покрытие
							if(str_v.get(i).equals("decoturf") && isClosed) 
								raf.setType(Param.poligon_outer, Param.seek_ways.get(tmpWaysIds.get(0)));
						}
						
						if(str_k.get(i).equals("tracktype")) {
							raf.setTypeOfObject(Param.seek_ways.get(tmpWaysIds.get(0)), (short) 37); // Тип дороги surface
							raf.setAdditionalTypeOfObject(Param.seek_ways.get(tmpWaysIds.get(0)), 
									                      objects.getTypeOfObject(str_k.get(i), 
									                        		              str_v.get(i)));
						}
					}
				}
					
				// Из нескольких линий в отношении была создана одна новая лини.
				// Сохраним ее в массиве всех линий.
				saveNewWay(tmpWaysIds);
				
				// Присваиваем внешниму полигону внутренние
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
				// Список линий относящихся к внутреннему полигону в отношении
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
			
			// Очищаем список внутренних полигонов, чтобы записать туда новые
			innerWaysId.clear();
					
			// Упорядочиваем внутренние полигоны
			raf.sortWays(innerWaysArray);
						
			// Заполняем данными внутренние полигоны
			for(int k = 0; k < innerWaysArray.size(); k++) {
				tmpWaysIds = innerWaysArray.get(k);
				firstElement = false;
				
				// Проверяем внутренние полигоны на замкнутость (на полноту), если полигон не полный (не замыкается),
				// то удаляем его из списка внутренних полигонов	
				if(!raf.checkFirstAndLastNodesInWays(tmpWaysIds)) {
					// Удаляем тип объекта на карте у не замкнутого (не полного) полигона 
					for(int i = 0; i < tmpWaysIds.size(); i++) {
						raf.setTypeOfObject(Param.seek_ways.get(tmpWaysIds.get(i)), Param.noType);
					}
					
					tmpWaysIds.clear();					
					continue;
				}
				
				for(int i = 0; i < tmpWaysIds.size(); i++) {
					// Внутренний полигон
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
				
				// Удаляем дубликаты точек в полигоне
				raf.deleteDublicatNodes(tmpWaysIds);
					
				// Удаляем атрибуты всех линий кроме первой
				for(int i = 1; i < tmpWaysIds.size(); i++) {
					long attrSeek = raf.getArrtSeek(Param.seek_ways.get(tmpWaysIds.get(i)));
					int index = Param.delete_attrs.indexOf(attrSeek);
					
					if(index == -1)
						Param.delete_attrs.add(attrSeek);
						
				}
				
				// Записываем св-ва полигона
				setPropertiesInPolygons(tmpWaysIds.get(0),
						                typeOfObject, 
						                addType, false,
						                area, bridge, tunnel,
			                            noMainTag, noAddTag, false);
				// Из нескольких линий в отношении была создана одна новая лини.
				// Сохраним ее в массиве всех линий.
				innerWaysId.add(saveNewWay(tmpWaysIds));// Внутренние полигоны после сортировок
				
				tmpWaysIds.clear();
			}
		}
	}
	
	/**
	 * Определяет наличие типа объекта на карте у отсортированных внешних
	 * пролигонов.
	 * 
	 * @param outerWaysId идентификаторы линий, принадлежащих внешним полигонам
	 * @return ипа объекта на карте
	 */
	private short checkTypeOfObjectInOuter(ArrayList<Long> outerWaysId) {
		short typeOfObject = Param.noType;
		
		for(int i = 0; i < outerWaysId.size(); i++) {
			typeOfObject = raf.getTypeOfObject(Param.seek_ways.get(outerWaysId.get(i)));
			
			// Первый попавшийся тип объекта на карте
			if(typeOfObject > Param.noType) {
				Param.ids_outer_ways_used_in_relation_for_delete.add(outerWaysId.get(i));
				break;
			}
		}
		
		return typeOfObject;
	}
}
