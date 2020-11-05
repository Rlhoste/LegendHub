package com.example.multiplayerlegend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity5 extends AppCompatActivity {
    ListView listView;
    FirebaseDatabase database;
    Info info;
    int NBvote;
    String answerWinner = "";
    List<Integer> ListNBvote = new ArrayList<Integer>();
    List<Answer> Answers = new ArrayList<Answer>();
    DatabaseReference setVoteRef;
    DatabaseReference setWinnerRef;
    DatabaseReference getVoteRef;
    DatabaseReference disconnectRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        database = FirebaseDatabase.getInstance();
        Intent intent = getIntent();
        info = (Info)intent.getParcelableExtra("info");

        disconnectRef = database.getReference("rooms/" + info.room + "/Players/" + info.playername);
        disconnectRef.onDisconnect().removeValue();

        listView = findViewById(R.id.listanswerView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                setVoteRef = database.getReference("rooms/" + info.room + "/Votes/" + info.playername);

                setVoteRef.setValue(Answers.get(i).Playername);
                listView.setEnabled(false);
            }
        });
/*        for(int i=0;i<info.Players.size(); i++){
            getAnswersRef = database.getReference("rooms/" + info.room + "/Answers/"  + info.Players.get(i));
            Answer answer = new Answer(info.Players.get(i), getAnswersRef.getKey());
            Log.e("DebutAffichage", getAnswersRef.);
            Answers.add(answer);
        }*/



        for(int i=0;i<info.AnswersPlayer.size(); i++){
            Answer answer = new Answer(info.AnswersPlayer.get(i), info.Answers.get(i));

            Answers.add(answer);
        }

        Collections.shuffle(Answers);
        List<String> AdapterList = new ArrayList<String>();
        for(int i=0;i<Answers.size(); i++){
            AdapterList.add(Answers.get(i).Answer);
            ArrayAdapter<String> adapter= new ArrayAdapter<>(MainActivity5.this, android.R.layout.simple_list_item_1,AdapterList );
            listView.setAdapter(adapter);
        }
        if(info.iHost == 1) {
            ValidationEventListener();
        }

    }
    
    private  void NextPhase(){
        Intent intent2 = new Intent(getApplicationContext(), Main6Activity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent2.putExtra("info", info);
        disconnectRef.onDisconnect().cancel();
        //Log.e("NextPhase" , String.valueOf(info.Answers.size()));
        startActivity(intent2);
        finish();
    }


    private void ValidationEventListener(){
        getVoteRef= database.getReference("rooms/" + info.room + "/Votes");
        getVoteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> Votes = snapshot.getChildren();
                NBvote =0;

                for(DataSnapshot snapshot1 : Votes){
                    NBvote++;
                    //Log.e("vote" , String.valueOf(NBvote) + "it's ok");
                }


                if(NBvote>= info.NBplayers){
                    //Lancer Answers
                    //Etablie la liste des nombres de de vote dans l'ordre des info.players.

                    for(int i = 0; i<info.Players.size();i++) {
                        int nbVoteTemp = 0;
                        for (DataSnapshot snapshot1 : Votes) {
                            if(info.Players.get(i) == snapshot1.getKey()){
                                nbVoteTemp++;
                                ListNBvote.add(nbVoteTemp);
                            }
                        }

                    }
                    //Garde l'index dans l'ordre des info.players et selectionne le plus grand
                    int maxVote=0;
                    int indexBest=0;
                    for(int i=0; i<ListNBvote.size(); i++){
                            if(ListNBvote.get(i)>=maxVote){
                                maxVote = ListNBvote.get(i);
                                indexBest = i;
                            }
                    }
                    //ecrit info.player
                    setWinnerRef = database.getReference("rooms/" + info.room + "/Winner/playerName");
                    setWinnerRef.setValue(info.Players.get(indexBest));

                    //recherche qui la réponse correspondante en fonction de info.player
                    //Log.e("Answer.size" , String.valueOf(Answers.size()));
                    //Answers est shuffle
                    for(int i =0; i<info.AnswersPlayer.size();i++){

                        if(info.AnswersPlayer.get(i).toString().equals(info.Players.get(indexBest).toString())){
                            answerWinner = info.Answers.get(i);
                            //info.ScoreBoard.set(i, String.valueOf(Integer.parseInt(info.ScoreBoard.get(i)) + 1) );

                        }

                    }

                    setWinnerRef = database.getReference("rooms/" + info.room + "/Winner/answer");
                    setWinnerRef.setValue(answerWinner);
                    NextPhase();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

/*    private  void addPhaseListener(){
        phaseRef = database.getReference("rooms/" + info.room);
        phaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("GameParameters").child("Phase").getValue() == "v"){

                    for(int i=0;i<info.Players.size(); i++){
                        String S_answer = (String) snapshot.child("Answers").child(info.Players.get(i)).getValue();
                        Answer answer = new Answer(info.Players.get(i), getAnswersRef.getKey());
                        Log.e("DebutAffichage", getAnswersRef.);
                        Answers.add(answer);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            });
    });

    }*/
}