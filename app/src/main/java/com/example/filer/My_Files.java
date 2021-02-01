package com.example.filer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.file.Files;
import java.util.ArrayList;

public class My_Files extends AppCompatActivity {
    ListView listView;
    int total_files;
    String files[];
    ArrayList<String> arrlist = new ArrayList<String>();
    int i;
    //ArrayList<FileNames> arrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my__files);

        listView = findViewById(R.id.list_view);



        fetchfiledetails();
       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Files").child(arrlist.get(position)).addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       try{
                           String url = snapshot.getValue().toString();
                           Intent i = new Intent(Intent.ACTION_VIEW);
                           i.setData(Uri.parse(url));
                           startActivity(i);

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
        FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Files").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    total_files = Integer.parseInt(String.valueOf(snapshot.getChildrenCount()));
                    files = new String[total_files];
                    for(DataSnapshot ds : snapshot.getChildren()){
                        String filenm = ds.getKey();
                        files[i] = filenm;
                        arrlist.add(filenm);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(My_Files.this,android.R.layout.simple_list_item_1,arrlist);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0,0);
    }
}
