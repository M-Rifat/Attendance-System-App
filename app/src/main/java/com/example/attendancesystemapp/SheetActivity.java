package com.example.attendancesystemapp;


import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;

import java.util.Calendar;





public class SheetActivity extends AppCompatActivity {
    Button logout;
    //private LinearLayout linear;
    // Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet);
        showTable();
        logout=findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(v.getContext());
                builder.setTitle("Logout");
                builder.setMessage("Are you sure to Logout");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog=builder.create();
                alertDialog.show();
            }
        });


        /*linear = findViewById(R.id.lineard);
        btn = findViewById(R.id.btn_create_pdf);
        btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                Log.d("size", "" + linear.getWidth() + " " + linear.getWidth());
                bitmap = LoadBitmap(linear, linear.getWidth(), linear.getHeight());
                createPdf();
            }
        });
      /*  btn_create_pdf = (Button) findViewById(R.id.btn_create_pdf);
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        btn_create_pdf.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onClick(View v) {
                                createPDFFile(Common.getAppPath(SheetActivity.this) + "test_pdf.pdf");
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();
    }
   @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createPDFFile(String path) {
        if (new File(path).exists())
            new File(path).delete();
        try {
            Document document = new Document();
            //save
            PdfWriter.getInstance(document, new FileOutputStream(path));
            //open to write
            document.open();

            //setting
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("com.nitichan");
            document.addCreator("App");

            //Font Setting
            BaseColor colorAccent = new BaseColor(0,153,204,255);
            float fontSize=20.0f;
            float valueFontSize = 26.0f;

            //custom font
            BaseFont fontName = BaseFont.createFont("assets/fonts/brandon_medium.otf","UTF-8",BaseFont.EMBEDDED);

            //Create Title of Document
            Font titleFont = new Font(fontName,36.0f,Font.NORMAL,BaseColor.BLACK);
            showTable1(document,"Attendance",Element.ALIGN_CENTER,titleFont);
            document.close();

            Toast.makeText(this,"Success",Toast.LENGTH_SHORT).show();

            printPDF();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void printPDF() {
        PrintManager printManager = (PrintManager)getSystemService(Context.PRINT_SERVICE);
        try{
            PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(SheetActivity.this,Common.getAppPath(SheetActivity.this)+"test_pdf.pdf");
            printManager.print("Document",printDocumentAdapter,new PrintAttributes.Builder().build());
        }
        catch (Exception ex)
        {
            Log.e("com.nitichan","" +ex.getMessage());
        }
    }

    private void showTable1(Document document, String text, int align, Font font) throws DocumentException {
        Chunk chunk = new Chunk(text, font);
        Paragraph paragraph = new Paragraph(chunk);
        paragraph.setAlignment(align);
        document.add(paragraph);
        DBHelper dbHelper = new DBHelper(this);

    }*/
    }
    private void showTable(){
        DBHelper dbHelper = new DBHelper(this);
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        long[] idArray = getIntent().getLongArrayExtra("idArray");
        int[] rollArray = getIntent().getIntArrayExtra("rollArray");
        String[] nameArray = getIntent().getStringArrayExtra("nameArray");
        String month = getIntent().getStringExtra("month");

        int DAY_IN_MONTH = getDayInMonth(month);

        //row setup
        int rowSize = idArray.length + 1;
        TableRow[] rows = new TableRow[rowSize];
        TextView[] roll_tvs = new TextView[rowSize];
        TextView[] name_tvs = new TextView[rowSize];
        TextView[][] status_tvs = new TextView[rowSize][DAY_IN_MONTH + 1];

        for (int i = 0; i < rowSize; i++) {
            roll_tvs[i] = new TextView(this);
            name_tvs[i] = new TextView(this);
            for (int j = 1; j <= DAY_IN_MONTH; j++) {
                status_tvs[i][j] = new TextView(this);
            }
        }
        //header
        roll_tvs[0].setText("Roll");
        roll_tvs[0].setTypeface(roll_tvs[0].getTypeface(), Typeface.BOLD);
        name_tvs[0].setText("Name");
        name_tvs[0].setTypeface(name_tvs[0].getTypeface(), Typeface.BOLD);
        for (int i = 1; i <= DAY_IN_MONTH; i++) {
            status_tvs[0][i].setText(String.valueOf(i));
            status_tvs[0][i].setTypeface(status_tvs[0][i].getTypeface(), Typeface.BOLD);
        }
        for (int i = 1; i < rowSize; i++) {
            roll_tvs[i].setText(String.valueOf(rollArray[i - 1]));
            name_tvs[i].setText(nameArray[i - 1]);

            for (int j = 1; j <= DAY_IN_MONTH; j++) {
                String day = String.valueOf(j);
                if (day.length() == 1) day = "0" + day;
                String date = day + "." + month;
                String status = dbHelper.getStatus(idArray[i - 1], date);
                status_tvs[i][j].setText(status);
            }
        }
        for (int i = 0; i < rowSize; i++) {
            rows[i] = new TableRow(this);
            if (i % 2 == 0)
                rows[i].setBackgroundColor(Color.parseColor("#4CAF50"));
            else
                rows[i].setBackgroundColor(Color.parseColor("#FFFFFFFF"));
            roll_tvs[i].setPadding(16, 16, 16, 16);
            name_tvs[i].setPadding(16, 16, 16, 16);

            rows[i].addView(roll_tvs[i]);
            rows[i].addView(name_tvs[i]);

            for (int j = 1; j <= DAY_IN_MONTH; j++) {
                status_tvs[i][j].setPadding(16, 16, 16, 16);
                rows[i].addView(status_tvs[i][j]);
            }
            tableLayout.addView(rows[i]);
        }
        tableLayout.setShowDividers(TableLayout.SHOW_DIVIDER_MIDDLE);
        // webView.loadDataWithBaseURL(null,showTable,"text/html","utf-8",null);
    }

    private int getDayInMonth(String month) {
        int monthIndex = Integer.parseInt(month.substring(0, 1));
        int year = Integer.parseInt(month.substring(4));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, monthIndex);
        calendar.set(Calendar.YEAR, year);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

   /* private Bitmap LoadBitmap(View v, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
         return bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createPdf() {
        WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float width= displayMetrics.widthPixels;
        float height= displayMetrics.heightPixels;
        int convertWidth = (int)width,convertHeight=(int)height;

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo= new PdfDocument.PageInfo.Builder(convertWidth,convertHeight,1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

       Paint paint= new Paint();
        canvas.drawPaint(paint);
        bitmap= Bitmap.createScaledBitmap(bitmap,convertWidth,convertHeight,true);

        canvas.drawBitmap(bitmap,0,0,null);
        document.finishPage(page);

        String targetPdf="/sdcard/Download/page.pdf";
        File file;
        file = new File(targetPdf);
        try{
            document.writeTo(new FileOutputStream(file));
        }
        catch(IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "something wrong try again" + e.toString(), Toast.LENGTH_SHORT).show();

            document.close();
            Toast.makeText(this, "successfully downloaded", Toast.LENGTH_SHORT).show();

            openPdf();
        }

        }

    private void openPdf() {
        File file = new File("/sdcard/Download/page.pdf");
        if(file.exists()){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri,"application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try{
                startActivity(intent);
            }
            catch (ActivityNotFoundException e){
                Toast.makeText(this,"No Application for pdf" ,Toast.LENGTH_SHORT).show();
            }
        }
    }*/

}
