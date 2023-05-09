package com.example.proyectorecuperacion1ev;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner spComarcas;
    private static final String COMARCA = "com.example.proyectoRecuperacion.MainActivity.comarca";
    private String comarcaPreferida = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spComarcas = findViewById(R.id.sp_comarca);
        cargarSpinnerComarcas();
        cargarPreferencias();
        if(comarcaPreferida != null){
            seleccionarComarca(comarcaPreferida);
        }
        getComandosInsertRepoblarBBDD();
    }

    @Override
    protected void onPause() {
        guardarPreferencias();
        super.onPause();
    }

    public void onNavegar(View view){

       navegar("https://google.com//");

    }

    private void navegar(String url) {

        Intent abrirWeb = new Intent();

        abrirWeb.setAction(Intent.ACTION_VIEW);

        abrirWeb.setData(Uri.parse(url));

        this.startActivity(abrirWeb);

    }

    private int provinciaFiltro = SeleccionProvinciaActivity.TODAS;
    private static final int REQUEST_CODE = 1;

    public void onSeleccionarProvincia(View view){

        Intent intent = new Intent(this, SeleccionProvinciaActivity.class);

        intent.putExtra(SeleccionProvinciaActivity.PROVINCIA, provinciaFiltro);

        startActivityForResult(intent, REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            if(resultCode == RESULT_OK){
                provinciaFiltro = data.getIntExtra(SeleccionProvinciaActivity.PROVINCIA, SeleccionProvinciaActivity.TODAS);
                cargarSpinnerComarcas();
                if(comarcaPreferida != null){
                    seleccionarComarca(comarcaPreferida);
                }
            }
        }
    }

    private String[] getComarcas(String filtroProvincia){

        String consulta = "SELECT nom FROM Comarques WHERE Provincia = ? OR ? IS NULL";
        String[] argumentos = filtroProvincia != null && !filtroProvincia.isEmpty() ? new String[] {filtroProvincia, filtroProvincia} : null;
        return new String[] {
                "Comarca1", "Comarca2", "Comarca3", "Comarca4"
        };
    }

    private SpinnerAdapter crearSpinnerComarcasAdapter(){

        String filtro = getFiltroFromSelectedProvincia(provinciaFiltro);

        String[] comarcas = getComarcas(filtro);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, comarcas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;

    }

    private void cargarSpinnerComarcas(){

        SpinnerAdapter adapter = crearSpinnerComarcasAdapter();
        spComarcas.setAdapter(adapter);

    }

    private static String getFiltroFromSelectedProvincia(int selectedProvincia){

        switch (selectedProvincia){

            case SeleccionProvinciaActivity.VALENCIA:
                return "València";

            case SeleccionProvinciaActivity.ALICANTE:
                return "Alacant";

            case SeleccionProvinciaActivity.CASTELLON:
                return "Castelló";

            default:
                return null;

        }

    }

    class MiOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            String comarca = spComarcas.getItemAtPosition(position).toString();

            mostrarInfoComarca(comarca);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private void mostrarInfoComarca(String comarca) {

        //TO-DO Se completara cuando empecemos con las bases de datos

    }

    private void guardarPreferencias(){

        SharedPreferences preferencias = getPreferences(Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferencias.edit();

        String comarca = getComarcaSeleccionada();

        if(comarca == null){
            comarca = "";
        }

        editor.putString(COMARCA, comarca);

        editor.commit();

    }

    private void cargarPreferencias(){

        SharedPreferences preferencias = getPreferences(Context.MODE_PRIVATE);

        comarcaPreferida = preferencias.getString(COMARCA, null);

    }

    private void seleccionarComarca(String comarca){

        ArrayAdapter<String> adaptador = (ArrayAdapter<String>) spComarcas.getAdapter();

        int posicion = adaptador.getPosition(comarca);

        spComarcas.setSelection(posicion);

    }

    private String getComarcaSeleccionada(){

        if(spComarcas.getSelectedItem() != null){
            return spComarcas.getSelectedItem().toString();
        }
        return null;
    }

    private void configurarSpinnerComarcasListener(){

        AdapterView.OnItemSelectedListener listener;

        listener = new MiOnItemSelectedListener();

        spComarcas.setOnItemSelectedListener(listener);


    }

    private List<String> getComandosInsertRepoblarBBDD(){

        List<String> inserts = new ArrayList<>();

        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        try {
            fis = openFileInput("fitxerInserts.txt");
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);

            String linea = br.readLine();

            while (linea != null){
                inserts.add(linea);
                linea = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "El fichero seleccionado no existe", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error leyendo el fichero seleccionado", Toast.LENGTH_SHORT).show();
        }finally {
            try {
                if(br != null){
                    br.close();
                }
                if(isr != null){
                    isr.close();
                }
                if(fis != null){
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error cerrando el fichero seleccionado", Toast.LENGTH_SHORT).show();
            }

        }

        return inserts;
    }

}