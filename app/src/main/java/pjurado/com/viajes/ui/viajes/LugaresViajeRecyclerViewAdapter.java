package pjurado.com.viajes.ui.viajes;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import pjurado.com.viajes.R;
import pjurado.com.viajes.dummy.DummyContent.DummyItem;
import pjurado.com.viajes.modelo.Lugares;
import pjurado.com.viajes.ui.lugares.LugaresRecyclerViewAdapter;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class LugaresViajeRecyclerViewAdapter extends FirestoreRecyclerAdapter<Lugares, LugaresViajeRecyclerViewAdapter.ViewHolder> {

    private Context frAct;

    public LugaresViajeRecyclerViewAdapter(FragmentActivity activity, @NonNull FirestoreRecyclerOptions<Lugares> options) {
        super(options);
        frAct = activity;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lugares_viaje, parent, false);
        return new ViewHolder(view);
    }

  /*
    @Override
    public int getItemCount() {
        return mValues.size();
    }
    */

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Lugares model) {

        holder.tvNombreLugarViaje.setText(model.getNombre());
        if (model.getUrlfoto() != null) {
            if (!model.getUrlfoto().isEmpty()) {
                Glide.with(frAct)
                        .load(model.getUrlfoto())
                        .into(holder.ivFotoLugarViaje);
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvNombreLugarViaje;
        public final ImageView ivFotoLugarViaje;
        public Lugares mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvNombreLugarViaje = (TextView) view.findViewById(R.id.textViewNombreLugarViaje);
            ivFotoLugarViaje = (ImageView) view.findViewById(R.id.imageViewLugarViaje);
        }

    }
}