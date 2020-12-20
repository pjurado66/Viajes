package pjurado.com.viajes.ui.viajes;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import pjurado.com.viajes.R;
import pjurado.com.viajes.modelo.Lugares;
import pjurado.com.viajes.modelo.Viajes;
import pjurado.com.viajes.ui.lugares.LugaresRecyclerViewAdapter;

public class ViajesFragment extends Fragment {
    //private List<Lugares> lugaresList;
    //private LugaresViewModel lugaresViewModel;

    private ViajesRecyclerViewAdapter adapter;
    private FirebaseFirestore miFirebase;



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
        View root = inflater.inflate(R.layout.fragment_viajes, container, false);
        //getActivity().getActionBar().setTitle("Nuevo lugar");

        if (root instanceof RecyclerView) {
            Context context = root.getContext();

            RecyclerView rvLugares = (RecyclerView) root;
            rvLugares.setLayoutManager(new LinearLayoutManager(context));

            miFirebase= FirebaseFirestore.getInstance();

            Query query = miFirebase.collection("Viajes").orderBy("nombre");
            FirestoreRecyclerOptions<Viajes> firestoreRecyclerOptions =
                    new FirestoreRecyclerOptions.Builder<Viajes>()
                    .setQuery(query, Viajes.class)
                    .build();




            adapter = new ViajesRecyclerViewAdapter(getActivity(), firestoreRecyclerOptions);
            adapter.notifyDataSetChanged();
            rvLugares.setAdapter(adapter);


        }

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_baseline_add_road_24);
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
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }


    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

/*
    @Override
    public void onDetach() {
        super.onDetach();
        adapter.stopListening();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        adapter.startListening();
    }
*/

    /*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLugaresInteractionListener) {
            mListener = (OnLugaresInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

*/
}