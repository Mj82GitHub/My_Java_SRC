/*
 * Copyright (c) 01.2018
 */

package com.mikhail.mj82.nvg.Triangulation;

import java.util.ArrayList;
import java.util.HashMap;

import com.mikhail.mj82.nvg.Triangulation.Decompositor.Scaning;
import com.mikhail.mj82.nvg.Triangulation.Edge.Intersect;
import com.mikhail.mj82.nvg.Triangulation.Mj_Point.Point_position;
import com.mikhail.mj82.nvg.Triangulation.Mj_Vertex.Rotation;

/**
 * Класс для триангуляции любых по форме полигонов (включая полигоны, содержащие внутренние области).
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class Triangulation {
	
	//При проекции Меркатора на сферу
	public static final double R = 6378137.0000000; // Радиус земли

	//При расщеплении полигона, чтобы небыло двух одинаковых ребер, при сравнении, одно из них 
	// смещаем на эту величину
	public static double delta = 0.00001; 
	// Габаритный параметр для рассчета стороны треугольника при триангуляции
	public static double h = 1;
	
	// Оригинальные координаты до добавления delta и поворота при декомпозиции
	public static HashMap<Double, Double> org_coords = new HashMap<Double, Double>();
	// Используется при объединении внутреннего полигона и внешнего
	// Хранит точки ребра разрезания
	public static ArrayList<Mj_Vertex> useOuterVertexes; 
	
	private Decompositor decompositor = new Decompositor(); // Разбивает полигон на монотонные
	private Mj_Polygon outer_polygon; // Внешний полигон до декомпозиции на монотонные полигоны
	private Mj_List<Mj_Polygon> monotone_polygons; // Список монотонных полигонов после декомпозиции внешнего полигона
	// Список треугольников после триангуляции монотонных полигонов
	private Mj_List<Mj_Polygon> triangles = new Mj_List<Mj_Polygon>(); 
	
	// После объединения внутренних полигонов с одинаковыми сторонами могут возникать
	// дополнительные внешние полигоны
	public static Mj_List<Mj_Polygon> outerPolysAfterUnionInnersPolys = new Mj_List<Mj_Polygon>();

	int count = 0;
	
	/**
	 * Триангулирует заданный полигон.
	 * 
	 * @param P полигон для триангуляции
	 * @param h параметр, задающий размер стороны треугольника
	 * @return список треугольников
	 */
	
	private Mj_List<Mj_Polygon> contri_poly(Mj_Polygon P, double h) {
		// Избавляемся от лишних точек, лежащих на одной прямой
		P = deleteMiddlePoints(P);
		
		short cnt = 0; // Счетчик самопересеченй и измнения направления обхода полигона
		short cnt_setting  = 1; // Уставка счетчика count
		// Список треугольников, которые имеют самопересечения или изменяют направление обхода полигона
		Mj_List<Mj_Polygon> tmp_triangles = new Mj_List<>();
		// Копия заданного полигона
		Mj_Polygon copy_P = new Mj_Polygon(P);
		
		Mj_List<Mj_Polygon> LT = new Mj_List<Mj_Polygon>(); // Итоговый список треугольников
		int D = getIntDirectionOfPolygon(P); // Определяет направление обхода полигона
		
		while(true) {	
			cnt = 0;
			
			Mj_Polygon triangle = null; 
			Mj_Polygon Q = new Mj_Polygon(P);
			int n = P.size();
			// Угол между двумя векторами не должен быть больше 180 градусов
			double KCI = 180.0;  
			
			for(int i = 1; i <= n; i++) {
				Mj_Vertex q1 = Q.getVertex();
				Mj_Vertex q2 = (Mj_Vertex) Q.getVertex().next();
				Mj_Vertex qn = (Mj_Vertex) Q.getVertex().prev();
				
				triangle = new Mj_Polygon();
				triangle.insert(q1.point());
				triangle.insert(q2.point());
				triangle.insert(qn.point());
				
				// Алгебраический угол м/д двумя векторами
				double FI = ang(qn.point().subtraction(q1.point()), q2.point().subtraction(q1.point()));
				
				// Определяем направление обхода треугольника (у внешнего угла знак отличается)
				// и сохраняем наименьший угол учитывая направление обхода самого полигона
				if(D > 0) {
					if((getIntDirectionOfPolygon(triangle) * FI) >= 0.0 && Math.abs(FI) < KCI) {
						if(!isContainPolygonInList(tmp_triangles, triangle) && Q.size > 3) {
							KCI = Math.abs(FI);
							P = new Mj_Polygon(Q);
						}
						
						if(!isContainPolygonInList(tmp_triangles, triangle) && Q.size == 3) {
							KCI = Math.abs(FI);
						}
					}
				} else {
					if((getIntDirectionOfPolygon(triangle) * FI) < 0.0 && Math.abs(FI) < KCI) {
						if(!isContainPolygonInList(tmp_triangles, triangle) && Q.size > 3) {
							KCI = Math.abs(FI);
							P = new Mj_Polygon(Q);
						}
						
						if(!isContainPolygonInList(tmp_triangles, triangle) && Q.size == 3) {
							KCI = Math.abs(FI);
						}
					}
				}
				
				Q = LCShift(Q);
			}
			
			// Если не удалась триангуляция, то изменяем настройки и пробуем снова
			if(KCI == 180 && Q.size > 3) {
				P = new Mj_Polygon(copy_P);
				LT.delete_list();
				tmp_triangles.delete_list();
				cnt_setting++;
				continue;
			}
			
			Mj_Vertex p1 = P.getVertex();
			Mj_Vertex p2 = (Mj_Vertex) P.getVertex().next();
			Mj_Vertex pn = (Mj_Vertex) P.getVertex().prev();
			
			Mj_Polygon tmp_triangle = new Mj_Polygon();
			tmp_triangle.insert(p2.point());
			tmp_triangle.insert(pn.point());
			tmp_triangle.insert(p1.point());
			
			tmp_triangles.append(tmp_triangle);
			
			// Векторы сторон полигона
			Mj_Point V = pn.point().subtraction(p1.point());
			V.x = Decompositor.round7(V.x);
			V.y = Decompositor.round7(V.y);
			
			Mj_Point W = p2.point().subtraction(p1.point());
			W.x = Decompositor.round7(W.x);
			W.y = Decompositor.round7(W.y);
			
			// Расчет целых кратностей длин сторон к числу h (округленный по верхнему целому)
			int Kv = roundUp(V.length() / h);
			int Kw = roundUp(W.length() / h);
			
			// Расчет целой кратности угла KCI к углу в 60 градусов (округленный по верхнему целому)
			int K = roundUp(KCI / 60.0);
			
			// Делаем копии полигона и списка треугольников
			Q = new Mj_Polygon(P);
			Mj_List<Mj_Polygon> QT = LT.copyList(LT);		
			
			do {
				// Вычисление векторов не превышающих значения h
				Mj_Point v = V.multiplication(1.0 / Kv);
				Mj_Point w = W.multiplication(1.0 / Kw); 
				
				// Вычисление вершин треугольников, лежащих на смежных с p1 ребрах
				Mj_Vertex a = new Mj_Vertex(p1.point().sum(v));
				a.point().x = Decompositor.round7(a.point().x);
				a.point().y = Decompositor.round7(a.point().y);
				
				Mj_Vertex b = new Mj_Vertex(p1.point().sum(w));
				b.point().x = Decompositor.round7(b.point().x);
				b.point().y = Decompositor.round7(b.point().y);
				
				// Список некратных вершин полилинии, проходящей м/д точками b и a с учетом
				// возможного совпадения точек b = p2 и a = pn
				Mj_ListVertex Lp = new Mj_ListVertex(); 
				
				for(int i = 1; i < P.size; i++)			
					Lp.append(P.advance());
				
				if(Kw != 1)
					Lp.prepend(b);
				else
						b = p2;
					
				if(Kv != 1)
					Lp.append(a);
				else
					a = pn;
				
				// Отрезок м/д точками
				Mj_Point ab = a.point().subtraction(b.point());
				ab.x = Decompositor.round7(ab.x);
				ab.y = Decompositor.round7(ab.y);
				
				// В зависимости от длины отрезка ab и коэффициента K возможны три варианта 
				// отрезания треугольников и формирования списка вершин нового полигона P
				if(ab.length() <= h || K == 1) {
					Mj_Polygon tmp_p = new Mj_Polygon();
					tmp_p.insert(new Mj_Point(Decompositor.round7(p1.point().x), 
							                  Decompositor.round7(p1.point().y)));
					tmp_p.insert(new Mj_Point(Decompositor.round7(b.point().x), 
							                  Decompositor.round7(b.point().y)));
					tmp_p.insert(new Mj_Point(Decompositor.round7(a.point().x), 
							                  Decompositor.round7(a.point().y)));
				
					LT.append(tmp_p);
					
//					System.out.println(" *** Triangles " + ++count);
					
					Lp.last();
					Lp.remove();
					Lp.prepend(a);
					
					P = new Mj_Polygon(Lp.first());
					
					if(P.size < 3)
						return LT;
				} else if(K == 2) {
					double rc = ((1.0 - (1.0 / 2.0)) * v.length()) + ((1.0 / 2.0) * w.length());
					Mj_Point Nv = v.multiplication(1.0 / v.length());
					Mj_Point R = rotateMatrix(-(0.5 * D * KCI), Nv);
					
					Mj_Point u = R.multiplication(rc);
					Mj_Vertex c = new Mj_Vertex(p1.point().sum(u));
					
					Mj_Polygon tmp_p1 = new Mj_Polygon();
					tmp_p1.insert(new Mj_Point(Decompositor.round7(p1.point().x), 
							                   Decompositor.round7(p1.point().y)));
					tmp_p1.insert(new Mj_Point(Decompositor.round7(c.point().x), 
							                   Decompositor.round7(c.point().y)));
					tmp_p1.insert(new Mj_Point(Decompositor.round7(a.point().x), 
							                   Decompositor.round7(a.point().y)));
					
					Mj_Polygon tmp_p2 = new Mj_Polygon();
					tmp_p2.insert(new Mj_Point(Decompositor.round7(p1.point().x), 
							                   Decompositor.round7(p1.point().y)));
					tmp_p2.insert(new Mj_Point(Decompositor.round7(b.point().x), 
							                   Decompositor.round7(b.point().y)));
					tmp_p2.insert(new Mj_Point(Decompositor.round7(c.point().x), 
							                   Decompositor.round7(c.point().y)));
					
					LT.append(tmp_p1);
					LT.append(tmp_p2);
					
//					System.out.println(" *** Triangles " + ++count);
//					System.out.println(" *** Triangles " + ++count);
					
					Lp.last();
					Lp.remove();
					Lp.prepend(c);
					Lp.prepend(a);
					
					P = new Mj_Polygon(Lp.first());
				} else if(K == 3) {						
					double rd = ((1.0 - (1.0 / 3.0)) * v.length()) + ((1.0 / 3.0) * w.length());
					double re = ((1.0 - (2.0 / 3.0)) * v.length()) + ((2.0 / 3.0) * w.length());
					
					Mj_Point Nv = v.multiplication(1.0 / v.length());
					
					Mj_Point Rd = rotateMatrix(-((1.0 / 3.0) * D * KCI), Nv);
					Mj_Point Re = rotateMatrix(-((2.0 / 3.0) * D * KCI), Nv);
					
					Mj_Point ud = Rd.multiplication(rd);
					Mj_Point ue = Re.multiplication(re);
					
					Mj_Vertex d = new Mj_Vertex(p1.point().sum(ud));
					Mj_Vertex e = new Mj_Vertex(p1.point().sum(ue));
					
					Mj_Polygon tmp_p1 = new Mj_Polygon();
					tmp_p1.insert(new Mj_Point(Decompositor.round7(p1.point().x), 
							                   Decompositor.round7(p1.point().y)));
					tmp_p1.insert(new Mj_Point(Decompositor.round7(d.point().x), 
							                   Decompositor.round7(d.point().y)));
					tmp_p1.insert(new Mj_Point(Decompositor.round7(a.point().x), 
							                   Decompositor.round7(a.point().y)));
					
					Mj_Polygon tmp_p2 = new Mj_Polygon();
					tmp_p2.insert(new Mj_Point(Decompositor.round7(p1.point().x), 
							                   Decompositor.round7(p1.point().y)));
					tmp_p2.insert(new Mj_Point(Decompositor.round7(e.point().x), 
							                   Decompositor.round7(e.point().y)));
					tmp_p2.insert(new Mj_Point(Decompositor.round7(d.point().x), 
							                   Decompositor.round7(d.point().y)));
					
					Mj_Polygon tmp_p3 = new Mj_Polygon();
					tmp_p3.insert(new Mj_Point(Decompositor.round7(p1.point().x), 
							                   Decompositor.round7(p1.point().y)));
					tmp_p3.insert(new Mj_Point(Decompositor.round7(b.point().x), 
							                   Decompositor.round7(b.point().y)));
					tmp_p3.insert(new Mj_Point(Decompositor.round7(e.point().x), 
							                   Decompositor.round7(e.point().y)));
					
					LT.append(tmp_p1);
					LT.append(tmp_p2);
					LT.append(tmp_p3);
					
//					System.out.println(" *** Triangles " + ++count);
//					System.out.println(" *** Triangles " + ++count);
//					System.out.println(" *** Triangles " + ++count);
					
					Lp.last();
					Lp.remove();
					Lp.prepend(e);
					Lp.prepend(d);
					Lp.prepend(a);
					
					P = new Mj_Polygon(Lp.first());
				}
				
				// Проверяем самопересечение полигона и направление обхода 
				// получившегося полигона (при самопересечение направление меняется)
				if((self_test(P) || D != getIntDirectionOfPolygon(P)) && cnt < cnt_setting) {
					Kv++;
					Kw++;
					cnt++;
					
					// Восстанавливаем полигон и список треугольников
					P = new Mj_Polygon(Q);
					LT = QT.copyList(QT);
				} else if(cnt >= cnt_setting) { 
					// Восстанавливаем полигон и список треугольников
					P = new Mj_Polygon(Q);
					LT = QT.copyList(QT);
					break;
				} else {
					tmp_triangles.last();
					tmp_triangles.remove();
					break;
				}
			} while(true);
		}
	}
	
	/**
	 * Ищет угол между двумя векторами, и если он равен 180 градусам, удаляет
	 * начальную вершину каждого вектора. Так проверяется каждый угол полигона.
	 * 
	 * @param p полигон
	 * @return полигон
	 */
	
	private Mj_Polygon deleteMiddlePoints(Mj_Polygon p) {
		for(int i = 1; i <= p.size; i++) {
			Mj_Vertex q1 = p.getVertex();
			Mj_Vertex q2 = (Mj_Vertex) p.getVertex().next();
			Mj_Vertex qn = (Mj_Vertex) p.getVertex().prev();
			
			// Алгебраический угол м/д двумя векторами
			double FI = ang(qn.point().subtraction(q1.point()), q2.point().subtraction(q1.point()));
			
			// Если получающийся угол м/д двумя векторами по модулю равен 180 градусам,
			// тоудаляем начальную точку каждого вектора
			if(Math.abs(FI) == 180) {
				p.remove();
				i--;
			}
			
			p = LCShift(p);
		}
		
		// Т.к. при декомпозиции полигона на монотонные части при наличии внутренних
		// полигонов делаются разрезы для их объединения, то некоторые итоговые
		// монотонные полигоны могут иметь самопересечения
		if(self_test(p)) {
			Mj_Vertex first = p.getVertex();		
			
			int size_a = p.size - 1;
			
			for(int i = 0; i <= size_a; i++) {
				Edge a = new Edge(first.point(), ((Mj_Vertex) first.next()).point());
				Mj_Vertex third = first.cw().cw();
				
				int size_b = size_a - 1;
				
				for(int j = 1; j < size_b; j++) {
					Edge b = new Edge(third.point(), ((Mj_Vertex) third.next()).point());
				
					Intersect inter = a.cross(b, 0.0);
						
					if(inter == Intersect.SKEW_CROSS) {
						p.setVertexValue(new Mj_Vertex(b.org.x, b.org.y));
						p.remove();
						size_a = p.size - 1;
						size_b = 0;
						i = -1;
						break;
					}					
					third = third.cw();
				}				
				first = first.cw();
			}
		}
		
		return p;
	}
	
	/**
	 * При самопересечении сторон внешнего полигона оставляет точку пересечения, а
	 * остальные точки удаляет.
	 */
	
	private void deleteIncorrectPointsInOuter() {
		boolean isCross = false; // Имеется пересечение сторон
		
		Mj_Vertex first = outer_polygon.getVertex();		
			
		int size = outer_polygon.size;
		
		for(int i = 0; i <= size - 1; i++) {
			Edge a = new Edge(first.point(), ((Mj_Vertex) first.next()).point());
			Mj_Vertex third = first.cw().cw();
				
			for(int j = 1; j < size - 2; j++) {
				Edge b = new Edge(third.point(), ((Mj_Vertex) third.next()).point());
				
				Intersect inter = a.cross(b, 0.0);
						
				if(inter == Intersect.SKEW_CROSS) {
					Mj_Vertex cross_v = getCrossingPoint(a, b);
					
					Mj_Vertex tmp1 = null;
					Mj_Vertex tmp2 = null;
					
					if(cross_v.point().equalsPoints(a.org)) {
						outer_polygon.setVertexValue(new Mj_Vertex(a.org));					
						tmp1 = outer_polygon.getVertex();
						
						outer_polygon.setVertexValue(new Mj_Vertex(b.org));
						tmp2 = outer_polygon.getVertex();
						
						tmp1.prev = tmp2;
						tmp2.next = tmp1;
					} else if(a.dest.equalsPoints(b.dest)) {
						tmp1 = ((Mj_Vertex) first.next());
						tmp2 = ((Mj_Vertex) third.next());
						// Объект для изменения координат вершин в месте пересечения
						VertexOffseter vOff = new VertexOffseter();
						
						// Смещаем координаты вершин в месте пересечения полигона так, чтобы небыло самопересечений
						vOff.offset(tmp1, tmp2, outer_polygon, 0.0000001);
					} else {
						outer_polygon.setVertexValue(new Mj_Vertex(a.org));					
						tmp1 = outer_polygon.getVertex();
						
						outer_polygon.setVertexValue(new Mj_Vertex(b.dest));
						tmp2 = outer_polygon.getVertex();
						
						tmp1.next = tmp2;
						tmp2.prev = tmp1;
					}
					
					outer_polygon.resize();
					size = outer_polygon.size;
					isCross = true;
					break;
				}
					
				third = third.cw();
			}
			
			if(isCross) {
				isCross = false;
				i = -1;
			}
				
			first = first.cw();
		}
	}
	
	/**
	 * Ищет в списке полигонов полигон с одинаковыми вершинами (координатами вершин)
	 * и количеством этих вершин в полигоне.
	 * 
	 * @param list список полигонов
	 * @param polygon полигон, который необходимо найти в списке полигонов
	 * @return TRUE, если в списке полигонов найден заданный полигон, иначе - FALSE
	 */
	
	private boolean isContainPolygonInList(Mj_List<Mj_Polygon> list, Mj_Polygon polygon) {
		// Массив для сохранения состояния равенства вершин заданного полигона и
		// полигона из списка
		boolean [] contains = new boolean[polygon.size];
		
		for(int i = 0; i < contains.length; i++)
			contains[i] = false;
		
		list.first();
		
		for(int i = 0; i < list.length(); i++, list.next()) {
			Mj_Polygon tmp_p = list.val();
			
			if(tmp_p.size == polygon.size) {
				for(int j = 0; j < polygon.size(); j++, polygon.advance()) {
					Mj_Vertex tmp_v = polygon.getVertex();
					
					for(int k = 0; k < tmp_p.size; k++, tmp_p.advance()) {
						if(tmp_v.equalsCoordsVertex(tmp_p.getVertex())) {
							contains[j] = true;
						}
					}
				}
			}
		}
		
		// Если количество вершин заданного полигона совпадает с количеством
		// состояний равенства координат вершин в одном из полигонов списка, 
		// то в списке полигонов присутствует такой же полигон (с теми же координатами), 
		// что и заданный
		short true_count = 0;
		
		for(int i = 0; i < contains.length; i++) {
			if(contains[i] == true)
				true_count++;
		}
		
		if(true_count == polygon.size())
			return true;
		
		return false;
	}
	
	/**
	 * Проверяет пересечение ребра, созданного из одной точки внешнего полигона и одной
	 * точки внутреннего полигона, с любой из сторон внешнего и внутреннего полигонов.
	 * 
	 * @param edge ребро
	 * @param outer внешний полигон
	 * @param inner внутренний полигон
	 * @return TRUE, если ребро пересекает какую-либо сторону полигонов, иначе - FALSE
	 */
	
	private boolean cross_edge_test(Edge edge, Mj_Polygon outer, Mj_Polygon inner) {
		boolean isCrossOuter = false; // Признак пересечения внешнего полигона
		boolean isCrossInner = false; // Признак пересечения внутреннего полигона
		
		Mj_Vertex first_outer = outer.getVertex();
		Mj_Vertex first_inner = inner.getVertex();
		
		for(int i = 0; i < outer.size; i++) {
			Edge a = new Edge(first_outer.point(), ((Mj_Vertex) first_outer.next()).point());
			
			Intersect inter = a.cross(edge, 0.0);
			
			Point_position pp = first_outer.point().classify(edge);
			Point_position ppn = ((Mj_Vertex) first_outer.next()).point().classify(edge);
			
			if(inter == Intersect.SKEW_CROSS && pp != Point_position.ORIGIN && ppn != Point_position.ORIGIN) {
				isCrossOuter = true;
				
				break;
			}
			
			first_outer = first_outer.cw();
		}
		
		for(int i = 0; i < inner.size; i++) {
			Edge a = new Edge(first_inner.point(), ((Mj_Vertex) first_inner.next()).point());
			
			Intersect inter = a.cross(edge, 0.0);
			
			Point_position pp = first_inner.point().classify(edge);
			Point_position ppn = ((Mj_Vertex) first_inner.next()).point().classify(edge);
			
			if(inter == Intersect.SKEW_CROSS && pp != Point_position.DESTINATION && ppn != Point_position.DESTINATION) {
				isCrossInner = true;
				
				break;
			}
			
			first_inner = first_inner.cw();
		}
		
		if(isCrossOuter || isCrossInner)
			return true;
		
		return false;
	}
	
	/**
	 * Определяет пересечение несмежных сторон полигона. Учитывает одинаковые
	 * координаты ребер получившихся при объединении внутреннего и внешнего полигонов.
	 * 
	 * @param P тестируемый полигон
	 * @return TRUE, если пересечения присутствуют, иначе - FALSE
	 */
	
	private boolean self_test(Mj_Polygon P) {		
		Mj_Vertex first = P.getVertex();		
		
		for(int i = 0; i <= P.size - 1; i++) {
			Edge a = new Edge(first.point(), ((Mj_Vertex) first.next()).point());
			Mj_Vertex third = first.cw().cw();
			
			for(int j = 1; j < P.size - 2; j++) {
				Edge b = new Edge(third.point(), ((Mj_Vertex) third.next()).point());
			
				Intersect inter = a.cross(b, 0.0);
					
				if(inter == Intersect.SKEW_CROSS)
					return true;
				
				third = third.cw();
			}			
			first = first.cw();
		}
		
		return false;
	}
	
	/**
	 * Поворачивает вектор на заданный угол.
	 * 
	 * @param angle заданный угол
	 * @param v заданный вектор
	 * @return повернутый вектор
	 */
	
	private Mj_Point rotateMatrix(double angle, Mj_Point v) {
		double x = (v.x * Math.cos(Math.toRadians(angle))) - (v.y * Math.sin(Math.toRadians(angle)));
		double y = (v.x * Math.sin(Math.toRadians(angle))) + (v.y * Math.cos(Math.toRadians(angle)));
		
		return new Mj_Point(x, y);
	}
	
	/**
	 * Округляет число по верхнему целому.
	 * 
	 * @param num округляемое число
	 * @return округленное число по верхнему целому
	 */
	
	private int roundUp(double num) {
		if((num - (int) num) == 0)
			return (int) num;
		else
			return (int) num + 1;
	}
	
	/**
	 * Сдвигает текущую точку полигона на следующую. 
	 * 
	 * @param Q полигон
	 * @return новую текущую точку полигона
	 */
	
	private Mj_Polygon LCShift(Mj_Polygon Q) {
		Q.advance();
		
		return Q;
	}
	
	/**
	 * Вычисляет алгебраический угол м/д векторами
	 * 
	 * @param V вектор
	 * @param W вектор
	 * @return алгебраический угол м/д векторами
	 */
	
	private double ang(Mj_Point V, Mj_Point W) {
		return Math.toDegrees(Math.acos(((V.x * W.x) + (V.y * W.y)) / ((Math.sqrt(Math.pow(V.x, 2) + Math.pow(V.y, 2))) * (Math.sqrt(Math.pow(W.x, 2) + Math.pow(W.y, 2))))));
	}
		
	/**
	 * Функция получает на вход массив точек с координатами в формате (x, y), из которых состоит полигон.
	 * Также функция получает на вход второй аргумент в виде массива массивов точек с координатами в 
	 * формате (x, y) полигонов, которые располагаются внутри полигона, заданного в первом аргументе.
	 * В качестве второго аргумента можно задавать значение NULL. Затем функция разбивает исходный полигон 
	 * на монотонный полигон(ы) и возвращает его (их) в виде массива массивов точек с координатами в 
	 * формате (x, y).
	 * 
	 * @param outer массив точек с координатами в формате (x, y), из которых состоит полигон
	 * @param inners массив массивов точек с координатами в формате (x, y) полигонов, которые 
	 * располагаются внутри полигона, заданного в первом аргументе
	 * @return массив массивов точек с координатами в формате (x, y), треугольники либо null
	 */
	
	public double[][] triangulation(double[] outer, 
			                        double[][] inners) {	
//		middleH_fromSize(outer);
		
		useOuterVertexes = new ArrayList<>(); 
		Mj_List<Mj_Polygon> inner_polys_list = new Mj_List<Mj_Polygon>(); // Внутренние полигоны, если они есть
		
		// Проверяем на четность. Если не четное кол-во координат, то 
		// полигон не удастся построить.
		if(outer.length % 2 == 0) {
			outer_polygon = makePolygon(outer); // Создаем полигон из массива
			
			// Попадаются полигоны с самопересечением (не корректные). Оставляем точки
			// пересечения, а остальное отрезаем.
			deleteIncorrectPointsInOuter();
			
			// Проверяем наличие внутренних полигонов
			if(inners != null) {				
				// Проверяем каждый внутренний полигон на четность и заполняем список внутренних полигонов
				for(int i = 0; i < inners.length; i++) {
					if(inners[i].length % 2 == 0)
						inner_polys_list.append(makePolygon(inners[i])); 
				}				
				
				// Избавляемся от внутренних полигонов
				removeInnerPolygons(inner_polys_list);
			} else {
				// Принимаем, что изначально обход от вершины к вершине полигона по часовой стрелке
				boolean outer_CW = getBoolDirectionOfPolygonVertexes(outer_polygon); 
				
				// Если обход вершин полигона против часовой стрелки, то меняем его на обратный
				if(!outer_CW) {
					outer_polygon.changeCircumventPoints();
					outer_CW = true; 
				}
			}
		}
		
		// Декомпозиция полигона на монотонные части
		monotone_polygons = decompositor.regularize(outer_polygon);
		
		// Вычисляем среднее значение параметра h
//		middleH_fromEdge();
		
		useOuterVertexes = null;
		inner_polys_list = null;
		// Триангуляция монотонных полигонов
		return getTriangleArray();
	}
	
	/**
	 * Вычисляет габаритный параметр для рассчета стороны треугольника при триангуляции,
	 * как среднее от самого большого и самого маленького ребер полигона.
	 */
	
	@SuppressWarnings("unused")
	private void middleH_fromEdge() {
		double min_length = 0, max_length = 0;
		
		if(monotone_polygons != null && monotone_polygons.length() > 0) {
			monotone_polygons.first();
			
			for(int i = 0; i < monotone_polygons.length(); i++) {
				Mj_Polygon p = monotone_polygons.val();
				
				for(int j = 0; j < p.size; j++) {
					double length = ((Mj_Vertex) p.getVertex().next()).point().subtraction(p.getVertex().point()).length();
					
					if(i == 0 && j == 0) {
						min_length = length;
						max_length = length;
					}
					
					if(length > max_length)
						max_length = length;
					
					if(length < min_length)
						min_length = length;
										
					p.advance();
				}
				
				monotone_polygons.next();
			}
		}
		
		h = (min_length + max_length) / 2;
		
//		System.out.println("Min L = " + min_length + ", Max L = " + max_length + ", H = " + h);
	}
	
	/**
	 * Вычисляет габаритный параметр для рассчета стороны треугольника при триангуляции,
	 * как половина длины от стороны ограничивающего объект карты прямоугольника.
	 * 
	 * @param outer массив координат внешнего полигона
	 */
	
	@SuppressWarnings("unused")
	private void middleH_fromSize(double[] outer) {		
		JRect bounds = new JRect(); // Ограничивающий объект карты прямоугольник
		
		// Долгота (ось X) точек объекта карты
		double [] lon_x_outer = new double[outer.length / 2];
		
		for(int i = 0, j = 0; i < outer.length; i+=2, j++)
			lon_x_outer[j] = outer[i];
		
			
		// Широта (ось Y) точек объекта карты
		double [] lat_y_outer = new double[outer.length / 2];
		
		for(int i = 1, j = 0; i <= outer.length; i+=2, j++)
			lat_y_outer[j] = outer[i];
		
		// Создаем геометрическую фигуру полигона из точек объекта карты
		JPolygon polygon = new JPolygon(lon_x_outer, lat_y_outer, lon_x_outer.length);
		
		// Получаем от полигона его ограничивающий пряоугольник, но т.к. ось Y полигона напрвленна
		// вверх и широта изменяется также, а ось Y у прямоугольника направлена вниз, то меняем 
		// друг с другом координаты top и bottom в возвращаемом ограничивающем прямоугольнике
		bounds = polygon.getBounds(); // Ось Y вверх
			
		h = bounds.width / 2;
		
//		System.out.println("Min L = " + min_length + ", Max L = " + max_length + ", H = " + h);
	}
	
	/**
	 * Выполняет триангуляцию монотонных полигонов из списка и возвращает 
	 * список треугольников в виде массива массивов с координатами. Координаты
	 * треугольников храняться в виде: array_1 = {x1,y1, x2,y2, x3,y3}.
	 * 
	 * 
	 * @return список треугольников в виде массива массивов с координатами
	 */
	
	private double[][] getTriangleArray() {
		if(monotone_polygons != null && monotone_polygons.length() > 0) {
			monotone_polygons.first();
		
			for(int i = 0; i < monotone_polygons.length(); i++) {
				boolean outer_CW = getBoolDirectionOfPolygonVertexes(monotone_polygons.val());
				// Возвращает полигонам оригинальные (изначальные) координаты вершин,
				// которые менялись при приращении и повороте
				decompositor.returnOriginalViewAfterSplit(monotone_polygons.val());
				
				// Если обход вершин полигона по часовой стрелке, то меняем его на обратный
				if(outer_CW) {
					monotone_polygons.val().changeCircumventPoints();
					outer_CW = false; 
				}
				
				triangles.append(contri_poly(monotone_polygons.val(), h));
				monotone_polygons.next();
			}			
		
			return getArraysCoords();
		} else
			return null;
	}
	
	/**
	 * Сортирует вершины в порядке увеличения координаты X.
	 * 
	 * @param p полигон
	 * @param cmp идентификатор направления оси X (влево или вправо)
	 * @return массив отсортированных вершин
	 */
	
	private Mj_Vertex[] buildSchedule(Mj_Polygon p, Scaning cmp) {
		Mj_Vertex[] schedule = new Mj_Vertex[p.size];
		
		for(int i = 0; i < p.size; i++, p.advance(Rotation.CLOCKWISE)) {
			schedule[i] = p.getVertex();
		}
		
		insertionSort(schedule, p.size, cmp);
		
		return schedule;
	}
	
	/**
	 * Сортирует элементы в массиве от меньшего к большему.
	 * 
	 * @param a массив
	 * @param n кол-во элементов массива для сортировки
	 */
	
	private void insertionSort(Mj_Vertex[] a, int n, Scaning cmp) {
		for(int i = 0; i < n - 1; i++) {
			int min = i;
			
			for(int j = i + 1; j < n; j++) {
				switch(cmp) {
				case LEFT_TO_RIGHT:
					if(CompareFunc.leftToRightCmp(a[j], a[min]) < 0)
						min = j;
					break;
				case RIGHT_TO_LEFT:
					if(CompareFunc.rightToLeftCmp(a[j], a[min]) < 0)
						min = j;
					break;
				}
			}
			
			a = swap(a, i, min);
		}
	}
	
	/**
	 * Выполняет взаимную замену значений в массиве вершин для указаных ей двух индексов.
	 * 
	 * @param a массив вершин
	 * @param i первый индекс
	 * @param min второй индекс
	 * @return измененный массив вершин
	 */
	
	private Mj_Vertex[] swap(Mj_Vertex[] a, int i, int min) {
		Mj_Vertex v = a[i];
		
		a[i] = a[min];
		a[min] = v;
		
		return a;
	}
	
	/**
	 * Преобразует координаты вершин треугольников в массив массивов.
	 * 
	 * @return массив массивов с координатами
	 */
	
	public double[][] getArraysCoords() {
		double[][] arraysCoords = new double[triangles.length()][];
		
		triangles.first();
		
		for(int i = 0; i < triangles.length(); i++) {
			Mj_Polygon p = triangles.val();
			
			double[] tmp = new double[p.size * 2];
			
			for(int j = 0; j < p.size * 2; j+=2) {
				arraysCoords[i] = tmp;
				
				tmp[j] = p.point().x;
				tmp[j + 1] = p.point().y;	
				
				p.advance();
			}
			
			triangles.next();
		}
		
		triangles.delete_list();
		
		return arraysCoords;
	}
	
	/**
	 * Если полигон содержит в себе внутренние полигоны, то эта функция избавляется
	 * от них. В итоге получаются полигон без внутренних полигонов. Сначала проверяем 
	 * внутренние полигоны на наличие стороны (сторон), которые коллинеарны (одинаковы) стороне (сторонам) 
	 * внешнего полигона, и если такие есть, то объединяем внутренний полигон с внешним, а коллинерные 
	 * ребра (стороны у внутреннего и внешнего полигонов) удаляем.  Далее берем каждую точку внешнего 
	 * полигона и ищем самую ближнею точку какого-нибудь внутреннего полигона. Когда находим, то проверяем 
	 * направление обхода внешнего полигона, сравниваем с направлением обхода внутреннего полигона, 
	 * которому принадлежит найденная точка, если надо, меняем направление обхода внутреннего
	 * полигона. Затем делаем расщепление по двум точкам и остается только внешний полигон.
	 * 
	 * @param inner_polys_list внутренние полигоны
	 */
	
	private void removeInnerPolygons(Mj_List<Mj_Polygon> inner_polys_list) {	
		// Объединяем внутренние полигоны, если они имеют одинаковые ребра (стороны)
		inner_polys_list = sortInnerPolygons(inner_polys_list);
				
		int size = inner_polys_list.length(); // Размер списка внутренних полигонов
		
		inner_polys_list.first(); // Переходим на первый полигон в списке
		
		// Избавляемся от внутренних полигонов стороны которых лежат на сторонах внешнего полигона
		for(int i = 0; i < size; i++) {				
			// Проверяем наличие внутренних полигонов, сторона(ы) которого лежит на
			// стороне(ах) внешнего полигона
			if(checkAndUnionInnerSides(inner_polys_list.val())) {
				inner_polys_list.remove();
			} 
			
			inner_polys_list.next();
		}
			
		inner_polys_list.first(); // Переходим на первый полигон в списке
		size = inner_polys_list.length(); // Размер списка внутренних полигонов
		
		for(int i = 0; i < size; i++) {
			// Находим внутренний полигон наиболее близко расположенный к внешнему
			inner_polys_list.val(getPolygonWithMinDistance(inner_polys_list));
			// Объединяем два полигона в один внешний 
			// полигон, который объединили с внешним
			unionPolygons(inner_polys_list.remove());	
		}
	}
	
	/**
	 * Сортирует список внутренних полигонов на отдельные ни чем с друг
	 * другом не свзанные. Это может быть один независимый полигон, 
	 * либо группа из нескольих полигонов имеющих общие стороны.
	 * 
	 * @param list список внутренних полигонов
	 * @return список отсортированных внутренних полигонов
	 */
	
	private Mj_List<Mj_Polygon> sortInnerPolygons(Mj_List<Mj_Polygon> list) {
		// Список отсортированых полигонов
		ArrayList<ArrayList<Mj_Polygon>> sortInnerPolygons = new ArrayList<ArrayList<Mj_Polygon>>();
		// Первоначальный список полигонов (не отсортированных)
		ArrayList<Mj_Polygon> allInnersPolygons = new ArrayList<>();
		// Признак того, что один полигон имеет хотябы одну общую сторону с другим
		boolean isBelong = false;
		
		list.first();
		
		// Для удобства работы переводим полигоны из списка одного типа в список другого типа
		for(int p = 0; p < list.length(); p++) {
			allInnersPolygons.add(list.val());
			list.next();
		}
		
		if(allInnersPolygons.size() == 1) {
			sortInnerPolygons.add(allInnersPolygons);
		} else {
			int size = allInnersPolygons.size() - 1;
			
			for(int p = 0; p < size; p++) {
				isBelong = false;
				
				// Первый в списке полигон
				Mj_Polygon first_inner_polygon = allInnersPolygons.get(p);
										
				// Направление обхода вершин в первом в списке внутреннем полигоне
				boolean first_CW = getBoolDirectionOfPolygonVertexes(first_inner_polygon);
										
				// Если обход вершин полигона против часовой стрелки, то меняем его на обратны
				if(!first_CW) {
					first_inner_polygon.changeCircumventPoints();
					first_CW = true; 
				}
				
				// Временный полигон для хранения полигонов с общими сторонами
				ArrayList<Mj_Polygon> tmpList = new ArrayList<>();
				
				for(int p2 = 1; p2 < allInnersPolygons.size(); p2++) {
					// Следующий за первым в списке полигон
					Mj_Polygon next_inner_polygon = allInnersPolygons.get(p2);
											
					// Направление обхода вершин в следующем внутреннем полигоне
					boolean next_CW = getBoolDirectionOfPolygonVertexes(next_inner_polygon);
					
					if(first_CW != next_CW) {
						next_inner_polygon.changeCircumventPoints();
						next_CW = true;
					}
					
					// Ищем в полигонах одинаковые ребра
					int next_size = next_inner_polygon.size;
								
					for(int i = 0; i < next_size; i++) {
						Edge next_inner_edge = next_inner_polygon.edge();
						
						for(int j = 0; j < first_inner_polygon.size; j++) {
							Edge first_inner_edge = first_inner_polygon.edge();
										
							Intersect intersect = next_inner_edge.intersect(first_inner_edge, 0.0);
										
							if(intersect == Intersect.COLLINEAR) {
								if(first_inner_edge.org.equalsPoints(next_inner_edge.dest) && 
								   first_inner_edge.dest.equalsPoints(next_inner_edge.org)) {
									isBelong = true;
									
									if(!tmpList.contains(next_inner_polygon))
										tmpList.add(next_inner_polygon);
								}
							}
							first_inner_polygon.advance();
						}
						next_inner_polygon.advance();
					}
				}
				
				if(isBelong) {
					tmpList.add(first_inner_polygon);
					sortInnerPolygons.add(tmpList);
					
					for(int n = 0; n < tmpList.size(); n++)
						allInnersPolygons.remove(tmpList.get(n));
					
					size = allInnersPolygons.size() - 1;
					p = -1;
				} else {
					tmpList.add(first_inner_polygon);
					sortInnerPolygons.add(tmpList);
					
					allInnersPolygons.remove(first_inner_polygon);
					size = allInnersPolygons.size() - 1;
					p = -1;
					
					// Если все полигоны перебрали, а один остался, то
					// т.к. он не имеет коллинеарных сторон, добавляем его
					// к окончательному списку
					if(allInnersPolygons.size() == 1)
						sortInnerPolygons.add(allInnersPolygons);
				}
			}
		}
		
		// Очищаем заданный список
		list.delete_list();
		
		// Теперь отсортированные полигоны с общими сторонами (при наличии) объединяем 
		// друг с другом и заполняем заданный список новыми полигонами
		list = unionInnerPolygons(sortInnerPolygons);
		
		return list;
	}
	
	/**
	 * Проверяет полигон на замыкание, т.е. не имеет ли полигон одного общего
	 * ребра (стороны). При наличии общего ребра соединяет полигон по крайним точкам
	 * этого ребра, в результате чего получаем два новых полигона: внутренний и внешний.
	 * Внешний полигон возвращаем, а внутренний полигон заносим в спиок новых полигонов карты.
	 * 
	 * @param P полигон для проверки
	 * @return новый полигон
	 */
	
	private Mj_Polygon testPolygon(Mj_Polygon P) {
		boolean self = self_test(P);
		boolean check = true;
		
		while(self){			
			if(check) {
				Mj_Vertex first = P.getVertex();
				
				int size = P.size;
				
				for(int i = 0; i < size - 1; i++) {
					Edge a = new Edge(first.point(), ((Mj_Vertex) first.next()).point());
					Mj_Vertex third = first.cw().cw();
				
					for(int j = 0; j < size - 2; j++) {
						Edge b = new Edge(third.point(), ((Mj_Vertex) third.next()).point());
					
						Intersect intersect = a.intersect(b, 0.0);
					
						if(intersect == Intersect.COLLINEAR) {							
							if(a.org.equalsPoints(b.dest) && b.org.equalsPoints(a.dest)) {
								Mj_Polygon copy_polygon1 = new Mj_Polygon(P);
								Mj_Polygon copy_polygon2 = new Mj_Polygon(P);
								
								Mj_Vertex [] first_orgs = copy_polygon1.setVertexValueToArray(copy_polygon1.setVertexValue(new Mj_Vertex(a.org)));
								Mj_Vertex [] next_orgs = copy_polygon2.setVertexValueToArray(copy_polygon2.setVertexValue(new Mj_Vertex(b.org)));
								
								// Объединяем два полигона для создания двух новых полигонов
								copy_polygon1.setVertex(first_orgs[0]);
								first_orgs[0].splice((Mj_Vertex) first_orgs[1]);
								copy_polygon1.resize();
								
								copy_polygon2.setVertex(next_orgs[1]);
								next_orgs[1].splice((Mj_Vertex) next_orgs[0]);
								copy_polygon2.resize();
								
								// Попределяем какой из двух получившихся полигонов внутренний, 
								// а какой станет новым внешним полигоном
								// У кого площать по модулю больше, тот внутренний полигон
								double sum1 = Math.abs(copy_polygon1.getAreaOfPolygon());
								double sum2 = Math.abs(copy_polygon2.getAreaOfPolygon());
								
								if(sum1 > sum2) {
									outerPolysAfterUnionInnersPolys.append(copy_polygon2);
									P = copy_polygon1;
									P.resize();
								} else {
									outerPolysAfterUnionInnersPolys.append(copy_polygon1);
									P = copy_polygon2;
									P.resize();
								}
								
								self = self_test(P);
								
								if(!self)
									check = false;
								break;
							}
						}
						third = third.cw();
					}
					first = first.cw();
					
					if(!check && !self) 
						break;
				}
			}
		}
		
    	return P;
    }
	
	/**
	 * Проверяет полигоны на коллинеарные (одинаковые) ребра и, если такие имеются, 
	 * объединяет полигоны по крайним точкам одинаковых ребер. В итоге, получается 
	 * один полигон.
	 * 
	 * @param first_inner_polygon этот полигон будет результативным полигоном после объединения
	 * @param next_inner_polygon этот полигон сливается с предыдущим и больше не используется
	 */
	
	private void testEdges(Mj_Polygon first_inner_polygon, Mj_Polygon next_inner_polygon) {	
		// Крайние точки одинаковых ребер двух полигонов
		Mj_Vertex first_point1 = null;
		Mj_Vertex next_point1 = null;
		Mj_Vertex first_point2 = null;
		Mj_Vertex next_point2 = null;
		
		// Проверяем не является ли это цепочкой последовательно коллинеарных ребер
		boolean stop = false;
		boolean reverse = false;
		Edge tmp_first_inner_edge = null;
		Edge tmp_next_inner_edge = null;
		
		while(!stop) {
			if(!reverse) {
				first_inner_polygon.setVertex(first_inner_polygon.ccw());
				tmp_first_inner_edge = first_inner_polygon.edge();
				
				next_inner_polygon.setVertex(next_inner_polygon.cw());
				tmp_next_inner_edge = next_inner_polygon.edge();
			} else {
				first_inner_polygon.setVertex(first_inner_polygon.cw());
				tmp_first_inner_edge = first_inner_polygon.edge();
				
				next_inner_polygon.setVertex(next_inner_polygon.ccw());
				tmp_next_inner_edge = next_inner_polygon.edge();
			}						
			
			Intersect tmp_intersect = tmp_next_inner_edge.intersect(tmp_first_inner_edge, 0.0);
			
			if(!(tmp_intersect == Intersect.COLLINEAR && 
			   (tmp_first_inner_edge.org.equalsPoints(tmp_next_inner_edge.dest) && 
				tmp_first_inner_edge.dest.equalsPoints(tmp_next_inner_edge.org)))) {				
				if(!reverse) {
					first_inner_polygon.setVertex(first_inner_polygon.cw());
					next_inner_polygon.setVertex(next_inner_polygon.ccw());
					
					first_point1 = first_inner_polygon.getVertex();
					next_point1 = next_inner_polygon.getVertex();
					
					reverse = true;
				} else {
					first_inner_polygon.setVertex(first_inner_polygon.ccw());
					next_inner_polygon.setVertex(next_inner_polygon.cw());
					
					first_point2 = first_inner_polygon.getVertex();
					next_point2 = next_inner_polygon.getVertex();
					
					reverse = false;
				}
			}
			
			if(first_point1 != null && next_point1 != null && first_point2 != null && next_point2 != null)
				stop = true;
		}
		
		if(first_point1.equalsVertex(first_point2) && next_point1.equalsVertex(next_point2)) {
			Mj_Vertex tmp = (Mj_Vertex) first_point1.next.next;
			
			first_point1.next = next_point1.next.next;
			next_point1.next.next.prev = first_point1;
			
			first_inner_polygon.setVertex((Mj_Vertex) first_point2.next);
			
			tmp.prev = next_point1;
			next_point1.next = tmp;
		} else {
			first_point1.next = next_point1.next.next;
			next_point1.next.next.prev = first_point1;
			
			first_inner_polygon.setVertex((Mj_Vertex) first_point2.next);
			
			first_point2.next.prev = next_point2.prev;
			next_point2.prev.next = first_point2.next;
		}	
		
		first_inner_polygon.resize();
	}
	
	/**
	 * Объединяет полигоны имеющие коллинеарные (одинаковые) стороны в один общий
	 * полигон, а также, если получившийся один полигон также имеет общие стороны
	 * (замкнут сам на себя), то создает два новых полигона. 
	 * 
	 * @param list список полигонов с коолинеарными (одинаковыми) сторонами
	 * @return список полигонов
	 */
	
	private Mj_List<Mj_Polygon> unionInnerPolygons(ArrayList<ArrayList<Mj_Polygon>> list) {
		// Итоговый список внутренних полигонов
		Mj_List<Mj_Polygon> inner_polys = new Mj_List<Mj_Polygon>();
		ArrayList<Mj_Polygon> tmpList = null;
		
		for(int l = 0; l < list.size(); l++) {
			tmpList = list.get(l);
			
			if(tmpList.size() == 1) {
				Mj_Polygon tmpPolygon = testPolygon(tmpList.get(0));
				
				inner_polys.append(tmpPolygon);
			} else {
				int size = tmpList.size() - 1;
				
				for(int p = 0; p < size; p++) {
					// Первый в списке полигон
					Mj_Polygon first_inner_polygon = tmpList.get(p);
					
					for(int p2 = 1; p2 < tmpList.size(); p2++) {
						// Следующий за первым в списке полигон
						Mj_Polygon next_inner_polygon = tmpList.get(p2);
						
						// Ищем в полигонах одинаковые ребра
						int next_size = next_inner_polygon.size;
						
						for(int i = 0; i < next_size; i++) {
							Edge next_inner_edge = next_inner_polygon.edge();
							
							for(int j = 0; j < first_inner_polygon.size; j++) {
								Edge first_inner_edge = first_inner_polygon.edge();
								
								Intersect intersect = next_inner_edge.intersect(first_inner_edge, 0.0);
								
								if(intersect == Intersect.COLLINEAR) {
									if(first_inner_edge.org.equalsPoints(next_inner_edge.dest) && 
									   first_inner_edge.dest.equalsPoints(next_inner_edge.org)) {
										testEdges(first_inner_polygon, next_inner_polygon);	
										
										tmpList.remove(next_inner_polygon);
										next_size = 0;
										size = tmpList.size() - 1;
										p = -1;										
										
										if(tmpList.size() < 2) { 
											first_inner_polygon = testPolygon(first_inner_polygon);
											
											tmpList.clear();
											tmpList.add(first_inner_polygon);
										} 
										
										break;
									}
								}
								first_inner_polygon.advance();
							}
							next_inner_polygon.advance();
						}
					}
				}
				inner_polys.append(tmpList.get(0));
			}
		}
			
		return inner_polys;
	}
	
	/**
	 * Проверяет принадлежность какой-либо стороны внутреннего полигона стороне(ам)
	 * внешнего полигона.
	 * 
	 * @param inner_polygon внутренний полигон
	 * @return TRUE, если одна или несколько сторон внутреннего полигона принадлежат
	 *  стороне(ам) внешнего полигона, иначе - FALSE
	 */
	
	private boolean checkAndUnionInnerSides(Mj_Polygon inner_polygon) {
		boolean isCheck = false; // Признак коллинеарности ребер (одинаковые стороны у полигонов)
		// Список ребер внешнего полигона
		ArrayList<Edge> outer_edges = new ArrayList<>(0); 
		// Список ребер внутрреннего полигона
		ArrayList<Edge> inner_edges = new ArrayList<>(0); 
		// Список не коллинеарных ребер внешнего и внутрреннего полигонов
		ArrayList<Edge> edges = new ArrayList<>(0); 
		// Список коллинеарных ребер внешнего и внутрреннего полигонов
		ArrayList<Edge> collinear_edges = new ArrayList<>(0); 
		
		// Направление обхода вершин во внешнем полигоне
		boolean outer_CW = getBoolDirectionOfPolygonVertexes(outer_polygon); 
		// Направление обхода вершин во внутреннем полигоне
		boolean inner_CW = getBoolDirectionOfPolygonVertexes(inner_polygon);
					
		// Если обход вершин полигона против часовой стрелки, то меняем его на обратный
		if(!outer_CW) {
			outer_polygon.changeCircumventPoints();
			outer_CW = true; 
		}
					
		// Если направления обхода вершин полигонов не совпадают, то меняем направление обхода
		// у внутреннего полигона на противоположное
		if(outer_CW != inner_CW) {
			inner_polygon.changeCircumventPoints();
			inner_CW = true;
		}
		
		// Получаем ребра, из которых состоят полионы
		outer_edges = outer_polygon.getEdgesOfPolygon(outer_polygon);
		inner_edges = inner_polygon.getEdgesOfPolygon(inner_polygon);
		
		// Ищем в полигонах одинаковые ребра
		for(int i = 0; i < inner_edges.size(); i++) {
			Edge inner_edge = inner_edges.get(i);
			
			for(int j = 0; j < outer_edges.size(); j++) {
				Edge outer_edge = outer_edges.get(j);
				
				Intersect intersect = inner_edge.intersect(outer_edge, 0.0);
				
				if(intersect == Intersect.COLLINEAR) {
					if(outer_edge.org.equalsPoints(inner_edge.org) && outer_edge.dest.equalsPoints(inner_edge.dest)) {
						collinear_edges.add(inner_edge);
						
						isCheck = true;
					} 
				}
			}
		}
		
		if(collinear_edges.size() != 0) {
			// Создаем общий список ребер внешнего и внутреннего полигонов без их коллинеарных сторон
			// Для внутреннего полигона			
			for(int i = 0; i < collinear_edges.size(); i++) {
				Edge coll_edge = collinear_edges.get(i);
				
				for(int j = 0; j < inner_edges.size(); j++) {
					Edge inner_edge = inner_edges.get(j);
					
					if(inner_edge.equalsEdges(coll_edge)) {
						inner_edges.remove(j);
						j = -1;	
					}
				}
			}
			// Для внешнего полигона			
			for(int i = 0; i < collinear_edges.size(); i++) {
				Edge coll_edge = collinear_edges.get(i);
				
				for(int j = 0; j < outer_edges.size(); j++) {
					Edge outer_edge = outer_edges.get(j);
					
					if(outer_edge.equalsEdges(coll_edge)) {
						outer_edges.remove(j);
						j = -1;	
					}
				}
			}
			
			// Инвертируем направление ребер
			for(int i = 0, j = 0; i < inner_edges.size(); i++) {
				Edge e = inner_edges.remove(j);
				e.changeCircumventPoints();
				inner_edges.add(e);
			}
			
			// Объединяем списки ребер полигонов в один общий список
			edges.addAll(outer_edges);
			edges.addAll(inner_edges);
			
			// Ищем у ребер общие точки и объединяем их в полигон(ы)
			ArrayList<Mj_Polygon> res_poly = createPolygonsFromEdges(edges);
			
			// Если в результате получился один полигон, то делаем его внешним полигоном,
			// но если в результате получилось больше одного полигона, то первый полигон
			// в списке делаем внешним, а остальные помещаем в список внешних полигонов,
			// получившихся после объединения с внутренними полигонами
			if(res_poly.size() == 1) {
				outer_polygon = null;
				outer_polygon = res_poly.get(0);
			} else if(res_poly.size() > 1) {
				outer_polygon = null;
				outer_polygon = res_poly.get(0);
				
				for(int i = 1; i < res_poly.size(); i++) {
					outerPolysAfterUnionInnersPolys.append(res_poly.get(i));
				}
			}
		}
		
		return isCheck;
	}
	
	/**
	 * Проверяет наличие одной общей точки у внешнего полигона с внутренним
	 * 
	 * @param inner_polygon
	 * @return
	 */
	private boolean checkOnePoint(Mj_Polygon inner_polygon) {
		return false;
	}
	
	/**
	 * Из списка отдельных ребер создает замкнутые полигоны.
	 * 
	 * @param edges список ребер
	 * @return список оттдельных полигонов
	 */
	
	private ArrayList<Mj_Polygon> createPolygonsFromEdges(ArrayList<Edge> edges) {
		ArrayList<Mj_Polygon> polys = new ArrayList<>(1);
		ArrayList<Edge> tmp_edges = new ArrayList<>();
		
		if(edges.size() > 0) {
			tmp_edges.add(edges.remove(0));
			
			Mj_Point org = tmp_edges.get(0).org;
			Mj_Point dest = tmp_edges.get(tmp_edges.size() - 1).dest;
			
			for(int i = 0; i < edges.size(); i++) {
				if(tmp_edges.size() == 1) {
					if(org.equalsPoints(edges.get(i).dest)) {
						tmp_edges.add(0, edges.remove(i));
						org = tmp_edges.get(0).org;
						i = -1;
					} else if(dest.equalsPoints(edges.get(i).org)) {
						tmp_edges.add(edges.remove(i));
						dest = tmp_edges.get(tmp_edges.size() - 1).dest;
						i = -1;
					}
				} else {
					if(dest.equalsPoints(edges.get(i).org) && !org.equalsPoints(edges.get(i).dest)) {
						tmp_edges.add(edges.remove(i));
						dest = tmp_edges.get(tmp_edges.size() - 1).dest;
						i = -1;
					} else if(org.equalsPoints(edges.get(i).dest) && !dest.equalsPoints(edges.get(i).org)) {
						tmp_edges.add(0, edges.remove(i));
						org = tmp_edges.get(0).org;
						i = -1;
					} else if(dest.equalsPoints(edges.get(i).org) && org.equalsPoints(edges.get(i).dest)) {
						tmp_edges.add(edges.remove(i));
						
						// Создаем новый полигон
						// Координаты полигона
						double [] coords = new double[tmp_edges.size() * 2];
						
						for(int j = 0, k = 0; j < tmp_edges.size(); j++, k+=2) {
							coords[k] = tmp_edges.get(j).org.x;
							coords[k + 1] = tmp_edges.get(j).org.y;
						}
						
						polys.add(makePolygon(coords));
						
						tmp_edges.clear();
						
						if(edges.size() > 0) {
							tmp_edges.add(edges.remove(0));
							
							org = tmp_edges.get(0).org;
							dest = tmp_edges.get(tmp_edges.size() - 1).dest;
						}
						
						i = -1;
					}
				}
			}
		}
				
		return polys;
	}
	
	/**
	 * Объединяет внешний полигон с внутренним. В итоге получается один внешний полигон.
	 * 
	 * @param inner_polygon внутренний полигон
	 */ 
	
	private void unionPolygons(Mj_Polygon inner_polygon) {
		// Направление обхода вершин во внешнем полигоне
		boolean outer_CW = getBoolDirectionOfPolygonVertexes(outer_polygon); 
		// Направление обхода вершин во внутреннем полигоне
		boolean inner_CW = getBoolDirectionOfPolygonVertexes(inner_polygon);
				
		// Если обход вершин полигона против часовой стрелки, то меняем его на обратный
		if(!outer_CW) {
			outer_polygon.changeCircumventPoints();
			outer_CW = true; 
		}
				
		// Если направления обхода вершин полигонов совпадают, то меняем направление обхода
		// у внутреннего полигона на противоположное
		if(outer_CW == inner_CW) {
			inner_polygon.changeCircumventPoints();
			inner_CW = false;
		}
					
		// Разрезает внешний полигон по двум точкам (наиближайшие точки внешнего и внутреннего
		// полигонов), при этом создает их дубликаты. В итоге, внутренний полигон становится 
		// частью внешнего.		
		outer_polygon.getVertex().split_and_offset_triangle(inner_polygon.getVertex(), outer_polygon);
		
		// Изменяем размер полигона
//		outer_polygon.resize();
	}
	
	/**
	 * Определяет направление обхода от точки к следующей точки в полигоне.
	 * 
	 * @param polygon полигон
	 * @return TRUE, если обход осуществляется по часовой стрелке, иначе - FALSE 
	 */
	
	private boolean getBoolDirectionOfPolygonVertexes(Mj_Polygon polygon) {
		// Принимаем, что изначально обход от вершины к вершине полигона по часовой стрелке
		boolean CW = true; 
		
		// Вычисляем площадь полигона
		double S = polygon.getAreaOfPolygon();
		
		// Если площадь полигона больше нуля, то обход вершин совершается против часовой стрелки
		if(S > 0)
			CW = false;
		
		return CW;
	}
	
	/**
	 * Ищет значение координат точки пересечения двух отрезков.
	 * 
	 * @param a первый отрезок
	 * @param b второй отрезок
	 * @return вершина с координатами точки пересечения заданных отрезков
	 */
	
	private Mj_Vertex getCrossingPoint(Edge a, Edge b) {
		Mj_Vertex v = null;
//		double EPS = 1e-9; // Бесконечно малая величина (как ноль)	
		
		// Находим коэффициенты прямой для первого отрезка
		double a1 = a.dest.y - a.org.y; 
		double b1 = a.org.x - a.dest.x;
		double c1 = (a.org.x * (a.org.y - a.dest.y)) + (a.org.y * (a.dest.x - a.org.x));
		
		// Находим коэффициенты прямой для второго отрезка
		double a2 = b.dest.y - b.org.y; 
		double b2 = b.org.x - b.dest.x;
		double c2 = (b.org.x * (b.org.y - b.dest.y)) + (b.org.y * (b.dest.x - b.org.x));
		
		// Находим точку пересечения двух прямых
		
		// Ищем знаменатель, чтобы сразу понять пересекаются прямые или нет
		double znamenatel = (a1 * b2) - (a2 * b1);
		
		// Если знаменатель равен нулю или стремиться к нему, то прямые не пересекаются, иначе ...
		if(!(Math.abs(znamenatel) == 0)) {
			double chislitel_x = (c1 * b2) - (c2 * b1);
			double chislitel_y = (a1 * c2) - (a2 * c1);
			
			// Точка пересечения
			double res_x = (- (chislitel_x / znamenatel));
			double res_y = (- (chislitel_y / znamenatel));
			res_x = Decompositor.round7(res_x);
			res_y = Decompositor.round7(res_y);
			
			v = new Mj_Vertex(res_x, res_y);
		}
		
		return v;
	}
	
	/**
	 * Определяет направление обхода от точки к следующей точки в полигоне.
	 * 
	 * @param P полигон
	 * @return -1, если направление обхода по часовой стрелке, 1 - против часовой стрелки, иначе - 0
	 */
	
	private int getIntDirectionOfPolygon(Mj_Polygon P) {
		// Вычисляем площадь полигона
		double S = P.getAreaOfPolygon();
		
		if(S > 0)
			return 1; // Против часовой стрелки 
		else if(S < 0)
			return -1; // По часовой стрелке 
		else
			return 0; 
	}
	
	/**
	 * Находит внутренний полигон, который ближе всего расположен к какой-то вершине внешнего полигона.
	 * 
	 * @param outer_polygon внешний полигон
	 * @param inner_polys_list список внутренних полигонов
	 * @return внутренний полигон, который ближе всего расположен к какой-то вершине внешнего полигона
	 */
	
	private Mj_Polygon getPolygonWithMinDistance(Mj_List<Mj_Polygon> inner_polys_list) {
		double minDistance = 0; // Минимальное расстояние между двумя точками	
		int min_count = -1; // Индекс наиближайшего внутреннего полигона в списке внутренних полигонов 
		Mj_Vertex minVertex = null; // Вершина внутреннего полигона ближе всех расположенная к внешнему полигону
		boolean isFirstMin = false; // Флаг того, что была рассчитана в первый (начальная) раз минимальная дистанция м/д точками 
		
		// Сортируем вершины полигона слева на право
		Mj_Vertex[] outer_schedule = buildSchedule(outer_polygon, Scaning.LEFT_TO_RIGHT);
		Mj_Polygon inner = null; // Первый внутренний полигон списка		
		
		for(int i = 0; i < outer_schedule.length; i++) {
			inner = inner_polys_list.first();
			
			for(int j = 0; j < inner_polys_list.length(); j++) {
				// Сортируем вершины полигона слева на право
				Mj_Vertex[] inner_schedule = buildSchedule(inner, Scaning.LEFT_TO_RIGHT);
				
				for(int k = 0; k < inner_schedule.length; k++) {
					if(!isUse(outer_schedule[i])) { 
						double min = getDistanceBetweenVertexes(outer_schedule[i], inner_schedule[k]);
					
						if(!isFirstMin) {
							if(!cross_edge_test(new Edge(outer_schedule[i].point(), inner_schedule[k].point()), 
									            outer_polygon, inner)) {
								minDistance = min;
								min_count = j;
								minVertex = inner_schedule[k];
								outer_polygon.setVertex(outer_schedule[i]);
								
								isFirstMin = true;
							}							
						} else {
							if(min < minDistance) {
								if(!cross_edge_test(new Edge(outer_schedule[i].point(), inner_schedule[k].point()), 
							                        outer_polygon, inner)) {
									minDistance = min;
									min_count = j;
									minVertex = inner_schedule[k];
									outer_polygon.setVertex(outer_schedule[i]);
								}
								
							}
						}
					}
				}
				
				inner = inner_polys_list.next();
			}
		}
		
		inner_polys_list.first(); // Перемещаем окно списка на первого члена списка
		
		// Ищем нужный внутренний полигон
		for(int i = 0; i < min_count; i++) {
			inner_polys_list.next();
		}
		
		// Ищем вершину во внутреннем полигоне наиближайшую к внешнему полигону
		for(int j = 0; j < inner_polys_list.val().size; j++) {
			if(inner_polys_list.val().getVertex().equalsVertex(minVertex)) {
				inner_polys_list.val().setVertex(minVertex);
				break;
			}
			
			inner_polys_list.val().advance();
		}
		
		return inner_polys_list.val();
	}
	
	/**
	 * Проверяет вершину полигона на использование ранее в разрезании полигона
	 * при объединении внешнего полигона с внутренним
	 * 
	 * @param v вершина полигона
	 * @return TRUE, если вершина ранее уже использовалась, иначе - FALSE
	 */
	
	private boolean isUse(Mj_Vertex v) {
		for(int i = 0; i < useOuterVertexes.size(); i++) {
			Mj_Vertex tmp_v = useOuterVertexes.get(i);
			
			if(tmp_v.point().x == v.point().x && tmp_v.point().y == v.point().y)
				return true;
		} 
		
		return false;
	}
	
	/**
	 * Вычисляет расстояние между двумя вершинами по их координатам.
	 * 
	 * @param v1 первая вершина
	 * @param v2 вторая вершина
	 * @return расстояние между двумя вершинами
	 */
	
	private double getDistanceBetweenVertexes(Mj_Vertex v1, Mj_Vertex v2) {
		return Math.sqrt(Math.pow((v2.point().x - v1.point().x), 2) + Math.pow((v2.point().y - v1.point().y), 2));
	}
	
	/**
	 * Создает полигон по заданному массиву координат.
	 * 
	 * @param array массив координат
	 * @return полигон либо null
	 */
	
	private Mj_Polygon makePolygon(double[] array) {
		if(array.length % 2 == 0) {
			Mj_Point[] p_outer = getPointsArray(array);
			Mj_Vertex[] v_array = getVertexesArray(p_outer);
		
			Mj_ListVertex v_list = new Mj_ListVertex();
			v_list = v_list.arrayToList(v_array, v_array.length);
		
			return new Mj_Polygon(v_list.first());
		}
		
		return null;
	}
	
	/**
	 * Создает массив точек из заданноко списка координат.
	 * 
	 * @param array массив координат
	 * @return массив точек
	 */
	
	private Mj_Point[] getPointsArray(double[] array) {
		Mj_Point[] points = new Mj_Point[array.length / 2];
		
		for(int i = 0, j = 0; i < points.length; i++, j += 2)
			points[i] = new Mj_Point(array[j], array[j + 1]);
		
		return points;
	}
	
	/**
	 * Создает массив вершин из заданноко списка точек.
	 * 
	 * @param array массив точек
	 * @return массив вершин
	 */
	
	private Mj_Vertex[] getVertexesArray(Mj_Point[] array) {
		Mj_Vertex[] vertexes = new Mj_Vertex[array.length];
		
		for(int i = 0; i < array.length; i++)
			vertexes[i] = new Mj_Vertex(array[i]);
		
		return vertexes;
	}
}