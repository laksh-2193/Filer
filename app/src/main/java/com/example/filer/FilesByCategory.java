package com.example.filer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.nio.file.Files;
import java.util.ArrayList;

public class FilesByCategory extends AppCompatActivity {
    ListView listView;
    int total_files;
    String files[];
    String catergory, url, file_name_from_pos;
    ArrayList<String> arrlist = new ArrayList<String>();
    int i;
    ProgressDialog progressDialog;
    Dialog dialog;
    TextView txtv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_by_category);
        catergory = getIntent().getStringExtra("category_name");
        catergory = catergory.toUpperCase();
        txtv = findViewById(R.id.category_name);
        txtv.setText(catergory.substring(0,1)+catergory.substring(1,catergory.length()).toLowerCase());
        listView = findViewById(R.id.list_view);
        fetchfiledetails();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                file_name_from_pos = arrlist.get(position).toString();
                FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Category").child(catergory).child(arrlist.get(position)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        try{
                            url = snapshot.getValue().toString();
                            option_viewer();


                        }
                        catch (Exception e){


                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
    void fetchfiledetails(){
        i = 0;
        FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Category").child(catergory).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    total_files = Integer.parseInt(String.valueOf(snapshot.getChildrenCount()));
                    files = new String[total_files];
                    for(DataSnapshot ds : snapshot.getChildren()){
                        String filenm = ds.getKey();
                        files[i] = filenm;
                        arrlist.add(filenm);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(FilesByCategory.this,android.R.layout.simple_list_item_1,arrlist);
                        listView.setAdapter(adapter);



                        // Toast.makeText(getApplicationContext(),files[i],Toast.LENGTH_SHORT).show();
                        i++;
                    }



                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.getMessage().toString(),Toast.LENGTH_SHORT);

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void option_viewer(){

        dialog = new Dialog(FilesByCategory.this  );
        dialog.setContentView(R.layout.custom_list_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.findViewById(R.id.view_files).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }
        });
        dialog.findViewById(R.id.share_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,"Sharing "+file_name_from_pos+"\nAccess it from the URL below\n"+url);
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });
        dialog.findViewById(R.id.delete_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(FilesByCategory.this).setTitle("Are you sure?").setMessage("That you want to signout. This won't affect your existing data").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        progressDialog = new ProgressDialog(FilesByCategory.this);
                        progressDialog.setTitle("Please wait");
                        progressDialog.setMessage("Deleting file from online storage");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        FirebaseStorage.getInstance().getReferenceFromUrl(url).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Toast.makeText(getApplicationContext(),"File deleted",Toast.LENGTH_SHORT).show();
                                FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Category").child(catergory.toUpperCase()).child(file_name_from_pos).removeValue();
                                FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Files").child(file_name_from_pos).removeValue();

                                progressDialog.dismiss();
                                dialog.dismiss();
                                startActivity(getIntent());
                                overridePendingTransition(0,0);
                                finish();

                            }
                        });


                    }
                })
                  .setNegativeButton("No",null).show();


            }
        });

        dialog.show();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0,0);
    }
}
