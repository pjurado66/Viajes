package pjurado.com.viajes.ui.lugares;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

import pjurado.com.viajes.R;
import pjurado.com.viajes.modelo.AreasyParkings;
import pjurado.com.viajes.modelo.Lugares;

import static android.app.Activity.RESULT_OK;


public class NuevoLugarFragment extends Fragment {
    private TextView etNombre;
    private TextView etDecripcion;
    private TextView etTiempoVisita;
    private ImageView ivFotoLugar;
    private TextView etLatitud;
    private TextView etLongitud;
    private TextView etUrlArea;
    private TextView etUrlParking;
    private TextView etUrlInfo;
    private ImageButton btMapa;
    private ImageView btSalvar;
    private ImageView btVolver;

    FirebaseFirestore mFirebaseFireStore;
    private Uri uriImagen;
    private Uri uri;

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
        etNombre = (TextView) view.findViewById(R.id.texttViewEditarViajeNombre);
        etDecripcion = (TextView) view.findViewById(R.id.texttViewEditarViajeDescripcion);
        etTiempoVisita = (TextView) view.findViewById(R.id.editTextEditarLugarTiempoVisita);

        ivFotoLugar = (ImageView) view.findViewById(R.id.imageViewNuevoLugarFoto);
        etLatitud= (TextView) view.findViewById(R.id.editTextLatitud);
        etLongitud = (TextView) view.findViewById(R.id.editTextLongitud);
        btMapa = (ImageButton) view.findViewById(R.id.imageButtonMapa);
        btSalvar = (ImageView) view.findViewById(R.id.imageButtonNuevoLugarSalvar);
        btVolver = (ImageView) view.findViewById(R.id.imageViewNuevoLugarVolver);

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

        ivFotoLugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
            }
        });

        btVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.nav_gallery);
            }
        });

        btSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uriImagen != null) {
                    subirImagen();
                }
                else{
                    grabarLugar();
                }
                Navigation.findNavController(v).navigate(R.id.nav_gallery);
            }
        });

    }

    private void subirImagen() {
        if (uriImagen != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            final StorageReference ref = storageRef.child("images/" + uriImagen.getLastPathSegment());
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
                        grabarLugar();
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    uriImagen = data.getData();
                    ivFotoLugar.setImageURI(uriImagen);
                }
                break;
        }
    }

    public void grabarLugar(){
        Lugares lugar = new Lugares();
        lugar.setNombre(etNombre.getText().toString());
        lugar.setDescripcion(etDecripcion.getText().toString());
        lugar.setTiempoVisita(etTiempoVisita.getText().toString());

        lugar.setLatitud(etLatitud.getText().toString());
        lugar.setLongitud(etLongitud.getText().toString());
        lugar.setAreas(new ArrayList<AreasyParkings>());
        lugar.setInformacion(new ArrayList<AreasyParkings>());
        lugar.setParking(new ArrayList<AreasyParkings>());
        lugar.setViaje("");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ArrayList<String> usuarios = new ArrayList<>();
        usuarios.add(user.getEmail().toString());
        lugar.setUsuarios(usuarios);


        if (uri != null) {
            lugar.setUrlfoto(uri.toString());
        }
        else{
            lugar.setUrlfoto(null);
        }

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