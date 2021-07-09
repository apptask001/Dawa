package tzdawa.app.mwakalonga.dawa.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import tzdawa.app.mwakalonga.dawa.R;
import tzdawa.app.mwakalonga.dawa.models.fragment_items_dawa;

public class dawa_list_fragment_adp extends RecyclerView.Adapter<dawa_list_fragment_adp.dawa_list_view_holder> {
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private final ArrayList<fragment_items_dawa> fragment_items_dawa;
    private final OnItemClickListener mListener;

    public dawa_list_fragment_adp(ArrayList<fragment_items_dawa> fragment_items_dawa, OnItemClickListener mListener) {
        this.fragment_items_dawa = fragment_items_dawa;
        this.mListener = mListener;
    }


    @NonNull
    @Override
    public dawa_list_view_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_layout_items_dawa, parent, false);
        return new dawa_list_view_holder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull dawa_list_view_holder holder, int position) {
        fragment_items_dawa items_general = fragment_items_dawa.get(position);
        holder.textView1.setText(items_general.getTextview1().toUpperCase());
        holder.textView2.setText(items_general.getTextview2().toUpperCase());
    }

    @Override
    public int getItemCount() {
        return fragment_items_dawa.size();
    }

    public static class dawa_list_view_holder extends RecyclerView.ViewHolder {
        private final TextView textView1;
        private final TextView textView2;

        public dawa_list_view_holder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            textView1 = itemView.findViewById(R.id.ltextview1);
            textView2 = itemView.findViewById(R.id.ltextview2);

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
