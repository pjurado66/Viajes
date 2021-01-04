package pjurado.com.viajes.ui.viajes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import pjurado.com.viajes.R;
import pjurado.com.viajes.modelo.Lugares;
import pjurado.com.viajes.modelo.PosicionLugarEnViaje;
import pjurado.com.viajes.modelo.Viajes;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrdenarLugaresViajeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrdenarLugaresViajeFragment extends Fragment {
    //private ArrayList<PosicionLugarEnViaje> listaViajes = new ArrayList<>();
    private RecyclerView myRecyclerView;
    private OrdenarLugaresRecyclerViewAdapter myAdapter;
    private Viajes viaje;
    private String idViaje;
    private FirebaseFirestore mFirebaseFireStore;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrdenarLugaresViajeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrdenarLugaresViajeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrdenarLugaresViajeFragment newInstance(String param1, String param2) {
        OrdenarLugaresViajeFragment fragment = new OrdenarLugaresViajeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        for (int i = 0; i < viaje.getIdLugares().size(); i++){
            viaje.getIdLugares().get(i).setPosicion(i);
        }
        DocumentReference docViaje = mFirebaseFireStore.collection("Viajes").document(idViaje);
        docViaje.update("idLugares", viaje.getIdLugares())
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ordenar_lugares_viaje, container, false);
        idViaje = getArguments().getString("Id");
        viaje = (Viajes) getArguments().getSerializable("Viaje");
        mFirebaseFireStore = FirebaseFirestore.getInstance();

        if (view instanceof RecyclerView) {
            if (viaje.getIdLugares() != null) {
                myRecyclerView = (RecyclerView) view;
                myRecyclerView.setHasFixedSize(true);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                myRecyclerView.setLayoutManager(layoutManager);

                myAdapter = new OrdenarLugaresRecyclerViewAdapter(viaje);
                loadSwipe();
                myRecyclerView.setAdapter(myAdapter);

            }
            else{
                Toast.makeText(getActivity(), "No tiene lugares", Toast.LENGTH_SHORT).show();
            }
        }

        return view;
    }

    private void loadSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP |ItemTouchHelper.DOWN, 0) {

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // remove item from adapter
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder dragged, RecyclerView.ViewHolder target) {
                final int fromPos = dragged.getAdapterPosition();
                final int toPos = target.getAdapterPosition();

                Collections.swap(viaje.getIdLugares(), fromPos, toPos);


                myAdapter.notifyItemMoved(fromPos, toPos);
                //myAdapter.notifyDataSetChanged();
                // move item in `fromPos` to `toPos` in adapter.
                return true;// true if moved, false otherwise
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(myRecyclerView);
    }

}