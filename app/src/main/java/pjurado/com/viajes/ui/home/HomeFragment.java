package pjurado.com.viajes.ui.home;

import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import pjurado.com.viajes.R;

public class HomeFragment extends Fragment {
    private ViewPager viewPager;
    private MyViewPagerAdapter adapter;
    private LinearLayout dotsLayout;
    private Button btnBack, btnNext;

    private ArrayList<Uri> gallery = new ArrayList<>();
    private ArrayList<String> content = new ArrayList<>();
    private ArrayList<String> title = new ArrayList<>();
    private TextView[] dots;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        //ImageView imagen = getActivity().findViewById(R.id.imagenHome);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_baseline_location_city_64_green);
        fab.hide();


        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        listaFotos();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = view.findViewById(R.id.viewPaper);
        btnBack = view.findViewById(R.id.btnBack);
        btnNext = view.findViewById(R.id.btnNext);
        dotsLayout  = view.findViewById(R.id.layoutDots);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() == 0){
                    viewPager.setCurrentItem(gallery.size()-1);
                }
                else{
                    int back = getItem(-1);
                    viewPager.setCurrentItem(back);
                }

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() == gallery.size() -1){
                    viewPager.setCurrentItem(0);
                }
                else{
                    int back = getItem(1);
                    viewPager.setCurrentItem(back);
                }
            }
        });

    }

    private int getItem(int i){
        return viewPager.getCurrentItem()+i;
    }

    private void loadViewPaper(){
        adapter = new MyViewPagerAdapter(getActivity().getSupportFragmentManager());
        for (int i = 0; i < gallery.size(); i++){
            adapter.addFragment(newInstance(title.get(i), content.get(i), gallery.get(i)));
        }

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(pageListener);
    }

    private SliderFragment newInstance(String title, String content, Uri urlImagen){
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("content", content);
        bundle.putString("image", urlImagen.toString());

        SliderFragment fragment = new SliderFragment();
        fragment.setArguments(bundle);

        return fragment;
    }


    public void listaFotos() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference listRef = storage.getReference().child("/ImagenesSlide");

        listRef.listAll()
                .addOnCompleteListener(new OnCompleteListener<ListResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ListResult> task) {
                        if (task.isSuccessful()){
                            final ListResult listResult = task.getResult();

                            for (StorageReference item : listResult.getItems()) {
                                // All the items under listRef.
                                Log.d("Foto", "F ");

                                item.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        Uri url = task.getResult();
                                        gallery.add(url);

                                        if (listResult.getItems().size() == gallery.size()){
                                            llenarInstrucciones();
                                            loadViewPaper();
                                            addDots(0);
                                        }
                                    }
                                });

                            }

                        }
                    }
                });

    }

    private void addDots(int currentPage){
        dots = new TextView[gallery.size()];

        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++){
            dots[i] = new TextView(getActivity());
            dots[i].setText((Html.fromHtml("&#8226")));
            dots[i].setTextSize(35);
            if (i==currentPage){
                dots[i].setTextColor(getResources().getColor(R.color.colorAccent));
            }
            else{
                dots[i].setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            }
            dotsLayout.addView(dots[i]);

        }
    }

    ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void llenarInstrucciones() {
        title.add("Viajes en Autocaravana");
        content.add("Hay lugares donde uno se queda, y lugares que quedan en uno");
        title.add("Datos de lugares");
        content.add("Se pueden guardar datos de cada lugar desde cualquier web. Selccionando compartir en el navegador y luego la aplicación viajes.");
        title.add("Park4night");
        content.add("Si compartes datos desde la app Park4night, se recuperarán las coordenadas del lugar para mostrar el área o el parking en el mapa.");
        title.add("Coordenadas de lugar");
        content.add("Al añadir áreas o parkings desde la app Park4night, se pueden añadir esas coordenadas como coordenadas del lugar");
        title.add("Rutas y recorrido");
        content.add("Para generar rutas o recorridos todos los lugares del viaje tienen que tener coordenadas");
        title.add("Añadir lugares a viajes");
        content.add("Para añadir o quitar lugares de un viaje se hará con una pulsación larga");
        title.add("Ordenar Viaje");
        content.add("Se puede cambiar el orden de visita arrastrando los lugares");
        title.add("Inicio y fin de ruta");
        content.add("En settings se pueden añadir coordenadas de origen y final del viaje.");



        for (int i = title.size(); i <= gallery.size(); i++){
            title.add("Viajes en Autocaravana");
            content.add("Hay lugares donde uno se queda, y lugares que quedan en uno");
        }
    }
}