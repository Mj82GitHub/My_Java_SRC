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
 * ����� �������� ������� ������ ����������. � ��� �������� �����, ������� ���������� 
 * ��������������.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class OsmConverter {
	// ����� � ������� xml ��� ��������������� 
//	private final static String path = "zelenogradsk.osm";
//	private final static String path = "svetlogorsk_pionersk.osm";
//	private final static String path = "chernyahovsk.osm";
//	private final static String path = "moscow.osm";
//	private final static String path = "s_piter.osm";
	private final static String path = "pangody.osm";
//	private final static String path = "Pan2.osm";
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
	
	public static StringBuilder log_str = new StringBuilder(); // ����� ��� ����� Log.txt
	public static FileWriter fw; // ������� ���� ���� � ���������� � ���� �����
	
	// ������� ����� ����������
	public static void main(String[] args) {
		File file = new File(path); // ���� � �����
		
		try {
			Param.setNameOfMapFile(file.getName());
			
			Param.dirLogs.mkdirs();
			fw = new FileWriter(Param.log_path);
		
		    RndAccessFile raf = new RndAccessFile(); // ���������� � ��������� ������ �/�� �����
		
		    CheckNameLength check; // ���� ����� ������� ������� �� �����
		    NodeSAXHandler node_handler; // ������ �����
		    WaysSAXHandler way_handler; // ������ �����
		    RelationSAXHandler relation_handler; // ������ ���������*/
		    RTreeCreator creator; // ������� ��������� �������
		    Triangulator triangulator; // ������������� �������� �����		
		    
		    printLog("����: " + file.getName());
		    printLog("------------------------------");
		
		    // ���������� SAX ������
		
////////////// ���� ����� ������� �������� ������� �����
			SAXParserFactory factory = SAXParserFactory.newInstance();		
			SAXParser parser = factory.newSAXParser();
			
			check = new CheckNameLength(file);
			
			if(file != null) {
				parser.parse(file, check);
			}
			
////////////// ������ ����� � ���������� �� � ���� �����.
			node_handler = new NodeSAXHandler();			
			
			Date startTime = new Date();
			long timeStart = startTime.getTime();
			
			printLog("���������� ����� � ���� ����� ...");
			
			if(file != null) {
				node_handler.createNewFiles();			
				parser.parse(file, node_handler);
			}
			
			Date endTime = new Date();
			long timeEnd = endTime.getTime();
			
			printLog("����� ������ ����� � ���� �����: " + (timeEnd - timeStart) + " ms\r\n");				
		
////////////// ������ ����� � ������ ��������� � �������� �����	
//			FileWriter fwf = new FileWriter(Param.osm_path);
//			way_handler = new WaysSAXHandler(fwf);
			way_handler = new WaysSAXHandler();
			
			startTime = new Date();
			timeStart = startTime.getTime();
			
			printLog("���������� ����� � ����� (��������) ...");
			printLog("������������ ������������� ����� �� ����������� �����: " + Param.maxNodeId);
			
			if(file != null)
				parser.parse(file, way_handler);
			
			endTime = new Date();
			timeEnd = endTime.getTime();
			
			printLog("������� �� ����� ����� (����� ������ � ���������) � ����������� ������������ ...");
			printLog("���������� �����: " + Param.ids_ways_with_dublicat_coords.size());
			raf.deleteDublicatCoordsNodesInWay();
			
			printLog("������������ ������������� ����� ����� ����������� �����: " + Param.maxNodeId);
			printLog("���������� ����� ����� ����������� ����� � �����: " + 
			         (Param.seek_nodes.size() + Param.delete_nodes.size()));
			printLog("����� ����������� ����� � �����: " + (timeEnd - timeStart) + " ms\r\n");
//			fwf.close();
			
////////////// ������ ��������� � ������ ��������� � �������� �����
			relation_handler = new RelationSAXHandler();
			
			startTime = new Date();
			timeStart = startTime.getTime();
			
			printLog("���������� ����� � ��������� ...");
						
			if(file != null)
				parser.parse(file, relation_handler);
			
			endTime = new Date();
			timeEnd = endTime.getTime();
			
			printLog("���������� ����� ����� ����������� ����� � ���������: " + 
			          (Param.seek_nodes.size() + Param.delete_nodes.size()));
			printLog("����� ����������� ����� � ���������: " + (timeEnd - timeStart) + " ms\r\n");				
			
////////////// ��������� ���. ����� � ������� ����� ���� poligon_line
			printLog("��������� �������������� ����� � ��� �������� ����� poligon_line ...");
			printLog("���������� ��������� ����� � ����� poligon_line: " + Param.seek_ways_with_poligon_line_type.size());
			
			raf.setPointInPolygonLine();
			
			printLog("���������� ����� ����� ���������� ���. ����� � ��� �������� ����� poligon_line: " + 
			          (Param.seek_nodes.size() + Param.delete_nodes.size()) + "\r\n");
			
			System.gc(); // ��������� ������� ������
			
////////////// ������� �������� ����� � ����� �� ����� �����
			startTime = new Date();
			timeStart = startTime.getTime();
			
			// ������ ��� ������� �� ����� � �������������� � ���������� ��� ���� ���������
			raf.modifyTypeOfObjectInOuterWaysUsedInRelstions();			
			
			printLog("��������� ��� ������� �������� �� ������� ���������� (��������, ������� �� �������������� � ����������) ...");		
			raf.deleteDublicatNodesInWays();
			
			printLog("������� ����� � ����� �� ����� ����� ...");			
			raf.deleteNodesAndWays();
			
			endTime = new Date();
			timeEnd = endTime.getTime();
			
			printLog("���������� ����� ����� ��������: " + 
	                  (Param.seek_nodes.size() + Param.delete_nodes.size()));
			printLog("����� �������� ����� �� ����� �����: " + (timeEnd - timeStart) + " ms\r\n");	

////////////// ��������������� ����� �����
		    startTime = new Date();
			timeStart = startTime.getTime();
			
			printLog("���� �������������� ����� ����� ...");
			
			raf.setNewIndexes();
			
			endTime = new Date();
			timeEnd = endTime.getTime();
			
			printLog("������������ �������� �������: " + Param.newIndex);
			printLog("����� �������������� ����� �����: " + (timeEnd - timeStart) + " ms\r\n");

////////////// ������������� �������� �����
		    startTime = new Date();
			timeStart = startTime.getTime();
			
			long beforeNodes = Param.new_seek_nodes.size(); // ���-�� ����� �� ������������
			
			printLog("���� ������������ ��������� ����� ...");
			
			triangulator = new Triangulator();
			triangulator.makeTriangulation();
			
			printLog("�������������� ��������� ����� ������������:");
			raf.deleteAttrsAndNodesAfterTriangulation();
			printLog("   ������ � ���� ������������� �������������� ����� �����: " + Param.newIndex);
			raf.setMaxId(Param.newIndex);
			
			endTime = new Date();
			timeEnd = endTime.getTime();
			
			printLog("���������� ����� ����� ������������ ���������: " + Param.new_seek_nodes.size());
			printLog("���������� ����� ����� ������������ ������ ��: " + ( Param.new_seek_nodes.size() - beforeNodes));
			printLog("����� ������������ ��������� �����: " + (timeEnd - timeStart) + " ms\r\n");
						
////////////// ������� ��������� ������ �����
			startTime = new Date();
			timeStart = startTime.getTime();
			
			printLog("������� ��������� ������� ����� ... ");
			
			creator = new RTreeCreator();
			creator.makeRTree();
//			creator.makeRTree(true); // ���������� ��� ������� ������ osm
			
			endTime = new Date();
			timeEnd = endTime.getTime();
			
			printLog("����� �������� ��������� �������� �����: " + (timeEnd - timeStart) + " ms\r\n");

//////////////������� ����������� �����
			RndAccessFile my_raf = new RndAccessFile();
			my_raf.createCRC32(true);
//////////////			
			printLog("------------------------------");
			printLog("�������������� ������ �����(�����): "+ file.length());
			printLog("�������� ������ �����(�����): " + raf.getLengthFiles() + ". ������� (�����): " + 
			          (raf.getLengthFiles() - file.length()) + ". ��������: " + 
					  ((100.0f * raf.getLengthFiles()) / file.length()) + "%");	
			
////////////// ��������� ����� ������ � ���
			fw.close();
		} catch (FileNotFoundException ex) {
			System.out.println("���� ����� ��� ����������� �����������.");
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
	 * ������� ��������� ������� � ������� � ���������� �� � ���� ����.
	 * 
	 * @param str - ��������� �������
	 */
	
	public static void printLog(String str) {
		System.out.println(str);
		log_str.setLength(0);
		log_str.append(str + "\r\n");		// ���������� ���� Log.txt
		try {
			fw.write(log_str.toString());
			fw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		};
	}
}