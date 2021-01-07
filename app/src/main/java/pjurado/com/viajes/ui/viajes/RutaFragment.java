package pjurado.com.viajes.ui.viajes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.auth.viewmodel.RequestCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import pjurado.com.viajes.R;
import pjurado.com.viajes.modelo.Lugares;
import pjurado.com.viajes.modelo.Viajes;

public class RutaFragment extends Fragment {
    private Viajes viaje;
    private FirebaseFirestore mFirebaseFireStore;
    private LatLng sydney;
    private JSONObject jso;
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
                                Log.d("Leo", "DocumentSnapshot data: " + document.getData());
                                Lugares lugar = document.toObject(Lugares.class);
                                if (lugar.getLatitud() != "") {
                                    Double lat = Double.parseDouble(lugar.getLatitud());
                                    Double lon = Double.parseDouble(lugar.getLongitud());
                                    sydney = new LatLng(lat, lon);
                                    googleMap.addMarker(new MarkerOptions().position(sydney).title(lugar.getNombre()));
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
            //https://maps.googleapis.com/maps/api/directions/json?origin=parametroLatitud%2CparametroLongitud&destination=parametroLatitud%2CparametroLongitud&key=tu_key
            //https://maps.googleapis.com/maps/api/directions/json?origin=42.414709411031%2C0.14024643480909066&destination=0.14024643480909066%2C0.31520933662753364&key=AIzaSyAQhDJN01WwSb0blkfFY6cb6MTUEax2TAE

            //googleMap.animateCamera(CameraUpdateFactory.newLatLng(sydney));
            //googleMap.animateCamera(CameraUpdateFactory.zoomTo(10.f));
            /*
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(42.414709411031, 0.14024643480909066))
                    .zoom(14)
                    .build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

             */
            /*
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

    public void trazarRuta(JSONObject jso){

    }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_ruta, container, false);

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
        }
    }