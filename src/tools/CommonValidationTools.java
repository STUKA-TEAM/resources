package tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.fileupload.FileItem;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Title: CommonValidationTools
 * @Description: all the tools to validate input
 * @Company: ZhongHe
 * @author BelieveIt
 * @date 2013年11月21日
 */
public class CommonValidationTools {
	
	/**
	 * 
	 * @param email
	 * @return 是否符合邮件规范
	 */
	public static boolean checkEmail(String email){
		String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";  
		Pattern regex = Pattern.compile(check);  
		Matcher matcher = regex.matcher(email);
		return matcher.matches();
	}
	
	/**
	 * 
	 * @param phone
	 * @return 是否符合中国手机号规则
	 */
	public static boolean checkPhone(String phone){
		String check = "^(13[4,5,6,7,8,9]|15[0,8,9,1,7]|188|187)\\d{8}$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(phone);		 
		return matcher.matches();
	}
	
	/**
	 * 
	 * @param multipartFile
	 * @return 是否超过2MB
	 */
	public static boolean checkImageSize(MultipartFile multipartFile) {
		//不能超过 2MB
		if(multipartFile.getSize() > 2100000){
			return false;
		}else {
			return true;
		}
	}
	
	/**
	 * 
	 * @param multipartFile
	 * @return 是否超过2MB
	 */
	public static boolean checkImageSizeFromFileItem(FileItem fileItem) {
		//不能超过 2MB
		if(fileItem.getSize() > 2100000){
			return false;
		}else {
			return true;
		}
	}	
	
	/**
	 * 
	 * @param multipartFile
	 * @return 是否是图片格式
	 */
	public static boolean checkImageType(MultipartFile multipartFile) {
		if (multipartFile.getContentType().equals("image/jpeg")  
				|| multipartFile.getContentType().equals("image/png")) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 
	 * @param multipartFile
	 * @return 是否是视频格式
	 */
	public static boolean checkVideoType(MultipartFile multipartFile) {
		if (multipartFile.getContentType().equals("video/mp4")  
				|| multipartFile.getContentType().equals("video/webm")) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 
	 * @param multipartFile
	 * @return 是否是音频格式
	 */
	public static boolean checkAudioType(MultipartFile multipartFile) {
		if (multipartFile.getContentType().equals("audio/mp3")) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
     * @Title: getLength
     * @Description: detect the length of one sentence
     * @param text
     * @return int --the length
     */
    public static int getLength(String text) {
        int length = 0;
        for (int i = 0; i < text.length(); i++) {
            if (new String(text.charAt(i) + "").getBytes().length > 1) {
                length += 2;
            } else {
                length += 1;
            }
        }
        return (int) Math.round(length / 2.0);
    }
}
