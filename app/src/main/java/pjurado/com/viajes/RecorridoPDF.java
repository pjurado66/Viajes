package pjurado.com.viajes;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import pjurado.com.viajes.modelo.Recorrido;

import static android.os.Environment.getExternalStorageDirectory;

public class RecorridoPDF {
    private Context context;
    private Activity activity;
    private File pdfFile;
    private Recorrido[] recorrido;
    private File archivo;
    private Document document;
    private PdfWriter pdfWriter;
    private Paragraph paragraph;
    private String nombre;
    private Font fTitle = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
    private Font fSubTitle = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    private Font fText = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
    private Font fHigh = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD, BaseColor.RED);

    public RecorridoPDF(Context context, Activity activity, String nombre, Recorrido[] recorrido) {
        this.context = context;
        this.activity = activity;
        this.recorrido = recorrido;
        this.nombre = nombre;
        File folder = new File(context.getExternalFilesDir(null), "Viajes");
        pdfFile = new File(folder, nombre + ".pdf");
        if (!folder.exists()){
            folder.mkdir();
        }
        openDocument();
        crea();
    }

    public void openDocument(){
        try {
            document = new com.itextpdf.text.Document(PageSize.A4);
            pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
        }catch (Exception e){
            Log.e("openDocument", e.toString());
        }

    }
    public void crea(){
        //openDirectory(Uri.parse(Environment.DIRECTORY_DOWNLOADS));
        String[] header = {"Origen - Destino", "Distancia", "Tiempo"};
        //String parrafo = "hola Irene no se caya";


        //RecorridoPDF recorridoPDF = new RecorridoPDF(context, activity, archivo);
        //openDocument();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date date = new Date();
        String fecha = dateFormat.format(date);
        addMetaData("Viaje a " + nombre, fecha, "Pjurado");
        titles("Viaje a " + nombre, "", fecha);
        //addParagraph(parrafo);
        createTable(header, recorrido);
        closeDocument();
        Toast.makeText(context, "PDF Creado\n" + pdfFile, Toast.LENGTH_LONG).show();
        //viewPdf();
    }

    public void closeDocument(){
        document.close();
    }

    private void createFile(){



    }

    private void permisos(){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(context, "Sin permisos", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions((Activity)context, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        else{
            Toast.makeText(context, "Permisos recibidos", Toast.LENGTH_SHORT).show();
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(context, "Sin permisos", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions((Activity)context, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        else{
            Toast.makeText(context, "Permisos recibidos", Toast.LENGTH_SHORT).show();
        }
    }

    public void addMetaData(String title, String subject, String author){
        document.addTitle(title);
        document.addSubject(subject);
        document.addAuthor(author);
    }

    public void titles (String title, String subTitle, String date){
        try {
            paragraph = new Paragraph();
            addChildPar(new Paragraph(title, fTitle));
            addChildPar(new Paragraph(subTitle, fSubTitle));
            addChildPar(new Paragraph("Generado " + date, fHigh));
            paragraph.setSpacingAfter(30);
            document.add(paragraph);
        } catch (Exception e){
            Log.e("addTitles", e.toString());
        }
    }

    private void addChildPar(Paragraph childParagraph){
        childParagraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.add(childParagraph);
    }

    public void addParagraph(String text){
        try {
            paragraph = new Paragraph(text, fText);
            paragraph.setSpacingAfter(5);
            paragraph.setSpacingBefore(5);
            document.add(paragraph);
        }catch (Exception e){
            Log.e("addParagraph", e.toString());
        }
    }

    public void createTable(String[] header, Recorrido[] recorrido){
        paragraph = new Paragraph();
        paragraph.setFont(fText);
        float[] colWidths = {4,1,1};
        PdfPTable pdfPTable = new PdfPTable(colWidths);

        //PdfPTable pdfPTable = new PdfPTable(header.length);
        pdfPTable.setWidthPercentage(100);

        PdfPCell pdfPCell;
        for (int i = 0; i < header.length; i++){
            pdfPCell = new PdfPCell(new Phrase(header[i], fSubTitle));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell.setBackgroundColor(BaseColor.GREEN);
            pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            pdfPCell.setFixedHeight(40);
            pdfPTable.addCell(pdfPCell);
        }

        for (int indexF = 0; indexF < recorrido.length; indexF++){
            //String[] row = clients.get(indexF);
            //for (int indexC = 0; indexC < row.length; indexC++){
                pdfPCell = new PdfPCell(new Phrase(recorrido[indexF].getOrigen()
                + " - " + recorrido[indexF].getDestino()));
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPCell.setFixedHeight(40);
            pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                pdfPTable.addCell(pdfPCell);


            String dist = String.valueOf(recorrido[indexF].getDistancia());
            pdfPCell = new PdfPCell(new Phrase(dist + " Km"));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell.setFixedHeight(40);
            pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            pdfPTable.addCell(pdfPCell);

            pdfPCell = new PdfPCell(new Phrase(recorrido[indexF].getTiempo()));
            pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPCell.setFixedHeight(40);
            pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            pdfPTable.addCell(pdfPCell);
            //}
        }
        try{
            paragraph.add(pdfPTable);
            document.add(paragraph);
        }catch (Exception e){
            Log.e("createTable", e.toString());
        }
    }
    public void viewPdf(){
        if (pdfFile.exists()){
            //Uri uri = Uri.fromFile(pdfFile);
            //storage/emulated/0/Android/data/pjurado.com.viajes/files/Viajes/Huesca y Navarra.pdf
            Uri uri = Uri.parse(pdfFile.getPath());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            try{
                context.startActivity(intent);
            }catch (ActivityNotFoundException e){
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.adobe.reader&gl=ES")));
                Toast.makeText(context, "Debes instalar una aplicación para visualizar PDFs", Toast.LENGTH_SHORT).show();
            }
        }

    }




}
