package pjurado.com.viajes.ui.lugares;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.collect.Maps;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import pjurado.com.viajes.R;
import pjurado.com.viajes.modelo.AreasyParkings;
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

    FirebaseFirestore mFirebaseFireStore;
    private String id;
    private Lugares lugar;

    private ArrayList<String> titulosAreas;
    private ArrayList<AreasyParkings> areas;
    private int areaSeleccionada = -1;
    private ImageButton btnArea;
    private ImageButton btnAreaMapa;
    private ImageButton btnAreaBorra;

    private ArrayList<String> titulosParkings;
    private ArrayList<AreasyParkings> parkings;
    private int parkingsSeleccionado = -1;
    private ImageButton btnParking;
    private ImageButton btnParkingMapa;
    private ImageButton btnParkingBorra;

    private ArrayList<String> titulosInfo;
    private ArrayList<AreasyParkings> infos;
    private int infoSeleccionada = -1;
    private ImageButton btnInfo;
    private ImageButton btnInfoBorra;

    private Spinner spnAreas;

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
        btnArea = view.findViewById(R.id.imageButtonArea);
        btnAreaMapa = view.findViewById(R.id.imageButtonAreaMapa);
        btnParking = view.findViewById(R.id.imageButtonParking);
        btnParkingMapa = view.findViewById(R.id.imageButtonParkingMapa);
        btnInfo = view.findViewById(R.id.imageButtonInfo);

        btnAreaBorra = view.findViewById(R.id.imageButtonAreaBorra);
        btnParkingBorra = view.findViewById(R.id.imageButtonParkingBorra);
        btnInfoBorra = view.findViewById(R.id.imageButtonInfoBorra);
        btnAreaBorra.setEnabled(false);

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

        btnArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (areaSeleccionada > -1) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(areas.get(areaSeleccionada).getUrl()));
                    startActivity(i);
                }
            }
        });

        btnAreaMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (areaSeleccionada > -1) {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" +
                                    areas.get(areaSeleccionada).getLatitud() + ","
                                    +areas.get(areaSeleccionada).getLongitud());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            }
        });

        btnParking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parkingsSeleccionado > -1) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(parkings.get(parkingsSeleccionado).getUrl()));
                    startActivity(i);
                }
            }
        });

        btnParkingMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parkingsSeleccionado > -1) {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" +
                            parkings.get(parkingsSeleccionado).getLatitud() + ","
                            + parkings.get(parkingsSeleccionado).getLongitud());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            }
        });
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (infoSeleccionada > -1) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(infos.get(infoSeleccionada).getUrl()));
                    startActivity(i);
                }
            }
        });

        btnAreaBorra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (areaSeleccionada > -1){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setMessage("¿Esta seguro de borrar el área seleccionada?")
                            .setTitle("Borrar lugar");
                    // Add the buttons
                    builder.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int idDialog) {
                            // User clicked OK button
                            lugar.getAreas().remove(areaSeleccionada);
                            DocumentReference docViaje = mFirebaseFireStore.collection("Lugares").document(id);
                            docViaje.update("areas", lugar.getAreas());
//                            areas.remove(areaSeleccionada);
                            titulosAreas.remove(areaSeleccionada);

                            if (titulosAreas.size() > -1){
                                areaSeleccionada = 0;

                            }
                            else {
                                areaSeleccionada = -1;

                                //spnAreas
                            }
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    // Set other dialog properties


                    // Create the AlertDialog
                    AlertDialog dialog = builder.create();
                    builder.show();
                }
            }
        });
    }

    public void recuperarDatosLugar(){
        mFirebaseFireStore.collection("Lugares").document(id).get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    lugar = documentSnapshot.toObject(Lugares.class);
                    Log.d("Recuperar", lugar.getNombre());
                    etNombre.setText(lugar.getNombre());
                    etDecripcion.setText(lugar.getDescripcion());
                    etTiempoVisita.setText(lugar.getTiempoVisita());
                    latitud = lugar.getLatitud();
                    longitud = lugar.getLongitud();
                    Log.d("gps1", latitud + "/" + longitud);
                    if (longitud == null || latitud == null) {
                        btnGPS.setEnabled(false);
                    } else {
                        if (latitud.isEmpty() || longitud.isEmpty()) {
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
                    cargaAreas();
                    cargaParking();
                    cargaInfo();
                }
            }
        });

    }

    private void cargaInfo() {
        infos = lugar.getInformacion();
        titulosInfo = new ArrayList<>();
        if (titulosInfo != null) {
            for (int i = 0; i < infos.size(); i++) {
                titulosInfo.add(infos.get(i).getTitulo());
            }
            creaSpinnerInfos();
        }
    }

    private void creaSpinnerInfos() {
        Spinner spnInfos = (Spinner) getView().findViewById(R.id.spinnerInfos);
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, titulosInfo);

        spnInfos.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        spnInfos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                infoSeleccionada = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void cargaParking() {
        parkings = lugar.getParking();
        titulosParkings = new ArrayList<>();
        if (titulosParkings != null) {
            for (int i = 0; i < parkings.size(); i++) {
                titulosParkings.add(parkings.get(i).getTitulo());
            }
            creaSpinnerParkings();
        }
    }

    private void creaSpinnerParkings() {
        Spinner spnParkings = (Spinner) getView().findViewById(R.id.spinnerParking);
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, titulosParkings);

        spnParkings.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        spnParkings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parkingsSeleccionado = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void cargaAreas() {
        areas = lugar.getAreas();
        titulosAreas = new ArrayList<>();
        if (titulosAreas != null) {
            for (int i = 0; i < areas.size(); i++) {
                titulosAreas.add(areas.get(i).getTitulo());
            }
            creaSpinnerAreas();
        }


    }

    private void creaSpinnerAreas() {
        spnAreas = (Spinner) getView().findViewById(R.id.spinnerAreas);
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, titulosAreas);

        spnAreas.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        spnAreas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                areaSeleccionada = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void cargarMapa(String latitud, String longitud) {
        Bundle coordenadas = new Bundle();
        coordenadas.putString("Latitud", latitud);
        coordenadas.putString("Longitud", longitud);
        coordenadas.putString("Titulo", "Mapa");
        coordenadas.putString("Id", id);

        Fragment mapaFragment = new MapsFragment();
        mapaFragment.setArguments(coordenadas);

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, mapaFragment)
                .commit();
    }


}