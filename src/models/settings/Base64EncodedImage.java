package models.settings;

import java.awt.Image;
import java.awt.image.BufferedImage;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author SeanAnderson
 */
@Getter @Setter
public class Base64EncodedImage {
    private String encodedImage;
    private String mimeType;
    public Image getImage() 
    {
        return null;
    }
}
