package com.example.multiplayerlegend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main3Activity extends AppCompatActivity {
    int nbPlayers=2;
    int maxPlayer=4;
    int nbRound=0;
    //boolean bHost=false;
    int iHost=0;
    boolean bstart = true;
    TextView maxText;
    TextView maxTextfix;
    TextView RoundTextfix;
    TextView RoundText;
    SeekBar seekBar;
    SeekBar seekBarRound;
    ListView listViewPlayer;
    Button button;
    Button buttonSettings;
    String playerName = "";
    String roomName = "";
    String message = "";
    String launchMessage = "";
    String messageMax = "";
    String NBroundString="";
    String messageNbplayers = "";
    String phaseMessage = "";
    boolean bSettings= false;
    FirebaseDatabase database;
    DatabaseReference RoundRef;
    DatabaseReference phaseRef;
    DatabaseReference roomsRef;
    DatabaseReference LaunchGameRef;
    DatabaseReference disconnectRef;
    DatabaseReference messageRef;
    DatabaseReference maxRef;
    DatabaseReference PlayersInAroomRef;
    DatabaseReference NbplayersRef;
    DatabaseReference destructRoomRef;
    List<String> PlayersList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        seekBar = findViewById(R.id.seekBar2);
        seekBarRound = findViewById(R.id.seekBarRounds);
        maxText = findViewById(R.id.textmax);
        maxTextfix = findViewById(R.id.textMaxFix);
        RoundText = findViewById(R.id.TextNBround);
        RoundTextfix = findViewById(R.id.RoundTextFixed);
        button = findViewById(R.id.button3);
        buttonSettings = findViewById(R.id.SettingsButton);
        listViewPlayer = findViewById(R.id.listplayer);

        PlayersList = new ArrayList<>();
        database = FirebaseDatabase.getInstance();



        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        playerName = preferences.getString("playerName", "");

        Bundle extras = getIntent().getExtras();
        roomName = extras.getString("roomName");
        if(extras != null){

            if(roomName.equals(playerName)){
                button.setVisibility(View.VISIBLE);
                buttonSettings.setVisibility(View.VISIBLE);
                iHost = 1;
                LaunchGameRef = database.getReference("rooms/" + roomName + "/GameParameters/phase");
                launchMessage = "n";
                LaunchGameRef.setValue(launchMessage);
            }
            else{
                button.setVisibility(View.GONE);
                buttonSettings.setVisibility(View.GONE);
                maxTextfix.setVisibility(View.GONE);
                maxText.setVisibility(View.GONE);
                RoundText.setVisibility(View.GONE);
                RoundTextfix.setVisibility(View.GONE);
                seekBar.setVisibility(View.GONE);
                seekBarRound.setVisibility(View.GONE);
                iHost = 0;
            }
        }
        maxText.setText("4");
        //ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.parent);
        //ConstraintSet constraintSet = new ConstraintSet();
        //constraintSet.clone(constraintLayout);
        //constraintSet.connect(listViewPlayer.getId(),ConstraintSet.BOTTOM, button.getId(), ConstraintSet.TOP);
        //constraintSet.applyTo(constraintLayout);
        messageRef = database.getReference("rooms/" + roomName + "/maxPlace");
        message = String.valueOf(maxPlayer);
        messageRef.setValue(message);

        disconnectRef = database.getReference("rooms/" + roomName + "/Players/" + playerName);
        disconnectRef.onDisconnect().removeValue();


        Log.e("RoomNmae", roomName);


        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!bSettings){
                    maxTextfix.setVisibility(View.GONE);
                    maxText.setVisibility(View.GONE);
                    RoundText.setVisibility(View.GONE);
                    RoundTextfix.setVisibility(View.GONE);
                    seekBar.setVisibility(View.GONE);
                    seekBarRound.setVisibility(View.GONE);
                    bSettings = true;
                }else{
                    maxTextfix.setVisibility(View.VISIBLE);
                    maxText.setVisibility(View.VISIBLE);
                    RoundText.setVisibility(View.VISIBLE);
                    RoundTextfix.setVisibility(View.VISIBLE);
                    seekBar.setVisibility(View.VISIBLE);
                    seekBarRound.setVisibility(View.VISIBLE);
                    bSettings = false;
                }

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setEnabled(false);
                LaunchGameRef = database.getReference("rooms/" + roomName + "/GameParameters/phase");
                launchMessage = "o";
                LaunchGameRef.setValue(launchMessage);
            }
        });

        seekBarRound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                nbRound = i*5;
                RoundText.setText(String.valueOf(nbRound));
                RoundRef = database.getReference("rooms/" + roomName + "/Round");
                NBroundString = String.valueOf(nbRound);
                RoundRef.setValue(String.valueOf(nbRound));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    maxPlayer = i;
                    maxText.setText(String.valueOf(maxPlayer));
                    maxRef = database.getReference("rooms/" + roomName + "/maxPlace");
                    messageMax = String.valueOf(maxPlayer);
                    maxRef.setValue(String.valueOf(maxPlayer));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });





        addPhaseEventListener();
        //addRoomEventListener();
        addPlayersListEventListener();
//        whenDisconnect();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        Toast.makeText(Main3Activity.this, "Vous avez quittez la partie", Toast.LENGTH_SHORT).show();
        disconnectRef = database.getReference("rooms/" + roomName + "/Players/" + playerName);
        disconnectRef.removeValue();
    }


   // private  void addRoomEventListener(){
/*        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(role.equals("host")){
                    if(snapshot.getValue(String.class).contains("host:")){
                        button.setEnabled(true);
                        Toast.makeText(Main3Activity.this, "" + snapshot.getValue(String.class).replace("host:",""), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
   // }

/*    private void addPlayersListEventListener(){
        PlayersInAroomRef = database.getReference("rooms/" + roomName + "/Players");
        PlayersInAroomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //show list of rooms
                PlayersList.clear();
                nbPlayers = 0;
                Iterable<DataSnapshot> Players = snapshot.getChildren();
                for(DataSnapshot snapshot1 : Players){
                    nbPlayers++;
                    PlayersList.add(snapshot1.getKey());
                    ArrayAdapter<String> adapter= new ArrayAdapter<>(Main3Activity.this, android.R.layout.simple_list_item_1, PlayersList);
                    listViewPlayer.setAdapter(adapter);
                }
                NbplayersRef = database.getReference("rooms/" + roomName + "/NBplayers");
                messageNbplayers = String.valueOf(nbPlayers);
                NbplayersRef.setValue(messageNbplayers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

    private void addPhaseEventListener(){
        phaseRef = database.getReference("rooms/" + roomName + "/GameParameters/phase");
        phaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("Info", (String) snapshot.getValue());
                if(snapshot.getValue().equals("o"))
                {
                    //Set<String> listplayers = new HashSet<String>();
                    //Toast.makeText(Main3Activity.this, "Vous avez quittez la partie", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), Main4Activity.class);

                    Log.e("Info", playerName);
                    Info info = new Info(roomName, nbPlayers, iHost, playerName);
                    for(int i=0; i<PlayersList.size(); i++){
                        info.addPlayersList(PlayersList.get(i));
                        info.addScoreboardList();
                        //info.addPlayersList(PlayersList.get(i));
                        //intent.putExtra("listplayers",listplayers);
                    }
                    //Log.e("Main3size = ", String.valueOf(PlayersList.size()));
                    //Log.e("Main3sizeSC = ", String.valueOf(info.ScoreBoard.size()));
                    intent.putExtra("info", info);
//                    intent.putExtra("roomName", roomName);
//                    intent.putExtra("NBplayers",String.valueOf(nbPlayers) );
//                    intent.putExtra("listplayers",listplayers);
                    disconnectRef.onDisconnect().cancel();
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addPlayersListEventListener(){
        PlayersInAroomRef = database.getReference("rooms/" + roomName + "/Players");
        PlayersInAroomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //show list of rooms
                PlayersList.clear();
                nbPlayers = 0;
                Iterable<DataSnapshot> Players = snapshot.getChildren();
                for(DataSnapshot snapshot1 : Players){
                    nbPlayers++;
                    PlayersList.add(snapshot1.getKey());
                    ArrayAdapter<String> adapter= new ArrayAdapter<>(Main3Activity.this, android.R.layout.simple_list_item_1, PlayersList);
                    listViewPlayer.setAdapter(adapter);
                }

                NbplayersRef = database.getReference("rooms/" + roomName + "/NBplayers");
                messageNbplayers = String.valueOf(nbPlayers);
                NbplayersRef.setValue(messageNbplayers);
//                if(nbPlayers==0){
//                    destructRoomRef = database.getReference("rooms/" + roomName);
//                    destructRoomRef.removeValue();
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

/*    private void whenDisconnect(){
        disconnectRef = database.getReference("rooms/" + roomName + "/Players/" + playerName);
        disconnectRef.onDisconnect().removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, @NonNull DatabaseReference reference) {
*//*                if(bstart == false) {
                    //disconnectRef = database.getReference("rooms/" + roomName + "/NBplayers");
                    //int temp = Integer.parseInt(disconnectRef.getKey());
                    destructRoomRef = database.getReference("rooms/" + roomName);
                    destructRoomRef.removeValue();
                }
                else {
                    bstart = false;
                    Toast.makeText(Main3Activity.this, Boolean.toString(bstart), Toast.LENGTH_SHORT).show();
                }*//*

            }
        });
    }*/



}
