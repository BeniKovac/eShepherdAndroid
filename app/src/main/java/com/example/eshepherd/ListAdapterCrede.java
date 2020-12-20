package com.example.eshepherd;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
public class ListAdapterCrede  extends RecyclerView.Adapter<ListAdapterCrede.MyViewHolder>{
    ArrayList<String> arrayListID;
    ArrayList<String> arrayListDatum;
    Context context;

    public ListAdapterCrede(Context ct, ArrayList<String> dataID, ArrayList<String> dataDatum){
        context=ct;
        arrayListID = dataID;
        arrayListDatum = dataDatum;
    }

    @NonNull
    @Override
    public ListAdapterCrede.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.one_row_crede,parent,false);
        return new ListAdapterCrede.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapterCrede.MyViewHolder holder, int position) {
        holder.textView1.setText(arrayListID.get(position));
        holder.textView2.setText(arrayListDatum.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayListID.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView1, textView2;
        ImageView myImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.textView_ID);
            textView2 = itemView.findViewById(R.id.textView_Datum);
            myImage = itemView.findViewById(R.id.gonitev_img);
        }
    }
}