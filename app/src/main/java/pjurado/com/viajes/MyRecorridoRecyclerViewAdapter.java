package pjurado.com.viajes;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pjurado.com.viajes.dummy.DummyContent.DummyItem;
import pjurado.com.viajes.modelo.Recorrido;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyRecorridoRecyclerViewAdapter extends RecyclerView.Adapter<MyRecorridoRecyclerViewAdapter.ViewHolder> {

    private final Recorrido[] mValues;

    public MyRecorridoRecyclerViewAdapter(Recorrido[] items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_recorrido_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues[position];
        holder.origen.setText(mValues[position].getOrigen() +
                " - " + mValues[position].getDestino());
        //holder.destino.setText(mValues.get(position).getDestino());
        holder.tiempo.setText(""+mValues[position].getTiempo());
        holder.distancia.setText(""+mValues[position].getDistancia()+ " Km.");
    }

    @Override
    public int getItemCount() {
        return mValues.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView origen;
        //public final TextView destino;
        public final TextView tiempo;
        public final TextView distancia;
        public Recorrido mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            origen = (TextView) view.findViewById(R.id.origen);
            //destino = (TextView) view.findViewById(R.id.destino);
            tiempo = (TextView) view.findViewById(R.id.tiempo);
            distancia = (TextView) view.findViewById(R.id.distancia);
        }

    }
}