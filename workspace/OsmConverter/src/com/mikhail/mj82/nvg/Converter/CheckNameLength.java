/*
 * Copyright (c) 12.2016
 */

package com.mikhail.mj82.nvg.Converter;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import OsmConverter.OsmConverter;

/**
 * ����� ������ hml ���� osm ����� � ������� �������������� ����������: ���-�� �����, ����� � ��.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class CheckNameLength extends DefaultHandler {

	static int cnt = 0; // ���-�� �����
	static int way_cnt = 0; // ���-�� �����
	static int relation_cnt = 0; // ���-�� ���������
	static int max_byte_size = 0; // ������������ ����� ������ � ������
	static int max_str_size = 0; // ������������ ����� ������������� ������ � ��������	
	static long max_id = 0; // ID ������������� ������
	private long tmp_id = -1; // ���� ����
	private String name = "none"; // ����� �������� �������
		
	// ����
	private boolean isNode = false; // �����
	private boolean isWay = false; // �����
	private boolean isRelation = false; // ���������
	// �������� �����
	private boolean isName = false; // �������� �������
	
	private String tmp_str_v = ""; // �������� ����
	
	private File file; // ���� � ����� �����
	
	public CheckNameLength(File file) {
		this.file = file;
	}
	
	// ���� � ���
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes attr) throws SAXException {
		// �������� ����
		for(int i = 0; i < attr.getLength(); i++) {
			// �����
			if(qName.equals("node")) {
				if(!isNode)
					isNode = true; // �� � ���� �����
				
				// ������������� �����
				if(attr.getQName(i).equals("id")) {	
					cnt++;
					tmp_id = Long.valueOf(attr.getValue(i));
				}
			}
			
			// ��� tag � ������� ���� �����
			if(qName.equals("tag") && isNode) {
				// ����
				if(attr.getQName(i).equals("k")) {
					// �������� �����
					if(attr.getValue(i).equals("name") || attr.getValue(i).equals("name:ru")) {
						isName = true;
					}
				}
				
				// �������� �����
				if(attr.getQName(i).equals("v")) {
					// �������� �������� �����
					if(isName) {
						tmp_str_v = attr.getValue(i);
						
                       // ���� ����� ������� ������ �������� � ����� �����
						try {
							if(tmp_str_v.getBytes("UTF-8").length >= max_byte_size) {
								max_str_size = tmp_str_v.length();
								max_byte_size = tmp_str_v.getBytes("UTF-8").length;
								max_id = tmp_id;
								name = tmp_str_v;
							}
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						
						tmp_str_v = "";
						isName = false;
					}
				}
			}
			
			// �����
			if(qName.equals("way")) {
				if(!isWay) {
					isWay = true; // �� � ���� �����
				}
				
				// ������������� �����
				if(attr.getQName(i).equals("id")) {	
					way_cnt++;
					tmp_id = Long.valueOf(attr.getValue(i));
				}
			}
			
			// ��� tag � ������� ���� �����
			if(qName.equals("tag") && isWay) {
				// ����
				if(attr.getQName(i).equals("k")) {
					// �������� �����
					if(attr.getValue(i).equals("name") || attr.getValue(i).equals("name:ru")) {
						isName = true;
					}
				}
				
				// �������� �����
				if(attr.getQName(i).equals("v")) {
					// �������� �������� �����
					if(isName) {
						tmp_str_v = attr.getValue(i);
						
						// ���� ����� ������� ������ �������� � ����� �����
						try {
							if(tmp_str_v.getBytes("UTF-8").length >= max_byte_size) {
								max_str_size = tmp_str_v.length();
								max_byte_size = tmp_str_v.getBytes("UTF-8").length;
								max_id = tmp_id;
								name = tmp_str_v;
							}
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						
						tmp_str_v = "";
						isName = false;
					}
				}
			}
			
			// ���������
			if(qName.equals("relation")) {
				if(!isRelation)
					isRelation = true; // �� � ���� ���������
				
				// ������������� ���������
				if(attr.getQName(i).equals("id")) {	
					relation_cnt++;
					tmp_id = Long.valueOf(attr.getValue(i));
				}
			}
						
			// ��� tag � ������� ���� �����
			if(qName.equals("tag") && isRelation) {
				// ����
				if(attr.getQName(i).equals("k")) {
					// �������� �����
					if(attr.getValue(i).equals("name") || attr.getValue(i).equals("name:ru")) {
						isName = true;
					}
				}
				
				// �������� �����
				if(attr.getQName(i).equals("v")) {
					// �������� �������� �����
					if(isName) {
						tmp_str_v = attr.getValue(i);
						
                       // ���� ����� ������� ������ �������� � ����� �����
						try {
							if(tmp_str_v.getBytes("UTF-8").length >= max_byte_size) {
								max_str_size = tmp_str_v.length();
								max_byte_size = tmp_str_v.getBytes("UTF-8").length;
								max_id = tmp_id;
								name = tmp_str_v;
							}
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						
						tmp_str_v = "";
						isName = false;
					}
				}
			}
		}
	}
		
	// ����� �� ����
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		// �����
		if(qName.equals("node")) {
			if(isNode)
				isNode = false; // �� �� � ���� �����
		}
		
		// �����
		if(qName.equals("way")) {
			if(isWay)
				isWay = false; // �� �� � ���� �����
		}
		
		// ���������
		if(qName.equals("relation")) {
			if(isRelation)
				isRelation = false; // �� �� � ���� �����
		}
		
		// ��������� ��� � ����� �����
		if(qName.equals("osm")) {
			OsmConverter.printLog("���������� �����: " + cnt);
			OsmConverter.printLog("���������� �����: " + way_cnt);
			OsmConverter.printLog("���������� ���������: " + relation_cnt);
			OsmConverter.printLog("���������� �������� �������� � �������� �����:");
			OsmConverter.printLog("   ID: " + max_id);
			OsmConverter.printLog("   ������ ������ (�������): " + max_str_size);
			OsmConverter.printLog("   ������ ������ (�����): " + max_byte_size);
			OsmConverter.printLog("   �����: " + name);
			OsmConverter.printLog("������ ��������� ����� (�����): " + file.length());	
			OsmConverter.printLog("------------------------------\r\n");
		}
	}
}
