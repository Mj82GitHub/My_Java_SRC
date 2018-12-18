/*
 * Copyright (c) 12.2016
 */

package mj82.Converter;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import OsmConverter.OsmConverter;

/**
 * Класс парсит hml файл osm карты и выводит статистическую информацию: кол-во точек, линий и др.
 * 
 * @author Mikhail Kushnerov (mj82)
 */

public class CheckNameLength extends DefaultHandler {

	static int cnt = 0; // Кол-во точек
	static int way_cnt = 0; // Кол-во линий
	static int relation_cnt = 0; // Кол-во отношений
	static int max_byte_size = 0; // Максимальная длина строки в байтах
	static int max_str_size = 0; // Максимальная длина вышеуказанной строки в символах	
	static long max_id = 0; // ID вышеуказанной строки
	private long tmp_id = -1; // Ключ тега
	private String name = "none"; // Текст описания объекта
		
	// Теги
	private boolean isNode = false; // Точка
	private boolean isWay = false; // Линия
	private boolean isRelation = false; // Отношение
	// Атрибуты тегов
	private boolean isName = false; // Название объекта
	
	private String tmp_str_v = ""; // Значение тега
	
	private File file; // Путь к файлу карты
	
	public CheckNameLength(File file) {
		this.file = file;
	}
	
	// Вход в тег
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes attr) throws SAXException {
		// Атрибуты тега
		for(int i = 0; i < attr.getLength(); i++) {
			// Точка
			if(qName.equals("node")) {
				if(!isNode)
					isNode = true; // Мы в теге точки
				
				// Идентификатор точки
				if(attr.getQName(i).equals("id")) {	
					cnt++;
					tmp_id = Long.valueOf(attr.getValue(i));
				}
			}
			
			// Тег tag в составе тега точки
			if(qName.equals("tag") && isNode) {
				// Ключ
				if(attr.getQName(i).equals("k")) {
					// Название ключа
					if(attr.getValue(i).equals("name") || attr.getValue(i).equals("name:ru")) {
						isName = true;
					}
				}
				
				// Значение ключа
				if(attr.getQName(i).equals("v")) {
					// Название значения ключа
					if(isName) {
						tmp_str_v = attr.getValue(i);
						
                       // Ищем самую длинную строку описания в файле карты
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
			
			// Линия
			if(qName.equals("way")) {
				if(!isWay) {
					isWay = true; // Мы в теге линии
				}
				
				// Идентификатор линии
				if(attr.getQName(i).equals("id")) {	
					way_cnt++;
					tmp_id = Long.valueOf(attr.getValue(i));
				}
			}
			
			// Тег tag в составе тега линии
			if(qName.equals("tag") && isWay) {
				// Ключ
				if(attr.getQName(i).equals("k")) {
					// Название ключа
					if(attr.getValue(i).equals("name") || attr.getValue(i).equals("name:ru")) {
						isName = true;
					}
				}
				
				// Значение ключа
				if(attr.getQName(i).equals("v")) {
					// Название значения ключа
					if(isName) {
						tmp_str_v = attr.getValue(i);
						
						// Ищем самую длинную строку описания в файле карты
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
			
			// Отношение
			if(qName.equals("relation")) {
				if(!isRelation)
					isRelation = true; // Мы в теге отношения
				
				// Идентификатор отношения
				if(attr.getQName(i).equals("id")) {	
					relation_cnt++;
					tmp_id = Long.valueOf(attr.getValue(i));
				}
			}
						
			// Тег tag в составе тега точки
			if(qName.equals("tag") && isRelation) {
				// Ключ
				if(attr.getQName(i).equals("k")) {
					// Название ключа
					if(attr.getValue(i).equals("name") || attr.getValue(i).equals("name:ru")) {
						isName = true;
					}
				}
				
				// Значение ключа
				if(attr.getQName(i).equals("v")) {
					// Название значения ключа
					if(isName) {
						tmp_str_v = attr.getValue(i);
						
                       // Ищем самую длинную строку описания в файле карты
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
		
	// Выход из тега
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		// Точка
		if(qName.equals("node")) {
			if(isNode)
				isNode = false; // Мы не в теге точки
		}
		
		// Линия
		if(qName.equals("way")) {
			if(isWay)
				isWay = false; // Мы не в теге точки
		}
		
		// Отношение
		if(qName.equals("relation")) {
			if(isRelation)
				isRelation = false; // Мы не в теге точки
		}
		
		// Последний тег в файле карты
		if(qName.equals("osm")) {
			OsmConverter.printLog("Количество точек: " + cnt);
			OsmConverter.printLog("Количество линий: " + way_cnt);
			OsmConverter.printLog("Количество отношений: " + relation_cnt);
			OsmConverter.printLog("Наибольшее описание элемента в исходном файле:");
			OsmConverter.printLog("   ID: " + max_id);
			OsmConverter.printLog("   Размер строки (символы): " + max_str_size);
			OsmConverter.printLog("   Размер строки (байты): " + max_byte_size);
			OsmConverter.printLog("   Текст: " + name);
			OsmConverter.printLog("Размер исходного файла (байты): " + file.length());	
			OsmConverter.printLog("------------------------------\r\n");
		}
	}
}
