package com.datavirtue.nevitium.services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import javax.imageio.ImageIO;

/**
 *
 * @author SeanAnderson
 */
public class ImageCaptureService {

    public String convertImageToBase64Png(BufferedImage buffered) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(buffered, "png", baos);
        var byteArray = baos.toByteArray();
        return Base64.getEncoder().encodeToString(byteArray);
    }

    public String createHtmlImgSrc(String base64PngImage) {
        return "\"data:image/png;base64," + base64PngImage + "\"";
    }

    public BufferedImage getPngFromUrl(URL url) throws IOException {
        
        return ImageIO.read(url);
    }
    //invoiceItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/businessmanager/res/ViewInvoice.png"))); // NOI18N
}
