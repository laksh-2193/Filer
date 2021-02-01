package com.example.filer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.input.InputManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.sdsmdg.tastytoast.TastyToast;
import com.skyhope.textrecognizerlibrary.TextScanner;
import com.skyhope.textrecognizerlibrary.callback.TextExtractCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class Image_Identifier extends AppCompatActivity {
    public static TextView result_txt;
    Button scan, generate;
    ImageView qr_code;
    EditText data_ed;
    OutputStream outputStream;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image__identifier);
        scan = findViewById(R.id.scan_btn);
        result_txt = findViewById(R.id.result);
        data_ed = findViewById(R.id.data_qr);
        generate = findViewById(R.id.genrate_btn);
        qr_code = findViewById(R.id.qr_code);



        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Camera_Scanning.class));

            }
        });

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data_ed.getText().toString().trim().equalsIgnoreCase("")==false){
                    String sText = data_ed.getText().toString();
                    MultiFormatWriter writer = new MultiFormatWriter();
                    try {
                        BitMatrix matrix = writer.encode(sText, BarcodeFormat.QR_CODE,200,200);
                        BarcodeEncoder encoder = new BarcodeEncoder();
                        Bitmap bitmap = encoder.createBitmap(matrix);
                        qr_code.setImageBitmap(bitmap);
                        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        manager.hideSoftInputFromWindow(data_ed.getApplicationWindowToken(),0);
                        Toast.makeText(getApplicationContext(),"Click on QR code to save to device",Toast.LENGTH_SHORT).show();
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please write something",Toast.LENGTH_SHORT).show();
                }

            }
        });

        qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    BitmapDrawable drawable = (BitmapDrawable) qr_code.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    File filepath = Environment.getExternalStorageDirectory();
                    File dir = new File(filepath.getAbsolutePath()+"/Filer/");
                    dir.mkdir();
                    File file = new File(dir,"qr_code_"+data_ed.getText().toString()+".jpeg");
                    try {
                        outputStream = new FileOutputStream(file);
                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(),e.getMessage().toString(),Toast.LENGTH_SHORT).show();

                    }
                    try {
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                        TastyToast.makeText(getApplicationContext(),"File saved",TastyToast.LENGTH_SHORT,TastyToast.SUCCESS).show();
                        File file2 = new File(Environment.getExternalStorageDirectory(),
                                "/Filer/");
                        if(file2.exists()){
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setDataAndType(Uri.fromFile(file2), "*/*");
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"File doesn't exists",Toast.LENGTH_LONG).show();
                        }

                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(),"Failed to save to file - "+e.getMessage().toString(),Toast.LENGTH_LONG).show();


                    }



                    try {
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Failed to save to file - "+e.getMessage().toString(),Toast.LENGTH_LONG).show();
                }


            }
        });

        result_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("TextView",result_txt.getText().toString());
                clipboardManager.setPrimaryClip(clipData);
                TastyToast.makeText(getApplicationContext(),"Copied",TastyToast.LENGTH_SHORT,TastyToast.SUCCESS).show();
            }
        });



    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
    }
}
