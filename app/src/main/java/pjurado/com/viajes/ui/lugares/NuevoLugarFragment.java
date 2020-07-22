package pjurado.com.viajes.ui.lugares;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import pjurado.com.viajes.R;
import pjurado.com.viajes.modelo.Lugares;


public class NuevoLugarFragment extends Fragment {
    private TextView etNombre;
    private TextView etDecripcion;
    private TextView etTiempoVisita;
    private TextView etUrlFoto;
    private TextView etLatitud;
    private TextView etLongitud;
    private TextView etUrlArea;
    private TextView etUrlParking;
    private TextView etUrlInfo;
    private ImageButton btMapa;
    private ImageButton btSalvar;
    FirebaseFirestore mFirebaseFireStore;

    public NuevoLugarFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();
        return inflater.inflate(R.layout.fragment_nuevo_lugar, container, false);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etNombre = (TextView) view.findViewById(R.id.texttViewVerLugarNombre);
        etDecripcion = (TextView) view.findViewById(R.id.texttViewVerLugarDescripcion);
        etTiempoVisita = (TextView) view.findViewById(R.id.editTextEditarLugarTiempoVisita);
        etUrlFoto = (TextView) view.findViewById(R.id.editTextEditarLugarUrlFoto);
        etLatitud= (TextView) view.findViewById(R.id.editTextLatitud);
        etLongitud = (TextView) view.findViewById(R.id.editTextLongitud);
        etUrlArea = (TextView) view.findViewById(R.id.editTextArea);
        etUrlParking = (TextView) view.findViewById(R.id.editTextParking);
        etUrlInfo = (TextView) view.findViewById(R.id.editTextInfo);
        btMapa = (ImageButton) view.findViewById(R.id.imageButtonMapa);
        btSalvar = (ImageButton) view.findViewById(R.id.imageButtonNuevoLugarSalvar);

        mFirebaseFireStore= FirebaseFirestore.getInstance();

        btMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle coordenadas = new Bundle();
                coordenadas.putString("Latitud", etLatitud.getText().toString());
                coordenadas.putString("Longitud", etLongitud.getText().toString());
                coordenadas.putString("Titulo", etNombre.getText().toString());
                Navigation.findNavController(v).navigate(R.id.action_nuevoLugarFragment_to_mapsFragment, coordenadas);
                Toast.makeText(getActivity(), "Latitud " + coordenadas.getString("Latitud"), Toast.LENGTH_SHORT).show();
            }
        });

        btSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grabarLugar();
                Navigation.findNavController(v).navigate(R.id.nav_gallery);
            }
        });

    }

    public void grabarLugar(){
        /* Añadir mediante mapas
        Map<String, Object> map = new HashMap<>();
        map.put("nombre", etNombre.getText().toString());
        map.put("descripcion", etDecripcion.getText().toString());
        map.put("tiempoVisita", etTiempoVisita.getText().toString());
        map.put("urlFoto", etUrlFoto.getText().toString());

         */
        Lugares lugar = new Lugares();
        lugar.setNombre(etNombre.getText().toString());
        lugar.setDescripcion(etDecripcion.getText().toString());
        lugar.setTiempoVisita(etTiempoVisita.getText().toString());
        lugar.setUrlfoto(etUrlFoto.getText().toString());
        lugar.setLatitud(etLatitud.getText().toString());
        lugar.setLongitud(etLongitud.getText().toString());
        lugar.setArea(etUrlArea.getText().toString());
        lugar.setParking(etUrlParking.getText().toString());
        lugar.setEnlaceInformacion(etLongitud.getText().toString());

        //Alternativa
        //mFirebaseFireStore.collection("Lugares").document().set(map);

        mFirebaseFireStore.collection("Lugares").add(lugar).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getActivity(), "El lugar se creo correctamente", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error al crear el lugar", Toast.LENGTH_SHORT).show();
            }
        });
    }


}