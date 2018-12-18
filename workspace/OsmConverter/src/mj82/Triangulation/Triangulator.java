/*
 * Copyright (c) 01.2018
 */

package mj82.Triangulation;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import mj82.Converter.Param;
import mj82.Converter.RndAccessFile;

/**
 * ����� ��� ������������ ��������� �����.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class Triangulator {

	private RandomAccessFile h_raf; // ���������� � ��������� ������ �/�� �����
	private RandomAccessFile raf; // ���������� � ��������� ������ �/�� �����
	private RndAccessFile my_raf = new RndAccessFile(); // ���������� � ��������� ������ �/�� �����
	int count = 0;
	// ������������� �������
	private Triangulation triangulation = new Triangulation();
	
	double [][] triangles = null; // ������ �������������
	
	/**
	 * ������������� �������� �������� ������ �� ����� �����.
	 */
	
	public void makeTriangulation() {
		try {
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			long seek = Param.dnvg_headerSize;
			
			while(!(seek >= raf.length())) {
				raf.seek(seek);
				byte type = raf.readByte();
				
				if(seek == 2532088) {
					int y = 0;
				}
				if(type == Param.poligon_outer) {
					raf.seek(seek + Param.myself_seek);
					long myself = raf.readLong();
					
					raf.seek(seek + Param.attr_seek);
					long attrSeek = raf.readLong();
					
					h_raf.seek(attrSeek + Param.firsPointInWay_seek);
					long first = h_raf.readLong();
					
					if(myself == first) {
						raf.seek(seek + Param.id_seek);
						long id = raf.readLong();
//						System.out.println("SEEK: " + seek);
						// ������ �������� ���� ����� �������� (� �������� � �����������(��), ���� ����)
						ArrayList<Long> allSeeks = new ArrayList<>();
						
						// ��������� � ������ �������� ����� �������� ��������
						allSeeks.addAll(my_raf.getSeekAllPointsInWay(id, raf));
						// ���������� �������� ��������
						double [] array_outers = my_raf.getArrayCoords(id);
						// �������������� ���������� ���������
						ArrayList<Long> inners = Param.ids_outer_ways_with_inner_ways.get(id);
						
						// ������� �� ������ ����� ����� ������� �������
						Param.new_seek_only_ways.remove(id);
						Param.num_points.remove(id);
						
						if(inners != null) {
							// ���������� ������� �� ���������� ��������� �� �����������
							double [][] array_inners = new double[inners.size()][];
							
							for(int i = 0; i < array_inners.length; i++) {
								array_inners[i] = my_raf.getArrayCoords(inners.get(i));
								
								long innerAttrSeek = my_raf.getArrtSeek(Param.new_seek_nodes.get((inners.get(i))));
								
								// ������� ��������� ���������� ���������, �.�. ����������
								// �������� �������� � ��������
								if(innerAttrSeek > 0) {
									int index = Param.delete_attrs.indexOf(innerAttrSeek);
									
									if(index == -1)
										Param.delete_attrs.add(innerAttrSeek);
								}
								
								// ��������� � ������ �������� ����� ����������� ��������
								allSeeks.addAll(my_raf.getSeekAllPointsInWay(inners.get(i), raf));
								
								Param.num_points.remove(inners.get(i));
							}
							
							tri_circle(array_outers, array_inners);
							
							// ���������� ������������ � ���� �����
							my_raf.createNewTrianglePoligon(triangles, allSeeks, array_outers, first);
						} else {
							tri_circle(array_outers, null);
							
							// ���������� ������������ � ���� �����
							my_raf.createNewTrianglePoligon(triangles, allSeeks, array_outers, first);
						}
						
						//���� ����� ������������ ��������� ����� ������� ��������, �� ���������� �� � ���� �����
						if(Triangulation.outerPolysAfterUnionInnersPolys.length() > 0) {
							Triangulation.outerPolysAfterUnionInnersPolys.first();
							
							for(int i = 0; i < Triangulation.outerPolysAfterUnionInnersPolys.length(); i++) {
								double[] coords = toArray(Triangulation.outerPolysAfterUnionInnersPolys.val());
								
								long newWaySeek = my_raf.createNewWay(coords, seek, raf);
								
								// ������� � ������� ����� ����� �������� ������ �����
								raf.seek(newWaySeek + Param.attr_seek);
								long newWayAttrSeek = raf.readLong();
								
								h_raf.seek(newWayAttrSeek + Param.firsPointInWay_seek);
								h_raf.writeLong(newWaySeek);
								
								Triangulation.outerPolysAfterUnionInnersPolys.next();
							}
							
							Triangulation.outerPolysAfterUnionInnersPolys.delete_list();
						}
						
						seek += Param.elementSize;
					} else {
						seek += Param.elementSize;
					}
				} else {
					seek += Param.elementSize;
				}
			}
			System.out.println("ERROR COUNT: " + count);
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
	 * ������������� ������� � ����� ��� ����, ����� ��� �� ����������
	 * ����������� ����� ���������� ��� ����������� �������� �������� �
	 * ����������(�) ����� ���� �������� �������� DELTA � ������ Triangulation.
	 * 
	 * @param outer ������ ����� � ������������ � ������� (x, y), �� ������� ������� �������
	 * @param inners ������ �������� ����� � ������������ � ������� (x, y) ���������, ������� 
	 * ������������� ������ ��������, ��������� � ������ ���������
	 */
	
	private void tri_circle(double [] outer, double [][] inners) {
		boolean isStop = false; // ������������� ����
		triangles = null; // ������ �������������
//		count++;
//		System.out.println("COUNT: " + count);
		if(count == 51) {
			int y = 0;
		}
		while(!isStop) {
			try {
				triangles = triangulation.triangulation(outer, inners);
			} catch(Exception e) {
				if(inners != null) {
					Triangulation.delta *= 10;
					
					continue;
				} else {
					count++;
					System.out.println("" + e.toString());
				}
			}
			
			isStop = true;
			Triangulation.delta = 0.0000001;
		}		
	}
	
	/**
	 * ���������� ������ ��������� ������ �������� � ������� x1,y1, ... xN, yN.
	 * 
	 * @param p �������
	 * @return ������ ��������� ������ ��������
	 */
	
	private double[] toArray(Mj_Polygon p) {
		double [] array = new double[p.size * 2];
 		
		for(int i = 0, j = 0; i < p.size; i++, j +=2, p.advance()) {
			array[j] = p.getVertex().point().x;
			array[j + 1] = p.getVertex().point().y;
		}
		
		return array;
	}
}


