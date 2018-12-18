/*
 * Copyright (c) 09.2017
 */

package Elements;

/**
 * Обобщенный функциональный интерфейс для ф-ций посещения узлов дерева поиска
 * при симметричном его обходе.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public interface VisitFuncImpl<T> {

	public void visit(T val);
}
