package pjurado.com.viajes.urlCompartida;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import pjurado.com.viajes.Login;
import pjurado.com.viajes.R;
import pjurado.com.viajes.modelo.Lugares;

public class ReceptorUrlFunciona extends AppCompatActivity {
    private RecyclerView rvLugares;
    private  SeleccionarLugarRecyclerViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receptor_url_funciona);



        rvLugares = (RecyclerView) findViewById(R.id.rcv);
        if (rvLugares instanceof RecyclerView) {
            Log.d("recycler", "entro");
            rvLugares.setHasFixedSize(true);
            rvLugares.setLayoutManager(new LinearLayoutManager(this));
            //rvLugares.setLayoutManager(new GridLayoutManager(this, 3));
            FirebaseFirestore miFirebase = FirebaseFirestore.getInstance();

            Query query = miFirebase.collection("Lugares").orderBy("nombre");
            FirestoreRecyclerOptions<Lugares> firestoreRecyclerOptions =
                    new FirestoreRecyclerOptions.Builder<Lugares>()
                            .setQuery(query, Lugares.class)
                            .build();


            adapter = new SeleccionarLugarRecyclerViewAdapter(firestoreRecyclerOptions);

            adapter.notifyDataSetChanged();
            rvLugares.setAdapter(adapter);
        }


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
                Intent i = new Intent(ReceptorUrlFunciona.this, Login.class);

                startActivity(i);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }
    @Override protected void onStart()
    {
        super.onStart();
        adapter.startListening();
    }

    // Function to tell the app to stop getting
    // data from database on stoping of the activity
    @Override protected void onStop()
    {
        super.onStop();
        adapter.stopListening();
    }
}