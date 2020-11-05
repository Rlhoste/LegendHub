package com.example.multiplayerlegend;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static android.os.SystemClock.elapsedRealtime;

public class Main4Activity extends AppCompatActivity {
    int NBvalidation=0;
    int timeStart = 90;
   //String playerName = "";
    //String roomName = "";
    //String MaxPlayers = "";
    String PlayersValidation = "";
    //List<String> listplayers = new ArrayList<String>();
    //boolean bhost= false;

    //UI

    TextView textValider;
    TextView Textnbplayers;
    Button ButtonValider;
    ImageView Image;
    Chronometer chronometer;
    EditText AnswerText;
    Info info;
    FirebaseDatabase database;
    DatabaseReference AnswersRef;
    DatabaseReference AnswerLocalRef;
    DatabaseReference disconnectRef;
    DatabaseReference AnswerRef;
    DatabaseReference ImageRefSet;
    DatabaseReference ImageGetSet;
    Random r;
    Integer[] Images={
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3,
            R.drawable.image4,
            R.drawable.image5
    };
    ImageView iv;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        database = FirebaseDatabase.getInstance();
        Intent intent = getIntent();
        info = (Info)intent.getParcelableExtra("info");
        iv = (ImageView)findViewById(R.id.imageView3);
        if(info.iHost == 1){
            r=new Random();

            ImageRefSet = database.getReference("rooms/" + info.room + "/Image");
            int rand = r.nextInt(Images.length);
            boolean bidImage = false;
            do{
                rand = r.nextInt(Images.length);
                Log.e("rand", String.valueOf(rand));
                bidImage = false;
                for(int i=0; i<info.ImagePrec.size();i++){
                    if(rand == Integer.valueOf(info.ImagePrec.get(i))){
                        bidImage = true;
                    }
                }
            }while(bidImage);
            ImageRefSet.setValue(rand);
            info.ImagePrec.add(String.valueOf(rand));
        }
        //ImageView iv = (ImageView)findViewById(R.id.imageView3);

        //iv.setImageResource(R.drawable.Image1);
        Log.e("Info", "Info Room : " + info.room + ",Info NBplayers : " + info.NBplayers + ",Info Ihost : "+  info.iHost );
        // Toast.makeText(Main4Activity.this,Integer.toString( info.iHost), Toast.LENGTH_SHORT).show();
/*        if(info.iHost==1)
        {
            AnswerRef = database.getReference("rooms/" + info.room + "/Answers");
            AnswerRef.push();

        }*/


        NBvalidation=0;

        disconnectRef = database.getReference("rooms/" + info.room + "/Players/" + info.playername);
        disconnectRef.onDisconnect().removeValue();
        //Toast.makeText(Main4Activity.this,  info.room + info.playername , Toast.LENGTH_SHORT).show();

        //Text
        AnswerText = findViewById(R.id.editTextTextPersonName);


        textValider = findViewById(R.id.nbValidationText);
        textValider.setText(Integer.toString(0));

        ButtonValider = findViewById(R.id.ValiderButton);
        chronometer = findViewById(R.id.chronometer);
        chronometer.setBase(elapsedRealtime()+(timeStart * 1000));
        chronometer.setCountDown(true);
        chronometer.start();

        //Text
        Textnbplayers = findViewById(R.id.nbPlayersText);
        Textnbplayers.setText(Integer.toString(info.NBplayers));

        ButtonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ButtonValider.setEnabled(false);
                AnswerText.setActivated(false);
                AnswerLocalRef = database.getReference("rooms/" + info.room + "/Answers/" + info.playername);
                AnswerLocalRef.setValue(AnswerText.getText().toString());
            }
        });

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                timeStart--;
                if(info.iHost==1)
                {
                    if(timeStart == 0)
                    {
                        //launch Activity
                    }
                }
            }
        });
//        messageRef = database.getReference("rooms/" + roomName + "/maxPlace");
//        message = String.valueOf(maxPlayer);
//        messageRef.setValue(message);
        ValidationEventListener();
        SetImageEventListener();
    }

    private  void NextPhase(){
        Intent intent2 = new Intent(getApplicationContext(), MainActivity5.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //Log.e("nbvalkidart","dkskdskq");
        intent2.putExtra("info", info);
        disconnectRef.onDisconnect().cancel();
        //Log.e("ScoreBoardM4A" , info.ScoreBoard.get(0));
        startActivity(intent2);
        finish();
    }

    private void SetImageEventListener(){
        ImageGetSet = database.getReference("rooms/" + info.room + "/Image");
        ImageGetSet.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        String s_id = String.valueOf(snapshot.getValue());
                        int id = Integer.valueOf(s_id);
                        iv.setImageResource(Images[id]);
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ValidationEventListener(){
        AnswersRef= database.getReference("rooms/" + info.room + "/Answers");
        AnswersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> Answers = snapshot.getChildren();
                NBvalidation =0;
                info.Answers.clear();
                for(DataSnapshot snapshot1 : Answers){
                    NBvalidation++;
                    info.addAnswersList((String) snapshot1.getValue());
                    info.addAnswersPlayerList((String) snapshot1.getKey());

                }

                textValider.setText(String.valueOf(NBvalidation));
                if(NBvalidation>= info.NBplayers){
                    //Lancer Answers
                    NextPhase();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
                /*private void ValidationEventListener(){
        AnswersRef= database.getReference("rooms/" + info.room + "/Answers");
        AnswersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Iterable<DataSnapshot> Answers = snapshot.getChildren();
                NBvalidation =0;

                for(DataSnapshot snapshot1 : Answers){
                    NBvalidation++;
                    Log.e("Validation" , String.valueOf(NBvalidation) + "it's ok");
                }

                textValider.setText(String.valueOf(NBvalidation));
                if(NBvalidation>= info.NBplayers){
                    //Lancer Answers
                    //NextPhase();
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/




}