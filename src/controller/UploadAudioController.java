package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.UUID;

import model.TempVideo;
import model.dao.TempVideoDAO;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import tools.CommonValidationTools;
import tools.Constant;
import tools.UploadResponseMessage;

import com.google.gson.Gson;

/**
 * @Title: UploadAudioController
 * @Description: control audio upload 
 * @Company: ZhongHe
 * @author ben
 * @date 2013年1月9日
 */
@Controller
public class UploadAudioController {
    /**
     * @Description: 上传音乐
     * @param fileFromForm
     * @return
     */
	@RequestMapping(value = "/audio", method = RequestMethod.POST)
	@ResponseBody
	 public String audioUpload(
        @RequestParam("file") MultipartFile fileFromForm) {
		UploadResponseMessage responseMessage = new UploadResponseMessage();
		Gson gson = new Gson();
		if (!fileFromForm.isEmpty()) {
			try {
				InputStream inputStream = fileFromForm.getInputStream();
				if (!CommonValidationTools.checkAudioType(fileFromForm)) {
					responseMessage.setStatus(false);
					responseMessage.setMessage("请选择mp3格式音频！");
				}
				else {
					String audioType = fileFromForm.getContentType().replace("audio/", "");					
					String relativePathID = saveAudioFile(inputStream, audioType);
					if (relativePathID != "") {                  
						ApplicationContext context = 
								new ClassPathXmlApplicationContext("All-Modules.xml");
						TempVideoDAO tempVideoDao = (TempVideoDAO) context.getBean("TempVideoDAO");
						((ConfigurableApplicationContext)context).close();
						
						TempVideo video = new TempVideo();
						video.setVideoPath(relativePathID);
						video.setCreateDate(new Timestamp(System.currentTimeMillis()));
						tempVideoDao.insertVideoTempRecord(video);
						
						responseMessage.setStatus(true);
						responseMessage.setMessage("上传成功！");
						responseMessage.setLink(relativePathID);
					}
					else {
						responseMessage.setStatus(false);
						responseMessage.setMessage("文件保存失败，请重新上传");					
					}
				}
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
	 * @title: saveAudioFile
	 * @description: 把音频文件存储到服务器上
	 * @param input
	 * @param audioType
	 * @return
	 */
	private String saveAudioFile(InputStream input, String audioType){
		try {
			String audioID = generateRandomImageID();
			String saveInDataBase =  Constant.MEDIA_DATABASE_PATH + audioID;
			
			//save to server
			String classPath = this.getClass().getClassLoader().getResource("/").getPath();
			String savePath = classPath.replaceAll("/WEB-INF/classes/", Constant.MEDIA_NORMAL_PATH);
			File file = new File(savePath + audioID + "." + audioType);
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
     * @title: generateRandomImageID
     * @description: 生成文件名
     * @return
     */
    private static String generateRandomImageID() {    	
        String randomImageID = UUID.randomUUID().toString().replace("-", "");
        return randomImageID;
    }
}
