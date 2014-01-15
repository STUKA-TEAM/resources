package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import model.TempVideo;
import model.dao.TempVideoDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import threads.VideoConvert;
import tools.Constant;
import tools.UploadResponseMessage;

import com.google.gson.Gson;

/**
 * @Title: UploadVideoController
 * @Description: control video upload 
 * @Company: ZhongHe
 * @author ben
 * @date 2013年12月29日
 */
@Controller
@RequestMapping(value = "/video")
public class UploadVideoController {
	ExecutorService threadPool = Executors.newFixedThreadPool(1);	
	
    /**
     * @Description: 上传视频	
     * @param fileFromForm
     * @return
     */	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	 public String videoUpload(
        @RequestParam("file") MultipartFile fileFromForm) {			
		UploadResponseMessage responseMessage = new UploadResponseMessage();
		Gson gson = new Gson();
		if (!fileFromForm.isEmpty()) {
			try {
				InputStream inputStream = fileFromForm.getInputStream();
				/*CommonValidationTools commonValidationTools = new CommonValidationTools();
				if (!commonValidationTools.checkVideoType(fileFromForm)) {
					responseMessage.setStatus(false);
					responseMessage.setMessage("请选择mp4或webm格式视频！");
				}
				else {*/
					String videoType = fileFromForm.getContentType().replace("video/", "");					
					String relativePathID = saveVideoFile(inputStream, videoType);
					if (relativePathID != "") {                  
						ApplicationContext context = 
								new ClassPathXmlApplicationContext("All-Modules.xml");
						TempVideoDAO tempVideoDao = (TempVideoDAO) context.getBean("TempVideoDAO");
						((ConfigurableApplicationContext)context).close();
						
						TempVideo video = new TempVideo();
						video.setVideoPath(relativePathID);
						video.setCreateDate(new Timestamp(System.currentTimeMillis()));
						tempVideoDao.insertVideoTempRecord(video);
						
						String videoPath = getVideoPath(relativePathID).substring(1).replace('/', '\\');
						threadPool.submit(new VideoConvert(videoType, videoPath));
						
					//	threadPool.shutdown();
						responseMessage.setStatus(true);
						responseMessage.setMessage("上传成功！");
						responseMessage.setLink(relativePathID);
					}
					else {
						responseMessage.setStatus(false);
						responseMessage.setMessage("文件保存失败，请重新上传");					
					}
			//	}
			} catch (IOException e) {
				responseMessage.setStatus(false);
				responseMessage.setMessage("上传失败！");
			}
		} 
		else {
			responseMessage.setStatus(false);
			responseMessage.setMessage("请选择文件！");
		}
		return gson.toJson(responseMessage);
    }
	
	/**
	 * @title: saveVideoFile
	 * @description: 把视频文件存储到服务器上
	 * @param input
	 * @param videoType
	 * @return
	 */
	private String saveVideoFile(InputStream input, String videoType){
		try {
			String videoID = generateRandomImageID();
			String saveInDataBase =  Constant.MEDIA_DATABASE_PATH + videoID;
			
			//save to server
			String classPath = this.getClass().getClassLoader().getResource("/").getPath();
			String savePath = classPath.replaceAll("/WEB-INF/classes/", Constant.MEDIA_NORMAL_PATH);
			File file = new File(savePath + videoID + "_temp." + videoType);
			OutputStream out=new FileOutputStream(file);
			int read = 0;
			byte[] bytes = new byte[1024];			 
			while ((read = input.read(bytes)) != -1) {
			 out.write(bytes, 0, read);
			}
			out.close();
			input.close();
			
			return saveInDataBase;
		} catch (IOException e) {
			return "";
		}
	}
	
	/**
	 * @title: getVideoPath
	 * @description: 根据数据库存储地址获取实际可用地址
	 * @param saveInDataBase
	 * @return
	 */
	private String getVideoPath(String saveInDataBase){
		String videoPath = saveInDataBase.replaceAll(Constant.MEDIA_DATABASE_PATH,
				Constant.MEDIA_NORMAL_PATH);
		String classPath = this.getClass().getClassLoader().getResource("/").getPath();
		return classPath.replaceAll("/WEB-INF/classes/", videoPath);
	}
	
    /**
     * @title: generateRandomImageID
     * @description: 生成文件名
     * @return
     */
    private static String generateRandomImageID() {    	
        String randomImageID = UUID.randomUUID().toString().replace("-", "");
        return randomImageID;
    }
}
