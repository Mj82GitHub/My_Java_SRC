/*
 * Copyright (c) 02.2017
 */

package com.mikhail.mj82.Tree;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import com.mikhail.mj82.Converter.Param;
import com.mikhail.mj82.Converter.RndAccessFile;
import com.mikhail.mj82.Converter.Types;
import com.mikhail.mj82.Converter.TypesOfObjects;
import com.mikhail.mj82.Geom.JRect;

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
	private RndAccessFile my_raf; // Записывает и считывает данные в/из файла
	
	private RTrees trees = new RTrees(); // Хранит в себе все возможные деревья поиска
	private Types objects; // Типы объектов на карте	
	private GPSObject gps_obj; // Объект карты для помещения в дерево поиска
	private JRect[] bounds; // Ограничивающие прямоугольники созданные из составных частей объектов карты
	
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
				}
			}
			
			getTreeInfo();
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
			gps_obj = new GPSObject();
			
			if (Param.makeFromFile)
				bounds = my_raf.getBoundsFromFile(seek);
			else
				bounds = my_raf.getBounds(seek);
			
			highway_count++;
			
			for(int i = 0; i < bounds.length; i++) {
				gps_obj.setSeek(seek);
				gps_obj.setMbr(bounds[i]);
				
				trees.getHighwayTree().insertObject(gps_obj);
			}			
			break;
		case "highway":
			gps_obj = new GPSObject();

			if (Param.makeFromFile)
				bounds = my_raf.getBoundsFromFile(seek);
			else
				bounds = my_raf.getBounds(seek);
			
			highway_count++;
			
			for(int i = 0; i < bounds.length; i++) {
				gps_obj.setSeek(seek);
				gps_obj.setMbr(bounds[i]);
				
				trees.getHighwayTree().insertObject(gps_obj);
			}			
			break;
		case "barrier":	
			gps_obj = new GPSObject();

			if (Param.makeFromFile)
				bounds = my_raf.getBoundsFromFile(seek);
			else
				bounds = my_raf.getBounds(seek);
			
			barrier_count++;
			
			for(int i = 0; i < bounds.length; i++) {
				gps_obj.setSeek(seek);
				gps_obj.setMbr(bounds[i]);
				
				trees.getBarrierTree().insertObject(gps_obj);
			}			
			break;
	    case "building": 
	    	gps_obj = new GPSObject();

			if (Param.makeFromFile)
				bounds = my_raf.getBoundsFromFile(seek);
			else
				bounds = my_raf.getBounds(seek);
			
			building_count++;
			
			for(int i = 0; i < bounds.length; i++) {
				gps_obj.setSeek(seek);
				gps_obj.setMbr(bounds[i]);
				
				trees.getBuildingTree().insertObject(gps_obj);
			}			
			break;
        case "building_part":
        	gps_obj = new GPSObject();

			if (Param.makeFromFile)
				bounds = my_raf.getBoundsFromFile(seek);
			else
				bounds = my_raf.getBounds(seek);
			
			building_part_count++;
			
			for(int i = 0; i < bounds.length; i++) {
				gps_obj.setSeek(seek);
				gps_obj.setMbr(bounds[i]);
				
				trees.getBuildingPartTree().insertObject(gps_obj);
			}			
			break;
        case "waterway":
        	gps_obj = new GPSObject();

			if (Param.makeFromFile)
				bounds = my_raf.getBoundsFromFile(seek);
			else
				bounds = my_raf.getBounds(seek);
			
			waterway_count++;
			
			for(int i = 0; i < bounds.length; i++) {
				gps_obj.setSeek(seek);
				gps_obj.setMbr(bounds[i]);
				
				trees.getWaterwayTree().insertObject(gps_obj);
			}			
			break;
        case "railway":	
        	gps_obj = new GPSObject();

			if (Param.makeFromFile)
				bounds = my_raf.getBoundsFromFile(seek);
			else
				bounds = my_raf.getBounds(seek);
			
			railway_count++;
			
			for(int i = 0; i < bounds.length; i++) {
				gps_obj.setSeek(seek);
				gps_obj.setMbr(bounds[i]);
				
				trees.getRailwayTree().insertObject(gps_obj);
			}			
			break;
        case "aeroway":	
        	gps_obj = new GPSObject();

			if (Param.makeFromFile)
				bounds = my_raf.getBoundsFromFile(seek);
			else
				bounds = my_raf.getBounds(seek);
			
			aeroway_count++;
			
			for(int i = 0; i < bounds.length; i++) {
				gps_obj.setSeek(seek);
				gps_obj.setMbr(bounds[i]);
				
				trees.getAerowayTree().insertObject(gps_obj);
			}			
			break;
        case "aerialway":
        	gps_obj = new GPSObject();

			if (Param.makeFromFile)
				bounds = my_raf.getBoundsFromFile(seek);
			else
				bounds = my_raf.getBounds(seek);
			
			aerialway_count++;
			
			for(int i = 0; i < bounds.length; i++) {
				gps_obj.setSeek(seek);
				gps_obj.setMbr(bounds[i]);
				
				trees.getAerialwayTree().insertObject(gps_obj);
			}			
			break;
        case "emergency":
        	gps_obj = new GPSObject();

			if (Param.makeFromFile)
				bounds = my_raf.getBoundsFromFile(seek);
			else
				bounds = my_raf.getBounds(seek);
			
			emergency_count++;
			
			for(int i = 0; i < bounds.length; i++) {
				gps_obj.setSeek(seek);
				gps_obj.setMbr(bounds[i]);
				
				trees.getEmergencyTree().insertObject(gps_obj);
			}			
			break;
        case "natural":
        	gps_obj = new GPSObject();

			if (Param.makeFromFile)
				bounds = my_raf.getBoundsFromFile(seek);
			else
				bounds = my_raf.getBounds(seek);
			
			natural_count++;
			
			for(int i = 0; i < bounds.length; i++) {
				gps_obj.setSeek(seek);
				gps_obj.setMbr(bounds[i]);
				
				trees.getNaturalTree().insertObject(gps_obj);
			}			
			break;
        case "power":
        	gps_obj = new GPSObject();

			if (Param.makeFromFile)
				bounds = my_raf.getBoundsFromFile(seek);
			else
				bounds = my_raf.getBounds(seek);
			
			power_count++;
			
			for(int i = 0; i < bounds.length; i++) {
				gps_obj.setSeek(seek);
				gps_obj.setMbr(bounds[i]);
				
				trees.getPowerTree().insertObject(gps_obj);
			}			
			break;
        case "landuse":
        	gps_obj = new GPSObject();

			if (Param.makeFromFile)
				bounds = my_raf.getBoundsFromFile(seek);
			else
				bounds = my_raf.getBounds(seek);
			
			landuse_count++;
			
			for(int i = 0; i < bounds.length; i++) {
				gps_obj.setSeek(seek);
				gps_obj.setMbr(bounds[i]);
				
				trees.getLanduseTree().insertObject(gps_obj);
			}			
			break;
        case "man_made":
        	gps_obj = new GPSObject();

			if (Param.makeFromFile)
				bounds = my_raf.getBoundsFromFile(seek);
			else
				bounds = my_raf.getBounds(seek);
			
			man_made_count++;
			
			for(int i = 0; i < bounds.length; i++) {
				gps_obj.setSeek(seek);
				gps_obj.setMbr(bounds[i]);
				
				trees.getManMadeTree().insertObject(gps_obj);
			}			
			break;
        case "leisure":
        	gps_obj = new GPSObject();

			if (Param.makeFromFile)
				bounds = my_raf.getBoundsFromFile(seek);
			else
				bounds = my_raf.getBounds(seek);
			
			leisure_count++;
			
			for(int i = 0; i < bounds.length; i++) {
				gps_obj.setSeek(seek);
				gps_obj.setMbr(bounds[i]);
				
				trees.getLeisureTree().insertObject(gps_obj);
			}			
			break;
        case "amenity":
        	gps_obj = new GPSObject();

			if (Param.makeFromFile)
				bounds = my_raf.getBoundsFromFile(seek);
			else
				bounds = my_raf.getBounds(seek);
			
			amenity_count++;
			
			for(int i = 0; i < bounds.length; i++) {
				gps_obj.setSeek(seek);
				gps_obj.setMbr(bounds[i]);
				
				trees.getAmenityTree().insertObject(gps_obj);
			}			
			break;
        case "office":
        	gps_obj = new GPSObject();

			if (Param.makeFromFile)
				bounds = my_raf.getBoundsFromFile(seek);
			else
				bounds = my_raf.getBounds(seek);
			
			office_count++;
			
			for(int i = 0; i < bounds.length; i++) {
				gps_obj.setSeek(seek);
				gps_obj.setMbr(bounds[i]);
				
				trees.getOfficeTree().insertObject(gps_obj);
			}			
			break;
        case "shop":
        	gps_obj = new GPSObject();

			if (Param.makeFromFile)
				bounds = my_raf.getBoundsFromFile(seek);
			else
				bounds = my_raf.getBounds(seek);
			
			shop_count++;
			
			for(int i = 0; i < bounds.length; i++) {
				gps_obj.setSeek(seek);
				gps_obj.setMbr(bounds[i]);
				
				trees.getShopTree().insertObject(gps_obj);
			}			
			break;
        case "craft":
        	gps_obj = new GPSObject();

			if (Param.makeFromFile)
				bounds = my_raf.getBoundsFromFile(seek);
			else
				bounds = my_raf.getBounds(seek);
			
			craft_count++;
			
			for(int i = 0; i < bounds.length; i++) {
				gps_obj.setSeek(seek);
				gps_obj.setMbr(bounds[i]);
				
				trees.getCraftTree().insertObject(gps_obj);
			}			
			break;
        case "tourism":
        	gps_obj = new GPSObject();

			if (Param.makeFromFile)
				bounds = my_raf.getBoundsFromFile(seek);
			else
				bounds = my_raf.getBounds(seek);
			
			tourism_count++;
			
			for(int i = 0; i < bounds.length; i++) {
				gps_obj.setSeek(seek);
				gps_obj.setMbr(bounds[i]);
				
				trees.getTourismTree().insertObject(gps_obj);
			}			
			break;
        case "historic":
        	gps_obj = new GPSObject();

			if (Param.makeFromFile)
				bounds = my_raf.getBoundsFromFile(seek);
			else
				bounds = my_raf.getBounds(seek);
			
			historic_count++;
			
			for(int i = 0; i < bounds.length; i++) {
				gps_obj.setSeek(seek);
				gps_obj.setMbr(bounds[i]);
				
				trees.getHistoricTree().insertObject(gps_obj);
			}			
			break;
        case "military":
        	gps_obj = new GPSObject();

			if (Param.makeFromFile)
				bounds = my_raf.getBoundsFromFile(seek);
			else
				bounds = my_raf.getBounds(seek);
			
			military_count++;
			
			for(int i = 0; i < bounds.length; i++) {
				gps_obj.setSeek(seek);
				gps_obj.setMbr(bounds[i]);
				
				trees.getMilitaryTree().insertObject(gps_obj);
			}			
			break;
        case "place":
        	gps_obj = new GPSObject();

			if (Param.makeFromFile)
				bounds = my_raf.getBoundsFromFile(seek);
			else
				bounds = my_raf.getBounds(seek);
			
			place_count++;
			
			for(int i = 0; i < bounds.length; i++) {
				gps_obj.setSeek(seek);
				gps_obj.setMbr(bounds[i]);
				
				trees.getPlaceTree().insertObject(gps_obj);
			}			
			break;
        case "sport":
        	gps_obj = new GPSObject();

			if (Param.makeFromFile)
				bounds = my_raf.getBoundsFromFile(seek);
			else
				bounds = my_raf.getBounds(seek);
			
			sport_count++;
			
			for(int i = 0; i < bounds.length; i++) {
				gps_obj.setSeek(seek);
				gps_obj.setMbr(bounds[i]);
				
				trees.getSportTree().insertObject(gps_obj);
			}			
			break;
        case "boundary":
        	gps_obj = new GPSObject();

			if (Param.makeFromFile)
				bounds = my_raf.getBoundsFromFile(seek);
			else
				bounds = my_raf.getBounds(seek);
			
			boundary_count++;
			
			for(int i = 0; i < bounds.length; i++) {
				gps_obj.setSeek(seek);
				gps_obj.setMbr(bounds[i]);
				
				trees.getBoundaryTree().insertObject(gps_obj);
			}			
			break;
		default:
			System.out.println("НЕВОЗМОЖНО ВНЕСТИ ОБЪЕТ В ПОИСКОВОЕ ДЕРЕВО !!!");
		}
	}

	/**
	 * Сохраняеn дерево поиска в файл.
	 */
	
	private void saveRTree() {		
		Date startNode = new Date();
		long timeStart = startNode.getTime();
		
		OsmConverter.printLog("Сохраняем дерево поиска ...");
		
		try {
//			Param.dirIndexes.mkdirs();
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
}
