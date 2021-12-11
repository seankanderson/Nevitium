package models.settings;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author SeanAnderson
 */
@Getter
@Setter
public class OutputSettings {
    private String pdfReaderUri;
    private boolean useSystemDefaultPdfReader=true;
    
    private String paymentSystemUri;
    private boolean usePaymentSystemForCard = true;
    private boolean usePaymentSystemForChecks = true;
    private boolean paymentSystemUriIsWeblink = true;
    
    private String invoiceDestinationUri;
    private String quoteDestinationUri;
    private String reportDestinationUri;
    private byte[] watermarkImage;
    private String watermarkMimeType = "png";
    private boolean userWatermarkInReports = false;
    
}
