package pjurado.com.viajes;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.print.pdf.PrintedPdfDocument;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import pjurado.com.viajes.modelo.Lugares;
import pjurado.com.viajes.modelo.Recorrido;
import pjurado.com.viajes.modelo.Viajes;

/**
 * A fragment representing a list of Items.
 */
public class RecorridoFragment extends Fragment {
    private static final int CREATE_FILE = 1;
    ActivityResultLauncher<Intent> mStartForResult;
    Recorrido[] recorrido;
    private Viajes viaje;
    boolean lleno = false;
    View view;
    int cont = 0;
    int kmTotales = 0;
    Double tiempoTotal = 0.;
    private TextView tvKm;
    private TextView tvTiempo;
    private ProgressBar progressBar;



    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecorridoFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RecorridoFragment newInstance(int columnCount) {
        RecorridoFragment fragment = new RecorridoFragment();
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_mapa_recorrido,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.pdfRecorrido){
            RecorridoPDF recorridoPDF = new RecorridoPDF(getContext(), getActivity(), viaje.getNombre(), recorrido);
        }

        if (item.getItemId() == R.id.paraRutaFragment){
            Bundle idViaje = new Bundle();
            idViaje.putSerializable("Viaje", viaje);

            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
            //NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
            navController.navigate(R.id.rutaFragment, idViaje);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_item_recorrido_list, container, false);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();
        viaje = (Viajes) getArguments().getSerializable("Viaje");

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Recorrido " + viaje.getNombre());
        tvKm = view.findViewById(R.id.tvKm);
        tvTiempo = view.findViewById(R.id.tvTiempo);
        progressBar = view.findViewById(R.id.progressBar2);
        setHasOptionsMenu(true);
        calculaRecorrido(viaje);


        return view;
    }

    private void calculaRecorrido(Viajes viaje) {
        kmTotales = 0;
        tiempoTotal = 0.;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean origenydestino = pref.getBoolean("activaLatLon",false);
        final int[] desplaza = {0};
        String lat="";
        String lon="";
        progressBar.setVisibility(View.VISIBLE);
        if (origenydestino){
            lat = pref.getString("latitudOrigen",null);
            lon = pref.getString("longitudOrigen",null);
            if (lat != null && lon != null){
                desplaza[0] = 2;
            }
        }
        final String[] latitud = new String[viaje.getIdLugares().size()+desplaza[0]];
        final String[] longitud = new String[viaje.getIdLugares().size()+desplaza[0]];
        final String[] ciudades = new String[viaje.getIdLugares().size()+desplaza[0]];
        recorrido = new Recorrido[viaje.getIdLugares().size()-1+desplaza[0]];
        if (desplaza[0] == 2) {
            latitud[0] = lat;
            longitud[0] = lon;
            ciudades[0]="Home";
            latitud[viaje.getIdLugares().size() + 1] = lat;
            longitud[viaje.getIdLugares().size() + 1] = lon;
            ciudades[viaje.getIdLugares().size() + 1]="Home";
            desplaza[0] = 1;
        }

        FirebaseFirestore mFirebaseFireStore = FirebaseFirestore.getInstance();

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
                            Lugares lugar = document.toObject(Lugares.class);
                            String id = document.getId();
                            if (lugar.getLatitud() != "") {
                                Double lat = Double.parseDouble(lugar.getLatitud());
                                Double lon = Double.parseDouble(lugar.getLongitud());

                                int pos = posicionId(id)+desplaza[0];
                                latitud[pos] = lugar.getLatitud();
                                longitud[pos] = lugar.getLongitud();
                                ciudades[pos] = lugar.getNombre();

                                if (lleno = estaCompleto(latitud)){
                                    cont=0;
                                    calcularRuta(latitud, longitud, ciudades);
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
    }

    private void calcularRuta(String[] latitud, String[] longitud, final String[] ciudades) {
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
                        String tiempo = jso.getString("total_time");
                        tiempoTotal += Double.valueOf(tiempo);
                        tiempo = pasarHorasyMin(tiempo);
                        int distancia = jso.getInt("total_distance")/1000;
                        kmTotales += distancia;
                        recorrido[finalI-1]=new Recorrido(ciudades[finalI-1], ciudades[finalI], tiempo, distancia);
                        Log.d("Recorrido", ciudades[finalI-1] + " " +  ciudades[finalI]+ " " + tiempo+ " " + distancia);
                        cont++;
                        if (cont == ciudades.length - 1){

                            String tt = String.valueOf(tiempoTotal);
                            tt = pasarHorasyMin(tt);
                            tvKm.setText(kmTotales + " Km.");
                            tvTiempo.setText(tt);
                            // Set the adapter
                            Context context = view.getContext();
                            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
                            if (mColumnCount <= 1) {
                                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                            } else {
                                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
                            }
                            MyRecorridoRecyclerViewAdapter adapter = new MyRecorridoRecyclerViewAdapter(recorrido);
                            adapter.notifyDataSetChanged();
                            recyclerView.setAdapter(adapter);

                        }
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

    private String pasarHorasyMin(String tiempo) {
        int min = (int) Math.round(Double.valueOf(tiempo)/60);
        int horas = (int) Math.round(min/60);
        min = min % 60;
        return horas + "h " + min + "'";
    }


    private int posicionId(String id) {
        for (int i = 0; i < viaje.getIdLugares().size(); i++){
            if (id.equals(viaje.getIdLugares().get(i).getId())){
                return i;
            }
        }
        return -1;
    }

    private boolean estaCompleto(String[] latitud) {
        boolean completo = true;
        for (int i = 0; i < latitud.length; i++) {
            if (latitud[i] == null) {
                return false;
            }
        }
        return true;
    }
    public void openDirectory(Uri uriToLoad) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        //intent.setType("application/pdf");
        //intent.putExtra(Intent.EXTRA_TITLE, "invoice.pdf");

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uriToLoad);

        //getActivity().startActivityForResult(intent, CREATE_FILE);
        mStartForResult.launch(intent);

    }
/*
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == 0
                && resultCode == getActivity().RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                // Perform operations on the document using its URI.
            }
        }
    }

 */

    public void viewPdf(){
        File pdfFile = new File("/storage/emulated/0/Android/data/pjurado.com.viajes/files/Viajes/Huesca y Navarra.pdf");
        if (pdfFile.exists()){
            //Uri uri = Uri.fromFile(pdfFile);
            //storage/emulated/0/Android/data/pjurado.com.viajes/files/Viajes/Huesca y Navarra.pdf
            Uri uri = Uri.parse(pdfFile.getPath());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/PDF");
            try{
                getActivity().startActivity(intent);
            }catch (ActivityNotFoundException e){
                getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.adobe.reader&gl=ES")));
                //Toast.makeText(context, "Debes instalar una aplicación para visualizar PDFs", Toast.LENGTH_SHORT).show();
            }
        }

    }
}