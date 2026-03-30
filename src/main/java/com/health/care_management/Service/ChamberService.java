package com.health.care_management.Service;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class ChamberService {

    public ChamberPDFResult generateChamberPDF(
            String patientName, 
            int patientAge, 
            String patientSex, 
            String doctorName, 
            String doctorQualifications, 
            String hospitalName, 
            String appointmentContact, 
            String hospitalAddress,
            String diseases, 
            String clinicalTest, 
            String medicines, 
            String additionalAdvice) throws IOException {
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);

        // Get today's date
        LocalDate today = LocalDate.now();
        DateTimeFormatter fileDateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd"); 
        String formattedDateForFile = today.format(fileDateFormatter);

        // Generate custom filename: patientName_date.pdf
        String fileName = patientName.replace(" ", "_") + "_" + formattedDateForFile + ".pdf";

        // Date formatting for display
        DateTimeFormatter displayDateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        String formattedDateForDisplay = today.format(displayDateFormatter);

        StringBuilder htmlContent = new StringBuilder();

        // Construct HTML content
        htmlContent.append("<html><head><title>Chamber</title>")
            .append("<link href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css\" rel=\"stylesheet\"/>")
            .append("<link href=\"https://fonts.googleapis.com/css2?family=Great+Vibes&display=swap\" rel=\"stylesheet\">")
            .append("<style>")
            .append("body { margin: 0; font-family: Arial, sans-serif; }")
            .append(".header { display: flex; align-items: center; background-color: #4a4a4a; color: white; padding: 20px; }")
            .append(".header .left { flex: 1; }")
            .append(".header .left h1 { margin: 0; font-size: 24px; }")
            .append(".header .left p { margin: 5px 0; font-size: 14px; }")
            .append(".header .right { text-align: right; }")
            .append(".header .right p { margin: 5px 0; font-size: 14px; }")
            .append(".header .right h2 { margin: 0; font-size: 18px; }")
            .append(".header .icon { text-align: center; margin: 0 20px; }")
            .append(".header .icon i { font-size: 48px; }")
            .append(".info-bar { display: flex; background-color: #a3d065; color: white; padding: 10px; font-size: 14px; }")
            .append(".info-bar div { flex: 1; text-align: center; }")
            .append(".content { display: flex; padding: 20px; position: relative; }")
            .append(".content .left { flex: 1; padding: 10px; }")
            .append(".content .right { flex: 3; padding: 10px; }")
            .append(".vertical-line { border-left: 2px solid #a3d065; position: absolute; height: calc(100% - 40px); top: 0; left: 25%; }")
            .append(".signature-section { margin-top: 50px; padding-right: 20px; text-align: right; }")
            .append(".signature-section .signature-label { font-size: 18px; font-weight: bold; padding-bottom: 5px; }")
            .append(".signature-section .signature-line { border-bottom: 1px solid black; margin-bottom: 5px; width: 200px; display: inline-block; }")
            .append(".signature-section .signature { font-family: 'Great Vibes', cursive; font-size: 24px; }")
            .append(".date { margin-top: 20px; font-size: 16px; text-align: right; padding-right: 20px; }")
            .append("</style></head><body>")
            // Header
            .append("<div class=\"header\">")
            .append("<div class=\"left\">")
            .append("<h1>").append(hospitalName).append("</h1>")
            .append("<p>Appointment Contact: ").append(appointmentContact).append("</p>")
            .append("<p>Address: ").append(hospitalAddress).append("</p>")
            .append("</div>")
            .append("<div class=\"icon\">")
            .append("<i class=\"fas fa-caduceus\"></i>")
            .append("</div>")
            .append("<div class=\"right\">")
            .append("<h2>").append(doctorName).append("</h2>")
            .append("<p>").append(doctorQualifications).append("</p>")
            .append("</div>")
            .append("</div>")
            // Patient Info Bar
            .append("<div class=\"info-bar\">")
            .append("<div><strong>Name:</strong> ").append(patientName).append("</div>")
            .append("<div><strong>Age:</strong> ").append(patientAge).append("</div>")
            .append("<div><strong>Gender:</strong> ").append(patientSex).append("</div>")
            .append("</div>")
            // Content Section
            .append("<div class=\"content\">")
            .append("<div class=\"left\">")
            .append("<h3>Clinical Tests:</h3><p>").append(clinicalTest).append("</p>")
            .append("</div>")
            .append("<div class=\"right\">")
            .append("<h3>Known Diseases:</h3><p>").append(diseases).append("</p>")
            .append("<h3>Medicines:</h3><p>").append(medicines).append("</p>")
            .append("<h3>Additional Advice:</h3><p>").append(additionalAdvice).append("</p>")
            .append("</div>")
            .append("<div class=\"vertical-line\"></div>")
            .append("</div>")
            // Signature Section
            .append("<div class=\"signature-section\">")
            .append("<div class=\"signature-label\">Signature:</div>")
            .append("<div class=\"signature-line\"></div>")
            .append("<div class=\"signature\">").append(doctorName).append("</div>")
            .append("</div>")
            // Date Section
            .append("<div class=\"date\">Date: ").append(formattedDateForDisplay).append("</div>")  // Append formatted date
            .append("</body></html>");

        HtmlConverter.convertToPdf(htmlContent.toString(), writer);
        writer.close();

        return new ChamberPDFResult(byteArrayOutputStream.toByteArray(), fileName); // Return the PDF as byte array along with the filename
    }

    // A class to hold the PDF data and file name
    public static class ChamberPDFResult {
        private final byte[] pdfData;
        private final String fileName;

        public ChamberPDFResult(byte[] pdfData, String fileName) {
            this.pdfData = pdfData;
            this.fileName = fileName;
        }

        public byte[] getPdfData() {
            return pdfData;
        }

        public String getFileName() {
            return fileName;
        }
    }
}
