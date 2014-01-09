package model;

import java.sql.Timestamp;

/**
 * @Title: TempVideo
 * @Description: record video info to temporary table 
 * @Company: ZhongHe
 * @author ben
 * @date 2013年1月9日
 */
public class TempVideo {
	private String videoPath;
	private Timestamp createDate;
	
	/**
	 * @return the videoPath
	 */
	public String getVideoPath() {
		return videoPath;
	}
	/**
	 * @param videoPath the videoPath to set
	 */
	public void setVideoPath(String videoPath) {
		this.videoPath = videoPath;
	}
	/**
	 * @return the createDate
	 */
	public Timestamp getCreateDate() {
		return createDate;
	}
	/**
	 * @param createDate the createDate to set
	 */
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
}
