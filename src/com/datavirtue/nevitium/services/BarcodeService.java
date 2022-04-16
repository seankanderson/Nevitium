package com.datavirtue.nevitium.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.image.BufferedImage;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

public class BarcodeService {

    public BufferedImage createCode128Image(String text, int heightInMm) {
        var dpiResolution = 160;
        var barcodeGenerator = new Code128Bean();
        barcodeGenerator.setModuleWidth((1.0f / dpiResolution) * 25.4f);
        barcodeGenerator.setHeight(heightInMm);
        BitmapCanvasProvider canvas = new BitmapCanvasProvider(dpiResolution, BufferedImage.TYPE_BYTE_BINARY, false, 0);
        barcodeGenerator.generateBarcode(canvas, text);
        return canvas.getBufferedImage();
    }

    public BufferedImage createQrCodeImage(String text, int sizeInPx) throws WriterException {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = barcodeWriter.encode(text, BarcodeFormat.QR_CODE, sizeInPx, sizeInPx);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

}
