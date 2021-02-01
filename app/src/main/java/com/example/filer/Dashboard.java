package com.example.filer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.input.InputManager;
import android.icu.text.CaseMap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.FileUtils;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;


import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.razerdp.widget.animatedpieview.AnimatedPieView;
import com.razerdp.widget.animatedpieview.AnimatedPieViewConfig;
import com.razerdp.widget.animatedpieview.data.SimplePieInfo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dashboard extends AppCompatActivity {


    private static final String TAG ="Dashboard" ;
    Intent myFileIntent;
    TextView file_names_txtv, name_txtv;
    ImageView imgv;
    int flags = 0;
    TextView lbl;
    Dialog dialog_profile, filename_diag;
    TextView l1,l2,l3,l4;
    String fbname, fbimgurl;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    String[] filecat = {"Media","Audio","Documents","Others"};
    int[] noofchild = {0,0,0,0};
    int id = 1;


    String category;

    StorageReference storageReference;

    public Uri furi;
    int array_size;
    TextView txtv_file;
    ScrollView scrollView;
    String url_fs;
    BottomSheetDialog bottomSheetDialog;
    private StorageTask uploadTask;
    String file_name;
    double fz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        file_name = Long.toString(System.currentTimeMillis());


        file_names_txtv = findViewById(R.id.file_name_txt);
        txtv_file = findViewById(R.id.total_no_of_files);
        name_txtv = findViewById(R.id.username_txt);
        imgv = findViewById(R.id.user_profile);

        final ImageSlider imageSlider = findViewById(R.id.image_slider);
        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel("https://image.freepik.com/free-vector/server-room-cloud-storage-icon-datacenter-database-concept-data-exchange-process_39422-556.jpg","Why Cloud?"));
        slideModels.add(new SlideModel("https://image.freepik.com/free-vector/abstract-secure-technology-background_52683-28464.jpg","Your Documents are safe"));
        slideModels.add(new SlideModel("https://image.freepik.com/free-vector/illustration-personal-memories-concept_23-2148394383.jpg","No device storage required"));
        slideModels.add(new SlideModel("https://image.freepik.com/free-vector/group-people-illustration-set_52683-33806.jpg","Easy to share with people"));
        slideModels.add(new SlideModel("https://image.freepik.com/free-photo/creative-background-male-hand-with-phone_99433-522.jpg","Secured by enhanced technology"));
        slideModels.add(new SlideModel("https://image.freepik.com/free-vector/technical-support-programming-coding_335657-2470.jpg","Easy to update"));
        imageSlider.setImageList(slideModels,true);


        fetchdata();
        //bottomsheet();
        findViewById(R.id.view_my_files_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bottomSheetDialog.show();
                bottomsheet();



            }
        });

        findViewById(R.id.image_recognizer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this,Image_Identifier.class));
                overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
            }
        });










        storageReference = FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid());




        findViewById(R.id.select_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                myFileIntent.setType("*/*");
                startActivityForResult(myFileIntent,10);




            }
        });
        findViewById(R.id.edit_profile_icon_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editprofile();
            }
        });
        l1 = findViewById(R.id.media_label);
        l2 = findViewById(R.id.audio_label);
        l3 = findViewById(R.id.docs_label);
        l4 = findViewById(R.id.other_labels);



        setupiechart();
        lablesintent();
        loader();





    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();


                    String path = data.getData().getPath();



                    file_names_txtv.setText(path);
                    furi = data.getData();


                    filename_edit();

                }
                break;
        }
    }

    public void FileUploader() {
        file_names_txtv.setText("Upload File");
        final ProgressDialog progressDialog = new ProgressDialog(Dashboard.this);
        progressDialog.setIndeterminate(false);
        //progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Please wait");
        progressDialog.setCancelable(false);

        progressDialog.show();
        if (file_name.trim().equalsIgnoreCase("")) {
            file_name = Long.toString(System.currentTimeMillis());

        }


        StorageReference Ref = storageReference.child(file_name + "." + getExtension(furi));
        //Toast.makeText(getApplicationContext(),getExtension(furi),Toast.LENGTH_LONG).show();
        if ((getExtension(furi).equalsIgnoreCase("jpg")) || (getExtension(furi).equalsIgnoreCase("jpeg")) || ((getExtension(furi).equalsIgnoreCase("png")) || ((getExtension(furi).equalsIgnoreCase("bmp")) || ((getExtension(furi).equalsIgnoreCase("gif")))) || ((getExtension(furi).equalsIgnoreCase("mp4"))) || (getExtension(furi).equalsIgnoreCase("wmv")) || ((getExtension(furi).equalsIgnoreCase("avi"))))) {
            category = "MEDIA";
        } else if ((getExtension(furi).equalsIgnoreCase("mp3")) || (getExtension(furi).equalsIgnoreCase("m4a")) || (getExtension(furi).equalsIgnoreCase("wav")) || (getExtension(furi).equalsIgnoreCase("wma"))) {
            category = "AUDIO";

        } else if ((getExtension(furi).equalsIgnoreCase("pdf")) || (getExtension(furi).equalsIgnoreCase("txt"))) {
            category = "DOCUMENTS";
        } else {
            category = "OTHERS";
        }







       uploadTask =  Ref.putFile(furi)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        try{

                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isComplete());
                            url_fs = uriTask.getResult().toString();
                            FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Files").child(file_name).setValue(url_fs);
                            FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Category").child(category).child(file_name).setValue(url_fs);



                            //FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Files").child(file_name).setValue(String.valueOf(uriTask));

                            Toast.makeText(Dashboard.this,"File Uploaded successfully",Toast.LENGTH_LONG).show();
                            file_name = Long.toString(System.currentTimeMillis());





                        }
                        catch (Exception e){
                            Toast.makeText(Dashboard.this,e.getMessage().toString(),Toast.LENGTH_LONG).show();

                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                })
               .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                       mBuilder.setContentText("File uploaded successfully")
                               .setProgress(0,0,false);
                       mNotifyManager.notify(id,mBuilder.build());
                       progressDialog.dismiss();
                       //Context context;
                       MediaPlayer mediaPlayer = MediaPlayer.create(Dashboard.this, R.raw.notification_sound);
                       mediaPlayer.start();


                   }
               })
       .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
           @Override
           public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
               double pr = (100.0* snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
               progressDialog.setMessage("Uploading is in progress. "+(int)pr+"% uploaded");
               progressDialog.setProgress((int)pr);
               mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
               mBuilder = new NotificationCompat.Builder(Dashboard.this);
               mBuilder.setContentTitle("Uploading")
                       .setContentText("Uploading file to online storage")
                       .setSmallIcon(R.drawable.download);
               mBuilder.setProgress(100, (int)pr, false);
               mNotifyManager.notify(id, mBuilder.build());

           }
       });

    }
    private String getExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(furi));


    }
    public void filename_edit(){
        try{
            filename_diag = new Dialog(Dashboard.this);
            filename_diag.setContentView(R.layout.file_name_txt);
            filename_diag.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            filename_diag.setCancelable(false);
            final EditText fnamei = filename_diag.findViewById(R.id.file_name_edt);
            TextView uploadbtn = filename_diag.findViewById(R.id.upload);
            TextView cancel = filename_diag.findViewById(R.id.cancel);
            fnamei.setText(file_name);

            uploadbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    file_name = fnamei.getText().toString();
                    if(fnamei.getText().toString().trim().equalsIgnoreCase("")){
                        Toast.makeText(Dashboard.this,"Please enter file name",Toast.LENGTH_SHORT).show();

                    }
                    else{

                        if (file_name.contains(".") || file_name.contains("#") || file_name.contains("$") || file_name.contains("[") || file_name.contains("]")) {
                            Toast.makeText(Dashboard.this,"Sorry special characters are not allowed",Toast.LENGTH_LONG).show();

                        }
                        else{
                            file_name = fnamei.getText().toString();
                            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            manager.hideSoftInputFromWindow(fnamei.getApplicationWindowToken(),0);

                            FileUploader();
                            filename_diag.dismiss();

                        }


                    }
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Dashboard.this,Dashboard.class));
                    overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
                    finish();
                }
            });



            filename_diag.show();

        }
        catch (Exception e){
            Toast.makeText(Dashboard.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();

        }








    }
     void fetchdata(){
        FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Files").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    String nof = "0"+Long.toString(snapshot.getChildrenCount());
                    txtv_file.setText(nof);

                   array_size = Integer.parseInt(nof);


                }
                catch (Exception e){

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        fetchdetails();

     }
     void fetchdetails(){
         FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Profile").addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 try{

                     String name_of_user = snapshot.child("Name").getValue().toString();
                     fbname = name_of_user;
                     name_txtv.setText(name_of_user);
                     flags = 1;

                     String photo_url = snapshot.child("PhotoURL").getValue().toString();
                     fbimgurl = photo_url;
                     Glide.with(Dashboard.this).load(photo_url).centerCrop().placeholder(R.drawable.user).into(imgv);


                 }
                 catch (Exception e){
                     //Toast.makeText(getApplicationContext(),""+e.getMessage().toString(),Toast.LENGTH_SHORT).show();

                 }


             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });


     }

     void bottomsheet(){
        try{

            bottomSheetDialog  = new BottomSheetDialog(Dashboard.this,R.style.BottomSheetTheme);

            View sheetview = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet, (ViewGroup) findViewById(R.id.bottom_sheet));
            bottomSheetDialog.setContentView(sheetview);
            bottomSheetDialog.show();



            sheetview.findViewById(R.id.media_files).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Dashboard.this,FilesByCategory.class);
                    i.putExtra("category_name","MEDIA");
                    bottomSheetDialog.dismiss();
                    startActivity(i);
                }
            });

            sheetview.findViewById(R.id.sign_out).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(Dashboard.this).setTitle("Are you sure?").setMessage("That you want to signout. This won't affect your existing data").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseAuth.getInstance().signOut();

                            Intent intent = new Intent(Dashboard.this,MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                            finish();

                        }
                    })
                            .setNegativeButton("No",null).show();

                }
            });

            sheetview.findViewById(R.id.docs_files).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Dashboard.this,FilesByCategory.class);
                    i.putExtra("category_name","DOCUMENTS");
                    bottomSheetDialog.dismiss();
                    startActivity(i);
                }
            });
            sheetview.findViewById(R.id.audio_files).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Dashboard.this,FilesByCategory.class);
                    i.putExtra("category_name","AUDIO");
                    bottomSheetDialog.dismiss();
                    startActivity(i);
                }
            });
            sheetview.findViewById(R.id.other_files).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Dashboard.this,FilesByCategory.class);
                    i.putExtra("category_name","OTHERS");
                    bottomSheetDialog.dismiss();
                    startActivity(i);
                }
            });
            sheetview.findViewById(R.id.setuppin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        editprofile();

                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(),e.getMessage().toString(),Toast.LENGTH_SHORT).show();

                    }



                }
            });









        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage().toString(),Toast.LENGTH_SHORT).show();

        }




     }

     void lablesintent(){
        findViewById(R.id.media_label).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lbl = findViewById(R.id.media_label);
                lbl.setTextSize(25);
                new CountDownTimer(100, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        Intent i = new Intent(Dashboard.this,FilesByCategory.class);
                        i.putExtra("category_name","MEDIA");

                        startActivity(i);
                        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                        lbl.setTextSize(20);

                    }
                }.start();


            }
        });
         findViewById(R.id.audio_label).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 lbl = findViewById(R.id.audio_label);
                 lbl.setTextSize(25);
                 new CountDownTimer(100, 100) {
                     @Override
                     public void onTick(long millisUntilFinished) {

                     }

                     @Override
                     public void onFinish() {
                         Intent i = new Intent(Dashboard.this,FilesByCategory.class);
                         i.putExtra("category_name","AUDIO");

                         startActivity(i);
                         overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                         lbl.setTextSize(20);

                     }
                 }.start();


             }
         });
         findViewById(R.id.docs_label).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 lbl = findViewById(R.id.docs_label);
                 lbl.setTextSize(25);
                 new CountDownTimer(100, 100) {
                     @Override
                     public void onTick(long millisUntilFinished) {

                     }

                     @Override
                     public void onFinish() {
                         Intent i = new Intent(Dashboard.this,FilesByCategory.class);
                         i.putExtra("category_name","DOCUMENTS");

                         startActivity(i);
                         overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                         lbl.setTextSize(20);

                     }
                 }.start();


             }
         });
         findViewById(R.id.other_labels).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 lbl = findViewById(R.id.other_labels);
                 lbl.setTextSize(25);
                 new CountDownTimer(100, 100) {
                     @Override
                     public void onTick(long millisUntilFinished) {

                     }

                     @Override
                     public void onFinish() {
                         Intent i = new Intent(Dashboard.this,FilesByCategory.class);
                         i.putExtra("category_name","OTHERS");

                         startActivity(i);
                         overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                         lbl.setTextSize(20);

                     }
                 }.start();


             }
         });
     }

     void setupiechart(){




         FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Category").addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 try{
                     noofchild[0] = (int) snapshot.child("MEDIA").getChildrenCount();
                     noofchild[1] = (int)snapshot.child("AUDIO").getChildrenCount();
                     noofchild[2] = (int) snapshot.child("DOCUMENTS").getChildrenCount();
                     noofchild[3] = (int) snapshot.child("OTHERS").getChildrenCount();
                     l1.setText(Integer.toString(noofchild[0])+"\nMedia");
                     l2.setText(Integer.toString(noofchild[1])+"\nAudio");
                     l3.setText(Integer.toString(noofchild[2])+"\nDocs");
                     l4.setText(Integer.toString(noofchild[3])+"\nOthers");
                     AnimatedPieView animatedPieView = findViewById(R.id.piechart);
                     AnimatedPieViewConfig config = new AnimatedPieViewConfig();


                     if(noofchild[0]==0 && noofchild[1]==0 && noofchild[2]==0 && noofchild[3]==0){
                         config.addData(new SimplePieInfo(1,Color.parseColor("#E74C3C"),"No Data Found"));

                     }
                     else{
                         config.addData(new SimplePieInfo(noofchild[0],Color.parseColor("#FF4062"),"Media"));
                         config.addData(new SimplePieInfo(noofchild[1],Color.parseColor("#406AFF"),"Audio"));
                         config.addData(new SimplePieInfo(noofchild[2],Color.parseColor("#FFC940"),"Documents"));
                         config.addData(new SimplePieInfo(noofchild[3],Color.parseColor("#5F27CD"),"Others"));

                     }
                     config.drawText(true);
                     config.duration(1000);
                     config.strokeMode(false);
                     config.textSize(12);

                     animatedPieView.applyConfig(config);

                     animatedPieView.start();
                 }
                 catch (Exception e){
                     Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

                 }

             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });



     }

     void loader(){
        final ProgressDialog progressDialog = new ProgressDialog(Dashboard.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait while data is retriving");
        progressDialog.setCancelable(false);
        progressDialog.show();
         new CountDownTimer(25000, 1000) {
             @Override
             public void onTick(long millisUntilFinished) {
                 if(flags == 0){
                     progressDialog.show();

                 }
                 else{
                     progressDialog.dismiss();
                 }

                 if(millisUntilFinished<12000){
                     progressDialog.setTitle("Network Error");
                     progressDialog.setMessage("Poor network bandwidth.\nFetching data may take time");
                 }
                 if(millisUntilFinished<6000){
                     progressDialog.setMessage("Poor or no internet connection.\nApp is closing automatically within "+(int)millisUntilFinished/1000+" seconds");
                 }
                 if((millisUntilFinished/1000)%2==0){
                     progressDialog.setTitle("Loading.");
                 }
                 else if((millisUntilFinished/1000)%3==0){
                     progressDialog.setTitle("Loading..");

                 }
                 else{
                     progressDialog.setTitle("Loading...");
                 }

             }

             @Override
             public void onFinish() {
                if(flags == 0){
                    finishAffinity();
                    System.exit(0);
                }

             }
         }.start();
     }

     void editprofile(){
        dialog_profile = new Dialog(Dashboard.this);
        dialog_profile.setContentView(R.layout.profile);
        dialog_profile.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog_profile.setCancelable(true);
        final TextInputLayout textInputLayout = dialog_profile.findViewById(R.id.filledTextField);
        textInputLayout.getEditText().setText(fbname);
        ImageView usrimg = dialog_profile.findViewById(R.id.user_img);
         Glide.with(Dashboard.this).load(fbimgurl).centerCrop().placeholder(R.drawable.user).into(usrimg);

        dialog_profile.findViewById(R.id.save_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = textInputLayout.getEditText().getText().toString().trim();
                FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Profile").child("Name").setValue(txt);
                Toast.makeText(getApplicationContext(),"Name updated",Toast.LENGTH_SHORT).show();
                dialog_profile.dismiss();
            }
        });
        dialog_profile.show();



     }




}
