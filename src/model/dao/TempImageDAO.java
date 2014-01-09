package model.dao;

import javax.sql.DataSource;

import model.TempImage;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @Title: TempImageDAO
 * @Description: DAO for tempImage model
 * @Company: ZhongHe
 * @author ben
 * @date 2013年1月9日
 */
public class TempImageDAO {
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	//insert
	/**
	 * @title: insertImageTempRecord
	 * @description: 将要删除图片的信息存入临时表
	 * @param image
	 * @return
	 */
	public int insertImageTempRecord(TempImage image){
		int result = 0;
		String SQL = "INSERT INTO image_temp_record VALUES (default, ?, ?)";
		
		result = jdbcTemplate.update(SQL, image.getImagePath(), image.getCreateDate());
		
		return result <= 0 ? 0 : result;
	}
}
