package com.halcyon.helpModel;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;

/**
 * Created by Himanshu on 4/18/2016.
 */
public class PDFfooter extends PdfPageEventHelper {
    Font paragraphFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();

        DottedLineSeparator separator = new DottedLineSeparator();
        separator.setPercentage(59500f / 523f);
        Chunk linebreak = new Chunk(separator);
        Chunk c2 = new Chunk(".................................................................................");
        Chunk c1 = new Chunk("PAYMENT BY 'ACCOUNT PAYEE' CHEQUES DRAWN IN FAVOUR OF INDOSCAN [PVT] LIMITED scssds");
        Paragraph add1 = new Paragraph();

        add1.add(c2);
        add1.add(c1);
        Phrase footer = new Phrase();



       // footer.add(linebreak);
        footer.add(add1);




        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, footer, (document.right() - document.left()) / 2 + document.leftMargin(), document.bottom() - 10, 0);

    }
}
