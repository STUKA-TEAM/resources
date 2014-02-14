package model.dao;

import javax.sql.DataSource;

import model.TempVideo;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @Title: TempVideoDAO
 * @Description: DAO for tempVideo model
 * @Company: ZhongHe
 * @author ben
 * @date 2013年1月9日
 */
public class TempVideoDAO {
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	//insert
	/**
	 * @title: insertImageTempRecord
	 * @description: 将要删除视频的信息存入临时表
	 * @param video
	 * @return
	 */
	public int insertVideoTempRecord(TempVideo video){
		int result = 0;
		String SQL = "INSERT INTO video_temp_record (id, videoPath, createDate) VALUES (default, ?, ?)";
		
		result = jdbcTemplate.update(SQL, video.getVideoPath(), video.getCreateDate());
		
		return result <= 0 ? 0 : result;
	}
}
