/*
 * Copyright (c) 03.2017
 */

package OsmConverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.mikhail.mj82.nvg.Converter.CheckNameLength;
import com.mikhail.mj82.nvg.Converter.NodeSAXHandler;
import com.mikhail.mj82.nvg.Converter.Param;
import com.mikhail.mj82.nvg.Converter.RelationSAXHandler;
import com.mikhail.mj82.nvg.Converter.RndAccessFile;
import com.mikhail.mj82.nvg.Converter.WaysSAXHandler;
import com.mikhail.mj82.nvg.Tree.RTreeCreator;
import com.mikhail.mj82.nvg.Triangulation.Triangulator;

/**
 * Класс является входной точкой приложения. В нем выбирают карту, которую необходимо 
 * конвертировать.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class OsmConverter {
	// Карты в формате xml для конвертирования 
//	private final static String path = "zelenogradsk.osm";
//	private final static String path = "svetlogorsk_pionersk.osm";
//	private final static String path = "chernyahovsk.osm";
//	private final static String path = "moscow.osm";
//	private final static String path = "s_piter.osm";
	private final static String path = "pangody.osm";
//	private final static String path = "Pangody_highway.osm";
//	private final static String path = "kaliningrad.osm";
//	private final static String path = "columbia.osm";
//	private final static String path = "nebug.osm";
//	private final static String path = "newyork.osm";
//	private final static String path = "RU-YAN.osm";
//	private final static String path = "RU-KGD.osm";
//	private final static String path = "rim.osm";
//	private final static String path = "braniewo.osm";
//	private final static String path = "map.osm";
	
	public static StringBuilder log_str = new StringBuilder(); // Текст для файла Log.txt
	public static FileWriter fw; // Создает файл лога и записывает в него текст
	
	// Входная точка приложения
	public static void main(String[] args) {
		File file = new File(path); // Путь к карте
		
		try {
			Param.setNameOfMapFile(file.getName());
			
			Param.dirLogs.mkdirs();
			fw = new FileWriter(Param.log_path);
		
		    RndAccessFile raf = new RndAccessFile(); // Записывает и считывает данные в/из файла
		
		    CheckNameLength check; // Ищем самую длинную надпись на карте
		    NodeSAXHandler node_handler; // Парсит точки
		    WaysSAXHandler way_handler; // Парсит линии
		    RelationSAXHandler relation_handler; // Парсит отношения*/
		    RTreeCreator creator; // Создает поисковые деревья
		    Triangulator triangulator; // Триангулирует полигоны карты		
		    
		    printLog("Файл: " + file.getName());
		    printLog("------------------------------");
		
		    // Используем SAX парсер
		
////////////// Ищем самое длинное описание объекта карты
			SAXParserFactory factory = SAXParserFactory.newInstance();		
			SAXParser parser = factory.newSAXParser();
			
			check = new CheckNameLength(file);
			
			if(file != null) {
				parser.parse(file, check);
			}
			
////////////// Парсим точки и записываем их в файл карты.
			node_handler = new NodeSAXHandler();			
			
			Date startTime = new Date();
			long timeStart = startTime.getTime();
			
			printLog("Записываем точки в файл карты ...");
			
			if(file != null) {
				node_handler.createNewFiles();			
				parser.parse(file, node_handler);
			}
			
			Date endTime = new Date();
			long timeEnd = endTime.getTime();
			
			printLog("Время записи точек в файл карты: " + (timeEnd - timeStart) + " ms\r\n");				
		
////////////// Парсим линии и вносим изменения в свойства точек	
//			FileWriter fwf = new FileWriter(Param.osm_path);
//			way_handler = new WaysSAXHandler(fwf);
			way_handler = new WaysSAXHandler();
			
			startTime = new Date();
			timeStart = startTime.getTime();
			
			printLog("Группируем точки в линии (полигоны) ...");
			printLog("Максимальный идентификатор точки до группировки линий: " + Param.maxNodeId);
			
			if(file != null)
				parser.parse(file, way_handler);
			
			endTime = new Date();
			timeEnd = endTime.getTime();
			
			printLog("Убираем из линий точки (кроме первой и последней) с одинаковыми координатами ...");
			printLog("Количество линий: " + Param.ids_ways_with_dublicat_coords.size());
			raf.deleteDublicatCoordsNodesInWay();
			
			printLog("Максимальный идентификатор точки после группировки линий: " + Param.maxNodeId);
			printLog("Количество точек после группировки точек в линии: " + Param.seek_nodes.size());
			printLog("Время группировки точек в линии: " + (timeEnd - timeStart) + " ms\r\n");
//			fwf.close();
			
////////////// Парсим отношения и вносим изменения в свойства линий
			relation_handler = new RelationSAXHandler();
			
			startTime = new Date();
			timeStart = startTime.getTime();
			
			printLog("Группируем линии в отношения ...");
						
			if(file != null)
				parser.parse(file, relation_handler);
			
			endTime = new Date();
			timeEnd = endTime.getTime();
			
			printLog("Количество точек после группировки линий в отношения: " + 
			          (Param.seek_nodes.size() + Param.delete_nodes.size()));
			printLog("Время группировки линий в отношения: " + (timeEnd - timeStart) + " ms\r\n");				
			
////////////// Добавляем доп. точку в элемент карты типа poligon_line
			printLog("Добавляем дополнительные точки в тип элемента карты poligon_line ...");
			printLog("Количество элементов карты с типом poligon_line: " + Param.seek_ways_with_poligon_line_type.size() + "\r\n");
			
			raf.setPointInPolygonLine();
			
			System.gc(); // Запускаем сборщик мусора
			
////////////// Удаляем ненужные точки и линии из файла карты
			startTime = new Date();
			timeStart = startTime.getTime();
			
			// Удалям тип объекта на карте у использованных в отношениях без типа полигонов
			raf.modifyTypeOfObjectInOuterWaysUsedInRelstions();			
			
			printLog("Проверяем все внешние полигоны на наличие дубликатов (полигоны, которые не использовались в отношениях) ...");		
			raf.deleteDublicatNodesInWays();
			
			printLog("Удаляем точки и линии из файла карты ...");			
			raf.deleteNodesAndWays();
			
			endTime = new Date();
			timeEnd = endTime.getTime();
			
			printLog("Количество точек после удаления: " + 
	                  (Param.seek_nodes.size() + Param.delete_nodes.size()));
			printLog("Время удаления точек из файла карты: " + (timeEnd - timeStart) + " ms\r\n");	

////////////// Переиндексируем точки карты
		    startTime = new Date();
			timeStart = startTime.getTime();
			
			printLog("Идет переиндексация точек карты ...");
			
			raf.setNewIndexes();
			
			endTime = new Date();
			timeEnd = endTime.getTime();
			
			printLog("Максимальное значение индекса: " + Param.newIndex);
			printLog("Время переиндексации точек карты: " + (timeEnd - timeStart) + " ms\r\n");

////////////// Триангулируем полигоны карты
		    startTime = new Date();
			timeStart = startTime.getTime();
			
			printLog("Идет триангуляция полигонов карты ...");
			
			triangulator = new Triangulator();
			triangulator.makeTriangulation();
			
			raf.deleteAttrsAfterTriangulation();
			
			endTime = new Date();
			timeEnd = endTime.getTime();
			
			printLog("Количество точек после триангуляции полигонов: " + Param.newIndex);
			printLog("Количество точек после триангуляции больше на: " + (Param.newIndex - Param.seek_nodes.size()));
			printLog("Время триангуляции полигонов карты: " + (timeEnd - timeStart) + " ms\r\n");
						
////////////// Создаем поисковое дерево карты
			startTime = new Date();
			timeStart = startTime.getTime();
			
			printLog("Создаем поисковые деревья карты ... ");
			
			creator = new RTreeCreator();
			creator.makeRTree();
//			creator.makeRTree(true); // Используем при больших файлах osm
			
			endTime = new Date();
			timeEnd = endTime.getTime();
			
			printLog("Время создания поисковых деревьев карты: " + (timeEnd - timeStart) + " ms\r\n");

//////////////Создаем контрольную сумму
			RndAccessFile my_raf = new RndAccessFile();
			my_raf.createCRC32(true);
//////////////			
			printLog("------------------------------");
			printLog("Первоначальный размер файла(байты): "+ file.length());
			printLog("Итоговый размер файла(байты): " + raf.getLengthFiles() + ". Разница (байты): " + 
			          (raf.getLengthFiles() - file.length()) + ". Проценты: " + 
					  ((100.0f * raf.getLengthFiles()) / file.length()) + "%");	
			
////////////// Закрываем поток записи в лог
			fw.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Файл карты для конвертации отсутствует.");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Выводит сообщения отладки в консоль и записывает их в файл лога.
	 * 
	 * @param str - сообщение отладки
	 */
	
	public static void printLog(String str) {
		System.out.println(str);
		log_str.setLength(0);
		log_str.append(str + "\r\n");		// Записываем файл Log.txt
		try {
			fw.write(log_str.toString());
			fw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		};
	}
}