package com.example.proyectorecuperacion1ev;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import java.util.EventListener;

public class SeleccionProvinciaActivity extends AppCompatActivity {

    public static final String PROVINCIA = "provincia";
    public static final int TODAS = R.id.rb_todas;
    public static final int VALENCIA = R.id.rb_valencia;
    public static final int ALICANTE = R.id.rb_alicante;
    public static final int CASTELLON = R.id.rb_castellon;

    private int filtroProvincia;

    private RadioGroup rgFiltroProvincia;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_provincia);
        Intent intentLlamada = this.getIntent();
        filtroProvincia = intentLlamada.getIntExtra(PROVINCIA, TODAS);
        rgFiltroProvincia = findViewById(R.id.rbg_provincias);
        inicializarFiltro();

        rgFiltroProvincia.setOnCheckedChangeListener((group,checkedId) -> filtroProvincia = checkedId);

        findViewById(R.id.bt_aceptar).setOnClickListener(this::onAceptar);

        mediaPlayer = MediaPlayer.create(this, R.raw.himne);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(0.5f, 0.5f);
        mediaPlayer.seekTo(0);
        mediaPlayer.start();
    }

    private void onAceptar(View view){

        Intent seleccion = new Intent();
        seleccion.putExtra(PROVINCIA, filtroProvincia);

        setResult(RESULT_OK, seleccion);

        this.finish();

    }

    @Override
    protected void onPause() {
        mediaPlayer.stop();
        super.onPause();
    }

    private void inicializarFiltro(){

        rgFiltroProvincia.check(filtroProvincia);

        /*switch (filtroProvincia){
            case TODAS:
                rgFiltroProvincia.check(R.id.rb_todas);
                break;
            case VALENCIA:
                rgFiltroProvincia.check(R.id.rb_valencia);
                break;
            case ALICANTE:
                rgFiltroProvincia.check(R.id.rb_alicante);
                break;
            case CASTELLON:
                rgFiltroProvincia.check(R.id.rb_castellon);
                break;
        }*/

    }

}