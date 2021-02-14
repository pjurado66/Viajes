package pjurado.com.viajes.ui.lugares;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import pjurado.com.viajes.R;
import pjurado.com.viajes.modelo.Lugares;
import pjurado.com.viajes.modelo.Viajes;

public class LugaresRecyclerViewAdapter extends FirestoreRecyclerAdapter<Lugares, LugaresRecyclerViewAdapter.ViewHolder> {


    private Context frAct;

   // private final OnLugaresInteractionListener mListener;


    public LugaresRecyclerViewAdapter(FragmentActivity activity, @NonNull FirestoreRecyclerOptions<Lugares> options) {
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
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull Lugares lugar) {
        holder.mNombre.setText(lugar.getNombre());
        //holder.mTiempo.setText(lugar.getTiempoVisita() + "h");

        if (lugar.getUrlfoto() != null) {
           if (!lugar.getUrlfoto().isEmpty()) {
               Glide.with(frAct)
                       .load(lugar.getUrlfoto())
                       .into(holder.mFoto);
           }
        }

        holder.btnCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentSnapshot lugarDocument = getSnapshots().getSnapshot(holder.getAdapterPosition());
                final String id = lugarDocument.getId();
                comparteUsuario(id);
            }
        });
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
                        final FirebaseFirestore mFirebaseFireStore= FirebaseFirestore.getInstance();
                        DocumentSnapshot lugarDocument = getSnapshots().getSnapshot(holder.getAdapterPosition());
                        final String id_borrar = lugarDocument.getId();
                        final String idViaje = (lugarDocument.toObject(Lugares.class)).getViaje();
                        mFirebaseFireStore.collection("Lugares").document(id_borrar)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(frAct, "Lugar Borrado", Toast.LENGTH_SHORT).show();
                                        if (!idViaje.isEmpty()) {
                                            final DocumentReference documentReference =
                                                    mFirebaseFireStore.collection("Viajes").document(idViaje);
                                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    DocumentSnapshot documentSnapshot = task.getResult();
                                                    Viajes viaje = documentSnapshot.toObject(Viajes.class);
                                                    int pos = -1;
                                                    for (int i = 0; i < viaje.getIdLugares().size(); i++) {
                                                        if (viaje.getIdLugares().get(i).getId().equals(id_borrar)) {
                                                            pos = i;
                                                            break;
                                                        }
                                                    }
                                                    if (pos != -1) {
                                                        viaje.getIdLugares().remove(pos);
                                                        documentReference.update("idLugares", viaje.getIdLugares());
                                                    }
                                                }
                                            });

                                        }
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

    private void comparteUsuario(final String idLugar) {
        AlertDialog.Builder builder = new AlertDialog.Builder(frAct);
        // Get the layout inflater
        LayoutInflater inflater = (LayoutInflater) frAct.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View customdialog = inflater.inflate(R.layout.dialogo_usuario, null);
                //frAct.requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(customdialog)
                // Add action buttons
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        final EditText emailuser = customdialog.findViewById(R.id.username);
                        final FirebaseFirestore mFirebaseFireStore= FirebaseFirestore.getInstance();

                        final DocumentReference ref = mFirebaseFireStore.collection("Lugares").document(idLugar);
                        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    final Lugares lugar = documentSnapshot.toObject(Lugares.class);
                                    lugar.getUsuarios().add(emailuser.getText().toString());
                                    ref.update("usuarios", lugar.getUsuarios());
                                    }
                                }

                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //LoginDialogFragment.this.getDialog().cancel();
                    }
                });
        builder.create();
        builder.show();
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
        //public final TextView mTiempo;
        public final ImageView mFoto, mCircle, mCheck;
        public ImageButton btnEditar;
        public ImageButton btnBorrar;
        public ImageButton btnVer;
        public ImageButton btnCompartir;
        public Lugares mItem;



        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            mNombre = (TextView) mView.findViewById(R.id.textViewViaje);
            //mTiempo = (TextView) mView.findViewById(R.id.textViewTiempo);
            mFoto = (ImageView) mView.findViewById(R.id.imageViewLugar);
            mCircle = (ImageView) mView.findViewById(R.id.imageCircle);
            mCheck = (ImageView) mView.findViewById(R.id.imageCheck);
            btnEditar = mView.findViewById(R.id.imageButtonEditar);
            btnBorrar = mView.findViewById(R.id.imageButtonBorrar);
            btnVer = mView.findViewById(R.id.imageButtonVer);
            btnCompartir = mView.findViewById(R.id.imageButtonCompartirLugar);


        }
    }
}
