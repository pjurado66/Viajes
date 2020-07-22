package pjurado.com.viajes.ui.lugares;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.ButtCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.collect.Maps;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import pjurado.com.viajes.MapsFragment;
import pjurado.com.viajes.R;
import pjurado.com.viajes.modelo.Lugares;


public class VerLugarFragment extends Fragment {
    private TextView etNombre;
    private TextView etDecripcion;
    private TextView etTiempoVisita;

    private ImageView ivFoto;
    private Maps mapa;
    private ImageButton btnGPS;
    private String latitud;
    private String longitud;

    private TextView etUrlArea;
    private TextView etUrlParking;
    private TextView etUrlInfo;
    private ImageButton btMapa;
    private ImageButton btSalvar;
    FirebaseFirestore mFirebaseFireStore;
    private String id;
    private Lugares lugar;

    public VerLugarFragment() {
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

        return inflater.inflate(R.layout.fragment_ver_lugar, container, false);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etNombre = (TextView) view.findViewById(R.id.texttViewVerLugarNombre);
        etDecripcion = (TextView) view.findViewById(R.id.texttViewVerLugarDescripcion);
        etTiempoVisita = (TextView) view.findViewById(R.id.editTextEditarLugarTiempoVisita);
        ivFoto = (ImageView) view.findViewById(R.id.imageViewFoto);
        btnGPS = view.findViewById(R.id.imageButtonGPS);

        /*
        etLatitud= (TextView) view.findViewById(R.id.editTextLatitud);
        etLongitud = (TextView) view.findViewById(R.id.editTextLongitud);
        etUrlArea = (TextView) view.findViewById(R.id.editTextArea);
        etUrlParking = (TextView) view.findViewById(R.id.editTextParking);
        etUrlInfo = (TextView) view.findViewById(R.id.editTextInfo);
        btMapa = (ImageButton) view.findViewById(R.id.imageButtonMapa);
        btSalvar = (ImageButton) view.findViewById(R.id.imageButtonEditarLugarSalvar);

         */

        mFirebaseFireStore= FirebaseFirestore.getInstance();

        recuperarDatosLugar();
        Log.d("gps", latitud + "/"+ longitud);


        btnGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Uri intentUri = Uri.parse("geo:41.382,2.170?z=16&q=41.382,2.170(Esta+Es+La+Etiqueta)");
                Uri intentUri = Uri.parse("geo:"+latitud+","+ longitud+"?z=16&q="+latitud+","+longitud+"(Esta+Es+La+Etiqueta)");
                Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
                startActivity(intent);
            }
        });





    }

    public void recuperarDatosLugar(){
        mFirebaseFireStore.collection("Lugares").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    lugar = documentSnapshot.toObject(Lugares.class);
                    Log.d("Recuperar", lugar.getNombre());
                    etNombre.setText(lugar.getNombre());
                    etDecripcion.setText(lugar.getDescripcion());
                    etTiempoVisita.setText(lugar.getTiempoVisita());
                    latitud = lugar.getLatitud();
                    longitud = lugar.getLongitud();
                    Log.d("gps1", latitud + "/"+ longitud);
                    if (longitud == null || latitud == null) {
                        btnGPS.setEnabled(false);
                    }
                    else{
                        if (latitud.isEmpty() || longitud.isEmpty()){
                            btnGPS.setEnabled(false);
                        }
                    }
                    if (lugar.getUrlfoto() != null) {
                        if (!lugar.getUrlfoto().isEmpty()) {
                            Glide.with(getActivity())
                                    .load(lugar.getUrlfoto())
                                    .into(ivFoto);
                        }
                    }

                cargarMapa(lugar.getLatitud(), lugar.getLongitud());
                    /*
                    etLatitud.setText(lugar.getLatitud());
                    etLongitud.setText(lugar.getLongitud());
                    etUrlArea.setText(lugar.getArea());
                    etUrlParking.setText(lugar.getParking());
                    etUrlInfo.setText(lugar.getEnlaceInformacion());

                     */
                }
            }
        });
    }

    private void cargarMapa(String latitud, String longitud) {
        Bundle coordenadas = new Bundle();
        coordenadas.putString("Latitud", latitud);
        coordenadas.putString("Longitud", longitud);
        coordenadas.putString("Titulo", "Mapa");

        Fragment mapaFragment = new MapsFragment();
        mapaFragment.setArguments(coordenadas);

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, mapaFragment)
                .commit();
    }


}