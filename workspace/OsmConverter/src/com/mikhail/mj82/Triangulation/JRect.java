/*
 * Copyright (c) 08.2016
 */

package com.mikhail.mj82.Triangulation;

import java.io.Serializable;

/**
 * ����� ������������ ��� �������� ��������������.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class JRect implements Serializable {
	
	private static final long serialVersionUID = -2855079158194121381L;
	
	public double left; // ���������� �������� ������ ���� �������������� �� ��� X
	public double top; // ���������� �������� ������ ���� �������������� �� ��� Y
	public double right; // ���������� ������� ������� ���� �������������� �� ��� X
	public double bottom; // ���������� ������� ������� ���� �������������� �� ��� Y
	public double width; // ������ ��������������
	public double height; // ������ ��������������
	
/**
 * ������� ����� �������������. ��� ���������� ����� ����.
 */
	
	public JRect() {
		left = 0;
		top = 0;
		right = 0;
		bottom = 0;
		
		width = this.right - this.left;
		height = this.bottom - this.top;
	}

	/**
	 * ������� ����� �������� �������������. ���������� ������� ������� ���� �����
	 * ����������� �������� ������ ����.
	 */

	public JRect(double left, double top) {
		this.left = left;
		this.top = top;
		this.right = left;
		this.bottom = top;
		
		width = this.right - this.left;
		height = this.bottom - this.top;
	}

/**
 * ������� ����� �������� �������������. ��� ����������� �������� ��������������
 * ����������, ����� left <= right, top <= bottom.
 * 
 * @param left ���������� �������� ������ ���� �������������� �� ��� X
 * @param top ���������� �������� ������ ���� �������������� �� ��� Y
 * @param right ���������� ������� ������� ���� �������������� �� ��� X
 * @param bottom ���������� ������� ������� ���� �������������� �� ��� Y
 */
	
	public JRect(double left, double top, double right, double bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		
		width = this.right - this.left;
		height = this.bottom - this.top;
	}
	
/**
 * ���������� �������������� ���� ��������������.
 */
	
	public final double centerX() {
		return ((double)right - (double)left) / 2.0;
	}
		
/**
 * ���������� ������������ ���� ��������������.
 */
	
	public final double centerY() {
		return ((double)bottom - (double)top) / 2.0;
	}
	
/**
 * ���������� true, ���� ����� � ����������� (x, y) ��������� ������ ��������������,
 * �.�. left <= x < right � top <= y < bottom. ������ ������������� �� ����� ���������
 * ������ ���� ����� (x, y).
 * 
 * @param x X ���������� ����� ����������� �� ���������� � ��������������
 * @param y Y ���������� ����� ����������� �� ���������� � ��������������
 */
	
	public boolean contains(double x, double y) {
		return (x >= left && x <= right && y >= top && y <= bottom) ? true : false; 
	}
	
	/**
	 * ���������� true, ���� ����������� ������ ���������� �������������� ��������� ������
	 * ��� �� ��� �� ������ ����� ��������������. ������ ������������� �� ����� ��������� ������
	 * ���� ������ �������������.
	 * 
	 * @param left - ����� ������� ������������ ��������������
	 * @param top - ������� ������� ������������ ��������������
	 * @param right - ������ ������� ������������ ��������������
	 * @param bottom - ������ ������� ������������ ��������������
	 */
	
	public boolean contains(double left, double top, double right, double bottom) {
		       // ������ ��������� �� ������� ��������������
		return this.left < this.right && this.top < this.bottom 
			   // ������ ��������� �� �������������� � ��������������
			   && this.left <= left && this.top <= top
			   && this.right >= right && this.bottom >= bottom;
	}
	
	/**
	 * ���������� true, ���� ����������� ������������� ��������� ������
	 * ��� �� ��� �� ������ ����� ��������������. ������ ������������� �� ����� ��������� ������
	 * ���� ������ �������������.
	 * 
	 * @param rect - ����������� �������������
	 */
	
	public boolean contains(JRect rect) {
		return this.left < this.right && this.top < this.bottom 
			   && this.left <= rect.left && this.top <= rect.top
			   && this.right >= rect.right && this.bottom >= rect.bottom;
	}
	
/**
 * ���������� ������ ��������������. ���� top >= bottom, �� ��������� ����� ����
 * �������������.
 */
	
	public final double height() {
		return bottom - top;
	}
	
/**
 * ���������� true, ���� ������������� ������ (left >= right ��� top >= bottom).
 */
	
	public final boolean isEmpty() {
		return (left >= right || top >= bottom) ? true : false;
	}

/**
 * ������ ����������� �������������� ����� ��������.
 * 
 * @param left ���������� �������� ������ ���� �������������� �� ��� X
 * @param top ���������� �������� ������ ���� �������������� �� ��� Y
 * @param right ���������� ������� ������� ���� �������������� �� ��� X
 * @param bottom ���������� ������� ������� ���� �������������� �� ��� Y
 */
	
	public void set(double left, double top, double right, double bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		
		width = this.right - this.left;
		height = this.bottom - this.top;
	}
	
/**
 * ������ ����������� �������������� ����� ��������, ������� ���������� �������������� src.
 * 
 * @param src �������������, ���������� �������� ��������
 */
	
	public void set(JRect src) {
		this.set(src.left, src.top, src.right, src.bottom);
	}
	
/**
 * ������������� ��� ���������� �������������� ������� ����.
 */
	
	public void setEmpty() {
		this.left = 0;
		this.top = 0;
		this.right = 0;
		this.bottom = 0;
		
		width = this.right - this.left;
		height = this.bottom - this.top;
	}
	
/**
 * ���������� ��������� �������� ��������������.
 */
	
	@Override
	public String toString() {
		return "[" + left + ", " + top + " - " + right + ", " + bottom + "]";
	}
		
/**
 * ���������� ������ ��������������, ���� left >= right, �� ��������� ����� ����
 * �������������.
 */
	
	public final double width() {
		return right - left;
	}
	
	/**
	 * ���������� ������� �������������� �� dX ����� ��� X � �� dY ����� ��� Y.
	 * 
	 * @param dX - ����������, �� ������� ������������ ������� �������������� ����� ��� X
	 * @param dY - ����������, �� ������� ������������ ������� �������������� ����� ��� Y
	 */
	
	public void translate(double dX, double dY) {
		double old_left = this.left;
		double new_left = old_left + dX;
		
		if(dX < 0) {
			if(new_left > old_left) {
				if(width >= 0) {
					width += new_left - Double.MIN_VALUE;
				}
				
				new_left = Double.MIN_VALUE;
			}
		} else {
			if(new_left < old_left) {
				if(width >= 0) {
					width += new_left - Double.MAX_VALUE;
					
					if(width < 0) {
						width = Double.MAX_VALUE;
					}
				}
				
				new_left = Double.MAX_VALUE;
			}
		}
		
		this.left = new_left;
		
		double old_top = this.top;
		double new_top = old_top + dY;
		
		if(dY < 0) {
			if(new_top > old_top) {
				if(height >= 0) {
					height += new_top - Double.MIN_VALUE; 
				}
				
				new_top = Double.MIN_VALUE;
			}
		} else {
			if(new_top < old_top) {
				if(height >= 0) {
					height += new_top - Double.MAX_VALUE;
					
					if(height < 0) {
						height = Double.MAX_VALUE;
					}
					
					new_top = Double.MAX_VALUE;
				}
			}
		}
		
		this.top = new_top;
	}
		
	public boolean intersects(double x, double y, double w, double h) {
		if(isEmpty() || w <= 0 || h <= 0) {
			return false;
		}
		
		double x0 = left;
		double y0 = top;
		
		return (x + w > x0 && y + h > y0 && x < x0 + width && y < y0 + height);
	}
	
	/**
	 * ���������� ����� ��������������.
	 * 
	 * @return ����� ��������������
	 */
	
	public JRect getBounds() {
		return new JRect(left, top, right, bottom);
	}
}