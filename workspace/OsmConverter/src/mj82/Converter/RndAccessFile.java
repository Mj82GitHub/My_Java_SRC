/*
 * Copyright (c) 12.2016
 */

package mj82.Converter;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.zip.CRC32;

import mj82.Geom.JPolygon;
import mj82.Geom.JRect;
import OsmConverter.OsmConverter;

/**
 * Класс записывает и считывает данные в/из файл(а).
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class RndAccessFile {
	
	private RandomAccessFile raf; // Объект доступа к фалу map.dnvg
	private RandomAccessFile h_raf; // Объект доступа к фалу map.hnvg
	private RandomAccessFile i_raf; // Объект доступа к фалу index_map
	
	/**
	 * Создает новые пустые файлы карты (map.hnvg и map.dnvg) и 
	 * записывает в них форматы (типы) файлов карты. 
	 */
	
	public void createNewFiles() {
		try {
			Param.dirMap.mkdirs();
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			h_raf.setLength(0);
			
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			raf.setLength(0);
			
			Param.dirIndexes.mkdirs();
			i_raf = new RandomAccessFile(Param.tree_path, "rw");
			i_raf.setLength(0);
			
			setFormat();
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
	 * Записываем в начало файлов карты (map.hnvg и map.dnvg) форматы 
	 * (собственные типы) файлов карты и нулевые значения контрольной
	 * суммы файла карты map.dnvg.
	 */
	
	private void setFormat() {
		try {			
			// Записываем форматы карт	
			// В map.hnvg файле
			h_raf.seek(Param.start_seek);
			h_raf.write(Param.hnvg_format);
			
			// В map.dnvg файле
			raf.seek(Param.start_seek);
			raf.write(Param.dnvg_format);
			
			// Бронируем место для контрольной суммы
			// В map.hnvg файле
			h_raf.seek(Param.crc32_seek);
			h_raf.writeLong(0);
			
			// В map.dnvg файле
			raf.seek(Param.crc32_seek);
			raf.writeLong(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Записываем в файл map.dnvg границы карты.
	 * 
	 * @param minLat минимальное значение широты
	 * @param minLon минимальное значение долготы
	 * @param maxLat максимальное значение широты
	 * @param maxLon максимальное значение долготы
	 */
	
	public void setMapBounds(double minLat, double minLon, double maxLat, double maxLon) {
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			// Минимальное значение широты
			long seek = Param.minLat_seek;			
			raf.seek(seek);
			raf.writeDouble(minLat);
			
			// Минимальное значение долготы
			seek = Param.minLon_seek;			
			raf.seek(seek);
			raf.writeDouble(minLon);
			
			// Максимальное значение широты 
			seek = Param.maxLat_seek;			
			raf.seek(seek);
			raf.writeDouble(maxLat);
			
			// Максимальное значение долготы
			seek = Param.maxLon_seek;			
			raf.seek(seek);
			raf.writeDouble(maxLon);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Присваивает внешним полигонам ссылки на внутренние, если они их содержат.
	 * 
	 * @param outerWayId идентификатор внешнего полигона
	 * @param innerWaysId массив идентификаторов внутренних полигонов
	 * @param method метод поиска внутренних полигонов
	 */
	
	public void assignedOuterWaysInnerWays(long outerWayId, ArrayList<Long> innerWaysId, int method) {
		// Внешний полигон		
		ArrayList<Long> nodes_ids_outer_way = new ArrayList<>(); // Идентификаторы точек внешнего полигона
		nodes_ids_outer_way.addAll(getNodeIdsFromWay(outerWayId));
		if(outerWayId == 4776275561l) {
			int y = 0;
		}
		// Долгота (ось X) точек внешнего полигона
		double [] lon_x_outer = new double[nodes_ids_outer_way.size()]; // Долгота (ось X) точек внешнего полигона
		lon_x_outer = Arrays.copyOf(getLongitude(nodes_ids_outer_way), nodes_ids_outer_way.size());
		
		// Широта (ось Y) точек внешнего полигона
		double [] lat_y_outer = new double[nodes_ids_outer_way.size()]; // Широта (ось Y) точек внешнего полигона
		lat_y_outer = Arrays.copyOf(getLatitude(nodes_ids_outer_way), nodes_ids_outer_way.size());
		
		// Создаем геометрическую фигуру полигона из точек внешнего полигона
		JPolygon outerPolygon = new JPolygon(lon_x_outer, lat_y_outer, nodes_ids_outer_way.size());
		
		// Внутренний полигон
		
		// Смещения внутренних полигонов, принадлежащих заданному внешнему полигону
		ArrayList<Long> ids_inner_ways = new ArrayList<>(); 
		ArrayList<Long> nodes_ids_inner_way = new ArrayList<>(); // Идентификаторы точек внутреннего полигона	
		
		for(int i = 0; i < innerWaysId.size(); i++) {
			boolean contains = false; // Признак того, что внутренний полигон принадлежит внешнему			
			nodes_ids_inner_way.addAll(getNodeIdsFromWay(innerWaysId.get(i)));
			if(innerWaysId.get(i) == 4776278500l) {
				int y = 0;
			}
			// Долгота (ось X) точек внутреннего полигона
			double [] lon_x_inner = new double[nodes_ids_inner_way.size()];
			lon_x_inner = Arrays.copyOf(getLongitude(nodes_ids_inner_way), nodes_ids_inner_way.size());
			
			// Широта (ось Y) точек внутреннего полигона
			double [] lat_y_inner = new double[nodes_ids_inner_way.size()];
			lat_y_inner = Arrays.copyOf(getLatitude(nodes_ids_inner_way), nodes_ids_inner_way.size());
			
			// Создаем геометрическую фигуру полигона из точек внутреннего полигона
			JPolygon innerPolygon = new JPolygon(lon_x_inner, lat_y_inner, nodes_ids_inner_way.size());
			
			// Проверяем принадлежность внутреннего полигона к внешнему
			if(method == Param.ray) {
				contains = isContains(outerPolygon, innerPolygon);
			} else if(method == Param.bound) {
				contains = outerPolygon.contains(innerPolygon);
			}			
			
			if(contains) {
				ids_inner_ways.add(innerWaysId.get(i));
				setType(Param.poligon_inner_composition, Param.seek_ways.get(innerWaysId.get(i)));
			}
			
			nodes_ids_inner_way.clear();
		}
		
		// Сохраняем в карте соответствие списка внутренних полигонов заданному внешнему полигону
		if(ids_inner_ways.size() > 0) {
				Param.ids_outer_ways_with_inner_ways.put(outerWayId, ids_inner_ways);
		}
		
		// Если внешний полигон имеет внутренний(е) полигоны, но эти внутренние полигоны
		// не находятся внутри внешнего, то удаляем эти внутренние полигоны
/*		if(ids_inner_ways.size() == 0 && innerWaysId.size() != 0 && getType(outerWayId) == Param.poligon_incomplete_outer) {
			for(int i = 0; i < innerWaysId.size(); i++) {
				deleteWayFromList(innerWaysId.get(i));
			}
		}*/
	}
	
	/**
	 * Возвращает массив ограничивающих прямоугольников, построенный по составным частям объекта карты.
	 * 
	 * @param seek смещение элемента в файле карты
	 * @return массив ограничивающих прямоугольников
	 */
	
	public JRect [] getBounds(long seek) {
		// Массив ограничивающих прямоугольников, построенный по составным частям объекта карты
		JRect[] bounds = null; 
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			ArrayList<Long> nodes_ids = new ArrayList<>(); // Идентификаторы точек объекта карты
			
			// Проверяем точечный это объект, линейный или площадной
			raf.seek(seek);
			byte type = raf.readByte();
			
			raf.seek(seek + Param.id_seek);
			nodes_ids.addAll(getNodeIdsFromWay(raf.readLong(), raf));
			
			// Долгота (ось X) точек объекта карты
			double [] lon_x_outer = new double[nodes_ids.size()]; // Долгота (ось X) точек внешнего полигона
			lon_x_outer = Arrays.copyOf(getLongitude(nodes_ids), nodes_ids.size());
			
			// Широта (ось Y) точек объекта карты
			double [] lat_y_outer = new double[nodes_ids.size()]; // Широта (ось Y) точек внешнего полигона
			lat_y_outer = Arrays.copyOf(getLatitude(nodes_ids), nodes_ids.size());
			
			if(type == Param.point) { // Точка
				// Создаем геометрическую фигуру полигона из точек объекта карты
				JPolygon polygon = new JPolygon(lon_x_outer, lat_y_outer, nodes_ids.size());
				
				bounds = new JRect[1];
				bounds[0] = polygon.getBounds(); // Ось Y вверх
			} else if(type == Param.poligon_outer) { // Площадной объект
				if(nodes_ids.size() % 3 == 0) { // Проверяем, что кол-во треугольников ровное
					bounds = new JRect[nodes_ids.size() / 3];
					
					for(int i = 0, j = 0; i < nodes_ids.size(); i += 3, j++) {
						double[] tmpLonX = {lon_x_outer[i], lon_x_outer[i + 1], lon_x_outer[i + 2]};
						double[] tmpLatY = {lat_y_outer[i], lat_y_outer[i + 1], lat_y_outer[i + 2]};
						
						// Создаем геометрическую фигуру полигона из точек объекта карты
						JPolygon polygon = new JPolygon(tmpLonX, tmpLatY, tmpLonX.length);
						
						// Получаем от полигона его ограничивающий пряоугольник, но т.к. ось Y полигона напрвленна
						// вверх и широта изменяется также, а ось Y у прямоугольника направлена вниз, то меняем 
						// друг с другом координаты top и bottom в возвращаемом ограничивающем прямоугольнике
						bounds[j] = polygon.getBounds(); // Ось Y вверх
					}
				}				
//			} else if(type == Param.poligon_line) { // Замкнутая линия
				
			} else { // Линия 
				if(nodes_ids.size() > 1) { // Проверка, что это не точка
					bounds = new JRect[nodes_ids.size() - 1];
					
					for(int i = 0, j = 0; i < nodes_ids.size() - 1; i++, j++) {
						double[] tmpLonX = {lon_x_outer[i], lon_x_outer[i + 1]};
						double[] tmpLatY = {lat_y_outer[i], lat_y_outer[i + 1]};
						
						// Создаем геометрическую фигуру полигона из точек объекта карты
						JPolygon polygon = new JPolygon(tmpLonX, tmpLatY, tmpLonX.length);
						
						// Получаем от полигона его ограничивающий пряоугольник, но т.к. ось Y полигона напрвленна
						// вверх и широта изменяется также, а ось Y у прямоугольника направлена вниз, то меняем 
						// друг с другом координаты top и bottom в возвращаемом ограничивающем прямоугольнике
						bounds[j] = polygon.getBounds(); // Ось Y вверх
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return bounds;
	}
	
	/**
	 * Возвращает массив со значениями долготы для объекта карты.
	 * 
	 * @param nodes_ids идентификаторы точек объекта карты
	 * @return массив со значениями долготы для заданного объекта карты
	 */
	
	private double [] getLongitude(ArrayList<Long> nodes_ids) {
		double [] lonArray = new double[nodes_ids.size()];
		
		for(int i = 0; i < nodes_ids.size(); i++) {
			lonArray[i] = getLongitude(nodes_ids.get(i));
		}
		
		return lonArray;
	}
	
	/**
	 * Возвращает значение долготы заданной точки.
	 * 
	 * @param nodeId идентификатор точки
	 * @return значение долготы заданной точки
	 */
	
	public double getLongitude(long nodeId) {
		double lon = Double.MIN_VALUE; // Долгота точки
		long seek = 0;
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			if(Param.seekChanged) {
				seek = Param.new_seek_nodes.get(nodeId);
			} else {
				seek = Param.seek_nodes.get(nodeId);
			}			
			
			raf.seek(seek + Param.lon_seek);
			lon = raf.readDouble();	
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return lon;
	}
	
	/**
	 * Возвращает массив со значениями широты для объекта карты.
	 * 
	 * @param nodes_ids идентификаторы точек объекта карты
	 * @return массив со значениями широты для заданного объекта карты
	 */
	
	private double [] getLatitude(ArrayList<Long> nodes_ids) {
		double [] latArray = new double[nodes_ids.size()];
		
		for(int i = 0; i < nodes_ids.size(); i++) {
			latArray[i] = getLatitude(nodes_ids.get(i));
		}
		
		return latArray;
	}
	
	/**
	 * Возвращает значение широты заданной точки.
	 * 
	 * @param nodeId - идентификатор точки
	 * @return значение широты заданной точки
	 */
	
	public double getLatitude(long nodeId) {
		double lat = Double.MIN_VALUE; // Долгота точки
		long seek = 0;
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			if(Param.seekChanged) {
				seek = Param.new_seek_nodes.get(nodeId);
			} else {
				seek = Param.seek_nodes.get(nodeId);
			}			
			
			raf.seek(seek + Param.lat_seek);
			lat = raf.readDouble();			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return lat;
	}
	
	/**
	 * Записываем в файл карты map.dnvg параметры точки при первичном считывании данных из xml
	 * файла без учета тегов.
	 * 
	 * @param id идентификатор точки
	 * @param lat широта точки
	 * @param lon долгота точки
	 */
	
	public void setNodeParam(long id, double lat, double lon) {
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			long seek = raf.length();
			
			// Тип элемента
			raf.seek(seek);
			raf.writeByte(Param.point);
			
			// Удаление	
			raf.seek(seek + Param.delete_seek);
			raf.writeByte(0x00);
			
			// Идентификатор		
			raf.seek(seek + Param.id_seek);
			raf.writeLong(id);
			
			// Свое смещение		
			raf.seek(seek + Param.myself_seek);
			raf.writeLong(seek);
			
			// Смещение следующего элемента
			raf.seek(seek + Param.next_seek);
			raf.writeLong(0);
			
			// Смещение атрибута		
			raf.seek(seek + Param.attr_seek);
			raf.writeLong(0);
			
			// Широта		
			raf.seek(seek + Param.lat_seek);
			raf.writeDouble(lat);
			
			// Долгота			
			raf.seek(seek + Param.lon_seek);
			raf.writeDouble(lon);
			
			// Высота			
			raf.seek(seek + Param.alt_seek);
			raf.writeFloat(0);
			
			// Точность			
			raf.seek(seek + Param.acc_seek);
			raf.writeFloat(0);
			
			// Признак принадлежности элемента к ограничивающему контуру площадного объекта карты
			raf.seek(seek + Param.boundary_seek);
			raf.writeByte(Param.boundary);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Присваиваем новые идентификаторы точкам карты.
	 */
	
	public void setNewIndexes() {
		long tmp_newIndex = Param.newIndex; // Для сравнения	
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			// Список всех точек файла		
			ArrayList<Long> nodeSeeks = getAllPoints(raf);	
						
			// Только точки
			for(int i = 0; i < nodeSeeks.size(); i++) {
				long tmp_seek = nodeSeeks.get(i);
				
				Param.newIndex++;
								
				raf.seek(tmp_seek + Param.id_seek);
				raf.writeLong(Param.newIndex);
				
				Param.new_seek_nodes.put(Param.newIndex, tmp_seek);
				Param.new_seek_only_nodes.put(Param.newIndex, tmp_seek);
			}
			
			// Идентификаторы полигонов, у которых есть внутренние полигоны
			ArrayList<Long> outer_ways_ids_with_inners = new ArrayList<>();
			int c = 0;
			Collection<Long> array_ways = Param.seek_ways.values();
			Iterator<Long> iterator_ways = array_ways.iterator();
			
			// Только линии
			while(iterator_ways.hasNext()) {
				long tmp_seek = iterator_ways.next();
				if(tmp_seek == 919087) {
					int y = 0;
				}
				raf.seek(tmp_seek);
				byte type = raf.readByte();
			
				// Внутренние полигоны пока не индексируем
				if(type == Param.poligon_inner_composition) {
					///
/*										c++;
										raf.seek(tmp_seek + Param.id_seek);
										Param.inner.add(raf.readLong());*/
										///
					continue;
				}
																
				Param.newIndex++;
				c++;
				// Сохраняем идентификаторы в новом списке точек в линии
				raf.seek(tmp_seek + Param.id_seek);
				long id = raf.readLong();
				
				if(id == 2349109159l) {
					int y = 0;
				}
				
				// Ищем внешний полигон, у которого есть внутренние
				if(Param.ids_outer_ways_with_inner_ways.get(id) != null) {
					outer_ways_ids_with_inners.add(Param.newIndex);
					
					ArrayList<Long> tmp_inner_ids = Param.ids_outer_ways_with_inner_ways.get(id);
					Param.ids_outer_ways_with_inner_ways.remove(id);
					Param.ids_outer_ways_with_inner_ways.put(Param.newIndex, new ArrayList<Long>(tmp_inner_ids));
				}
					
				int size = (int) getNodeIdsFromWay(id, raf).size();
				
				Param.num_points.remove(id);
				Param.num_points.put(Param.newIndex, size);				
				
				// Меняем идентификатор
				raf.seek(tmp_seek + Param.id_seek);
				raf.writeLong(Param.newIndex);
				
				// Заполняем новые списки
				Param.new_seek_nodes.put(Param.newIndex, tmp_seek);
				
				// Внутренние полигоны в этот список не сохраняем.
				// В деревья поиска они не попадают.
				raf.seek(tmp_seek);
				
				if(raf.readByte() != Param.poligon_inner_composition)
					Param.new_seek_only_ways.put(Param.newIndex, tmp_seek);
				
				// Переиндексируем остальные точки линии
				reIndexWay(tmp_seek, tmp_seek, raf);
			}
//			int cc = 0;
			// Теперь переиндексируем внутренние полигоны
			for(int i = 0; i < outer_ways_ids_with_inners.size(); i++) {
				ArrayList<Long> tmp_inner_ways_ids = Param.ids_outer_ways_with_inner_ways.get(outer_ways_ids_with_inners.get(i));
				ArrayList<Long> new_inner_ways_ids = new ArrayList<>(); // Новые идентификаторы внутренних полигонов
//				cc += tmp_inner_ways_ids.size();
				for(int j = 0; j < tmp_inner_ways_ids.size(); j++) {
					long inner_way_id = tmp_inner_ways_ids.get(j);
					long tmp_seek = Param.seek_ways.get(inner_way_id);
					///
//					Param.inner_from_way.add(inner_way_id);
					///
					Param.newIndex++;
					
					// Сохраняем идентификаторы в новом списке точек в линии
					int size = (int) getNodeIdsFromWay(inner_way_id, raf).size();
					Param.num_points.remove(inner_way_id);
					Param.num_points.put(Param.newIndex, size);				
					
					// Меняем идентификатор
					raf.seek(tmp_seek + Param.id_seek);
					raf.writeLong(Param.newIndex);
					
					new_inner_ways_ids.add(Param.newIndex);
					
					// Заполняем новые списки
					Param.new_seek_nodes.put(Param.newIndex, tmp_seek);
									
					reIndexWay(tmp_seek, tmp_seek, raf);
				}
				
				// Удаляем старую ссылку и добавляем вместо нее новую
				Param.ids_outer_ways_with_inner_ways.remove(outer_ways_ids_with_inners.get(i));
				Param.ids_outer_ways_with_inner_ways.put(outer_ways_ids_with_inners.get(i), new_inner_ways_ids);
			}
//			System.out.println("C: " + c + " CC: " + cc);
			// Признак того, что старые идентификаторы заменили новыми
			Param.seekChanged = true;
			
			// Проверка количества проиндексированых точек
			Collection<Long> array = Param.seek_nodes.values();
			Iterator<Long> iterator = array.iterator();
			
			while(iterator.hasNext()) {	
				@SuppressWarnings("unused")
				long tmp_seek = iterator.next();
				tmp_newIndex++;
			}
						
			System.out.println("Общий счетчик точек: " + tmp_newIndex + ".\nСуммированный счетчик точек: " + Param.newIndex + ". Разница: " + (tmp_newIndex - Param.newIndex));
			///
/*						int s2 = Param.inner.size();
						int s1 = Param.inner_from_way.size();
						
						for(int i = 0; i < s1; i++) {
							long id1 = Param.inner_from_way.get(i);
							
							for(int j = 0; j < s2; j++) {
								long id2 = Param.inner.get(j);
								
								if(id1 == id2) {
									Param.inner.remove(j);
									s2 = Param.inner.size();
								}
							}
						}
						
						int count = 0;
						
						int y = 0;*/
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Переиндексирует все последующие точки в линии.
	 * 
	 * @param seek - смещение первой точки в линии
	 * @param firstSeek - смещение первой точки в линии
	 * @param raf - объект для доступа к файлу
	 */
	
	private void reIndexWay(long seek, long firstSeek, RandomAccessFile raf) {
		try {
			long first = firstSeek; // Смещение первой точки в линии
			
			raf.seek(seek + Param.next_seek); // Значение смещения следующего элемента в линии
			long nextSeek = raf.readLong(); // Смещение следующего элемента	
			
			while(nextSeek != 0 && nextSeek != first) {
				Param.newIndex++;
				
				// Меняем идентификатор
				raf.seek(nextSeek + Param.id_seek);				
				raf.writeLong(Param.newIndex);
				
				Param.new_seek_nodes.put(Param.newIndex, nextSeek);
				
				raf.seek(nextSeek + Param.next_seek); // Значение смещения следующего элемента в линии
				nextSeek = raf.readLong();
			} 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
			
	/**
	 * Записываем в файл карты map.hnvg описание элемента карты.
	 * 
	 * @param seek смещение элемента в файле карты
	 * @param str_v строка для записи в файл
	 **/
	
	public void setName(long seek, String str_v) {
		try {
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			raf.seek(seek + Param.attr_seek);
			long attr_seek = raf.readLong();
			
			// Если у элемента существует атрибут, то записываем в него описание объекта
			if(attr_seek > 0) {	
				long description_seek = attr_seek + Param.description_seek;
				h_raf.seek(description_seek);
							
				// Проверяем не пустая ли строка описания, и если не пустая, затираем ее 
				if(!h_raf.readUTF().isEmpty()) {
					h_raf.seek(description_seek);
					h_raf.write(setEmptyBytes(Param.description_size));
				}	
							
				h_raf.seek(description_seek);
							
				// Проверяем длину описания, и если она больше допустимой, то обрезаем ее
				if(str_v.getBytes("UTF-8").length > (Param.description_size - 2)) {
					int size = str_v.getBytes("UTF-8").length - (str_v.getBytes("UTF-8").length - (Param.description_size - 2));				
								
					h_raf.writeShort((short)size);
					h_raf.seek(description_seek + 2);
					h_raf.write(str_v.getBytes("UTF-8"), 0, (Param.description_size - 2));
				} else
					h_raf.writeUTF(str_v);
			}	
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
	 * Записываем в файл карты map.hnvg тип объекта на карте.
	 * 
	 * @param seek смещение элемента в файле карты
	 * @param typeOfObject тип объекта на карте
	 */
	
	public void setTypeOfObject(long seek, short typeOfObject) {
		try {
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			raf.seek(seek + Param.attr_seek);
			long attr_seek = raf.readLong();
			
			// Если у точки уже есть атрибут, то записываем в него тип объекта на карте
			if(attr_seek > 0) {
				h_raf.seek(attr_seek + Param.typeOfObject_seek);
				h_raf.writeShort(typeOfObject);
				
				setAttrSeek(seek, seek, attr_seek, raf);
			} else { // Если у точки нет атрибута, то создаем его и записываем в него тип объекта на карте
				attr_seek = createAttr(seek, h_raf);
				
				// Тип объекта на карте
				h_raf.seek(attr_seek + Param.typeOfObject_seek);
				h_raf.writeShort(typeOfObject);
				
				// Смещение атрибута элемента карты
				raf.seek(seek + Param.attr_seek);
				raf.writeLong(attr_seek);
				
				setAttrSeek(seek, seek, attr_seek, raf);
			}
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
	 * Создает копию атрибута элемента карты в файле map.hnvg с новым идентификатором.
	 * 
	 * @param attrSeek смещение атрибута оригинальной точки 
	 * @param firstPointInWaySeek смещение первой точки в линии
	 * @return смещение созданного атрибута
	 */
	
	private long createCopyAttr(long attrSeek, long firstPointInWaySeek) {
		long newAttrSeek = -1; // Новое смещение атрибута
		
		try {
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			
			newAttrSeek = h_raf.length();
			
			if(newAttrSeek == 9344634) {
				int y = 0;
			}
			
			// Копируем данные из оригинального атрибута
			h_raf.seek(attrSeek + Param.typeOfObject_seek);
			short typeOfObject = h_raf.readShort();
			short addType = h_raf.readShort();
			byte property = h_raf.readByte();
			String description = h_raf.readUTF();
			
			// Создаем новый атрибут и заполняем данными
			// Новое смещение атрибута
			h_raf.seek(newAttrSeek);
			h_raf.writeLong(newAttrSeek);
			
			// Смещение первой точки в линии
			h_raf.seek(newAttrSeek + Param.firsPointInWay_seek);
			h_raf.writeLong(firstPointInWaySeek);
			
			// Тип объекта на карте
			h_raf.seek(newAttrSeek + Param.typeOfObject_seek);	
			h_raf.writeShort(typeOfObject);
				
			// Дополнительный тип объекта на карте
			h_raf.seek(newAttrSeek + Param.additionalTypeOfObject_seek);	
			h_raf.writeShort(addType);
				
			// Св-во объекта на карте
			h_raf.seek(newAttrSeek + Param.properyType_seek);			
			h_raf.writeByte(property);
				
			// Описание
			h_raf.seek(newAttrSeek + Param.description_seek);
			h_raf.write(setEmptyBytes(Param.description_size));
			
			h_raf.seek(newAttrSeek + Param.description_seek);
			h_raf.writeUTF(description);
			
			Param.attrs.add(newAttrSeek);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(h_raf != null)
					h_raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return newAttrSeek;
	}
	
	/**
	 * Создает пустой атрибут элемента карты в файле map.hnvg и записывает в него
	 * значение смещения элемента карты, на который он указывает.
	 * 
	 * @param seek смещение элемента карты
	 * @param h_raf объект для доступа к файлу map.hnvg
	 * @return смещение созданного атрибута
	 */
	
	private long createAttr(long seek, RandomAccessFile h_raf) {
		long attr_seek = 0;
		
		try {
			attr_seek = h_raf.length();
			
			if(attr_seek == 9344634) {
				int y = 0;
			}
			
			// Смещение атрибута
			h_raf.seek(attr_seek);
			h_raf.writeLong(attr_seek);
			
			// Смещение первого элемента обекта карты в файле map.dnvg
			h_raf.seek(attr_seek + Param.firsPointInWay_seek);
			h_raf.writeLong(seek);
			
			// Тип объекта на карте
			h_raf.seek(attr_seek + Param.typeOfObject_seek);
			h_raf.writeShort(Param.noType);
			
			// Дополнительный тип объекта на карте
			h_raf.seek(attr_seek + Param.additionalTypeOfObject_seek);
			h_raf.writeShort(Param.noType);
			
			// Свойство объекта на карте
			h_raf.seek(attr_seek + Param.properyType_seek);
			h_raf.writeByte(0x00);
			
			// Описание объекта карты	
			h_raf.seek(attr_seek + Param.description_seek);
			h_raf.write(setEmptyBytes(Param.description_size));
			
			Param.attrs.add(attr_seek);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return attr_seek;
	}
	
	/**
	 * Очищает все данные (кроме своего смещения) в атрибуте элемента карты, а 
	 * на место смещения первой точки в линии записывает заданное смещение.
	 * 
	 * @param firstPointInWay смещение элемента карты
	 */
	
	public long  clearAttrSeek(long firstPointInWay) {
		long attrSeek = 0;
		
		try {	
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			raf.seek(firstPointInWay + Param.attr_seek);
			attrSeek = raf.readLong();
			
			if(attrSeek > 0) {				
				h_raf.seek(attrSeek + Param.firsPointInWay_seek);
				h_raf.writeLong(firstPointInWay);
				
				h_raf.seek(attrSeek + Param.typeOfObject_seek);
				h_raf.writeShort(Param.noType);
				
				h_raf.seek(attrSeek + Param.additionalTypeOfObject_seek);
				h_raf.writeShort(Param.noType);
				
				h_raf.seek(attrSeek + Param.properyType_seek);
				h_raf.writeByte(0);
				
				h_raf.seek(attrSeek + Param.description_seek);
				h_raf.write(setEmptyBytes(Param.description_size));
				
				setAttrSeek(firstPointInWay, firstPointInWay, attrSeek, raf);
			} else {
				attrSeek = createAttr(firstPointInWay, h_raf);
				setAttrSeek(firstPointInWay, firstPointInWay, attrSeek, raf);
			}				
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
		
		return attrSeek;
	}
	
	/**
	 * Записывает значение смещения атрибута в элемент карты.
	 * 
	 * @param seek смещение элемента в файле карты
	 * @param attrSeek смещение атрибута элемента
	 */
	
	public void setAttrSeek(long seek, long attrSeek) {
		try {	
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			raf.seek(seek + Param.attr_seek); // Значение смещения следующего элемента в линии
			raf.writeLong(attrSeek);				
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {				
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Записывает во все точки элемента карты значение смещения атрибута.
	 * 
	 * @param seek смещение элемента в файле карты
	 * @param firstSeek смещение первой точки в линии
	 * @param attrSeek смещение атрибута элемента
	 * @param raf объект для доступа к файлу
	 */
	
	private void setAttrSeek(long seek, long firstSeek, long attrSeek, RandomAccessFile raf) {
		try {
			long first = firstSeek; // Смещение первой точки в линии
			
			// Записываем значение смещения атрибута в первую точку линии
			if(seek == firstSeek) {
				raf.seek(firstSeek + Param.attr_seek);
				raf.writeLong(attrSeek);
			}
			
			raf.seek(seek + Param.next_seek); // Значение смещения следующего элемента в линии
			long nextSeek = raf.readLong(); // Смещение следующего элемента
			
			while(nextSeek != 0 && nextSeek != first) {
				raf.seek(nextSeek + Param.attr_seek);					
				raf.writeLong(attrSeek);
				
				raf.seek(nextSeek + Param.next_seek); // Значение смещения следующего элемента в линии
				nextSeek = raf.readLong();
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Записывает во все точки элемента карты значение высоты точки.
	 * 
	 * @param seek смещение элемента в файле карты
	 * @param firstSeek смещение первой точки в линии
	 * @param alt значение высоты точки
	 * @param raf объект для доступа к файлу
	 */
	
	private void setAltitude(long seek, long firstSeek, float alt, RandomAccessFile raf) {
		try {
			long first = firstSeek; // Смещение первой точки в линии
			
			// Записываем значение значение высоты точки в первую точку линии
			if(seek == firstSeek) {
				raf.seek(firstSeek + Param.alt_seek);
				raf.writeFloat(alt);
			}
			
			raf.seek(seek + Param.next_seek); // Значение смещения следующего элемента в линии
			long nextSeek = raf.readLong(); // Смещение следующего элемента
			
			while(nextSeek != 0 && nextSeek != first) {
				raf.seek(nextSeek + Param.alt_seek);					
				raf.writeFloat(alt);
				
				raf.seek(nextSeek + Param.next_seek); // Значение смещения следующего элемента в линии
				nextSeek = raf.readLong();
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Записывает во все точки элемента карты значение точности координат точки.
	 * 
	 * @param seek смещение элемента в файле карты
	 * @param firstSeek смещение первой точки в линии
	 * @param acc значение точности координат точки
	 * @param raf объект для доступа к файлу
	 */
	
	private void setAccuracy(long seek, long firstSeek, float acc, RandomAccessFile raf) {
		try {
			long first = firstSeek; // Смещение первой точки в линии
			
			// Записываем значение точности координат точки в первую точку линии
			if(seek == firstSeek) {
				raf.seek(firstSeek + Param.acc_seek);
				raf.writeFloat(acc);
			}
			
			raf.seek(seek + Param.next_seek); // Значение смещения следующего элемента в линии
			long nextSeek = raf.readLong(); // Смещение следующего элемента
			
			while(nextSeek != 0 && nextSeek != first) {
				raf.seek(nextSeek + Param.acc_seek);					
				raf.writeFloat(acc);
				
				raf.seek(nextSeek + Param.next_seek); // Значение смещения следующего элемента в линии
				nextSeek = raf.readLong(); // Смещение следующего элемента
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Записывает во все точки элемента карты значение смещения атрибута.
	 * 
	 * @param seek смещение элемента в файле карты
	 * @param firstSeek смещение первой точки в линии
	 * @param attrSeek смещение атрибута элемента
	 */
	
	public void setAttrSeek(long seek, long firstSeek, long attrSeek) {
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			long first = firstSeek; // Смещение первой точки в линии
			
			// Записываем значение смещения атрибута в первую точку линии
			if(seek == firstSeek) {
				raf.seek(firstSeek + Param.attr_seek);
				raf.writeLong(attrSeek);
			}
			
			raf.seek(seek + Param.next_seek); // Значение смещения следующего элемента в линии
			long nextSeek = raf.readLong(); // Смещение следующего элемента
			
			if(nextSeek != 0 && nextSeek != first) {
				raf.seek(nextSeek + Param.attr_seek);					
				raf.writeLong(attrSeek);
				
				setAttrSeek(nextSeek, first, attrSeek, raf);
			}			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Записываем в файл карты map.hnvg дополнительный тип объекта на карте.
	 * 
	 * @param seek смещение элемента в файле карты
	 * @param typeOfObject тип объекта на карте
	 */
	
	public void setAdditionalTypeOfObject(long seek, short typeOfObject) {
		try {
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			// Значение смещения атрибута в map.hnvg
			raf.seek(seek + Param.attr_seek);
			long attr_seek = raf.readLong();
						
			if(attr_seek > 0) {
				// Записываем значение доп. типа объекта на карте
				h_raf.seek(attr_seek + Param.additionalTypeOfObject_seek);
				h_raf.writeShort(typeOfObject);
			}
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
	 * Записываем в файл карты дополнительный тип объекта на карте.
	 * 
	 * @param seek - смещение элемента в файле карты
	 * @param typeOfObject - тип объекта на карте
	 * @param raf - объект для доступа к файлу
	 */
	
	public void setAdditionalTypeOfObject(long seek, short typeOfObject, RandomAccessFile raf) {
		try {
			raf.seek(seek + Param.additionalTypeOfObject_seek);
			raf.writeShort(typeOfObject);
			
			setAdditionalTypeOfObject(seek, seek, typeOfObject, raf);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Записываем в файл карты дополнительный тип объекта на карте.
	 * 
	 * @param seek - смещение элемента в файле карты
	 * @param firstSeek - смещение первой точки в линии
	 * @param typeOfObject - тип объекта на карте
	 * @param raf - объект для доступа к файлу
	 */
	
	private void setAdditionalTypeOfObject(long seek, long firstSeek, short typeOfObject, 
			                               RandomAccessFile raf) {
		try {	
			long first = firstSeek; // Смещение первой точки в линии
			
			raf.seek(seek + Param.next_seek); // Значение смещения следующего элемента в линии
			long nextSeek = raf.readLong(); // Смещение следующего элемента
			
			if(nextSeek != 0 && nextSeek != first) {
				raf.seek(nextSeek + Param.additionalTypeOfObject_seek);
					
				raf.writeShort(typeOfObject);
				
				setAdditionalTypeOfObject(nextSeek, first, typeOfObject, raf);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Устанавливает в отдельной точке признак принадлежности элемента к ограничивающему контуру 
	 * площадного объекта карты.
	 * 
	 * @param seek смещение точки
	 * @param raf объект для доступа к файлу
	 */
	
	public void setBoundary(long seek, RandomAccessFile raf) {
		try {
			raf.seek(seek + Param.boundary_seek);
			raf.writeByte(Param.boundary);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Добавляет дополнительную точку в конец элемента карты
	 * polygon_line и меняет тип элемента на line. 
	 */
	
	public void setPointInPolygonLine() {
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			for(int i = 0; i < Param.seek_ways_with_poligon_line_type.size(); i++) {
				long tmp_seek = Param.seek_ways_with_poligon_line_type.get(i);
				
				raf.seek(tmp_seek + Param.id_seek);
				long way_id = raf.readLong();
				
				raf.seek(tmp_seek + Param.delete_seek);
				byte del = raf.readByte();
				
				if(del != Param.delete) {
					Param.maxNodeId++; // Новый идентификатор точки
					
					long newSeek = createNewNodeForWay(tmp_seek, raf);
					
					Param.seek_nodes.put(Param.maxNodeId, newSeek);
					Param.seek_nodes_used.put(Param.maxNodeId, newSeek);
					
					// Делаем точку последней в линии
					raf.seek(newSeek + Param.next_seek);
					raf.writeLong(0);
						
					long lastSeek = getSeekLastPointWay(tmp_seek, raf);
						
					// Делаем точку предпоследней в линии
					raf.seek(lastSeek + Param.next_seek);
					raf.writeLong(newSeek);	
					
					int size = Param.num_points.get(way_id);
					
					// Изменяем количество точек в линии в списке
					Param.num_points.remove(way_id);
					Param.num_points.put(way_id, size + 1);
					
					// Меняем тип элемента карты
					setType(Param.line, tmp_seek, raf);
				}
			}
			
			Param.seek_ways_with_poligon_line_type.clear();
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
	 * Записываем в файл карты map.hnvg свойство объекта карты.
	 * 
	 * @param seek смещение элемента в файле карты
	 * @param properyType свойство типа элемента карты
	 */
	
	public void setPropertyType(long seek, byte properyType) {
		try {
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			// Значение смещения атрибута в map.hnvg
			raf.seek(seek + Param.attr_seek);
			long attr_seek = raf.readLong();
			
			if(attr_seek > 0) {
				// Записываем значение свойства объекта на карте
				h_raf.seek(attr_seek + Param.properyType_seek);
				h_raf.writeByte(properyType);
			} 
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
	 * Записываем в файл карты свойство объекта карты.
	 * 
	 * @param seek - смещение элемента в файле карты
	 * @param properyType - свойство типа элемента карты
	 * @param raf - объект для доступа к файлу
	 */
	
	public void setPropertyType(long seek, byte properyType, RandomAccessFile raf) {
		try {
			raf.seek(seek + Param.properyType_seek);
			raf.writeByte(properyType);	
			
			setPropertyType(seek, seek, properyType, raf);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Записываем в файл карты свойство объекта карты.
	 * 
	 * @param seek - смещение элемента в файле карты
	 * @param firstSeek - смещение первой точки в линии
	 * @param properyType - свойство типа элемента карты
	 * @param raf - объект для доступа к файлу
	 */
	
	private void setPropertyType(long seek, long firstSeek, byte properyType, RandomAccessFile raf) {
		try {
			long first = firstSeek; // Смещение первой точки в линии
			
			raf.seek(seek + Param.next_seek); // Значение смещения следующего элемента в линии
			long nextSeek = raf.readLong(); // Смещение следующего элемента
			
			if(nextSeek != 0 && nextSeek != first) {
				raf.seek(nextSeek + Param.properyType_seek);
					
				raf.writeByte(properyType);
				
				setPropertyType(nextSeek, first, properyType, raf);
			}		
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
		
	/**
	 * Заменяем тип элемента карты по заданному смещению и всех последующих
	 * элементов линии.
	 * 
	 * @param type тип элемента
	 * @param seek смещение элемента в файле карты
	 */
	
	public void setType(byte type, long seek) {
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			raf.seek(seek);
			raf.writeByte(type);
			
			setType(type, seek, seek, raf);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Заменяем тип элемента карты по заданному смещению и всех остальных
	 * элементов, на которые указывает данных элемент.
	 * 
	 * @param type - тип элемента
	 * @param seek - смещение элемента в файле карты
	 * @param raf - объект для доступа к файлу
	 */
	
	public void setType(byte type, long seek, RandomAccessFile raf) {
		try {			
			raf.seek(seek);
			raf.writeByte(type);
			
			setType(type, seek, seek, raf);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Заменяем тип элемента карты по заданному смещению и всех остальных
	 * элементов, на которые указывает данных элемент в файле map.dnvg.
	 * 
	 * @param type тип элемента
	 * @param seek смещение элемента в файле карты
	 * @param firstSeek смещение первой точки в линии
	 * @param raf объект для доступа к файлу
	 */
	
	private void setType(byte type, long seek, long firstSeek, RandomAccessFile raf) {
		try {
			long first = firstSeek; // Смещение первой точки в линии
			
			// Записываем значение типа элемента в первую точку линии
			if(seek == firstSeek) {
				raf.seek(firstSeek);
				raf.writeByte(type);
			}
			
			raf.seek(seek + Param.next_seek); // Значение смещения следующего элемента в линии
			long nextSeek = raf.readLong(); // Смещение следующего элемента
			
			while(nextSeek != 0 && nextSeek != first) {
				raf.seek(nextSeek);					
				raf.writeByte(type);
				
				raf.seek(nextSeek + Param.next_seek); // Значение смещения следующего элемента в линии
				nextSeek = raf.readLong();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Устанавливает значение смещения следующего элемента в последней точке предыдущего элемента равным
	 * заданным значением смещения, тем самым объединяет несколько линий в одну линию. 
	 * 
	 * @param seek смещение текущего элемента
	 * @param prevSeek смещение предыдущего элемента
	 */
	
	public void setNextInLastElement(long seek, long prevSeek) {
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");		
			
			raf.seek(getSeekLastPointWay(prevSeek, raf) + Param.next_seek);
			raf.writeLong(seek);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
		
	/**
	 * Удаляет последнюю точку в линии, если она совпадает с ее первой точкой.
	 * 
	 * @param way_id идентификатор линии
	 */
	
	public void deleteDublicatNodes(Long way_id) {
		// Первая точка линии равна последней точке линии
		boolean twins = false; 
				
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			ArrayList<Long> nodes_ids_way = new ArrayList<>(); // Идентификаторы точек линии
			nodes_ids_way.addAll(getNodeIdsFromWay(way_id, raf));
			
			twins = compareCoordsNodes(nodes_ids_way.get(0), 
					                   nodes_ids_way.get(nodes_ids_way.size() - 1), raf);
			if(twins) {
				// Обнуляем ссылку на последнюю точку линии в предыдущей ее точке
				raf.seek(Param.seek_nodes.get(nodes_ids_way.get(nodes_ids_way.size() - 2)) + Param.next_seek);
				raf.writeLong(0);
				
				// Подготавливаем к удалению последнюю точку
				deleteNodeFromList(nodes_ids_way.get(nodes_ids_way.size() - 1), raf);
				
				// Удаляем последнюю точку линии из списка
				nodes_ids_way.remove(nodes_ids_way.size() - 1);
				// Изменяем количество точек в линии в списке
				Param.num_points.remove(way_id);
				Param.num_points.put(way_id, nodes_ids_way.size());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Удаляет последнюю точку в линии, если она совпадает с ее первой точкой.
	 * 
	 * @param way_id идентификатор линии
	 * @param raf объект для доступа к файлу map.dnvg
	 */
	
	public void deleteDublicatNodes(Long way_id, RandomAccessFile raf) {
		// Первая точка линии равна последней точке линии
		boolean twins = false;
		
		try {			
			ArrayList<Long> nodes_ids_way = new ArrayList<>(); // Идентификаторы точек линии
			nodes_ids_way.addAll(getNodeIdsFromWay(way_id, raf));
			
			twins = compareCoordsNodes(nodes_ids_way.get(0), 
					                   nodes_ids_way.get(nodes_ids_way.size() - 1), raf);
			if(twins) {
				// Обнуляем ссылку на последнюю точку линии в предыдущей ее точке
				raf.seek(Param.seek_nodes.get(nodes_ids_way.get(nodes_ids_way.size() - 2)) + Param.next_seek);
				raf.writeLong(0);
				
				// Подготавливаем к удалению последнюю точку
				deleteNodeFromList(nodes_ids_way.get(nodes_ids_way.size() - 1), raf);
				
				// Удаляем последнюю точку линии из списка
				nodes_ids_way.remove(nodes_ids_way.size() - 1);
				// Изменяем количество точек в линии в списке
				Param.num_points.remove(way_id);
				Param.num_points.put(way_id, nodes_ids_way.size());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Удаляет точки с одинаковыми координатами из линий, находящихся в массиве линий.
	 * 
	 * @param ways_ids идентификаторы линий в массиве линий
	 * @return обновленные идентификаторы линий в массиве линий
	 */
	
	public void deleteDublicatNodes(ArrayList<Long> ways_ids) {
		// Последняя точка первой линии равна первой точке второй линии
		boolean twins = false; 
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");	
			
			ArrayList<Long> nodes_ids_way_1 = new ArrayList<>(); // Идентификаторы точек первой линии в паре для сравнивания
			ArrayList<Long> nodes_ids_way_2 = new ArrayList<>(); // Идентификаторы точек второй линии в паре для сравнивания
			
			int size = ways_ids.size();
			
			if(size > 1) { // В отношении больше одной линии
				// Кроме первой и последней точек
				for(int i = 0; i < size - 1; i++) {
					nodes_ids_way_1.addAll(getNodeIdsFromWay(ways_ids.get(i), raf));
					nodes_ids_way_2.addAll(getNodeIdsFromWay(ways_ids.get(i + 1), raf));
					
					twins = compareCoordsNodes(nodes_ids_way_1.get(nodes_ids_way_1.size() - 1),
							                   nodes_ids_way_2.get(0), raf);					
					if(twins) {
						if(nodes_ids_way_2.size() == 1) {							
							// Указывает на первую точку в следующей после второй линии (если есть)
							raf.seek(Param.seek_nodes.get(nodes_ids_way_2.get(0)) + Param.next_seek);
							long nextSeek = raf.readLong();
							
							// В последнюю точку первой линии записываем смещение на новую первую точку 
							// в следующей после второй линии (если есть)
							raf.seek(Param.seek_nodes.get(nodes_ids_way_1.get(nodes_ids_way_1.size() - 1)) + Param.next_seek);
							raf.writeLong(nextSeek);					
							
							Param.seek_ways.remove(ways_ids.get(i + 1));
							Param.seek_ways_used.remove(ways_ids.get(i + 1));
//							Param.seek_ways_used_in_relations.remove(ways_ids.get(i + 1));
							Param.num_points.remove(ways_ids.get(i + 1));
							
							deleteNodeFromList(nodes_ids_way_2.get(0), raf);
							
							ways_ids.remove(i + 1);
							
							--i;
						} else {
							// Указывает на вторую точку во второй линии
							raf.seek(Param.seek_nodes.get(nodes_ids_way_2.get(0)) + Param.next_seek);
							long nextSeek = raf.readLong();
							
							// В последнюю точку первой линии записываем смещение на новую первую точку второй линии
							raf.seek(Param.seek_nodes.get(nodes_ids_way_1.get(nodes_ids_way_1.size() - 1)) + Param.next_seek);
							raf.writeLong(nextSeek);							
							
							// Т.к. первая точка линии меняется, то удаляем эту линию из списка всех линий,
							// списка используемых линий, списка используемых линий в отношении и из 
							// списка количества точек в линии
						    Param.seek_ways.remove(nodes_ids_way_2.get(0));
						    Param.seek_ways_used.remove(nodes_ids_way_2.get(0));
//						    Param.seek_ways_used_in_relations.remove(nodes_ids_way_2.get(0));
						    Param.num_points.remove(nodes_ids_way_2.get(0));
						    
						    // Перезаписываем атрибуты
						    setFirstPointInAttr(nextSeek, raf);
						    
						    deleteNodeFromList(nodes_ids_way_2.get(0), raf);	
						    
						    // Удаляем первую точку во второй линии
							nodes_ids_way_2.remove(0);
							
							// Вставляем в список всех линий, список используемых линий, список используемых 
							// линий в отношении и список количества точек в линии новую линию
						    Param.seek_ways.put(nodes_ids_way_2.get(0), nextSeek);
						    Param.seek_ways_used.put(nodes_ids_way_2.get(0), nextSeek);
//						    Param.seek_ways_used_in_relations.put(nodes_ids_way_2.get(0), nextSeek);
						    Param.num_points.put(nodes_ids_way_2.get(0), nodes_ids_way_2.size());
						
						    // Изменяем идентификатор второй линии в списке линий
						    ways_ids.set(i + 1, nodes_ids_way_2.get(0));
							
							--i;
						}
					}
					
					nodes_ids_way_1.clear();
					nodes_ids_way_2.clear();
					
					size = ways_ids.size();
				}
			}
			
			// Для одной линии
			if(size == 1) {
				nodes_ids_way_1.addAll(getNodeIdsFromWay(ways_ids.get(0), raf));
				
				if(nodes_ids_way_1.size() > 1) {
					twins = compareCoordsNodes(nodes_ids_way_1.get(0),
							                   nodes_ids_way_1.get(nodes_ids_way_1.size() - 1), raf);
				
					if(twins) {
						// Обнуляем ссылку в предыдущей точке
						raf.seek(Param.seek_nodes.get(nodes_ids_way_1.get(nodes_ids_way_1.size() - 2)) + Param.next_seek);
						raf.writeLong(0);
						
						// Подготавливаем к удалению последнюю точку
						deleteNodeFromList(nodes_ids_way_1.get(nodes_ids_way_1.size() - 1), raf);
						
						// Удаляем последнюю точку в линии
						nodes_ids_way_1.remove(nodes_ids_way_1.get(nodes_ids_way_1.size() - 1));
						
						// Обновляем ссылку в списке количества точек в линии
						Param.num_points.remove(nodes_ids_way_1.get(0));
						Param.num_points.put(nodes_ids_way_1.get(0), nodes_ids_way_1.size());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Физическое удаление точек из файла карты. 
	 */
	
	private void deleteNodes() {
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");			
						
			Collection<Long> nodes_ids = Param.delete_nodes.values();
			Iterator<Long> iterator_nodes = nodes_ids.iterator();
						
			while(iterator_nodes.hasNext()) {
				// Место для вставки точки из конца файла
				long tmp_seek = iterator_nodes.next();
								
				// Идентификатор точки в списке удаленых точек
				raf.seek(tmp_seek + Param.id_seek);
				long tmp_id = raf.readLong();
				
				long node_seek = raf.length() - Param.elementSize; // Смещение последней точки в файле 
					
				raf.seek(node_seek + Param.delete_seek);
				byte del = raf.readByte(); // Признак того, что точка удалена
				
				// Если последнее место для вставки точки совпадает с этой же удаленной точкой, тогда
				// обрезаем файл и заканчиваем
				if((tmp_seek == node_seek) && nodes_ids.size() == 1) {
					raf.seek(node_seek + Param.id_seek);
					Param.delete_nodes.remove(raf.readLong());
					
					raf.setLength(node_seek);
					continue;
				}
				
				// Защита от точек с признаком удаления
				while(del == 0x01) {
					raf.seek(node_seek + Param.id_seek);
					Param.delete_nodes.remove(raf.readLong());
					
					nodes_ids = Param.delete_nodes.values();
					iterator_nodes = nodes_ids.iterator();
					
					if(tmp_seek == node_seek && nodes_ids.size() == 0) {
						raf.setLength(node_seek);
						return;
					}
					
					tmp_seek = iterator_nodes.next();
					
					// Идентификатор точки в списке удаленых точек
					raf.seek(tmp_seek + Param.id_seek);
					tmp_id = raf.readLong();
					
					node_seek -= Param.elementSize;
					
					raf.seek(node_seek + Param.delete_seek);
					del = raf.readByte();
				}
				
				insertNode(tmp_seek, node_seek, raf);
				
				raf = new RandomAccessFile(Param.dnvg_file_path, "rw");	
				
				Param.delete_nodes.remove(tmp_id);
				
				nodes_ids = Param.delete_nodes.values();
				iterator_nodes = nodes_ids.iterator();
				
				raf.setLength(node_seek);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void deleteAttrsAfterTriangulation() {
		OsmConverter.printLog("Количество атрибутов до удаления: " + Param.attrs.size());
		OsmConverter.printLog("Количество атрибутов для удаления: " + Param.delete_attrs.size());
		
		// Физическое удаление атрибутов точек карты
		deleteAttrs();
		
		OsmConverter.printLog("Атрибуты удалены: " + Param.delete_attrs.size());
		OsmConverter.printLog("Количество атрибутов после удаления: " + Param.attrs.size());
	}
	
	/**
	 * Физическое удаление атрибутов точек из файла карты. 
	 */
	
	private void deleteAttrs() {
		try {
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			
			int size = Param.delete_attrs.size();
			
			for(int i = 0; i < size; i++) {
				// Место для вставки атрибута из конца файла
				long tmp_seek = Param.delete_attrs.get(i);
				// Смещение последнего атрибута в файле
				long attr_seek = h_raf.length() - Param.attrBlockSize; 
							
				// Если последнее место для вставки атрибута совпадает с этим же удаленным атрибутом, тогда
				// обрезаем файл и заканчиваем
				if(tmp_seek == attr_seek) {
					Param.delete_attrs.remove(tmp_seek);
					Param.attrs.remove(attr_seek);
					size = Param.delete_attrs.size();
					
					--i;
					
					h_raf.setLength(attr_seek);
					continue;
				}
				
				// Проверяем не является ли перемещаемый атрибут атрибутом
				// из списка удаления
				int index = Param.delete_attrs.indexOf(attr_seek);
				
				// Если в списке нет этого атрибута
				while(index != -1) {
					Param.delete_attrs.remove(attr_seek);
					Param.attrs.remove(attr_seek);
					size = Param.delete_attrs.size();
					
					--i;
					
					if(tmp_seek == attr_seek && Param.delete_attrs.size() == 0) {
						h_raf.setLength(attr_seek);
						return;
					}
					
					if(tmp_seek == attr_seek && Param.delete_attrs.size() != 0) {
						tmp_seek = Param.delete_attrs.get(0);
					}
					
					attr_seek -= Param.attrBlockSize;
					
					index = Param.delete_attrs.indexOf(attr_seek);
				}				
				
				insertAttr(tmp_seek, attr_seek, h_raf);
				
				Param.delete_attrs.remove(tmp_seek);
				Param.attrs.remove(attr_seek);
				
				size = Param.delete_attrs.size();
				
				--i;
				
				if(i < -1)
					i = -1;				
				
				h_raf.setLength(attr_seek);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(h_raf != null)
					h_raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Вставляет атрибут (делает его копию и вставляет) в другое место файла карты.
	 * 
	 * @param src_seek смещение на место вставки атрибута
	 * @param attr_seek смещение атрибута
	 * @param h_raf объект для доступа к файлу
	 */
	
	public void insertAttr(long src_seek, long attr_seek, RandomAccessFile h_raf) {
		try {			
			h_raf.seek(attr_seek + Param.firsPointInWay_seek);
			
			// Считываем данные из копируемого атрибута
			long firstInLine = h_raf.readLong();
			short typeOfObject = h_raf.readShort();
			short additionalTypeOfObject = h_raf.readShort();
			byte properyType = h_raf.readByte();
			String description = h_raf.readUTF();
			
			// Записываем данные атрибута в нужное место
			// Смещение атрибута
			h_raf.seek(src_seek);
			h_raf.writeLong(src_seek);
						
			// Смещение первого элемента обекта карты в файле map.dnvg
			h_raf.seek(src_seek + Param.firsPointInWay_seek);
			h_raf.writeLong(firstInLine);
						
			// Тип объекта на карте
			h_raf.seek(src_seek + Param.typeOfObject_seek);
			h_raf.writeShort(typeOfObject);
						
			// Дополнительный тип объекта на карте
			h_raf.seek(src_seek + Param.additionalTypeOfObject_seek);
			h_raf.writeShort(additionalTypeOfObject);
						
			// Свойство объекта на карте
			h_raf.seek(src_seek + Param.properyType_seek);
			h_raf.writeByte(properyType);
						
			// Описание объекта карты	
			h_raf.seek(src_seek + Param.description_seek);
			h_raf.write(setEmptyBytes(Param.description_size));
			
			h_raf.seek(src_seek + Param.description_seek);
			h_raf.writeUTF(description);
			
			// Обновляем ссылки на вставленную в новое место точку	
			h_raf.seek(src_seek + Param.firsPointInWay_seek);
			long seek = h_raf.readLong();
			
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			raf.seek(seek + Param.attr_seek);
			
			// Обновляем ссылки атрибутов в точках
			setAttrSeek(seek, seek, src_seek, raf);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {				
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Вставляет точку (делает ее копию и вставляет) в другое место файла карты перезаписывая все ссылки 
	 * на нее от других точек (если ссылки есть).
	 * 
	 * @param src_seek смещение на место вставки точки
	 * @param node_seek смещение точки
	 * @param raf объект для доступа к файлу
	 */
	
	public void insertNode(long src_seek, long node_seek, RandomAccessFile raf) {
		try {
			boolean isFirstInLine = false; // Если смещение точки и первая точка в линии совпалают
			
			raf.seek(node_seek);
			if(src_seek == 4827366011l) {
				int y = 0;
			}
			// Считываем данные из копируемой точки
			byte type = raf.readByte();
			byte delete = raf.readByte();
			long id = raf.readLong();
			long myselfSeek = raf.readLong();
			long next = raf.readLong();
			long attrSeek = raf.readLong();
			double lat = raf.readDouble();
			double lon = raf.readDouble();
			float alt = raf.readFloat();
			float acc = raf.readFloat();
			byte boundary = raf.readByte();
			
			//Если это первая точка в линии
			long firstInLine = 0;
			
			// Ищем смещение первой точки в линии
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			
			if(attrSeek > 0) {
				h_raf.seek(attrSeek + Param.firsPointInWay_seek);
				firstInLine = h_raf.readLong();	
			}
			
			if(myselfSeek == firstInLine) {
				myselfSeek = src_seek;
				
				h_raf.seek(attrSeek + Param.firsPointInWay_seek);
				h_raf.writeLong(src_seek);
				
				isFirstInLine = true;
			} else {
				myselfSeek = src_seek;
			}
			
			// Записываем данные точки в нужное место
			// Тип элемента
			raf.seek(src_seek);			
			raf.writeByte(type);
			
			// Удаление
			raf.seek(src_seek + Param.delete_seek);			
			raf.writeByte(delete);
			
			// Идентификатор
			raf.seek(src_seek + Param.id_seek);	
			raf.writeLong(id);
			
			// Свое смещение
			raf.seek(src_seek + Param.myself_seek);	
			raf.writeLong(myselfSeek);
			
			// Смещение след элемента
			raf.seek(src_seek + Param.next_seek);	
			raf.writeLong(next);
			
			// Смещение атрибута линии
			raf.seek(src_seek + Param.attr_seek);	
			raf.writeLong(attrSeek);
			
			// Широта
			raf.seek(src_seek + Param.lat_seek);	
			raf.writeDouble(lat);
			
			// Долгота
			raf.seek(src_seek + Param.lon_seek);	
			raf.writeDouble(lon);
			
			// Высота над уровнем моря
			raf.seek(src_seek + Param.alt_seek);	
			raf.writeFloat(alt);
			
			// Точность
			raf.seek(src_seek + Param.acc_seek);	
			raf.writeFloat(acc);
			
			// Признак принадлежности элемента к ограничивающему контуру площадного объекта карты
			raf.seek(src_seek + Param.boundary_seek);		
			raf.writeByte(boundary);
			
			// Обновляем ссылку на точку в списке
			Param.seek_nodes.remove(id);
			Param.seek_nodes.put(id, myselfSeek);
						
			// Перезаписываем файл
			if(raf != null)
				raf.close();
			
			// Заново открываем файл для работы
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			// Обновляем ссылки на вставленную в новое место точку	
			
			if(type != Param.point) { // Если это не отдельная точка
				// Обновляем ссылки точек в линиях
				if(isFirstInLine) {
					// Обновляем ссылку на линию в списке
					Param.seek_ways.remove(id);
					Param.seek_ways.put(id, myselfSeek);				
				} else {
					if(firstInLine > 0) {
						raf.seek(firstInLine + Param.id_seek); // Идентификатор первой точки в линии
				
						ArrayList<Long> nodes_ids = new ArrayList<>(); // Идентификаторы всех точек линии
					
						nodes_ids.addAll(getNodeIdsFromWay(raf.readLong(), raf));
				
						// Записываем в предыдущую к нашей точке ссылку на нашу точку
						for(int i = 0; i < nodes_ids.size(); i++) {
							if(nodes_ids.size() > 1) {
								if(nodes_ids.get(i) == id) {
									if(i > 0) {
										raf.seek(Param.seek_nodes.get(nodes_ids.get(i - 1)) + Param.next_seek);
										raf.writeLong(src_seek);
									}
								}
							}
						}
					}
				}
			} else { // Если это отдельная точка
				// Обновляем ссылку на точку в списке
				Param.seek_nodes.remove(id);
				Param.seek_nodes.put(id, myselfSeek);
				
//				Param.seek_nodes_without_ways.remove(id);
//				Param.seek_nodes_without_ways.put(id, myselfSeek);
			}
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
	 * Удаляет точку из списка всех точек файла.
	 * 
	 * @param node_id идентификатор удаляемой точки
	 */
	
	private void deleteNodeFromList(long node_id) {	
		try {
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			long seek = Param.seek_nodes.get(node_id);
			
			raf.seek(seek + Param.attr_seek);
			long attrSeek = raf.readLong();
			
			h_raf.seek(attrSeek + Param.firsPointInWay_seek);
			long firstPointInWay = h_raf.readLong();
			
			if(seek == firstPointInWay && attrSeek > 0) {
				int index = Param.delete_attrs.indexOf(attrSeek);
				
				if(index == -1)
					Param.delete_attrs.add(attrSeek);
			}
			
			raf.seek(seek + Param.delete_seek);
			raf.writeByte(Param.delete);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(h_raf != null)
					raf.close();
				
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		Param.delete_nodes.put(node_id, Param.seek_nodes.get(node_id));
		Param.seek_nodes.remove(node_id);
		Param.seek_nodes_used.remove(node_id);
	}
	
	/**
	 * Удаляет точку из списка всех точек файла.
	 * 
	 * @param node_id идентификатор удаляемой точки
	 * @param raf объект для доступа к файлу
	 */
	
	private void deleteNodeFromList(long node_id, RandomAccessFile raf) {
		// Записываем признак удаления в свойства точки
		try {
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			
			long seek = Param.seek_nodes.get(node_id);
			
			raf.seek(seek + Param.attr_seek);
			long attrSeek = raf.readLong();
			
			h_raf.seek(attrSeek + Param.firsPointInWay_seek);
			long firstPointInWay = h_raf.readLong();
			
			if(seek == firstPointInWay && attrSeek > 0) {
				int index = Param.delete_attrs.indexOf(attrSeek);
				
				if(index == -1)
					Param.delete_attrs.add(attrSeek);
			}
			
			raf.seek(seek + Param.delete_seek);
			raf.writeByte(Param.delete);		
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(h_raf != null)
					h_raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		Param.delete_nodes.put(node_id, Param.seek_nodes.get(node_id));
		Param.seek_nodes.remove(node_id);
		Param.seek_nodes_used.remove(node_id);
	}
	
	/**
	 * Удаляет линию из списка всех линий файла.
	 * 
	 * @param way_id идентификатор удаляемой линии
	 */
	
	public void deleteWayFromList(long way_id) {
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
		
			// Точки, из которых состоит линия
			ArrayList<Long> nodes_ids = getNodeIdsFromWay(way_id, raf);
		
			// Если первая и последняя точки равны
			if(nodes_ids.get(0) == nodes_ids.get(nodes_ids.size() - 1))
				nodes_ids.remove(nodes_ids.size() - 1);
		
			for(int i = 0; i < nodes_ids.size(); i++) {				
				deleteNodeFromList(nodes_ids.get(i));
			}
		
			Param.seek_ways.remove(way_id);
			Param.seek_ways_used.remove(way_id);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Удаляет линию из списка всех линий файла.
	 * 
	 * @param way_id идентификатор удаляемой линии
	 * @param raf объект для доступа к файлу
	 */
	
	private void deleteWayFromList(long way_id, RandomAccessFile raf) {
		
		// Точки, из которых состоит линия
		ArrayList<Long> nodes_ids = getNodeIdsFromWay(way_id, raf);
		
		// Если первая и последняя точки равны
		if(nodes_ids.get(0) == nodes_ids.get(nodes_ids.size() - 1))
			nodes_ids.remove(nodes_ids.size() - 1);
		
		for(int i = 0; i < nodes_ids.size(); i++) {			
			deleteNodeFromList(nodes_ids.get(i), raf);
		}
		
		Param.seek_ways.remove(way_id);
		Param.seek_ways_used.remove(way_id);
	}
	
	/**
	 * Удаляет оставшиеся и неиспользованные в программе точки.
	 */
	
	public void deleteNodesAndWays() {
		try {
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			long waysCount = 0; // Счетчик линий для удаления
			long nodesCount = 0; // Счетчик точек для удаления
			
			// Список всех точек файла		
			ArrayList<Long> nodeSeeks = getAllPoints(raf);			
			// Список точек для удаления
			ArrayList<Long> nodesForDelete = new ArrayList<>();
			
			short typeOfObject = Param.noType;
			long attrSeek = 0;
			
			// Ищем точки без типа и с неизвестным типом объекта на карте
			for(int i = 0; i < nodeSeeks.size(); i++) {
				long tmp_seek = nodeSeeks.get(i);
				
				raf.seek(tmp_seek + Param.delete_seek);
				byte del = raf.readByte();
				
				if(del != Param.delete) {
					raf.seek(tmp_seek + Param.attr_seek);
					attrSeek = raf.readLong();
				
					if(attrSeek > 0) {
						h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
					
						h_raf.seek(attrSeek + Param.typeOfObject_seek);
						typeOfObject = h_raf.readShort();
					} else
						typeOfObject = Param.noType;
						
					if(typeOfObject == Param.noType || typeOfObject == Param.unknownType) { // Удаляем точку
						raf.seek(tmp_seek + Param.id_seek);
					
						nodesForDelete.add(raf.readLong());
						
						if(attrSeek > 0) {
							int index = Param.delete_attrs.indexOf(attrSeek);
							
							if(index == -1)
								Param.delete_attrs.add(attrSeek);
						}
						
						nodesCount++;
					}
				}				
			}
			
			// Удаляем из всех списков точки для удаления
			for(int i = 0; i < nodesForDelete.size(); i++)
				deleteNodeFromList(nodesForDelete.get(i), raf);
			
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			
			// Ищем линии без типа и с неизвестным типом объекта на карте
			if(Param.seek_ways != null && Param.seek_ways.size() != 0) {
				Collection<Long> ways_ids = Param.seek_ways.values();
				Iterator<Long> iterator_ways = ways_ids.iterator();
				
				// Список линий для удаления
				ArrayList<Long> waysForDelete = new ArrayList<>();
			
				typeOfObject = Param.noType;
				attrSeek = 0;
			
				// Перебираем линии
				while(iterator_ways.hasNext()) {
					long tmp_seek = iterator_ways.next();
				
					if(tmp_seek == 1352324) {
						int y = 0;
					}
					raf.seek(tmp_seek + Param.attr_seek);
					attrSeek = raf.readLong();
					
					if(attrSeek > 0) {
						h_raf.seek(attrSeek + Param.typeOfObject_seek);
						typeOfObject = h_raf.readShort();
					} else
						typeOfObject = Param.noType;
				
					raf.seek(tmp_seek);
					byte inner_polygon = raf.readByte();
					
					// Удаляем линию
					if((typeOfObject == Param.noType || typeOfObject == Param.unknownType) &&
						inner_polygon != Param.poligon_inner_composition) {
						raf.seek(tmp_seek + Param.id_seek);
						long id = raf.readLong();
						
						waysForDelete.add(id);
						
						if(attrSeek > 0) {
							int index = Param.delete_attrs.indexOf(attrSeek);
							
							if(index == -1)
								Param.delete_attrs.add(attrSeek);
						}
						
						waysCount++;
						
						// Проверяем у удаляемого внешнего полигона наличие внутренних полигонов, 
						// если они есть - удаляем их
						if(Param.ids_outer_ways_with_inner_ways.get(id) != null) {							
							ArrayList<Long> tmp_inner_ids = Param.ids_outer_ways_with_inner_ways.get(id);
							Param.ids_outer_ways_with_inner_ways.remove(id);
							
							for(int i = 0; i < tmp_inner_ids.size(); i++) {
								long inner_id = tmp_inner_ids.get(i);
								
								waysForDelete.add(inner_id);
								
								raf.seek(Param.seek_ways.get(inner_id) + Param.attr_seek);
								attrSeek = raf.readLong();
								
								if(attrSeek > 0) {
									int index = Param.delete_attrs.indexOf(attrSeek);
									
									if(index == -1)
										Param.delete_attrs.add(attrSeek);
								}
								
								waysCount++;
							}
						}
					}
				}
				
				// Удаляем из всех списков линии для удаления
				for(int i = 0; i < waysForDelete.size(); i++) {
					deleteWayFromList(waysForDelete.get(i), raf);
				}
			}
			
			OsmConverter.printLog("Количество отдельных точек для удаления: " + nodesCount);
			OsmConverter.printLog("Количество линий для удаления: " + waysCount);
			OsmConverter.printLog("Общее количество точек для удаления: " + Param.delete_nodes.size());
			
			// Физическое удаление точек из файла карты
			deleteNodes();
			
			OsmConverter.printLog("Точки удалены: " + Param.delete_nodes.size());
			OsmConverter.printLog("Количество атрибутов до удаления: " + Param.attrs.size());
			OsmConverter.printLog("Количество атрибутов для удаления: " + Param.delete_attrs.size());
			
			// Физическое удаление атрибутов точек карты
			deleteAttrs();
			
			OsmConverter.printLog("Атрибуты удалены: " + Param.delete_attrs.size());
			OsmConverter.printLog("Количество атрибутов после удаления: " + Param.attrs.size());
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
	 * Перебирает все точки в линии и возвращает смещение последней.
	 * 
	 * @param seek смещение точки в линии
	 * @param raf объект для доступа к файлу
	 * @return возвращает смещение последней точки
	 */
	
	private long getSeekLastPointWay(long seek, RandomAccessFile raf) {
		long lastSeek = seek;
		
		try {
			raf.seek(seek + Param.next_seek);  // Значение смещения следующего элемента в линии
			long nextSeek = raf.readLong(); // Смещение следующего элемента
			
			if(nextSeek != 0)  {
				lastSeek = getSeekLastPointWay(nextSeek, raf);
			} 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return lastSeek;
	}
	
	/**
	 * Перебирает все точки в линии и возвращает смещения всех этих точек.
	 * 
	 * @param id идентификатор линии
	 * @param raf объект для доступа к файлу
	 * @return возвращает смещения всех точек в линии
	 */
	
	public ArrayList<Long> getSeekAllPointsInWay(long id, RandomAccessFile raf) {	
		ArrayList<Long> allSeeks = new ArrayList<>();
		
		try {
			long seek = Param.new_seek_nodes.get(id);
			
			while(seek != 0) {
				allSeeks.add(seek);
				
				raf.seek(seek + Param.next_seek);  
				seek = raf.readLong(); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return allSeeks;
	}
	
	/**
	 * Возвращает тип объекта на карте.
	 * 
	 * @param seek смещение в файле карты
	 * @return тип объекта на карте
	 */
	
	public short getTypeOfObject(long seek) {
		short typeOfObject = 0;
		
		try {
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			raf.seek(seek + Param.attr_seek);
			long attrSeek = raf.readLong();
			
			if(attrSeek > 0) {
				h_raf.seek(attrSeek + Param.typeOfObject_seek);
				typeOfObject = h_raf.readShort();
			}
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
		
		return typeOfObject;
	}
	
	/**
	 * Возвращает тип элемента карты.
	 * 
	 * @param id идентификатор точки
	 * @return тип элемента карты
	 */
	
	public byte getType(long id) {
		byte type = 0;
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			raf.seek(Param.seek_nodes.get(id));
			type = raf.readByte();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return type;
	}
		
	/**
	 *  Проверяем линии на наличие общих точек.
	 *  
	 * @param wayId идентификаторы линий в отношении
	 * @param nextWayId смещения линий в файле карты
	 * @return если линии имеют общие точки - true, иначе - false
	 */
	
	public boolean checkNodesIdInWays(long wayId, long nextWayId) {
		boolean check = false; // Признак наличия общих точек у линий
		boolean tmp_check1 = false, tmp_check2 = false, tmp_check3 = false, tmp_check4 = false;
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			ArrayList<Long> nodes_ids_way_1 = new ArrayList<>(); // Идентификаторы точек первой линии в паре для сравнивания
			ArrayList<Long> nodes_ids_way_2 = new ArrayList<>(); // Идентификаторы точек второй линии в паре для сравнивания
			
			nodes_ids_way_1.addAll(getNodeIdsFromWay(wayId, raf));
			nodes_ids_way_2.addAll(getNodeIdsFromWay(nextWayId, raf));
			
			// Первая - первая
			tmp_check1 = compareCoordsNodes(nodes_ids_way_1.get(0), nodes_ids_way_2.get(0), raf);
			
			// Первая - последняя
			tmp_check2 = compareCoordsNodes(nodes_ids_way_1.get(0), nodes_ids_way_2.get(nodes_ids_way_2.size() - 1), raf);
			
			// Последняя - первая
			tmp_check3 = compareCoordsNodes(nodes_ids_way_1.get(nodes_ids_way_1.size() - 1), nodes_ids_way_2.get(0), raf);
			
			// Последняя - последняя
			tmp_check4 = compareCoordsNodes(nodes_ids_way_1.get(nodes_ids_way_1.size() - 1), nodes_ids_way_2.get(nodes_ids_way_2.size() - 1), raf);
			
			check = (tmp_check1 || tmp_check2 || tmp_check3 || tmp_check4) ? true : false;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return check;
	}
	
	/**
	 *  Проверяем линии на наличие общих точек для определенных, с помощью перечисления,
	 *  точек линий.
	 *  
	 * @param wayId идентификаторы линий в отношении
	 * @param nextWayId смещения линий в файле карты
	 * @param sort обозначает, какие точки в отдельных линиях сравниваются
	 * @return если линии имеют общие точки - true, иначе - false
	 */
	
	public boolean checkNodesIdInWays(long wayId, long nextWayId, Param.sort_points sort) {
		boolean check = false; // Признак наличия общих точек у линий
		boolean tmp_check1 = false, tmp_check2 = false, tmp_check3 = false, tmp_check4 = false;
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			ArrayList<Long> nodes_ids_way_1 = new ArrayList<>(); // Идентификаторы точек первой линии в паре для сравнивания
			ArrayList<Long> nodes_ids_way_2 = new ArrayList<>(); // Идентификаторы точек второй линии в паре для сравнивания
			
			nodes_ids_way_1.addAll(getNodeIdsFromWay(wayId, raf));
			nodes_ids_way_2.addAll(getNodeIdsFromWay(nextWayId, raf));
			
			// Первая - первая
			if(sort == Param.sort_points.first_first)
				tmp_check1 = compareCoordsNodes(nodes_ids_way_1.get(0), nodes_ids_way_2.get(0), raf);
			
			// Первая - последняя
			if(sort == Param.sort_points.first_last)
				tmp_check2 = compareCoordsNodes(nodes_ids_way_1.get(0), nodes_ids_way_2.get(nodes_ids_way_2.size() - 1), raf);
			
			// Последняя - первая
			if(sort == Param.sort_points.last_first)
				tmp_check3 = compareCoordsNodes(nodes_ids_way_1.get(nodes_ids_way_1.size() - 1), nodes_ids_way_2.get(0), raf);
			
			// Последняя - последняя
			if(sort == Param.sort_points.last_last)
				tmp_check4 = compareCoordsNodes(nodes_ids_way_1.get(nodes_ids_way_1.size() - 1), nodes_ids_way_2.get(nodes_ids_way_2.size() - 1), raf);
			
			check = (tmp_check1 || tmp_check2 || tmp_check3 || tmp_check4) ? true : false;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return check;
	}
	
	/**
	 * Принимает массив связанных м/д собой линий и проверяет не замыкается ли
	 * он сам на себе (т.е. общая линия построенная из линий массива не замыкается ли).
	 * 
	 * @param array массив линий
	 * @return если замыкается - true, иначе - false
	 */
	
	public boolean checkNodesIdInWaysFirstAndLastPoints(ArrayList<Long> array) {
		boolean check = false; // Признак наличия общих точек у линий
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			ArrayList<Long> nodes_ids_way_1 = new ArrayList<>(); // Идентификаторы точек первой линии в паре для сравнивания
			ArrayList<Long> nodes_ids_way_2 = new ArrayList<>(); // Идентификаторы точек второй линии в паре для сравнивания
			
			nodes_ids_way_1.addAll(getNodeIdsFromWay(array.get(0), raf));
			nodes_ids_way_2.addAll(getNodeIdsFromWay(array.get(array.size() - 1), raf));
			
			// Первая - последняя		
			check = compareCoordsNodes(nodes_ids_way_1.get(0), nodes_ids_way_2.get(nodes_ids_way_2.size() - 1), raf);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return check;
	}
	
	/**
	 * Проверяет отдельные внешние полигоны на наличие общих точек.
	 * 
	 * @param outerWaysArray массив отдельных внешних полигонов
	 * @param incomplete признак неполного отношения
	 * @return новый массив отдельных внешних полигонов
	 */
	
	public ArrayList<ArrayList<Long>> checkNodesIdInOuterWays(ArrayList<ArrayList<Long>> outerWaysArray) {
		boolean tmp_check1 = false, tmp_check2 = false, tmp_check3 = false, tmp_check4 = false;
		
		// Упорядочиваем внешние полигоны
		sortWays(outerWaysArray);
					
		for(int i = 0; i < outerWaysArray.size() - 1; i++) {
			tmp_check1 = false;
			tmp_check2 = false;
			tmp_check3 = false;
			tmp_check4 = false;
			
			// Первая - первая
			tmp_check1 = checkNodesIdInWays(outerWaysArray.get(i).get(0), outerWaysArray.get(i + 1).get(0), 
					                        Param.sort_points.first_first);
			
			// Первая - последняя
			tmp_check2 = checkNodesIdInWays(outerWaysArray.get(i).get(0), 
				                            outerWaysArray.get(i + 1).get(outerWaysArray.get(i + 1).size() - 1),
				                            Param.sort_points.first_last);
			
			// Последняя - первая
			tmp_check3 = checkNodesIdInWays(outerWaysArray.get(i).get(outerWaysArray.get(i).size() - 1), 
				                            outerWaysArray.get(i + 1).get(0),
				                            Param.sort_points.last_first);			
			
			// Последняя - последняя
			tmp_check4 = checkNodesIdInWays(outerWaysArray.get(i).get(outerWaysArray.get(i).size() - 1), 
				                            outerWaysArray.get(i + 1).get(outerWaysArray.get(i + 1).size() - 1),
				                            Param.sort_points.last_last);
						
			if(tmp_check1) {
				if(outerWaysArray.size() > 1) {
					ArrayList<Long> tmp_ways = new ArrayList<>();
				
				    for(int j = outerWaysArray.get(i).size() - 1; j >= 0; j--)
				        tmp_ways.add(outerWaysArray.get(i).get(j));
				
				    tmp_ways.addAll(outerWaysArray.get(i + 1));
				
				    outerWaysArray.remove(i);
				    outerWaysArray.remove(i);
    				outerWaysArray.add(tmp_ways);
    				
    				if(outerWaysArray.size() >= 2) {
    					// Упорядочиваем внешние полигоны
    					sortWays(outerWaysArray);
    					
    					i-= 2;
    					
    					if(i == -2)
    						i = -1;
    				}
				}
				continue;
			}
			
			if(tmp_check2) {
				if(outerWaysArray.size() > 1) {
    				ArrayList<Long> tmp_ways = new ArrayList<>();
				
    				for(int j = outerWaysArray.get(i).size() - 1; j >= 0; j--)
	    			    tmp_ways.add(outerWaysArray.get(i).get(j));
				
	    			for(int j = outerWaysArray.get(i + 1).size() - 1; j >= 0; j--)
	    			    tmp_ways.add(outerWaysArray.get(i + 1).get(j));
				
	    			outerWaysArray.remove(i);
	    			outerWaysArray.remove(i);
	    			outerWaysArray.add(tmp_ways);
	    			
	    			if(outerWaysArray.size() >= 2) {
    					// Упорядочиваем внешние полигоны
    					sortWays(outerWaysArray);
    					
    					i-= 2;
    					
    					if(i == -2)
    						i = -1;
    				}
				}
				continue;
			}
			
			if(tmp_check3) {
				if(outerWaysArray.size() > 1) {
    				ArrayList<Long> tmp_ways = new ArrayList<>();
	    			tmp_ways.addAll(outerWaysArray.get(i));
	    			tmp_ways.addAll(outerWaysArray.get(i + 1));
				
	    			outerWaysArray.remove(i);
	    			outerWaysArray.remove(i);
	    			outerWaysArray.add(tmp_ways);
	    			
	    			if(outerWaysArray.size() >= 2) {
    					// Упорядочиваем внешние полигоны
    					sortWays(outerWaysArray);
    					
    					i-= 2;
    					
    					if(i == -2)
    						i = -1;
    				}
				}
				continue;
			}
			
			if(tmp_check4) {
				if(outerWaysArray.size() > 1) {
    				ArrayList<Long> tmp_ways = new ArrayList<>();
	    			tmp_ways.addAll(outerWaysArray.get(i));
				
    				for(int j = outerWaysArray.get(i + 1).size() - 1; j >= 0; j--)
	    			    tmp_ways.add(outerWaysArray.get(i + 1).get(j));				
				
	    			outerWaysArray.remove(i);
	    			outerWaysArray.remove(i);
	    			outerWaysArray.add(tmp_ways);
	    			
	    			if(outerWaysArray.size() >= 2) {
    					// Упорядочиваем внешние полигоны
    					sortWays(outerWaysArray);
    					
    					i-= 2;
    					
    					if(i == -2)
    						i = -1;
    				}
				}
				continue;
			}
		}
			
		return outerWaysArray;
	}
	
	/**
	 * Проверяет неполные полигоны на принадлежность к одному объекту карты и, 
	 * если это так, строит из этих полигонов один новый полигон (полный). 
	 * 
	 * @param outerWaysArray массив отдельных внешних полигонов
	 * @return новый массив отдельных внешних полигонов
	 */
	
	public ArrayList<ArrayList<Long>> checkNodesIdInIncompleteOuterWays(ArrayList<ArrayList<Long>> outerWaysArray) {
		// Если отдельные внешние полигоны принадлежат одному отношению, отношение не полное, 
		// каждый отдельный внешний полигон не замыкается сам на себя, то создаем новый полигон
		// из имеющихся с добавлением новых связующих линий
		if(outerWaysArray.size() > 1) {
			// Хранит вычисленное наименьшее расстояние и пару идентификаторов точек, м/д которыми 
			// наименьшее расстояние ([length, id_point1, id_point2])
			double[][] tmp_length = new double[4][3]; 
			ArrayList<Long> tmp_ways = null;
			
			int size = outerWaysArray.size() - 1;
			
			for(int i = 0; i < size; i++) {
				// Если два соседних полигона не замкнуты сами на себя
				if(!checkNodesIdInWaysFirstAndLastPoints(outerWaysArray.get(i)) &&
				   !checkNodesIdInWaysFirstAndLastPoints(outerWaysArray.get(i + 1))) {
					
					// Ищем сторону, где края линий ближе друг к другу.
					// Не забываем, что линии в массивах уже отсортированы.
					double length = Double.MAX_VALUE; // Наименьшее расстояние между линиями
					
					// Первые и последние линии
					long way_id_1_first = outerWaysArray.get(i).get(0);
					long way_id_1_last = outerWaysArray.get(i).get(outerWaysArray.get(i).size() - 1);
					long way_id_2_first = outerWaysArray.get(i + 1).get(0);
					long way_id_2_last = outerWaysArray.get(i + 1).get(outerWaysArray.get(i + 1).size() - 1);
					// Соседние линии первых и последних линий
					long way_id_1_first_adjacent = 0;
					long way_id_1_last_adjacent = 0;
					long way_id_2_first_adjacent = 0;
					long way_id_2_last_adjacent = 0;
					
					// Размер массивов с идентификаторами линий объединенных в одну общую линию
					int size1 = outerWaysArray.get(i).size();
					int size2 = outerWaysArray.get(i + 1).size();
					
					// Если общая линия состоит из более чем одной линии, то берем соседние линии
					if(size1 > 1) {
						way_id_1_first_adjacent = outerWaysArray.get(i).get(1);
						way_id_1_last_adjacent = outerWaysArray.get(i).get(outerWaysArray.get(i).size() - 2);
					}
					
					if(size2 > 1) {
						way_id_2_first_adjacent = outerWaysArray.get(i + 1).get(1);
						way_id_2_last_adjacent = outerWaysArray.get(i + 1).get(outerWaysArray.get(i + 1).size() - 2);
					}
					
					// Первая - первая
					tmp_length[0] = checkLength(way_id_1_first, way_id_1_first_adjacent,
							                    way_id_2_first, way_id_2_first_adjacent);
										
					// Первая - последняя
					tmp_length[1] = checkLength(way_id_1_first, way_id_1_first_adjacent,
		                                        way_id_2_last, way_id_2_last_adjacent);
					
					// Последняя - первая
					tmp_length[2] = checkLength(way_id_1_last, way_id_1_last_adjacent,
		                                        way_id_2_first, way_id_2_first_adjacent);			
					
					// Последняя - последняя
					tmp_length[3] = checkLength(way_id_1_last, way_id_1_last_adjacent,
		                                        way_id_2_last, way_id_2_last_adjacent);
					
					int index = -1; // Индекс наименьшего расстояния в массиве (tmp_length[0][0])
					
					for(int j = 0; j < 4; j++) {
						if(tmp_length[j][0] < length) {
							length = tmp_length[j][0];
							index = j;
						}
					}
					
					if(outerWaysArray.size() > 1) {						
						switch(index) {
						case 0: // Первая - первая
						case 3: // Последняя - последняя
							tmp_ways = new ArrayList<>();
							
    						for(int j = outerWaysArray.get(i).size() - 1; j >= 0; j--)
    		    			    tmp_ways.add(outerWaysArray.get(i).get(j));	
						
    						tmp_ways.add(createNewEmptyWayFromPoints((long) tmp_length[index][1], (long) tmp_length[index][2]));
	    					tmp_ways.addAll(outerWaysArray.get(i + 1));
					
		    			    outerWaysArray.remove(i);
		    			    outerWaysArray.remove(i);
		     				outerWaysArray.add(tmp_ways);
							break;
						case 1: // Последняя - первая
						case 2: // Первая - последняя
							tmp_ways = new ArrayList<>();
							
		    				tmp_ways.addAll(outerWaysArray.get(i));
		    				tmp_ways.add(createNewEmptyWayFromPoints((long) tmp_length[index][1], (long) tmp_length[index][2]));
	    					tmp_ways.addAll(outerWaysArray.get(i + 1));
					
				    	    outerWaysArray.remove(i);
			    		    outerWaysArray.remove(i);
		   	    			outerWaysArray.add(tmp_ways);
							break;
						}
						
						size = outerWaysArray.size() - 1; // Переопределяем размер массива массивов
						i = -1;
					} 
				}
			}
			
			sortWays(outerWaysArray);
			
			// После соединения всех линий в один массив создаем замыкающую линию
			tmp_length[0] = checkLengthForOneWay(outerWaysArray.get(0));
			
/*			double lon1 = getLongitude((long) tmp_length[0][1]);
			double lat1 = getLatitude((long) tmp_length[0][1]);
			double lon2 = getLongitude((long) tmp_length[0][2]);
			double lat2 = getLatitude((long) tmp_length[0][2]);*/
			
			tmp_ways = new ArrayList<>();
			
			tmp_ways.addAll(outerWaysArray.get(0));
			tmp_ways.add(createNewEmptyWayFromPoints((long) tmp_length[0][1], (long) tmp_length[0][2]));
	
    	    outerWaysArray.clear();
   			outerWaysArray.add(tmp_ways);
		}
		
		return outerWaysArray;
	}
	
	/**
	 * Проверяем линии на наличие двух соседних точек в линии с одинаковыми координатами,
	 * и если такие есть, то заносим смещение первой точки в линии в список для дальнейшего
	 * удаление одной из этих точек из линии.
	 * 
	 * @param seek смещение первой точки в линии
	 */
	
	public void checkDublicatCoords(long seek) {
		// Первая точка линии равна соседней точке линии
		boolean twins = false; 
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			raf.seek(seek + Param.id_seek);
			long way_id = raf.readLong();
			
			ArrayList<Long> nodes_ids_way = new ArrayList<>(); // Идентификаторы точек линии
			nodes_ids_way.addAll(getNodeIdsFromWay(way_id, raf));
			
			for(int i = 0; i < nodes_ids_way.size() - 1; i++) {
				twins = compareCoordsNodes(nodes_ids_way.get(i), 
		                                   nodes_ids_way.get(i + 1), raf);
				if(twins) {
					if(Param.ids_ways_with_dublicat_coords.indexOf(way_id) == -1) // Если нет в списке
						Param.ids_ways_with_dublicat_coords.add(way_id);
					
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Сравнивает последовательно координаты соседних двух точек в линии,
	 * и если они равны, удаляет одну из этих точек.
	 */
	
	public void deleteDublicatCoordsNodesInWay() {
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			for(int i = 0; i < Param.ids_ways_with_dublicat_coords.size(); i++) {
				long way_id = Param.ids_ways_with_dublicat_coords.get(i);
				
				ArrayList<Long> nodes_ids_way = new ArrayList<>(); // Идентификаторы точек линии
				nodes_ids_way.addAll(getNodeIdsFromWay(way_id, raf));
				
				// Первая точка линии равна соседней точке линии
				boolean twins = false; 
				
				if(nodes_ids_way.size() == 2) {
					twins = compareCoordsNodes(nodes_ids_way.get(0), 
	                                           nodes_ids_way.get(nodes_ids_way.size() - 1), raf);
					if(twins) {
						// Обнуляем ссылку на последнюю точку линии в предыдущей ее точке
						raf.seek(Param.seek_nodes.get(nodes_ids_way.get(0) + Param.next_seek));
						raf.writeLong(0);
						
						// Подготавливаем к удалению последнюю точку
						deleteNodeFromList(nodes_ids_way.get(nodes_ids_way.size() - 1), raf);
						
						// Удаляем последнюю точку линии из списка
						nodes_ids_way.remove(nodes_ids_way.size() - 1);
						// Изменяем количество точек в линии в списке
						Param.num_points.remove(way_id);
						Param.num_points.put(way_id, nodes_ids_way.size());
					}
				} else if (nodes_ids_way.size() >= 3) {
					for(int j = 0; j < nodes_ids_way.size() - 1; j++) {
						twins = compareCoordsNodes(nodes_ids_way.get(j), 
				                                   nodes_ids_way.get(j + 1), raf);
						if(twins) {
							// Считываем смещение точки находящейся за соседней точкой
							raf.seek(Param.seek_nodes.get(nodes_ids_way.get(j + 1)) + Param.next_seek);
							long nextSeek = raf.readLong();
							
							// Записываем новое значение смещение сделующей точки в первую сравниваемую точку
							raf.seek(Param.seek_nodes.get(nodes_ids_way.get(j)) + Param.next_seek);
							raf.writeLong(nextSeek);
							
							// Подготавливаем к удалению последнюю точку
							deleteNodeFromList(nodes_ids_way.get(j + 1), raf);
							
							// Удаляем последнюю точку линии из списка
							nodes_ids_way.remove(j + 1);
							// Изменяем количество точек в линии в списке
							Param.num_points.remove(way_id);
							Param.num_points.put(way_id, nodes_ids_way.size());
							
							j = -1;
						}
					}
				}
			}
			
			Param.ids_ways_with_dublicat_coords.clear();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Создает новую линию по двум заданным точкам.
	 * 
	 * @param node_id_1 идентификатор первой точки
	 * @param node_id_2 идентификатор второй точки
	 * @return идентификатор новой линии
	 */
	
	public long createNewEmptyWayFromPoints(long node_id_1, long node_id_2) {
		long id = -1; // Идентификатор новой линии
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			ArrayList<Long> tmp_array = new ArrayList<>();
			tmp_array.add(node_id_1);
			tmp_array.add(node_id_2);
			
			long seek = createNewWay(tmp_array);
			
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			raf.seek(seek + Param.id_seek);
			id = raf.readLong();
			
			Param.seek_ways_used.put(id, Param.seek_ways.get(id));		
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return id;
	}
	
	/**
	 * Создает новую линию. Берет последнюю точку первой линии и первую точку
	 * второй линии.
	 * 
	 * @param way_id_1 первая линия
	 * @param way_id_2 вторая линия
	 * @return идентификатор новой линии
	 */
	
	public long createNewEmptyWay(long way_id_1, long way_id_2) {
		long id = -1; // Идентификатор новой линии
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			ArrayList<Long> nodes_ids_way_1 = new ArrayList<>(); // Идентификаторы точек первой линии
			ArrayList<Long> nodes_ids_way_2 = new ArrayList<>(); // Идентификаторы точек второй линии
			
			nodes_ids_way_1.addAll(getNodeIdsFromWay(way_id_1, raf));
			nodes_ids_way_2.addAll(getNodeIdsFromWay(way_id_2, raf));
			
			ArrayList<Long> tmp_array = new ArrayList<>();
			tmp_array.add(nodes_ids_way_1.get(nodes_ids_way_1.size() - 1));
			tmp_array.add(nodes_ids_way_2.get(0));
			
			long seek = createNewWay(tmp_array);
			
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			raf.seek(seek + Param.id_seek);
			id = raf.readLong();
			
			Param.seek_ways_used.put(id, Param.seek_ways.get(id));
//			Param.seek_ways_used_in_relations.put(id, Param.seek_ways.get(id));			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return id;
	}
	
	/**
	 * Ищет наименьшее расстояние м/д крайними точками одной общей линии и возвращает
	 * это расстояние с идентификаторами этих точек.
	 * 
	 * @param outerWaysArray массив идентификаторов линий, из которых состоит одна общая линия
	 * @return наименьшее расстояние м/д крайними точками одной общей линии с идентификаторами этих точек
	 */
	
	private double[] checkLengthForOneWay(ArrayList<Long> outerWaysArray) {
		// Расстояние м/д точками и идентификаторы двух точек для постройки двух линий
		double [] length = { Double.MAX_VALUE, 0, 0 }; 
		// Точки, по которым строят дополнительную линию (их возвращает ф-ция)
		long id_point_1 = 0; 
		long id_point_2 = 0;
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			ArrayList<Long> nodes_ids_way_first = new ArrayList<Long>(); // Идентификаторы точек первой линии
			ArrayList<Long> nodes_ids_way_first_adjacent = null; // Идентификаторы точек соседней к первой линии
			
			ArrayList<Long> nodes_ids_way_last = new ArrayList<Long>(); // Идентификаторы точек последней линии
			ArrayList<Long> nodes_ids_way_last_adjacent = null; // Идентификаторы точек соседней к последней линии
			
			nodes_ids_way_first.addAll(getNodeIdsFromWay(outerWaysArray.get(0), raf));
			
			if(outerWaysArray.size() == 1) {
				id_point_1 = nodes_ids_way_first.get(nodes_ids_way_first.size() - 1);
				id_point_2 = nodes_ids_way_first.get(0);
			} else {
				nodes_ids_way_first_adjacent = new ArrayList<Long>();
				nodes_ids_way_first_adjacent.addAll(getNodeIdsFromWay(outerWaysArray.get(1), raf));
				
				nodes_ids_way_last.addAll(getNodeIdsFromWay(outerWaysArray.get(outerWaysArray.size() - 1), raf));
				nodes_ids_way_last_adjacent = new ArrayList<Long>();
				nodes_ids_way_last_adjacent.addAll(getNodeIdsFromWay(outerWaysArray.get(outerWaysArray.size() - 2), raf));
				
				// Ищем крайние точки в общей линии
				// Первая линия в общей линии
				boolean cmp_first_first = compareCoordsNodes(nodes_ids_way_first.get(0), 
						                                     nodes_ids_way_first_adjacent.get(0), raf);
				boolean cmp_first_last = compareCoordsNodes(nodes_ids_way_first.get(0),
						                                    nodes_ids_way_first_adjacent.get(nodes_ids_way_first_adjacent.size() - 1), raf);

				boolean cmp_last_first = compareCoordsNodes(nodes_ids_way_first.get(nodes_ids_way_first.size() - 1),
						                                    nodes_ids_way_first_adjacent.get(0), raf);
				boolean cmp_last_last = compareCoordsNodes(nodes_ids_way_first.get(nodes_ids_way_first.size() - 1),
						                                   nodes_ids_way_first_adjacent.get(nodes_ids_way_first_adjacent.size() - 1), raf);
				boolean cmp_point1 = (cmp_first_first | cmp_first_last);
				boolean cmp_point2 = (cmp_last_first | cmp_last_last);

				if(!cmp_point1)
					id_point_1 = nodes_ids_way_first.get(0);
				else if(!cmp_point2)
					id_point_1 = nodes_ids_way_first.get(nodes_ids_way_first.size() - 1);
				
				// Вторая линия в общей линии
				cmp_first_first = compareCoordsNodes(nodes_ids_way_last.get(0), 
                                                     nodes_ids_way_last_adjacent.get(0), raf);
				cmp_first_last = compareCoordsNodes(nodes_ids_way_last.get(0),
						                            nodes_ids_way_last_adjacent.get(nodes_ids_way_last_adjacent.size() - 1), raf);

				cmp_last_first = compareCoordsNodes(nodes_ids_way_last.get(nodes_ids_way_last.size() - 1),
						                            nodes_ids_way_last_adjacent.get(0), raf);
				cmp_last_last = compareCoordsNodes(nodes_ids_way_last.get(nodes_ids_way_last.size() - 1),
						                           nodes_ids_way_last_adjacent.get(nodes_ids_way_last_adjacent.size() - 1), raf);
				cmp_point1 = (cmp_first_first | cmp_first_last);
				cmp_point2 = (cmp_last_first | cmp_last_last);

				if(!cmp_point1)
					id_point_2 = nodes_ids_way_last.get(0);
				else if(!cmp_point2)
					id_point_2 = nodes_ids_way_last.get(nodes_ids_way_last.size() - 1);
			}
			
			// Координаты точек для вычисления расстояния
			double x1 = getLongitude(id_point_1);
			double y1 = getLatitude(id_point_1);
			
			double x2 = getLongitude(id_point_2);
			double y2 = getLatitude(id_point_2);
		
			// Нахоим расстояние м/д точками и записываем идентификаторы этих точек
			length[0] = Math.abs(Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2)));
			length[1] = id_point_2;
			length[2] = id_point_1;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return length;
	}
	
	/**
	 * Ищет наименьшее расстояние м/д крайними точками двух линий и возвращает
	 * это расстояние с идентификаторами этих точек.
	 * 
	 * @param way_id_1 линия первая
	 * @param way_id_1_adjacent соседняя к первой линия
	 * @param way_id_2 линия вторая
	 * @param way_id_2_adjacent соседняя ко второй линия
	 * @return наименьшее расстояние м/д крайними точками двух линий с идентификаторами этих точек
	 */
	
	private double[] checkLength(long way_id_1, long way_id_1_adjacent, 
			                     long way_id_2, long way_id_2_adjacent) {
		// Расстояние м/д точками и идентификаторы двух точек для постройки двух линий
		double [] length = { Double.MAX_VALUE, 0, 0 }; 
		// Точки, по которым строят дополнительную линию (их возвращает ф-ция)
		long id_point_1 = 0; 
		long id_point_2 = 0;
		
		// Если в ф-ции не заданны соседние линии, то значит общая линия состоит из одной линии.
		// Чтобы вычислить наименьшее расстояние м/д двумя общими линиями нужно измерить его
		// м/д каждой из точек. Тут заданны переменные на этот случай
		long id_point_1_1 = 0; // Первая точка первой линии
		long id_point_1_2 = 0; // Вторая точка первой линии
		long id_point_2_1 = 0; // Первая точка второй линии
		long id_point_2_2 = 0; // Вторая точка второй линии
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			ArrayList<Long> nodes_ids_way_1 = new ArrayList<Long>(); // Идентификаторы точек первой линии
			ArrayList<Long> nodes_ids_way_1_adjacent = null; // Идентификаторы точек соседней к первой линии
			
			ArrayList<Long> nodes_ids_way_2 = new ArrayList<Long>(); // Идентификаторы точек второй линии
			ArrayList<Long> nodes_ids_way_2_adjacent = null; // Идентификаторы точек соседней ко второй линии
			
			if(way_id_1 != 0)
				nodes_ids_way_1.addAll(getNodeIdsFromWay(way_id_1, raf));
			
			if(way_id_2 != 0)
				nodes_ids_way_2.addAll(getNodeIdsFromWay(way_id_2, raf));
			
			// Если у заданных линий имеются соседние, то находим свободные (крайние) точки заданных линий
			if(way_id_1 != 0) {
				if(way_id_1_adjacent != 0) {
					nodes_ids_way_1_adjacent = new ArrayList<>();
					nodes_ids_way_1_adjacent.addAll(getNodeIdsFromWay(way_id_1_adjacent, raf));
					
					boolean cmp_first_first = compareCoordsNodes(nodes_ids_way_1.get(0), 
							                                     nodes_ids_way_1_adjacent.get(0), raf);
					boolean cmp_first_last = compareCoordsNodes(nodes_ids_way_1.get(0),
							                                    nodes_ids_way_1_adjacent.get(nodes_ids_way_1_adjacent.size() - 1), raf);
					
					boolean cmp_last_first = compareCoordsNodes(nodes_ids_way_1.get(nodes_ids_way_1.size() - 1),
							                                    nodes_ids_way_1_adjacent.get(0), raf);
					boolean cmp_last_last = compareCoordsNodes(nodes_ids_way_1.get(nodes_ids_way_1.size() - 1),
							                                   nodes_ids_way_1_adjacent.get(nodes_ids_way_1_adjacent.size() - 1), raf);
					boolean cmp_point1 = (cmp_first_first | cmp_first_last);
					boolean cmp_point2 = (cmp_last_first | cmp_last_last);
					
					if(!cmp_point1)
						id_point_1 = nodes_ids_way_1.get(0);
					else if(!cmp_point2)
						id_point_1 = nodes_ids_way_1.get(nodes_ids_way_1.size() - 1);
				} else {
					id_point_1_1 = nodes_ids_way_1.get(0);
					id_point_1_2 = nodes_ids_way_1.get(nodes_ids_way_1.size() - 1);
				}
			}
			
			if(way_id_2 != 0) {
				if(way_id_2_adjacent != 0) {
					nodes_ids_way_2_adjacent = new ArrayList<>();
					nodes_ids_way_2_adjacent.addAll(getNodeIdsFromWay(way_id_2_adjacent, raf));
					
					boolean cmp_first_first = compareCoordsNodes(nodes_ids_way_2.get(0), 
	                                                             nodes_ids_way_2_adjacent.get(0), raf);
					boolean cmp_first_last = compareCoordsNodes(nodes_ids_way_2.get(0),
	                                                            nodes_ids_way_2_adjacent.get(nodes_ids_way_2_adjacent.size() - 1), raf);

					boolean cmp_last_first = compareCoordsNodes(nodes_ids_way_2.get(nodes_ids_way_2.size() - 1),
	                                                            nodes_ids_way_2_adjacent.get(0), raf);
					boolean cmp_last_last = compareCoordsNodes(nodes_ids_way_2.get(nodes_ids_way_2.size() - 1),
	                                                           nodes_ids_way_2_adjacent.get(nodes_ids_way_2_adjacent.size() - 1), raf);

					boolean cmp_point1 = (cmp_first_first | cmp_first_last);
					boolean cmp_point2 = (cmp_last_first | cmp_last_last);
					
					if(!cmp_point1)
						id_point_2 = nodes_ids_way_2.get(0);
					else if(!cmp_point2)
						id_point_2 = nodes_ids_way_2.get(nodes_ids_way_2.size() - 1);
				} else {
					id_point_2_1 = nodes_ids_way_2.get(0);
					id_point_2_2 = nodes_ids_way_2.get(nodes_ids_way_2.size() - 1);
				}
			}
			
			// Координаты точек для вычисления расстояния
			double x1 = 0;
			double y1 = 0;
			double x2 = 0;
			double y2 = 0;
			
			if(id_point_1 == 0 && id_point_2 == 0) { // Общие линии состоят каждая из одной линии
				double x11 = getLongitude(id_point_1_1);
				double y11 = getLatitude(id_point_1_1);	
				double x12 = getLongitude(id_point_1_2);
				double y12 = getLatitude(id_point_1_2);
				
				double x21 = getLongitude(id_point_2_1);
				double y21 = getLatitude(id_point_2_1);	
				double x22 = getLongitude(id_point_2_2);
				double y22 = getLatitude(id_point_2_2);
				
				// Нахоим расстояние м/д точками и записываем идентификаторы этих точек
				double length11_21 = Math.abs(Math.sqrt(Math.pow((x21 - x11), 2) + Math.pow((y21 - y11), 2)));
				double length11_22 = Math.abs(Math.sqrt(Math.pow((x22 - x11), 2) + Math.pow((y22 - y11), 2)));
				double length12_21 = Math.abs(Math.sqrt(Math.pow((x21 - x12), 2) + Math.pow((y21 - y12), 2)));
				double length12_22 = Math.abs(Math.sqrt(Math.pow((x22 - x12), 2) + Math.pow((y22 - y12), 2)));
				
				double [] all_length = { length11_21, length11_22, length12_21, length12_22 };
				int index = -1; // Индекс в массиве с наименьшим расстоянием м/д точками				
				double max = Double.MAX_VALUE;
				
				for(int i = 0; i < all_length.length; i++) {
					if(all_length[i] < max) {
						max = all_length[i];
						index = i;
					}
				}
				
				switch(index) {
				case 0:
					id_point_1 = id_point_1_1;
					id_point_2 = id_point_2_1;
					break;
				case 1:
					id_point_1 = id_point_1_1;
					id_point_2 = id_point_2_2;
					break;
				case 2:
					id_point_1 = id_point_1_2;
					id_point_2 = id_point_2_1;
					break;
				case 3:
					id_point_1 = id_point_1_2;
					id_point_2 = id_point_2_2;
					break;
				}
				
				x1 = getLongitude(id_point_1);
				y1 = getLatitude(id_point_1);				
				x2 = getLongitude(id_point_2);
				y2 = getLatitude(id_point_2);
			} else if(id_point_1 == 0 && id_point_2 != 0) { // Первая общая линия состоит из одной линии, вторую точку уже нашли				
				x2 = getLongitude(id_point_2);
				y2 = getLatitude(id_point_2);
				
				double x11 = getLongitude(id_point_1_1);
				double y11 = getLatitude(id_point_1_1);				
				// Нахоим расстояние м/д точками и записываем идентификаторы этих точек
				double length11 = Math.abs(Math.sqrt(Math.pow((x2 - x11), 2) + Math.pow((y2 - y11), 2)));
				
				double x12 = getLongitude(id_point_1_2);
				double y12 = getLatitude(id_point_1_2);
				// Нахоим расстояние м/д точками и записываем идентификаторы этих точек
				double length12 = Math.abs(Math.sqrt(Math.pow((x2 - x12), 2) + Math.pow((y2 - y12), 2)));
				
				if(length11 <= length12)
					id_point_1 = id_point_1_1;
				else
					id_point_1 = id_point_1_2;	
				
				x1 = getLongitude(id_point_1);
				y1 = getLatitude(id_point_1);
			} else if(id_point_1 != 0 && id_point_2 == 0) { // Вторая общая линия состоит из одной линии, первую точку уже нашли				
				x1 = getLongitude(id_point_1);
				y1 = getLatitude(id_point_1);
				
				double x21 = getLongitude(id_point_2_1);
				double y21 = getLatitude(id_point_2_1);				
				// Нахоим расстояние м/д точками и записываем идентификаторы этих точек
				double length21 = Math.abs(Math.sqrt(Math.pow((x1 - x21), 2) + Math.pow((y1 - y21), 2)));
				
				double x22 = getLongitude(id_point_2_2);
				double y22 = getLatitude(id_point_2_2);
				// Нахоим расстояние м/д точками и записываем идентификаторы этих точек
				double length22 = Math.abs(Math.sqrt(Math.pow((x1 - x22), 2) + Math.pow((y1 - y22), 2)));
				
				if(length21 <= length22)
					id_point_2 = id_point_2_1;
				else
					id_point_2 = id_point_2_2;	
				
				x2 = getLongitude(id_point_2);
				y2 = getLatitude(id_point_2);
			} else if(id_point_1 != 0 && id_point_2 != 0) { // Уже нашли обе точки для линии
				x1 = getLongitude(id_point_1);
				y1 = getLatitude(id_point_1);
				
				x2 = getLongitude(id_point_2);
				y2 = getLatitude(id_point_2);
			}
			
			// Нахоим расстояние м/д точками и записываем идентификаторы этих точек
			length[0] = Math.abs(Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2)));
			length[1] = id_point_1;
			length[2] = id_point_2;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return length;
	}

	/**
	 * Сравнивает координаты первой и последней точек в линии.
	 * 
	 * @param way_id идентификатор линии в файле карты
	 * @return возвращает true, если координаты равны, иначе - false
	 */
	
	public boolean checkFirstAndLastNodes(long way_id) {
		boolean check = false; // Признак замыкания линий
		ArrayList<Long> nodes_way = getNodeIdsFromWay(way_id); // Все точки линии
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			// Сравниваем первую и последнюю точки линии
			check = compareCoordsNodes(nodes_way.get(0), nodes_way.get(nodes_way.size() - 1), raf);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return check;
	} 
	
	/**
	 * Сравнивает координаты первой и последней точек в списке отсортированных линий.
	 * 
	 * @param ways_ids список идентификаторов линий в файле карты
	 * @return возвращает true, если координаты равны, иначе - false
	 */
	
	public boolean checkFirstAndLastNodesInWays(ArrayList<Long> ways_ids) {
		boolean check = false; // Признак замыкания линий
		ArrayList<Long> nodes_way_first = getNodeIdsFromWay(ways_ids.get(0)); // Все точки первой линии
		ArrayList<Long> nodes_way_last = getNodeIdsFromWay(ways_ids.get(ways_ids.size() - 1)); // Все точки последней линии
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			// Сравниваем первую точку первой в списке линии и последнюю точку последней в списке линии
			check = compareCoordsNodes(nodes_way_first.get(0), nodes_way_last.get(nodes_way_last.size() - 1), raf);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return check;
	} 
	
	/**
	 * Перезаписываем идентификатор точки.
	 * 
	 * @param seek смещение точки, идентификатор которой перезаписывают
	 * @param newId новый идентификатор
	 */
	
	public void renameId(long seek, long newId) {
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			raf.seek(seek + Param.id_seek);
			raf.writeLong(newId);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Создает копию точки по заданному смещению, но с другим идентификатором.
	 * 
	 * @param seek смещение точки, которую копируем
	 * @return смещение новой точки
	 */
	
	public long createNewNode(long seek) {		
		long myselfSeek = -1; // Собственное смещение новой точки
		Param.maxNodeId++; // Новый идентификатор точки
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			myselfSeek = raf.length(); // Новое смещение создаваемой точки элемента
			
			// Копируем данные из точки
			raf.seek(seek);		
			byte type = raf.readByte();
						
			raf.seek(seek + Param.next_seek);
			long next = raf.readLong();
			long attrSeek = raf.readLong();
			double lat = raf.readDouble();
			double lon = raf.readDouble();
			float alt = raf.readFloat();
			float acc = raf.readFloat();
			byte boundary = raf.readByte();
			
			// Создаем новую точку и заполняем данными			
			// Тип элемента
			raf.seek(myselfSeek);			
			raf.writeByte(type);
			
			// Удаление
			raf.seek(myselfSeek + Param.delete_seek);			
			raf.writeByte(0x00);
			
			// Идентификатор
			raf.seek(myselfSeek + Param.id_seek);	
			raf.writeLong(Param.maxNodeId);
			
			// Свое смещение
			raf.seek(myselfSeek + Param.myself_seek);	
			raf.writeLong(myselfSeek);
			
			// Смещение след элемента
			raf.seek(myselfSeek + Param.next_seek);	
			raf.writeLong(next);
			
			// Смещение атрибута элемента
			raf.seek(myselfSeek + Param.attr_seek);	
			raf.writeLong(attrSeek);
			
			// Широта
			raf.seek(myselfSeek + Param.lat_seek);
			raf.writeDouble(lat);
			
			// Долгота
			raf.seek(myselfSeek + Param.lon_seek);	
			raf.writeDouble(lon);
			
			// Высота над уровнем моря
			raf.seek(myselfSeek + Param.alt_seek);	
			raf.writeFloat(alt);
			
			// Точность
			raf.seek(myselfSeek + Param.acc_seek);	
			raf.writeFloat(acc);
			
			// Признак принадлежности элемента к ограничивающему контуру площадного объекта карты
			raf.seek(myselfSeek + Param.boundary_seek);
			raf.writeByte(boundary);					
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return myselfSeek;
	}
	
	/**
	 * Создает пустую точки с новым идентификатором.
	 * 
	 * @param raf объект для доступа к файлу
	 * @return смещение новой точки
	 */
	
	public long createNewEmptyNode(RandomAccessFile raf) {		
		long myselfSeek = -1; // Собственное смещение новой точки
		Param.newIndex++; // Новый идентификатор точки
		
		try {			
			myselfSeek = raf.length(); // Новое смещение создаваемой точки элемента
			
			// Создаем новую точку и заполняем данными			
			// Тип элемента
			raf.seek(myselfSeek);			
			raf.writeByte(Param.point);
			
			// Удаление
			raf.seek(myselfSeek + Param.delete_seek);			
			raf.writeByte(0x00);
			
			// Идентификатор
			raf.seek(myselfSeek + Param.id_seek);	
			raf.writeLong(Param.newIndex);
			
			// Свое смещение
			raf.seek(myselfSeek + Param.myself_seek);	
			raf.writeLong(myselfSeek);
			
			// Смещение след элемента
			raf.seek(myselfSeek + Param.next_seek);	
			raf.writeLong(0);
			
			// Смещение атрибута элемента
			raf.seek(myselfSeek + Param.attr_seek);	
			raf.writeLong(0);
			
			// Широта
			raf.seek(myselfSeek + Param.lat_seek);
			raf.writeDouble(0);
			
			// Долгота
			raf.seek(myselfSeek + Param.lon_seek);	
			raf.writeDouble(0);
			
			// Высота над уровнем моря
			raf.seek(myselfSeek + Param.alt_seek);	
			raf.writeFloat(0);
			
			// Точность
			raf.seek(myselfSeek + Param.acc_seek);	
			raf.writeFloat(0);
			
			// Признак принадлежности элемента к ограничивающему контуру площадного объекта карты
			raf.seek(myselfSeek + Param.boundary_seek);
			raf.writeByte(0x00);					
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return myselfSeek;
	}
	
	/**
	 * Создает копию точки в линии в файле map.dnvg по заданному смещению, но с другим идентификатором.
	 * 
	 * @param seek смещение точки, которую копируем
	 * @return смещение новой точки
	 */
	
	public long createNewNodeForWay(long seek) {
		long myselfSeek = -1; // Собственное смещение новой точки
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			myselfSeek = raf.length();
			
			if(Param.maxNodeId == 4569060707L) {
				int y = 0;
			}
			
			// Копируем данные из точки
			raf.seek(seek);
			byte type = raf.readByte();
			
			raf.seek(seek + Param.next_seek);
			long next = raf.readLong();
			long attrSeek = raf.readLong();
			double lat = raf.readDouble();
			double lon = raf.readDouble();
			float alt = raf.readFloat();
			float acc = raf.readFloat();
			byte boundary = raf.readByte();
			
			// Создаем новую точку и заполняем данными			
			// Тип элемента
			raf.seek(myselfSeek);			
			raf.writeByte(type);
			
			// Удаление
			raf.seek(myselfSeek + Param.delete_seek);			
			raf.writeByte(0x00);
			
			// Идентификатор
			raf.seek(myselfSeek + Param.id_seek);	
			raf.writeLong(Param.maxNodeId);
			
			// Свое смещение
			raf.seek(myselfSeek + Param.myself_seek);	
			raf.writeLong(myselfSeek);
			
			// Смещение след элемента
			raf.seek(myselfSeek + Param.next_seek);	
			raf.writeLong(next);
			
			// Смещение атрибута элемента карты
			raf.seek(myselfSeek + Param.attr_seek);	
			raf.writeLong(attrSeek);
			
			// Широта
			raf.seek(raf.length());	
			raf.writeDouble(lat);
			
			// Долгота
			raf.seek(raf.length());	
			raf.writeDouble(lon);
			
			// Высота над уровнем моря
			raf.seek(raf.length());	
			raf.writeFloat(alt);
			
			// Точность
			raf.seek(raf.length());	
			raf.writeFloat(acc);
			
			// Признак принадлежности элемента к ограничивающему контуру площадного объекта карты
			raf.seek(myselfSeek + Param.boundary_seek);			
			raf.writeByte(boundary);			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return myselfSeek;
	}
	
	/**
	 * Создает копию точки в линии в файле map.dnvg по заданному смещению, но с другим идентификатором.
	 * 
	 * @param seek смещение точки, которую копируем
	 * @param raf объект для доступа к файлу
	 * @return смещение новой точки
	 */
	
	public long createNewNodeForWay(long seek, RandomAccessFile raf) {
		long myselfSeek = -1; // Собственное смещение новой точки
		
		try {			
			myselfSeek = raf.length();
			
			// Копируем данные из точки
			raf.seek(seek);
			byte type = raf.readByte();
			
			raf.seek(seek + Param.next_seek);
			long next = raf.readLong();
			long attrSeek = raf.readLong();
			double lat = raf.readDouble();
			double lon = raf.readDouble();
			float alt = raf.readFloat();
			float acc = raf.readFloat();
			byte boundary = raf.readByte();
			
			// Создаем новую точку и заполняем данными			
			// Тип элемента
			raf.seek(myselfSeek);			
			raf.writeByte(type);
			
			// Удаление
			raf.seek(myselfSeek + Param.delete_seek);			
			raf.writeByte(0x00);
			
			// Идентификатор
			raf.seek(myselfSeek + Param.id_seek);	
			raf.writeLong(Param.maxNodeId);
			
			// Свое смещение
			raf.seek(myselfSeek + Param.myself_seek);	
			raf.writeLong(myselfSeek);
			
			// Смещение след элемента
			raf.seek(myselfSeek + Param.next_seek);	
			raf.writeLong(next);
			
			// Смещение атрибута элемента карты
			raf.seek(myselfSeek + Param.attr_seek);	
			raf.writeLong(attrSeek);
			
			// Широта
			raf.seek(raf.length());	
			raf.writeDouble(lat);
			
			// Долгота
			raf.seek(raf.length());	
			raf.writeDouble(lon);
			
			// Высота над уровнем моря
			raf.seek(raf.length());	
			raf.writeFloat(alt);
			
			// Точность
			raf.seek(raf.length());	
			raf.writeFloat(acc);
			
			// Признак принадлежности элемента к ограничивающему контуру площадного объекта карты
			raf.seek(myselfSeek + Param.boundary_seek);			
			raf.writeByte(boundary);			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return myselfSeek;
	}
	
	/**
	 * Возвращает размер (кол-во точек) в элементе карты.
	 * 
	 * @param id идентификатор елемента карты
	 * @return размер (кол-во точек)
	 */ 
	
	public int getElementSize(long id) {
		 return getNodeIdsFromWay(id).size();
	}

	/**
	 * Возвращает массив с координатами точек элемента карты в виде
	 * {x1, y1, x2, y2, ... ,xN, yN}.
	 * 
	 * @param id идентификатор элемента карты
	 * @return массив с координатами точек элемента карты
	 */
	
	public double [] getArrayCoords(long id) {
		// Массив с координатами точек элемента карты
		double [] tmp_array = new double[getElementSize(id) * 2];
		
		ArrayList<Long> node_ids = getNodeIdsFromWay(id);
		
		for(int i = 0, j = 0; i < tmp_array.length; i+=2, j++) {
			tmp_array[i] = getLongitude(node_ids.get(j));
			tmp_array[i + 1] = getLatitude(node_ids.get(j));
		}
		
		return tmp_array;
	}
	
	/**
	 * Возвращает смещение атрибута у элемента карты.
	 * 
	 * @param seek смещение элемента карты в файле map.dnvg
	 * @return
	 */
	
	public Long getArrtSeek(long seek) {
		long attrSeek = 0;
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");	
			
			raf.seek(seek + Param.attr_seek);
			attrSeek = raf.readLong();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return attrSeek;
	}
	
	/**
	 * Возвращает список идентификаторов всех контурных точек полигона.
	 * 
	 * @param firstPointInWay идентификатор первой точки в полигоне
	 * @return  список идентификаторов всех точек линии
	 */
	
	public ArrayList<Long> getBoundaryNodeIdsFromWay(long firstPointInWay) {
		ArrayList<Long> node_ids = new ArrayList<Long>();
		long seek = 0;	
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");			
			
			seek = Param.new_seek_nodes.get(firstPointInWay);
			
			int size = Param.num_points.get(firstPointInWay);
			
			for(int i = 0; i < size; i++) {
				raf.seek(seek + Param.boundary_seek);
				
				if(raf.readByte() == Param.boundary) {
					raf.seek(seek + Param.id_seek);
					node_ids.add(raf.readLong());
				}				
				
				raf.seek(seek + Param.next_seek);  // Значение смещения следующего элемента в линии
				long nextSeek = raf.readLong(); // Смещение следующего элемента
				seek = nextSeek;
			}			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return node_ids;
	}
	
	/**
	 * Возвращает идентификатор точки по ее смещению.
	 * 
	 * @param seek смещение точки в файле карты
	 * @return идентификатор точки
	 */
	
	public long getNodeId(long seek) {
		long id = 0;
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");	
			
			raf.seek(seek + Param.id_seek);
			id = raf.readLong();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return id;
	}
	
	/**
	 * Возвращает список идентификаторов всех точек линии.
	 * 
	 * @param wayId идентификатор линии
	 * @return  список идентификаторов всех точек линии
	 */
	
	public ArrayList<Long> getNodeIdsFromWay(long wayId) {
		ArrayList<Long> node_ids = new ArrayList<Long>();
		long id = 0;
		long seek = 0;	
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");			
			
			if(Param.seekChanged) {
				seek = Param.new_seek_nodes.get(wayId);
			} else {
				seek = Param.seek_nodes.get(wayId);
			}
			
			raf.seek(seek);
			byte type = raf.readByte();
			
			if(type == Param.point) {
				raf.seek(seek + Param.id_seek);
				node_ids.add(raf.readLong());
			} else {
				for(int i = 0; i < Param.num_points.get(wayId); i++) {
					raf.seek(seek + Param.id_seek);
					id = raf.readLong();
					node_ids.add(id);
					
					raf.seek(seek + Param.next_seek);  // Значение смещения следующего элемента в линии
					long nextSeek = raf.readLong(); // Смещение следующего элемента
					seek = nextSeek;
				}
			}			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return node_ids;
	}
	
	/**
	 * Возвращает список идентификаторов всех точек линии.
	 * 
	 * @param wayId идентификатор линии
	 * @param raf объект для доступа к файлу
	 * @return  список идентификаторов всех точек линии
	 */
	
	public ArrayList<Long> getNodeIdsFromWay(long wayId, RandomAccessFile raf) {
		ArrayList<Long> node_ids = new ArrayList<Long>();
		long id = 0;
		long seek = 0;	
		
		try {			
			if(Param.seekChanged) {
				seek = Param.new_seek_nodes.get(wayId);
			} else {
				seek = Param.seek_nodes.get(wayId);
			}
			
			raf.seek(seek);
			byte type = raf.readByte();
			
			if(type == Param.point) {
				raf.seek(seek + Param.id_seek);
				node_ids.add(raf.readLong());
			} else {
				for(int i = 0; i < Param.num_points.get(wayId); i++) {
					raf.seek(seek + Param.id_seek);
					id = raf.readLong();
					node_ids.add(id);
					
					raf.seek(seek + Param.next_seek);  // Значение смещения следующего элемента в линии
					long nextSeek = raf.readLong(); // Смещение следующего элемента
					seek = nextSeek;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return node_ids;
	}
	
	/**
	 * Создает копию линии с другим идентификатором и другими точками, заданными с помощью
	 * массива координат в формате x1,y1, ... xN, yN.
	 * 
	 * @param coords массив координат линии
	 * @param seek смещение копируемого объекта 
	 * @param raf объект для доступа к файлу
	 * @return смещение новой линии
	 */
	
	public long createNewWay(double[] coords, long seek, RandomAccessFile raf) {
		long first = -1; // Собственное смещение первой точки новой линии
		ArrayList<Long> new_seek_nodes = new ArrayList<>(); // Новые смещения точек
		
		try {			
			// Создаем новую линию и заполняем данными	
			long newWayId = -1;
			
			// Копируем данные из точки
			raf.seek(seek);
			byte type = raf.readByte();
						
			raf.seek(seek + Param.attr_seek);
			long attrSeek = raf.readLong();
			long newAttrSeek = 0;
			
			raf.seek(seek + Param.alt_seek);
			float alt = raf.readFloat();
			float acc = raf.readFloat();
			
			for(int i = 0; i < coords.length; i+=2) {
				long myselfSeek = raf.length();
				
				if(i == 0) {
					newWayId = ++Param.newIndex;
					first = myselfSeek;
					
					if(attrSeek > 0) 
						newAttrSeek = createCopyAttr(attrSeek, seek);
				} else
					++Param.newIndex;
				
				double lon = coords[i]; // X
				double lat = coords[i + 1];	// Y					
				
				// Создаем новую точку и заполняем данными			
				// Тип элемента
				raf.seek(myselfSeek);			
				raf.writeByte(type);
				
				// Удаление
				raf.seek(myselfSeek + Param.delete_seek);			
				raf.writeByte(0x00);
				
				// Идентификатор
				raf.seek(myselfSeek + Param.id_seek);	
				raf.writeLong(Param.newIndex);
				
				// Свое смещение
				raf.seek(myselfSeek + Param.myself_seek);	
				raf.writeLong(myselfSeek);
				
				// Смещение след элемента
				raf.seek(myselfSeek + Param.next_seek);	
				raf.writeLong(0);
				
				// Смещение атрибута элемента карты
				raf.seek(myselfSeek + Param.attr_seek);				
				raf.writeLong(newAttrSeek);
				
				// Широта
				raf.seek(raf.length());	
				raf.writeDouble(lat);
				
				// Долгота
				raf.seek(raf.length());	
				raf.writeDouble(lon);
				
				// Высота над уровнем моря
				raf.seek(raf.length());	
				raf.writeFloat(alt);
				
				// Точность
				raf.seek(raf.length());	
				raf.writeFloat(acc);
				
				// Признак принадлежности элемента к ограничивающему контуру площадного объекта карты
				raf.seek(myselfSeek + Param.boundary_seek);			
				raf.writeByte(Param.boundary);		
				
				new_seek_nodes.add(myselfSeek);
				Param.new_seek_nodes.put(Param.newIndex, myselfSeek);
				Param.seek_nodes_used.put(Param.newIndex, myselfSeek);
				
				if(i == 0) {
					Param.my_ways_ids.put(newWayId, newWayId);
					Param.new_seek_only_ways.put(newWayId, myselfSeek);
					Param.num_points.put(newWayId, coords.length / 2);
				}
			}
			
			// Записываем новые смещения следующих точек в линии
			setSeekNextNodesInWay(new_seek_nodes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return first;
	}
		
	/**
	 * Создает копию линии с другим идентификатором.
	 * 
	 * @param node_ids массив id точек в линии, копию которой надо сделать
	 * @return смещение новой линии
	 */
	
	public long createNewWay(ArrayList<Long> node_ids) {		
		long myselfSeek = -1; // Собственное смещение первой точки новой линии
		ArrayList<Long> new_seek_nodes = new ArrayList<>(); // Новые смещения точек
		
		try {
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			myselfSeek = raf.length();
			
			if(raf != null)
				raf.close();
						
			// Создаем новую линию и заполняем данными	
			long newWayId = -1;
			
			for(int i = 0; i < node_ids.size(); i++) {
				if(i == 0)
					newWayId = ++Param.maxNodeId;
				else
					++Param.maxNodeId;
				
				long seek = Param.seek_nodes.get(node_ids.get(i));
				
				long new_seek = createNewNodeForWay(seek);
				if(Param.maxNodeId == 4827366011l) {
					int y = 0;
				}
				new_seek_nodes.add(new_seek);
				Param.seek_nodes.put(Param.maxNodeId, new_seek);
				Param.seek_nodes_used.put(Param.maxNodeId, new_seek);
				
				if(i == 0) {
					Param.my_ways_ids.put(newWayId, newWayId);
					Param.seek_ways.put(newWayId, new_seek);
					Param.num_points.put(newWayId, node_ids.size());
				}
			}
			
			// Записываем новые смещения следующих точек в линии
			setSeekNextNodesInWay(new_seek_nodes);
			
			// Создаем копию атрибута и записываем новое смещение атрибута в точки линии
			createCopyAttrInWay(new_seek_nodes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return myselfSeek;
	}
	
	/**
	 * Создает копию атрибута и записывает новое смещение атрибута в точки линии.
	 * 
	 * @param firstSeek первая точка в линии
	 */
	
	public void createCopyAttrInWay(Long firstSeek) {		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			// Проверяем наличие атрибута у линии
			raf.seek(firstSeek + Param.attr_seek);
			long attrSeek = raf.readLong();
			
			// Если атрибут у линии имеется, создаем его копию
			if(attrSeek > 0) {
				long newAttrSeek = createCopyAttr(attrSeek, firstSeek); // Копируем атрибут
				
				setAttrSeek(firstSeek, firstSeek, newAttrSeek, raf);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Создает копию атрибута и записывает новое смещение атрибута в точки линии.
	 * 
	 * @param seek_nodes точки, из которых состоит линия
	 */
	
	public void createCopyAttrInWay(ArrayList<Long> seek_nodes) {
		long firstSeek = seek_nodes.get(0); // Первая точка линии
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			// Проверяем наличие атрибута у линии
			raf.seek(firstSeek + Param.attr_seek);
			long attrSeek = raf.readLong();
			
			// Если атрибут у линии имеется, создаем его копию
			if(attrSeek > 0) {
				long newAttrSeek = createCopyAttr(attrSeek, firstSeek); // Копируем атрибут
				
				setAttrSeek(firstSeek, firstSeek, newAttrSeek, raf);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Создает и записывает в файлы карты новый полигон, состоящий из триугольников 
	 * (триангулированный полигон).
	 * 
	 * @param triangles массив треугольников
	 * @param allSeeks смещения точек, используемые для полигона(ов) до триангуляции
	 * @param bounderyCoords координаты в формате (x, y) граничных (внешних, контурных) точек
	 * нового полигона
	 * @param firstPointInWay смещение первой точки внешнего полигона до его триангуляции
	 * @return смещение первой точки полигона (первой точки первого треугольника) либо 0
	 */
	
	public long createNewTrianglePoligon(double [][] triangles, ArrayList<Long > allSeeks, 
			                             double [] bounderyCoords, long firstPointInWay) {
		long firstSeek = 0;
		
		if(triangles != null && triangles.length != 0 && allSeeks != null && allSeeks.size() > 1) {			
			try {
				h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
				raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
				
				// Копируем некоторые данные из из первой точки внешнего полигона 
				// до его триангуляции
				raf.seek(firstPointInWay + Param.attr_seek);
				long attrSeek = raf.readLong();				
				
				raf.seek(firstPointInWay + Param.alt_seek);
				float alt = raf.readFloat();
				float acc = raf.readFloat();
				
				// Связываем все смещения точек используемые до триангуляции в одну линию
				for(int i = 0; i < allSeeks.size() - 1; i++) {
					long seek = allSeeks.get(i);
					long nextSeek = allSeeks.get(i + 1);
					
					if(i == 0) {
						firstSeek = seek;
						
						// Записываем в список всех линий файла новую линию и соответствие кол-ва точек линии
						raf.seek(firstSeek + Param.id_seek);
						long id = raf.readLong();
						
						Param.new_seek_only_ways.put(id, firstSeek);
						Param.num_points.put(id, (triangles.length * 3));
					}
					
					setNextSeek(seek, nextSeek, raf);
				}
				
				// Т.к. массив треугольников содержит отдельные триугольники, у которых
				// три вершины, то умножаем размер этого массива на 3
				if((triangles.length * 3) > allSeeks.size()) {
					long prevSeek = allSeeks.get(allSeeks.size() - 1);
					
					// Отнимаем уже используемые смещения из общего кол-ва смещений
					for(int i = 0; i < (triangles.length * 3) - allSeeks.size(); i++) {						
						long newSeek = createNewEmptyNode(raf);
						setNextSeek(prevSeek, newSeek, raf);
						prevSeek = newSeek;
						
						// Сохраняем вновь созданную точку линии
						Param.new_seek_nodes.put(Param.newIndex, newSeek);
					}
				}
				
				// Теперь заполняем данными точки триангулированного полигона
				h_raf.seek(attrSeek + Param.firsPointInWay_seek);
				h_raf.writeLong(firstSeek);
								
				setType(Param.poligon_outer, firstSeek, firstSeek, raf);
				setAttrSeek(firstSeek, firstSeek, attrSeek, raf);
				setAltitude(firstSeek, firstSeek, alt, raf);
				setAccuracy(firstSeek, firstSeek, acc, raf);
				
				// Координаты
				long tmpSeek = firstSeek;
				
				for(int i = 0; i < triangles.length; i++) {					
					double [] coords = triangles[i];
					
					for(int j = 0; j < coords.length; j+=2) {
						raf.seek(tmpSeek + Param.lat_seek);
						raf.writeDouble(coords[j]);
						
						raf.seek(tmpSeek + Param.lon_seek);
						raf.writeDouble(coords[j + 1]);
						
						// Ищем координаты контура
						int size = bounderyCoords.length;
						
						if(size > 1) {
							// Чтобы несколько раз не отмечать точку как контурную в разных треугольниках
							// будем ее удалять из массива контурных координат
							ArrayList<Double> tmpBoundaryCoords = new ArrayList<>();
							
							for(int k = 0; k < size; k+=2) {
								if(coords[j] == bounderyCoords[k] && coords[j + 1] == bounderyCoords[k + 1]) {
									setBoundary(tmpSeek, raf);
								} else {
									tmpBoundaryCoords.add(bounderyCoords[k]);
									tmpBoundaryCoords.add(bounderyCoords[k + 1]);
								}
							}
							
							// Изменяем размер массива контурных координат
							Double [] tmpArray = new Double[tmpBoundaryCoords.size()];
							tmpBoundaryCoords.toArray(tmpArray);
							
							bounderyCoords = new double[tmpArray.length];
							
							for(int m = 0; m < tmpArray.length; m++) {
								bounderyCoords[m] = tmpArray[m];
							}	
						}					
						
						// Следующая точка элемента карты
						raf.seek(tmpSeek + Param.next_seek);
						tmpSeek = raf.readLong();
					}
				}
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
		
		return firstSeek;
	}
	
	/**
	 * Записывает значение смещения первой точки в линии в атрибут элемента.
	 * 
	 * @param firstPointInWay смещение первой точки в линии
	 * @param raf объект для доступа к файлу
	 */
	
	public void setFirstPointInAttr(long firstPointInWay, RandomAccessFile raf) {		
		try {
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			
			raf.seek(firstPointInWay + Param.attr_seek);
			long attrSeek = raf.readLong();
			
			if(attrSeek > 0) {
				h_raf.seek(attrSeek + Param.firsPointInWay_seek);
				h_raf.writeLong(firstPointInWay);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(h_raf != null)
					h_raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * В линии каждая точка будет указывать на следующую.
	 * 
	 * @param seek_nodes смещения точек линии
	 */
	
	private void setSeekNextNodesInWay(ArrayList<Long> seek_nodes) {
		// Записываем новые смещения следующих точек в линии
		for(int i = 0; i < seek_nodes.size(); i++) {
			if(seek_nodes.size() == 1) {
				setNextSeek(seek_nodes.get(i), (long) 0);
			} else {
				if(i == (seek_nodes.size() - 1))
					setNextSeek(seek_nodes.get(i), (long) 0);
				else
					setNextSeek(seek_nodes.get(i), seek_nodes.get(i + 1));
			}
		}
	}
	
	/**
	 * В линии каждая точка будет указывать на следующую.
	 * 
	 * @param seek_nodes смещения точек линии
	 * @param raf объект для доступа к файлу
	 */
	
	private void setSeekNextNodesInWay(ArrayList<Long> seek_nodes, RandomAccessFile raf) {
		// Записываем новые смещения следующих точек в линии
		for(int i = 0; i < seek_nodes.size(); i++) {
			if(seek_nodes.size() == 1) {
				setNextSeek(seek_nodes.get(i), (long) 0, raf);
			} else {
				if(i == (seek_nodes.size() - 1))
					setNextSeek(seek_nodes.get(i), (long) 0, raf);
				else
					setNextSeek(seek_nodes.get(i), seek_nodes.get(i + 1), raf);
			}
		}
	}
	
	/**
	 * Записывает для заданной точки значение смещения следующей точки.
	 * 
	 * @param seek смещение точки
	 * @param nextSeek смещение следующей точки
	 */
	
	private void setNextSeek(long seek, long nextSeek) {
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			raf.seek(seek + Param.next_seek);
			raf.writeLong(nextSeek);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Записывает для заданной точки значение смещения следующей точки.
	 * 
	 * @param seek смещение точки
	 * @param nextSeek смещение следующей точки
	 * @param raf объект для доступа к файлу
	 */
	
	private void setNextSeek(long seek, long nextSeek, RandomAccessFile raf) {
		try {			
			raf.seek(seek + Param.next_seek);
			raf.writeLong(nextSeek);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Заполняет элемент карты данными, которые соответствуют пустой линии
	 * (стирает некоторые св-ва и смещения элемента и устанавливает тип линии).
	 * 
	 * @param type тип элемента карты (линия)
	 * @param seek смещение в файле
	 * @param seekNextElement смещение следующего элемента в линии
	 */
	
	public void setWay(byte type, long seek, long seekNextElement) {
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			// Тип элемента
			raf.seek(seek);
			raf.writeByte(type);			
			
			// Смещение след элемента
			raf.seek(seek + Param.next_seek);
			raf.writeLong(seekNextElement);
			
			// Смещение атрибута
			raf.seek(seek + Param.attr_seek);
			raf.writeLong(0);				
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Сортирует (выставляет по порядку) заданные линии в полигонах.
	 * 
	 * @param outerWaysArray массив идентификаторов линий, которые необходимо отсортировать
	 */
	
	public void sortWays(ArrayList<ArrayList<Long>> outerWaysArray) {
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			for(int k = 0; k < outerWaysArray.size(); k++) {
				ArrayList<Long> ways_ids = outerWaysArray.get(k); // Идентификаторы точек линий
				ArrayList<Long> tmp_ways_ids = new ArrayList<Long>(); // Идентификаторы точек линий в отсортированном массиве
				
				 if(ways_ids.size() > 1) { 
					 tmp_ways_ids.add(ways_ids.get(0));
					 int size_tmp_ways = tmp_ways_ids.size();
					 
					 ArrayList<Long> nodes_ids_way = new ArrayList<>(); // Идентификаторы точек одной из заданных линий
					 ArrayList<Long> nodes_ids_way_tmp = new ArrayList<>(); // Идентификаторы точек одной из крайних линий сортированного массива линий
					 
					 int size = ways_ids.size();
					 
					 while(size_tmp_ways != size) {
						 boolean first_way_tmp = false; // Есть общие точки в первой линии остортированного массива
						 
						 // Берем первую линию в отсортированном массиве
						 nodes_ids_way_tmp.clear();
						 nodes_ids_way_tmp.addAll(getNodeIdsFromWay(tmp_ways_ids.get(0), raf));
						 
						 for(int i = 1; i < ways_ids.size(); i++) {
							 boolean first_first = false; // Сравниваем первую точку в одной линии и первую точку в другой линии
							 boolean first_last = false; // Сравниваем первую точку в одной линии и последнюю точку в другой линии
							 boolean last_first = false; // Сравниваем последнюю точку в одной линии и первую точку в другой линии
							 boolean last_last = false; // Сравниваем последнюю точку в одной линии и последнюю точку в другой линии
							 
							 // Перебираем линии из заданного массива
							 nodes_ids_way.clear();
							 nodes_ids_way.addAll(getNodeIdsFromWay(ways_ids.get(i), raf));
							 
							 // Первая - первая
							 first_first = compareCoordsNodes(nodes_ids_way_tmp.get(0),
									                          nodes_ids_way.get(0), raf);
							 
							 // Последняя - последняя
							 last_last = compareCoordsNodes(nodes_ids_way_tmp.get(nodes_ids_way_tmp.size() - 1),
                                                            nodes_ids_way.get(nodes_ids_way.size() - 1), raf);
							 
							 if(first_first || last_last) {
								 ArrayList<Long> tmp_ways = new ArrayList<>();
								 
								 // Меняем направление заданной линии
								 for(int j = nodes_ids_way.size() - 1; j >= 0; j--)
									    tmp_ways.add(nodes_ids_way.get(j));
								
								
								 // Т.к. первая точка линии меняется, то удаляем эту линию из списка всех линий,
								 // списка используемых линий, списока используемых линий в отношении и
								 // списка количества точек в линии
								 Param.seek_ways.remove(nodes_ids_way.get(0));
								 Param.seek_ways_used.remove(nodes_ids_way.get(0));
								 Param.num_points.remove(nodes_ids_way.get(0));
								 
								 nodes_ids_way.clear();
								 nodes_ids_way.addAll(tmp_ways);
								
								 // Вставляет в список всех линий, список используемых линий,
								 // списока используемых линий в отношении и список количества точек в линии
								 // новую линию
								 Param.seek_ways.put(nodes_ids_way.get(0), Param.seek_nodes.get(nodes_ids_way.get(0)));
								 Param.seek_ways_used.put(nodes_ids_way.get(0), Param.seek_nodes.get(nodes_ids_way.get(0)));
								 Param.num_points.put(nodes_ids_way.get(0), nodes_ids_way.size());
								
								 // Вставляем новую линию в отсортированный массив
								 tmp_ways_ids.add(0, nodes_ids_way.get(0));
								 size_tmp_ways = tmp_ways_ids.size();
								
								 // Заполняем массив смещений точек в линии, т.к. направление линии поменялось
								 ArrayList<Long> seek_nodes_way = new ArrayList<>(); // Смещения точек в линии
								 
								 for(int j = 0; j < nodes_ids_way.size(); j++)
									 seek_nodes_way.add(Param.seek_nodes.get(nodes_ids_way.get(j)));
								
								 setSeekNextNodesInWay(seek_nodes_way, raf);
								 
								 ways_ids.remove(ways_ids.get(i));
								 
								 first_way_tmp = true;
								 break;
							 }
							 
							 // Первая - последняя
							 first_last = compareCoordsNodes(nodes_ids_way_tmp.get(0),
			                                                 nodes_ids_way.get(nodes_ids_way.size() - 1), raf);
							
							 // Последняя - первая
							 last_first = compareCoordsNodes(nodes_ids_way_tmp.get(nodes_ids_way_tmp.size() - 1),
                                                             nodes_ids_way.get(0), raf);
							 
							 if(first_last || last_first) {
								 // Вставляем линию в отсортированный массив
								 tmp_ways_ids.add(0, nodes_ids_way.get(0));
								 size_tmp_ways = tmp_ways_ids.size();
								 
								 ways_ids.remove(ways_ids.get(i));
								 
								 first_way_tmp = true;
								 break;
							 }
						 }
						 
						 // Не нашли в первой линии отсортированного массива общих точек
						 if(!first_way_tmp) {
							 // Берем последнюю линию в отсортированном массиве
							 nodes_ids_way_tmp.clear();
							 nodes_ids_way_tmp.addAll(getNodeIdsFromWay(tmp_ways_ids.get(tmp_ways_ids.size() - 1), raf));
							 
							 for(int i = 1; i < ways_ids.size(); i++) {
								 boolean first_first = false; // Сравниваем первую точку в одной линии и первую точку в другой линии
								 boolean first_last = false; // Сравниваем первую точку в одной линии и последнюю точку в другой линии
								 boolean last_first = false; // Сравниваем последнюю точку в одной линии и первую точку в другой линии
								 boolean last_last = false; // Сравниваем последнюю точку в одной линии и последнюю точку в другой линии
								 
								 // Перебираем линии из заданного массива
								 nodes_ids_way.clear();
								 nodes_ids_way.addAll(getNodeIdsFromWay(ways_ids.get(i), raf));
								 
								 // Первая - первая
								 first_first = compareCoordsNodes(nodes_ids_way_tmp.get(0),
										                          nodes_ids_way.get(0), raf);
								 
								 // Последняя - последняя
								 last_last = compareCoordsNodes(nodes_ids_way_tmp.get(nodes_ids_way_tmp.size() - 1),
	                                                            nodes_ids_way.get(nodes_ids_way.size() - 1), raf);
								 
								 if(first_first || last_last) {
									 ArrayList<Long> tmp_ways = new ArrayList<>();
									 
									 // Меняем направление заданной линии
									 for(int j = nodes_ids_way.size() - 1; j >= 0; j--)
										    tmp_ways.add(nodes_ids_way.get(j));
									
									
									 // Т.к. первая точка линии меняется, то удаляем эту линию из списка всех линий,
									 // списка используемых линий, списока используемых линий в отношении и
									 // списка количества точек в линии
									 Param.seek_ways.remove(nodes_ids_way.get(0));
									 Param.seek_ways_used.remove(nodes_ids_way.get(0));
									 Param.num_points.remove(nodes_ids_way.get(0));
									 
									 nodes_ids_way.clear();
									 nodes_ids_way.addAll(tmp_ways);
									
									 // Вставляет в список всех линий, список используемых линий,
									 // списока используемых линий в отношении и список количества точек в линии
									 // новую линию
									 Param.seek_ways.put(nodes_ids_way.get(0), Param.seek_nodes.get(nodes_ids_way.get(0)));
									 Param.seek_ways_used.put(nodes_ids_way.get(0), Param.seek_nodes.get(nodes_ids_way.get(0)));
									 Param.num_points.put(nodes_ids_way.get(0), nodes_ids_way.size());
									
									 // Вставляем новую линию в отсортированный массив
									 tmp_ways_ids.add(nodes_ids_way.get(0));
									 size_tmp_ways = tmp_ways_ids.size();
									 
									 // Заполняем массив смещений точек в линии, т.к. направление линии поменялось
									 ArrayList<Long> seek_nodes_way = new ArrayList<>(); // Смещения точек в линии
									 
									 for(int j = 0; j < nodes_ids_way.size(); j++)
										 seek_nodes_way.add(Param.seek_nodes.get(nodes_ids_way.get(j)));
									
									 setSeekNextNodesInWay(seek_nodes_way, raf);
									 
									 ways_ids.remove(ways_ids.get(i));
									 break;
								 }
								 
								 // Первая - последняя
								 first_last = compareCoordsNodes(nodes_ids_way_tmp.get(0),
				                                                 nodes_ids_way.get(nodes_ids_way.size() - 1), raf);
								 // Последняя - первая
								 last_first = compareCoordsNodes(nodes_ids_way_tmp.get(nodes_ids_way_tmp.size() - 1),
	                                                             nodes_ids_way.get(0), raf);
								 
								 if(first_last || last_first) {
									 // Вставляем линию в отсортированный массив
									 tmp_ways_ids.add(nodes_ids_way.get(0));
									 size_tmp_ways = tmp_ways_ids.size();
									 
									 ways_ids.remove(ways_ids.get(i));
									 break;
								 }
								 
							 }
						 }
					 }
					 
					 ways_ids.clear();
					 ways_ids.addAll(tmp_ways_ids);
				 }
			}
			
			// Перезаписываем атрибуты
			for(int k = 0; k < outerWaysArray.size(); k++) {
				ArrayList<Long> ways_ids = outerWaysArray.get(k); // Идентификаторы точек линий
				
				for(int j = 0; j < ways_ids.size(); j ++) {
					setFirstPointInAttr(Param.seek_nodes.get(ways_ids.get(j)), raf);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Сравнивает координаты двух точек.
	 * 
	 * @param nodeId_1 идентификатор первой точки
	 * @param nodeId_2 идентификатор второй точки 
	 * @param raf объект для доступа к файлу
	 * @return если равны, возвращает true, иначе false
	 */
	
	private boolean compareCoordsNodes(long nodeId_1, long nodeId_2, RandomAccessFile raf) {
		double [] coords_node_1 = new double [2]; // Широта и долгота точки первой линии
		double [] coords_node_2 = new double [2]; // Широта и долгота точки второй линии
		
		try {
			// Широта
			raf.seek(Param.seek_nodes.get(nodeId_1) + Param.lat_seek);
			coords_node_1[0] = raf.readDouble();
			// Долгота
			raf.seek(Param.seek_nodes.get(nodeId_1) + Param.lon_seek);
			coords_node_1[1] = raf.readDouble();
		
			// Широта
			raf.seek(Param.seek_nodes.get(nodeId_2) + Param.lat_seek);
			coords_node_2[0] = raf.readDouble();
			// Долгота
			raf.seek(Param.seek_nodes.get(nodeId_2) + Param.lon_seek);
			coords_node_2[1] = raf.readDouble();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return (coords_node_1[0] == coords_node_2[0]) && (coords_node_1[1] == coords_node_2[1]) ? true : false;
	}
	
	/**
	 * Возвращает размер файла карты map.dnvg.
	 * 
	 * @return размер файла карты
	 */
	
	public long getLengthFile() throws IOException {	
		raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
		
		long seek = raf.length();	
		
		raf.close();
		
		return seek;
	}
	
	/**
	 * Возвращает размер всех файлов карты (map.dnvg, map.hnvg и index_map).
	 * 
	 * @return размер всех файлов карты
	 */
	
	public long getLengthFiles() throws IOException {	
		i_raf = new RandomAccessFile(Param.tree_path, "rw");
		h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
		raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
		
		long i_seek = i_raf.length();	
		long h_seek = h_raf.length();	
		long seek = raf.length();	
		
		i_raf.close();
		h_raf.close();
		raf.close();
		
		return (seek + h_seek + i_seek);
	}
	
	/**
	 * Проверяем принадлежность внутреннего полигона к внешнему.
	 * 
	 * @param outerBounds внешний полигон
	 * @param innerBounds внутренний полигон
	 * @return если внутренний полигон принадлежит внешнему полигоны - true, 
	 * иначе - false
	 */
	
	private boolean isContains(JPolygon outerBounds, JPolygon innerBounds) {
		// Координаты точки для построения луча с каждой точкой внутреннего полигона
		double X = 10000;
		double Y = 10000;
		double EPS = 1e-9; // Бесконечно малая величина (как ноль)		
		
		boolean contains = true; // Признак принадлежности внутреннего полигона внешнему
		// Признак того, что точка пересечения попадает на граничную точку ребра. Если ребра соседние,
		// то эта точка может засчитыватся два раза подряд и увеличивать тем самым счетчик cnt на 2, 
		// хотя по сути пересечение одно. 
		boolean isAgain = false; 
		
		for(int i = 0; i < innerBounds.n_points; i++) {		
			int cnt = 0; // Счетчик пересечений одной из точек внутреннего полигона со всеми сторонами внешнего полигона
						
			// Находим коэффициенты прямой для точки внутреннего полигона и точки луча
			double inner_a = Y - innerBounds.y_points[i]; 
//			inner_a = round7(inner_a);
			double inner_b = innerBounds.x_points[i] - X;
//			inner_b = round7(inner_b);
			double inner_c = (innerBounds.x_points[i] * (innerBounds.y_points[i] - Y)) + 
					         (innerBounds.y_points[i] * (X - innerBounds.x_points[i]));
//			inner_c = round7(inner_c);
			
			for(int j = 0; j < outerBounds.n_points; j++) {				
				// Находим коэффициенты прямой для одной из сторон внешнего полигона
				int next = j + 1; // Следующая точка в полигоне
				
				if(j == outerBounds.n_points - 1)
					next = 0;
					
				double outer_a = outerBounds.y_points[next] - outerBounds.y_points[j]; 
//				outer_a = round7(outer_a);
				double outer_b = outerBounds.x_points[j] - outerBounds.x_points[next];
//				outer_b = round7(outer_b);
				double outer_c = (outerBounds.x_points[j] * (outerBounds.y_points[j] - outerBounds.y_points[next])) + 
						         (outerBounds.y_points[j] * (outerBounds.x_points[next] - outerBounds.x_points[j]));
//				outer_c = round7(outer_c);
				
				// Находим точку пересечения двух прямых
				double znamenatel = (inner_a * outer_b) - (outer_a * inner_b);
//				znamenatel = round7(znamenatel);
				
				// Если знаменатель равен нулю или стремиться к нему, то прямые не пересекаются
				if(!(Math.abs(znamenatel) < EPS)) {
					double chislitel_x = (inner_c * outer_b) - (outer_c * inner_b);
					double chislitel_y = (inner_a * outer_c) - (outer_a * inner_c);
					
					// Точка пересечения
					double res_x = (- (chislitel_x / znamenatel));
					double res_y = (- (chislitel_y / znamenatel));
					res_x = round7(res_x);
					res_y = round7(res_y);
					
					// Координаты точек отрезка внешнего полигона
					double x_min = outerBounds.x_points[j];
					double x_max = outerBounds.x_points[next];
					double y_min = outerBounds.y_points[j];
					double y_max = outerBounds.y_points[next]; 
					
					// Соблюдаем устовие, чтобы координаты первой точки в отрезке были меньше
					// координат второй точки отрезка
					if(x_min > x_max) {
						double tmp_x = x_min;
						
						x_min = x_max;
						x_max = tmp_x;
					}
					
					if(y_min > y_max) {
						double tmp_y = y_min;
						
						y_min = y_max;
						y_max = tmp_y;
					}
					
					// Ограничиваем прямые нашими отрезками
					if(((innerBounds.x_points[i] <= res_x) && (X >= res_x) && 
						(x_min <= res_x) && (x_max >= res_x)) && 
					   ((innerBounds.y_points[i] <= res_y) && (Y >= res_y) && 
					    (y_min <= res_y) && (y_max >= res_y))) {
						// Совпадает с крайней точкой ребра
						if(res_x == outerBounds.x_points[j] && res_y == outerBounds.y_points[j] && (cnt % 2 != 0))
							isAgain = true;
						else
							isAgain = false;
						
						if(!isAgain)
							cnt++;
					}
				}
			}
			
			// При четном кол-ве пересечений точка находится снаружи полигона, при нечетном - внутри
			if(cnt % 2 == 0) 
				contains = false;
		}
		
		return contains;
	}
	
	/**
	 * Перебирает все внешние полигоны и проверяет, есть ли в них одинаковые
	 * точки. Если есть, то удаляет их.
	 */
	
	public void deleteDublicatNodesInWays() {
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			long nodesCount = 0; // Счетчик точек для удаления	
			byte type = 0;
			
			OsmConverter.printLog("Удаляем точки-дубликаты из файла карты ...");
			
			if(Param.seek_ways != null && Param.seek_ways.size() != 0) {
				Collection<Long> ways_ids = Param.seek_ways.values();
				Iterator<Long> iterator_ways = ways_ids.iterator();
			
				// Перебираем линии
				while(iterator_ways.hasNext()) {
					long tmp_seek = iterator_ways.next();
					
					raf.seek(tmp_seek);
					type = raf.readByte();
					
					raf.seek(tmp_seek + Param.delete_seek);
					byte del = raf.readByte();
				
					// Удаляем точку в линии
					if(type == Param.poligon_outer && del != 0x01) {
						deleteDublicatNodes(raf.readLong(), raf);
						nodesCount++;
					}
				}
			}
			
			OsmConverter.printLog("Количество точек-дубликатов для удаления: " + nodesCount);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Удаляет тип объекта на карте у внешних полигонов, тип которых использовался в
	 * отношениях без типа объекта на карте (чтобы не было одинаковых объектов). 
	 */
	
	public void modifyTypeOfObjectInOuterWaysUsedInRelstions() {
		for(int i = 0; i < Param.ids_outer_ways_used_in_relation_for_delete.size(); i++) {
			try {
				long id = Param.ids_outer_ways_used_in_relation_for_delete.get(i);
			
				for(int j = 0; j < Param.outer_ways_used_in_relations.size(); j++) {
					long org_id = Param.outer_ways_used_in_relations.get(id);
					
					if(org_id > 0) {
						setTypeOfObject(Param.seek_ways.get(org_id), Param.noType);
						Param.outer_ways_used_in_relations.remove(id);
					}
				}
			} catch(NullPointerException ex) {
				continue;
			}
		}
		
		Param.ids_outer_ways_used_in_relation_for_delete.clear();
		Param.outer_ways_used_in_relations.clear();
	}
	
	/**
	 * Возвращает смещения всех точек файла карты.
	 * 
	 * @param raf объект для доступа к файлу
	 * @return список смещений всех точек файла карты
	 */
	
	public ArrayList<Long> getAllPoints(RandomAccessFile raf) {
		ArrayList<Long> nodeSeeks = new ArrayList<>();
		long seek = Param.dnvg_headerSize;	
		
		try {
			// Ищем все точки в файле карты
			while(!(seek >= raf.length())) {
				raf.seek(seek);
				byte type = raf.readByte();
			
				if(type == Param.point) {
					nodeSeeks.add(seek);
				}
			
				seek += Param.elementSize;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return nodeSeeks;
	}
	
	/**
	 * Возвращает смещения всех точек файла карты, которые имеют признак удаления.
	 * 
	 * @param raf объект для доступа к файлу
	 */
	
	public ArrayList<Long> getAll() {
		ArrayList<Long> nodeSeeks = new ArrayList<>();
		long seek = Param.dnvg_headerSize;	
		
		try {
			// Ищем все точки в файле карты
			while(!(seek >= raf.length())) {
				raf.seek(seek);
							
				nodeSeeks.add(seek);
			
				seek += Param.elementSize;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return nodeSeeks;
	}
	
	

	/**
	 * Создает байтовый массив заданной длины.
	 * 
	 * @param bytes длина нужного массива
	 * @return массив нужной длинны заполненый нулями 
	 */
	
	private byte[] setEmptyBytes(int bytes) {
		byte[] data = new byte[bytes];
		
		for(int i = 0; i < bytes; i++) {
			data[i] = 0x00;
		}
		
		return data;
	}
	
	/**
	 * Создает и записывает в файлы карты зачение контрольной суммы.
	 */
	
	public void createCRC32() {
		OsmConverter.printLog("Создаем контрольную сумму ... ");
		
		// Создаем слово (набор байтов) для создания уникальной контрольной суммы
		// основанной на названии файла карты и размере файлов
		// map.dnvg и map.hnvg		
		try {
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
		
			// Название файла карты
			String mapName = Param.mapName;
		
			// Размер файлов		
			String strDnvgSize = Long.toHexString(raf.length());						
			String strHnvgSize = Long.toHexString(h_raf.length());
			
			// Складываем все строковые переменнве и получаем массив в байтах
			String finalStr = mapName.concat(strDnvgSize).concat(strHnvgSize);
			byte [] word = finalStr.getBytes();
			
			CRC32 crc32 = new CRC32();
			crc32.update(word);
			
			long result = crc32.getValue();
			
			// Записываем контрольную сумму в файлы карты
			raf.seek(Param.crc32_seek);
			raf.writeLong(result);
			
			h_raf.seek(Param.crc32_seek);
			h_raf.writeLong(result);
			
			OsmConverter.printLog("Контрольная сумма " + Long.toHexString(result) + " создана и записана в файлы.");		
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
	 * Избавляется от чисел после седьмого знака после запятой.
	 * 
	 * @param val корректируемое число
	 * @return число с нулями после седьмого знака после запятой
	 */
	
	public static double round7(double val) {
		double tmp = val * 10000000;
		tmp = Math.round(tmp);
		tmp = tmp / 10000000;
		
		return tmp;
	}
}
