package pjurado.com.viajes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import pjurado.com.viajes.modelo.Lugares;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        TextView tVNombre = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textViewNombre);
        TextView tvMail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textViewMail);
        ImageView iVFotoPerfil = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageViewPhPerfil);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            //String name = user.getDisplayName();
            tvMail.setText(user.getEmail());
            //Uri photoUrl = user.getPhotoUrl();
            tVNombre.setText(user.getDisplayName());
            if(user.getPhotoUrl()!= null) {
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(iVFotoPerfil);
            }

        }



        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_viajes, R.id.nav_perfil)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //Guardar datos de login
        SharedPreferences mFilePreferences = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mFilePreferences.edit();
        String email = getIntent().getStringExtra("email");
        editor.putString("email", email);
        Log.d("Grabo", email);
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        boolean retValue = super.onCreateOptionsMenu(menu);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // The NavigationView already has these same navigation items, so we only add
        // navigation items to the menu here if there isn't a NavigationView

            getMenuInflater().inflate(R.menu.main, menu);
            return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.logout ){
            SharedPreferences mFilePreferences = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mFilePreferences.edit();
            Log.d("Borro Login", mFilePreferences.getString("email", null));
            editor.clear();
            editor.commit();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        if (NavigationUI.onNavDestinationSelected(item,
                Navigation.findNavController(this, R.id.nav_host_fragment)))
            return true;
        else {

            return super.onOptionsItemSelected(item);
        }
    }
/*
    private void modificaPerfil() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationView navigationView = findViewById(R.id.nav_view);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navController.navigate(R.id.nav_perfil);

    }
*/

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


}