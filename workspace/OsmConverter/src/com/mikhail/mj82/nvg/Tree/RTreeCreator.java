/*
 * Copyright (c) 02.2017
 */

package com.mikhail.mj82.nvg.Tree;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import com.mikhail.mj82.nvg.Converter.Param;
import com.mikhail.mj82.nvg.Converter.RndAccessFile;
import com.mikhail.mj82.nvg.Converter.Types;
import com.mikhail.mj82.nvg.Converter.TypesOfObjects;
import com.mikhail.mj82.nvg.Geom.JRect;

import OsmConverter.OsmConverter;

/**
 * Класс строит поисковое дерево и сохраняет его в файл.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class RTreeCreator {
	
	private FileOutputStream file_out;
	private ObjectOutputStream object_out;	
	
	private RandomAccessFile h_raf; // Записывает и считывает данные в/из файла map.hnvg
	private RandomAccessFile raf; // Записывает и считывает данные в/из файла map.dnvg
	private RandomAccessFile i_raf; // Записывает и считывает данные в/из файла mapIndex
	private RndAccessFile my_raf; // Записывает и считывает данные в/из файла
	
	private RTrees trees = new RTrees(); // Хранит в себе все возможные деревья поиска

	private Types objects; // Типы объектов на карте	
	private GPSObject gps_obj; // Объект карты для помещения в дерево поиска
	private JRect[] bounds; // Ограничивающие прямоугольники созданные из составных частей объектов карты
	private long [] seeks; // Смещения первых точек составных частей элемента карты, которые будут записаны в лист дерева
	
	private int highway_count = 0; // Дорога
	private int barrier_count = 0; // Барьеры
	private int building_count = 0; // Здания
	private int building_part_count = 0; // Части здания
	private int waterway_count = 0; // Гидрография проточных вод
	private int railway_count = 0; // Рельсовые пути
	private int aeroway_count = 0; // Воздушный транспорт
	private int aerialway_count = 0; // Канатная дорога
	private int emergency_count = 0; // Экстренные службы
	private int natural_count = 0; // Природные образования
	private int power_count = 0; // Энергетика
	private int landuse_count = 0; // Землепользование, назачение территории
	private int man_made_count = 0; // Искусственные сооружения
	private int leisure_count = 0; // Места проведения досуга
	private int amenity_count = 0; // Инфраструктура, благоустройство
	private int office_count = 0; // Офисы
	private int shop_count = 0; // Магазины, услуги
	private int craft_count = 0; // Мастерские
	private int sport_count = 0; // Спорт
	private int tourism_count = 0; // Туризм
	private int historic_count = 0; // Исторические места
	private int military_count = 0; // Военные объекты
	private int place_count = 0; // Населенный пункт
	private int boundary_count = 0; // Граница
	
	/**
	 * Создает поисковые деревья считывая данные из файла карты.
	 * 
	 * @param flag очищает память
	 */
	
	public void makeRTree(boolean flag) {

		// Обнуляем все неиспользуемые переменные и запускаем сборщик мусора Java
		if(flag) {
			Param.seekChanged = true; // Все точки переиндексированы
			Param.makeFromFile = true; // Строим дерево по файлу
			Param.clearOldVariables();
			System.gc();
		}
		
		// Записываем название поискового дерева
        trees.setRTreeName(Param.mapName);
		
		try {
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			my_raf = new RndAccessFile();
			
			long seek = Param.dnvg_headerSize;
				
			while(!(seek >= raf.length())) {				
				raf.seek(seek);
				byte type = raf.readByte();
				
				if(type == Param.point) {					
					raf.seek(seek + Param.attr_seek);
					long attrSeek = raf.readLong();
					
					h_raf.seek(attrSeek + Param.typeOfObject_seek);
					short typeOfObject = h_raf.readShort(); // Код типа объекта
					
					objects = new TypesOfObjects();
					String str_typeOfObject = objects.getTypeOfObject(typeOfObject); // Текстовое описание типа объекта
					
					if(!str_typeOfObject.equals("")) {
						addObjectInTree(seek, str_typeOfObject);
					}
					
					seek += Param.elementSize;
				} else {
					raf.seek(seek + Param.myself_seek);
					long myself = raf.readLong();
						
					raf.seek(seek + Param.attr_seek);
					long attrSeek = raf.readLong();
					
					h_raf.seek(attrSeek + Param.firsPointInWay_seek);
					long first = h_raf.readLong();
						
					if(myself == first) {						
						h_raf.seek(attrSeek + Param.typeOfObject_seek);
						short typeOfObject = h_raf.readShort(); // Код типа объекта
						
						objects = new TypesOfObjects();
						String str_typeOfObject = objects.getTypeOfObject(typeOfObject); // Текстовое описание типа объекта
						
						if(!str_typeOfObject.equals("")) {
							addObjectInTree(seek, str_typeOfObject);
						}
						
						seek += Param.elementSize;
					} else {
						seek += Param.elementSize;
					}
				}				
			}
			
			getTreeInfo();
//			writeCrc32();
//			saveRTreeData();
			saveRTree();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(h_raf != null)
					h_raf.close();
				
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Создает поисковые деревья используя готовые списки точек и линий.
	 */
	
	public void makeRTree() {
		Param.makeFromFile = false;
		
		// Записываем название поискового дерева
        trees.setRTreeName(Param.mapName);
		
		try {
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			my_raf = new RndAccessFile();
			
			// Распределяем по поисковым деревья точечные объекты карты
			Collection<Long> nodes_array = Param.new_seek_only_nodes.values();
			Iterator<Long> nodes_iterator = nodes_array.iterator();
			
			long tmp_seek = 0;	
			String str_typeOfObject = "";
			
			while(nodes_iterator.hasNext()) {				
				tmp_seek = nodes_iterator.next();
				
				raf.seek(tmp_seek + Param.attr_seek);
				long attrSeek = raf.readLong();
				
				h_raf.seek(attrSeek + Param.typeOfObject_seek);
				short typeOfObject = h_raf.readShort(); // Код типа объекта
				
				objects = new TypesOfObjects();
				str_typeOfObject = objects.getTypeOfObject(typeOfObject); // Текстовое описание типа объекта
				
				if(!str_typeOfObject.equals("")) {
					addObjectInTree(tmp_seek, str_typeOfObject);
//					System.out.println("SEEK: " + tmp_seek);
				}
			}
			
			// Распределяем по поисковым деревья линейные и площадные объекты карты
			Collection<Long> ways_array = Param.new_seek_only_ways.values();
			Iterator<Long> ways_iterator = ways_array.iterator();
						
			while(ways_iterator.hasNext()) {				
				tmp_seek = ways_iterator.next();
				
				raf.seek(tmp_seek + Param.attr_seek);
				long attrSeek = raf.readLong();
				
				h_raf.seek(attrSeek + Param.typeOfObject_seek);
				short typeOfObject = h_raf.readShort(); // Код типа объекта
				
				objects = new TypesOfObjects();
				str_typeOfObject = objects.getTypeOfObject(typeOfObject); // Текстовое описание типа объекта
				
				if(!str_typeOfObject.equals("")) {
					addObjectInTree(tmp_seek, str_typeOfObject);
//					System.out.println("SEEK: " + tmp_seek);
				}
			}
			
			getTreeInfo();
//			writeCrc32();
//			saveRTreeData();
			saveRTree();
			// Ось Y вниз, т.е. top ниже bottom
			findObjs(new JRect(74.4858149, 65.8490876, 74.4867851, 65.8508124));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(h_raf != null)
					h_raf.close();
				
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Выводит сведения о заполнености поисковых деревьев. 
	 */
	
	private void getTreeInfo() {
		StringBuilder text = new StringBuilder();
		
		text.append("Highway_tree (objs / size): " + highway_count + " / " + trees.getHighwayTree().getGPSObjectsSize() + ".\t");
		text.append("Barrier_tree (objs / size): " + barrier_count + " / " + trees.getBarrierTree().getGPSObjectsSize() + ".\r\n");
		
		text.append("Building_tree (objs / size): " + building_count + " / " + trees.getBuildingTree().getGPSObjectsSize() + ".\t");
		text.append("Building_part_tree (objs / size): " + building_part_count + " / " + trees.getBuildingPartTree().getGPSObjectsSize() + ".\r\n");
		
		text.append("Waterway_tree (objs / size): " + waterway_count + " / " + trees.getWaterwayTree().getGPSObjectsSize() + ".\t");
		text.append("Railway_tree (objs / size): " + railway_count + " / " + trees.getRailwayTree().getGPSObjectsSize() + ".\r\n");
		
		text.append("Aeroway_tree (objs / size): " + aeroway_count + " / " + trees.getAerowayTree().getGPSObjectsSize() + ".\t");
		text.append("Aerialway_tree (objs / size): " + aerialway_count + " / " + trees.getAerialwayTree().getGPSObjectsSize() + ".\r\n");
		
		text.append("Emergency_tree (objs / size): " + emergency_count + " / " + trees.getEmergencyTree().getGPSObjectsSize() + ".\t");
		text.append("Natural_tree (objs / size): " + natural_count + " / " + trees.getNaturalTree().getGPSObjectsSize() + ".\r\n");
		
		text.append("Power_tree (objs / size): " + power_count + " / " + trees.getPowerTree().getGPSObjectsSize() + ".\t");
		text.append("Landuse_tree (objs / size): " + landuse_count + " / " + trees.getLanduseTree().getGPSObjectsSize() + ".\r\n");
		
		text.append("Man_made_tree (objs / size): " + man_made_count + " / " + trees.getManMadeTree().getGPSObjectsSize() + ".\t");
		text.append("Leisure_tree (objs / size): " + leisure_count + " / " + trees.getLeisureTree().getGPSObjectsSize() + ".\r\n");
		
		text.append("Amenity_tree (objs / size): " + amenity_count + " / " + trees.getAmenityTree().getGPSObjectsSize() + ".\t");
		text.append("Office_tree (objs / size): " + office_count + " / " + trees.getOfficeTree().getGPSObjectsSize() + ".\r\n");
		
		text.append("Shop_tree (objs / size): " + shop_count + " / " + trees.getShopTree().getGPSObjectsSize() + ".\t");
		text.append("Craft_tree (objs / size): " + craft_count + " / " + trees.getCraftTree().getGPSObjectsSize() + ".\r\n");
		
		text.append("Tourism_tree (objs / size): " + tourism_count + " / " + trees.getTourismTree().getGPSObjectsSize() + ".\t");
		text.append("Historic_tree (objs / size): " + historic_count + " / " + trees.getHistoricTree().getGPSObjectsSize() + ".\r\n");
		
		text.append("Military_tree (objs / size): " + military_count + " / " + trees.getMilitaryTree().getGPSObjectsSize() + ".\t");
		text.append("Place_tree (objs / size): " + place_count + " / " + trees.getPlaceTree().getGPSObjectsSize() + ".\r\n");
		
		text.append("Sport_tree (objs / size): " + sport_count + " / " + trees.getSportTree().getGPSObjectsSize() + ".\t");
		text.append("Boundary_tree (objs / size): " + boundary_count+ " / " + trees.getBoundaryTree().getGPSObjectsSize() + ".");
		
		OsmConverter.printLog(text.toString());
	}
	
	/**
	 * Добавляет объекты карты в поисковые деревья.
	 * 
	 * @param seek смещение объекта карты в файле карты
	 * @param str_typeOfObject тип объекта на карте
	 */
	
	private void addObjectInTree(long seek, String str_typeOfObject) {
		switch(str_typeOfObject) {
		case "area:highway":
			if (Param.makeFromFile) {
				seeks = my_raf.getSeeksForTreeFromFile(seek);
				bounds = my_raf.getBoundsFromFile(seek);
			} else {
				bounds = my_raf.getBounds(seek);	
				seeks = my_raf.getSeeksForTree(seek);
			}
			
			highway_count++;
			
			if (seeks.length == bounds.length) {
				for(int i = 0; i < bounds.length; i++) {
					gps_obj = new GPSObject(seeks[i], bounds[i]);
					
					trees.getHighwayTree().insertObject(gps_obj);
				}			
			}			
			break;
		case "highway":
			if (Param.makeFromFile) {
				seeks = my_raf.getSeeksForTreeFromFile(seek);
				bounds = my_raf.getBoundsFromFile(seek);
			} else {
				bounds = my_raf.getBounds(seek);
				seeks = my_raf.getSeeksForTree(seek);
			}
			
			highway_count++;
			
			if (seeks.length == bounds.length) {
				for(int i = 0; i < bounds.length; i++) {
					gps_obj = new GPSObject(seeks[i], bounds[i]);
					
					trees.getHighwayTree().insertObject(gps_obj);
				}	
			}					
			break;
		case "barrier":	
			if (Param.makeFromFile) {
				seeks = my_raf.getSeeksForTreeFromFile(seek);
				bounds = my_raf.getBoundsFromFile(seek);
			} else {
				bounds = my_raf.getBounds(seek);
				seeks = my_raf.getSeeksForTree(seek);
			}
			
			barrier_count++;
			
			if (seeks.length == bounds.length) {
				for(int i = 0; i < bounds.length; i++) {
					gps_obj = new GPSObject(seeks[i], bounds[i]);
					
					trees.getBarrierTree().insertObject(gps_obj);
				}
			}						
			break;
	    case "building": 	    	
	    	if (Param.makeFromFile) {
				seeks = my_raf.getSeeksForTreeFromFile(seek);
				bounds = my_raf.getBoundsFromFile(seek);
			} else {
				bounds = my_raf.getBounds(seek);
				seeks = my_raf.getSeeksForTree(seek);
			}
			
			building_count++;
			
			if (seeks.length == bounds.length) {
				for(int i = 0; i < bounds.length; i++) {
					gps_obj = new GPSObject(seeks[i], bounds[i]);					
					trees.getBuildingTree().insertObject(gps_obj);
				}			
			}
			break;
        case "building_part":
        	if (Param.makeFromFile) {
				seeks = my_raf.getSeeksForTreeFromFile(seek);
				bounds = my_raf.getBoundsFromFile(seek);
			} else {
				bounds = my_raf.getBounds(seek);
				seeks = my_raf.getSeeksForTree(seek);
			}
			
			building_part_count++;
			
			if (seeks.length == bounds.length) {
				for(int i = 0; i < bounds.length; i++) {
					gps_obj = new GPSObject(seeks[i], bounds[i]);
					
					trees.getBuildingPartTree().insertObject(gps_obj);
				}
			}						
			break;
        case "waterway":
        	if (Param.makeFromFile) {
				seeks = my_raf.getSeeksForTreeFromFile(seek);
				bounds = my_raf.getBoundsFromFile(seek);
			} else {
				bounds = my_raf.getBounds(seek);
				seeks = my_raf.getSeeksForTree(seek);
			}
			
			waterway_count++;
			
			if (seeks.length == bounds.length) {
				for(int i = 0; i < bounds.length; i++) {
					gps_obj = new GPSObject(seeks[i], bounds[i]);
					
					trees.getWaterwayTree().insertObject(gps_obj);
				}
			}						
			break;
        case "railway":	
        	if (Param.makeFromFile) {
				seeks = my_raf.getSeeksForTreeFromFile(seek);
				bounds = my_raf.getBoundsFromFile(seek);
			} else {
				bounds = my_raf.getBounds(seek);
				seeks = my_raf.getSeeksForTree(seek);
			}
			
			railway_count++;
			
			if (seeks.length == bounds.length) {
				for(int i = 0; i < bounds.length; i++) {
					gps_obj = new GPSObject(seeks[i], bounds[i]);
					
					trees.getRailwayTree().insertObject(gps_obj);
				}
			}						
			break;
        case "aeroway":	
        	if (Param.makeFromFile) {
				seeks = my_raf.getSeeksForTreeFromFile(seek);
				bounds = my_raf.getBoundsFromFile(seek);
			} else {
				bounds = my_raf.getBounds(seek);
				seeks = my_raf.getSeeksForTree(seek);
			}
			
			aeroway_count++;
			
			if (seeks.length == bounds.length) {
				for(int i = 0; i < bounds.length; i++) {
					gps_obj = new GPSObject(seeks[i], bounds[i]);
					
					trees.getAerowayTree().insertObject(gps_obj);
				}
			}						
			break;
        case "aerialway":
        	if (Param.makeFromFile) {
				seeks = my_raf.getSeeksForTreeFromFile(seek);
				bounds = my_raf.getBoundsFromFile(seek);
			} else {
				bounds = my_raf.getBounds(seek);
				seeks = my_raf.getSeeksForTree(seek);
			}
			
			aerialway_count++;
			
			if (seeks.length == bounds.length) {
				for(int i = 0; i < bounds.length; i++) {
					gps_obj = new GPSObject(seeks[i], bounds[i]);
					
					trees.getAerialwayTree().insertObject(gps_obj);
				}	
			}					
			break;
        case "emergency":
        	if (Param.makeFromFile) {
				seeks = my_raf.getSeeksForTreeFromFile(seek);
				bounds = my_raf.getBoundsFromFile(seek);
			} else {
				bounds = my_raf.getBounds(seek);
				seeks = my_raf.getSeeksForTree(seek);
			}
			
			emergency_count++;
			
			if (seeks.length == bounds.length) {
				for(int i = 0; i < bounds.length; i++) {
					gps_obj = new GPSObject(seeks[i], bounds[i]);
					
					trees.getEmergencyTree().insertObject(gps_obj);
				}
			}						
			break;
        case "natural":
        	if (Param.makeFromFile) {
				seeks = my_raf.getSeeksForTreeFromFile(seek);
				bounds = my_raf.getBoundsFromFile(seek);
			} else {
				bounds = my_raf.getBounds(seek);
				seeks = my_raf.getSeeksForTree(seek);
			}
			
			natural_count++;
			
			if (seeks.length == bounds.length) {
				for(int i = 0; i < bounds.length; i++) {
					gps_obj = new GPSObject(seeks[i], bounds[i]);
					
					trees.getNaturalTree().insertObject(gps_obj);
				}
			}						
			break;
        case "power":
        	if (Param.makeFromFile) {
				seeks = my_raf.getSeeksForTreeFromFile(seek);
				bounds = my_raf.getBoundsFromFile(seek);
			} else {
				bounds = my_raf.getBounds(seek);
				seeks = my_raf.getSeeksForTree(seek);
			}
			
			power_count++;
			
			if (seeks.length == bounds.length) {
				for(int i = 0; i < bounds.length; i++) {
					gps_obj = new GPSObject(seeks[i], bounds[i]);
					
					trees.getPowerTree().insertObject(gps_obj);
				}
			}						
			break;
        case "landuse":
        	if (Param.makeFromFile) {
				seeks = my_raf.getSeeksForTreeFromFile(seek);
				bounds = my_raf.getBoundsFromFile(seek);
			} else {
				bounds = my_raf.getBounds(seek);
				seeks = my_raf.getSeeksForTree(seek);
			}
			
			landuse_count++;
			
			if (seeks.length == bounds.length) {
				for(int i = 0; i < bounds.length; i++) {
					gps_obj = new GPSObject(seeks[i], bounds[i]);
					
					trees.getLanduseTree().insertObject(gps_obj);
				}
			}						
			break;
        case "man_made":
        	if (Param.makeFromFile) {
				seeks = my_raf.getSeeksForTreeFromFile(seek);
				bounds = my_raf.getBoundsFromFile(seek);
			} else {
				bounds = my_raf.getBounds(seek);
				seeks = my_raf.getSeeksForTree(seek);
			}
			
			man_made_count++;
			
			if (seeks.length == bounds.length) {
				for(int i = 0; i < bounds.length; i++) {
					gps_obj = new GPSObject(seeks[i], bounds[i]);
					
					trees.getManMadeTree().insertObject(gps_obj);
				}
			}						
			break;
        case "leisure":
        	if (Param.makeFromFile) {
				seeks = my_raf.getSeeksForTreeFromFile(seek);
				bounds = my_raf.getBoundsFromFile(seek);
			} else {
				bounds = my_raf.getBounds(seek);
				seeks = my_raf.getSeeksForTree(seek);
			}
			
			leisure_count++;
			
			if (seeks.length == bounds.length) {
				for(int i = 0; i < bounds.length; i++) {
					gps_obj = new GPSObject(seeks[i], bounds[i]);
					
					trees.getLeisureTree().insertObject(gps_obj);
				}
			}						
			break;
        case "amenity":
        	if (Param.makeFromFile) {
				seeks = my_raf.getSeeksForTreeFromFile(seek);
				bounds = my_raf.getBoundsFromFile(seek);
			} else {
				bounds = my_raf.getBounds(seek);
				seeks = my_raf.getSeeksForTree(seek);
			}
			
			amenity_count++;
			
			if (seeks.length == bounds.length) {
				for(int i = 0; i < bounds.length; i++) {
					gps_obj = new GPSObject(seeks[i], bounds[i]);
					
					trees.getAmenityTree().insertObject(gps_obj);
				}
			}						
			break;
        case "office":
        	if (Param.makeFromFile) {
				seeks = my_raf.getSeeksForTreeFromFile(seek);
				bounds = my_raf.getBoundsFromFile(seek);
			} else {
				bounds = my_raf.getBounds(seek);
				seeks = my_raf.getSeeksForTree(seek);
			}
			
			office_count++;
			
			if (seeks.length == bounds.length) {
				for(int i = 0; i < bounds.length; i++) {
					gps_obj = new GPSObject(seeks[i], bounds[i]);
					
					trees.getOfficeTree().insertObject(gps_obj);
				}
			}						
			break;
        case "shop":
        	if (Param.makeFromFile) {
				seeks = my_raf.getSeeksForTreeFromFile(seek);
				bounds = my_raf.getBoundsFromFile(seek);
			} else {
				bounds = my_raf.getBounds(seek);
				seeks = my_raf.getSeeksForTree(seek);
			}
			
			shop_count++;
			
			if (seeks.length == bounds.length) {
				for(int i = 0; i < bounds.length; i++) {
					gps_obj = new GPSObject(seeks[i], bounds[i]);
					
					trees.getShopTree().insertObject(gps_obj);
				}
			}						
			break;
        case "craft":
        	if (Param.makeFromFile) {
				seeks = my_raf.getSeeksForTreeFromFile(seek);
				bounds = my_raf.getBoundsFromFile(seek);
			} else {
				bounds = my_raf.getBounds(seek);
				seeks = my_raf.getSeeksForTree(seek);
			}
			
			craft_count++;
			
			if (seeks.length == bounds.length) {
				for(int i = 0; i < bounds.length; i++) {
					gps_obj = new GPSObject(seeks[i], bounds[i]);
					
					trees.getCraftTree().insertObject(gps_obj);
				}
			}						
			break;
        case "tourism":
        	if (Param.makeFromFile) {
				seeks = my_raf.getSeeksForTreeFromFile(seek);
				bounds = my_raf.getBoundsFromFile(seek);
			} else {
				bounds = my_raf.getBounds(seek);
				seeks = my_raf.getSeeksForTree(seek);
			}
			
			tourism_count++;
			
			if (seeks.length == bounds.length) {
				for(int i = 0; i < bounds.length; i++) {
					gps_obj = new GPSObject(seeks[i], bounds[i]);
					
					trees.getTourismTree().insertObject(gps_obj);
				}
			}						
			break;
        case "historic":
        	if (Param.makeFromFile) {
				seeks = my_raf.getSeeksForTreeFromFile(seek);
				bounds = my_raf.getBoundsFromFile(seek);
			} else {
				bounds = my_raf.getBounds(seek);
				seeks = my_raf.getSeeksForTree(seek);
			}
			
			historic_count++;
			
			if (seeks.length == bounds.length) {
				for(int i = 0; i < bounds.length; i++) {
					gps_obj = new GPSObject(seeks[i], bounds[i]);
					
					trees.getHistoricTree().insertObject(gps_obj);
				}
			}						
			break;
        case "military":
        	if (Param.makeFromFile) {
				seeks = my_raf.getSeeksForTreeFromFile(seek);
				bounds = my_raf.getBoundsFromFile(seek);
			} else {
				bounds = my_raf.getBounds(seek);
				seeks = my_raf.getSeeksForTree(seek);
			}
			
			military_count++;
			
			if (seeks.length == bounds.length) {
				for(int i = 0; i < bounds.length; i++) {
					gps_obj = new GPSObject(seeks[i], bounds[i]);
					
					trees.getMilitaryTree().insertObject(gps_obj);
				}
			}						
			break;
        case "place":
        	if (Param.makeFromFile) {
				seeks = my_raf.getSeeksForTreeFromFile(seek);
				bounds = my_raf.getBoundsFromFile(seek);
			} else {
				bounds = my_raf.getBounds(seek);
				seeks = my_raf.getSeeksForTree(seek);
			}
			
			place_count++;
			
			if (seeks.length == bounds.length) {
				for(int i = 0; i < bounds.length; i++) {
					gps_obj = new GPSObject(seeks[i], bounds[i]);
					
					trees.getPlaceTree().insertObject(gps_obj);
				}
			}						
			break;
        case "sport":
        	if (Param.makeFromFile) {
				seeks = my_raf.getSeeksForTreeFromFile(seek);
				bounds = my_raf.getBoundsFromFile(seek);
			} else {
				bounds = my_raf.getBounds(seek);
				seeks = my_raf.getSeeksForTree(seek);
			}
			
			sport_count++;
			
			if (seeks.length == bounds.length) {
				for(int i = 0; i < bounds.length; i++) {
					gps_obj = new GPSObject(seeks[i], bounds[i]);
					
					trees.getSportTree().insertObject(gps_obj);
				}
			}						
			break;
        case "boundary":
        	if (Param.makeFromFile) {
				seeks = my_raf.getSeeksForTreeFromFile(seek);
				bounds = my_raf.getBoundsFromFile(seek);
			} else {
				bounds = my_raf.getBounds(seek);
				seeks = my_raf.getSeeksForTree(seek);
			}
			
			boundary_count++;
			
			if (seeks.length == bounds.length) {
				for(int i = 0; i < bounds.length; i++) {
					gps_obj = new GPSObject(seeks[i], bounds[i]);
					
					trees.getBoundaryTree().insertObject(gps_obj);
				}
			}						
			break;
		default:
			System.out.println("НЕВОЗМОЖНО ВНЕСТИ ОБЪЕТ В ПОИСКОВОЕ ДЕРЕВО !!!");
		}
	}
	
	/**
	 * Записывает в поисковое дерево значение ранее вычислиной контрольной
	 * суммы карты.
	 */
	private void writeCrc32() {
		trees.setCRC(my_raf.getCRC32fromFile());
	}

	/**
	 * Сохраняеn дерево поиска в файл.
	 */
	
	private void saveRTree() {		
		Date startNode = new Date();
		long timeStart = startNode.getTime();
		
		OsmConverter.printLog("Сохраняем дерево поиска ...");
		
		try {
			Param.dirIndexes.mkdirs();
			file_out = new FileOutputStream(Param.tree_path);
			object_out = new ObjectOutputStream(file_out);
			
			object_out.writeObject(trees);
			
			object_out.flush();
			object_out.close();
			file_out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Date endNode = new Date();
		long timeEnd = endNode.getTime();
		
		OsmConverter.printLog("Время сохранения дерева поиска: " + (timeEnd - timeStart) + " ms");	
	}
	
	public void createRTreeFromIndexFile(String path) {}
	
	/**
	 * Сохраняет данные поисковых деревьев в файл.
	 */
	@SuppressWarnings("unused")
	private void saveRTreeData() {
		Date startNode = new Date();
		long timeStart = startNode.getTime();
		
		OsmConverter.printLog("Сохраняем дерево поиска в виде данных ...");
		
		try {
			i_raf = new RandomAccessFile(Param.tree_path, "rw");
			
			// Записываем объекты поисковых деревьев в файл
			setRTNodesInFile(trees.getHighwayTree(), i_raf);
			setRTNodesInFile(trees.getBarrierTree(), i_raf);
			setRTNodesInFile(trees.getBuildingTree(), i_raf);
			setRTNodesInFile(trees.getBuildingPartTree(), i_raf);
			setRTNodesInFile(trees.getWaterwayTree(), i_raf);
			setRTNodesInFile(trees.getRailwayTree(), i_raf);
			setRTNodesInFile(trees.getAerowayTree(), i_raf);
			setRTNodesInFile(trees.getAerialwayTree(), i_raf);
			setRTNodesInFile(trees.getEmergencyTree(), i_raf);
			setRTNodesInFile(trees.getNaturalTree(), i_raf);
			setRTNodesInFile(trees.getPowerTree(), i_raf);
			setRTNodesInFile(trees.getLanduseTree(), i_raf);
			setRTNodesInFile(trees.getManMadeTree(), i_raf);
			setRTNodesInFile(trees.getLeisureTree(), i_raf);
			setRTNodesInFile(trees.getAmenityTree(), i_raf);
			setRTNodesInFile(trees.getOfficeTree(), i_raf);
			setRTNodesInFile(trees.getShopTree(), i_raf);
			setRTNodesInFile(trees.getCraftTree(), i_raf);
			setRTNodesInFile(trees.getSportTree(), i_raf);
			setRTNodesInFile(trees.getTourismTree(), i_raf);
			setRTNodesInFile(trees.getHistoricTree(), i_raf);
			setRTNodesInFile(trees.getMilitaryTree(), i_raf);
			setRTNodesInFile(trees.getPlaceTree(), i_raf);
			setRTNodesInFile(trees.getBoundaryTree(), i_raf);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(i_raf != null)
					i_raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		Date endNode = new Date();
		long timeEnd = endNode.getTime();
		
		OsmConverter.printLog("Время сохранения дерева поиска в виде данных: " + (timeEnd - timeStart) + " ms");	
	}
	
	/**
	 * Записывает в файл данные поискового дерева.
	 * 
	 * @param tree поисковое дерево
	 * @param raf - объект для доступа к файлу
	 */
	private void setRTNodesInFile(RTree tree, RandomAccessFile raf) {
		RTNode [] nodes = tree.getNodes(); // Все узлы дерева
		
		for (int i = 0; i < nodes.length; i++) {
			// Все объекты одного узла дерева
			GPSObject [] objects = nodes[i].getObjects();
			
			if (objects.length > 0) {
				for (int j = 0; j < objects.length; j++)
					my_raf.setGpsObjectInIndexFile(objects[j].getSeek(), 
							objects[j].getMbr(), raf);
			}
		}
	}
	
	private void findObjs(JRect rect) {
		ArrayList<GPSObject> mapObjects = new ArrayList<>();

        RTree sBuildingTree = trees.getBuildingTree();
        sBuildingTree.findObjectsInArea(rect, mapObjects);
        
        System.out.println("Buildings: " + mapObjects.size());
	}
}
