package pjurado.com.viajes.ui.viajes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pjurado.com.viajes.R;
import pjurado.com.viajes.modelo.Lugares;
import pjurado.com.viajes.modelo.Viajes;

public class RutaFragment extends Fragment {
    private Viajes viaje;
    private FirebaseFirestore mFirebaseFireStore;
    private LatLng sydney;
    private JSONObject jso;
    private double lati;
    private double longi;
    private ProgressBar progressBar;

    private LatLngBounds.Builder constructor = new LatLngBounds.Builder();;

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

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
        public void onMapReady(final GoogleMap googleMap) {
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            //googleMap.getUiSettings().setMapToolbarEnabled(true);
            progressBar.setVisibility(View.VISIBLE);
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getActivity(), "Sin permisos", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
            else{
                Toast.makeText(getActivity(), "Permisos recibidos", Toast.LENGTH_SHORT).show();
            }
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getActivity(), "Sin permisos", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            else{
                Toast.makeText(getActivity(), "Permisos recibidos", Toast.LENGTH_SHORT).show();
            }


            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                }
            });

            final ArrayList<String> ciudades = new ArrayList<>();

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            boolean origenydestino = pref.getBoolean("activaLatLon",false);
            final int[] desplaza = {0};
            String lat="";
            String lon="";
            if (origenydestino){
                lat = pref.getString("latitudOrigen",null);
                lon = pref.getString("longitudOrigen",null);
                if (lat != null && lon != null){
                    ciudades.add("Home");
                    sydney = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                    googleMap.addMarker(new MarkerOptions().position(sydney).title("Home"));
                    constructor.include(sydney);
                    //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 8.0f));
                    LatLngBounds limites = constructor.build();
                    int ancho = getResources().getDisplayMetrics().widthPixels;
                    int alto = getResources().getDisplayMetrics().heightPixels;
                    int padding = (int) (alto * 0.25);
                    CameraUpdate centrarMarcadores = CameraUpdateFactory.newLatLngBounds(limites, ancho, alto, padding);
                    googleMap.animateCamera(centrarMarcadores);
                    desplaza[0] = 2;
                }
            }
            final String[] latitud = new String[viaje.getIdLugares().size()+desplaza[0]];
            final String[] longitud = new String[viaje.getIdLugares().size()+desplaza[0]];
            if (desplaza[0] == 2) {
                latitud[0] = lat;
                longitud[0] = lon;
                latitud[viaje.getIdLugares().size() + 1] = lat;
                longitud[viaje.getIdLugares().size() + 1] = lon;
                desplaza[0] = 1;
            }

            for (int i = 0; i < viaje.getIdLugares().size(); i++) {
                String idLugar = viaje.getIdLugares().get(i).getId();
                DocumentReference documentReference = mFirebaseFireStore.
                        collection("Lugares").document(idLugar);

                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                //cont[0]++;
                                Log.d("Leo", "DocumentSnapshot data: " + document.getData());
                                Lugares lugar = document.toObject(Lugares.class);
                                String id = document.getId();
                                if (lugar.getLatitud() != "") {
                                    Double lat = Double.parseDouble(lugar.getLatitud());
                                    Double lon = Double.parseDouble(lugar.getLongitud());
                                    //latitud.add(lugar.getLatitud());
                                    //longitud.add(lugar.getLongitud());
                                    int pos = posicionId(id)+desplaza[0];
                                    latitud[pos] = lugar.getLatitud();
                                    longitud[pos] = lugar.getLongitud();
                                    ciudades.add(lugar.getNombre());
                                    sydney = new LatLng(lat, lon);
                                    googleMap.addMarker(new MarkerOptions().position(sydney).title(lugar.getNombre()));
                                    constructor.include(sydney);
                                    //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 8.0f));
                                    LatLngBounds limites = constructor.build();
                                    int ancho = getResources().getDisplayMetrics().widthPixels;
                                    int alto = getResources().getDisplayMetrics().heightPixels;
                                    int padding = (int) (alto * 0.25);
                                    CameraUpdate centrarMarcadores = CameraUpdateFactory.newLatLngBounds(limites, ancho, alto, padding);
                                    googleMap.animateCamera(centrarMarcadores);
                                    if (estaCompleto(latitud)){
                                        calcularRuta(latitud, longitud, googleMap, ciudades);
                                    }
                                }
                            } else {
                                Log.d("Leo", "No such document");
                            }
                        } else {
                            Log.d("Leo", "get failed with ", task.getException());
                        }
                    }
                });
            }


            //constructor.include(new LatLng(42.1353000, -0.4202950));

            //https://maps.googleapis.com/maps/api/directions/json?origin=parametroLatitud%2CparametroLongitud&destination=parametroLatitud%2CparametroLongitud&key=tu_key
            //https://maps.googleapis.com/maps/api/directions/json?origin=42.414709411031%2C0.14024643480909066&destination=0.14024643480909066%2C0.31520933662753364&key=AIzaSyAQhDJN01WwSb0blkfFY6cb6MTUEax2TAE

            //googleMap.animateCamera(CameraUpdateFactory.newLatLng(sydney));
            //googleMap.animateCamera(CameraUpdateFactory.zoomTo(10.f));
/*
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lati, longi))
                    .zoom(10.f)
                    .build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
*/               /*
            String url="https://maps.googleapis.com/maps/api/directions/json?origin=42.414709411031%2C0.14024643480909066&destination=0.14024643480909066%2C0.31520933662753364&key=AIzaSyAQhDJN01WwSb0blkfFY6cb6MTUEax2TAE";
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        jso = new JSONObject(response);
                        trazarRuta(jso);
                        Log.i("Ruta", response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            queue.add(stringRequest);

             */
        }


    };

    private boolean estaCompleto(String[] latitud) {
        boolean completo = true;
        for (int i = 0; i < latitud.length; i++) {
            if (latitud[i] == null) {
                return false;
            }
        }
        return true;
    }

    private int posicionId(String id) {
        for (int i = 0; i < viaje.getIdLugares().size(); i++){
            if (id.equals(viaje.getIdLugares().get(i).getId())){
                return i;
            }
        }
        return -1;
    }

    private void calcularRuta(String[] latitud, String[] longitud, final GoogleMap googleMap, final ArrayList<String> ciudades) {
        for (int i = 1; i < latitud.length; i++) {
            RequestQueue queue = Volley.newRequestQueue(getContext());
            String url = "https://maps.open-street.com/api/route/?origin=";
            url = url + latitud[i-1] + "," + longitud[i-1] + "&destination=";
            url = url + latitud[i] + "," + longitud[i];
            url = url + "&mode=driving&key=d608057cddca02bb1bc33d5c98cb1b60";
            Log.i("json", url);
            final int finalI = i;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jso = new JSONObject(response);
                        String poli = jso.getString("polyline");
                        final  String LINE = poli;
                        List decodedPath = decodePoly(poli);
                        googleMap.addPolyline(new PolylineOptions().addAll(decodedPath));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            queue.add(stringRequest);
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ruta, container, false);
        progressBar = view.findViewById(R.id.progressBar3);
        viaje = (Viajes) getArguments().getSerializable("Viaje");

       ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Mapa del viaje a "+ viaje.getNombre());

        mFirebaseFireStore = FirebaseFirestore.getInstance();

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapRuta);

        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

    private ArrayList<LatLng> decodePoly(String encoded) {

        Log.i("Locatiooo", "String received: "+encoded);
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        double lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            //LatLng p = new LatLng((int) (((double) lat /1E5)* 1E6), (int) (((double) lng/1E5   * 1E6)));
            LatLng p = new LatLng((((double) lat / 1E6)),(((double) lng / 1E6)));
            poly.add(p);
        }


        return poly;
    }
}