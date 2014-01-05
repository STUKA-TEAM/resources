package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

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
				/*CommonValidationTools commonValidationTools = new CommonValidationTools();
				if (!commonValidationTools.checkVideoType(fileFromForm)) {
					responseMessage.setStatus(false);
					responseMessage.setMessage("请选择mp4或webm格式视频！");
				}
				else {*/
					String videoType = fileFromForm.getContentType().replace("video/", "");					
					String relativePathID = saveVideoFile(inputStream, videoType);
					if (relativePathID != "") {
						String videoPath = getVideoPath(relativePathID).substring(1).replace('/', '\\');
						
						InputStream input = UploadVideoController.class.getResourceAsStream("/environment.properties");
						Properties properties = new Properties();
						String ffmpegPath = null;
						try {
							properties.load(input);
							ffmpegPath = (String)properties.get("ffmpegPath");
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
						
						if (ffmpegPath != null) {
							boolean testMp4 = getMp4(ffmpegPath, videoPath, videoType);
							boolean testWebm = getWebm(ffmpegPath, videoPath, videoType);
							if (testMp4 && testWebm) {
								deleteFile(videoPath + "." + videoType);
								responseMessage.setStatus(true);
								responseMessage.setMessage("上传成功！");
								responseMessage.setLink(relativePathID);
							}else {
								deleteFile(videoPath + "." + videoType);
								deleteFile(videoPath + "_standard.mp4");
								deleteFile(videoPath + "_standard.webm");
								responseMessage.setStatus(false);
								responseMessage.setMessage("视频上传失败!请选择其他类型上传");
							}
						}else {
							deleteFile(videoPath + "." + videoType);
							responseMessage.setStatus(false);
							responseMessage.setMessage("系统配置信息丢失");
						}						
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
			String saveInDataBase =  Constant.VIDEO_DATABASE_PATH + videoID;
			
			//save to server
			String classPath = this.getClass().getClassLoader().getResource("/").getPath();
			String savePath = classPath.replaceAll("/WEB-INF/classes/", Constant.VIDEO_NORMAL_PATH);
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
		String videoPath = saveInDataBase.replaceAll(Constant.VIDEO_DATABASE_PATH,
				Constant.VIDEO_NORMAL_PATH);
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
    
    /**
     * @title: getMp4
     * @description: 将视频转换成mp4格式
     * @param ffmpegPath
     * @param filename
     * @param oldType
     * @return
     */
    private static boolean getMp4(String ffmpegPath, String filename, String oldType) {
		List<String> command = new ArrayList<String>();		
		command.add(ffmpegPath);
		command.add("-i");
		command.add(filename + "." + oldType);
		command.add(filename + "_standard.mp4");
		try {
			 ProcessBuilder builder = new ProcessBuilder(); 
             builder.command(command);
             Process process = builder.start(); 
             doWaitFor(process);  
             process.destroy();
             if (!checkFile(filename + "_standard.mp4")) {
 				return false;
 			 }
			 return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}
    
    /**
     * @title: getWebm
     * @description: 将视频转换成webm格式
     * @param ffmpegPath
     * @param filename
     * @param oldType
     * @return
     */
    private static boolean getWebm(String ffmpegPath, String filename, String oldType) {
		List<String> command = new ArrayList<String>();		
		command.add(ffmpegPath);
		command.add("-i");
		command.add(filename + "." + oldType);
        command.add("-acodec");
        command.add("libvorbis");
		command.add("-aq");
		command.add("5");
		command.add("-ac");
		command.add("2");
		command.add("-vcodec");
        command.add("libvpx");
		command.add("-qmax");
		command.add("25");
		command.add("-threads");
		command.add("0");
		command.add(filename + "_standard.webm");
		try {
			 ProcessBuilder builder = new ProcessBuilder(); 
             builder.command(command);
             Process process = builder.start(); 
             doWaitFor(process);  
             process.destroy();
        	
             if (!checkFile(filename + "_standard.webm")) {
				return false;
			 }
			 return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}
    
    /**
     * @title: checkFile
     * @description: 确认转换后的文件是完整的
     * @param path
     * @return
     */
    private static boolean checkFile(String path) {  
        File file = new File(path);  
        if (!file.isFile()) {  
            return false;  
        } else {  
            return true;  
        }  
    } 
    
    /**
     * @title: deleteFile
     * @description: 删除文件
     * @param filepath
     * @return
     */
    private static boolean deleteFile(String filepath) {  
        File file = new File(filepath);  
        if (file.exists() && file.isFile()) {
			return file.delete();
		}
        return false;
    } 
    
    /**
     * @title: doWaitFor
     * @description: 等待进程执行结束
     * @param p
     * @return
     */
    private static int doWaitFor(Process p) {  
        InputStream in = null;  
        InputStream err = null;  
        int exitValue = -1; // returned to caller when p is finished  
        try {  
            System.out.println("coming");  
            in = p.getInputStream();  
            err = p.getErrorStream();  
            boolean finished = false; // Set to true when p is finished  
   
            while (!finished) {  
                try {  
                    while (in.available() > 0) {  
                        Character c = new Character((char) in.read());  
                        System.out.print(c);  
                    }  
                    while (err.available() > 0) {  
                        Character c = new Character((char) err.read());  
                        System.out.print(c);  
                    }  
   
                    exitValue = p.exitValue();  
                    finished = true;  
   
                } catch (IllegalThreadStateException e) {  
                    Thread.sleep(500);  
                }  
            }            
        } catch (Exception e) {  
            System.err.println("doWaitFor(): unexpected exception - "  
                    + e.getMessage());  
        } finally {  
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
            if (err != null) {  
                try {  
                    err.close();  
                } catch (IOException e) {  
                    System.out.println(e.getMessage());  
                }  
            }  
        }  
        return exitValue;  
    }  
}
