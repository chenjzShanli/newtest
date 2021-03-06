package com.example.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.item.LinkInfo;
import com.example.model.MessageFaceModel;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;


public class ParseNewsInfoUtil {
	
	public static String getReplistString(JSONArray jsonList){
    	String strReplist = null;
    	StringBuffer sbuf = new StringBuffer();
    	try{
    		int size = jsonList.length();
    		if(size == 0){
    			return null;
    		}
    		for(int i = 0; i < size; i++){
    			JSONObject jobj = jsonList.getJSONObject(i);
    			sbuf.append(jobj.getString("title"));
    			if(i != size - 1){
    			   sbuf.append('\n');
    			}
    		}
    	}catch(Exception ex){
    		Log.e("NewsModel", "getReplistString", ex);
    	}finally{
    		strReplist = sbuf.toString();
    	}
    	return strReplist;
    }
	
	private static Pattern EMAIL_PATTERN = Patterns.EMAIL_ADDRESS;
	private static Pattern PHONE_PATTERN = Patterns.PHONE;
	private static Pattern WEBURL_PATTERN = Patterns.WEB_URL;
	
	public static ArrayList<LinkInfo> parseStr(String strLink) {
		if(TextUtils.isEmpty(strLink)){
    		return null;
    	}
		ArrayList<LinkInfo> resultList = new ArrayList<LinkInfo>();
    	ArrayList<LinkInfo> infoList = null;
    	try{
    		infoList = new ArrayList<LinkInfo>();
    		Matcher matcher = EMAIL_PATTERN.matcher(strLink);
    		int begin = 0;
    		while(matcher.find()) {
    			int start = matcher.start();
    			int end = matcher.end();
        		LinkInfo info = new LinkInfo();
        		info.setStartIndex(start);
        		info.setEndIndex(end);
            	info.setContent(matcher.group());
            	info.setType(LinkInfo.EMAIL);
        		infoList.add(info);
    		}
    		
    		Matcher matcher1 = PHONE_PATTERN.matcher(strLink);
    		while(matcher1.find()) {
    			int start = matcher1.start();
    			int end = matcher1.end();
        		LinkInfo info = new LinkInfo();
        		info.setStartIndex(start);
        		info.setEndIndex(end);
            	info.setContent(matcher1.group());
            	info.setType(LinkInfo.PHONENUMBER);
        		infoList.add(info);
    		}
    		
    		//(#大笑)
    		Pattern pattern = Pattern.compile("(\\(#\\S{1,2}\\))");
    	    Matcher matcher2 = pattern.matcher(strLink);
    		 while(matcher2.find()) {
    			int start = matcher2.start();
    			int end = matcher2.end();
    			System.out.println("====start="+start+"end="+end+"match group="+matcher2.group());
        		LinkInfo info = new LinkInfo();
        		info.setStartIndex(start);
        		info.setEndIndex(end);
            	info.setContent(matcher2.group());
            	info.setFace(true);
        		infoList.add(info);
    		}
    		
    		Collections.sort(infoList);
    		int last = 0;
    		for(int i=0;i<infoList.size();i++) {
    			LinkInfo info = infoList.get(i);
    			if(begin != info.getStartIndex()){
    				LinkInfo infoBefore = new LinkInfo();
        			infoBefore.setContent(strLink.substring(begin,info.getStartIndex()));
        			resultList.add(infoBefore);
    			}
    			resultList.add(info);
    			begin = info.getEndIndex();
    			last = info.getEndIndex();
    		}
    		if(last < strLink.length()) {
    			LinkInfo info = new LinkInfo();
        		info.setContent(strLink.substring(last,strLink.length()));
        		resultList.add(info);
    		}
    		
    		
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return resultList;
	}
	
	 //解析Intro
    public static ArrayList<LinkInfo> parseNewsLinkString(String strLink){
    	if(TextUtils.isEmpty(strLink)){
    		return null;
    	}
    	ArrayList<LinkInfo> infoList = null;
    	try{
    	infoList = new ArrayList<LinkInfo>();
    	TreeMap<Integer, String> faceMap = new TreeMap<Integer, String>();
    	
    	int startIndex = strLink.indexOf("[|s|]");
    	int len = strLink.length();
    	int curIndex = 0;
    	if(startIndex > 0){//第一个标签之前有普通文本
    		String str = strLink.substring(0, startIndex);
    		addCommentInfo(infoList, str, faceMap, 0);
    	}
    	//link的形式 [|s|]aasfsfafsdsdf[|m|]dsfsdfsdf[|m|]dfsdf[|e|]
    	while(startIndex >= 0){
    		int min1Index = -1;
    		int min2Index = -1;
    		int endIndex = -1;
    		min1Index = strLink.indexOf("[|m|]", startIndex + 5);
    		if(min1Index >= 0){
    		    min2Index = strLink.indexOf("[|m|]", min1Index + 5);
    		}
    		if(min2Index >= 0){
    		    endIndex = strLink.indexOf("[|e|]", min2Index + 5);
    		}
    		if(min1Index >= 0 && min2Index >= 0 && endIndex >= 0){
    			//找到了一个完整的Link对象
    			LinkInfo info = new LinkInfo();
    			String str = strLink.substring(startIndex + 5, min1Index);
    			info.setContent(str);
    			str = strLink.substring(min1Index + 5, min2Index);
    			info.setId(str);
    			str = strLink.substring(min2Index + 5, endIndex);
    			info.setType(str);
    			infoList.add(info);
    			//curIndex为当前Link对象之后的第一个字符的位置
    			curIndex = endIndex + 5;
    			startIndex = strLink.indexOf("[|s|]", curIndex);
    			if(startIndex >= 0 && curIndex != startIndex){
    				//两个Link对象之间有文本
    				String strMid = strLink.substring(curIndex, startIndex);
    				addCommentInfo(infoList, strMid, faceMap, curIndex);
    				//addNewInfo(infoList, strMid);
    				curIndex = startIndex;
    			}
    		}
    		else{//没找到完整的对象，则把从[|s|]开始到下一个[|s|]中的内容当做普通文本
    			int tmpIndex = strLink.indexOf("[|s|]", startIndex + 5);
    			String str = "";
    			if(tmpIndex >= 0){
    				str = strLink.substring(startIndex, tmpIndex);
    			}
    			else{
    				str = strLink.substring(startIndex);
    			}
    			addCommentInfo(infoList, str, faceMap, startIndex);
    			//addNewInfo(infoList, str);
    			startIndex = tmpIndex;
    			//字符串已经扫描完，curIndex为字符串结尾
    			curIndex = len;
    		}
    	}
    	if(curIndex < len){//标签之后还有普通文本需要处理
    		String str = strLink.substring(curIndex);
    		addCommentInfo(infoList, str, faceMap, curIndex);
    	}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return infoList;
    }
    
  //解析Intro
    public static ArrayList<LinkInfo> parseDiaryLinkString(String strLink){
    	if(TextUtils.isEmpty(strLink)){
    		return null;
    	}
    	ArrayList<LinkInfo> infoList = null;
    	try{
    	infoList = new ArrayList<LinkInfo>();
    	TreeMap<Integer, String> faceMap = new TreeMap<Integer, String>();
    	parseDiaryFaceString(infoList, strLink, faceMap);
    	
    	int startIndex = strLink.indexOf("[|s|]");
    	int len = strLink.length();
    	int curIndex = 0;
    	if(startIndex > 0){//第一个标签之前有普通文本
    		String str = strLink.substring(0, startIndex);
    		addCommentInfo(infoList, str, faceMap, 0);
    	}
    	while(startIndex >= 0){
    		int min1Index = -1;
    		int min2Index = -1;
    		int endIndex = -1;
    		min1Index = strLink.indexOf("[|m|]", startIndex + 5);
    		if(min1Index >= 0){
    		    min2Index = strLink.indexOf("[|m|]", min1Index + 5);
    		}
    		if(min2Index >= 0){
    		    endIndex = strLink.indexOf("[|e|]", min2Index + 5);
    		}
    		if(min1Index >= 0 && min2Index >= 0 && endIndex >= 0){
    			//找到了一个完整的Link对象
    			LinkInfo info = new LinkInfo();
    			String str = strLink.substring(startIndex + 5, min1Index);
    			info.setContent(str);
    			str = strLink.substring(min1Index + 5, min2Index);
    			info.setId(str);
    			str = strLink.substring(min2Index + 5, endIndex);
    			info.setType(str);
    			infoList.add(info);
    			//curIndex为当前Link对象之后的第一个字符的位置
    			curIndex = endIndex + 5;
    			startIndex = strLink.indexOf("[|s|]", curIndex);
    			if(startIndex >= 0 && curIndex != startIndex){
    				//两个Link对象之间有文本
    				String strMid = strLink.substring(curIndex, startIndex);
    				addCommentInfo(infoList, strMid, faceMap, curIndex);
    				//addNewInfo(infoList, strMid);
    				curIndex = startIndex;
    			}
    		}
    		else{//没找到完整的对象，则把从[|s|]开始到下一个[|s|]中的内容当做普通文本
    			int tmpIndex = strLink.indexOf("[|s|]", startIndex + 5);
    			String str = "";
    			if(tmpIndex >= 0){
    				str = strLink.substring(startIndex, tmpIndex);
    			}
    			else{
    				str = strLink.substring(startIndex);
    			}
    			addCommentInfo(infoList, str, faceMap, startIndex);
    			//addNewInfo(infoList, str);
    			startIndex = tmpIndex;
    			//字符串已经扫描完，curIndex为字符串结尾
    			curIndex = len;
    		}
    	}
    	if(curIndex < len){//标签之后还有普通文本需要处理
    		String str = strLink.substring(curIndex);
    		addCommentInfo(infoList, str, faceMap, curIndex);
    	}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return infoList;
    }
    
    
    private static void parseDiaryFaceString(ArrayList<LinkInfo> infoList, String str, TreeMap<Integer, String> faceMap){
    	if(TextUtils.isEmpty(str)){
    		return;
    	}
    	ArrayList<String> faceList = MessageFaceModel.getInstance().getFaceStrings();
    	int len = faceList.size();
    	int startIndex = 0;

    	for(int i = 0; i < len; i++){
    		String strFace = faceList.get(i);
    		startIndex = str.indexOf(strFace);
    		while(startIndex >= 0){
    			int endIndex = startIndex + strFace.length();//最后一个字符的索引
    			faceMap.put(startIndex, strFace);
    			startIndex = str.indexOf(strFace, endIndex);
    		}
    	}
    }
    //int startIndex = strLink.indexOf("[|s|]");
    //String str = strLink.substring(0, startIndex);
    //keys里存储的是表情在返回串的位置
    private static void addCommentInfo(ArrayList<LinkInfo> infoList, String str, TreeMap<Integer, String> faceMap, int index){
    	boolean hasFace = false;
    	int startIndex = index;
    	Set<Integer> keys = faceMap.keySet();
    	//@SuppressWarnings("rawtypes")
		Iterator iterator = keys.iterator();
    	while(iterator.hasNext()){
    		//iKey表示的是face的第一个位置
    		int iKey = ((Integer)iterator.next()).intValue();
    		if(iKey < startIndex){//还没到当前的字符串
    			continue;
    		}
    		if(iKey > index + str.length()){
    			break;//已经超过当前字符串了
    		}
    		int current = iKey - startIndex;
    		if(current > 0){
    			int tmp = startIndex - index;
    			String first = str.substring(tmp, tmp + current);
    			addNewInfo(infoList, first);
    		}
    		hasFace = true;
    		String strFace = faceMap.get(iKey);
    		//String strFace = faceInfo.getFace();
    		LinkInfo info = new LinkInfo();
    		info.setFace(true);
        	info.setContent(strFace);
    		infoList.add(info);
    		startIndex = iKey + strFace.length();
    	}
    	if(hasFace){
    		if(startIndex < index + str.length()){
    			//表情之后还有剩余的普通文本
    			int current = startIndex - index;
    			String strEnd = str.substring(current);
    			addNewInfo(infoList, strEnd);
    		}
    	}
    	else{
    		addNewInfo(infoList, str);
    	}
    }
    
    private static void addNewInfo(ArrayList<LinkInfo> infoList, String str){
    	LinkInfo info = new LinkInfo();
    	info.setContent(str);
		infoList.add(info);
    }
    
}
