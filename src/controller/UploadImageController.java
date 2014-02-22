package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import model.TempImage;
import model.dao.TempImageDAO;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import tools.CommonValidationTools;
import tools.Constant;
import tools.ImageUtil;
import tools.KindEditorErrorMes;
import tools.KindEditorSuccessMes;
import tools.OSUtil;
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
						responseMessage.setMessage("文件保存失败，请重新上传!");					
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
						responseMessage.setMessage("文件保存失败，请重新上传!");	
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
						responseMessage.setMessage("文件保存失败，请重新上传!");	
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
						responseMessage.setMessage("文件保存失败，请重新上传!");	
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
	 * @description: 获取icon_lib下的图片路径列表
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/image/iconlib/list", method = RequestMethod.GET)
	@ResponseBody
	public String getIconLib(Model model) {
		String classPath = this.getClass().getClassLoader().getResource("/")
				.getPath();
		String targetFolder = classPath.replaceAll("/WEB-INF/classes/",
				Constant.ICON_LIB);

		List<String> nameList = new ArrayList<String>();
		File folder = new File(targetFolder);
		if (folder.isDirectory()) {
			File[] images = folder.listFiles();
			for (int i = 0; i < images.length; i++) {
				String filePath = images[i].getAbsolutePath();
				if (OSUtil.isWindows()) {
					filePath = filePath.replaceAll("\\\\", "/");
				}
				int beginIndex = filePath.lastIndexOf("/") + 1;
				filePath = Constant.ICON_LIB_PATH
						+ filePath.substring(beginIndex);
				nameList.add(filePath);
			}
		}

		Gson gson = new Gson();
		String response = gson.toJson(nameList);
		return response;
	}
	
	/**
	 * @Description: 拷贝png文件到另外一个路径下，并改后缀为jpg
	 * @param filepath
	 * @return
	 */
	@RequestMapping(value = "/image/iconlib/copy", method = RequestMethod.POST)
	@ResponseBody
	 public String copyIconLibImage(@RequestParam("filepath") String filepath) {
		String classPath = this.getClass().getClassLoader().getResource("/")
				.getPath();
		String srcFolder = classPath.replaceAll("/WEB-INF/classes/",
				Constant.ICON_LIB);
		filepath = filepath.replaceAll(Constant.ICON_LIB_PATH, srcFolder);

		Gson gson = new Gson();
		UploadResponseMessage responseMessage = new UploadResponseMessage();

		File srcImage = new File(filepath);
		InputStream input = null;
		try {
			input = new FileInputStream(srcImage);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		
		if (input != null) {		
			String relativePathID = saveImageFile(input, Constant.SMALL_IMAGE);
			if (relativePathID != "") {
				ApplicationContext context = new ClassPathXmlApplicationContext(
						"All-Modules.xml");
				TempImageDAO tempImageDao = (TempImageDAO) context
						.getBean("TempImageDAO");
				((ConfigurableApplicationContext) context).close();

				TempImage image = new TempImage();
				image.setImagePath(relativePathID);
				image.setCreateDate(new Timestamp(System.currentTimeMillis()));
				tempImageDao.insertImageTempRecord(image);

				responseMessage.setStatus(true);
				responseMessage.setMessage("上传成功！");
				responseMessage.setLink(relativePathID);
			} else {
				responseMessage.setStatus(false);
				responseMessage.setMessage("文件保存失败，请重新上传!");
			}
		} else {
			responseMessage.setStatus(false);
			responseMessage.setMessage("请选择有效文件！");
		}
        return gson.toJson(responseMessage);   
    }
	
    /**
     * @Description: 上传图片存储为三种大小尺寸,来源为KindEditor编辑器	
     * @param fileFromForm
     * @return
     */
	@RequestMapping(value = "/image/kindeditor", method = RequestMethod.POST)
	@ResponseBody
	 public String kindEditorImageUpload(HttpServletRequest request) {

		try {
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setHeaderEncoding("UTF-8");
			List items = upload.parseRequest(request);
			Iterator itr = items.iterator();
			while (itr.hasNext()) {
				FileItem item = (FileItem) itr.next();
					if (!item.isFormField()) {
						InputStream inputStream = item.getInputStream();
						//检查文件大小
						if(!CommonValidationTools.checkImageSizeFromFileItem(item)){
							return getError("文件大小超过限制！");
						}else {
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
								
								KindEditorSuccessMes kindEditorSuccessMes = new KindEditorSuccessMes();
								kindEditorSuccessMes.setError(0);
								kindEditorSuccessMes.setUrl(relativePathID + Constant.ORIGINAL_IMAGE_JPG);
								Gson gson = new Gson();
				                return gson.toJson(kindEditorSuccessMes);
							} else {
								return getError("文件保存失败，请重新上传！");
							}						
						}
					}
			}
		return getError("请选择图片！");
		} catch (Exception e) {
			return getError("文件保存失败，请重新上传！");
		}

    }	
	
	 private String getError(String message) {  
		 	KindEditorErrorMes kindEditorErrorMes = new KindEditorErrorMes();
		 	kindEditorErrorMes.setError(1);
		 	kindEditorErrorMes.setMessage(message);
		 	Gson gson = new Gson();	        
	        return gson.toJson(kindEditorErrorMes);  
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
