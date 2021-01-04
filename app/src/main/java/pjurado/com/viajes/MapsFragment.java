package pjurado.com.viajes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment {

    private String longitud;
    private String latitud;
    private String titulo;
    private LatLng lugar;
    private Marker marcador;

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
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapRuta);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}