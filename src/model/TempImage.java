package model;

import java.sql.Timestamp;

/**
 * @Title: TempImage
 * @Description: record image info to temporary table 
 * @Company: ZhongHe
 * @author ben
 * @date 2013年1月9日
 */
public class TempImage {
	private String imagePath;
	private Timestamp createDate;
	
	/**
	 * @return the imagePath
	 */
	public String getImagePath() {
		return imagePath;
	}
	/**
	 * @param imagePath the imagePath to set
	 */
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
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
