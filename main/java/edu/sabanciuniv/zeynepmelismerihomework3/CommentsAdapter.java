package edu.sabanciuniv.zeynepmelismerihomework3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {

    List<CommentItem> commentsItems;
    Context context2;

    public CommentsAdapter(List<CommentItem> commentsItems, Context context2) {
        this.commentsItems = commentsItems;
        this.context2 = context2;

    }

    @NonNull
    @Override
    public CommentsAdapter.CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context2).inflate(R.layout.comments_row_layout,parent,false);
        return new CommentsAdapter.CommentsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.CommentsViewHolder holder, int position) {
        holder.commenterName.setText(commentsItems.get(position).getName());
        holder.txtComment.setText(commentsItems.get(position).getMessage());
    }



    @Override
    public int getItemCount() {
        return commentsItems.size();
    }

    class CommentsViewHolder extends RecyclerView.ViewHolder{
        TextView commenterName;
        TextView txtComment;
        ConstraintLayout root;



        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            commenterName = itemView.findViewById(R.id.commentername);
            txtComment = itemView.findViewById(R.id.txtcomment);
            root = itemView.findViewById(R.id.container2);
        }
    }
}
