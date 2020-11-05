package com.example.multiplayerlegend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    ListView listView;
    Button button;

    List<String> roomsList;

    String playerName = "";
    String roomName = "";
    boolean bCreateRoom = false;
    FirebaseDatabase database;

    DatabaseReference NbPlayersInRoomRef;
    DatabaseReference destructRoomRef;
    DatabaseReference roomRef;
    DatabaseReference roomsRef;
    DatabaseReference NbPlayersRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        database = FirebaseDatabase.getInstance();

        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        playerName = preferences.getString("playerName", "");
        roomName = playerName;

        listView = findViewById(R.id.ListView);
        button = findViewById(R.id.button2);

        roomsList = new ArrayList<>();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bCreateRoom = true;
                button.setText("CREATING ROOM");
                button.setEnabled(false);
                roomName = playerName;
                roomRef = database.getReference("rooms/" + roomName + "/Players/" + playerName);
                //addRoomEventListener();
                Intent intent = new Intent(getApplicationContext(), Main3Activity.class);
                intent.putExtra("roomName", roomName);
                startActivity(intent);
                roomRef.setValue(playerName);
                NbPlayersRef = database.getReference("rooms/" + roomName + "NbPlayers");
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                roomName = roomsList.get(position);

                roomRef = database.getReference("rooms/" + roomName + "/NBplayers");
                NbPlayersInRoomRef = database.getReference("rooms/" + roomName + "/maxPlace");
//                if (Integer.parseInt(roomRef.getKey()) < (Integer.parseInt(NbPlayersInRoomRef.getKey()) - 1)) {
                    roomRef = database.getReference("rooms/" + roomName + "/Players/" + playerName);
                    addRoomEventListener();
                    roomRef.setValue(playerName);
                    bCreateRoom = true;
         //       } else {
           //         Toast.makeText(Main2Activity.this, "This room is full !", Toast.LENGTH_SHORT).show();
             //   }

            }
        });
        //show if new room is available
        addRoomsEventListener();

    }
    private void addRoomEventListener(){
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                button.setText("CREATE ROOM");
                button.setEnabled(true);
                Intent intent = new Intent(getApplicationContext(), Main3Activity.class);
                intent.putExtra("roomName", roomName);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                button.setText("CREATE ROOM");
                button.setEnabled(true);
                Toast.makeText(Main2Activity.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void addRoomsEventListener(){
        roomsRef = database.getReference("rooms");
        roomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //show list of rooms
                roomsList.clear();
                Iterable<DataSnapshot> rooms = snapshot.getChildren();
                for(DataSnapshot snapshot1 : rooms){


//                    Toast.makeText(Main2Activity.this, snapshot1.child("NBplayers").getValue().toString(), Toast.LENGTH_SHORT).show();
                    //Integer.parseInt(snapshot1.child("NBplayers").getValue().toString())== 0 ||
                    if( snapshot1.child("Players").hasChild(playerName) && !bCreateRoom ){
                        //Toast.makeText(Main2Activity.this, "Détruit!" + snapshot1.getKey(), Toast.LENGTH_SHORT).show();
                        //destructRoomRef = database.getReference("rooms/" + snapshot1.getKey());
                        //destructRoomRef.removeValue();
                        //startActivity(new Intent(getApplicationContext(), MainActivity.class ));
                    }
                    if( !snapshot1.child("Players").hasChildren()){

                        Toast.makeText(Main2Activity.this, "Détruit!" + snapshot1.getKey(), Toast.LENGTH_SHORT).show();
                        destructRoomRef = database.getReference("rooms/" + snapshot1.getKey());
                        destructRoomRef.removeValue();
                        //startActivity(new Intent(getApplicationContext(), MainActivity.class ));
                    }
                    else{
                        roomsList.add(snapshot1.getKey());
                        ArrayAdapter<String> adapter= new ArrayAdapter<>(Main2Activity.this, android.R.layout.simple_list_item_1, roomsList);
                        listView.setAdapter(adapter);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
