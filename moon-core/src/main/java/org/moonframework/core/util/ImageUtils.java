package org.moonframework.core.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author quzile
 * @version 1.0
 * @since 2016/08/31
 */
public class ImageUtils {

    /**
     * <p>按比例压缩</p>
     *
     * @param src   原图片
     * @param pixel 压缩到的像素
     * @return InputStream
     */
    public static InputStream compress(BufferedImage src, int pixel) throws IOException {
        int width = src.getWidth();
        int height = src.getHeight();

        // get scale
        float widthScale = (float) pixel / (float) width;
        float heightScale = (float) pixel / (float) height;
        float scale = Math.min(widthScale, heightScale);

        // Don't make the image larger than it already is.
        if (scale > 1.0) {
            scale = 1.0F;
        }

        width = (int) (width * scale);
        height = (int) (height * scale);

        // compress
        BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        tag.getGraphics().drawImage(src, 0, 0, width, height, null);

        // get input stream
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(tag, "jpg", os);
        return new ByteArrayInputStream(os.toByteArray());
    }

}
