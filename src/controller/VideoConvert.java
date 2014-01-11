package controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class VideoConvert implements Runnable {
    private String videoType;
    private String videoPath;
	
	public VideoConvert(String videoType, String videoPath){
		this.videoType = videoType;
		this.videoPath = videoPath;
	}
    
	@Override
	public void run() {
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
				deleteFile(videoPath + "_temp." + videoType);
				System.out.println("convert to mp4 and webm successfully!");
			}else {
				deleteFile(videoPath + ".mp4");
				deleteFile(videoPath + ".webm");
				System.out.println(videoPath + " can not be converted\n" + "The video type is not supported!");
			}
		}else {
			System.out.println(videoPath + " can not be converted\n" + "ffmpegPath configure info can not be found!");
		}
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
		command.add(filename + "_temp." + oldType);
		command.add("-acodec");
        command.add("libvo_aacenc");
		command.add("-vcodec");
        command.add("libx264");;
		command.add("-threads");
		command.add("1");
		command.add(filename + ".mp4");
		try {
			 ProcessBuilder builder = new ProcessBuilder(); 
             builder.command(command);
             Process process = builder.start(); 
             doWaitFor(process);  
             process.destroy();
             if (!checkFile(filename + ".mp4")) {
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
		command.add(filename + "_temp." + oldType);
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
		command.add("1");
		command.add(filename + ".webm");
		try {
			 ProcessBuilder builder = new ProcessBuilder(); 
             builder.command(command);
             Process process = builder.start(); 
             doWaitFor(process);  
             process.destroy();
        	
             if (!checkFile(filename + ".webm")) {
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
            System.out.println("start");  
            in = p.getInputStream();  
            err = p.getErrorStream();  
            boolean finished = false; // Set to true when p is finished  
   
            while (!finished) {  
                try {  
                    while (in.available() > 0) {  
                      /*Character c = new Character((char) in.read());  
                        System.out.print(c); */ 
                    }  
                    while (err.available() > 0) {  
                      /*Character c = new Character((char) err.read());  
                        System.out.print(c); */ 
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
