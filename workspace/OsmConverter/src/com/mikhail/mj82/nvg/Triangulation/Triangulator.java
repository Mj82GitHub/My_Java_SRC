/*
 * Copyright (c) 01.2018
 */

package com.mikhail.mj82.nvg.Triangulation;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import com.mikhail.mj82.nvg.Converter.Param;
import com.mikhail.mj82.nvg.Converter.RndAccessFile;

/**
 *  ласс дл€ триангул€ции полигонов карты.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class Triangulator {

	private RandomAccessFile h_raf; // «аписывает и считывает данные в/из файла
	private RandomAccessFile raf; // «аписывает и считывает данные в/из файла
	private RndAccessFile my_raf = new RndAccessFile(); // «аписывает и считывает данные в/из файла
	int count = 0;
	// “риангулирует полигон
	private Triangulation triangulation = new Triangulation();
	
	double [][] triangles = null; // ћассив треугольников
	
	/**
	 * “риангулирует полигоны считыва€ данные из файла карты.
	 */
	
	public void makeTriangulation() {
		try {
			h_raf = new RandomAccessFile(Param.hnvg_file_path, "rw");
			raf = new RandomAccessFile(Param.dnvg_file_path, "rw");
			
			long seek = Param.dnvg_headerSize;
			
			while(!(seek >= raf.length())) {
				raf.seek(seek);
				byte type = raf.readByte();
				
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

						// —писок смещений всех точек полигона (и внешнего и внутреннего(их), если есть)
						ArrayList<Long> allSeeks = new ArrayList<>();
						
						// ƒобавл€ем в список смещени€ точек внешнего полигона
						allSeeks.addAll(my_raf.getSeekAllPointsInWay(id, raf));
						//  оординаты внешнего полигона
						double [] array_outers = my_raf.getArrayCoords(id);
						// »дентификаторы внутренних полигонов
						ArrayList<Long> inners = Param.ids_outer_ways_with_inner_ways.get(id);
						
						// ”дал€ем из списка линий файла внешний полигон
						Param.new_seek_only_ways.remove(id);
						Param.num_points.remove(id);
						
						if(inners != null) {
							//  оординаты каждого из ннутренних полигонов по отдельности
							double [][] array_inners = new double[inners.size()][];
							
							for(int i = 0; i < array_inners.length; i++) {
								array_inners[i] = my_raf.getArrayCoords(inners.get(i));
								
								long innerAttrSeek = my_raf.getArrtSeek(Param.new_seek_nodes.get((inners.get(i))));
								
								// ”дал€ем аргументы внутренних полигонов, т.к. внутренние
								// полигоны сольютс€ с внешними
								if(innerAttrSeek > 0) {
									int index = Param.delete_attrs.indexOf(innerAttrSeek);
									
									if(index == -1)
										Param.delete_attrs.add(innerAttrSeek);
								}
								
								// ƒобавл€ем в список смещени€ точек внутреннего полигона
								allSeeks.addAll(my_raf.getSeekAllPointsInWay(inners.get(i), raf));
								
								Param.num_points.remove(inners.get(i));
							}
							
							tri_circle(array_outers, array_inners);
							
							// «аписываем треугольники в файл карты
							my_raf.createNewTrianglePoligon(triangles, allSeeks, array_outers, first);
						} else {
							tri_circle(array_outers, null);
							
							// «аписываем треугольники в файл карты
							my_raf.createNewTrianglePoligon(triangles, allSeeks, array_outers, first);
						}
						
						//≈сли после триангул€ции по€вились новые внешние полигоны, то записываем их в файл карты
						if(Triangulation.outerPolysAfterUnionInnersPolys.length() > 0) {
							Triangulation.outerPolysAfterUnionInnersPolys.first();
							
							for(int i = 0; i < Triangulation.outerPolysAfterUnionInnersPolys.length(); i++) {
								double[] coords = toArray(Triangulation.outerPolysAfterUnionInnersPolys.val());
								
								long newWaySeek = my_raf.createNewWay(coords, seek, raf);
								
								// «апишем в атрибут новой линии смещение первой точки
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
	 * “риангулирует полигон в цикле дл€ того, чтобы при не корректных
	 * координатах ребра рассечени€ при объединении внешнего полигона с
	 * внутренним(и) можно было изменить параметр DELTA в классе Triangulation.
	 * 
	 * @param outer массив точек с координатами в формате (x, y), из которых состоит полигон
	 * @param inners массив массивов точек с координатами в формате (x, y) полигонов, которые 
	 * располагаютс€ внутри полигона, заданного в первом аргументе
	 */
	
	private void tri_circle(double [] outer, double [][] inners) {
		boolean isStop = false; // ќстанавливает цикл
		triangles = null; // ћассив треугольников
//		count++;
//		System.out.println("COUNT: " + count);
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
	 * ¬озвращает массив координат вершин полигона в формате x1,y1, ... xN, yN.
	 * 
	 * @param p полигон
	 * @return массив координат вершин полигона
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


