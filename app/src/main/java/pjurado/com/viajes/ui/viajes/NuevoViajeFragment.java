package pjurado.com.viajes.ui.viajes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.util.ArrayList;

import pjurado.com.viajes.R;
import pjurado.com.viajes.modelo.PosicionLugarEnViaje;
import pjurado.com.viajes.modelo.Viajes;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NuevoViajeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NuevoViajeFragment extends Fragment {
    private EditText etNombreNuevoViaje;
    private EditText etDescripcionNuevoViaje;
    private ImageView iVFotoNuevoViaje;
    private ImageButton btnSalvar;
    FirebaseFirestore mFirebaseFireStore;
    private Uri uriImagen;
    private Uri uri;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NuevoViajeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NuevoViajeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NuevoViajeFragment newInstance(String param1, String param2) {
        NuevoViajeFragment fragment = new NuevoViajeFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();
        //fab.setImageResource(R.drawable.grabar);


        return inflater.inflate(R.layout.fragment_nuevo_viaje, container, false);
    }

    private void grabarViaje() {
        Viajes viaje = new Viajes();
        viaje.setNombre(etNombreNuevoViaje.getText().toString());
        viaje.setDescripcion(etDescripcionNuevoViaje.getText().toString());
        if (uri != null) {
            Log.d("Grabardesp", uri.toString());
        }
        else{
            Log.d("Grabardesp", "uri nulo");
        }
        viaje.setUrlfoto(uri.toString());

        //Código de prueba para añadir la lista de lugares
        /*
        ArrayList<PosicionLugarEnViaje> listaLugares = new ArrayList<>();
       PosicionLugarEnViaje lugarvisitar = new PosicionLugarEnViaje();
       lugarvisitar.setId("iisois");
       lugarvisitar.setPosicion(1);
        listaLugares.add(lugarvisitar);

        lugarvisitar = new PosicionLugarEnViaje();
        lugarvisitar.setId("dddd");
        lugarvisitar.setPosicion(2);
        listaLugares.add(lugarvisitar);
        viaje.setIdLugares(listaLugares);

         */

        //Alternativa
        //mFirebaseFireStore.collection("Lugares").document().set(map);
        mFirebaseFireStore= FirebaseFirestore.getInstance();
        mFirebaseFireStore.collection("Viajes").add(viaje).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getActivity(), "El viaje se creo correctamente", Toast.LENGTH_SHORT).show();
                Log.d("Grabar", "Ok");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error al crear el viaje", Toast.LENGTH_SHORT).show();
                Log.d("Grabar", "Fallo");
            }
        });
    }

    private void subirImagen() {
        Log.d("Foto", "entro");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        Log.d("Foto",String.valueOf(uriImagen));
        //final StorageReference ref = storageRef.child("images/"+file.getLastPathSegment());
        final StorageReference ref = storageRef.child("images/"+uriImagen.getLastPathSegment());
        Log.d("Foto","images/"+uriImagen.getLastPathSegment());
        UploadTask uploadTask = ref.putFile(uriImagen);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    uri = task.getResult();
                    Log.d("Grabar", uri.toString());
                    grabarViaje();
                } else {
                    // Handle failures
                    // ...
                }
            }
        });



/*
// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d("Foto", "Fallo");

                Toast.makeText(getActivity(), "Fallo subir foto", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getActivity(), "Foto subida", Toast.LENGTH_SHORT).show();
                Log.d("Foto", "Grabada");
                url[0] = ref.getDownloadUrl();
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
        return url[0];

 */
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etNombreNuevoViaje = view.findViewById(R.id.editTextTituloViaje);
        etDescripcionNuevoViaje = view.findViewById(R.id.editTextDescripcionViaje);
        iVFotoNuevoViaje = view.findViewById(R.id.imageViewFotoViaje);
        btnSalvar = view.findViewById(R.id.imageButtonSalvarViaje);

        mFirebaseFireStore= FirebaseFirestore.getInstance();

        iVFotoNuevoViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,0);
            }
        });
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subirImagen();


                Navigation.findNavController(v).navigate(R.id.viajesFragment);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    uriImagen = data.getData();
                    iVFotoNuevoViaje.setImageURI(uriImagen);
                }
                break;
        }
    }
}