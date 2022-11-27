package cn.yangman.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SplitPDF {


    /**
     * pdf 拆分及合并
     *
     * splitFile(inputFile,3,4); //第3，4页为一个文件
     * @param pdfFile
     * @param start
     * @param end
     * @return
     */
    public static String splitAndConcat(String pdfFile, Integer start, Integer end) {
        Document document = null;
        PdfCopy pdfCopy = null;
        try {
            PdfReader reader = new PdfReader(pdfFile);
            int totalPages = reader.getNumberOfPages();
            if (end <= 0 || end > totalPages) {
                end = totalPages;
            }
            String originPath = pdfFile.substring(0, pdfFile.lastIndexOf(".pdf"));
            String savePath = originPath + "_" + start + "-" + end + ".pdf";
            document = new Document(reader.getPageSize(1));
            pdfCopy = new PdfCopy(document, new FileOutputStream(savePath));
            document.open();
            for (int j = start; j <= end; j++) {
                document.newPage();
                PdfImportedPage page = pdfCopy.getImportedPage(reader, j);
                pdfCopy.addPage(page);
            }
            document.close();
            return new File(savePath).getName();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }
}
