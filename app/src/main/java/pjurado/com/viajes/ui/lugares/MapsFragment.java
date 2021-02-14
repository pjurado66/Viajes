package pjurado.com.viajes.ui.lugares;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import pjurado.com.viajes.R;
import pjurado.com.viajes.modelo.AreasyParkings;
import pjurado.com.viajes.modelo.Lugares;

public class MapsFragment extends Fragment {

    private String longitud;
    private String latitud;
    private String titulo;
    private LatLng lugar;
    private Marker marcador;
    private String id;
    private ArrayList<AreasyParkings> areas;
    private ArrayList<AreasyParkings> parkings;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {


        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            Log.d("latitus", "+"+ latitud+"+");
            if (latitud != null && longitud != null) {
                if (!latitud.isEmpty() && !longitud.isEmpty()) {
                    lugar = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));
                } else {
                    lugar = new LatLng(37, -6);
                }
            }
            else{
                lugar = new LatLng(37, -6);
            }

            marcador = googleMap.addMarker(new MarkerOptions().position(lugar).title(titulo));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(lugar));
            if (areas != null) {
                for (int i = 0; i < areas.size(); i++) {
                    if (areas.get(i).getEsExtraible()) {
                        lugar = new LatLng(Double.parseDouble(areas.get(i).getLatitud()), Double.parseDouble(areas.get(i).getLongitud()));
                        marcador = googleMap.addMarker(new MarkerOptions().
                                position(lugar).
                                title(areas.get(i).getTitulo())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.area_p)));
                    }
                }
            }

            if (parkings != null) {
                for (int i = 0; i < parkings.size(); i++) {
                    if (parkings.get(i).getEsExtraible()) {
                        lugar = new LatLng(Double.parseDouble(parkings.get(i).getLatitud()), Double.parseDouble(parkings.get(i).getLongitud()));
                        marcador = googleMap.addMarker(new MarkerOptions().
                                position(lugar).
                                title(parkings.get(i).getTitulo())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.parking_p)));
                    }
                }
            }
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    Toast.makeText(getActivity(), "Has hecho click en: " + latLng.latitude+"/"+latLng.longitude, Toast.LENGTH_SHORT).show();
                    marcador.setPosition(latLng);
                }
            });
        }


    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        latitud = getArguments().getString("Latitud");
        longitud = getArguments().getString("Longitud");
        titulo = getArguments().getString("Titulo");
        id = getArguments().getString("Id");

        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recuperarDatosLugar();

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public void recuperarDatosLugar(){
        FirebaseFirestore mFirebaseFireStore = FirebaseFirestore.getInstance();
        mFirebaseFireStore.collection("Lugares").document(id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            Lugares lugar = documentSnapshot.toObject(Lugares.class);
                            areas = lugar.getAreas();
                            parkings = lugar.getParking();
                            SupportMapFragment mapFragment =
                                    (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapRuta);
                            if (mapFragment != null) {
                                mapFragment.getMapAsync(callback);
                            }
                        }
                    }
                });


    }



}