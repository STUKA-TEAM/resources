package tools;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.UUID;

import javax.imageio.ImageIO;

/**
 * @Title: ImageUtil
 * @Description: Provide frequently used functions in image processing
 * @Company: ZhongHe
 * @author ben, BelieveIt
 * @date 2013年11月7日
 */
public class ImageUtil {
/**
 * 
 * @param input
 *        图片输入流
 * @return 返回存入数据库的图片路径（除去规格和后缀），使用时利用GetSpecificImage类可以构造相应规格的图片可访问路径
 */
	public String saveMutiSize(InputStream input){
		try{
			String imageID = generateRandomImageID();
			String saveInDataBase =  Constant.IMAGE_NORMAL_PATH + imageID;
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			org.apache.commons.io.IOUtils.copy(input, baos);
			byte[] bytes = baos.toByteArray();
			Boolean saveOrigin = scaleRatio(new ByteArrayInputStream(bytes), Constant.ORIGINAL_IMAGE_WIDTH, 
					imageID, Constant.ORIGINAL_IMAGE); 
			
			Boolean saveStandard = scaleCut(new ByteArrayInputStream(bytes), Constant.STANDARD_IMAGE_WIDTH, 
					Constant.STANDARD_IMAGE_HEIGHT, imageID, Constant.STANDARD_IMAGE);
			
			Boolean saveSmall = scaleCut(new ByteArrayInputStream(bytes), Constant.SAMLL_IMAGE_WIDTH, 
					Constant.SAMLL_IMAGE_WIDTH, imageID, Constant.SMALL_IMAGE);
			
			if (saveOrigin && saveStandard && saveSmall) {
				return saveInDataBase;
			} else {
				return "";
			}
		}catch(Exception e){
			return "";
		}
		
	}
	 
/**
 * 
 * @param srcImage 图片输入流
 * @param destWidth 定宽
 * @param imageID 图片ID
 * @param sizeType 	图片存储规格			
 * @return
 */
    private Boolean scaleRatio(InputStream srcImage, int destWidth, String imageID, String sizeType) {
        try {
            // 获取源图像长宽
            BufferedImage src = ImageIO.read(srcImage);
            int width = src.getWidth();
            int height = src.getHeight();
            double ratio = (double)destWidth / width;
            
            // 缩放长宽
            width = (int) (width * ratio);
            height = (int) (height * ratio);

            // 生成新的图像
            Image image = src.getScaledInstance(width, height,
                    Image.SCALE_DEFAULT);
            BufferedImage img = drawPicture(width, height, image);

            return saveNewImage(img, imageID, sizeType);
        } catch (Exception e) {
            // TODO: handle exception
        	e.printStackTrace();
        	return false;
        }
    }

/**
 * 
 * @param srcImage 图片输入流
 * @param destWidth 定宽
 * @param destHeight 定高
 * @param imageID 图片ID
 * @param sizeType 图片存储规格
 * @return
 */
    private Boolean scaleCut(InputStream srcImage, int destWidth, int destHeight,
    		 String imageID, String sizeType) {
        try {
            // 获取源图像长宽
            BufferedImage src = ImageIO.read(srcImage);
            int srcWidth = src.getWidth();
            int srcHeight = src.getHeight();

            // 源图像等比列缩小或放大后的过渡图像宽高
            int transWidth = 0;
            int transHeight = 0;

            if ((double) srcWidth / srcHeight > (double) destWidth / destHeight) { // 以高为基准缩放
                transWidth = (int) (((double) destHeight / srcHeight) * srcWidth);
                transHeight = destHeight;
                // 若高度一致，则宽度必然大于目标宽度，将其等分成两份
                int halfExcessWidth = (transWidth - destWidth) / 2;

                // 生成过渡图像
                Image image = src.getScaledInstance(transWidth, transHeight,
                        Image.SCALE_SMOOTH);
                BufferedImage transImg = drawPicture(transWidth, transHeight,
                        image);

                // 过渡图像宽度大于目标图像，截去多余部分
                CropImageFilter cropImageFilter = new CropImageFilter(
                        halfExcessWidth, 0, transWidth - halfExcessWidth * 2,
                        transHeight);
                image = Toolkit.getDefaultToolkit().createImage(
                        new FilteredImageSource(transImg.getScaledInstance(
                                transWidth, transHeight, Image.SCALE_SMOOTH)
                                .getSource(), cropImageFilter));
                transImg = drawPicture(transWidth - halfExcessWidth * 2,
                        transHeight, image);

                return saveNewImage(transImg, imageID, sizeType);
            } else { // 以宽为基准缩放
                transWidth = destWidth;
                transHeight = (int) (((double) destWidth / srcWidth) * srcHeight);
                // 若宽度一致，则高度必然大于目标高度，将其等分成两份
                int halfExcessHeight = (transHeight - destHeight) / 2;

                // 生成过渡图像
                Image image = src.getScaledInstance(transWidth, transHeight,
                        Image.SCALE_SMOOTH);
                BufferedImage transImg = drawPicture(transWidth, transHeight,
                        image);

                // 过渡图像高度大于目标图像，截去多余部分
                CropImageFilter cropImageFilter = new CropImageFilter(0,
                        halfExcessHeight, transWidth, transHeight
                                - halfExcessHeight * 2);
                image = Toolkit.getDefaultToolkit().createImage(
                        new FilteredImageSource(transImg.getScaledInstance(
                                transWidth, transHeight, Image.SCALE_SMOOTH)
                                .getSource(), cropImageFilter));
                transImg = drawPicture(transWidth, transHeight
                        - halfExcessHeight * 2, image);

                return saveNewImage(transImg, imageID, sizeType);
            }
        } catch (Exception e) {
        	e.printStackTrace();
            return false;
        }
    }

    /**
     * @Title: drawImage
     * @Description: draw image automatic
     * @param width
     * @param height
     * @param image
     * @return BufferedImage
     */
    private BufferedImage drawPicture(int width, int height, Image image) {
        // TODO Auto-generated method stub
        BufferedImage newImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics graphics = newImage.getGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();
        return newImage;
    }

    /**
     * 
     * @return 返回图片ID
     */
    private String generateRandomImageID() {
        // TODO Auto-generated method stub
        /** currentTime + randomNumber */
        /*
         * long current = System.currentTimeMillis(); Random rand = new
         * Random(); int random = rand.nextInt(10000000); String randomImageName
         * = current + String.format("%07d", random) + "." + imageType;
         */
    	
        String randomImageID = UUID.randomUUID().toString().replace("-", "");
        return randomImageID;
    }
    
/**
 * 
 * @param img 图片输入流
 * @param imageID 图片ID
 * @param size 图片规格
 * @return
 */
    private Boolean saveNewImage(BufferedImage img, String imageID, String sizeType){
    	try {
	    		String classPathString = this.getClass().getClassLoader().getResource("/").getPath();  
	            String savePathString = classPathString.replaceAll("/WEB-INF/classes/", Constant.IMAGE_NORMAL_PATH);           
	            ImageIO.write(img, Constant.IMAGE_TYPE_JPG, new File(savePathString + 
	            		imageID + sizeType)); 
	            return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
        
    }
    

}
