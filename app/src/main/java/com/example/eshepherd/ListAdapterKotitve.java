package com.example.eshepherd;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ListAdapterKotitve extends RecyclerView.Adapter<ListAdapterKotitve.MyViewHolder> {
    ArrayList<Integer> arrayListID;
    ArrayList<String> arrayListDatum;
    Context context;
    private OnClickListener mOnClickListener;
    List<String> filteredUserDataList;

    public ListAdapterKotitve(Context ct, ArrayList<Integer> dataID, ArrayList<String> dataDatum, OnClickListener onClickListener){
        context=ct;
        arrayListID = dataID;
        arrayListDatum = dataDatum;
        this.filteredUserDataList = dataDatum;
        this.mOnClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ListAdapterKotitve.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.one_row_kotitve,parent,false);
        return new MyViewHolder(view, mOnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapterKotitve.MyViewHolder holder, int position) {
        holder.textView1.setText(arrayListID.get(position).toString());
        holder.textView2.setText(filteredUserDataList.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return filteredUserDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textView1, textView2;
        ImageView myImage;
        OnClickListener onClickListener;

        public MyViewHolder(@NonNull View itemView, OnClickListener onClickListener) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.textView_ID);
            textView2 = itemView.findViewById(R.id.textView_Datum);
            myImage = itemView.findViewById(R.id.kotitev_img);
            this.onClickListener = onClickListener;

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            onClickListener.onRowClick(getAdapterPosition());
        }
    }

    public interface OnClickListener{
        void onRowClick(int position);
    }

    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String Key = charSequence.toString();
                if(Key.isEmpty())
                    filteredUserDataList = arrayListDatum;
                else {
                    List<String> lstFiltered = new ArrayList<>();
                    for(String row : arrayListDatum){
                        if(row.toLowerCase().contains(Key.toLowerCase())){
                            lstFiltered.add(row);
                        }
                    }
                    filteredUserDataList = lstFiltered;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredUserDataList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredUserDataList = (List<String>)filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void Clear(){
        arrayListDatum.clear();
        arrayListID.clear();
        filteredUserDataList.clear();
    }

}
