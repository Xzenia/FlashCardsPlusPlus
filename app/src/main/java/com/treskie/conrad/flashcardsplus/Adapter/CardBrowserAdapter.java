package com.treskie.conrad.flashcardsplus.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import com.treskie.conrad.flashcardsplus.R;

import static com.treskie.conrad.flashcardsplus.CardBrowserColumns.answerRow;
import static com.treskie.conrad.flashcardsplus.CardBrowserColumns.questionRow;

public class CardBrowserAdapter extends BaseAdapter{
    private ArrayList <HashMap<String,String>> list;
    private Activity activity;


    public CardBrowserAdapter (Activity activity,ArrayList<HashMap<String, String>> list){
        super();
        this.activity=activity;
        this.list=list;
    }

    static class ViewHolder{
        private TextView tvQuestion;
        private TextView tvAnswer;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        ViewHolder holder = new ViewHolder();
        if(convertView == null){
            convertView = inflater.inflate(R.layout.card_browser_rows, null);
            holder.tvAnswer = convertView.findViewById(R.id.answer_text);
            holder.tvQuestion = convertView.findViewById(R.id.question_text);
            convertView.setTag(holder);
        }
        HashMap<String, String> map = list.get(position);
        holder.tvQuestion.setText(map.get(questionRow));
        holder.tvAnswer.setText(map.get(answerRow));
        return convertView;
    }
}
