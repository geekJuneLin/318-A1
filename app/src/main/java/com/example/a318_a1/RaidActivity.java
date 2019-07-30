package com.example.a318_a1;

public class RaidActivity {

    int playerNum;
    String difficulty, time;
    double lat, lon;

    public RaidActivity(int numOfPlayers, String diff, String time, double lat, double lon){
        playerNum = numOfPlayers;
        difficulty = diff;
        this.time = time;
        this.lat = lat;
        this.lon = lon;
    }
}
