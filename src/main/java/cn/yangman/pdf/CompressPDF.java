package cn.yangman.pdf;

import com.aspose.pdf.Document;
import com.aspose.pdf.optimization.OptimizationOptions;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.parser.PdfImageObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CompressPDF {

    public static float FACTOR = 0.5f;

    public static void compress(String pdfFile) {
        PdfName key = new PdfName("ITXT_SpecialId");
        PdfName value = new PdfName("123456789");

        try {
            // Read the file
            PdfReader reader = new PdfReader(pdfFile);
            int n = reader.getXrefSize();
            PdfObject object;
            PRStream stream;
            // Look for image and manipulate image stream
            for (int i = 0; i < n; i++) {
                object = reader.getPdfObject(i);
                if (object == null || !object.isStream())
                    continue;
                stream = (PRStream) object;
                // if (value.equals(stream.get(key))) {
                PdfObject pdfsubtype = stream.get(PdfName.SUBTYPE);
                if (pdfsubtype != null && pdfsubtype.toString().equals(PdfName.IMAGE.toString())) {
                    PdfImageObject image = new PdfImageObject(stream);
                    BufferedImage bi = image.getBufferedImage();
                    if (bi == null) continue;
                    int width = (int) (bi.getWidth() * FACTOR);
                    int height = (int) (bi.getHeight() * FACTOR);
                    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    AffineTransform at = AffineTransform.getScaleInstance(FACTOR, FACTOR);
                    Graphics2D g = img.createGraphics();
                    g.drawRenderedImage(bi, at);
                    ByteArrayOutputStream imgBytes = new ByteArrayOutputStream();
                    ImageIO.write(img, "JPG", imgBytes);
                    stream.clear();
                    stream.setData(imgBytes.toByteArray(), false, PRStream.BEST_COMPRESSION);
                    stream.put(PdfName.TYPE, PdfName.XOBJECT);
                    stream.put(PdfName.SUBTYPE, PdfName.IMAGE);
                    stream.put(key, value);
                    stream.put(PdfName.FILTER, PdfName.DCTDECODE);
                    stream.put(PdfName.WIDTH, new PdfNumber(width));
                    stream.put(PdfName.HEIGHT, new PdfNumber(height));
                    stream.put(PdfName.BITSPERCOMPONENT, new PdfNumber(8));
                    stream.put(PdfName.COLORSPACE, PdfName.DEVICERGB);
                }
            }
            // Save altered PDF
            String originPath = pdfFile.substring(0, pdfFile.lastIndexOf(".pdf"));
            String savePath = originPath + "_compressed.pdf";
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(savePath));
            stamper.close();
            reader.close();
        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 参考：
     * - https://blog.aspose.com/pdf/compress-optimize-reduce-pdf-file-size-with-same-quality-java/#section1
     * - https://www.cnblogs.com/zhexuejun/p/13257522.html
     *
     * @param pdfFile
     */
    public static void compressWithAsposePDF(String pdfFile) {
        // Open document
        Document pdfDocument = new Document(pdfFile);

        // Initialize OptimizationOptions object
        OptimizationOptions opt = new OptimizationOptions();

        //删除PDF不必要的对象
        opt.setRemoveUnusedObjects(true);
        //删除未使用的流
        opt.setRemoveUnusedStreams(false);
        //删除不必要的字体
        opt.setUnembedFonts(true);
        //链接重复流
        opt.setLinkDuplcateStreams(false);
        // Enable image compression
        // 压缩PDF中的图片
        opt.getImageCompressionOptions().setCompressImages(true);
        // 图片压缩比， 1到100可选，越低压缩比越大
        opt.getImageCompressionOptions().setImageQuality(1);
        opt.getImageCompressionOptions().setMaxResolution(150);
        opt.getImageCompressionOptions().setResizeImages(true);
        pdfDocument.optimizeResources(opt);

        // 优化web的PDF文档
        pdfDocument.optimize();

        // 保存
        String originPath = pdfFile.substring(0, pdfFile.lastIndexOf(".pdf"));
        String savePath = originPath + "_compressed.pdf";
        pdfDocument.save(savePath);
    }

}
