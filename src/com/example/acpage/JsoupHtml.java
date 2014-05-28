package com.example.acpage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

public class JsoupHtml {
	public static Elements getItems(String HTML,String ELEMENT){
		Document doc  = Jsoup.parse(HTML);
		Elements itemsE = doc.select(ELEMENT);
		//String titles = titlesE.text();
		return itemsE;
	}
	public static String getTitle(Element itemE){
		String itemS = itemE.toString();
		Document itemD = Jsoup.parse(itemS);
		
		Element titleE = itemD.select(".title").first();
		String titleS = titleE.text();
		return titleS;
	}
	public static String getIntro(Element itemE){
		String itemS = itemE.toString();
		Document itemD = Jsoup.parse(itemS);
		
		Element titleE = itemD.select(".desc").first();
		String titleS = titleE.text();
		return titleS;
	}
	
	public static String getInfo(Element itemE){
		String itemS = itemE.toString();
		Document itemD = Jsoup.parse(itemS);
		
		Element titleE = itemD.select(".article-info").first();
		String titleS = titleE.text();
		return titleS;
	}
	public static String getUrl(Element itemE){
		String itemS = itemE.toString();
		Document itemD = Jsoup.parse(itemS);
		
		Element titleE = itemD.select(".title").first();
		String titleS = titleE.attr("href");
		return titleS;
	}
	public static String getArticle(Element articleE){
		String itemS = articleE.toString();
		//Document itemD = Jsoup.parse(itemS);
		
		//Element titleE = itemD.select(".title-info").first();
		//String titleS = titleE.text();
		return itemS;
	}
	
	
}
