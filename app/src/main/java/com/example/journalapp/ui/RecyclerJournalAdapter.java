package com.example.journalapp.ui;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.journalapp.R;
import com.example.journalapp.model.SuitcaseJournal;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerJournalAdapter extends RecyclerView.Adapter<RecyclerJournalAdapter.ViewHolder> {

    Context context;
    ArrayList<SuitcaseJournal> journalArrayList;


    public RecyclerJournalAdapter(Context context, ArrayList<SuitcaseJournal> journalArrayList) {
        this.context = context;
        this.journalArrayList = journalArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_row,parent,false),context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SuitcaseJournal journal=journalArrayList.get(position);
        holder.titletv.setText(journal.getTitle());
        holder.thoughttv.setText(journal.getThought());
        holder.nametv.setText(journal.getUserName());

        String imageUrl=journal.getImageUrl();

        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.image_three)
                .fit()
                .into(holder.imageView);
        String timeago= (String) DateUtils.getRelativeTimeSpanString(journal
                .getTimeAdded()
                .getSeconds()*1000);

        holder.datetv.setText(timeago);

    }

    @Override
    public int getItemCount() {
        return journalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titletv,thoughttv,datetv,nametv;
        ImageView imageView;
        ImageButton sharebtn;
        public ViewHolder(@NonNull View itemView, final Context context) {
            super(itemView);

            titletv=itemView.findViewById(R.id.listtitletv);
            thoughttv=itemView.findViewById(R.id.listthoughttv);
            imageView=itemView.findViewById(R.id.listiv);
            datetv=itemView.findViewById(R.id.listtimestamp);
            nametv=itemView.findViewById(R.id.listusername);
            sharebtn=itemView.findViewById(R.id.listshare);

            sharebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SuitcaseJournal journal=journalArrayList.get(getAdapterPosition());
                    Intent intent=new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT,"Title: "+journal.getTitle()+"\n");
                    intent.putExtra(Intent.EXTRA_TEXT," Thought:"+journal.getThought());
                    context.startActivity(intent);

                }
            });

        }
    }
}
