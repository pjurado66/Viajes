package pjurado.com.viajes.ui.lugares;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import pjurado.com.viajes.R;
import pjurado.com.viajes.modelo.Lugares;


public class EditarLugarFragment extends Fragment {
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
    private String id;
    private Lugares lugar;

    public EditarLugarFragment() {
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
        id = getArguments().getString("Id");
        return inflater.inflate(R.layout.fragment_editar_lugar, container, false);

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
        btSalvar = (ImageButton) view.findViewById(R.id.imageButtonEditarLugarSalvar);

        mFirebaseFireStore= FirebaseFirestore.getInstance();

        recuperarDatosLugar();

        btMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle coordenadas = new Bundle();
                coordenadas.putString("Latitud", etLatitud.getText().toString());
                coordenadas.putString("Longitud", etLongitud.getText().toString());
                coordenadas.putString("Titulo", etNombre.getText().toString());
                Navigation.findNavController(v).navigate(R.id.action_editarLugarFragment_to_mapsFragment, coordenadas);
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

    public void recuperarDatosLugar(){
        mFirebaseFireStore.collection("Lugares").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    lugar = documentSnapshot.toObject(Lugares.class);
                    etNombre.setText(lugar.getNombre());
                    etDecripcion.setText(lugar.getDescripcion());
                    etTiempoVisita.setText(lugar.getTiempoVisita());
                    etUrlFoto.setText(lugar.getUrlfoto());
                    etLatitud.setText(lugar.getLatitud());
                    etLongitud.setText(lugar.getLongitud());
                    etUrlArea.setText(lugar.getArea());
                    etUrlParking.setText(lugar.getParking());
                    etUrlInfo.setText(lugar.getEnlaceInformacion());
                }
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



        //mFirebaseFireStore.collection("Lugares").document(id).update(lugar).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
        mFirebaseFireStore.collection("Lugares").document(id).set(lugar);
                /*
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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

                 */
    }
}