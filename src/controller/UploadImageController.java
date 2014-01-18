package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.UUID;

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
import tools.Constant;
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
	 public String originalImageUpload(@RequestParam("file") MultipartFile fileFromForm) {
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
	 * @Description: 上传png图片, 直接保存
	 * @param fileFromForm
	 * @return
	 */
	@RequestMapping(value = "/image/copy_png", method = RequestMethod.POST)
	@ResponseBody
	 public String originalImageUploadForPNG(@RequestParam("file") MultipartFile fileFromForm) {
		  UploadResponseMessage responseMessage = new UploadResponseMessage();
		  Gson gson = new Gson();
		  if (!fileFromForm.isEmpty()) {
              try {
				InputStream inputStream = fileFromForm.getInputStream();			
				if (!CommonValidationTools.checkImageSize(fileFromForm)) {
					responseMessage.setStatus(false);
					responseMessage.setMessage("文件大小超过限制！");
				} else {
					String relativePathID = saveImageFile(inputStream, Constant.ORIGINAL_IMAGE_PNG);
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
	 public String squareImageUpload(@RequestParam("file") MultipartFile fileFromForm) {
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
	
	/**
	 * @title: saveImageFile
	 * @description: 把图片文件存储到服务器上
	 * @param input
	 * @param audioType
	 * @return
	 */
	private String saveImageFile(InputStream input, String imageType){
		try {
			String imageID = generateRandomImageID();
			String saveInDataBase =  Constant.IMAGE_DATABASE_PATH + imageID;
			
			//save to server
			String classPath = this.getClass().getClassLoader().getResource("/").getPath();
			String savePath = classPath.replaceAll("/WEB-INF/classes/", Constant.IMAGE_NORMAL_PATH);
			File file = new File(savePath + imageID + imageType);
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
