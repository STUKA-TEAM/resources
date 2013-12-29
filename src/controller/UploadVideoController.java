package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

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
 * @Title: UploadVideoController
 * @Description: control video upload 
 * @Company: ZhongHe
 * @author ben
 * @date 2013年12月29日
 */
@Controller
public class UploadVideoController {
    /**
     * @Description: 上传视频	
     * @param fileFromForm
     * @return
     */
	@RequestMapping(value = "/video", method = RequestMethod.POST)
	@ResponseBody
	 public String multiImageUpload(
        @RequestParam("file") MultipartFile fileFromForm) {
		UploadResponseMessage responseMessage = new UploadResponseMessage();
		Gson gson = new Gson();
		if (!fileFromForm.isEmpty()) {
			try {
				InputStream inputStream = fileFromForm.getInputStream();
				CommonValidationTools commonValidationTools = new CommonValidationTools();
				if (!commonValidationTools.checkVideoType(fileFromForm)) {
					responseMessage.setStatus(false);
					responseMessage.setMessage("请选择mp4或webm格式视频！");
				}
				else {
					String videoType = fileFromForm.getContentType().replace("video/", "");					
					String relativePathID = saveVideoFile(inputStream, videoType);
					if (relativePathID != "") {
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
	 * @title: saveVideoFile
	 * @description: 把视频文件存储到服务器上
	 * @param input
	 * @param videoType
	 * @return
	 */
	private String saveVideoFile(InputStream input, String videoType){
		try {
			String videoID = generateRandomImageID();
			String saveInDataBase =  Constant.IMAGE_DATABASE_PATH + videoID;
			
			//save to server
			String classPath = this.getClass().getClassLoader().getResource("/").getPath();
			String savePath = classPath.replaceAll("/WEB-INF/classes/", Constant.VIDEO_NORMAL_PATH);
			File file = new File(savePath + videoID + "." + videoType);
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
    private String generateRandomImageID() {    	
        String randomImageID = UUID.randomUUID().toString().replace("-", "");
        return randomImageID;
    }
}
