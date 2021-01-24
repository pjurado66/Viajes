package pjurado.com.viajes.ui.lugares;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import pjurado.com.viajes.R;
import pjurado.com.viajes.modelo.Lugares;
import pjurado.com.viajes.modelo.Viajes;

public class LugaresFragment extends Fragment {
    //private List<Lugares> lugaresList;
    //private LugaresViewModel lugaresViewModel;

    private LugaresRecyclerViewAdapter adapter;
    private FirebaseFirestore miFirebase;

    private RecyclerView rvLugares;

    private CharSequence[] nombreViajes;

    private ArrayList<String> idViajes = new ArrayList<>();
    /*public void setLugaresViewModel(LugaresViewModel listalugares) {
            lugaresViewModel = listalugares;
        }
    */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

/*
        lugaresViewModel =
                ViewModelProviders.of(this).get(LugaresViewModel.class);
        lugaresViewModel.getLugaresList();

 */

        View root = inflater.inflate(R.layout.fragment_lugares, container, false);
        //getActivity().getActionBar().setTitle("Nuevo lugar");
        setHasOptionsMenu(true);

        if (root instanceof RecyclerView) {
            Context context = root.getContext();

             rvLugares = (RecyclerView) root;
            rvLugares.setLayoutManager(new LinearLayoutManager(context));

            miFirebase= FirebaseFirestore.getInstance();

            Query query = miFirebase.collection("Lugares").orderBy("nombre");
            FirestoreRecyclerOptions<Lugares> firestoreRecyclerOptions =
                    new FirestoreRecyclerOptions.Builder<Lugares>()
                    .setQuery(query, Lugares.class)
                    .build();




            adapter = new LugaresRecyclerViewAdapter(getActivity(), firestoreRecyclerOptions);
            adapter.notifyDataSetChanged();
            rvLugares.setAdapter(adapter);


        }

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_baseline_location_city_64_green);
        fab.show();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                //NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
                NavigationUI.setupWithNavController(navigationView, navController);
                navController.navigate(R.id.nuevoLugarFragment);

            }
        });

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_lugares,menu);

        MenuItem item = menu.findItem(R.id.nav_buscar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchData(newText);
                return false;
            }
        });
    }

    private void searchData(String texto) {
        Query query;
        if (texto.isEmpty()){
            query = miFirebase.collection("Lugares")
                    .orderBy("nombre");
        }
        else{
            query = miFirebase.collection("Lugares")
                    //.whereEqualTo("nombre", texto)
                    .whereGreaterThanOrEqualTo("nombre", texto)
                    .orderBy("nombre");
        }


        FirestoreRecyclerOptions<Lugares> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Lugares>()
                        .setQuery(query, Lugares.class)
                        .build();
        adapter.updateOptions(firestoreRecyclerOptions);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_filtrar) {
            llenaViajes();
        }
        return super.onOptionsItemSelected(item);

    }

    private void dialogEscogerViaje() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Selecciona Viaje");
// add a radio button list

        final int checkedItem = 0; // cow
        builder.setSingleChoiceItems(nombreViajes, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user checked an item

            }
        });
// add OK and Cancel buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user clicked OK
                filtraPorViaje(idViajes.get(checkedItem));
            }
        });
        builder.setNegativeButton("Cancel", null);
// create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void filtraPorViaje(String idViaje) {
        Query query = miFirebase.collection("Lugares")
                    .whereEqualTo("viaje", idViaje)
                    .orderBy("nombre");

        FirestoreRecyclerOptions<Lugares> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Lugares>()
                        .setQuery(query, Lugares.class)
                        .build();
        adapter.updateOptions(firestoreRecyclerOptions);

    }

    public void llenaViajes(){
        miFirebase.collection("Viajes")
            .orderBy("nombre").get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()) {

                ArrayList<String> viajeslista = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Viajes viaje = document.toObject(Viajes.class);
                    viajeslista.add(viaje.getNombre());
                    idViajes.add(document.getId());
                }
                nombreViajes = new CharSequence[viajeslista.size()];
                for (int i = 0; i<viajeslista.size(); i++){
                    nombreViajes[i] = viajeslista.get(i);
                }
                dialogEscogerViaje();
                //creaSpinnerLugares();
            } else {
                Log.d("TAG", "Error getting documents: ", task.getException());
            }

        }});
    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }


    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}