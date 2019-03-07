/*
 * Copyright (c) 12.2016
 */

package com.mikhail.mj82.Converter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Класс содержит переменные общие для всех частей программы.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class Param {
	
	public static File dirMap = new File("NVG/Maps"); // Путь к карте
	public static File dirIndexes = new File("NVG/Indexes"); // Путь к деревьям поиска
	public static File dirLogs = new File("NVG/Logs"); // Путь к логам (файлам отладки)
	
//	public static File dirOsm = new File("NVG/Logs"); // Путь к логам (файлам отладки)
	
	public static String mapName = "map"; // Имя файла карты после конвертации по умолчанию
	public static final String hnvg_extension = ".hnvg"; // Расширение  заголовочного файла карты после конвертации
	public static final String dnvg_extension = ".dnvg"; // Расширение файла карты с данными после конвертации
	public static final String nvg_extension = ".nvg"; // Расширение файла карты (не редактируемого) после конвертации
	
	public static String tree_path; // Полный путь к файлу деревьев
	public static String hnvg_file_path; // Полный путь к заголовочному файлу карты
	public static String dnvg_file_path; // Полный путь к файлу карты с данными
	public static String nvg_file_path; // Полный путь к файлу карты (не редактируемому)
	public static String log_path; // Полный путь к файлу лога (отладки)
	
//	public static String osm_path;
	
	// Форматы файлов нашего приложения
	public static final byte[] hnvg_format = "HNVG".getBytes(); 
	public static final byte[] dnvg_format = "DNVG".getBytes(); 
	public static final byte[] nvg_format = "NVG".getBytes(); 
	
	// Тип проверки нахождения внутренних полигонов внутри внешнего
	public static final int ray = 1; // Лучом
	public static final int bound = 2; // Ограничивающим прямоугольником
	
	// Тип элемента
	public static final byte point = 0x01; // 1 - точка
	public static final byte line = 0x02; // 2 - линия
	public static final byte poligon_line = 0x03; // 3 - линия замкнутая (временный тип, пока не заменят на line)
	public static final byte poligon_outer = 0x04; // 4 - полигон (внешний) 
	public static final byte poligon_inner = 0x05; // 5 - полигон (внутренний) временный, пока не присоединится к своиму внешнему
	public static final byte poligon_inner_composition = 0x06; // 6 - полигон (внутренний), соединенный в отношении со своим внешним
	
	// Признак удаления элемента
	public static final byte delete = 0x01;
	//Признак контура
	public static final byte boundary = 0x01;
	
	// Смещения в структуре заголовков файлов карт (map.hnvg и map.dnvg) 
	public static final long start_seek = 0; // Начало файла 
	public static final long crc32_seek = 4; // Контрольная сумма файла карты 
	public static final long minLat_seek = 12; // Минимальное значение широты
	public static final long minLon_seek = 20; // Минимальное значение долготы
	public static final long maxLat_seek = 28; // Максимальное значение широты
	public static final long maxLon_seek = 36; // Максимальное значение долготы
	
	// Смещения в структуре атрибутов файла карты map.hnvg
	public static final long myselfAttr_seek = 0; // Свое собственное смещение
	public static final long firsPointInWay_seek = 8; // Смещение первой точки элемента обекта карты в файле map.dnvg
	public static final long typeOfObject_seek = 16; // Тип объекта на карте
	public static final long additionalTypeOfObject_seek = 18; // Дополнительный тип объекта на карте
	public static final long properyType_seek = 20; // Свойство объекта на карте
	public static final long description_seek = 21; // Описание объекта карты	
	
	// Смещения в структуре элементов карты файла карты map.dnvg
	public static final long type_seek = 0; // Тип элемента карты
	public static final long delete_seek = 1; // Удаление элемента
	public static final long id_seek = 2; // Идентификатор
	public static final long myself_seek = 10; // Свое собственное смещение
	public static final long next_seek = 18; // Смещение следующего элемента
	public static final long attr_seek = 26; // Смещение в файле map.hnvg, где находится аттрибутивная информация по объекту карты
	public static final long lat_seek = 34; // Широта
	public static final long lon_seek = 42; // Долгота
	public static final long alt_seek = 50; // Высота
	public static final long acc_seek = 54; // Точность
	public static final long boundary_seek = 58; // Признак принадлежности элемента к ограничивающему контуру площадного объекта карты
	
	// Смещения в структуре элементов карты файла карты map.nvg
	// Головной блок элемента
	public static final long header_nvg_type_seek = 0; // Тип элемента карты
	public static final long header_nvg_DescriptionSize_seek = 1; // Размер описания в байтах
	public static final long header_nvg_id_seek = 9; // Идентификатор
	public static final long header_nvg_myself_seek = 17; // Свое собственное смещение
	public static final long header_nvg_next_seek = 25; // Смещение следующего элемента
	public static final long header_nvg_firstPointInWay_seek = 33; // Смещение первой точки в линии
	public static final long header_nvg_lat_seek = 41; // Широта
	public static final long header_nvg_lon_seek = 49; // Долгота
	public static final long header_nvg_alt_seek = 57; // Высота
	public static final long header_nvg_acc_seek = 61; // Точность
	public static final long header_nvg_typeOfObject_seek = 65; // Тип объекта на карте
	public static final long header_nvg_additionalTypeOfObject_seek = 67; // Дополнительный тип объекта на карте
	public static final long header_nvg_properyType_seek = 69; // Свойство объекта на карте
	public static final long header_nvg_description_seek = 70; // Описание объекта карты	
	// Остальные блоки элемента
	public static final long nvg_type_seek = 0; // Тип элемента карты
	public static final long nvg_id_seek = 1; // Идентификатор
	public static final long nvg_myself_seek = 9; // Свое собственное смещение
	public static final long nvg_next_seek = 17; // Смещение следующего элемента
	public static final long nvg_firstPointInWay_seek = 25; // Смещение первой точки в линии
	public static final long nvg_lat_seek = 33; // Широта
	public static final long nvg_lon_seek = 41; // Долгота
	public static final long nvg_alt_seek = 49; // Высота
	public static final long nvg_acc_seek = 53; // Точность
	
	// Типы объектов карты 
	public static final short innerType = -2; // внутренняя область (для внутренних полигонов)
	public static final short unknownType = -1; // неизвестный тип
	public static final short noType = 0; // не определен
	
	// Свойства типа элемента
	public static final byte property_bridge = 0x01; // мост
	public static final byte property_tunnel = 0x02; // тунель
	
	public static HashMap<Long, Long> seek_nodes = new HashMap<>(); // Карта смещений всех точек в файле map.dnvg
	public static HashMap<Long, Long> seek_ways = new HashMap<>(); // Карта смещений первой точки в линии в файле
	public static HashMap<Long, Long> seek_attrs = new HashMap<>(); // Карта смещений атрибутов в файле
	public static HashMap<Long, Long> seek_nodes_used = new HashMap<>(); // Карта смещений точек используемых в линиях	
	public static HashMap<Long, Long> seek_nodes_without_ways = new HashMap<>(); // Карта смещений точек в файле с вычетом точек в линиях
	public static HashMap<Long, Long> new_seek_nodes = new HashMap<Long, Long>(); // Таблица с новыми (своими) идентификаторами
	public static HashMap<Long, Long> seek_ways_used = new HashMap<Long, Long>(); // Карта смещений линий используемых в отношениях
//	public static HashMap<Long, Long> seek_ways_used_in_relations = new HashMap<Long, Long>(); // Линии, которые используются в отношениях
	public static HashMap<Long, Long> outer_ways_used_in_relations = new HashMap<Long, Long>(); // Хранит соответствия идентификаторов линий внешних полигонов и копий этих же линий, после того, как они становятся использованными в отношении 
	public static HashMap<Long, Integer> num_points = new HashMap<>(); // Кол-во точек в линиях 
	public static HashMap<Long, Long> delete_nodes = new HashMap<>(); // Карта смещений всех удаленных (подготовленных к удалению) точек в файле map.dnvg
	public static HashMap<Long, Long> my_ways_ids = new HashMap<>(); // Соответствие идентификаторов линий в файле xml с моими идентификаторами линий (идентификаторы первых точек в линии)
	public static HashMap<Long, Long> new_seek_only_nodes = new HashMap<>(); // Смещения только точечных объектов карты с новыми идентификаторами
	public static HashMap<Long, Long> new_seek_only_ways = new HashMap<>(); // Смещения только линейных либо площадных объектов карты с новыми идентификаторами
	public static HashMap<Long, ArrayList<Long>> ids_outer_ways_with_inner_ways = new HashMap<>(); // Карта идентификаторов внутренних полигонов, принадлежащих одному внешнему (ключ - это идентификатор внешнего полигона, значение - массив идентификаторов начальных точек внутренних полигонов) 
	
	// Список идентификаторов линий в отношениях, которые использовались для задания типа объекта на
	// карте при отсутствии этого типа у самого отношения
	public static ArrayList<Long> ids_outer_ways_used_in_relation_for_delete = new ArrayList<>();
	// Список идентификаторов линий имеющих одинаковые координаты у двух соседних точек
	public static ArrayList<Long> ids_ways_with_dublicat_coords = new ArrayList<>();
	// Список смещений линий имеющих тип элемента poligon_line
	public static ArrayList<Long> seek_ways_with_poligon_line_type = new ArrayList<>();
	
	public static ArrayList<Long> osm_ways = new ArrayList<>();
	
	public static ArrayList<Long> attrs = new ArrayList<>(); // Список атрибутов карты
	public static ArrayList<Long> delete_attrs = new ArrayList<>(); // Список смещений всех удаленных (подготовленных к удалению) атрибутов в файле map.hnvg
	
	public static long maxNodeId = -1; // Наибольшее значение идентификатора точки
	
	public static final long hnvg_headerSize = 12; // Размер заголовка map.hnvg файла карты
	public static final long dnvg_headerSize = 44; // Размер заголовка map.dnvg файла карты
	public static final long nvg_headerSize = 35; // Размер заголовка map.nvg файла карты
	
	public static final long attrSize = 21; // Общий размер данных атрибута элемента без описания (в байтах) map.hnvg файла
	public static final long elementSize = 59; // Общий размер данных элемента (в байтах) map.dnvg файла
	public static final long heder_nvg_elementSize = 70; // Общий размер данных головного элемента без описания (в байтах) map.nvg файла
	public static final long nvg_elementSize = 57; // Общий размер данных остальных элементов map.nvg файла	
	
	public static final int description_size = 250; // Кол-во байт на описание объекта карты	
	public static final long attrBlockSize = attrSize + (long) description_size; // Общий размер данных атрибута элемента (в байтах) map.hnvg файла
	public static final long blockSize = heder_nvg_elementSize + (long) description_size; // Общий размер данных головного элемента (в байтах) map.nvg файла
	
	public static long newIndex = 0; // Идентификатор, с которого начинаются идентификаторы всех точек в моей карте
	public static boolean seekChanged = false; // Признак того, что старые идентификаторы заменили новыми
	public static boolean makeFromFile = false; // Признак того, что поисковое дерево строится из данных файла (карты смещений не используются)
	
	// Используется для поиска общих точек в отдельных линиях
	public static enum sort_points { first_first, first_last, last_first, last_last };
	
	/**
	 * Задает названия файлов карты, таким же как и у исходного xml файла.
	 * 
	 * @param fileName - название исходного xml файла
	 */
	
	public static void setNameOfMapFile(String fileName) {
		mapName = fileName.substring(0, fileName.length() - 4);
		
		tree_path = dirIndexes + "/" + mapName; // Полный путь к файлу деревьев
		hnvg_file_path = dirMap + "/" + mapName + hnvg_extension; // Полный путь к файлу карты
		dnvg_file_path = dirMap + "/" + mapName + dnvg_extension; // Полный путь к файлу карты
		nvg_file_path = dirMap + "/" + mapName + nvg_extension; // Полный путь к файлу карты
		log_path = dirLogs + "/" + mapName + "_log" + ".txt"; // Полный путь к файлу лога (отладки)
		
		log_path = dirLogs + "/" + mapName + "_log" + ".txt"; // Полный путь к файлу лога (отладки)
		
//		osm_path = dirLogs + "/" + mapName + "_osm" + ".txt";
	}
	
	/**
	 * Обнуляет неиспользуемые переменные.
	 */
	
	public static void clearOldVariables() {
		seek_nodes = null;
		seek_ways = null;
		seek_attrs = null;
		seek_nodes_used = null;
		seek_nodes_without_ways = null;
		seek_ways_used = null;
		outer_ways_used_in_relations = null;
		delete_nodes = null;
		my_ways_ids = null;
		ids_outer_ways_with_inner_ways = null;
		ids_outer_ways_used_in_relation_for_delete = null;
		ids_ways_with_dublicat_coords = null;
		seek_ways_with_poligon_line_type = null;
		attrs = null;
		delete_attrs = null;
	}
	
//	public static ArrayList<Long> inner = new ArrayList<>(); 
//	public static ArrayList<Long> inner_from_way = new ArrayList<>(); 
}
