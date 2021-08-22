package tzdawa.app.mwakalonga.dawa.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import tzdawa.app.mwakalonga.dawa.R;
import tzdawa.app.mwakalonga.dawa.models.fragment_items_posts;

public class posts_list_fragment_adp extends RecyclerView.Adapter<posts_list_fragment_adp.posts_list_fragment_holder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private ArrayList<fragment_items_posts> fragment_items_posts;
    private OnItemClickListener mListener;


    public posts_list_fragment_adp(ArrayList<fragment_items_posts> fragment_items_posts, OnItemClickListener mListener) {
        this.fragment_items_posts = fragment_items_posts;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public posts_list_fragment_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_posts_layout, parent, false);
        return new posts_list_fragment_adp.posts_list_fragment_holder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull posts_list_fragment_holder holder, int position) {
        fragment_items_posts items_general = fragment_items_posts.get(position);
        holder.postTitletxtx.setText(items_general.getPostTitle().toUpperCase());
        Picasso.get().load(items_general.getPostImage().trim()).into(holder.postImageThumbnail);
    }

    @Override
    public int getItemCount() {
        return fragment_items_posts.size();
    }

    public static class posts_list_fragment_holder extends RecyclerView.ViewHolder {
        private TextView postTitletxtx;
        private ImageView postImageThumbnail;

        public posts_list_fragment_holder(@NonNull View itemView, final posts_list_fragment_adp.OnItemClickListener listener) {
            super(itemView);

            postTitletxtx = itemView.findViewById(R.id.postTitletxtx);
            postImageThumbnail = itemView.findViewById(R.id.postImageThumbnail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}