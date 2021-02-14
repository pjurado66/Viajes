package pjurado.com.viajes.ui.viajes;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import pjurado.com.viajes.R;
import pjurado.com.viajes.modelo.Lugares;
import pjurado.com.viajes.modelo.PosicionLugarEnViaje;
import pjurado.com.viajes.modelo.Viajes;


public class LugaresViajeRecyclerViewAdapter
        extends FirestoreRecyclerAdapter<Lugares,
        LugaresViajeRecyclerViewAdapter.ViewHolder> {

    private Context frAct;
    private String idViaje;
    private ArrayList<PosicionLugarEnViaje> listaLugares = new ArrayList<>();
    private FirebaseFirestore mFirebaseFireStore;
    private Viajes viaje;


    public LugaresViajeRecyclerViewAdapter(FragmentActivity activity,
                                           @NonNull FirestoreRecyclerOptions<Lugares> options,
                                           String id,
                                           ArrayList<PosicionLugarEnViaje> lugaresDelViaje) {
        super(options);
        frAct = activity;
        idViaje = id;
        mFirebaseFireStore= FirebaseFirestore.getInstance();
        listaLugares = lugaresDelViaje;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lugares_viaje, parent, false);
        return new ViewHolder(view);
    }


    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Lugares model) {
        DocumentSnapshot lugarDocument = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = lugarDocument.getId();

        holder.tvNombreLugarViaje.setText(model.getNombre());
        if (model.getUrlfoto() != null) {
            if (!model.getUrlfoto().isEmpty()) {
                Glide.with(frAct)
                        .load(model.getUrlfoto())
                        .into(holder.ivFotoLugarViaje);
            }
        }
        Log.d("IDs", "Cargo Lugar " +id);
        if (isLugarEnViaje(id) >= 0){
            holder.ivCheck.setVisibility(View.VISIBLE);
        }
        else {
            holder.ivCheck.setVisibility(View.INVISIBLE);
        }

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int posLugar = isLugarEnViaje(id);
                DocumentReference docViaje = mFirebaseFireStore.collection("Viajes").document(idViaje);

                if ( posLugar <  0) {
                    //
                    holder.ivCheck.setVisibility(View.VISIBLE);
                    //Añade id del viaje al lugar

                    PosicionLugarEnViaje lugarvisitar = new PosicionLugarEnViaje();
                    lugarvisitar.setId(id);
                    lugarvisitar.setPosicion(listaLugares.size());
                    listaLugares.add(lugarvisitar);
                    final DocumentReference docLugar = mFirebaseFireStore.collection("Lugares").
                            document(id);
                    docLugar.update("viaje", idViaje);

                    docViaje.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                final Viajes viaje = documentSnapshot.toObject(Viajes.class);
//----------Hay que añadirle usuarios del viaje ----------------
                                for (int i = 0; i < model.getUsuarios().size(); i++){
                                    model.getUsuarios().remove(0);
                                }
                                for (int i = 0; i < viaje.getUsuarios().size(); i++){
                                    model.getUsuarios().add(viaje.getUsuarios().get(i));
                                }
                                docLugar.update("usuarios", model.getUsuarios());
                            }
                        }
                    });
                }
                else{
                    holder.ivCheck.setVisibility(View.INVISIBLE);
                    listaLugares.remove(posLugar);
                    DocumentReference docLugar = mFirebaseFireStore.collection("Lugares").document(id);
                    docLugar.update("viaje", "");
                }


                docViaje.update("idLugares", listaLugares)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void avoid) {
                                //Toast.makeText(, "El viaje se creo correctamente", Toast.LENGTH_SHORT).show();
                                Log.d("Grabar", "Ok");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(getActivity(), "Error al crear el viaje", Toast.LENGTH_SHORT).show();
                        Log.d("Grabar", e.toString());
                    }
                });
                return false;

            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvNombreLugarViaje;
        public final ImageView ivFotoLugarViaje;
        public final ImageView ivCheck;
        public Lugares mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvNombreLugarViaje = (TextView) view.findViewById(R.id.textViewNombreLugarViaje);
            ivFotoLugarViaje = (ImageView) view.findViewById(R.id.imageViewLugarViaje);
            ivCheck = (ImageView) view.findViewById(R.id.imageViewCheck);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //ivCheck.setVisibility(View.INVISIBLE);
                    ivCheck.setVisibility(View.VISIBLE);

                    return false;
                }
            });

        }

    }


    public int isLugarEnViaje(String id){

        Log.d("IDs", "Comprobando " + id + " Registros "+listaLugares.size());
        for (int i=0; i < listaLugares.size(); i++){

            if (listaLugares.get(i).getId().equals(id)){
                Log.d("IDs", listaLugares.get(i).getId() + " -- " + id);
                Log.d("IDs", "igual");
                return i;
            }
        }
        return -1;

    }

}