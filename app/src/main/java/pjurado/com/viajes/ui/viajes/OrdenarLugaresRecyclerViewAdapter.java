package pjurado.com.viajes.ui.viajes;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import pjurado.com.viajes.R;
import pjurado.com.viajes.modelo.Lugares;
import pjurado.com.viajes.modelo.PosicionLugarEnViaje;
import pjurado.com.viajes.modelo.Viajes;

public class OrdenarLugaresRecyclerViewAdapter extends RecyclerView.Adapter<OrdenarLugaresRecyclerViewAdapter.MyViewHolder>  {
    FirebaseFirestore mFirebaseFireStore;
    private ArrayList<PosicionLugarEnViaje> listaLugaresOrdenados;
    private Viajes viaje;
    public OrdenarLugaresRecyclerViewAdapter(Viajes viaje){
        this.viaje = viaje;
        listaLugaresOrdenados = viaje.getIdLugares();
        mFirebaseFireStore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ordenar_lugares_viaje, parent,  false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final MyViewHolder h = holder;
        String idLugar = listaLugaresOrdenados.get(position).getId();

        DocumentReference documentReference = mFirebaseFireStore.
                collection("Lugares").document(idLugar);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Leo", "DocumentSnapshot data: " + document.getData());
                        holder.lugar = document.toObject(Lugares.class);
                        holder.tvLugar.setText(holder.lugar.getNombre());
                    } else {
                        Log.d("Leo", "No such document");
                    }
                } else {
                    Log.d("Leo", "get failed with ", task.getException());
                }
            }
        });



        String pos = String.valueOf(listaLugaresOrdenados.get(position).getPosicion());
        holder.tvOrdenLugar.setText(pos);
    }

    @Override
    public int getItemCount() {
        return listaLugaresOrdenados.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView tvLugar;
        public TextView tvOrdenLugar;
        public Lugares lugar;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            tvLugar = mView.findViewById(R.id.textViewLugarOrdenar);
            tvOrdenLugar = mView.findViewById(R.id.textViewOrden);

        }
    }

}
