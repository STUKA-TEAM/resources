package tools;

/**
 * @Title: Constant
 * @Description: define global constant
 * @Company: ZhongHe
 * @author dai, ben
 * @date 2013年11月5日
 */
public final class Constant {
	/*image*/
	public static final String IMAGE_NORMAL_PATH = "/images/";
	public static final String IMAGE_DATABASE_PATH = "/resources/images/";
	public static final String IMAGE_TYPE_GIF = "gif";
	public static final String IMAGE_TYPE_JPG = "jpg";
	public static final String IMAGE_TYPE_JPEG = "jpeg";
	public static final String IMAGE_TYPE_BMP = "bmp";  
	public static final String IMAGE_TYPE_PNG = "png";
    public static final String IMAGE_TYPE_PSD = "psd";   // photoshop
    
    /*image size*/
    //处理过的原始图片，大小为300px*300px
    public static final String BIG_SQUARE = "_original.jpg";
    public static final int SQUARE_LENGTH = 300;
    //处理过的原始图片，定宽为360px，长宽比不变;
    public static final String ORIGINAL_IMAGE = "_original.jpg";
    public static final int ORIGINAL_IMAGE_WIDTH = 360;
    //要放入360px*200px区域的图片,主要是图文消息的图;
    public static final String STANDARD_IMAGE = "_standard.jpg";
    public static final int STANDARD_IMAGE_WIDTH = 360;
    public static final int STANDARD_IMAGE_HEIGHT = 175;
    //缩略图，形状为正方形，边长为50px;
    public static final String SMALL_IMAGE = "_small.jpg";
    public static final int SAMLL_IMAGE_WIDTH = 65;
    
    /*video*/
    public static final String VIDEO_NORMAL_PATH = "/media/";
    public static final String VIDEO_DATABASE_PATH = "/resources/media/";
}
