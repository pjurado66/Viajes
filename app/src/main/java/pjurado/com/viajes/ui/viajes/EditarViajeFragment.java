package pjurado.com.viajes.ui.viajes;

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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import pjurado.com.viajes.R;
import pjurado.com.viajes.modelo.Lugares;
import pjurado.com.viajes.modelo.Viajes;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditarViajeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditarViajeFragment extends Fragment {
    private TextView etNombre;
    private TextView etDecripcion;
    private ImageView ivFoto;
    private ImageView btCancel;
    private ImageView btSalvar;
    FirebaseFirestore mFirebaseFireStore;
    private String id;
    private Viajes viaje;
    private Uri uriImagen;
    private Uri uri;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditarViajeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditarViajeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditarViajeFragment newInstance(String param1, String param2) {
        EditarViajeFragment fragment = new EditarViajeFragment();
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
        id = getArguments().getString("Id");
        return inflater.inflate(R.layout.fragment_editar_viaje, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etNombre = (TextView) view.findViewById(R.id.texttViewEditarViajeNombre);
        etDecripcion = (TextView) view.findViewById(R.id.texttViewEditarViajeDescripcion);
        ivFoto = (ImageView) view.findViewById(R.id.imageViewEditarViajeFoto);
        btCancel = view.findViewById(R.id.imageViewEditarVolverViaje);
        btSalvar = view.findViewById(R.id.imageViewEditarSalvarViaje);

        mFirebaseFireStore= FirebaseFirestore.getInstance();

        recuperarDatosLugar();
        ivFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
            }
        });

        btSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uriImagen!= null && viaje.getUrlfoto() != uriImagen.toString()) {
                    subirImagen();
                }
                else{
                    grabarLugar();
                }
                Navigation.findNavController(v).navigate(R.id.viajesFragment);
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.viajesFragment);
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
                    ivFoto.setImageURI(uriImagen);
                }
                break;
        }
    }

    public void recuperarDatosLugar(){
        mFirebaseFireStore.collection("Viajes").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    viaje = documentSnapshot.toObject(Viajes.class);
                    etNombre.setText(viaje.getNombre());
                    etDecripcion.setText(viaje.getDescripcion());

                    if (viaje.getUrlfoto() != null) {
                        if (!viaje.getUrlfoto().isEmpty()) {
                            Glide.with(getActivity())
                                    .load(viaje.getUrlfoto())
                                    .into(ivFoto);
                        }
                    }
                }
            }
        });
    }

    public void grabarLugar(){
        viaje.setNombre(etNombre.getText().toString());
        viaje.setDescripcion(etDecripcion.getText().toString());

        if (uri != null) {
            viaje.setUrlfoto(uri.toString());
        }
        //else{
        //   lugar.setUrlfoto(null);
        //}
        mFirebaseFireStore.collection("Viajes").document(id).set(viaje)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getActivity(), "Lugar actualizado con exito", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getActivity(), "Fallo al actualizar", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}