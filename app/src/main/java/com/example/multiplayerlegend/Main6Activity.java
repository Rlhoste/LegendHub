package com.example.multiplayerlegend;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.os.SystemClock.elapsedRealtime;

public class Main6Activity extends AppCompatActivity {
    TextView textView;
    TextView textViewWinner;
    ImageView iv;
    Info info;
    FirebaseDatabase database;
    DatabaseReference disconnectRef;
    DatabaseReference winnerRef;
    DatabaseReference changePhaseRef;
    Chronometer chronometer;
    Integer[] Images={
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3,
            R.drawable.image4,
            R.drawable.image5
    };
    int timeStart = 10;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);
        textView = findViewById(R.id.WinnerTextid);
        textViewWinner = findViewById(R.id.WinnerName);
        iv = findViewById(R.id.ImageResultat);
        chronometer = findViewById(R.id.ChronometerWinner);
        database = FirebaseDatabase.getInstance();
        Intent intent = getIntent();
        info = (Info)intent.getParcelableExtra("info");


        chronometer.setBase(elapsedRealtime()+(timeStart * 1000));
        chronometer.setCountDown(true);
        chronometer.start();
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                timeStart--;
                if(timeStart==0)
                {
                    NextPhase();
                }
            }
        });
        changePhaseRef = database.getReference("rooms/"+ info.room + "/GameParameters/phase");
        changePhaseRef.setValue("w");
        disconnectRef = database.getReference("rooms/" + info.room + "/Players/" + info.playername);

        addWinnerListener();
    }

    private  void NextPhase(){
        Intent intent = new Intent(getApplicationContext(), Main7Activity.class);
        intent.putExtra("info", info);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        disconnectRef.onDisconnect().cancel();
        //Log.e("NextPhase" , String.valueOf(info.Answers.size()));
        startActivity(intent);
        finish();
    }

    public void addWinnerListener(){
        winnerRef = database.getReference("rooms/" + info.room);
        winnerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Log.e("Main6", "Snapshot");
                if(snapshot.child("Image").exists()) {
                    String s_id = String.valueOf(snapshot.child("Image").getValue());
                    //Log.e("test", s_id);
                    int id = Integer.valueOf(s_id);
                    iv.setImageResource(Images[id]);
                }
                if(snapshot.child("GameParameters").child("phase").getValue().equals("w")){
                    textView.setText(String.valueOf(snapshot.child("Winner").child("playerName").getValue()));
                    textViewWinner.setText(String.valueOf(snapshot.child("Winner").child("answer").getValue()));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}