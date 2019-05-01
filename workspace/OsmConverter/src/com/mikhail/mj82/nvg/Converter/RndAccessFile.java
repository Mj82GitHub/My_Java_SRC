/*
 * Copyright (c) 12.2016
 */

package com.mikhail.mj82.nvg.Converter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.zip.CRC32;

import com.mikhail.mj82.nvg.Geom.JPolygon;
import com.mikhail.mj82.nvg.Geom.JRect;
import com.mikhail.mj82.nvg.Tree.RTrees;

import OsmConverter.OsmConverter;

/**
 * ����� ���������� � ��������� ������ �/�� ����(�).
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class RndAccessFile {
	
	private RandomAccessFile raf; // ������ ������� � ���� map.dnvg
	private RandomAccessFile h_raf; // ������ ������� � ���� map.hnvg
	private RandomAccessFile i_raf; // ������ ������� � ���� index_map
	
	/**
	 * ������� ����� ������ ����� ����� (map.hnvg � map.dnvg) � 
	 * ���������� � ��� ������� (����) ������ �����. 
	 */
	
	public void createNewFiles() {
		try {
			Param.dirMap.mkdirs();
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			h_raf.setLength(0);
			
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			raf.setLength(0);
			
/*			Param.dirIndexes.mkdirs();
			i_raf = new RandomAccessFile(Param.tree_path, "rw");
			i_raf.setLength(0);*/
			
			setFormats();
//			setMapNameInIndexFile();
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
	 * ���������� � ������ ������ ����� (map.hnvg � map.dnvg) ������� 
	 * (����������� ����) ������ ����� � ������� �������� �����������
	 * ����� ����� ����� map.dnvg.
	 */
	
	private void setFormats() {
		try {			
			// ���������� ������� ����	
			// � map.hnvg �����
			h_raf.seek(Param.start_seek);
			h_raf.write(Param.hnvg_format);
			
			// � map.dnvg �����
			raf.seek(Param.start_seek);
			raf.write(Param.dnvg_format);
			
			// � index �����
//			i_raf.seek(Param.start_seek);
//			i_raf.write(Param.index_format);
			
			// ��������� ����� ��� ����������� �����
			// � map.hnvg �����
			h_raf.seek(Param.crc32_seek);
			h_raf.writeLong(0);
			
			// � map.dnvg �����
			raf.seek(Param.crc32_seek);
			raf.writeLong(0);
			
			// � index �����
//			i_raf.seek(Param.crc32_seek);
//			i_raf.writeLong(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ���������� � ��������� ���� ��� ����� � ������ � ������ ����� �����.
	 */
	private void setMapNameInIndexFile() {
		try {
			// ������ ����� ����� � ������ 
			int nameSize = Param.mapName.getBytes("UTF-8").length;
			
			i_raf.seek(Param.map_index_descriptionSize_seek);
			i_raf.writeInt(nameSize);
			
			i_raf.seek(Param.map_index_name_seek);			
			i_raf.writeUTF(Param.mapName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ���������� � ��������� ���� ������ �������� �� ���������� ������.
	 * 
	 * @param objectSeek �������� ������� � ����� map.dnvg
	 * @param mbr �������������� ������� ����� �������������
	 * @param raf ������ ��� ������� � �����
	 */
	public void setGpsObjectInIndexFile(long objectSeek, JRect mbr, RandomAccessFile raf) {
		try {
			long seek = raf.length();	
			
			// �������� �������� ����� � map.dnvg
			raf.seek(seek);
			raf.writeLong(objectSeek); 
			
			// ����� ������� ��������������� ������� ����� ��������������
			raf.seek(seek + Param.leftMbr_seek);
			raf.writeDouble(mbr.left);
			
			// ������� ������� ��������������� ������� ����� ��������������
			raf.seek(seek + Param.topMbr_seek);
			raf.writeDouble(mbr.top);
			
			// ������ ������� ��������������� ������� ����� ��������������
			raf.seek(seek + Param.rightMbr_seek);
			raf.writeDouble(mbr.right);
			
			// ������ ������� ��������������� ������� ����� ��������������
			raf.seek(seek + Param.bottomMbr_seek);
			raf.writeDouble(mbr.bottom);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ���������� � ���� map.dnvg ������� �����.
	 * 
	 * @param minLat ����������� �������� ������
	 * @param minLon ����������� �������� �������
	 * @param maxLat ������������ �������� ������
	 * @param maxLon ������������ �������� �������
	 */
	
	public void setMapBounds(double minLat, double minLon, double maxLat, double maxLon) {
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			// ����������� �������� ������
			long seek = Param.minLat_seek;			
			raf.seek(seek);
			raf.writeDouble(minLat);
			
			// ����������� �������� �������
			seek = Param.minLon_seek;			
			raf.seek(seek);
			raf.writeDouble(minLon);
			
			// ������������ �������� ������ 
			seek = Param.maxLat_seek;			
			raf.seek(seek);
			raf.writeDouble(maxLat);
			
			// ������������ �������� �������
			seek = Param.maxLon_seek;			
			raf.seek(seek);
			raf.writeDouble(maxLon);
			
			// ��������� ����� ��� ������������ ������������� ����� �����
			setMaxId(0, raf);
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
	 * ���������� � ���� �������� ������������� �������������� �����.
	 * 
	 * @param id ������������ ������������� �����
	 * @param raf ������ ��� ������� � �����
	 */
	public void setMaxId(long id) {
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			raf.seek(Param.maxId_seek);
			raf.writeLong(id);
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
	 * ���������� � ���� �������� ������������� �������������� �����.
	 * 
	 * @param id ������������ ������������� �����
	 * @param raf ������ ��� ������� � �����
	 */
	public void setMaxId(long id, RandomAccessFile raf) {
		try {
			raf.seek(Param.maxId_seek);
			raf.writeLong(id);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ����������� ������� ��������� ������ �� ����������, ���� ��� �� ��������.
	 * 
	 * @param outerWayId ������������� �������� ��������
	 * @param innerWaysId ������ ��������������� ���������� ���������
	 * @param method ����� ������ ���������� ���������
	 */
	
	public void assignedOuterWaysInnerWays(long outerWayId, ArrayList<Long> innerWaysId, int method) {
		// ������� �������		
		ArrayList<Long> nodes_ids_outer_way = new ArrayList<>(); // �������������� ����� �������� ��������
		nodes_ids_outer_way.addAll(getNodeIdsFromWay(outerWayId));
		
		// ������� (��� X) ����� �������� ��������
		double [] lon_x_outer = new double[nodes_ids_outer_way.size()]; // ������� (��� X) ����� �������� ��������
		lon_x_outer = Arrays.copyOf(getLongitude(nodes_ids_outer_way), nodes_ids_outer_way.size());
		
		// ������ (��� Y) ����� �������� ��������
		double [] lat_y_outer = new double[nodes_ids_outer_way.size()]; // ������ (��� Y) ����� �������� ��������
		lat_y_outer = Arrays.copyOf(getLatitude(nodes_ids_outer_way), nodes_ids_outer_way.size());
		
		// ������� �������������� ������ �������� �� ����� �������� ��������
		JPolygon outerPolygon = new JPolygon(lon_x_outer, lat_y_outer, nodes_ids_outer_way.size());
		
		// ���������� �������
		
		// �������� ���������� ���������, ������������� ��������� �������� ��������
		ArrayList<Long> ids_inner_ways = new ArrayList<>(); 
		ArrayList<Long> nodes_ids_inner_way = new ArrayList<>(); // �������������� ����� ����������� ��������	
		
		for(int i = 0; i < innerWaysId.size(); i++) {
			boolean contains = false; // ������� ����, ��� ���������� ������� ����������� ��������			
			nodes_ids_inner_way.addAll(getNodeIdsFromWay(innerWaysId.get(i)));
			
			// ������� (��� X) ����� ����������� ��������
			double [] lon_x_inner = new double[nodes_ids_inner_way.size()];
			lon_x_inner = Arrays.copyOf(getLongitude(nodes_ids_inner_way), nodes_ids_inner_way.size());
			
			// ������ (��� Y) ����� ����������� ��������
			double [] lat_y_inner = new double[nodes_ids_inner_way.size()];
			lat_y_inner = Arrays.copyOf(getLatitude(nodes_ids_inner_way), nodes_ids_inner_way.size());
			
			// ������� �������������� ������ �������� �� ����� ����������� ��������
			JPolygon innerPolygon = new JPolygon(lon_x_inner, lat_y_inner, nodes_ids_inner_way.size());
			
			// ��������� �������������� ����������� �������� � ��������
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
		
		// ��������� � ����� ������������ ������ ���������� ��������� ��������� �������� ��������
		if(ids_inner_ways.size() > 0) {
				Param.ids_outer_ways_with_inner_ways.put(outerWayId, ids_inner_ways);
		}
		
		// ���� ������� ������� ����� ����������(�) ��������, �� ��� ���������� ��������
		// �� ��������� ������ ��������, �� ������� ��� ���������� ��������
/*		if(ids_inner_ways.size() == 0 && innerWaysId.size() != 0 && getType(outerWayId) == Param.poligon_incomplete_outer) {
			for(int i = 0; i < innerWaysId.size(); i++) {
				deleteWayFromList(innerWaysId.get(i));
			}
		}*/
	}
	
	/**
	 * ���������� ������ �������������� ���������������, ����������� �� ��������� ������ ������� �����.
	 * ������ ������� �� ����� �����.
	 * 
	 * @param seek �������� �������� � ����� �����
	 * @return ������ �������������� ���������������
	 */
	public JRect [] getBoundsFromFile(long seek) {
		// ������ �������������� ���������������, ����������� �� ��������� ������ ������� �����
		JRect[] bounds = null; 
				
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
					
			// ��������� �������� ��� ������, �������� ��� ���������
			raf.seek(seek);
			byte type = raf.readByte();
			
			// ������ ��� �������: [0] - ������� (��� X), [1] - ������ (��� Y)
			double [][] coords_outer = getCoordsFromWayFromFile(seek, raf);			
			
			// ������� (��� X) ����� ������� �����
			double [] lon_x_outer = coords_outer[0]; // ������� (��� X) ����� �������� ��������
				
			// ������ (��� Y) ����� ������� �����
			double [] lat_y_outer = coords_outer[1]; // ������ (��� Y) ����� �������� ��������
					
			if(type == Param.point) { // �����
				// ������� �������������� ������ �������� �� ����� ������� �����
				JPolygon polygon = new JPolygon(lon_x_outer, lat_y_outer, lon_x_outer.length);
					
				bounds = new JRect[1];
				bounds[0] = polygon.getBounds(); // ��� Y �����
			} else if(type == Param.poligon_outer) { // ��������� ������
				if(lon_x_outer.length % 3 == 0) { // ���������, ��� ���-�� ������������� ������
					bounds = new JRect[lon_x_outer.length / 3];
							
					for(int i = 0, j = 0; i < lon_x_outer.length; i += 3, j++) {
						double[] tmpLonX = {lon_x_outer[i], lon_x_outer[i + 1], lon_x_outer[i + 2]};
						double[] tmpLatY = {lat_y_outer[i], lat_y_outer[i + 1], lat_y_outer[i + 2]};
								
						// ������� �������������� ������ �������� �� ����� ������� �����
						JPolygon polygon = new JPolygon(tmpLonX, tmpLatY, tmpLonX.length);
								
						// �������� �� �������� ��� �������������� ������������, �� �.�. ��� Y �������� ����������
						// ����� � ������ ���������� �����, � ��� Y � �������������� ���������� ����, �� ������ 
						// ���� � ������ ���������� top � bottom � ������������ �������������� ��������������
						bounds[j] = polygon.getBounds(); // ��� Y �����
					}
				}						
			} else { // ����� 
				if(lon_x_outer.length > 1) { // ��������, ��� ��� �� �����
					bounds = new JRect[lon_x_outer.length - 1];
							
					for(int i = 0, j = 0; i < lon_x_outer.length - 1; i++, j++) {
						double[] tmpLonX = {lon_x_outer[i], lon_x_outer[i + 1]};
						double[] tmpLatY = {lat_y_outer[i], lat_y_outer[i + 1]};
								
						// ������� �������������� ������ �������� �� ����� ������� �����
						JPolygon polygon = new JPolygon(tmpLonX, tmpLatY, tmpLonX.length);
							
						// �������� �� �������� ��� �������������� ������������, �� �.�. ��� Y �������� ����������
						// ����� � ������ ���������� �����, � ��� Y � �������������� ���������� ����, �� ������ 
						// ���� � ������ ���������� top � bottom � ������������ �������������� ��������������
						bounds[j] = polygon.getBounds(); // ��� Y �����
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
	 * ���������� ������ �������������� ���������������, ����������� �� ��������� ������ ������� �����.
	 * 
	 * @param seek �������� �������� � ����� �����
	 * @return ������ �������������� ���������������
	 */
	
	public JRect [] getBounds(long seek) {
		// ������ �������������� ���������������, ����������� �� ��������� ������ ������� �����
		JRect[] bounds = null; 
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			ArrayList<Long> nodes_ids = new ArrayList<>(); // �������������� ����� ������� �����
			
			// ��������� �������� ��� ������, �������� ��� ���������
			raf.seek(seek);
			byte type = raf.readByte();
			
			raf.seek(seek + Param.id_seek);
			nodes_ids.addAll(getNodeIdsFromWay(raf.readLong(), raf));
			
			// ������� (��� X) ����� ������� �����
			double [] lon_x_outer = new double[nodes_ids.size()]; // ������� (��� X) ����� �������� ��������
			lon_x_outer = Arrays.copyOf(getLongitude(nodes_ids), nodes_ids.size());
			
			// ������ (��� Y) ����� ������� �����
			double [] lat_y_outer = new double[nodes_ids.size()]; // ������ (��� Y) ����� �������� ��������
			lat_y_outer = Arrays.copyOf(getLatitude(nodes_ids), nodes_ids.size());
			
			if(type == Param.point) { // �����
				// ������� �������������� ������ �������� �� ����� ������� �����
				JPolygon polygon = new JPolygon(lon_x_outer, lat_y_outer, nodes_ids.size());
				
				bounds = new JRect[1];
				bounds[0] = polygon.getBounds(); // ��� Y �����
			} else if(type == Param.poligon_outer) { // ��������� ������
				if(nodes_ids.size() % 3 == 0) { // ���������, ��� ���-�� ������������� ������
					bounds = new JRect[nodes_ids.size() / 3];
					
					for(int i = 0, j = 0; i < nodes_ids.size(); i += 3, j++) {
						double[] tmpLonX = {lon_x_outer[i], lon_x_outer[i + 1], lon_x_outer[i + 2]};
						double[] tmpLatY = {lat_y_outer[i], lat_y_outer[i + 1], lat_y_outer[i + 2]};
						
						// ������� �������������� ������ �������� �� ����� ������� �����
						JPolygon polygon = new JPolygon(tmpLonX, tmpLatY, tmpLonX.length);
						
						// �������� �� �������� ��� �������������� ������������, �� �.�. ��� Y �������� ����������
						// ����� � ������ ���������� �����, � ��� Y � �������������� ���������� ����, �� ������ 
						// ���� � ������ ���������� top � bottom � ������������ �������������� ��������������
						bounds[j] = polygon.getBounds(); // ��� Y �����
					}
				}				
//			} else if(type == Param.poligon_line) { // ��������� �����
				
			} else { // ����� 
				if(nodes_ids.size() > 1) { // ��������, ��� ��� �� �����
					bounds = new JRect[nodes_ids.size() - 1];
					
					for(int i = 0, j = 0; i < nodes_ids.size() - 1; i++, j++) {
						double[] tmpLonX = {lon_x_outer[i], lon_x_outer[i + 1]};
						double[] tmpLatY = {lat_y_outer[i], lat_y_outer[i + 1]};
						
						// ������� �������������� ������ �������� �� ����� ������� �����
						JPolygon polygon = new JPolygon(tmpLonX, tmpLatY, tmpLonX.length);
						
						// �������� �� �������� ��� �������������� ������������, �� �.�. ��� Y �������� ����������
						// ����� � ������ ���������� �����, � ��� Y � �������������� ���������� ����, �� ������ 
						// ���� � ������ ���������� top � bottom � ������������ �������������� ��������������
						bounds[j] = polygon.getBounds(); // ��� Y �����
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
	 * ���������� ������ �� ���������� ������� ��� ������� �����.
	 * 
	 * @param nodes_ids �������������� ����� ������� �����
	 * @return ������ �� ���������� ������� ��� ��������� ������� �����
	 */
	
	private double [] getLongitude(ArrayList<Long> nodes_ids) {
		double [] lonArray = new double[nodes_ids.size()];
		
		for(int i = 0; i < nodes_ids.size(); i++) {
			lonArray[i] = getLongitude(nodes_ids.get(i));
		}
		
		return lonArray;
	}
	
	/**
	 * ���������� �������� ������� �������� �����.
	 * 
	 * @param nodeId ������������� �����
	 * @return �������� ������� �������� �����
	 */
	
	public double getLongitude(long nodeId) {
		double lon = Double.MIN_VALUE; // ������� �����
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
	 * ���������� ������ �� ���������� ������ ��� ������� �����.
	 * 
	 * @param nodes_ids �������������� ����� ������� �����
	 * @return ������ �� ���������� ������ ��� ��������� ������� �����
	 */
	
	private double [] getLatitude(ArrayList<Long> nodes_ids) {
		double [] latArray = new double[nodes_ids.size()];
		
		for(int i = 0; i < nodes_ids.size(); i++) {
			latArray[i] = getLatitude(nodes_ids.get(i));
		}
		
		return latArray;
	}
	
	/**
	 * ���������� �������� ������ �������� �����.
	 * 
	 * @param nodeId - ������������� �����
	 * @return �������� ������ �������� �����
	 */
	
	public double getLatitude(long nodeId) {
		double lat = Double.MIN_VALUE; // ������� �����
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
	 * ���������� � ���� ����� map.dnvg ��������� ����� ��� ��������� ���������� ������ �� xml
	 * ����� ��� ����� �����.
	 * 
	 * @param id ������������� �����
	 * @param lat ������ �����
	 * @param lon ������� �����
	 */
	
	public void setNodeParam(long id, double lat, double lon) {
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			long seek = raf.length();
			
			// ��� ��������
			raf.seek(seek);
			raf.writeByte(Param.point);
			
			// ��������	
			raf.seek(seek + Param.delete_seek);
			raf.writeByte(0x00);
			
			// �������������		
			raf.seek(seek + Param.id_seek);
			raf.writeLong(id);
			
			// ���� ��������		
			raf.seek(seek + Param.myself_seek);
			raf.writeLong(seek);
			
			// �������� ���������� ��������
			raf.seek(seek + Param.next_seek);
			raf.writeLong(0);
			
			// �������� ��������		
			raf.seek(seek + Param.attr_seek);
			raf.writeLong(0);
			
			// ������		
			raf.seek(seek + Param.lat_seek);
			raf.writeDouble(lat);
			
			// �������			
			raf.seek(seek + Param.lon_seek);
			raf.writeDouble(lon);
			
			// ������			
			raf.seek(seek + Param.alt_seek);
			raf.writeFloat(0);
			
			// ��������			
			raf.seek(seek + Param.acc_seek);
			raf.writeFloat(0);
			
			// ������� �������������� �������� � ��������������� ������� ���������� ������� �����
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
	 * ����������� ����� �������������� ������ �����.
	 */
	
	public void setNewIndexes() {
		long tmp_newIndex = Param.newIndex; // ��� ���������	
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			// ������ ���� ����� �����		
			ArrayList<Long> nodeSeeks = getAllPoints(raf);	
						
			// ������ �����
			for(int i = 0; i < nodeSeeks.size(); i++) {
				long tmp_seek = nodeSeeks.get(i);
				
				Param.newIndex++;
								
				raf.seek(tmp_seek + Param.id_seek);
				raf.writeLong(Param.newIndex);
				
				Param.new_seek_nodes.put(Param.newIndex, tmp_seek);
				Param.new_seek_only_nodes.put(Param.newIndex, tmp_seek);
			}
			
			// �������������� ���������, � ������� ���� ���������� ��������
			ArrayList<Long> outer_ways_ids_with_inners = new ArrayList<>();
//			int c = 0;
			Collection<Long> array_ways = Param.seek_ways.values();
			Iterator<Long> iterator_ways = array_ways.iterator();
			
			// ������ �����
			while(iterator_ways.hasNext()) {
				long tmp_seek = iterator_ways.next();
				
				raf.seek(tmp_seek);
				byte type = raf.readByte();
			
				// ���������� �������� ���� �� �����������
				if(type == Param.poligon_inner_composition) {
					///
/*										c++;
										raf.seek(tmp_seek + Param.id_seek);
										Param.inner.add(raf.readLong());*/
										///
					continue;
				}
				if (tmp_seek == 6111618) {
					int y = 0;
				}
				Param.newIndex++;
//				c++;
				// ��������� �������������� � ����� ������ ����� � �����
				raf.seek(tmp_seek + Param.id_seek);
				long id = raf.readLong();
								
				// ���� ������� �������, � �������� ���� ����������
				if(Param.ids_outer_ways_with_inner_ways.get(id) != null) {
					outer_ways_ids_with_inners.add(Param.newIndex);
					
					ArrayList<Long> tmp_inner_ids = Param.ids_outer_ways_with_inner_ways.get(id);
					Param.ids_outer_ways_with_inner_ways.remove(id);
					Param.ids_outer_ways_with_inner_ways.put(Param.newIndex, new ArrayList<Long>(tmp_inner_ids));
				}
					
				int size = (int) getNodeIdsFromWay(id, raf).size();
				
				Param.num_points.remove(id);
				Param.num_points.put(Param.newIndex, size);				
				
				// ������ �������������
				raf.seek(tmp_seek + Param.id_seek);
				raf.writeLong(Param.newIndex);
				
				// ��������� ����� ������
				Param.new_seek_nodes.put(Param.newIndex, tmp_seek);
				
				// ���������� �������� � ���� ������ �� ���������.
				// � ������� ������ ��� �� ��������.
				raf.seek(tmp_seek);
				
				if(raf.readByte() != Param.poligon_inner_composition)
					Param.new_seek_only_ways.put(Param.newIndex, tmp_seek);
				
				// ��������������� ��������� ����� �����
				reIndexWay(tmp_seek, tmp_seek, raf);
			}
//			int cc = 0;
			// ������ ��������������� ���������� ��������
			for(int i = 0; i < outer_ways_ids_with_inners.size(); i++) {
				ArrayList<Long> tmp_inner_ways_ids = Param.ids_outer_ways_with_inner_ways.get(outer_ways_ids_with_inners.get(i));
				ArrayList<Long> new_inner_ways_ids = new ArrayList<>(); // ����� �������������� ���������� ���������
//				cc += tmp_inner_ways_ids.size();
				for(int j = 0; j < tmp_inner_ways_ids.size(); j++) {
					long inner_way_id = tmp_inner_ways_ids.get(j);
					long tmp_seek = Param.seek_ways.get(inner_way_id);
					///
//					Param.inner_from_way.add(inner_way_id);
					///
					Param.newIndex++;
					
					// ��������� �������������� � ����� ������ ����� � �����
					int size = (int) getNodeIdsFromWay(inner_way_id, raf).size();
					Param.num_points.remove(inner_way_id);
					Param.num_points.put(Param.newIndex, size);				
					
					// ������ �������������
					raf.seek(tmp_seek + Param.id_seek);
					raf.writeLong(Param.newIndex);
					
					new_inner_ways_ids.add(Param.newIndex);
					
					// ��������� ����� ������
					Param.new_seek_nodes.put(Param.newIndex, tmp_seek);
									
					reIndexWay(tmp_seek, tmp_seek, raf);
				}
				
				// ������� ������ ������ � ��������� ������ ��� �����
				Param.ids_outer_ways_with_inner_ways.remove(outer_ways_ids_with_inners.get(i));
				Param.ids_outer_ways_with_inner_ways.put(outer_ways_ids_with_inners.get(i), new_inner_ways_ids);
			}
//			System.out.println("C: " + c + " CC: " + cc);
			// ������� ����, ��� ������ �������������� �������� ������
			Param.seekChanged = true;
			
			// �������� ���������� ����������������� �����
			Collection<Long> array = Param.seek_nodes.values();
			Iterator<Long> iterator = array.iterator();
			
			while(iterator.hasNext()) {	
				@SuppressWarnings("unused")
				long tmp_seek = iterator.next();
				tmp_newIndex++;
			}
						
			System.out.println("����� ������� �����: " + tmp_newIndex + ".\n������������� ������� �����: " + Param.newIndex + ". �������: " + (tmp_newIndex - Param.newIndex));
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
	 * ��������������� ��� ����������� ����� � �����.
	 * 
	 * @param seek - �������� ������ ����� � �����
	 * @param firstSeek - �������� ������ ����� � �����
	 * @param raf - ������ ��� ������� � �����
	 */
	
	private void reIndexWay(long seek, long firstSeek, RandomAccessFile raf) {
		try {
			long first = firstSeek; // �������� ������ ����� � �����
			
			raf.seek(seek + Param.next_seek); // �������� �������� ���������� �������� � �����
			long nextSeek = raf.readLong(); // �������� ���������� ��������	
			
			while(nextSeek != 0 && nextSeek != first) {
				Param.newIndex++;
				
				// ������ �������������
				raf.seek(nextSeek + Param.id_seek);				
				raf.writeLong(Param.newIndex);
				
				Param.new_seek_nodes.put(Param.newIndex, nextSeek);
				
				raf.seek(nextSeek + Param.next_seek); // �������� �������� ���������� �������� � �����
				nextSeek = raf.readLong();
			} 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
			
	/**
	 * ���������� � ���� ����� map.hnvg �������� �������� �����.
	 * 
	 * @param seek �������� �������� � ����� �����
	 * @param str_v ������ ��� ������ � ����
	 **/
	
	public void setName(long seek, String str_v) {
		try {
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			raf.seek(seek + Param.attr_seek);
			long attr_seek = raf.readLong();
			
			// ���� � �������� ���������� �������, �� ���������� � ���� �������� �������
			if(attr_seek > 0) {	
				long description_seek = attr_seek + Param.description_seek;
				h_raf.seek(description_seek);
							
				// ��������� �� ������ �� ������ ��������, � ���� �� ������, �������� �� 
				if(!h_raf.readUTF().isEmpty()) {
					h_raf.seek(description_seek);
					h_raf.write(setEmptyBytes(Param.description_size));
				}	
							
				h_raf.seek(description_seek);
							
				// ��������� ����� ��������, � ���� ��� ������ ����������, �� �������� ��
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
	 * ���������� � ���� ����� map.hnvg ��� ������� �� �����.
	 * 
	 * @param seek �������� �������� � ����� �����
	 * @param typeOfObject ��� ������� �� �����
	 */
	
	public void setTypeOfObject(long seek, short typeOfObject) {
		try {
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			raf.seek(seek + Param.attr_seek);
			long attr_seek = raf.readLong();
			
			// ���� � ����� ��� ���� �������, �� ���������� � ���� ��� ������� �� �����
			if(attr_seek > 0) {
				h_raf.seek(attr_seek + Param.typeOfObject_seek);
				h_raf.writeShort(typeOfObject);
				
				setAttrSeek(seek, seek, attr_seek, raf);
			} else { // ���� � ����� ��� ��������, �� ������� ��� � ���������� � ���� ��� ������� �� �����
				attr_seek = createAttr(seek, h_raf);
				
				// ��� ������� �� �����
				h_raf.seek(attr_seek + Param.typeOfObject_seek);
				h_raf.writeShort(typeOfObject);
				
				// �������� �������� �������� �����
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
	 * ������� ����� �������� �������� ����� � ����� map.hnvg � ����� ���������������.
	 * 
	 * @param attrSeek �������� �������� ������������ ����� 
	 * @param firstPointInWaySeek �������� ������ ����� � �����
	 * @return �������� ���������� ��������
	 */
	
	private long createCopyAttr(long attrSeek, long firstPointInWaySeek) {
		long newAttrSeek = -1; // ����� �������� ��������
		
		try {
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			
			newAttrSeek = h_raf.length();
						
			// �������� ������ �� ������������� ��������
			h_raf.seek(attrSeek + Param.typeOfObject_seek);
			short typeOfObject = h_raf.readShort();
			short addType = h_raf.readShort();
			byte property = h_raf.readByte();
			String description = h_raf.readUTF();
			
			// ������� ����� ������� � ��������� �������
			// ����� �������� ��������
			h_raf.seek(newAttrSeek);
			h_raf.writeLong(newAttrSeek);
			
			// �������� ������ ����� � �����
			h_raf.seek(newAttrSeek + Param.firsPointInWay_seek);
			h_raf.writeLong(firstPointInWaySeek);
			
			// ��� ������� �� �����
			h_raf.seek(newAttrSeek + Param.typeOfObject_seek);	
			h_raf.writeShort(typeOfObject);
				
			// �������������� ��� ������� �� �����
			h_raf.seek(newAttrSeek + Param.additionalTypeOfObject_seek);	
			h_raf.writeShort(addType);
				
			// ��-�� ������� �� �����
			h_raf.seek(newAttrSeek + Param.properyType_seek);			
			h_raf.writeByte(property);
				
			// ��������
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
	 * ������� ������ ������� �������� ����� � ����� map.hnvg � ���������� � ����
	 * �������� �������� �������� �����, �� ������� �� ���������.
	 * 
	 * @param seek �������� �������� �����
	 * @param h_raf ������ ��� ������� � ����� map.hnvg
	 * @return �������� ���������� ��������
	 */
	
	private long createAttr(long seek, RandomAccessFile h_raf) {
		long attr_seek = 0;
		
		try {
			attr_seek = h_raf.length();
						
			// �������� ��������
			h_raf.seek(attr_seek);
			h_raf.writeLong(attr_seek);
			
			// �������� ������� �������� ������ ����� � ����� map.dnvg
			h_raf.seek(attr_seek + Param.firsPointInWay_seek);
			h_raf.writeLong(seek);
			
			// ��� ������� �� �����
			h_raf.seek(attr_seek + Param.typeOfObject_seek);
			h_raf.writeShort(Param.noType);
			
			// �������������� ��� ������� �� �����
			h_raf.seek(attr_seek + Param.additionalTypeOfObject_seek);
			h_raf.writeShort(Param.noType);
			
			// �������� ������� �� �����
			h_raf.seek(attr_seek + Param.properyType_seek);
			h_raf.writeByte(0x00);
			
			// �������� ������� �����	
			h_raf.seek(attr_seek + Param.description_seek);
			h_raf.write(setEmptyBytes(Param.description_size));
			
			Param.attrs.add(attr_seek);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return attr_seek;
	}
	
	/**
	 * ������� ��� ������ (����� ������ ��������) � �������� �������� �����, � 
	 * �� ����� �������� ������ ����� � ����� ���������� �������� ��������.
	 * 
	 * @param firstPointInWay �������� �������� �����
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
	 * ���������� �������� �������� �������� � ������� �����.
	 * 
	 * @param seek �������� �������� � ����� �����
	 * @param attrSeek �������� �������� ��������
	 */
	
	public void setAttrSeek(long seek, long attrSeek) {
		try {	
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			raf.seek(seek + Param.attr_seek); // �������� �������� ���������� �������� � �����
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
	 * ���������� �� ��� ����� �������� ����� �������� �������� ��������.
	 * 
	 * @param seek �������� �������� � ����� �����
	 * @param firstSeek �������� ������ ����� � �����
	 * @param attrSeek �������� �������� ��������
	 * @param raf ������ ��� ������� � �����
	 */
	
	private void setAttrSeek(long seek, long firstSeek, long attrSeek, RandomAccessFile raf) {
		try {
			long first = firstSeek; // �������� ������ ����� � �����
			
			// ���������� �������� �������� �������� � ������ ����� �����
			if(seek == firstSeek) {
				raf.seek(firstSeek + Param.attr_seek);
				raf.writeLong(attrSeek);
			}
			
			raf.seek(seek + Param.next_seek); // �������� �������� ���������� �������� � �����
			long nextSeek = raf.readLong(); // �������� ���������� ��������
			
			while(nextSeek != 0 && nextSeek != first) {
				raf.seek(nextSeek + Param.attr_seek);					
				raf.writeLong(attrSeek);
				
				raf.seek(nextSeek + Param.next_seek); // �������� �������� ���������� �������� � �����
				nextSeek = raf.readLong();
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ���������� �� ��� ����� �������� ����� �������� ������ �����.
	 * 
	 * @param seek �������� �������� � ����� �����
	 * @param firstSeek �������� ������ ����� � �����
	 * @param alt �������� ������ �����
	 * @param raf ������ ��� ������� � �����
	 */
	
	private void setAltitude(long seek, long firstSeek, float alt, RandomAccessFile raf) {
		try {
			long first = firstSeek; // �������� ������ ����� � �����
			
			// ���������� �������� �������� ������ ����� � ������ ����� �����
			if(seek == firstSeek) {
				raf.seek(firstSeek + Param.alt_seek);
				raf.writeFloat(alt);
			}
			
			raf.seek(seek + Param.next_seek); // �������� �������� ���������� �������� � �����
			long nextSeek = raf.readLong(); // �������� ���������� ��������
			
			while(nextSeek != 0 && nextSeek != first) {
				raf.seek(nextSeek + Param.alt_seek);					
				raf.writeFloat(alt);
				
				raf.seek(nextSeek + Param.next_seek); // �������� �������� ���������� �������� � �����
				nextSeek = raf.readLong();
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ���������� �� ��� ����� �������� ����� �������� �������� ��������� �����.
	 * 
	 * @param seek �������� �������� � ����� �����
	 * @param firstSeek �������� ������ ����� � �����
	 * @param acc �������� �������� ��������� �����
	 * @param raf ������ ��� ������� � �����
	 */
	
	private void setAccuracy(long seek, long firstSeek, float acc, RandomAccessFile raf) {
		try {
			long first = firstSeek; // �������� ������ ����� � �����
			
			// ���������� �������� �������� ��������� ����� � ������ ����� �����
			if(seek == firstSeek) {
				raf.seek(firstSeek + Param.acc_seek);
				raf.writeFloat(acc);
			}
			
			raf.seek(seek + Param.next_seek); // �������� �������� ���������� �������� � �����
			long nextSeek = raf.readLong(); // �������� ���������� ��������
			
			while(nextSeek != 0 && nextSeek != first) {
				raf.seek(nextSeek + Param.acc_seek);					
				raf.writeFloat(acc);
				
				raf.seek(nextSeek + Param.next_seek); // �������� �������� ���������� �������� � �����
				nextSeek = raf.readLong(); // �������� ���������� ��������
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ���������� �� ��� ����� �������� ����� �������� �������� ��������.
	 * 
	 * @param seek �������� �������� � ����� �����
	 * @param firstSeek �������� ������ ����� � �����
	 * @param attrSeek �������� �������� ��������
	 */
	
	public void setAttrSeek(long seek, long firstSeek, long attrSeek) {
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			long first = firstSeek; // �������� ������ ����� � �����
			
			// ���������� �������� �������� �������� � ������ ����� �����
			if(seek == firstSeek) {
				raf.seek(firstSeek + Param.attr_seek);
				raf.writeLong(attrSeek);
			}
			
			raf.seek(seek + Param.next_seek); // �������� �������� ���������� �������� � �����
			long nextSeek = raf.readLong(); // �������� ���������� ��������
			
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
	 * ���������� � ���� ����� map.hnvg �������������� ��� ������� �� �����.
	 * 
	 * @param seek �������� �������� � ����� �����
	 * @param typeOfObject ��� ������� �� �����
	 */
	
	public void setAdditionalTypeOfObject(long seek, short typeOfObject) {
		try {
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			// �������� �������� �������� � map.hnvg
			raf.seek(seek + Param.attr_seek);
			long attr_seek = raf.readLong();
						
			if(attr_seek > 0) {
				// ���������� �������� ���. ���� ������� �� �����
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
	 * ���������� � ���� ����� �������������� ��� ������� �� �����.
	 * 
	 * @param seek - �������� �������� � ����� �����
	 * @param typeOfObject - ��� ������� �� �����
	 * @param raf - ������ ��� ������� � �����
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
	 * ���������� � ���� ����� �������������� ��� ������� �� �����.
	 * 
	 * @param seek - �������� �������� � ����� �����
	 * @param firstSeek - �������� ������ ����� � �����
	 * @param typeOfObject - ��� ������� �� �����
	 * @param raf - ������ ��� ������� � �����
	 */
	
	private void setAdditionalTypeOfObject(long seek, long firstSeek, short typeOfObject, 
			                               RandomAccessFile raf) {
		try {	
			long first = firstSeek; // �������� ������ ����� � �����
			
			raf.seek(seek + Param.next_seek); // �������� �������� ���������� �������� � �����
			long nextSeek = raf.readLong(); // �������� ���������� ��������
			
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
	 * ������������� � ��������� ����� ������� �������������� �������� � ��������������� ������� 
	 * ���������� ������� �����.
	 * 
	 * @param seek �������� �����
	 * @param raf ������ ��� ������� � �����
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
	 * ��������� �������������� ����� � ����� �������� �����
	 * polygon_line � ������ ��� �������� �� line. 
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
					Param.maxNodeId++; // ����� ������������� �����
					
					long newSeek = createNewNodeForWay(tmp_seek, raf);
					
					Param.seek_nodes.put(Param.maxNodeId, newSeek);
					Param.seek_nodes_used.put(Param.maxNodeId, newSeek);
					
					// ������ ����� ��������� � �����
					raf.seek(newSeek + Param.next_seek);
					raf.writeLong(0);
						
					long lastSeek = getSeekLastPointWay(tmp_seek, raf);
						
					// ������ ����� ������������� � �����
					raf.seek(lastSeek + Param.next_seek);
					raf.writeLong(newSeek);	
					
					int size = Param.num_points.get(way_id);
					
					// �������� ���������� ����� � ����� � ������
					Param.num_points.remove(way_id);
					Param.num_points.put(way_id, size + 1);
					
					// ������ ��� �������� �����
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
	 * ���������� � ���� ����� map.hnvg �������� ������� �����.
	 * 
	 * @param seek �������� �������� � ����� �����
	 * @param properyType �������� ���� �������� �����
	 */
	
	public void setPropertyType(long seek, byte properyType) {
		try {
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			// �������� �������� �������� � map.hnvg
			raf.seek(seek + Param.attr_seek);
			long attr_seek = raf.readLong();
			
			if(attr_seek > 0) {
				// ���������� �������� �������� ������� �� �����
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
	 * ���������� � ���� ����� �������� ������� �����.
	 * 
	 * @param seek - �������� �������� � ����� �����
	 * @param properyType - �������� ���� �������� �����
	 * @param raf - ������ ��� ������� � �����
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
	 * ���������� � ���� ����� �������� ������� �����.
	 * 
	 * @param seek - �������� �������� � ����� �����
	 * @param firstSeek - �������� ������ ����� � �����
	 * @param properyType - �������� ���� �������� �����
	 * @param raf - ������ ��� ������� � �����
	 */
	
	private void setPropertyType(long seek, long firstSeek, byte properyType, RandomAccessFile raf) {
		try {
			long first = firstSeek; // �������� ������ ����� � �����
			
			raf.seek(seek + Param.next_seek); // �������� �������� ���������� �������� � �����
			long nextSeek = raf.readLong(); // �������� ���������� ��������
			
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
	 * �������� ��� �������� ����� �� ��������� �������� � ���� �����������
	 * ��������� �����.
	 * 
	 * @param type ��� ��������
	 * @param seek �������� �������� � ����� �����
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
	 * �������� ��� �������� ����� �� ��������� �������� � ���� ���������
	 * ���������, �� ������� ��������� ������ �������.
	 * 
	 * @param type - ��� ��������
	 * @param seek - �������� �������� � ����� �����
	 * @param raf - ������ ��� ������� � �����
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
	 * �������� ��� �������� ����� �� ��������� �������� � ���� ���������
	 * ���������, �� ������� ��������� ������ ������� � ����� map.dnvg.
	 * 
	 * @param type ��� ��������
	 * @param seek �������� �������� � ����� �����
	 * @param firstSeek �������� ������ ����� � �����
	 * @param raf ������ ��� ������� � �����
	 */
	
	private void setType(byte type, long seek, long firstSeek, RandomAccessFile raf) {
		try {
			long first = firstSeek; // �������� ������ ����� � �����
			
			// ���������� �������� ���� �������� � ������ ����� �����
			if(seek == firstSeek) {
				raf.seek(firstSeek);
				raf.writeByte(type);
			}
			
			raf.seek(seek + Param.next_seek); // �������� �������� ���������� �������� � �����
			long nextSeek = raf.readLong(); // �������� ���������� ��������
			
			while(nextSeek != 0 && nextSeek != first) {
				raf.seek(nextSeek);					
				raf.writeByte(type);
				
				raf.seek(nextSeek + Param.next_seek); // �������� �������� ���������� �������� � �����
				nextSeek = raf.readLong();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * ������������� �������� �������� ���������� �������� � ��������� ����� ����������� �������� ������
	 * �������� ��������� ��������, ��� ����� ���������� ��������� ����� � ���� �����. 
	 * 
	 * @param seek �������� �������� ��������
	 * @param prevSeek �������� ����������� ��������
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
	 * ������� ��������� ����� � �����, ���� ��� ��������� � �� ������ ������.
	 * 
	 * @param way_id ������������� �����
	 */
	
	public void deleteDublicatNodes(Long way_id) {
		// ������ ����� ����� ����� ��������� ����� �����
		boolean twins = false; 
				
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			ArrayList<Long> nodes_ids_way = new ArrayList<>(); // �������������� ����� �����
			nodes_ids_way.addAll(getNodeIdsFromWay(way_id, raf));
			
			twins = compareCoordsNodes(nodes_ids_way.get(0), 
					                   nodes_ids_way.get(nodes_ids_way.size() - 1), raf);
			if(twins) {
				// �������� ������ �� ��������� ����� ����� � ���������� �� �����
				raf.seek(Param.seek_nodes.get(nodes_ids_way.get(nodes_ids_way.size() - 2)) + Param.next_seek);
				raf.writeLong(0);
				
				// �������������� � �������� ��������� �����
				deleteNodeFromList(nodes_ids_way.get(nodes_ids_way.size() - 1), raf);
				
				// ������� ��������� ����� ����� �� ������
				nodes_ids_way.remove(nodes_ids_way.size() - 1);
				// �������� ���������� ����� � ����� � ������
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
	 * ������� ��������� ����� � �����, ���� ��� ��������� � �� ������ ������.
	 * 
	 * @param way_id ������������� �����
	 * @param raf ������ ��� ������� � ����� map.dnvg
	 */
	
	public void deleteDublicatNodes(Long way_id, RandomAccessFile raf) {
		// ������ ����� ����� ����� ��������� ����� �����
		boolean twins = false;
		
		try {			
			ArrayList<Long> nodes_ids_way = new ArrayList<>(); // �������������� ����� �����
			nodes_ids_way.addAll(getNodeIdsFromWay(way_id, raf));
			
			twins = compareCoordsNodes(nodes_ids_way.get(0), 
					                   nodes_ids_way.get(nodes_ids_way.size() - 1), raf);
			if(twins) {
				// �������� ������ �� ��������� ����� ����� � ���������� �� �����
				raf.seek(Param.seek_nodes.get(nodes_ids_way.get(nodes_ids_way.size() - 2)) + Param.next_seek);
				raf.writeLong(0);
				
				// �������������� � �������� ��������� �����
				deleteNodeFromList(nodes_ids_way.get(nodes_ids_way.size() - 1), raf);
				
				// ������� ��������� ����� ����� �� ������
				nodes_ids_way.remove(nodes_ids_way.size() - 1);
				// �������� ���������� ����� � ����� � ������
				Param.num_points.remove(way_id);
				Param.num_points.put(way_id, nodes_ids_way.size());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * ������� ����� � ����������� ������������ �� �����, ����������� � ������� �����.
	 * 
	 * @param ways_ids �������������� ����� � ������� �����
	 * @return ����������� �������������� ����� � ������� �����
	 */
	
	public void deleteDublicatNodes(ArrayList<Long> ways_ids) {
		// ��������� ����� ������ ����� ����� ������ ����� ������ �����
		boolean twins = false; 
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");	
			
			ArrayList<Long> nodes_ids_way_1 = new ArrayList<>(); // �������������� ����� ������ ����� � ���� ��� �����������
			ArrayList<Long> nodes_ids_way_2 = new ArrayList<>(); // �������������� ����� ������ ����� � ���� ��� �����������
			
			int size = ways_ids.size();
			
			if(size > 1) { // � ��������� ������ ����� �����
				// ����� ������ � ��������� �����
				for(int i = 0; i < size - 1; i++) {
					nodes_ids_way_1.addAll(getNodeIdsFromWay(ways_ids.get(i), raf));
					nodes_ids_way_2.addAll(getNodeIdsFromWay(ways_ids.get(i + 1), raf));
					
					twins = compareCoordsNodes(nodes_ids_way_1.get(nodes_ids_way_1.size() - 1),
							                   nodes_ids_way_2.get(0), raf);					
					if(twins) {
						if(nodes_ids_way_2.size() == 1) {							
							// ��������� �� ������ ����� � ��������� ����� ������ ����� (���� ����)
							raf.seek(Param.seek_nodes.get(nodes_ids_way_2.get(0)) + Param.next_seek);
							long nextSeek = raf.readLong();
							
							// � ��������� ����� ������ ����� ���������� �������� �� ����� ������ ����� 
							// � ��������� ����� ������ ����� (���� ����)
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
							// ��������� �� ������ ����� �� ������ �����
							raf.seek(Param.seek_nodes.get(nodes_ids_way_2.get(0)) + Param.next_seek);
							long nextSeek = raf.readLong();
							
							// � ��������� ����� ������ ����� ���������� �������� �� ����� ������ ����� ������ �����
							raf.seek(Param.seek_nodes.get(nodes_ids_way_1.get(nodes_ids_way_1.size() - 1)) + Param.next_seek);
							raf.writeLong(nextSeek);							
							
							// �.�. ������ ����� ����� ��������, �� ������� ��� ����� �� ������ ���� �����,
							// ������ ������������ �����, ������ ������������ ����� � ��������� � �� 
							// ������ ���������� ����� � �����
						    Param.seek_ways.remove(nodes_ids_way_2.get(0));
						    Param.seek_ways_used.remove(nodes_ids_way_2.get(0));
//						    Param.seek_ways_used_in_relations.remove(nodes_ids_way_2.get(0));
						    Param.num_points.remove(nodes_ids_way_2.get(0));
						    
						    // �������������� ��������
						    setFirstPointInAttr(nextSeek, raf);
						    
						    deleteNodeFromList(nodes_ids_way_2.get(0), raf);	
						    
						    // ������� ������ ����� �� ������ �����
							nodes_ids_way_2.remove(0);
							
							// ��������� � ������ ���� �����, ������ ������������ �����, ������ ������������ 
							// ����� � ��������� � ������ ���������� ����� � ����� ����� �����
						    Param.seek_ways.put(nodes_ids_way_2.get(0), nextSeek);
						    Param.seek_ways_used.put(nodes_ids_way_2.get(0), nextSeek);
//						    Param.seek_ways_used_in_relations.put(nodes_ids_way_2.get(0), nextSeek);
						    Param.num_points.put(nodes_ids_way_2.get(0), nodes_ids_way_2.size());
						
						    // �������� ������������� ������ ����� � ������ �����
						    ways_ids.set(i + 1, nodes_ids_way_2.get(0));
							
							--i;
						}
					}
					
					nodes_ids_way_1.clear();
					nodes_ids_way_2.clear();
					
					size = ways_ids.size();
				}
			}
			
			// ��� ����� �����
			if(size == 1) {
				nodes_ids_way_1.addAll(getNodeIdsFromWay(ways_ids.get(0), raf));
				
				if(nodes_ids_way_1.size() > 1) {
					twins = compareCoordsNodes(nodes_ids_way_1.get(0),
							                   nodes_ids_way_1.get(nodes_ids_way_1.size() - 1), raf);
				
					if(twins) {
						// �������� ������ � ���������� �����
						raf.seek(Param.seek_nodes.get(nodes_ids_way_1.get(nodes_ids_way_1.size() - 2)) + Param.next_seek);
						raf.writeLong(0);
						
						// �������������� � �������� ��������� �����
						deleteNodeFromList(nodes_ids_way_1.get(nodes_ids_way_1.size() - 1), raf);
						
						// ������� ��������� ����� � �����
						nodes_ids_way_1.remove(nodes_ids_way_1.get(nodes_ids_way_1.size() - 1));
						
						// ��������� ������ � ������ ���������� ����� � �����
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
	 * ���������� �������� ����� �� ����� �����. 
	 */
	
	private void deleteNodes() {
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");			
						
			Collection<Long> nodes_ids = Param.delete_nodes.values();
			Iterator<Long> iterator_nodes = nodes_ids.iterator();
						
			while(iterator_nodes.hasNext()) {
				// ����� ��� ������� ����� �� ����� �����
				long tmp_seek = iterator_nodes.next();
								
				// ������������� ����� � ������ �������� �����
				raf.seek(tmp_seek + Param.id_seek);
				long tmp_id = raf.readLong();
				
				long node_seek = raf.length() - Param.elementSize; // �������� ��������� ����� � ����� 
					
				raf.seek(node_seek + Param.delete_seek);
				byte del = raf.readByte(); // ������� ����, ��� ����� �������
				
				// ���� ��������� ����� ��� ������� ����� ��������� � ���� �� ��������� ������, �����
				// �������� ���� � �����������
				if((tmp_seek == node_seek) && nodes_ids.size() == 1) {
					raf.seek(node_seek + Param.id_seek);
					Param.delete_nodes.remove(raf.readLong());
					
					raf.setLength(node_seek);
					continue;
				}
				
				// ������ �� ����� � ��������� ��������
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
					
					// ������������� ����� � ������ �������� �����
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
	
	/**
	 * ������� ��������� �������� ����� � �����, ���������� �� ��������������� �����
	 * ������������ ���������.
	 * 
	 * @return TRUE, ���� ���� ������� �����, ����� - FALSE
	 */
	public void deleteAttrsAndNodesAfterTriangulation() {		
		OsmConverter.printLog("   ���������� ����� �� ��������: " + 
	                           (Param.new_seek_nodes.size() + Param.delete_nodes.size()));
		OsmConverter.printLog("   ���������� ����� ��� ��������: " + Param.delete_nodes.size());
				
		// ���������� �������� ����� �����
		deleteNodes();
		
		OsmConverter.printLog("   ����� �������: " + Param.delete_nodes.size());
		OsmConverter.printLog("   ���������� ����� ����� ��������: " + Param.new_seek_nodes.size());
		
		OsmConverter.printLog("   ���������� ��������� �� ��������: " + Param.attrs.size());
		OsmConverter.printLog("   ���������� ��������� ��� ��������: " + Param.delete_attrs.size());
		
		// ���������� �������� ��������� ����� �����
		deleteAttrs();
		
		OsmConverter.printLog("   �������� �������: " + Param.delete_attrs.size());
		OsmConverter.printLog("   ���������� ��������� ����� ��������: " + Param.attrs.size());
	}
	
	/**
	 * ���������� �������� ��������� ����� �� ����� �����. 
	 */
	
	private void deleteAttrs() {
		try {
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			
			int size = Param.delete_attrs.size();
			
			for(int i = 0; i < size; i++) {
				// ����� ��� ������� �������� �� ����� �����
				long tmp_seek = Param.delete_attrs.get(i);
				// �������� ���������� �������� � �����
				long attr_seek = h_raf.length() - Param.attrBlockSize; 
							
				// ���� ��������� ����� ��� ������� �������� ��������� � ���� �� ��������� ���������, �����
				// �������� ���� � �����������
				if(tmp_seek == attr_seek) {
					Param.delete_attrs.remove(tmp_seek);
					Param.attrs.remove(attr_seek);
					size = Param.delete_attrs.size();
					
					--i;
					
					h_raf.setLength(attr_seek);
					continue;
				}
				
				// ��������� �� �������� �� ������������ ������� ���������
				// �� ������ ��������
				int index = Param.delete_attrs.indexOf(attr_seek);
				
				// ���� � ������ ��� ����� ��������
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
	 * ��������� ������� (������ ��� ����� � ���������) � ������ ����� ����� �����.
	 * 
	 * @param src_seek �������� �� ����� ������� ��������
	 * @param attr_seek �������� ��������
	 * @param h_raf ������ ��� ������� � �����
	 */
	
	public void insertAttr(long src_seek, long attr_seek, RandomAccessFile h_raf) {
		try {			
			h_raf.seek(attr_seek + Param.firsPointInWay_seek);
			
			// ��������� ������ �� ����������� ��������
			long firstInLine = h_raf.readLong();
			short typeOfObject = h_raf.readShort();
			short additionalTypeOfObject = h_raf.readShort();
			byte properyType = h_raf.readByte();
			String description = h_raf.readUTF();
			
			// ���������� ������ �������� � ������ �����
			// �������� ��������
			h_raf.seek(src_seek);
			h_raf.writeLong(src_seek);
						
			// �������� ������� �������� ������ ����� � ����� map.dnvg
			h_raf.seek(src_seek + Param.firsPointInWay_seek);
			h_raf.writeLong(firstInLine);
						
			// ��� ������� �� �����
			h_raf.seek(src_seek + Param.typeOfObject_seek);
			h_raf.writeShort(typeOfObject);
						
			// �������������� ��� ������� �� �����
			h_raf.seek(src_seek + Param.additionalTypeOfObject_seek);
			h_raf.writeShort(additionalTypeOfObject);
						
			// �������� ������� �� �����
			h_raf.seek(src_seek + Param.properyType_seek);
			h_raf.writeByte(properyType);
						
			// �������� ������� �����	
			h_raf.seek(src_seek + Param.description_seek);
			h_raf.write(setEmptyBytes(Param.description_size));
			
			h_raf.seek(src_seek + Param.description_seek);
			h_raf.writeUTF(description);
			
			// ��������� ������ �� ����������� � ����� ����� �����	
			h_raf.seek(src_seek + Param.firsPointInWay_seek);
			long seek = h_raf.readLong();
			
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			raf.seek(seek + Param.attr_seek);
			
			// ��������� ������ ��������� � ������
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
	 * ��������� ����� (������ �� ����� � ���������) � ������ ����� ����� ����� ������������� ��� ������ 
	 * �� ��� �� ������ ����� (���� ������ ����).
	 * 
	 * @param src_seek �������� �� ����� ������� �����
	 * @param node_seek �������� �����
	 * @param raf ������ ��� ������� � �����
	 */
	
	public void insertNode(long src_seek, long node_seek, RandomAccessFile raf) {
		try {
			boolean isFirstInLine = false; // ���� �������� ����� � ������ ����� � ����� ���������
			
			raf.seek(node_seek);
			
			// ��������� ������ �� ���������� �����
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
			
			//���� ��� ������ ����� � �����
			long firstInLine = 0;
			
			// ���� �������� ������ ����� � �����
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
			
			// ���������� ������ ����� � ������ �����
			// ��� ��������
			raf.seek(src_seek);			
			raf.writeByte(type);
			
			// ��������
			raf.seek(src_seek + Param.delete_seek);			
			raf.writeByte(delete);
			
			// �������������
			raf.seek(src_seek + Param.id_seek);	
			raf.writeLong(id);
			
			// ���� ��������
			raf.seek(src_seek + Param.myself_seek);	
			raf.writeLong(myselfSeek);
			
			// �������� ���� ��������
			raf.seek(src_seek + Param.next_seek);	
			raf.writeLong(next);
			
			// �������� �������� �����
			raf.seek(src_seek + Param.attr_seek);	
			raf.writeLong(attrSeek);
			
			// ������
			raf.seek(src_seek + Param.lat_seek);	
			raf.writeDouble(lat);
			
			// �������
			raf.seek(src_seek + Param.lon_seek);	
			raf.writeDouble(lon);
			
			// ������ ��� ������� ����
			raf.seek(src_seek + Param.alt_seek);	
			raf.writeFloat(alt);
			
			// ��������
			raf.seek(src_seek + Param.acc_seek);	
			raf.writeFloat(acc);
			
			// ������� �������������� �������� � ��������������� ������� ���������� ������� �����
			raf.seek(src_seek + Param.boundary_seek);		
			raf.writeByte(boundary);
			
			// ��������� ������ �� ����� � ������
			if(Param.seekChanged) {
				Param.new_seek_nodes.remove(id);
				Param.new_seek_nodes.put(id, myselfSeek);
			} else {
				Param.seek_nodes.remove(id);
				Param.seek_nodes.put(id, myselfSeek);
			}
						
			// �������������� ����
			if(raf != null)
				raf.close();
			
			// ������ ��������� ���� ��� ������
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			// ��������� ������ �� ����������� � ����� ����� �����	
			
			if(type != Param.point) { // ���� ��� �� ��������� �����
				// ��������� ������ ����� � ������
				if(isFirstInLine) {
					// ��������� ������ �� ����� � ������
					if(Param.seekChanged) {
						Param.new_seek_only_ways.remove(id);
						Param.new_seek_only_ways.put(id, myselfSeek);	
					} else {
						Param.seek_ways.remove(id);
						Param.seek_ways.put(id, myselfSeek);	
					}								
				} else {
					if(firstInLine > 0) {
						raf.seek(firstInLine + Param.id_seek); // ������������� ������ ����� � �����
				
						ArrayList<Long> nodes_ids = new ArrayList<>(); // �������������� ���� ����� �����
					
						nodes_ids.addAll(getNodeIdsFromWay(raf.readLong(), raf));
				
						// ���������� � ���������� � ����� ����� ������ �� ���� �����
						for(int i = 0; i < nodes_ids.size(); i++) {
							if(nodes_ids.size() > 1) {
								if(nodes_ids.get(i) == id) {
									if(i > 0) {
										if(Param.seekChanged) {
											raf.seek(Param.new_seek_nodes.get(nodes_ids.get(i - 1)) + Param.next_seek);
										} else {
											raf.seek(Param.seek_nodes.get(nodes_ids.get(i - 1)) + Param.next_seek);
										}	
										
										raf.writeLong(src_seek);
									}
								}
							}
						}
					}
				}
			} else { // ���� ��� ��������� �����
				// ��������� ������ �� ����� � ������
				if(Param.seekChanged) {
					Param.new_seek_nodes.remove(id);
					Param.new_seek_nodes.put(id, myselfSeek);
				} else {
					Param.seek_nodes.remove(id);
					Param.seek_nodes.put(id, myselfSeek);
				}		
				
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
	 * ������� ����� �� ������ ���� ����� �����.
	 * 
	 * @param node_id ������������� ��������� �����
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
	 * ������� ����� �� ������ ���� ����� �����.
	 * 
	 * @param node_id ������������� ��������� �����
	 * @param raf ������ ��� ������� � �����
	 */
	
	private void deleteNodeFromList(long node_id, RandomAccessFile raf) {
		long seek = 0;
		
		// ���������� ������� �������� � �������� �����
		try {
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			
			if(Param.seekChanged) {
				seek = Param.new_seek_nodes.get(node_id);
			} else {
				seek = Param.seek_nodes.get(node_id);
			}
			
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
	 * ������� ����� �� ������ ���� ����� �����.
	 * 
	 * @param node_id ������������� ��������� �����
	 * @param raf ������ ��� ������� � �����
	 * @param h_raf ������ ��� ������� � �����
	 */
	
	private void deleteNodeFromList(long node_id, RandomAccessFile raf, RandomAccessFile h_raf) {
		long seek = 0;
		
		// ���������� ������� �������� � �������� �����
		try {			
			if(Param.seekChanged) {
				seek = Param.new_seek_nodes.get(node_id);
			} else {
				seek = Param.seek_nodes.get(node_id);
			}
			
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
		} 
		
		if(Param.seekChanged) {
			Param.delete_nodes.put(node_id, Param.new_seek_nodes.get(node_id));
			
			Param.new_seek_nodes.remove(node_id);
		} else {
			Param.delete_nodes.put(node_id, Param.seek_nodes.get(node_id));
			
			Param.seek_nodes.remove(node_id);
			Param.seek_nodes_used.remove(node_id);
		}		
	}
	
	/**
	 * ������� ����� �� ������ ���� ����� �����.
	 * 
	 * @param way_id ������������� ��������� �����
	 */
	
	public void deleteWayFromList(long way_id) {
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
		
			// �����, �� ������� ������� �����
			ArrayList<Long> nodes_ids = getNodeIdsFromWay(way_id, raf);
		
			// ���� ������ � ��������� ����� �����
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
	 * ������� ����� �� ������ ���� ����� �����.
	 * 
	 * @param way_id ������������� ��������� �����
	 * @param raf ������ ��� ������� � �����
	 */
	
	private void deleteWayFromList(long way_id, RandomAccessFile raf) {
		
		// �����, �� ������� ������� �����
		ArrayList<Long> nodes_ids = getNodeIdsFromWay(way_id, raf);
		
		// ���� ������ � ��������� ����� �����
		if(nodes_ids.get(0) == nodes_ids.get(nodes_ids.size() - 1))
			nodes_ids.remove(nodes_ids.size() - 1);
		
		for(int i = 0; i < nodes_ids.size(); i++) {			
			deleteNodeFromList(nodes_ids.get(i), raf);
		}
		
		Param.seek_ways.remove(way_id);
		Param.seek_ways_used.remove(way_id);
	}
	
	/**
	 * ������� ���������� � ���������������� � ��������� �����.
	 */
	
	public void deleteNodesAndWays() {
		try {
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			long waysCount = 0; // ������� ����� ��� ��������
			long nodesCount = 0; // ������� ����� ��� ��������
			
			// ������ ���� ����� �����		
			ArrayList<Long> nodeSeeks = getAllPoints(raf);			
			// ������ ����� ��� ��������
			ArrayList<Long> nodesForDelete = new ArrayList<>();
			
			short typeOfObject = Param.noType;
			long attrSeek = 0;
			
			// ���� ����� ��� ���� � � ����������� ����� ������� �� �����
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
						
					if(typeOfObject == Param.noType || typeOfObject == Param.unknownType) { // ������� �����
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
			
			// ������� �� ���� ������� ����� ��� ��������
			for(int i = 0; i < nodesForDelete.size(); i++)
				deleteNodeFromList(nodesForDelete.get(i), raf);
			
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			
			// ���� ����� ��� ���� � � ����������� ����� ������� �� �����
			if(Param.seek_ways != null && Param.seek_ways.size() != 0) {
				Collection<Long> ways_ids = Param.seek_ways.values();
				Iterator<Long> iterator_ways = ways_ids.iterator();
				
				// ������ ����� ��� ��������
				ArrayList<Long> waysForDelete = new ArrayList<>();
			
				typeOfObject = Param.noType;
				attrSeek = 0;
			
				// ���������� �����
				while(iterator_ways.hasNext()) {
					long tmp_seek = iterator_ways.next();
				
					raf.seek(tmp_seek + Param.attr_seek);
					attrSeek = raf.readLong();
					
					if(attrSeek > 0) {
						h_raf.seek(attrSeek + Param.typeOfObject_seek);
						typeOfObject = h_raf.readShort();
					} else
						typeOfObject = Param.noType;
				
					raf.seek(tmp_seek);
					byte inner_polygon = raf.readByte();
					
					// ������� �����
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
						
						// ��������� � ���������� �������� �������� ������� ���������� ���������, 
						// ���� ��� ���� - ������� ��
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
				
				// ������� �� ���� ������� ����� ��� ��������
				for(int i = 0; i < waysForDelete.size(); i++) {
					deleteWayFromList(waysForDelete.get(i), raf);
				}
			}
			
			OsmConverter.printLog("���������� ��������� ����� ��� ��������: " + nodesCount);
			OsmConverter.printLog("���������� ����� ��� ��������: " + waysCount);
			OsmConverter.printLog("����� ���������� ����� ��� ��������: " + Param.delete_nodes.size());
			
			// ���������� �������� ����� �� ����� �����
			deleteNodes();
			
			OsmConverter.printLog("����� �������: " + Param.delete_nodes.size());
			OsmConverter.printLog("���������� ��������� �� ��������: " + Param.attrs.size());
			OsmConverter.printLog("���������� ��������� ��� ��������: " + Param.delete_attrs.size());
			
			// ���������� �������� ��������� ����� �����
			deleteAttrs();
			
			OsmConverter.printLog("�������� �������: " + Param.delete_attrs.size());
			OsmConverter.printLog("���������� ��������� ����� ��������: " + Param.attrs.size());
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
	 * ���������� ��� ����� � ����� � ���������� �������� ���������.
	 * 
	 * @param seek �������� ����� � �����
	 * @param raf ������ ��� ������� � �����
	 * @return ���������� �������� ��������� �����
	 */
	
	private long getSeekLastPointWay(long seek, RandomAccessFile raf) {
		long lastSeek = seek;
		
		try {
			raf.seek(seek + Param.next_seek);  // �������� �������� ���������� �������� � �����
			long nextSeek = raf.readLong(); // �������� ���������� ��������
			
			if(nextSeek != 0)  {
				lastSeek = getSeekLastPointWay(nextSeek, raf);
			} 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return lastSeek;
	}
	
	/**
	 * ���������� ������ �� ���������� ��� ������� �������� �����, �������
	 * ����� ������� � ��������� ������.
	 * 
	 * @param seek �������� ������� �������� �����
	 * @return ������ �� ���������� ��� ������� �������� �����, �������
	 * ����� ������� � ��������� ������
	 */
	public long [] getSeeksForTree(long seek) {
		long [] seeks = null;
		
		try {	
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			raf.seek(seek + Param.id_seek);
			long id = raf.readLong();
			
			ArrayList<Long> allSeeks = getSeekAllPointsInWay(id, raf);
			
			// ��������� �������� ��� ������, �������� ��� ���������
			// ����� ����� ���� �� ����� �-���
			raf.seek(seek);
			byte type = raf.readByte();
			
			if(type == Param.point) { // �����
				seeks = new long[1];
				seeks[0] = allSeeks.get(0); 
			} else if(type == Param.poligon_outer) { // ��������� ������
				if(allSeeks.size() % 3 == 0) { // ���������, ��� ���-�� ������������� ������
					seeks = new long[allSeeks.size() / 3];
							
					for(int i = 0, j = 0; i < allSeeks.size(); i += 3, j++) {
						seeks[j] = allSeeks.get(i); // ������ �������� ����� (������� ������)
					}
				}	
			} else { // ����� 
				if(allSeeks.size() > 1) { // ��������, ��� ��� �� �����
					seeks = new long[allSeeks.size() - 1];
							
					for(int i = 0, j = 0; i < allSeeks.size() - 1; i++, j++) {
						seeks[j] = allSeeks.get(i);
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
		
		return seeks;
	}
	
	/**
	 * ���������� ������ �������� ���� ����� �������� �����.
	 * @param seek �������� ������ ����� ��������
	 * @return ������ �������� ���� ����� �������� �����
	 */
	public long [] getSeeksForTreeFromFile(long seek) {
		long [] seeks = null;
		
		try {	
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			ArrayList<Long> allSeeks = getSeekAllPointsInWayFromFile(seek, raf);
			
			// ��������� �������� ��� ������, �������� ��� ���������
			// ����� ����� ���� �� ����� �-���
			raf.seek(seek);
			byte type = raf.readByte();
			
			if(type == Param.point) { // �����
				seeks = new long[1];
				seeks[0] = allSeeks.get(0); 
			} else if(type == Param.poligon_outer) { // ��������� ������
				if(allSeeks.size() % 3 == 0) { // ���������, ��� ���-�� ������������� ������
					seeks = new long[allSeeks.size() / 3];
							
					for(int i = 0, j = 0; i < allSeeks.size(); i += 3, j++) {
						seeks[j] = allSeeks.get(i); // ������ �������� ����� (������� ������)
					}
				}	
			} else { // ����� 
				if(allSeeks.size() > 1) { // ��������, ��� ��� �� �����
					seeks = new long[allSeeks.size() - 1];
							
					for(int i = 0, j = 0; i < allSeeks.size() - 1; i++, j++) {
						seeks[j] = allSeeks.get(i);
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
		
		return seeks;
	}
	
	/**
	 * ���������� �������� ���� ����� �������� �����.
	 * 
	 * @param seek ������� ������ ����� ��������
	 * @param raf raf ������ ��� ������� � �����
	 * @return �������� ���� ����� �������� �����
	 */
	public ArrayList<Long> getSeekAllPointsInWayFromFile(long seek, RandomAccessFile raf) {
		ArrayList<Long> allSeeks = new ArrayList<>();
		
		try {
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
	 * ���������� ��� ����� � ����� � ���������� �������� ���� ���� �����.
	 * 
	 * @param id ������������� �����
	 * @param raf ������ ��� ������� � �����
	 * @return ���������� �������� ���� ����� � �����
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
	 * ���������� ��� ������� �� �����.
	 * 
	 * @param seek �������� � ����� �����
	 * @return ��� ������� �� �����
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
	 * ���������� ��� �������� �����.
	 * 
	 * @param id ������������� �����
	 * @return ��� �������� �����
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
	 *  ��������� ����� �� ������� ����� �����.
	 *  
	 * @param wayId �������������� ����� � ���������
	 * @param nextWayId �������� ����� � ����� �����
	 * @return ���� ����� ����� ����� ����� - true, ����� - false
	 */
	
	public boolean checkNodesIdInWays(long wayId, long nextWayId) {
		boolean check = false; // ������� ������� ����� ����� � �����
		boolean tmp_check1 = false, tmp_check2 = false, tmp_check3 = false, tmp_check4 = false;
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			ArrayList<Long> nodes_ids_way_1 = new ArrayList<>(); // �������������� ����� ������ ����� � ���� ��� �����������
			ArrayList<Long> nodes_ids_way_2 = new ArrayList<>(); // �������������� ����� ������ ����� � ���� ��� �����������
			
			nodes_ids_way_1.addAll(getNodeIdsFromWay(wayId, raf));
			nodes_ids_way_2.addAll(getNodeIdsFromWay(nextWayId, raf));
			
			// ������ - ������
			tmp_check1 = compareCoordsNodes(nodes_ids_way_1.get(0), nodes_ids_way_2.get(0), raf);
			
			// ������ - ���������
			tmp_check2 = compareCoordsNodes(nodes_ids_way_1.get(0), nodes_ids_way_2.get(nodes_ids_way_2.size() - 1), raf);
			
			// ��������� - ������
			tmp_check3 = compareCoordsNodes(nodes_ids_way_1.get(nodes_ids_way_1.size() - 1), nodes_ids_way_2.get(0), raf);
			
			// ��������� - ���������
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
	 *  ��������� ����� �� ������� ����� ����� ��� ������������, � ������� ������������,
	 *  ����� �����.
	 *  
	 * @param wayId �������������� ����� � ���������
	 * @param nextWayId �������� ����� � ����� �����
	 * @param sort ����������, ����� ����� � ��������� ������ ������������
	 * @return ���� ����� ����� ����� ����� - true, ����� - false
	 */
	
	public boolean checkNodesIdInWays(long wayId, long nextWayId, Param.sort_points sort) {
		boolean check = false; // ������� ������� ����� ����� � �����
		boolean tmp_check1 = false, tmp_check2 = false, tmp_check3 = false, tmp_check4 = false;
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			ArrayList<Long> nodes_ids_way_1 = new ArrayList<>(); // �������������� ����� ������ ����� � ���� ��� �����������
			ArrayList<Long> nodes_ids_way_2 = new ArrayList<>(); // �������������� ����� ������ ����� � ���� ��� �����������
			
			nodes_ids_way_1.addAll(getNodeIdsFromWay(wayId, raf));
			nodes_ids_way_2.addAll(getNodeIdsFromWay(nextWayId, raf));
			
			// ������ - ������
			if(sort == Param.sort_points.first_first)
				tmp_check1 = compareCoordsNodes(nodes_ids_way_1.get(0), nodes_ids_way_2.get(0), raf);
			
			// ������ - ���������
			if(sort == Param.sort_points.first_last)
				tmp_check2 = compareCoordsNodes(nodes_ids_way_1.get(0), nodes_ids_way_2.get(nodes_ids_way_2.size() - 1), raf);
			
			// ��������� - ������
			if(sort == Param.sort_points.last_first)
				tmp_check3 = compareCoordsNodes(nodes_ids_way_1.get(nodes_ids_way_1.size() - 1), nodes_ids_way_2.get(0), raf);
			
			// ��������� - ���������
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
	 * ��������� ������ ��������� �/� ����� ����� � ��������� �� ���������� ��
	 * �� ��� �� ���� (�.�. ����� ����� ����������� �� ����� ������� �� ���������� ��).
	 * 
	 * @param array ������ �����
	 * @return ���� ���������� - true, ����� - false
	 */
	
	public boolean checkNodesIdInWaysFirstAndLastPoints(ArrayList<Long> array) {
		boolean check = false; // ������� ������� ����� ����� � �����
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			ArrayList<Long> nodes_ids_way_1 = new ArrayList<>(); // �������������� ����� ������ ����� � ���� ��� �����������
			ArrayList<Long> nodes_ids_way_2 = new ArrayList<>(); // �������������� ����� ������ ����� � ���� ��� �����������
			
			nodes_ids_way_1.addAll(getNodeIdsFromWay(array.get(0), raf));
			nodes_ids_way_2.addAll(getNodeIdsFromWay(array.get(array.size() - 1), raf));
			
			// ������ - ���������		
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
	 * ��������� ��������� ������� �������� �� ������� ����� �����.
	 * 
	 * @param outerWaysArray ������ ��������� ������� ���������
	 * @param incomplete ������� ��������� ���������
	 * @return ����� ������ ��������� ������� ���������
	 */
	
	public ArrayList<ArrayList<Long>> checkNodesIdInOuterWays(ArrayList<ArrayList<Long>> outerWaysArray) {
		boolean tmp_check1 = false, tmp_check2 = false, tmp_check3 = false, tmp_check4 = false;
		
		// ������������� ������� ��������
		sortWays(outerWaysArray);
					
		for(int i = 0; i < outerWaysArray.size() - 1; i++) {
			tmp_check1 = false;
			tmp_check2 = false;
			tmp_check3 = false;
			tmp_check4 = false;
			
			// ������ - ������
			tmp_check1 = checkNodesIdInWays(outerWaysArray.get(i).get(0), outerWaysArray.get(i + 1).get(0), 
					                        Param.sort_points.first_first);
			
			// ������ - ���������
			tmp_check2 = checkNodesIdInWays(outerWaysArray.get(i).get(0), 
				                            outerWaysArray.get(i + 1).get(outerWaysArray.get(i + 1).size() - 1),
				                            Param.sort_points.first_last);
			
			// ��������� - ������
			tmp_check3 = checkNodesIdInWays(outerWaysArray.get(i).get(outerWaysArray.get(i).size() - 1), 
				                            outerWaysArray.get(i + 1).get(0),
				                            Param.sort_points.last_first);			
			
			// ��������� - ���������
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
    					// ������������� ������� ��������
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
    					// ������������� ������� ��������
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
    					// ������������� ������� ��������
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
    					// ������������� ������� ��������
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
	 * ��������� �������� �������� �� �������������� � ������ ������� ����� �, 
	 * ���� ��� ���, ������ �� ���� ��������� ���� ����� ������� (������). 
	 * 
	 * @param outerWaysArray ������ ��������� ������� ���������
	 * @return ����� ������ ��������� ������� ���������
	 */
	
	public ArrayList<ArrayList<Long>> checkNodesIdInIncompleteOuterWays(ArrayList<ArrayList<Long>> outerWaysArray) {
		// ���� ��������� ������� �������� ����������� ������ ���������, ��������� �� ������, 
		// ������ ��������� ������� ������� �� ���������� ��� �� ����, �� ������� ����� �������
		// �� ��������� � ����������� ����� ��������� �����
		if(outerWaysArray.size() > 1) {
			// ������ ����������� ���������� ���������� � ���� ��������������� �����, �/� �������� 
			// ���������� ���������� ([length, id_point1, id_point2])
			double[][] tmp_length = new double[4][3]; 
			ArrayList<Long> tmp_ways = null;
			
			int size = outerWaysArray.size() - 1;
			
			for(int i = 0; i < size; i++) {
				// ���� ��� �������� �������� �� �������� ���� �� ����
				if(!checkNodesIdInWaysFirstAndLastPoints(outerWaysArray.get(i)) &&
				   !checkNodesIdInWaysFirstAndLastPoints(outerWaysArray.get(i + 1))) {
					
					// ���� �������, ��� ���� ����� ����� ���� � �����.
					// �� ��������, ��� ����� � �������� ��� �������������.
					double length = Double.MAX_VALUE; // ���������� ���������� ����� �������
					
					// ������ � ��������� �����
					long way_id_1_first = outerWaysArray.get(i).get(0);
					long way_id_1_last = outerWaysArray.get(i).get(outerWaysArray.get(i).size() - 1);
					long way_id_2_first = outerWaysArray.get(i + 1).get(0);
					long way_id_2_last = outerWaysArray.get(i + 1).get(outerWaysArray.get(i + 1).size() - 1);
					// �������� ����� ������ � ��������� �����
					long way_id_1_first_adjacent = 0;
					long way_id_1_last_adjacent = 0;
					long way_id_2_first_adjacent = 0;
					long way_id_2_last_adjacent = 0;
					
					// ������ �������� � ���������������� ����� ������������ � ���� ����� �����
					int size1 = outerWaysArray.get(i).size();
					int size2 = outerWaysArray.get(i + 1).size();
					
					// ���� ����� ����� ������� �� ����� ��� ����� �����, �� ����� �������� �����
					if(size1 > 1) {
						way_id_1_first_adjacent = outerWaysArray.get(i).get(1);
						way_id_1_last_adjacent = outerWaysArray.get(i).get(outerWaysArray.get(i).size() - 2);
					}
					
					if(size2 > 1) {
						way_id_2_first_adjacent = outerWaysArray.get(i + 1).get(1);
						way_id_2_last_adjacent = outerWaysArray.get(i + 1).get(outerWaysArray.get(i + 1).size() - 2);
					}
					
					// ������ - ������
					tmp_length[0] = checkLength(way_id_1_first, way_id_1_first_adjacent,
							                    way_id_2_first, way_id_2_first_adjacent);
										
					// ������ - ���������
					tmp_length[1] = checkLength(way_id_1_first, way_id_1_first_adjacent,
		                                        way_id_2_last, way_id_2_last_adjacent);
					
					// ��������� - ������
					tmp_length[2] = checkLength(way_id_1_last, way_id_1_last_adjacent,
		                                        way_id_2_first, way_id_2_first_adjacent);			
					
					// ��������� - ���������
					tmp_length[3] = checkLength(way_id_1_last, way_id_1_last_adjacent,
		                                        way_id_2_last, way_id_2_last_adjacent);
					
					int index = -1; // ������ ����������� ���������� � ������� (tmp_length[0][0])
					
					for(int j = 0; j < 4; j++) {
						if(tmp_length[j][0] < length) {
							length = tmp_length[j][0];
							index = j;
						}
					}
					
					if(outerWaysArray.size() > 1) {						
						switch(index) {
						case 0: // ������ - ������
						case 3: // ��������� - ���������
							tmp_ways = new ArrayList<>();
							
    						for(int j = outerWaysArray.get(i).size() - 1; j >= 0; j--)
    		    			    tmp_ways.add(outerWaysArray.get(i).get(j));	
						
    						tmp_ways.add(createNewEmptyWayFromPoints((long) tmp_length[index][1], (long) tmp_length[index][2]));
	    					tmp_ways.addAll(outerWaysArray.get(i + 1));
					
		    			    outerWaysArray.remove(i);
		    			    outerWaysArray.remove(i);
		     				outerWaysArray.add(tmp_ways);
							break;
						case 1: // ��������� - ������
						case 2: // ������ - ���������
							tmp_ways = new ArrayList<>();
							
		    				tmp_ways.addAll(outerWaysArray.get(i));
		    				tmp_ways.add(createNewEmptyWayFromPoints((long) tmp_length[index][1], (long) tmp_length[index][2]));
	    					tmp_ways.addAll(outerWaysArray.get(i + 1));
					
				    	    outerWaysArray.remove(i);
			    		    outerWaysArray.remove(i);
		   	    			outerWaysArray.add(tmp_ways);
							break;
						}
						
						size = outerWaysArray.size() - 1; // �������������� ������ ������� ��������
						i = -1;
					} 
				}
			}
			
			sortWays(outerWaysArray);
			
			// ����� ���������� ���� ����� � ���� ������ ������� ���������� �����
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
	 * ��������� ����� �� ������� ���� �������� ����� � ����� � ����������� ������������,
	 * � ���� ����� ����, �� ������� �������� ������ ����� � ����� � ������ ��� �����������
	 * �������� ����� �� ���� ����� �� �����.
	 * 
	 * @param seek �������� ������ ����� � �����
	 */
	
	public void checkDublicatCoords(long seek) {
		// ������ ����� ����� ����� �������� ����� �����
		boolean twins = false; 
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			raf.seek(seek + Param.id_seek);
			long way_id = raf.readLong();
			
			ArrayList<Long> nodes_ids_way = new ArrayList<>(); // �������������� ����� �����
			nodes_ids_way.addAll(getNodeIdsFromWay(way_id, raf));
			
			for(int i = 0; i < nodes_ids_way.size() - 1; i++) {
				twins = compareCoordsNodes(nodes_ids_way.get(i), 
		                                   nodes_ids_way.get(i + 1), raf);
				if(twins) {
					if(Param.ids_ways_with_dublicat_coords.indexOf(way_id) == -1) // ���� ��� � ������
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
	 * ���������� ��������������� ���������� �������� ���� ����� � �����,
	 * � ���� ��� �����, ������� ���� �� ���� �����.
	 */
	
	public void deleteDublicatCoordsNodesInWay() {
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			for(int i = 0; i < Param.ids_ways_with_dublicat_coords.size(); i++) {
				long way_id = Param.ids_ways_with_dublicat_coords.get(i);
				
				ArrayList<Long> nodes_ids_way = new ArrayList<>(); // �������������� ����� �����
				nodes_ids_way.addAll(getNodeIdsFromWay(way_id, raf));
				
				// ������ ����� ����� ����� �������� ����� �����
				boolean twins = false; 
				
				if(nodes_ids_way.size() == 2) {
					twins = compareCoordsNodes(nodes_ids_way.get(0), 
	                                           nodes_ids_way.get(nodes_ids_way.size() - 1), raf);
					if(twins) {
						// �������� ������ �� ��������� ����� ����� � ���������� �� �����
						raf.seek(Param.seek_nodes.get(nodes_ids_way.get(0) + Param.next_seek));
						raf.writeLong(0);
						
						// �������������� � �������� ��������� �����
						deleteNodeFromList(nodes_ids_way.get(nodes_ids_way.size() - 1), raf);
						
						// ������� ��������� ����� ����� �� ������
						nodes_ids_way.remove(nodes_ids_way.size() - 1);
						// �������� ���������� ����� � ����� � ������
						Param.num_points.remove(way_id);
						Param.num_points.put(way_id, nodes_ids_way.size());
					}
				} else if (nodes_ids_way.size() >= 3) {
					for(int j = 0; j < nodes_ids_way.size() - 1; j++) {
						twins = compareCoordsNodes(nodes_ids_way.get(j), 
				                                   nodes_ids_way.get(j + 1), raf);
						if(twins) {
							// ��������� �������� ����� ����������� �� �������� ������
							raf.seek(Param.seek_nodes.get(nodes_ids_way.get(j + 1)) + Param.next_seek);
							long nextSeek = raf.readLong();
							
							// ���������� ����� �������� �������� ��������� ����� � ������ ������������ �����
							raf.seek(Param.seek_nodes.get(nodes_ids_way.get(j)) + Param.next_seek);
							raf.writeLong(nextSeek);
							
							// �������������� � �������� ��������� �����
							deleteNodeFromList(nodes_ids_way.get(j + 1), raf);
							
							// ������� ��������� ����� ����� �� ������
							nodes_ids_way.remove(j + 1);
							// �������� ���������� ����� � ����� � ������
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
	 * ������� ����� ����� �� ���� �������� ������.
	 * 
	 * @param node_id_1 ������������� ������ �����
	 * @param node_id_2 ������������� ������ �����
	 * @return ������������� ����� �����
	 */
	
	public long createNewEmptyWayFromPoints(long node_id_1, long node_id_2) {
		long id = -1; // ������������� ����� �����
		
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
	 * ������� ����� �����. ����� ��������� ����� ������ ����� � ������ �����
	 * ������ �����.
	 * 
	 * @param way_id_1 ������ �����
	 * @param way_id_2 ������ �����
	 * @return ������������� ����� �����
	 */
	
	public long createNewEmptyWay(long way_id_1, long way_id_2) {
		long id = -1; // ������������� ����� �����
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			ArrayList<Long> nodes_ids_way_1 = new ArrayList<>(); // �������������� ����� ������ �����
			ArrayList<Long> nodes_ids_way_2 = new ArrayList<>(); // �������������� ����� ������ �����
			
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
	 * ���� ���������� ���������� �/� �������� ������� ����� ����� ����� � ����������
	 * ��� ���������� � ���������������� ���� �����.
	 * 
	 * @param outerWaysArray ������ ��������������� �����, �� ������� ������� ���� ����� �����
	 * @return ���������� ���������� �/� �������� ������� ����� ����� ����� � ���������������� ���� �����
	 */
	
	private double[] checkLengthForOneWay(ArrayList<Long> outerWaysArray) {
		// ���������� �/� ������� � �������������� ���� ����� ��� ��������� ���� �����
		double [] length = { Double.MAX_VALUE, 0, 0 }; 
		// �����, �� ������� ������ �������������� ����� (�� ���������� �-���)
		long id_point_1 = 0; 
		long id_point_2 = 0;
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			ArrayList<Long> nodes_ids_way_first = new ArrayList<Long>(); // �������������� ����� ������ �����
			ArrayList<Long> nodes_ids_way_first_adjacent = null; // �������������� ����� �������� � ������ �����
			
			ArrayList<Long> nodes_ids_way_last = new ArrayList<Long>(); // �������������� ����� ��������� �����
			ArrayList<Long> nodes_ids_way_last_adjacent = null; // �������������� ����� �������� � ��������� �����
			
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
				
				// ���� ������� ����� � ����� �����
				// ������ ����� � ����� �����
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
				
				// ������ ����� � ����� �����
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
			
			// ���������� ����� ��� ���������� ����������
			double x1 = getLongitude(id_point_1);
			double y1 = getLatitude(id_point_1);
			
			double x2 = getLongitude(id_point_2);
			double y2 = getLatitude(id_point_2);
		
			// ������ ���������� �/� ������� � ���������� �������������� ���� �����
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
	 * ���� ���������� ���������� �/� �������� ������� ���� ����� � ����������
	 * ��� ���������� � ���������������� ���� �����.
	 * 
	 * @param way_id_1 ����� ������
	 * @param way_id_1_adjacent �������� � ������ �����
	 * @param way_id_2 ����� ������
	 * @param way_id_2_adjacent �������� �� ������ �����
	 * @return ���������� ���������� �/� �������� ������� ���� ����� � ���������������� ���� �����
	 */
	
	private double[] checkLength(long way_id_1, long way_id_1_adjacent, 
			                     long way_id_2, long way_id_2_adjacent) {
		// ���������� �/� ������� � �������������� ���� ����� ��� ��������� ���� �����
		double [] length = { Double.MAX_VALUE, 0, 0 }; 
		// �����, �� ������� ������ �������������� ����� (�� ���������� �-���)
		long id_point_1 = 0; 
		long id_point_2 = 0;
		
		// ���� � �-��� �� ������� �������� �����, �� ������ ����� ����� ������� �� ����� �����.
		// ����� ��������� ���������� ���������� �/� ����� ������ ������� ����� �������� ���
		// �/� ������ �� �����. ��� ������� ���������� �� ���� ������
		long id_point_1_1 = 0; // ������ ����� ������ �����
		long id_point_1_2 = 0; // ������ ����� ������ �����
		long id_point_2_1 = 0; // ������ ����� ������ �����
		long id_point_2_2 = 0; // ������ ����� ������ �����
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			ArrayList<Long> nodes_ids_way_1 = new ArrayList<Long>(); // �������������� ����� ������ �����
			ArrayList<Long> nodes_ids_way_1_adjacent = null; // �������������� ����� �������� � ������ �����
			
			ArrayList<Long> nodes_ids_way_2 = new ArrayList<Long>(); // �������������� ����� ������ �����
			ArrayList<Long> nodes_ids_way_2_adjacent = null; // �������������� ����� �������� �� ������ �����
			
			if(way_id_1 != 0)
				nodes_ids_way_1.addAll(getNodeIdsFromWay(way_id_1, raf));
			
			if(way_id_2 != 0)
				nodes_ids_way_2.addAll(getNodeIdsFromWay(way_id_2, raf));
			
			// ���� � �������� ����� ������� ��������, �� ������� ��������� (�������) ����� �������� �����
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
			
			// ���������� ����� ��� ���������� ����������
			double x1 = 0;
			double y1 = 0;
			double x2 = 0;
			double y2 = 0;
			
			if(id_point_1 == 0 && id_point_2 == 0) { // ����� ����� ������� ������ �� ����� �����
				double x11 = getLongitude(id_point_1_1);
				double y11 = getLatitude(id_point_1_1);	
				double x12 = getLongitude(id_point_1_2);
				double y12 = getLatitude(id_point_1_2);
				
				double x21 = getLongitude(id_point_2_1);
				double y21 = getLatitude(id_point_2_1);	
				double x22 = getLongitude(id_point_2_2);
				double y22 = getLatitude(id_point_2_2);
				
				// ������ ���������� �/� ������� � ���������� �������������� ���� �����
				double length11_21 = Math.abs(Math.sqrt(Math.pow((x21 - x11), 2) + Math.pow((y21 - y11), 2)));
				double length11_22 = Math.abs(Math.sqrt(Math.pow((x22 - x11), 2) + Math.pow((y22 - y11), 2)));
				double length12_21 = Math.abs(Math.sqrt(Math.pow((x21 - x12), 2) + Math.pow((y21 - y12), 2)));
				double length12_22 = Math.abs(Math.sqrt(Math.pow((x22 - x12), 2) + Math.pow((y22 - y12), 2)));
				
				double [] all_length = { length11_21, length11_22, length12_21, length12_22 };
				int index = -1; // ������ � ������� � ���������� ����������� �/� �������				
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
			} else if(id_point_1 == 0 && id_point_2 != 0) { // ������ ����� ����� ������� �� ����� �����, ������ ����� ��� �����				
				x2 = getLongitude(id_point_2);
				y2 = getLatitude(id_point_2);
				
				double x11 = getLongitude(id_point_1_1);
				double y11 = getLatitude(id_point_1_1);				
				// ������ ���������� �/� ������� � ���������� �������������� ���� �����
				double length11 = Math.abs(Math.sqrt(Math.pow((x2 - x11), 2) + Math.pow((y2 - y11), 2)));
				
				double x12 = getLongitude(id_point_1_2);
				double y12 = getLatitude(id_point_1_2);
				// ������ ���������� �/� ������� � ���������� �������������� ���� �����
				double length12 = Math.abs(Math.sqrt(Math.pow((x2 - x12), 2) + Math.pow((y2 - y12), 2)));
				
				if(length11 <= length12)
					id_point_1 = id_point_1_1;
				else
					id_point_1 = id_point_1_2;	
				
				x1 = getLongitude(id_point_1);
				y1 = getLatitude(id_point_1);
			} else if(id_point_1 != 0 && id_point_2 == 0) { // ������ ����� ����� ������� �� ����� �����, ������ ����� ��� �����				
				x1 = getLongitude(id_point_1);
				y1 = getLatitude(id_point_1);
				
				double x21 = getLongitude(id_point_2_1);
				double y21 = getLatitude(id_point_2_1);				
				// ������ ���������� �/� ������� � ���������� �������������� ���� �����
				double length21 = Math.abs(Math.sqrt(Math.pow((x1 - x21), 2) + Math.pow((y1 - y21), 2)));
				
				double x22 = getLongitude(id_point_2_2);
				double y22 = getLatitude(id_point_2_2);
				// ������ ���������� �/� ������� � ���������� �������������� ���� �����
				double length22 = Math.abs(Math.sqrt(Math.pow((x1 - x22), 2) + Math.pow((y1 - y22), 2)));
				
				if(length21 <= length22)
					id_point_2 = id_point_2_1;
				else
					id_point_2 = id_point_2_2;	
				
				x2 = getLongitude(id_point_2);
				y2 = getLatitude(id_point_2);
			} else if(id_point_1 != 0 && id_point_2 != 0) { // ��� ����� ��� ����� ��� �����
				x1 = getLongitude(id_point_1);
				y1 = getLatitude(id_point_1);
				
				x2 = getLongitude(id_point_2);
				y2 = getLatitude(id_point_2);
			}
			
			// ������ ���������� �/� ������� � ���������� �������������� ���� �����
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
	 * ���������� ���������� ������ � ��������� ����� � �����.
	 * 
	 * @param way_id ������������� ����� � ����� �����
	 * @return ���������� true, ���� ���������� �����, ����� - false
	 */
	
	public boolean checkFirstAndLastNodes(long way_id) {
		boolean check = false; // ������� ��������� �����
		ArrayList<Long> nodes_way = getNodeIdsFromWay(way_id); // ��� ����� �����
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			// ���������� ������ � ��������� ����� �����
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
	 * ���������� ���������� ������ � ��������� ����� � ������ ��������������� �����.
	 * 
	 * @param ways_ids ������ ��������������� ����� � ����� �����
	 * @return ���������� true, ���� ���������� �����, ����� - false
	 */
	
	public boolean checkFirstAndLastNodesInWays(ArrayList<Long> ways_ids) {
		boolean check = false; // ������� ��������� �����
		ArrayList<Long> nodes_way_first = getNodeIdsFromWay(ways_ids.get(0)); // ��� ����� ������ �����
		ArrayList<Long> nodes_way_last = getNodeIdsFromWay(ways_ids.get(ways_ids.size() - 1)); // ��� ����� ��������� �����
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			// ���������� ������ ����� ������ � ������ ����� � ��������� ����� ��������� � ������ �����
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
	 * �������������� ������������� �����.
	 * 
	 * @param seek �������� �����, ������������� ������� ��������������
	 * @param newId ����� �������������
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
	 * ������� ����� ����� �� ��������� ��������, �� � ������ ���������������.
	 * 
	 * @param seek �������� �����, ������� ��������
	 * @return �������� ����� �����
	 */
	
	public long createNewNode(long seek) {		
		long myselfSeek = -1; // ����������� �������� ����� �����
		Param.maxNodeId++; // ����� ������������� �����
		if (Param.newIndex == 4569057068l) {
			int y = 0;
		}
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			myselfSeek = raf.length(); // ����� �������� ����������� ����� ��������
			
			// �������� ������ �� �����
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
			
			// ������� ����� ����� � ��������� �������			
			// ��� ��������
			raf.seek(myselfSeek);			
			raf.writeByte(type);
			
			// ��������
			raf.seek(myselfSeek + Param.delete_seek);			
			raf.writeByte(0x00);
			
			// �������������
			raf.seek(myselfSeek + Param.id_seek);	
			raf.writeLong(Param.maxNodeId);
			
			// ���� ��������
			raf.seek(myselfSeek + Param.myself_seek);	
			raf.writeLong(myselfSeek);
			
			// �������� ���� ��������
			raf.seek(myselfSeek + Param.next_seek);	
			raf.writeLong(next);
			
			// �������� �������� ��������
			raf.seek(myselfSeek + Param.attr_seek);	
			raf.writeLong(attrSeek);
			
			// ������
			raf.seek(myselfSeek + Param.lat_seek);
			raf.writeDouble(lat);
			
			// �������
			raf.seek(myselfSeek + Param.lon_seek);	
			raf.writeDouble(lon);
			
			// ������ ��� ������� ����
			raf.seek(myselfSeek + Param.alt_seek);	
			raf.writeFloat(alt);
			
			// ��������
			raf.seek(myselfSeek + Param.acc_seek);	
			raf.writeFloat(acc);
			
			// ������� �������������� �������� � ��������������� ������� ���������� ������� �����
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
	 * ������� ������ ����� � ����� ���������������.
	 * 
	 * @param raf ������ ��� ������� � �����
	 * @return �������� ����� �����
	 */
	
	public long createNewEmptyNode(RandomAccessFile raf) {		
		long myselfSeek = -1; // ����������� �������� ����� �����
		Param.newIndex++; // ����� ������������� �����
		
		try {			
			myselfSeek = raf.length(); // ����� �������� ����������� ����� ��������
			
			// ������� ����� ����� � ��������� �������			
			// ��� ��������
			raf.seek(myselfSeek);			
			raf.writeByte(Param.point);
			
			// ��������
			raf.seek(myselfSeek + Param.delete_seek);			
			raf.writeByte(0x00);
			
			// �������������
			raf.seek(myselfSeek + Param.id_seek);	
			raf.writeLong(Param.newIndex);
			
			// ���� ��������
			raf.seek(myselfSeek + Param.myself_seek);	
			raf.writeLong(myselfSeek);
			
			// �������� ���� ��������
			raf.seek(myselfSeek + Param.next_seek);	
			raf.writeLong(0);
			
			// �������� �������� ��������
			raf.seek(myselfSeek + Param.attr_seek);	
			raf.writeLong(0);
			
			// ������
			raf.seek(myselfSeek + Param.lat_seek);
			raf.writeDouble(0);
			
			// �������
			raf.seek(myselfSeek + Param.lon_seek);	
			raf.writeDouble(0);
			
			// ������ ��� ������� ����
			raf.seek(myselfSeek + Param.alt_seek);	
			raf.writeFloat(0);
			
			// ��������
			raf.seek(myselfSeek + Param.acc_seek);	
			raf.writeFloat(0);
			
			// ������� �������������� �������� � ��������������� ������� ���������� ������� �����
			raf.seek(myselfSeek + Param.boundary_seek);
			raf.writeByte(0x00);					
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return myselfSeek;
	}
	
	/**
	 * ������� ����� ����� � ����� � ����� map.dnvg �� ��������� ��������, �� � ������ ���������������.
	 * 
	 * @param seek �������� �����, ������� ��������
	 * @return �������� ����� �����
	 */
	
	public long createNewNodeForWay(long seek) {
		long myselfSeek = -1; // ����������� �������� ����� �����
		if (Param.newIndex == 4569057068l) {
			int y = 0;
		}
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			myselfSeek = raf.length();
						
			// �������� ������ �� �����
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
			
			// ������� ����� ����� � ��������� �������			
			// ��� ��������
			raf.seek(myselfSeek);			
			raf.writeByte(type);
			
			// ��������
			raf.seek(myselfSeek + Param.delete_seek);			
			raf.writeByte(0x00);
			
			// �������������
			raf.seek(myselfSeek + Param.id_seek);	
			raf.writeLong(Param.maxNodeId);
			
			// ���� ��������
			raf.seek(myselfSeek + Param.myself_seek);	
			raf.writeLong(myselfSeek);
			
			// �������� ���� ��������
			raf.seek(myselfSeek + Param.next_seek);	
			raf.writeLong(next);
			
			// �������� �������� �������� �����
			raf.seek(myselfSeek + Param.attr_seek);	
			raf.writeLong(attrSeek);
			
			// ������
			raf.seek(raf.length());	
			raf.writeDouble(lat);
			
			// �������
			raf.seek(raf.length());	
			raf.writeDouble(lon);
			
			// ������ ��� ������� ����
			raf.seek(raf.length());	
			raf.writeFloat(alt);
			
			// ��������
			raf.seek(raf.length());	
			raf.writeFloat(acc);
			
			// ������� �������������� �������� � ��������������� ������� ���������� ������� �����
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
	 * ������� ����� ����� � ����� � ����� map.dnvg �� ��������� ��������, �� � ������ ���������������.
	 * 
	 * @param seek �������� �����, ������� ��������
	 * @param raf ������ ��� ������� � �����
	 * @return �������� ����� �����
	 */
	
	public long createNewNodeForWay(long seek, RandomAccessFile raf) {
		long myselfSeek = -1; // ����������� �������� ����� �����
		if (Param.newIndex == 4569057068l) {
			int y = 0;
		}
		try {			
			myselfSeek = raf.length();
			
			// �������� ������ �� �����
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
			
			// ������� ����� ����� � ��������� �������			
			// ��� ��������
			raf.seek(myselfSeek);			
			raf.writeByte(type);
			
			// ��������
			raf.seek(myselfSeek + Param.delete_seek);			
			raf.writeByte(0x00);
			
			// �������������
			raf.seek(myselfSeek + Param.id_seek);	
			raf.writeLong(Param.maxNodeId);
			
			// ���� ��������
			raf.seek(myselfSeek + Param.myself_seek);	
			raf.writeLong(myselfSeek);
			
			// �������� ���� ��������
			raf.seek(myselfSeek + Param.next_seek);	
			raf.writeLong(next);
			
			// �������� �������� �������� �����
			raf.seek(myselfSeek + Param.attr_seek);	
			raf.writeLong(attrSeek);
			
			// ������
			raf.seek(raf.length());	
			raf.writeDouble(lat);
			
			// �������
			raf.seek(raf.length());	
			raf.writeDouble(lon);
			
			// ������ ��� ������� ����
			raf.seek(raf.length());	
			raf.writeFloat(alt);
			
			// ��������
			raf.seek(raf.length());	
			raf.writeFloat(acc);
			
			// ������� �������������� �������� � ��������������� ������� ���������� ������� �����
			raf.seek(myselfSeek + Param.boundary_seek);			
			raf.writeByte(boundary);			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return myselfSeek;
	}
	
	/**
	 * ���������� ������������� �������� �����.
	 * 
	 * @param seek �������� �������� �����
	 * @param raf ������ ��� ������� � �����
	 * @return ������������� �������� �����
	 */
	public long getId(long seek, RandomAccessFile raf) {
		long id = -1;
		
		try {			
			raf.seek(seek + Param.id_seek);
			id = raf.readLong();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return id;
	}
	
	/**
	 * ���������� ������ (���-�� �����) � �������� �����.
	 * 
	 * @param id ������������� �������� �����
	 * @return ������ (���-�� �����)
	 */ 
	
	public int getElementSize(long id) {
		 return getNodeIdsFromWay(id).size();
	}

	/**
	 * ���������� ������ � ������������ ����� �������� ����� � ����
	 * {x1, y1, x2, y2, ... ,xN, yN}.
	 * 
	 * @param id ������������� �������� �����
	 * @return ������ � ������������ ����� �������� �����
	 */
	
	public double [] getArrayCoords(long id) {
		// ������ � ������������ ����� �������� �����
		double [] tmp_array = new double[getElementSize(id) * 2];
		
		ArrayList<Long> node_ids = getNodeIdsFromWay(id);
		
		for(int i = 0, j = 0; i < tmp_array.length; i+=2, j++) {
			tmp_array[i] = getLongitude(node_ids.get(j));
			tmp_array[i + 1] = getLatitude(node_ids.get(j));
		}
		
		return tmp_array;
	}
	
	/**
	 * ���������� �������� �������� � �������� �����.
	 * 
	 * @param seek �������� �������� ����� � ����� map.dnvg
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
	 * ���������� ������ ��������������� ���� ��������� ����� ��������.
	 * 
	 * @param firstPointInWay ������������� ������ ����� � ��������
	 * @return  ������ ��������������� ���� ����� �����
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
				
				raf.seek(seek + Param.next_seek);  // �������� �������� ���������� �������� � �����
				long nextSeek = raf.readLong(); // �������� ���������� ��������
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
	 * ���������� ������������� ����� �� �� ��������.
	 * 
	 * @param seek �������� ����� � ����� �����
	 * @return ������������� �����
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
	 * ���������� ������ ��������������� ���� ����� �����.
	 * 
	 * @param wayId ������������� �����
	 * @return  ������ ��������������� ���� ����� �����
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
					
					raf.seek(seek + Param.next_seek);  // �������� �������� ���������� �������� � �����
					long nextSeek = raf.readLong(); // �������� ���������� ��������
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
	 * ���������� ������ �������� ���������� ��� �������. � ������ ���������� �������, ��
	 * ������ - ������. ������ ����������� �� �����.
	 * 
	 * @param seek �������� ������ ����� � �����
	 * @param raf ������ ��� ������� � �����
	 * @return ������ ���������� ������ � ������������ �� ������� � ������
	 */
	public double [][] getCoordsFromWayFromFile(long seek, RandomAccessFile raf) {
		ArrayList<Double> lon_x = new ArrayList<Double>();
		ArrayList<Double> lat_y = new ArrayList<Double>();
		
		double [][] arrays = new double [2][];
		
		try {
			raf.seek(seek);
			byte type = raf.readByte();
			
			if(type == Param.point) {
				raf.seek(seek + Param.lat_seek);
				lat_y.add(raf.readDouble());
				lon_x.add(raf.readDouble());
				
				double [] array_lon_x = new double [lon_x.size()];
				double [] array_lat_y = new double [lat_y.size()];
				
				arrays[0] = array_lon_x;
				arrays[1] = array_lat_y;
			} else {
				while(seek != 0) {
					raf.seek(seek + Param.lat_seek);
					lat_y.add(raf.readDouble());
					lon_x.add(raf.readDouble());
					
					raf.seek(seek + Param.next_seek);  // �������� �������� ���������� �������� � �����
					long nextSeek = raf.readLong(); // �������� ���������� ��������
					seek = nextSeek;
				}
				
				double [] array_lon_x = new double [lon_x.size()];
				double [] array_lat_y = new double [lat_y.size()];
				
				for (int i = 0; i < lon_x.size(); i++) {					
					array_lon_x[i] = lon_x.get(i);
					array_lat_y[i] = lat_y.get(i);
				}
				
				arrays[0] = array_lon_x;
				arrays[1] = array_lat_y;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return arrays;
	}
	
	/**
	 * ���������� ������ ��������������� ���� ����� �����.
	 * 
	 * @param wayId ������������� �����
	 * @param raf ������ ��� ������� � �����
	 * @return  ������ ��������������� ���� ����� �����
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
					
					raf.seek(seek + Param.next_seek);  // �������� �������� ���������� �������� � �����
					long nextSeek = raf.readLong(); // �������� ���������� ��������
					seek = nextSeek;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return node_ids;
	}
	
	/**
	 * ������� ����� ����� � ������ ��������������� � ������� �������, ��������� � �������
	 * ������� ��������� � ������� x1,y1, ... xN, yN.
	 * 
	 * @param coords ������ ��������� �����
	 * @param seek �������� ����������� ������� 
	 * @param raf ������ ��� ������� � �����
	 * @return �������� ����� �����
	 */
	
	public long createNewWay(double[] coords, long seek, RandomAccessFile raf) {
		long first = -1; // ����������� �������� ������ ����� ����� �����
		ArrayList<Long> new_seek_nodes = new ArrayList<>(); // ����� �������� �����
		if (Param.newIndex == 4569057068l) {
			int y = 0;
		}
		try {			
			// ������� ����� ����� � ��������� �������	
			long newWayId = -1;
			
			// �������� ������ �� �����
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
				
				// ������� ����� ����� � ��������� �������			
				// ��� ��������
				raf.seek(myselfSeek);			
				raf.writeByte(type);
				
				// ��������
				raf.seek(myselfSeek + Param.delete_seek);			
				raf.writeByte(0x00);
				
				// �������������
				raf.seek(myselfSeek + Param.id_seek);	
				raf.writeLong(Param.newIndex);
				
				// ���� ��������
				raf.seek(myselfSeek + Param.myself_seek);	
				raf.writeLong(myselfSeek);
				
				// �������� ���� ��������
				raf.seek(myselfSeek + Param.next_seek);	
				raf.writeLong(0);
				
				// �������� �������� �������� �����
				raf.seek(myselfSeek + Param.attr_seek);				
				raf.writeLong(newAttrSeek);
				
				// ������
				raf.seek(raf.length());	
				raf.writeDouble(lat);
				
				// �������
				raf.seek(raf.length());	
				raf.writeDouble(lon);
				
				// ������ ��� ������� ����
				raf.seek(raf.length());	
				raf.writeFloat(alt);
				
				// ��������
				raf.seek(raf.length());	
				raf.writeFloat(acc);
				
				// ������� �������������� �������� � ��������������� ������� ���������� ������� �����
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
			
			// ���������� ����� �������� ��������� ����� � �����
			setSeekNextNodesInWay(new_seek_nodes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return first;
	}
		
	/**
	 * ������� ����� ����� � ������ ���������������.
	 * 
	 * @param node_ids ������ id ����� � �����, ����� ������� ���� �������
	 * @return �������� ����� �����
	 */
	
	public long createNewWay(ArrayList<Long> node_ids) {		
		long myselfSeek = -1; // ����������� �������� ������ ����� ����� �����
		ArrayList<Long> new_seek_nodes = new ArrayList<>(); // ����� �������� �����
		if (Param.newIndex == 4569057068l) {
			int y = 0;
		}
		try {
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			myselfSeek = raf.length();
			
			if(raf != null)
				raf.close();
						
			// ������� ����� ����� � ��������� �������	
			long newWayId = -1;
			
			for(int i = 0; i < node_ids.size(); i++) {
				if(i == 0)
					newWayId = ++Param.maxNodeId;
				else
					++Param.maxNodeId;
				
				long seek = Param.seek_nodes.get(node_ids.get(i));
				
				long new_seek = createNewNodeForWay(seek);
				
				new_seek_nodes.add(new_seek);
				Param.seek_nodes.put(Param.maxNodeId, new_seek);
				Param.seek_nodes_used.put(Param.maxNodeId, new_seek);
				
				if(i == 0) {
					Param.my_ways_ids.put(newWayId, newWayId);
					Param.seek_ways.put(newWayId, new_seek);
					Param.num_points.put(newWayId, node_ids.size());
				}
			}
			
			// ���������� ����� �������� ��������� ����� � �����
			setSeekNextNodesInWay(new_seek_nodes);
			
			// ������� ����� �������� � ���������� ����� �������� �������� � ����� �����
			createCopyAttrInWay(new_seek_nodes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return myselfSeek;
	}
	
	/**
	 * ������� ����� �������� � ���������� ����� �������� �������� � ����� �����.
	 * 
	 * @param firstSeek ������ ����� � �����
	 */
	
	public void createCopyAttrInWay(Long firstSeek) {		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			// ��������� ������� �������� � �����
			raf.seek(firstSeek + Param.attr_seek);
			long attrSeek = raf.readLong();
			
			// ���� ������� � ����� �������, ������� ��� �����
			if(attrSeek > 0) {
				long newAttrSeek = createCopyAttr(attrSeek, firstSeek); // �������� �������
				
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
	 * ������� ����� �������� � ���������� ����� �������� �������� � ����� �����.
	 * 
	 * @param seek_nodes �����, �� ������� ������� �����
	 */
	
	public void createCopyAttrInWay(ArrayList<Long> seek_nodes) {
		long firstSeek = seek_nodes.get(0); // ������ ����� �����
		
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			// ��������� ������� �������� � �����
			raf.seek(firstSeek + Param.attr_seek);
			long attrSeek = raf.readLong();
			
			// ���� ������� � ����� �������, ������� ��� �����
			if(attrSeek > 0) {
				long newAttrSeek = createCopyAttr(attrSeek, firstSeek); // �������� �������
				
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
	 * ������� � ���������� � ����� ����� ����� �������, ��������� �� ������������� 
	 * (����������������� �������).
	 * 
	 * @param triangles ������ �������������
	 * @param allSeeks �������� �����, ������������ ��� ��������(��) �� ������������
	 * @param bounderyCoords ���������� � ������� (x, y) ��������� (�������, ���������) �����
	 * ������ ��������
	 * @param firstPointInWay �������� ������ ����� �������� �������� �� ��� ������������
	 * @return �������� ������ ����� �������� (������ ����� ������� ������������) ���� 0
	 */
	
	public long createNewTrianglePoligon(double [][] triangles, ArrayList<Long> allSeeks, 
			                             double [] bounderyCoords, long firstPointInWay) {
		long firstSeek = 0;
		
		if(triangles != null && triangles.length != 0 && allSeeks != null && allSeeks.size() > 1) {			
			try {
				h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
				raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
				
				// ���� ����� � ������������� ������, ��� � �������� ��������
				if ((triangles.length * 3) < allSeeks.size()) {
					ArrayList<Long> tmp = new ArrayList<>();
					
					for (int i = 0; i < (triangles.length * 3); i++)						
						tmp.add(allSeeks.get(i));
					
					// �������������� � �������� ���������� ������ �����
					for (int i = (triangles.length * 3); i < allSeeks.size(); i++)
						deleteNodeFromList(getId(allSeeks.get(i), raf), raf, h_raf);
					
					// � ��������� ����� �������� ������ �� ��������� �����
					setNextSeek(tmp.get(tmp.size() - 1), 0, raf);
					
					allSeeks = null;
					allSeeks = tmp;
				}
				
				// �������� ��������� ������ �� ������ ����� �������� �������� 
				// �� ��� ������������
				raf.seek(firstPointInWay + Param.attr_seek);
				long attrSeek = raf.readLong();				
				
				raf.seek(firstPointInWay + Param.alt_seek);
				float alt = raf.readFloat();
				float acc = raf.readFloat();
				
				// ��������� ��� �������� ����� ������������ �� ������������ � ���� �����
				for(int i = 0; i < allSeeks.size() - 1; i++) {
					long seek = allSeeks.get(i);
					long nextSeek = allSeeks.get(i + 1);
					
					if(i == 0) {
						firstSeek = seek;
						
						// ���������� � ������ ���� ����� ����� ����� ����� � ������������ ���-�� ����� �����
						raf.seek(firstSeek + Param.id_seek);
						long id = raf.readLong();
						
						Param.new_seek_only_ways.put(id, firstSeek);
						Param.num_points.put(id, (triangles.length * 3));
					}
					
					setNextSeek(seek, nextSeek, raf);
				}
				
				// �.�. ������ ������������� �������� ��������� ������������, � �������
				// ��� �������, �� �������� ������ ����� ������� �� 3
				if((triangles.length * 3) > allSeeks.size()) {
					long prevSeek = allSeeks.get(allSeeks.size() - 1);
					
					// �������� ��� ������������ �������� �� ������ ���-�� ��������
					for(int i = 0; i < (triangles.length * 3) - allSeeks.size(); i++) {						
						long newSeek = createNewEmptyNode(raf);
						setNextSeek(prevSeek, newSeek, raf);
						prevSeek = newSeek;
						
						// ��������� ����� ��������� ����� �����
						Param.new_seek_nodes.put(Param.newIndex, newSeek);
					}
				}
				
				// ������ ��������� ������� ����� ������������������ ��������
				h_raf.seek(attrSeek + Param.firsPointInWay_seek);
				h_raf.writeLong(firstSeek);
								
				setType(Param.poligon_outer, firstSeek, firstSeek, raf);
				setAttrSeek(firstSeek, firstSeek, attrSeek, raf);
				setAltitude(firstSeek, firstSeek, alt, raf);
				setAccuracy(firstSeek, firstSeek, acc, raf);
				
				// ����������
				long tmpSeek = firstSeek;
				
				for(int i = 0; i < triangles.length; i++) {					
					double [] coords = triangles[i];
					
					for(int j = 0; j < coords.length; j+=2) {
						raf.seek(tmpSeek + Param.lat_seek);
						raf.writeDouble(coords[j + 1]);
						
						raf.seek(tmpSeek + Param.lon_seek);
						raf.writeDouble(coords[j]);
						
						// ���� ���������� �������
						int size = bounderyCoords.length;
						
						if(size > 1) {
							// ����� ��������� ��� �� �������� ����� ��� ��������� � ������ �������������
							// ����� �� ������� �� ������� ��������� ���������
							ArrayList<Double> tmpBoundaryCoords = new ArrayList<>();
							
							for(int k = 0; k < size; k+=2) {
								if(coords[j] == bounderyCoords[k] && coords[j + 1] == bounderyCoords[k + 1]) {
									setBoundary(tmpSeek, raf);
								} else {
									tmpBoundaryCoords.add(bounderyCoords[k]);
									tmpBoundaryCoords.add(bounderyCoords[k + 1]);
								}
							}
							
							// �������� ������ ������� ��������� ���������
							Double [] tmpArray = new Double[tmpBoundaryCoords.size()];
							tmpBoundaryCoords.toArray(tmpArray);
							
							bounderyCoords = new double[tmpArray.length];
							
							for(int m = 0; m < tmpArray.length; m++) {
								bounderyCoords[m] = tmpArray[m];
							}	
						}					
						
						// ��������� ����� �������� �����
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
	 * ���������� �������� �������� ������ ����� � ����� � ������� ��������.
	 * 
	 * @param firstPointInWay �������� ������ ����� � �����
	 * @param raf ������ ��� ������� � �����
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
	 * � ����� ������ ����� ����� ��������� �� ���������.
	 * 
	 * @param seek_nodes �������� ����� �����
	 */
	
	private void setSeekNextNodesInWay(ArrayList<Long> seek_nodes) {
		// ���������� ����� �������� ��������� ����� � �����
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
	 * � ����� ������ ����� ����� ��������� �� ���������.
	 * 
	 * @param seek_nodes �������� ����� �����
	 * @param raf ������ ��� ������� � �����
	 */
	
	private void setSeekNextNodesInWay(ArrayList<Long> seek_nodes, RandomAccessFile raf) {
		// ���������� ����� �������� ��������� ����� � �����
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
	 * ���������� ��� �������� ����� �������� �������� ��������� �����.
	 * 
	 * @param seek �������� �����
	 * @param nextSeek �������� ��������� �����
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
	 * ���������� ��� �������� ����� �������� �������� ��������� �����.
	 * 
	 * @param seek �������� �����
	 * @param nextSeek �������� ��������� �����
	 * @param raf ������ ��� ������� � �����
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
	 * ��������� ������� ����� �������, ������� ������������� ������ �����
	 * (������� ��������� ��-�� � �������� �������� � ������������� ��� �����).
	 * 
	 * @param type ��� �������� ����� (�����)
	 * @param seek �������� � �����
	 * @param seekNextElement �������� ���������� �������� � �����
	 */
	
	public void setWay(byte type, long seek, long seekNextElement) {
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			// ��� ��������
			raf.seek(seek);
			raf.writeByte(type);			
			
			// �������� ���� ��������
			raf.seek(seek + Param.next_seek);
			raf.writeLong(seekNextElement);
			
			// �������� ��������
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
	 * ��������� (���������� �� �������) �������� ����� � ���������.
	 * 
	 * @param outerWaysArray ������ ��������������� �����, ������� ���������� �������������
	 */
	
	public void sortWays(ArrayList<ArrayList<Long>> outerWaysArray) {
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			for(int k = 0; k < outerWaysArray.size(); k++) {
				ArrayList<Long> ways_ids = outerWaysArray.get(k); // �������������� ����� �����
				ArrayList<Long> tmp_ways_ids = new ArrayList<Long>(); // �������������� ����� ����� � ��������������� �������
				
				 if(ways_ids.size() > 1) { 
					 tmp_ways_ids.add(ways_ids.get(0));
					 int size_tmp_ways = tmp_ways_ids.size();
					 
					 ArrayList<Long> nodes_ids_way = new ArrayList<>(); // �������������� ����� ����� �� �������� �����
					 ArrayList<Long> nodes_ids_way_tmp = new ArrayList<>(); // �������������� ����� ����� �� ������� ����� �������������� ������� �����
					 
					 int size = ways_ids.size();
					 
					 while(size_tmp_ways != size) {
						 boolean first_way_tmp = false; // ���� ����� ����� � ������ ����� ���������������� �������
						 
						 // ����� ������ ����� � ��������������� �������
						 nodes_ids_way_tmp.clear();
						 nodes_ids_way_tmp.addAll(getNodeIdsFromWay(tmp_ways_ids.get(0), raf));
						 
						 for(int i = 1; i < ways_ids.size(); i++) {
							 boolean first_first = false; // ���������� ������ ����� � ����� ����� � ������ ����� � ������ �����
							 boolean first_last = false; // ���������� ������ ����� � ����� ����� � ��������� ����� � ������ �����
							 boolean last_first = false; // ���������� ��������� ����� � ����� ����� � ������ ����� � ������ �����
							 boolean last_last = false; // ���������� ��������� ����� � ����� ����� � ��������� ����� � ������ �����
							 
							 // ���������� ����� �� ��������� �������
							 nodes_ids_way.clear();
							 nodes_ids_way.addAll(getNodeIdsFromWay(ways_ids.get(i), raf));
							 
							 // ������ - ������
							 first_first = compareCoordsNodes(nodes_ids_way_tmp.get(0),
									                          nodes_ids_way.get(0), raf);
							 
							 // ��������� - ���������
							 last_last = compareCoordsNodes(nodes_ids_way_tmp.get(nodes_ids_way_tmp.size() - 1),
                                                            nodes_ids_way.get(nodes_ids_way.size() - 1), raf);
							 
							 if(first_first || last_last) {
								 ArrayList<Long> tmp_ways = new ArrayList<>();
								 
								 // ������ ����������� �������� �����
								 for(int j = nodes_ids_way.size() - 1; j >= 0; j--)
									    tmp_ways.add(nodes_ids_way.get(j));
								
								
								 // �.�. ������ ����� ����� ��������, �� ������� ��� ����� �� ������ ���� �����,
								 // ������ ������������ �����, ������� ������������ ����� � ��������� �
								 // ������ ���������� ����� � �����
								 Param.seek_ways.remove(nodes_ids_way.get(0));
								 Param.seek_ways_used.remove(nodes_ids_way.get(0));
								 Param.num_points.remove(nodes_ids_way.get(0));
								 
								 nodes_ids_way.clear();
								 nodes_ids_way.addAll(tmp_ways);
								
								 // ��������� � ������ ���� �����, ������ ������������ �����,
								 // ������� ������������ ����� � ��������� � ������ ���������� ����� � �����
								 // ����� �����
								 Param.seek_ways.put(nodes_ids_way.get(0), Param.seek_nodes.get(nodes_ids_way.get(0)));
								 Param.seek_ways_used.put(nodes_ids_way.get(0), Param.seek_nodes.get(nodes_ids_way.get(0)));
								 Param.num_points.put(nodes_ids_way.get(0), nodes_ids_way.size());
								
								 // ��������� ����� ����� � ��������������� ������
								 tmp_ways_ids.add(0, nodes_ids_way.get(0));
								 size_tmp_ways = tmp_ways_ids.size();
								
								 // ��������� ������ �������� ����� � �����, �.�. ����������� ����� ����������
								 ArrayList<Long> seek_nodes_way = new ArrayList<>(); // �������� ����� � �����
								 
								 for(int j = 0; j < nodes_ids_way.size(); j++)
									 seek_nodes_way.add(Param.seek_nodes.get(nodes_ids_way.get(j)));
								
								 setSeekNextNodesInWay(seek_nodes_way, raf);
								 
								 ways_ids.remove(ways_ids.get(i));
								 
								 first_way_tmp = true;
								 break;
							 }
							 
							 // ������ - ���������
							 first_last = compareCoordsNodes(nodes_ids_way_tmp.get(0),
			                                                 nodes_ids_way.get(nodes_ids_way.size() - 1), raf);
							
							 // ��������� - ������
							 last_first = compareCoordsNodes(nodes_ids_way_tmp.get(nodes_ids_way_tmp.size() - 1),
                                                             nodes_ids_way.get(0), raf);
							 
							 if(first_last) {
								 // ��������� ����� � ��������������� ������
								 tmp_ways_ids.add(0, nodes_ids_way.get(0));
								 size_tmp_ways = tmp_ways_ids.size();
								 
								 ways_ids.remove(ways_ids.get(i));
								 
								 first_way_tmp = true;
								 break;
							 }
							 
							 if (last_first) {
								// ��������� ����� � ��������������� ������
								 tmp_ways_ids.add(nodes_ids_way.get(0));
								 size_tmp_ways = tmp_ways_ids.size();
								 
								 ways_ids.remove(ways_ids.get(i));
								 
								 first_way_tmp = true;
								 break;
							 }
						 }
						 
						 // �� ����� � ������ ����� ���������������� ������� ����� �����
						 if(!first_way_tmp) {
							 // ����� ��������� ����� � ��������������� �������
							 nodes_ids_way_tmp.clear();
							 nodes_ids_way_tmp.addAll(getNodeIdsFromWay(tmp_ways_ids.get(tmp_ways_ids.size() - 1), raf));
							 
							 for(int i = 1; i < ways_ids.size(); i++) {
								 boolean first_first = false; // ���������� ������ ����� � ����� ����� � ������ ����� � ������ �����
								 boolean first_last = false; // ���������� ������ ����� � ����� ����� � ��������� ����� � ������ �����
								 boolean last_first = false; // ���������� ��������� ����� � ����� ����� � ������ ����� � ������ �����
								 boolean last_last = false; // ���������� ��������� ����� � ����� ����� � ��������� ����� � ������ �����
								 
								 // ���������� ����� �� ��������� �������
								 nodes_ids_way.clear();
								 nodes_ids_way.addAll(getNodeIdsFromWay(ways_ids.get(i), raf));
								 
								 // ������ - ������
								 first_first = compareCoordsNodes(nodes_ids_way_tmp.get(0),
										                          nodes_ids_way.get(0), raf);
								 
								 // ��������� - ���������
								 last_last = compareCoordsNodes(nodes_ids_way_tmp.get(nodes_ids_way_tmp.size() - 1),
	                                                            nodes_ids_way.get(nodes_ids_way.size() - 1), raf);
								 
								 if(first_first || last_last) {
									 ArrayList<Long> tmp_ways = new ArrayList<>();
									 
									 // ������ ����������� �������� �����
									 for(int j = nodes_ids_way.size() - 1; j >= 0; j--)
										    tmp_ways.add(nodes_ids_way.get(j));
									
									
									 // �.�. ������ ����� ����� ��������, �� ������� ��� ����� �� ������ ���� �����,
									 // ������ ������������ �����, ������� ������������ ����� � ��������� �
									 // ������ ���������� ����� � �����
									 Param.seek_ways.remove(nodes_ids_way.get(0));
									 Param.seek_ways_used.remove(nodes_ids_way.get(0));
									 Param.num_points.remove(nodes_ids_way.get(0));
									 
									 nodes_ids_way.clear();
									 nodes_ids_way.addAll(tmp_ways);
									
									 // ��������� � ������ ���� �����, ������ ������������ �����,
									 // ������� ������������ ����� � ��������� � ������ ���������� ����� � �����
									 // ����� �����
									 Param.seek_ways.put(nodes_ids_way.get(0), Param.seek_nodes.get(nodes_ids_way.get(0)));
									 Param.seek_ways_used.put(nodes_ids_way.get(0), Param.seek_nodes.get(nodes_ids_way.get(0)));
									 Param.num_points.put(nodes_ids_way.get(0), nodes_ids_way.size());
									
									 // ��������� ����� ����� � ��������������� ������
									 tmp_ways_ids.add(nodes_ids_way.get(0));
									 size_tmp_ways = tmp_ways_ids.size();
									 
									 // ��������� ������ �������� ����� � �����, �.�. ����������� ����� ����������
									 ArrayList<Long> seek_nodes_way = new ArrayList<>(); // �������� ����� � �����
									 
									 for(int j = 0; j < nodes_ids_way.size(); j++)
										 seek_nodes_way.add(Param.seek_nodes.get(nodes_ids_way.get(j)));
									
									 setSeekNextNodesInWay(seek_nodes_way, raf);
									 
									 ways_ids.remove(ways_ids.get(i));
									 break;
								 }
								 
								 // ������ - ���������
								 first_last = compareCoordsNodes(nodes_ids_way_tmp.get(0),
				                                                 nodes_ids_way.get(nodes_ids_way.size() - 1), raf);
								 // ��������� - ������
								 last_first = compareCoordsNodes(nodes_ids_way_tmp.get(nodes_ids_way_tmp.size() - 1),
	                                                             nodes_ids_way.get(0), raf);
								 
								 if(first_last || last_first) {
									 // ��������� ����� � ��������������� ������
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
			
			// �������������� ��������
			for(int k = 0; k < outerWaysArray.size(); k++) {
				ArrayList<Long> ways_ids = outerWaysArray.get(k); // �������������� ����� �����
				
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
	 * ���������� ���������� ���� �����.
	 * 
	 * @param nodeId_1 ������������� ������ �����
	 * @param nodeId_2 ������������� ������ ����� 
	 * @param raf ������ ��� ������� � �����
	 * @return ���� �����, ���������� true, ����� false
	 */
	
	private boolean compareCoordsNodes(long nodeId_1, long nodeId_2, RandomAccessFile raf) {
		double [] coords_node_1 = new double [2]; // ������ � ������� ����� ������ �����
		double [] coords_node_2 = new double [2]; // ������ � ������� ����� ������ �����
		
		try {
			// ������
			raf.seek(Param.seek_nodes.get(nodeId_1) + Param.lat_seek);
			coords_node_1[0] = raf.readDouble();
			// �������
			raf.seek(Param.seek_nodes.get(nodeId_1) + Param.lon_seek);
			coords_node_1[1] = raf.readDouble();
		
			// ������
			raf.seek(Param.seek_nodes.get(nodeId_2) + Param.lat_seek);
			coords_node_2[0] = raf.readDouble();
			// �������
			raf.seek(Param.seek_nodes.get(nodeId_2) + Param.lon_seek);
			coords_node_2[1] = raf.readDouble();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return (coords_node_1[0] == coords_node_2[0]) && (coords_node_1[1] == coords_node_2[1]) ? true : false;
	}
	
	/**
	 * ���������� ������ ����� ����� map.dnvg.
	 * 
	 * @return ������ ����� �����
	 */
	
	public long getLengthFile() throws IOException {	
		raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
		
		long seek = raf.length();	
		
		raf.close();
		
		return seek;
	}
	
	/**
	 * ���������� ������ ���� ������ ����� (map.dnvg, map.hnvg � index_map).
	 * 
	 * @return ������ ���� ������ �����
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
	 * ��������� �������������� ����������� �������� � ��������.
	 * 
	 * @param outerBounds ������� �������
	 * @param innerBounds ���������� �������
	 * @return ���� ���������� ������� ����������� �������� �������� - true, 
	 * ����� - false
	 */
	
	private boolean isContains(JPolygon outerBounds, JPolygon innerBounds) {
		// ���������� ����� ��� ���������� ���� � ������ ������ ����������� ��������
		double X = 10000;
		double Y = 10000;
		double EPS = 1e-9; // ���������� ����� �������� (��� ����)		
		
		boolean contains = true; // ������� �������������� ����������� �������� ��������
		// ������� ����, ��� ����� ����������� �������� �� ��������� ����� �����. ���� ����� ��������,
		// �� ��� ����� ����� ������������ ��� ���� ������ � ����������� ��� ����� ������� cnt �� 2, 
		// ���� �� ���� ����������� ����. 
		boolean isAgain = false; 
		
		for(int i = 0; i < innerBounds.n_points; i++) {		
			int cnt = 0; // ������� ����������� ����� �� ����� ����������� �������� �� ����� ��������� �������� ��������
						
			// ������� ������������ ������ ��� ����� ����������� �������� � ����� ����
			double inner_a = Y - innerBounds.y_points[i]; 
//			inner_a = round7(inner_a);
			double inner_b = innerBounds.x_points[i] - X;
//			inner_b = round7(inner_b);
			double inner_c = (innerBounds.x_points[i] * (innerBounds.y_points[i] - Y)) + 
					         (innerBounds.y_points[i] * (X - innerBounds.x_points[i]));
//			inner_c = round7(inner_c);
			
			for(int j = 0; j < outerBounds.n_points; j++) {				
				// ������� ������������ ������ ��� ����� �� ������ �������� ��������
				int next = j + 1; // ��������� ����� � ��������
				
				if(j == outerBounds.n_points - 1)
					next = 0;
					
				double outer_a = outerBounds.y_points[next] - outerBounds.y_points[j]; 
//				outer_a = round7(outer_a);
				double outer_b = outerBounds.x_points[j] - outerBounds.x_points[next];
//				outer_b = round7(outer_b);
				double outer_c = (outerBounds.x_points[j] * (outerBounds.y_points[j] - outerBounds.y_points[next])) + 
						         (outerBounds.y_points[j] * (outerBounds.x_points[next] - outerBounds.x_points[j]));
//				outer_c = round7(outer_c);
				
				// ������� ����� ����������� ���� ������
				double znamenatel = (inner_a * outer_b) - (outer_a * inner_b);
//				znamenatel = round7(znamenatel);
				
				// ���� ����������� ����� ���� ��� ���������� � ����, �� ������ �� ������������
				if(!(Math.abs(znamenatel) < EPS)) {
					double chislitel_x = (inner_c * outer_b) - (outer_c * inner_b);
					double chislitel_y = (inner_a * outer_c) - (outer_a * inner_c);
					
					// ����� �����������
					double res_x = (- (chislitel_x / znamenatel));
					double res_y = (- (chislitel_y / znamenatel));
					res_x = round7(res_x);
					res_y = round7(res_y);
					
					// ���������� ����� ������� �������� ��������
					double x_min = outerBounds.x_points[j];
					double x_max = outerBounds.x_points[next];
					double y_min = outerBounds.y_points[j];
					double y_max = outerBounds.y_points[next]; 
					
					// ��������� �������, ����� ���������� ������ ����� � ������� ���� ������
					// ��������� ������ ����� �������
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
					
					// ������������ ������ ������ ���������
					if(((innerBounds.x_points[i] <= res_x) && (X >= res_x) && 
						(x_min <= res_x) && (x_max >= res_x)) && 
					   ((innerBounds.y_points[i] <= res_y) && (Y >= res_y) && 
					    (y_min <= res_y) && (y_max >= res_y))) {
						// ��������� � ������� ������ �����
						if(res_x == outerBounds.x_points[j] && res_y == outerBounds.y_points[j] && (cnt % 2 != 0))
							isAgain = true;
						else
							isAgain = false;
						
						if(!isAgain)
							cnt++;
					}
				}
			}
			
			// ��� ������ ���-�� ����������� ����� ��������� ������� ��������, ��� �������� - ������
			if(cnt % 2 == 0) 
				contains = false;
		}
		
		return contains;
	}
	
	/**
	 * ���������� ��� ������� �������� � ���������, ���� �� � ��� ����������
	 * �����. ���� ����, �� ������� ��.
	 */
	
	public void deleteDublicatNodesInWays() {
		try {
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			long nodesCount = 0; // ������� ����� ��� ��������	
			byte type = 0;
			
			OsmConverter.printLog("������� �����-��������� �� ����� ����� ...");
			
			if(Param.seek_ways != null && Param.seek_ways.size() != 0) {
				Collection<Long> ways_ids = Param.seek_ways.values();
				Iterator<Long> iterator_ways = ways_ids.iterator();
			
				// ���������� �����
				while(iterator_ways.hasNext()) {
					long tmp_seek = iterator_ways.next();
					if (tmp_seek == 6111618) {
						int y = 0;
					}
					raf.seek(tmp_seek);
					type = raf.readByte();
					
					raf.seek(tmp_seek + Param.delete_seek);
					byte del = raf.readByte();
				
					// ������� ����� � �����
					if(type == Param.poligon_outer && del != 0x01) {
						deleteDublicatNodes(raf.readLong(), raf);
						nodesCount++;
					}
				}
			}
			
			OsmConverter.printLog("���������� �����-���������� ��� ��������: " + nodesCount);
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
	 * ������� ��� ������� �� ����� � ������� ���������, ��� ������� ������������� �
	 * ���������� ��� ���� ������� �� ����� (����� �� ���� ���������� ��������). 
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
	 * ���������� �������� ���� ����� ����� �����.
	 * 
	 * @param raf ������ ��� ������� � �����
	 * @return ������ �������� ���� ����� ����� �����
	 */
	
	public ArrayList<Long> getAllPoints(RandomAccessFile raf) {
		ArrayList<Long> nodeSeeks = new ArrayList<>();
		long seek = Param.dnvg_headerSize;	
		
		try {
			// ���� ��� ����� � ����� �����
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
	 * ���������� �������� ���� ����� ����� �����, ������� ����� ������� ��������.
	 * 
	 * @param raf ������ ��� ������� � �����
	 */
	
	public ArrayList<Long> getAll() {
		ArrayList<Long> nodeSeeks = new ArrayList<>();
		long seek = Param.dnvg_headerSize;	
		
		try {
			// ���� ��� ����� � ����� �����
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
	 * ������� �������� ������ �������� �����.
	 * 
	 * @param bytes ����� ������� �������
	 * @return ������ ������ ������ ���������� ������ 
	 */
	
	private byte[] setEmptyBytes(int bytes) {
		byte[] data = new byte[bytes];
		
		for(int i = 0; i < bytes; i++) {
			data[i] = 0x00;
		}
		
		return data;
	}
	
	/**
	 * ������� � ���������� � ����� ����� ������� ����������� �����.
	 * 
	 * @param isIndexFile ������� ����, ��� ��������� ��������� ����. ���� 
	 * TRUE, �� ���� ��������� � ������� ������������ ���������� ������ ��������� 
	 * ��������, ���� FALSE, �� � ���� ������ ������������ ����� ������ ��������
	 * ���������� ������.
	 */
	
	public void createCRC32(boolean isIndexFile) {
		OsmConverter.printLog("������� ����������� ����� ... ");
		
		// ������� ����� (����� ������) ��� �������� ���������� ����������� �����
		// ���������� �� �������� ����� ����� � ������� ������
		// map.dnvg � map.hnvg		
		try {
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			i_raf = new RandomAccessFile(Param.tree_path, "rw");
		
			// �������� ����� �����
			String mapName = Param.mapName;
		
			// ������ ������		
			String strDnvgSize = Long.toHexString(raf.length());						
			String strHnvgSize = Long.toHexString(h_raf.length());
			String strIndexSize = Long.toHexString(i_raf.length());
			
			// ���������� ��� ��������� ���������� � �������� ������ � ������
			String finalStr = mapName.concat(strDnvgSize).concat(strHnvgSize).concat(strIndexSize);
							
			byte [] word = finalStr.getBytes();
			
			CRC32 crc32 = new CRC32();
			crc32.update(word);
			
			long result = crc32.getValue();
			
			// ���������� ����������� ����� � ����� �����
			raf.seek(Param.crc32_seek);
			raf.writeLong(result);
			
			h_raf.seek(Param.crc32_seek);
			h_raf.writeLong(result);
			
			if (!isIndexFile) {
				i_raf.seek(Param.crc32_seek);
				i_raf.writeLong(result);
			} else {
				 try {
					 // ��������������
					 FileInputStream file_in = new FileInputStream(Param.tree_path);
					 ObjectInputStream object_in = new ObjectInputStream(file_in);
					 
					 RTrees trees = (RTrees) object_in.readObject();
					 trees.setCRC(result);
					 
					 file_in.close();
					 object_in.close();
					 
					 // �������������
					 FileOutputStream file_out = new FileOutputStream(Param.tree_path);
					 ObjectOutputStream	object_out = new ObjectOutputStream(file_out);
					
					 object_out.writeObject(trees);
					
					 object_out.flush();
					 object_out.close();
					 file_out.close();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			
			OsmConverter.printLog("����������� ����� " + Long.toHexString(result) + " ������� � �������� � �����.");		
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
	 * ��������� �� ����� �������� ����������� ����� � ���������� ��.
	 * @return �������� ����������� �����.
	 */
	public long getCRC32fromFile() {
		long crc = 0;
		
		try {
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			
			long seek = Param.crc32_seek;
			
			h_raf.seek(seek);
			crc = h_raf.readLong();
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
		
		return crc;
	}
	
	/**
	 * ����������� �� ����� ����� �������� ����� ����� �������.
	 * 
	 * @param val �������������� �����
	 * @return ����� � ������ ����� �������� ����� ����� �������
	 */
	
	public static double round7(double val) {
		double tmp = val * 10000000;
		tmp = Math.round(tmp);
		tmp = tmp / 10000000;
		
		return tmp;
	}
}
