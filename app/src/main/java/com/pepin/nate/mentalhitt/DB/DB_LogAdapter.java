package com.pepin.nate.mentalhitt.DB;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pepin.nate.mentalhitt.R;

import java.util.ArrayList;

/**
 * Created by User on 1/1/2018.
 */

public class DB_LogAdapter extends RecyclerView.Adapter<DB_LogAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    //Defines arrays that will be plugged into the view
    private ArrayList<String> mFocus = new ArrayList<>();
    private ArrayList<String> mFocusMax = new ArrayList<>();
    private ArrayList<String> mRest = new ArrayList<>();
    private ArrayList<String> mReps = new ArrayList<>();
    private ArrayList<String> mCoolDown = new ArrayList<>();
    private ArrayList<String> mTotalTime = new ArrayList<>();
    private ArrayList<String> mDate = new ArrayList<>();
    private ArrayList<String> mLevel = new ArrayList<>();

    private Context mContext;

    //Initializes arrays with context
    public DB_LogAdapter(Context context,
                         ArrayList<String> vFocus, ArrayList<String> vFocusMax, ArrayList<String> vRest,
                         ArrayList<String> vReps, ArrayList<String> vCoolDown, ArrayList<String> vTotalTime,
                         ArrayList<String> vDate, ArrayList<String> vLevel) {
        mFocus = vFocus;
        mFocusMax = vFocusMax;
        mRest = vRest;
        mReps = vReps;
        mCoolDown = vCoolDown;
        mTotalTime = vTotalTime;
        mDate = vDate;
        mLevel = vLevel;

        mContext = context;
    }

    //Creates the ViewHolder and uses the layout item
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    //Sets the value
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        //This interfaces the ViewHolder with the adapter values
        holder.iFocus.setText(mFocus.get(position));
        holder.iFocusMax.setText(mFocusMax.get(position));
        holder.iRest.setText(mRest.get(position));
        holder.iReps.setText(mReps.get(position));
        holder.iCoolDown.setText(mCoolDown.get(position));
        holder.iTotalTime.setText(mTotalTime.get(position));
        holder.iDate.setText(mDate.get(position));
        holder.iLevel.setText(mLevel.get(position));
    }

    //Gets the size; Case use any value
    @Override
    public int getItemCount() {
        return mFocus.size();
    }


    //View Holder
    //This is where all the views are modified
    public class ViewHolder extends RecyclerView.ViewHolder{

        //Views
        TextView iFocus;
        TextView iFocusMax;
        TextView iRest;
        TextView iReps;
        TextView iCoolDown;
        TextView iTotalTime;
        TextView iDate;
        TextView iLevel;

        LinearLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            //Find views
            iFocus = itemView.findViewById(R.id.text_Focus);
            iFocusMax = itemView.findViewById(R.id.text_FocusMax);
            iRest = itemView.findViewById(R.id.text_Rest);
            iReps = itemView.findViewById(R.id.text_Reps);
            iCoolDown = itemView.findViewById(R.id.text_CoolDown);
            iTotalTime = itemView.findViewById(R.id.text_TotalTime);
            iDate = itemView.findViewById(R.id.text_date);
            iLevel = itemView.findViewById(R.id.text_level);

            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}















