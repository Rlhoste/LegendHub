package com.example.multiplayerlegend;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.os.SystemClock.elapsedRealtime;
//TODO Reboucler a 0
//TODO Verifier le nombre de parties

public class Main7Activity extends AppCompatActivity {
    String WinnerName = "";
    ListView listViewScoreboard;
    DatabaseReference phaseRef;
    DatabaseReference phaseGet;
    DatabaseReference disconnectRef;
    DatabaseReference removeRef;
    Chronometer chronometer;
    FirebaseDatabase database;
    int timeStart2 = 10;
    Info info;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7);
        listViewScoreboard = findViewById(R.id.ListViewScoreboard);
        database = FirebaseDatabase.getInstance();
        chronometer = findViewById(R.id.ChronometerScoreboard);
        chronometer.setBase(elapsedRealtime()+(timeStart2 * 1000));
        chronometer.setCountDown(true);
        chronometer.start();

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                timeStart2--;
                if(timeStart2==0)
                {
                    NextPhase();
                }
            }
        });
        Intent intent = getIntent();
        info = (Info)intent.getParcelableExtra("info");

        phaseRef = database.getReference("rooms/" + info.room + "/GameParameters/phase");

        disconnectRef = database.getReference("rooms/" + info.room + "/Players/" + info.playername);

        disconnectRef.onDisconnect().removeValue();
        phaseRef.setValue("s");
        addPhaseListener();


    }

    private  void NextPhase(){
        Intent intent = new Intent(getApplicationContext(), Main4Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("info", info);
        disconnectRef.onDisconnect().cancel();
        removeRef = database.getReference("rooms/" + info.room + "/Answers");
        removeRef.removeValue();
        removeRef = database.getReference("rooms/" + info.room + "/Votes");
        removeRef.removeValue();
        removeRef = database.getReference("rooms/" + info.room + "/Winner");
        removeRef.removeValue();
        removeRef = database.getReference("rooms/" + info.room + "/Image");
        removeRef.removeValue();
        info.Answers.clear();
        info.AnswersPlayer.clear();
        phaseRef.setValue("o");
        //Log.e("NextPhase" , String.valueOf(info.Answers.size()));
        startActivity(intent);
        finish();
    }

    void addPhaseListener(){
        phaseGet = database.getReference("rooms/" + info.room );
        phaseGet.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child("GameParameters").child("phase").getValue().equals("s")){
                    WinnerName = (String) snapshot.child("Winner").child("playerName").getValue();
                    List<String> ScoreBoardDisplay = new ArrayList<String>();
                    for(int i=0; i<info.Players.size();i++){

                        int ScoreboardI = Integer.parseInt(info.ScoreBoard.get(i));
                        Log.e("BeforeScore = ", info.ScoreBoard.get(i));
                        int score = 1 + ScoreboardI;
                        Log.e("AfterScore = ", String.valueOf(score));

                        if(info.Players.get(i).equals(WinnerName)){
                            //int ScoreboardI = Integer.parseInt(info.ScoreBoard.get(i));
                            //score = 1 + ScoreboardI;
                            info.ScoreBoard.set(i, String.valueOf(score));
                        }
                        String scorename = info.Players.get(i) + " : " + String.valueOf(score);
                        ScoreBoardDisplay.add(scorename);
                    }
                    ArrayAdapter<String> adapter= new ArrayAdapter<>(Main7Activity.this, android.R.layout.simple_list_item_1,ScoreBoardDisplay );
                    listViewScoreboard.setAdapter(adapter);
                    phaseRef.setValue("t");


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}