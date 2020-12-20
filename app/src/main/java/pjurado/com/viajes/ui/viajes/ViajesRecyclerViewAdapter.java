package pjurado.com.viajes.ui.viajes;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import pjurado.com.viajes.R;
import pjurado.com.viajes.modelo.Lugares;
import pjurado.com.viajes.modelo.Viajes;

public class ViajesRecyclerViewAdapter extends FirestoreRecyclerAdapter<Viajes, ViajesRecyclerViewAdapter.ViewHolder> {


    private Context frAct;

   // private final OnLugaresInteractionListener mListener;


    public ViajesRecyclerViewAdapter(FragmentActivity activity, @NonNull FirestoreRecyclerOptions<Viajes> options) {
        super(options);
        frAct = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lugar, parent, false);
        return new ViewHolder(view);
    }


    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull Viajes viaje) {
        holder.mNombre.setText(viaje.getNombre());


        if (viaje.getUrlfoto() != null) {
           if (!viaje.getUrlfoto().isEmpty()) {
               Glide.with(frAct)
                       .load(viaje.getUrlfoto())
                       .into(holder.mFoto);
           }
        }


        holder.btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentSnapshot lugarDocument = getSnapshots().getSnapshot(holder.getAdapterPosition());
                final String id = lugarDocument.getId();
                Bundle idLugar = new Bundle();
                idLugar.putString("Id", id);
                NavController navController = Navigation.findNavController((Activity) frAct, R.id.nav_host_fragment);
                NavigationView navigationView = ((Activity) frAct).findViewById(R.id.nav_view);
                //NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
                NavigationUI.setupWithNavController(navigationView, navController);
                navController.navigate(R.id.editarLugarFragment, idLugar);
            }
        });

        holder.btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Activity) frAct);

                builder.setMessage("¿Esta seguro de borrar " + holder.mNombre.getText() + "?")
                        .setTitle("Borrar lugar");
                // Add the buttons
                builder.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        FirebaseFirestore mFirebaseFireStore= FirebaseFirestore.getInstance();
                        DocumentSnapshot lugarDocument = getSnapshots().getSnapshot(holder.getAdapterPosition());
                        final String id_borrar = lugarDocument.getId();
                        mFirebaseFireStore.collection("Lugares").document(id_borrar)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(frAct, "Lugar Borrado", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("Borrado", "Error deleting document", e);
                                    }
                                });


                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                // Set other dialog properties


                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                builder.show();


            }
        });

        holder.btnVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentSnapshot lugarDocument = getSnapshots().getSnapshot(holder.getAdapterPosition());
                final String id = lugarDocument.getId();
                Bundle idLugar = new Bundle();
                idLugar.putString("Id", id);
                NavController navController = Navigation.findNavController((Activity) frAct, R.id.nav_host_fragment);
                NavigationView navigationView = ((Activity) frAct).findViewById(R.id.nav_view);
                //NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
                NavigationUI.setupWithNavController(navigationView, navController);
                navController.navigate(R.id.verLugarFragment, idLugar);
            }
        });
    }
/*
    @Override
    public int getItemCount() {
        return lugaresList.size();
    }
*/
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNombre;
        public final ImageView mFoto;
        public ImageButton btnEditar;
        public ImageButton btnBorrar;
        public ImageButton btnVer;
        public Lugares mItem;



        public ViewHolder(View itemView) {
            super(itemView);
            Log.d("ViewHolder", "Entro en constructir");
            mView = itemView;
            mNombre = (TextView) mView.findViewById(R.id.textViewViaje);
            mFoto = (ImageView) mView.findViewById(R.id.imageViewLugar);
            btnEditar = mView.findViewById(R.id.imageButtonEditar);
            btnBorrar = mView.findViewById(R.id.imageButtonBorrar);
            btnVer = mView.findViewById(R.id.imageButtonVer);


        }
    }
}
