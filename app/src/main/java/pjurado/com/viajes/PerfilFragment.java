package pjurado.com.viajes;

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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;

import static androidx.navigation.fragment.NavHostFragment.findNavController;


public class PerfilFragment extends Fragment {
    TextView email;
    EditText nombre;
    EditText urlImagen;
    ImageView imagen;

    public PerfilFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_perfil, container, false);


        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        email = (TextView) getActivity().findViewById(R.id.textViewEmail);
        nombre = (EditText) getActivity().findViewById(R.id.editTextName);
        urlImagen = (EditText) getActivity().findViewById(R.id.editTextUrlFoto);
        imagen = (ImageView) getActivity().findViewById(R.id.imageViewFotoPerfil);

        urlImagen.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    if(urlImagen.getText()!= null) {
                        Glide.with(getActivity())
                                .load(urlImagen.getText().toString())
                                .into(imagen);
                    }
                }
            }
        });

        if (user != null) {
            String mail = user.getEmail();
            email.setText(user.getEmail());
            nombre.setText(user.getDisplayName());

            if(user.getPhotoUrl()!= null) {
                urlImagen.setText(user.getPhotoUrl().toString());
                Glide.with(getActivity())
                        .load(urlImagen.getText())
                        .into(imagen);
            }

            ImageButton btnguardar = getView().findViewById(R.id.imageButtonSalvar);
            btnguardar.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity().getApplicationContext(),"Pulsando start",Toast.LENGTH_SHORT).show();
                    guardar(v);
                    Navigation.findNavController(v).navigate(R.id.nav_home);
                }
            });

        }
    }
/*
    @Override
    public void onViewCreated(Bundle savedInstanceStated){
        super.onActivityCreated(savedInstanceStated);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        email = (TextView) getActivity().findViewById(R.id.textViewEmail);
        nombre = (EditText) getActivity().findViewById(R.id.editTextName);
        urlImagen = (EditText) getActivity().findViewById(R.id.editTextUrlFoto);
        imagen = (ImageView) getActivity().findViewById(R.id.imageViewFotoPerfil);

        urlImagen.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    if(urlImagen.getText()!= null) {
                        Glide.with(getActivity())
                                .load(urlImagen.getText().toString())
                                .into(imagen);
                    }
                }
            }
        });

        if (user != null) {
            String mail = user.getEmail();
            email.setText(user.getEmail());
            nombre.setText(user.getDisplayName());

            if(user.getPhotoUrl()!= null) {
                urlImagen.setText(user.getPhotoUrl().toString());
                Glide.with(getActivity())
                        .load(urlImagen.getText())
                        .into(imagen);
            }

            ImageButton btnguardar = getView().findViewById(R.id.imageButtonSalvar);
            btnguardar.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity().getApplicationContext(),"Pulsando start",Toast.LENGTH_SHORT).show();
                    guardar(v);
                    Navigation.findNavController(v).navigate(R.id.nav_home);
                }
            });

        }
    }

 */
    public void guardar(View v){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        nombre = (EditText) getActivity().findViewById(R.id.editTextName);
        urlImagen = (EditText) getActivity().findViewById(R.id.editTextUrlFoto);

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(nombre.getText().toString())
                .setPhotoUri(Uri.parse(urlImagen.getText().toString()))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Actualizar user", "User profile updated.");
                            Toast.makeText(getActivity(), "Grabado", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Log.d("Actualizar user", "Falllo");
                            Toast.makeText(getActivity(), "Error grabar", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



    }
}