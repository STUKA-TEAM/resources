package controller;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import tools.CommonValidationTools;
import tools.ImageUtil;
import tools.UploadResponseMessage;

import com.google.gson.Gson;

/**
 * @Title: UploadImageController
 * @Description: control image upload 
 * @Company: ZhongHe
 * @author ben
 * @date 2013年12月20日
 */
@Controller
public class UploadImageController {
    /**
     * @Description: 上传图片存储为三种大小尺寸	
     * @param fileFromForm
     * @return
     */
	@RequestMapping(value = "/image/multi", method = RequestMethod.POST)
	@ResponseBody
	 public String multiImageUpload(@RequestParam("file") MultipartFile fileFromForm) {
		UploadResponseMessage responseMessage = new UploadResponseMessage();
		  Gson gson = new Gson();
		  if (!fileFromForm.isEmpty()) {
              try {
				InputStream inputStream = fileFromForm.getInputStream();		
				CommonValidationTools commonValidationTools = new CommonValidationTools();	
				if (!commonValidationTools.checkImageSize(fileFromForm)) {
					responseMessage.setStatus(false);
					responseMessage.setMessage("文件大小超过限制！");
				}
				else {
					ImageUtil imageUtil = new ImageUtil();
					String relativePathID = imageUtil.saveMutiSize(inputStream);
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
	 * @Description: 上传图片仅存储为原始大小尺寸
	 * @param fileFromForm
	 * @return
	 */
	@RequestMapping(value = "/image/original", method = RequestMethod.POST)
	@ResponseBody
	 public String originalImageUpload(
        @RequestParam("file") MultipartFile fileFromForm) {
		UploadResponseMessage responseMessage = new UploadResponseMessage();
		  Gson gson = new Gson();
		  if (!fileFromForm.isEmpty()) {
              try {
				InputStream inputStream = fileFromForm.getInputStream();		
				CommonValidationTools commonValidationTools = new CommonValidationTools();
	
				if (!commonValidationTools.checkImageSize(fileFromForm)) {
					responseMessage.setStatus(false);
					responseMessage.setMessage("文件大小超过限制！");
					return gson.toJson(responseMessage);
				}else {
					ImageUtil imageUtil = new ImageUtil();
					String relativePathID = imageUtil.saveOriginalSize(inputStream);
					responseMessage.setStatus(true);
					responseMessage.setMessage("上传成功！");
					responseMessage.setLink(relativePathID);					
					return gson.toJson(responseMessage);
				}
				
			} catch (IOException e) {
				responseMessage.setStatus(false);
				responseMessage.setMessage("上传失败！");
				return gson.toJson(responseMessage);
			}                  
		  }else {
              responseMessage.setStatus(false);
			  responseMessage.setMessage("请选择文件！");
			  return gson.toJson(responseMessage); 
		}
    }
	
	/**
	 * @Description: 上传图片仅存储为正方形大小尺寸 specially for elove
	 * @param fileFromForm
	 * @return
	 */
	@RequestMapping(value = "/image/square", method = RequestMethod.POST)
	@ResponseBody
	 public String squareImageUpload(
        @RequestParam("file") MultipartFile fileFromForm) {
		UploadResponseMessage responseMessage = new UploadResponseMessage();
		  Gson gson = new Gson();
		  if (!fileFromForm.isEmpty()) {
              try {
				InputStream inputStream = fileFromForm.getInputStream();		
				CommonValidationTools commonValidationTools = new CommonValidationTools();
	
				if (!commonValidationTools.checkImageSize(fileFromForm)) {
					responseMessage.setStatus(false);
					responseMessage.setMessage("文件大小超过限制！");
					return gson.toJson(responseMessage);
				}else {
					ImageUtil imageUtil = new ImageUtil();
					String relativePathID = imageUtil.saveSquareSize(inputStream);
					responseMessage.setStatus(true);
					responseMessage.setMessage("上传成功！");
					responseMessage.setLink(relativePathID);					
					return gson.toJson(responseMessage);
				}
				
			} catch (IOException e) {
				responseMessage.setStatus(false);
				responseMessage.setMessage("上传失败！");
				return gson.toJson(responseMessage);
			}                  
		  }else {
              responseMessage.setStatus(false);
			  responseMessage.setMessage("请选择文件！");
			  return gson.toJson(responseMessage); 
		}
    }
	
}
