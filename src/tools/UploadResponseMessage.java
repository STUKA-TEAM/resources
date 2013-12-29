package tools;

/**
 * @Title: UploadImageResponseMessage
 * @Description: 图片上传返回信息
 * @Company: ZhongHe
 * @author ben
 * @date 2013年12月20日
 */
public class UploadResponseMessage extends ResponseMessage{
private String link;

public String getLink() {
	return link;
}

public void setLink(String link) {
	this.link = link;
}

}
