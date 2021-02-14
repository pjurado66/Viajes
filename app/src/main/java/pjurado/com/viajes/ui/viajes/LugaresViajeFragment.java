package pjurado.com.viajes.ui.viajes;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;

import pjurado.com.viajes.R;
import pjurado.com.viajes.modelo.Lugares;
import pjurado.com.viajes.modelo.PosicionLugarEnViaje;
import pjurado.com.viajes.modelo.Viajes;

public class LugaresViajeFragment extends Fragment {

    private LugaresViajeRecyclerViewAdapter adapter;
    private FirebaseFirestore miFirebase;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();
        View root = inflater.inflate(R.layout.fragment_lugares_viaje_list, container, false);
        //getActivity().getActionBar().setTitle("Nuevo lugar");

        String id = getArguments().getString("Id");
        Viajes viaje = (Viajes) getArguments().getSerializable("Viaje");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Añadir lugares al viaje a "+ viaje.getNombre());
        ArrayList<PosicionLugarEnViaje> lugaresEnViaje = new ArrayList<>();
        lugaresEnViaje = viaje.getIdLugares();
        if (root instanceof RecyclerView) {
            Context context = root.getContext();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            RecyclerView rvLugares = (RecyclerView) root;
            //rvLugares.setLayoutManager(new LinearLayoutManager(context));
            rvLugares.setLayoutManager(new GridLayoutManager(context, 2));
            miFirebase = FirebaseFirestore.getInstance();

            Query query = miFirebase.collection("Lugares")
                    .whereIn("viaje", Arrays.asList(id, ""))
                    .whereArrayContains("usuarios", user.getEmail().toString())
                    .orderBy("nombre");
            FirestoreRecyclerOptions<Lugares> firestoreRecyclerOptions =
                    new FirestoreRecyclerOptions.Builder<Lugares>()
                            .setQuery(query, Lugares.class)
                            .build();


            adapter = new LugaresViajeRecyclerViewAdapter(getActivity(),
                    firestoreRecyclerOptions, id, lugaresEnViaje);
            adapter.notifyDataSetChanged();
            rvLugares.setAdapter(adapter);


        }



        return root;
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