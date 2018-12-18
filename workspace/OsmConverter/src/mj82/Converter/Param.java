/*
 * Copyright (c) 12.2016
 */

package mj82.Converter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * ����� �������� ���������� ����� ��� ���� ������ ���������.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class Param {
	
	public static File dirMap = new File("NVG/Maps"); // ���� � �����
	public static File dirIndexes = new File("NVG/Indexes"); // ���� � �������� ������
	public static File dirLogs = new File("NVG/Logs"); // ���� � ����� (������ �������)
	
//	public static File dirOsm = new File("NVG/Logs"); // ���� � ����� (������ �������)
	
	public static String mapName = "map"; // ��� ����� ����� ����� ����������� �� ���������
	public static final String hnvg_extension = ".hnvg"; // ����������  ������������� ����� ����� ����� �����������
	public static final String dnvg_extension = ".dnvg"; // ���������� ����� ����� � ������� ����� �����������
	public static final String nvg_extension = ".nvg"; // ���������� ����� ����� (�� ��������������) ����� �����������
	
	public static String tree_path; // ������ ���� � ����� ��������
	public static String hnvg_file_path; // ������ ���� � ������������� ����� �����
	public static String dnvg_file_path; // ������ ���� � ����� ����� � �������
	public static String nvg_file_path; // ������ ���� � ����� ����� (�� ��������������)
	public static String log_path; // ������ ���� � ����� ���� (�������)
	
//	public static String osm_path;
	
	// ������� ������ ������ ����������
	public static final byte[] hnvg_format = "HNVG".getBytes(); 
	public static final byte[] dnvg_format = "DNVG".getBytes(); 
	public static final byte[] nvg_format = "NVG".getBytes(); 
	
	// ��� �������� ���������� ���������� ��������� ������ ��������
	public static final int ray = 1; // �����
	public static final int bound = 2; // �������������� ���������������
	
	// ��� ��������
	public static final byte point = 0x01; // 1 - �����
	public static final byte line = 0x02; // 2 - �����
	public static final byte poligon_line = 0x03; // 3 - ����� ��������� (��������� ���, ���� �� ������� �� line)
	public static final byte poligon_outer = 0x04; // 4 - ������� (�������) 
	public static final byte poligon_inner = 0x05; // 5 - ������� (����������) ���������, ���� �� ������������� � ������ ��������
	public static final byte poligon_inner_composition = 0x06; // 6 - ������� (����������), ����������� � ��������� �� ����� �������
	
	// ������� �������� ��������
	public static final byte delete = 0x01;
	//������� �������
	public static final byte boundary = 0x01;
	
	// �������� � ��������� ���������� ������ ���� (map.hnvg � map.dnvg) 
	public static final long start_seek = 0; // ������ ����� 
	public static final long crc32_seek = 4; // ����������� ����� ����� ����� 
	public static final long minLat_seek = 12; // ����������� �������� ������
	public static final long minLon_seek = 20; // ����������� �������� �������
	public static final long maxLat_seek = 28; // ������������ �������� ������
	public static final long maxLon_seek = 36; // ������������ �������� �������
	
	// �������� � ��������� ��������� ����� ����� map.hnvg
	public static final long myselfAttr_seek = 0; // ���� ����������� ��������
	public static final long firsPointInWay_seek = 8; // �������� ������ ����� �������� ������ ����� � ����� map.dnvg
	public static final long typeOfObject_seek = 16; // ��� ������� �� �����
	public static final long additionalTypeOfObject_seek = 18; // �������������� ��� ������� �� �����
	public static final long properyType_seek = 20; // �������� ������� �� �����
	public static final long description_seek = 21; // �������� ������� �����	
	
	// �������� � ��������� ��������� ����� ����� ����� map.dnvg
	public static final long type_seek = 0; // ��� �������� �����
	public static final long delete_seek = 1; // �������� ��������
	public static final long id_seek = 2; // �������������
	public static final long myself_seek = 10; // ���� ����������� ��������
	public static final long next_seek = 18; // �������� ���������� ��������
	public static final long attr_seek = 26; // �������� � ����� map.hnvg, ��� ��������� ������������� ���������� �� ������� �����
	public static final long lat_seek = 34; // ������
	public static final long lon_seek = 42; // �������
	public static final long alt_seek = 50; // ������
	public static final long acc_seek = 54; // ��������
	public static final long boundary_seek = 58; // ������� �������������� �������� � ��������������� ������� ���������� ������� �����
	
	// �������� � ��������� ��������� ����� ����� ����� map.nvg
	// �������� ���� ��������
	public static final long header_nvg_type_seek = 0; // ��� �������� �����
	public static final long header_nvg_DescriptionSize_seek = 1; // ������ �������� � ������
	public static final long header_nvg_id_seek = 9; // �������������
	public static final long header_nvg_myself_seek = 17; // ���� ����������� ��������
	public static final long header_nvg_next_seek = 25; // �������� ���������� ��������
	public static final long header_nvg_firstPointInWay_seek = 33; // �������� ������ ����� � �����
	public static final long header_nvg_lat_seek = 41; // ������
	public static final long header_nvg_lon_seek = 49; // �������
	public static final long header_nvg_alt_seek = 57; // ������
	public static final long header_nvg_acc_seek = 61; // ��������
	public static final long header_nvg_typeOfObject_seek = 65; // ��� ������� �� �����
	public static final long header_nvg_additionalTypeOfObject_seek = 67; // �������������� ��� ������� �� �����
	public static final long header_nvg_properyType_seek = 69; // �������� ������� �� �����
	public static final long header_nvg_description_seek = 70; // �������� ������� �����	
	// ��������� ����� ��������
	public static final long nvg_type_seek = 0; // ��� �������� �����
	public static final long nvg_id_seek = 1; // �������������
	public static final long nvg_myself_seek = 9; // ���� ����������� ��������
	public static final long nvg_next_seek = 17; // �������� ���������� ��������
	public static final long nvg_firstPointInWay_seek = 25; // �������� ������ ����� � �����
	public static final long nvg_lat_seek = 33; // ������
	public static final long nvg_lon_seek = 41; // �������
	public static final long nvg_alt_seek = 49; // ������
	public static final long nvg_acc_seek = 53; // ��������
	
	// ���� �������� ����� 
	public static final short innerType = -2; // ���������� ������� (��� ���������� ���������)
	public static final short unknownType = -1; // ����������� ���
	public static final short noType = 0; // �� ���������
	
	// �������� ���� ��������
	public static final byte property_bridge = 0x01; // ����
	public static final byte property_tunnel = 0x02; // ������
	
	public static HashMap<Long, Long> seek_nodes = new HashMap<>(); // ����� �������� ���� ����� � ����� map.dnvg
	public static HashMap<Long, Long> seek_ways = new HashMap<>(); // ����� �������� ������ ����� � ����� � �����
	public static HashMap<Long, Long> seek_attrs = new HashMap<>(); // ����� �������� ��������� � �����
	public static HashMap<Long, Long> seek_nodes_used = new HashMap<>(); // ����� �������� ����� ������������ � ������	
	public static HashMap<Long, Long> seek_nodes_without_ways = new HashMap<>(); // ����� �������� ����� � ����� � ������� ����� � ������
	public static HashMap<Long, Long> new_seek_nodes = new HashMap<Long, Long>(); // ������� � ������ (������) ����������������
	public static HashMap<Long, Long> seek_ways_used = new HashMap<Long, Long>(); // ����� �������� ����� ������������ � ����������
//	public static HashMap<Long, Long> seek_ways_used_in_relations = new HashMap<Long, Long>(); // �����, ������� ������������ � ����������
	public static HashMap<Long, Long> outer_ways_used_in_relations = new HashMap<Long, Long>(); // ������ ������������ ��������������� ����� ������� ��������� � ����� ���� �� �����, ����� ����, ��� ��� ���������� ��������������� � ��������� 
	public static HashMap<Long, Integer> num_points = new HashMap<>(); // ���-�� ����� � ������ 
	public static HashMap<Long, Long> delete_nodes = new HashMap<>(); // ����� �������� ���� ��������� (�������������� � ��������) ����� � ����� map.dnvg
	public static HashMap<Long, Long> my_ways_ids = new HashMap<>(); // ������������ ��������������� ����� � ����� xml � ����� ���������������� ����� (�������������� ������ ����� � �����)
	public static HashMap<Long, Long> new_seek_only_nodes = new HashMap<>(); // �������� ������ �������� �������� ����� � ������ ����������������
	public static HashMap<Long, Long> new_seek_only_ways = new HashMap<>(); // �������� ������ �������� ���� ��������� �������� ����� � ������ ����������������
	public static HashMap<Long, ArrayList<Long>> ids_outer_ways_with_inner_ways = new HashMap<>(); // ����� ��������������� ���������� ���������, ������������� ������ �������� (���� - ��� ������������� �������� ��������, �������� - ������ ��������������� ��������� ����� ���������� ���������) 
	
	// ������ ��������������� ����� � ����������, ������� �������������� ��� ������� ���� ������� ��
	// ����� ��� ���������� ����� ���� � ������ ���������
	public static ArrayList<Long> ids_outer_ways_used_in_relation_for_delete = new ArrayList<>();
	// ������ ��������������� ����� ������� ���������� ���������� � ���� �������� �����
	public static ArrayList<Long> ids_ways_with_dublicat_coords = new ArrayList<>();
	// ������ �������� ����� ������� ��� �������� poligon_line
	public static ArrayList<Long> seek_ways_with_poligon_line_type = new ArrayList<>();
	
	public static ArrayList<Long> osm_ways = new ArrayList<>();
	
	public static ArrayList<Long> attrs = new ArrayList<>(); // ������ ��������� �����
	public static ArrayList<Long> delete_attrs = new ArrayList<>(); // ������ �������� ���� ��������� (�������������� � ��������) ��������� � ����� map.hnvg
	
	public static long maxNodeId = -1; // ���������� �������� �������������� �����
	
	public static final long hnvg_headerSize = 12; // ������ ��������� map.hnvg ����� �����
	public static final long dnvg_headerSize = 44; // ������ ��������� map.dnvg ����� �����
	public static final long nvg_headerSize = 35; // ������ ��������� map.nvg ����� �����
	
	public static final long attrSize = 21; // ����� ������ ������ �������� �������� ��� �������� (� ������) map.hnvg �����
	public static final long elementSize = 59; // ����� ������ ������ �������� (� ������) map.dnvg �����
	public static final long heder_nvg_elementSize = 70; // ����� ������ ������ ��������� �������� ��� �������� (� ������) map.nvg �����
	public static final long nvg_elementSize = 57; // ����� ������ ������ ��������� ��������� map.nvg �����	
	
	public static final int description_size = 250; // ���-�� ���� �� �������� ������� �����	
	public static final long attrBlockSize = attrSize + (long) description_size; // ����� ������ ������ �������� �������� (� ������) map.hnvg �����
	public static final long blockSize = heder_nvg_elementSize + (long) description_size; // ����� ������ ������ ��������� �������� (� ������) map.nvg �����
	
	public static long newIndex = 0; // �������������, � �������� ���������� �������������� ���� ����� � ���� �����
	public static boolean seekChanged = false; // ������� ����, ��� ������ �������������� �������� ������
	
	// ������������ ��� ������ ����� ����� � ��������� ������
	public static enum sort_points { first_first, first_last, last_first, last_last };
	
	/**
	 * ������ �������� ������ �����, ����� �� ��� � � ��������� xml �����.
	 * 
	 * @param fileName - �������� ��������� xml �����
	 */
	
	public static void setNameOfMapFile(String fileName) {
		mapName = fileName.substring(0, fileName.length() - 4);
		
		tree_path = dirIndexes + "/" + mapName; // ������ ���� � ����� ��������
		hnvg_file_path = dirMap + "/" + mapName + hnvg_extension; // ������ ���� � ����� �����
		dnvg_file_path = dirMap + "/" + mapName + dnvg_extension; // ������ ���� � ����� �����
		nvg_file_path = dirMap + "/" + mapName + nvg_extension; // ������ ���� � ����� �����
		log_path = dirLogs + "/" + mapName + "_log" + ".txt"; // ������ ���� � ����� ���� (�������)
		
		log_path = dirLogs + "/" + mapName + "_log" + ".txt"; // ������ ���� � ����� ���� (�������)
		
//		osm_path = dirLogs + "/" + mapName + "_osm" + ".txt";
	}
	
	/**
	 * �������� �������������� ����������.
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
