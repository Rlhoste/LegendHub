package com.example.multiplayerlegend;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

public class Info implements Parcelable {
    public int NBplayers = 0;
    public String room = "";
    public String playername = "";
    public int iHost= 0;
    List<String> Players = new ArrayList<String>();
    List<String> Answers = new ArrayList<String>();
    List<String> AnswersPlayer = new ArrayList<String>();
    List<String> ScoreBoard = new ArrayList<String>();
    List<String> ImagePrec = new ArrayList<String>();


    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected Info(Parcel in) {
        NBplayers = in.readInt();
        room = in.readString();
        in.readStringList(Players);
        in.readStringList(Answers);
        in.readStringList(AnswersPlayer);
        in.readStringList(ScoreBoard);
        in.readStringList(ImagePrec);
        iHost = in.readInt();
        playername = in.readString();
    }

    public Info(String _roomName, int _NBPlaces, int _iHost, String _playername){
        room = _roomName;
        NBplayers = _NBPlaces;
        iHost = _iHost;
        playername = _playername;


    }

    public static final Creator<Info> CREATOR = new Creator<Info>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public Info createFromParcel(Parcel in) {
            return new Info(in);
        }

        @Override
        public Info[] newArray(int size) {
            return new Info[size];
        }
    };

    public void addPlayersList(String name){
        Players.add(name);
        //ScoreBoard.add("10");
    }

    public void addScoreboardList(){
        ScoreBoard.add("0");
    }

    public void addAnswersList(String answer){
        Answers.add(answer);
    }

    public void addAnswersPlayerList(String answerPlayer){
        AnswersPlayer.add(answerPlayer);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(NBplayers);
        parcel.writeString(room);
        parcel.writeStringList(Players);
        parcel.writeStringList(Answers);
        parcel.writeStringList(AnswersPlayer);
        parcel.writeStringList(ScoreBoard);
        parcel.writeStringList(ImagePrec);
        parcel.writeInt(iHost);
        parcel.writeString(playername);
    }
}
