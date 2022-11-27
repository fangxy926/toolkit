import cn.yangman.pdf.CompressPDF;
import cn.yangman.pdf.SplitPDF;
import org.junit.Test;

public class MyTest {

    @Test
    public void splitPDFTest() {
        String file = "data/BPM 2022 Blockchain, RPA, and CEE Forum.pdf";
        SplitPDF.splitAndConcat(file, 10, 50);
    }

    @Test
    public void compressPDFTest() {
        String file = "data/A Human-in-the-Loop Approach to Support the Segments Compliance Analysis_2022_Agostinelli et al.pdf";
//        CompressPDF.compress(file);
        CompressPDF.compressWithAsposePDF(file);
    }
}
