package pjurado.com.viajes.urlCompartida;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;

import pjurado.com.viajes.Login;
import pjurado.com.viajes.R;
import pjurado.com.viajes.modelo.Lugares;
import pjurado.com.viajes.modelo.Viajes;

public class ReceptorUrl extends AppCompatActivity {
    private RecyclerView rvLugares;
    //private  SeleccionarLugarRecyclerViewAdapter adapter;
    private FirebaseFirestore miFirebase;
    private String idViajeSeleccionado;
    private String idLugarSeleccionado;
    private int tipoSeleccionado;

    private final static ArrayList<String> nombreViajes = new ArrayList<>();
    private ArrayList<String> idViajes = new ArrayList<>();
    private final static ArrayList<String> nombreLugares = new ArrayList<>();
    private ArrayList<String> idLugares = new ArrayList<>();
    private final static String[] tipos = { "Área", "Parking", "Información" };

    private Spinner spnLugares;
    private String urlCompartido;
    private Spinner spnViajes;

    private ImageButton btnSalvar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receptor_url);


        miFirebase = FirebaseFirestore.getInstance();
        nombreLugares.clear();
        nombreViajes.clear();
        sesion();
        llenarSpinnerViajes();
/*
        miFirebase.collection("Lugares")
                .orderBy("nombre")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                Lugares lugar = document.toObject(Lugares.class);
                                nombreLugares.add((document.toObject(Lugares.class)).getNombre());
                                idLugares.add(document.getId());
                                //documentSnapshot.toObject(City.class);

                            }
                            creaSpinnerLugares();
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }

                    }
                });
*/
        creaSpinnerTipos();
        urlCompartido  = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        WebView miVisorWeb = (WebView) findViewById(R.id.visorWeb);

        miVisorWeb.loadUrl(urlCompartido);

        btnSalvar = findViewById(R.id.salvarEnlace);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
                finish();
            }
        });
    }

    private void llenarSpinnerViajes() {
        miFirebase.collection("Viajes")
                .orderBy("nombre")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                Viajes viaje = document.toObject(Viajes.class);
                                if (document.toObject(Viajes.class).getIdLugares().size()>0) {
                                    nombreViajes.add((document.toObject(Viajes.class)).getNombre());
                                    idViajes.add(document.getId());
                                    //documentSnapshot.toObject(City.class);
                                }

                            }
                            creaSpinnerViajes();
                            //creaSpinnerLugares();
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }

                    }
                });

    }

    private void creaSpinnerViajes() {
        spnViajes = (Spinner) findViewById(R.id.spinnerViaje);
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                nombreViajes);
        //android.R.layout.simple_spinner_item
        //android.R.layout.simple_spinner_dropdown_item
        spnViajes.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        spnViajes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idViajeSeleccionado = idViajes.get(position);
                spnViajes.setSelection(position);
                //Log.d("Spinner", nombreLugares.get(position));
                llenarSpinnerLugares();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("Spinner", "Sin seleccion");
            }
        });
    }

    private void llenarSpinnerLugares() {
        miFirebase.collection("Viajes").document(idViajeSeleccionado)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final Viajes viaje = documentSnapshot.toObject(Viajes.class);
                for (int i=0; i<viaje.getIdLugares().size(); i++){
                    idLugares.add(viaje.getIdLugares().get(i).getId());
                    miFirebase.collection("Lugares").document(viaje.getIdLugares().get(i).getId())
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Lugares lugar = documentSnapshot.toObject(Lugares.class);
                            nombreLugares.add(lugar.getNombre());
                            if (viaje.getIdLugares().size() == nombreLugares.size()) {creaSpinnerLugares();}
                        }
                    });
                }

            }
        });
    }


    private void guardar() {
        miFirebase.collection("Lugares").document(idLugarSeleccionado)
            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Lugares lugar = documentSnapshot.toObject(Lugares.class);
                DocumentReference docViaje = miFirebase.collection("Lugares").document(idLugarSeleccionado);
                switch (tipoSeleccionado) {
                    case 0:
                        lugar.getAreas().add(urlCompartido);
                        docViaje.update("areas", lugar.getAreas());
                        break;
                    case 1:
                        lugar.getParking().add(urlCompartido);
                        docViaje.update("parking", lugar.getParking());
                        break;
                    case 2:
                        lugar.getInformacion().add(urlCompartido);
                        docViaje.update("informacion", lugar.getInformacion());
                        break;
                }


            }
        });
    }


    private void creaSpinnerTipos() {
        Spinner spnTipos = findViewById(R.id.spinnerTipos);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, tipos);
        spnTipos.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        spnTipos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipoSeleccionado = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void creaSpinnerLugares() {
        spnLugares = (Spinner) findViewById(R.id.spinnerLugar);
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                nombreLugares);
        //android.R.layout.simple_spinner_item
        //android.R.layout.simple_spinner_dropdown_item
        spnLugares.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        spnLugares.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idLugarSeleccionado = idLugares.get(position);
                spnLugares.setSelection(position);
                Log.d("Spinner", nombreLugares.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("Spinner", "Sin seleccion");
            }
        });
    }

    public void sesion(){
        SharedPreferences mFilePreferences = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        if (mFilePreferences.getString("email", null) == null){
            showMain(mFilePreferences.getString("email", null));
        }
    }

    void showMain(String email){
        //updateUI(AplicacionViajes.user);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Login");
        builder.setMessage("No ha iniciado sesión");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(ReceptorUrl.this, Login.class);

                startActivity(i);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }


}