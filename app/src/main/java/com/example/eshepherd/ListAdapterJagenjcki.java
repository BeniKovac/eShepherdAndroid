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

public class ListAdapterJagenjcki  extends RecyclerView.Adapter<ListAdapterJagenjcki.MyViewHolder> {
    ArrayList<String> arrayListID;
    ArrayList<String> arrayListDatum;
    Context context;
    private OnClickListener mOnClickListener;

    public ListAdapterJagenjcki(Context ct, ArrayList<String> dataID, ArrayList<String> dataDatum, ListAdapterJagenjcki.OnClickListener onClickListener) {
        context = ct;
        arrayListID = dataID;
        arrayListDatum = dataDatum;
        this.mOnClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ListAdapterJagenjcki.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.one_row_jagenjcki, parent, false);
        return new ListAdapterJagenjcki.MyViewHolder(view, mOnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapterJagenjcki.MyViewHolder holder, int position) {
        holder.textView1.setText(arrayListID.get(position));
        holder.textView2.setText(arrayListDatum.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayListID.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textView1, textView2;
        ImageView myImage;
        ListAdapterJagenjcki.OnClickListener onClickListener;

        public MyViewHolder(@NonNull View itemView, ListAdapterJagenjcki.OnClickListener onClickListener) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.textView_ID);
            textView2 = itemView.findViewById(R.id.textView_Datum);
            myImage = itemView.findViewById(R.id.jagenjcek_img);
            this.onClickListener = onClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickListener.onRowClick(getAdapterPosition());
        }
    }
        public interface OnClickListener {
            void onRowClick(int position);
        }
}