package com.example.a318_a1;

import android.widget.ListView;

import java.util.ArrayList;

public class RaidActivity {

    int playerNum;
    String difficulty, time;
    double lat, lon;
    ArrayList<Message> msgList;

    public RaidActivity(int num, String di, String time, double lat, double lon){
        playerNum = num;
        difficulty = di;
        this.time = time;
        this.lat = lat;
        this.lon = lon;
    }

    public void setList(ArrayList<Message> li){
        msgList = li;
    }

    public boolean getList(){
        if(msgList == null)
            return false;
        return true;
    }
}
