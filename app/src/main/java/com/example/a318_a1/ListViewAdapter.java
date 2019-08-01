package com.example.a318_a1;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter extends BaseAdapter {

    List<Message> msgList = new ArrayList<>();
    Context context;

    // Constructor
    public ListViewAdapter(Context c){
        context = c;
    }

    public void addMessage(Message msg){
        msgList.add(msg);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return msgList.size();
    }

    @Override
    public Object getItem(int i) {
        return msgList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        MessageHolder holder = new MessageHolder();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Message msg = msgList.get(i);

        if(msg.isBelongToCurrentUser()){
            view = inflater.inflate(R.layout.my_message_view, null);
            holder.messageBody = view.findViewById(R.id.message_body);
            view.setTag(holder);
            holder.messageBody.setText(msg.getMsgBody());
        }
        return view;
    }
}
