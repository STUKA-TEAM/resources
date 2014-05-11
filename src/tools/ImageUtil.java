package tools;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
			String saveInDataBase =  Constant.IMAGE_DATABASE_PATH + imageID;
			
			File file = inputstreamToFile(input);
			Boolean saveOrigin = scaleRatio(file, Constant.ORIGINAL_IMAGE_WIDTH, 
					imageID, Constant.ORIGINAL_IMAGE_JPG); 
			
			Boolean saveStandard = scaleCut(file, Constant.STANDARD_IMAGE_WIDTH, 
					Constant.STANDARD_IMAGE_HEIGHT, imageID, Constant.STANDARD_IMAGE);
			
			Boolean saveSmall = scaleCut(file, Constant.SAMLL_IMAGE_WIDTH, 
					Constant.SAMLL_IMAGE_WIDTH, imageID, Constant.SMALL_IMAGE);
			
			Boolean deleteTemp = file.delete();	
			if (saveOrigin && saveStandard && saveSmall && deleteTemp) {
				return saveInDataBase;
			} else {
				return "";
			}
		}catch(Exception e){
			return "";
		}		
	}
	
	/**
	 * @Description: 只生成原始尺寸大小的jpg格式图片
	 * @param input
	 * @return
	 */
	public String saveOriginalSize(InputStream input){
		try {
			String imageID = generateRandomImageID();
			String saveInDataBase =  Constant.IMAGE_DATABASE_PATH + imageID;
			
			File file = inputstreamToFile(input);
			Boolean saveOrigin = scaleRatio(file, Constant.ORIGINAL_IMAGE_WIDTH, 
					imageID, Constant.ORIGINAL_IMAGE_JPG); 
			
			Boolean deleteTemp = file.delete();	
			if(saveOrigin && deleteTemp){
				return saveInDataBase;
			}else {
				return "";
			}
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * @Description: 只生成正方形大小的图片   specially for elove
	 * @param input
	 * @return
	 */
	public String saveSquareSize(InputStream input){
		try {
			String imageID = generateRandomImageID();
			String saveInDataBase =  Constant.IMAGE_DATABASE_PATH + imageID;
			
			File file = inputstreamToFile(input);
			Boolean saveSquare = scaleCut(file, Constant.SQUARE_LENGTH, 
					Constant.SQUARE_LENGTH, imageID, Constant.BIG_SQUARE);
			
			Boolean deleteTemp = file.delete();		
			if (saveSquare && deleteTemp) {
				return saveInDataBase;
			}else {
				return "";
			}			
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * 
	 * @param input
	 *        图片输入流
	 * @return 返回存入数据库的图片路径（除去规格和后缀），使用时利用GetSpecificImage类可以构造相应规格的图片可访问路径
	 */
		public String saveMutiSquareSize(InputStream input){
			try{
				String imageID = generateRandomImageID();
				String saveInDataBase =  Constant.IMAGE_DATABASE_PATH + imageID;
				
				File file = inputstreamToFile(input);
				Boolean saveBigSquare = scaleCut(file, Constant.SQUARE_LENGTH, 
						Constant.SQUARE_LENGTH, imageID, Constant.BIG_SQUARE);	
				
				Boolean saveSmallSquare = scaleCut(file, Constant.SMALL_SQUARE_LENGTH, 
						Constant.SMALL_SQUARE_LENGTH, imageID, Constant.SMALL_SQUARE);				

				
				Boolean deleteTemp = file.delete();	
				if (saveBigSquare && saveSmallSquare && deleteTemp) {
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
    private Boolean scaleRatio(File srcImage, int destWidth, String imageID, String sizeType) {
        try {
            // 获取源图像长宽
        	BufferedImage src = new JpegReader().readImage(srcImage);
            int width = src.getWidth();
            int height = src.getHeight();
            double ratio = (double)destWidth / width;
            
            // 缩放长宽
            width = (int) (width * ratio);
            height = (int) (height * ratio);

            // 生成新的图像
            Image image = src.getScaledInstance(width, height,
                    Image.SCALE_SMOOTH);
            BufferedImage img = drawPicture(width, height, image);

            return saveNewImage(img, imageID, sizeType);
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
    private Boolean scaleCut(File srcImage, int destWidth, int destHeight,
    		 String imageID, String sizeType) {
        try {
            // 获取源图像长宽
        	BufferedImage src = new JpegReader().readImage(srcImage);
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
        	System.out.println(e.getMessage());
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
        BufferedImage newImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics graphics = newImage.getGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();
        return newImage;
    }

    /**
     * @title generateRandomImageID
     * @return 返回图片ID
     */
    private String generateRandomImageID() {  	
        String randomImageID = UUID.randomUUID().toString().replace("-", "");
        return randomImageID;
    }
    
/**
 * @title saveNewImage
 * @param img 图片输入流
 * @param imageID 图片ID
 * @param size 图片规格
 * @return
 */
    private Boolean saveNewImage(BufferedImage img, String imageID, String sizeType){
    	try {
	    		String classPathString = this.getClass().getClassLoader().getResource("/").getPath();  
	            String savePathString = classPathString.replaceAll("/WEB-INF/classes/", Constant.IMAGE_NORMAL_PATH); 
	            String format = sizeType.substring(sizeType.length() - 3);
	            ImageIO.write(img, format, new File(savePathString + imageID + sizeType)); 
	            return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}      
    }
    
    /**
     * @title inputstreamToFile
     * @param ins
     * @return
     */
	private File inputstreamToFile(InputStream ins) {
		String pathname = this.getClass().getClassLoader().getResource("/")
				.getPath()
				.replaceAll("/WEB-INF/classes/", Constant.IMAGE_NORMAL_PATH)
				+ generateRandomImageID() + "_temp";
		File file = new File(pathname);
		try {
			OutputStream os = new FileOutputStream(file);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			os.close();
			ins.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return file;
	}
}
