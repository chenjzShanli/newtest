package com.example.item;

import java.util.ArrayList;

import com.example.model.NewsModel;
import com.example.util.ParseNewsInfoUtil;


public class NewsInfo {
	
//	JSONObject jNewObj;
	public   String newsFeedFuid = null;
	public   String newsFeedFname = null;
	public   String newsFeedFlogo = null;
	public   String newsFeedNtype = null;
	public   String newsFeedNtypename = null;
	public   String newsFeedTitle = null;
	public   String newsFeedIntro = null;
	public   String newsFeedCtime = null;
	public   String newsFeedStime = null;
	public   String newsFeedThumbnail = null;
	public   String newsFeedNewsid = null;
	public   String newsFeedStar = null;
	public   String newsFeedCnum = null;
	public   String newsFeedUpnum = null;
	public   String newsFeedPrivacy = null;
	public   String newsFeedCommentflag = null;
	public	 String newsFeedLocation = null;
	public   String newsFeedSource = null;
	public   Boolean newsFeedHasUp = false;
	
	/** ��¼ͼƬ��ͼ��newsFeedNewsImages[0]Сͼ��newsFeedNewsImages[1]��ͼ */
	public   String[] newsFeedNewsImages;
	
	ArrayList<LinkInfo> titleList;
	ArrayList<LinkInfo> introList;
	
	
	public NewsInfo clone(NewsInfo info){
		this.newsFeedFuid = info.newsFeedFuid;
		this.newsFeedFname = info.newsFeedFname;
		this.newsFeedFlogo = info.newsFeedFlogo;
		this.newsFeedNtype = info.newsFeedNtype;
		this.newsFeedNtypename = info.newsFeedNtypename;
		this.newsFeedTitle = info.newsFeedTitle;
		this.newsFeedIntro = info.newsFeedIntro;
		this.newsFeedCtime = info.newsFeedCtime;
		this.newsFeedStime = info.newsFeedStime;
		this.newsFeedThumbnail = info.newsFeedThumbnail;
		this.newsFeedNewsid = info.newsFeedNewsid;
		this.newsFeedStar = info.newsFeedStar;
		this.newsFeedCnum = info.newsFeedCnum;
		this.newsFeedUpnum = info.newsFeedUpnum;
		this.newsFeedPrivacy = info.newsFeedPrivacy;
		this.newsFeedCommentflag = info.newsFeedCommentflag;
		this.newsFeedLocation = info.newsFeedLocation;
		this.newsFeedSource = info.newsFeedSource;
		
		if(info.newsFeedNewsImages!=null) {
			this.newsFeedNewsImages = info.newsFeedNewsImages.clone();
		}
		return this;
	}
	
	
	
//	public JSONObject getJSONObj(){
//		return jNewObj;
//	}
//	
//	public void setJSONObj(JSONObject jNewObj){
//		this.jNewObj = jNewObj;
//	}
	
	public ArrayList<LinkInfo> getTitleList(){
		return titleList;
	}
	
	public void setTitleList(ArrayList<LinkInfo> titleList){
		this.titleList = titleList;
	}
	/**���TitleList������ֱ�ӷ��أ���������TitleList*/
	public ArrayList<LinkInfo> makeTitleList(String strInfo){
		if(titleList == null){
		    titleList = ParseNewsInfoUtil.parseNewsLinkString(strInfo);
		}
		return titleList;
	}
	
	public ArrayList<LinkInfo> getIntroList(){
		return introList;
	}
	
	public void setIntroList(ArrayList<LinkInfo> introList){
		this.introList = introList;
	}
	/**���IntroList������ֱ�ӷ��أ���������IntroList*/
	public ArrayList<LinkInfo> makeIntroList(String strInfo){
		if(introList == null){
			introList = ParseNewsInfoUtil.parseNewsLinkString(strInfo);
		}
		return introList;
	}
	
	public ArrayList<LinkInfo> makeDiaryIntroList(String strInfo){
		if(introList == null){
			introList = ParseNewsInfoUtil.parseDiaryLinkString(strInfo);
		}
		return introList;
	}
}
