package pjurado.com.viajes.ui.viajes;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pjurado.com.viajes.R;
import pjurado.com.viajes.modelo.Lugares;
import pjurado.com.viajes.modelo.Viajes;

public class ViajesRecyclerViewAdapter
        extends FirestoreRecyclerAdapter<Viajes, ViajesRecyclerViewAdapter.ViewHolder> {


    private Context frAct;

   // private final OnLugaresInteractionListener mListener;


    public ViajesRecyclerViewAdapter(FragmentActivity activity, @NonNull FirestoreRecyclerOptions<Viajes> options) {
        super(options);
        frAct = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_viaje, parent, false);
        return new ViewHolder(view);
    }


    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Viajes viaje) {
        holder.mItem = viaje;
        holder.mNombre.setText(viaje.getNombre());


        if (viaje.getUrlfoto() != null) {
           if (!viaje.getUrlfoto().isEmpty()) {
               Glide.with(frAct)
                       .load(viaje.getUrlfoto())
                       .into(holder.mFoto);
           }
        }

        holder.btnCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentSnapshot viajeDocument = getSnapshots().getSnapshot(holder.getAdapterPosition());
                String id = viajeDocument.getId();
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
                navController.navigate(R.id.editarViajeFragment, idLugar);
            }
        });


        holder.btnAnadirLugares.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentSnapshot viajeDocument = getSnapshots().getSnapshot(holder.getAdapterPosition());
                final String id = viajeDocument.getId();
                Bundle idLugar = new Bundle();
                idLugar.putString("Id", id);
                idLugar.putSerializable("Viaje", viaje);
                
                NavController navController = Navigation.findNavController((Activity) frAct, R.id.nav_host_fragment);
                NavigationView navigationView = ((Activity) frAct).findViewById(R.id.nav_view);
                NavigationUI.setupWithNavController(navigationView, navController);
                navController.navigate(R.id.lugaresViajeFragment, idLugar);
            }
        });


        holder.btnOrdenar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DocumentSnapshot viajeDocument = getSnapshots().getSnapshot(holder.getAdapterPosition());
                final String id = viajeDocument.getId();
                Bundle idViaje = new Bundle();
                idViaje.putString("Id", id);
                idViaje.putSerializable("Viaje", viaje);

                NavController navController = Navigation.findNavController((Activity) frAct, R.id.nav_host_fragment);
                NavigationView navigationView = ((Activity) frAct).findViewById(R.id.nav_view);
                //NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
                NavigationUI.setupWithNavController(navigationView, navController);
                navController.navigate(R.id.ordenarLugaresViajeFragment, idViaje);


            }
        });


        holder.btnVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle idViaje = new Bundle();
                idViaje.putSerializable("Viaje", viaje);


                NavController navController = Navigation.findNavController((Activity) frAct, R.id.nav_host_fragment);
                NavigationView navigationView = ((Activity) frAct).findViewById(R.id.nav_view);
                //NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
                NavigationUI.setupWithNavController(navigationView, navController);
                navController.navigate(R.id.rutaFragment, idViaje);
            }
        });

        holder.btnRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle idViaje = new Bundle();
                idViaje.putSerializable("Viaje", viaje);


                NavController navController = Navigation.findNavController((Activity) frAct, R.id.nav_host_fragment);
                NavigationView navigationView = ((Activity) frAct).findViewById(R.id.nav_view);
                //NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
                NavigationUI.setupWithNavController(navigationView, navController);
                navController.navigate(R.id.recorridoFragment, idViaje);
            }
        });
    }

    private void calcularRuta() {
        RequestQueue queue = Volley.newRequestQueue(frAct);
        String url = "https://maps.open-street.com/api/route/?origin=48.856614,2.3522219&destination=45.764043,4.835659&mode=driving&key=d608057cddca02bb1bc33d5c98cb1b60";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jso = new JSONObject(response);
                    //JSONArray dist =  jso.getJSONArray("total_distance");
                    int dist =  jso.getInt("total_distance");
                    int ini = response.indexOf("total_distance");
                    String distancia = response.substring(ini+17);
                    int fin = distancia.indexOf(",");
                    distancia = distancia.substring(0, fin);
                    Log.i("Ruta", distancia);
                    Log.i("json", ""+dist);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }

    /*
        @Override
        public int getItemCount() {
            return lugaresList.size();
        }
    */
    private void comparteUsuario(final String idViaje) {
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

                        final DocumentReference ref = mFirebaseFireStore.collection("Viajes").document(idViaje);
                        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    final Viajes viaje = documentSnapshot.toObject(Viajes.class);
                                    for (int i = 0; i < viaje.getIdLugares().size(); i++){
                                        final DocumentReference refLugar = mFirebaseFireStore.collection("Lugares").document(viaje.getIdLugares().get(i).getId());
                                        refLugar.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()){
                                                    DocumentSnapshot documentSnapshot = task.getResult();
                                                    final Lugares lugar = documentSnapshot.toObject(Lugares.class);
                                                    lugar.getUsuarios().add(emailuser.getText().toString());
                                                    refLugar.update("usuarios", lugar.getUsuarios());
                                                }
                                            }
                                        });
                                    }

                                    viaje.getUsuarios().add(emailuser.getText().toString());
                                    ref.update("usuarios", viaje.getUsuarios());
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNombre;
        public final ImageView mFoto;
        public ImageButton btnEditar;
        public ImageButton btnBorrar;
        public ImageButton btnOrdenar;
        public ImageButton btnVer;
        public ImageButton btnAnadirLugares;
        public ImageButton btnCompartir;
        public ImageButton btnRuta;

        public Viajes mItem;



        public ViewHolder(View itemView) {
            super(itemView);
            Log.d("ViewHolder", "Entro en constructir");
            mView = itemView;
            mNombre = (TextView) mView.findViewById(R.id.textViewViaje);
            mFoto = (ImageView) mView.findViewById(R.id.imageViewViaje);
            btnEditar = mView.findViewById(R.id.imageButtonEditarViaje);
            btnOrdenar = mView.findViewById(R.id.imageButtonOrdenarViaje);
            btnVer = mView.findViewById(R.id.imageButtonVerViaje);
            btnAnadirLugares = mView.findViewById((R.id.imageViewAnadirLugaresAViaje));
            btnCompartir = mView.findViewById(R.id.imageButtonCompartirViaje);
            btnRuta = mView.findViewById(R.id.imageButtonRuta);

        }


    }
}
