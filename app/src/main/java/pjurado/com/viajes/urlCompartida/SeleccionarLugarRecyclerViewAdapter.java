package pjurado.com.viajes.urlCompartida;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import pjurado.com.viajes.R;
import pjurado.com.viajes.modelo.Lugares;
import pjurado.com.viajes.modelo.PosicionLugarEnViaje;
import pjurado.com.viajes.modelo.Viajes;


public class SeleccionarLugarRecyclerViewAdapter
        extends FirestoreRecyclerAdapter<Lugares,
        SeleccionarLugarRecyclerViewAdapter.ViewHolder> {


    private FirebaseFirestore mFirebaseFireStore;


    public SeleccionarLugarRecyclerViewAdapter( @NonNull FirestoreRecyclerOptions<Lugares> options) {
        super(options);





    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selecciona_lugar, parent, false);

        return new ViewHolder(view);
    }


    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull Lugares model) {
        DocumentSnapshot lugarDocument = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = lugarDocument.getId();

        holder.tvNombreLugar.setText(model.getNombre());

/*        Log.d("IDs", "Cargo Lugar " +id);
        if (isLugarEnViaje(id) >= 0){
            holder.ivCheck.setVisibility(View.VISIBLE);
        }

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int posLugar = isLugarEnViaje(id);
                if ( posLugar <  0) {
                    //
                    holder.ivCheck.setVisibility(View.VISIBLE);


                    PosicionLugarEnViaje lugarvisitar = new PosicionLugarEnViaje();
                    lugarvisitar.setId(id);
                    lugarvisitar.setPosicion(listaLugares.size());
                    listaLugares.add(lugarvisitar);




                }
                else{
                    holder.ivCheck.setVisibility(View.INVISIBLE);
                    listaLugares.remove(posLugar);

                }

                DocumentReference docViaje = mFirebaseFireStore.collection("Viajes").document(idViaje);
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
*/
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvNombreLugar;
        public Lugares mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvNombreLugar = view.findViewById(R.id.textViewNombreSeleccionaLugar);
            /*
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //ivCheck.setVisibility(View.INVISIBLE);
                    ivCheck.setVisibility(View.VISIBLE);

                    return false;
                }
            });

             */

        }

    }
//Cambiada por tarea asíncrona
    /*
    public boolean recuperarDatosViaje(){
        Log.d("IDs", "Busco viaje" + idViaje);
        mFirebaseFireStore.collection("Viajes").document(idViaje).get().
                addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("IDs", "Lo encuentro");
                if (documentSnapshot.exists()){
                        viaje = documentSnapshot.toObject(Viajes.class);
                        listaLugares = viaje.getIdLugares();
                    Log.d("IDs", "Asigno " + listaLugares.size() + " Lugares");
                }
            }
        });
        return true;
    }

     */


}