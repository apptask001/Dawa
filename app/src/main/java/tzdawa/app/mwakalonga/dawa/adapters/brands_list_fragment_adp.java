package tzdawa.app.mwakalonga.dawa.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

import tzdawa.app.mwakalonga.dawa.R;
import tzdawa.app.mwakalonga.dawa.models.fragment_items_brands;

public class brands_list_fragment_adp extends RecyclerView.Adapter<brands_list_fragment_adp.brands_list_view_holder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private final ArrayList<fragment_items_brands> fragment_items_brands;
    private final OnItemClickListener mListener;

    public brands_list_fragment_adp(ArrayList<tzdawa.app.mwakalonga.dawa.models.fragment_items_brands> fragment_items_brands, OnItemClickListener mListener) {
        this.fragment_items_brands = fragment_items_brands;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public brands_list_view_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_layout_items_brands, parent, false);
        return new brands_list_view_holder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull brands_list_view_holder holder, int position) {
        fragment_items_brands items_general = fragment_items_brands.get(position);
        holder.brandnametxt.setText(items_general.getMname());
        Picasso.get().load(items_general.getMimage().trim()).into(holder.brandnameimg);

        double amount = Double.parseDouble((items_general.getMprice()));
        DecimalFormat formatter = new DecimalFormat("#,###");
        holder.brandpricetxt.setText(String.format("%s Tsh", (formatter.format(amount))));
    }

    @Override
    public int getItemCount() {
        return fragment_items_brands.size();
    }

    public static class brands_list_view_holder extends RecyclerView.ViewHolder {
        private TextView brandnametxt;
        private ImageView brandnameimg;
        private TextView brandpricetxt;

        public brands_list_view_holder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            brandnametxt = itemView.findViewById(R.id.brandnametxt);
            brandnameimg = itemView.findViewById(R.id.brandnameimg);
            brandpricetxt = itemView.findViewById(R.id.brandpricetxt);


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
