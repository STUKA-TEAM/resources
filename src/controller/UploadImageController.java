package controller;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;

import model.TempImage;
import model.dao.TempImageDAO;

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
				if (!CommonValidationTools.checkImageSize(fileFromForm)) {
					responseMessage.setStatus(false);
					responseMessage.setMessage("文件大小超过限制！");
				} else {
					ImageUtil imageUtil = new ImageUtil();
					String relativePathID = imageUtil.saveMutiSize(inputStream);
					if (relativePathID != "") {
						ApplicationContext context = 
								new ClassPathXmlApplicationContext("All-Modules.xml");
						TempImageDAO tempImageDao = (TempImageDAO) context.getBean("TempImageDAO");
						((ConfigurableApplicationContext)context).close();
						
						TempImage image = new TempImage();
						image.setImagePath(relativePathID);
						image.setCreateDate(new Timestamp(System.currentTimeMillis()));
						tempImageDao.insertImageTempRecord(image);
						
						responseMessage.setStatus(true);
						responseMessage.setMessage("上传成功！");
						responseMessage.setLink(relativePathID);
					} else {
						responseMessage.setStatus(false);
						responseMessage.setMessage("文件保存失败，请重新上传");					
					}				
				}				
			} catch (IOException e) {
				responseMessage.setStatus(false);
				responseMessage.setMessage("上传失败！");
			}                  
		  } else {
              responseMessage.setStatus(false);
			  responseMessage.setMessage("请选择文件！");
		  }
		  return gson.toJson(responseMessage); 
    }
	
	/**
	 * @Description: 上传图片仅存储为原始大小尺寸，默认为jpg格式
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
				if (!CommonValidationTools.checkImageSize(fileFromForm)) {
					responseMessage.setStatus(false);
					responseMessage.setMessage("文件大小超过限制！");
				} else {
					ImageUtil imageUtil = new ImageUtil();
					String relativePathID = imageUtil.saveOriginalSize(inputStream);
					if (relativePathID != "") {
						ApplicationContext context = 
								new ClassPathXmlApplicationContext("All-Modules.xml");
						TempImageDAO tempImageDao = (TempImageDAO) context.getBean("TempImageDAO");
						((ConfigurableApplicationContext)context).close();
						
						TempImage image = new TempImage();
						image.setImagePath(relativePathID);
						image.setCreateDate(new Timestamp(System.currentTimeMillis()));
						tempImageDao.insertImageTempRecord(image);
						
						responseMessage.setStatus(true);
						responseMessage.setMessage("上传成功！");
						responseMessage.setLink(relativePathID);
					} else {
						responseMessage.setStatus(false);
						responseMessage.setMessage("文件保存失败，请重新上传");	
					}
				}
				
			} catch (IOException e) {
				responseMessage.setStatus(false);
				responseMessage.setMessage("上传失败！");
			}                  
		  } else {
              responseMessage.setStatus(false);
			  responseMessage.setMessage("请选择文件！");
		  }
		  return gson.toJson(responseMessage);   
    }
	
	/**
	 * @Description: 上传图片仅存储为原始大小尺寸且为png格式
	 * @param fileFromForm
	 * @return
	 */
	@RequestMapping(value = "/image/original_png", method = RequestMethod.POST)
	@ResponseBody
	 public String originalImageUploadForPNG(
        @RequestParam("file") MultipartFile fileFromForm) {
		UploadResponseMessage responseMessage = new UploadResponseMessage();
		  Gson gson = new Gson();
		  if (!fileFromForm.isEmpty()) {
              try {
				InputStream inputStream = fileFromForm.getInputStream();			
				if (!CommonValidationTools.checkImageSize(fileFromForm)) {
					responseMessage.setStatus(false);
					responseMessage.setMessage("文件大小超过限制！");
				} else {
					ImageUtil imageUtil = new ImageUtil();
					String relativePathID = imageUtil.saveOriginalSizeForPNG(inputStream);
					if (relativePathID != "") {
						ApplicationContext context = 
								new ClassPathXmlApplicationContext("All-Modules.xml");
						TempImageDAO tempImageDao = (TempImageDAO) context.getBean("TempImageDAO");
						((ConfigurableApplicationContext)context).close();
						
						TempImage image = new TempImage();
						image.setImagePath(relativePathID);
						image.setCreateDate(new Timestamp(System.currentTimeMillis()));
						tempImageDao.insertImageTempRecord(image);
						
						responseMessage.setStatus(true);
						responseMessage.setMessage("上传成功！");
						responseMessage.setLink(relativePathID);
					} else {
						responseMessage.setStatus(false);
						responseMessage.setMessage("文件保存失败，请重新上传");	
					}
				}
				
			} catch (IOException e) {
				responseMessage.setStatus(false);
				responseMessage.setMessage("上传失败！");
			}                  
		  } else {
              responseMessage.setStatus(false);
			  responseMessage.setMessage("请选择文件！");
		  }
		  return gson.toJson(responseMessage);   
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
				if (!CommonValidationTools.checkImageSize(fileFromForm)) {
					responseMessage.setStatus(false);
					responseMessage.setMessage("文件大小超过限制！");
				} else {
					ImageUtil imageUtil = new ImageUtil();
					String relativePathID = imageUtil.saveSquareSize(inputStream);
					if (relativePathID != "") {
						ApplicationContext context = 
								new ClassPathXmlApplicationContext("All-Modules.xml");
						TempImageDAO tempImageDao = (TempImageDAO) context.getBean("TempImageDAO");
						((ConfigurableApplicationContext)context).close();
						
						TempImage image = new TempImage();
						image.setImagePath(relativePathID);
						image.setCreateDate(new Timestamp(System.currentTimeMillis()));
						tempImageDao.insertImageTempRecord(image);
						
						responseMessage.setStatus(true);
						responseMessage.setMessage("上传成功！");
						responseMessage.setLink(relativePathID);
					} else {
						responseMessage.setStatus(false);
						responseMessage.setMessage("文件保存失败，请重新上传");	
					}					
				}
				
			} catch (IOException e) {
				responseMessage.setStatus(false);
				responseMessage.setMessage("上传失败！");
			}                  
		  } else {
              responseMessage.setStatus(false);
			  responseMessage.setMessage("请选择文件！");
		  }
		  return gson.toJson(responseMessage); 
    }	
}
