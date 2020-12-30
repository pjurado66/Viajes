package pjurado.com.viajes.ui.viajes;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import pjurado.com.viajes.R;
import pjurado.com.viajes.dummy.DummyContent;
import pjurado.com.viajes.modelo.Lugares;
import pjurado.com.viajes.ui.lugares.LugaresRecyclerViewAdapter;

/**
 * A fragment representing a list of Items.
 */
public class LugaresViajeFragment extends Fragment {
    private LugaresViajeRecyclerViewAdapter adapter;
    private FirebaseFirestore miFirebase;
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 2;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LugaresViajeFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static LugaresViajeFragment newInstance(int columnCount) {
        LugaresViajeFragment fragment = new LugaresViajeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lugares_viaje_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Log.d("Recycler", "empiezo");

            Context context = view.getContext();

            RecyclerView rvLugares = (RecyclerView) view;

            rvLugares.setLayoutManager(new GridLayoutManager(context, 2));
            miFirebase= FirebaseFirestore.getInstance();

            Query query = miFirebase.collection("Lugares").orderBy("nombre");
            FirestoreRecyclerOptions<Lugares> firestoreRecyclerOptions =
                    new FirestoreRecyclerOptions.Builder<Lugares>()
                            .setQuery(query, Lugares.class)
                            .build();




            adapter = new LugaresViajeRecyclerViewAdapter(getActivity(), firestoreRecyclerOptions);
            adapter.notifyDataSetChanged();
            rvLugares.setAdapter(adapter);





        }
        return view;
    }
}