package com.example.proyectorecuperacion1ev;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
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
    private SQLiteDatabase db;
    private Button btRepoblarBBDD;
    private Button btBorrar;
    private TextView tvProvincia;
    private TextView tvCapital;
    private TextView tvPoblacion;
    private TextView tvLatitud;
    private TextView tvLongitud;
    private TextView tvClima;
    private TextView tvTemperatura;
    private TextView tvVelocidadViento;
    private LocationManager locationManager = null;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spComarcas = findViewById(R.id.sp_comarca);
        btRepoblarBBDD = findViewById(R.id.bt_repoblar);
        btRepoblarBBDD.setOnClickListener(this::onRepoblarBBDD);
        btBorrar = findViewById(R.id.bt_borrar);
        btBorrar.setOnClickListener(this::onBorrar);
        tvProvincia = findViewById(R.id.tv_provincia);
        tvCapital = findViewById(R.id.tv_capital);
        tvPoblacion = findViewById(R.id.tv_poblacion);
        tvLatitud = findViewById(R.id.tv_latitud);
        tvLongitud = findViewById(R.id.tv_longitud);
        tvClima = findViewById(R.id.tv_clima);
        tvTemperatura = findViewById(R.id.tv_temperatura);
        tvVelocidadViento = findViewById(R.id.tv_velocidadViento);
        configurarSpinnerComarcasListener();
        configurarPoliticaThreads();
        pedirPermisoInternet();
        iniciarLocalizacion();
    }

    private void iniciarLocalizacion() {

        pedirPermisosLocalizacion();
        inicializarLocationManager();
        inicializarInfoGps();
        inicializarLocationListener();

    }

    private void inicializarLocationManager() {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

    }

    private void inicializarInfoGps() {

        Location localizacion = obtenerUltimaUbicacionConocida();
        actualizarInfoGps(localizacion);

    }

    private Location obtenerUltimaUbicacionConocida() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            System.out.println("No se a obtenido los permisos necesarios");
            return null;
        }
        Location localizacion = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        return localizacion;
    }

    private void actualizarInfoGps(Location localizacion) {

        if (localizacion == null) {
            System.out.println("Localizacion null");
            return;
        }

        double latitud = localizacion.getLatitude();
        double longitud = localizacion.getLongitude();
        actualizarIULocalizacionGps(latitud, longitud);
        MeteorologiaWS meteo = new MeteorologiaWS();
        meteo.consultarDatosMeteo((float) latitud, (float) longitud);
        float temperatura = meteo.getTemperatura();
        float velocidadViento = meteo.getVelocidadViento();
        actualizarIUMeteorlogiaGPS(temperatura, velocidadViento);
    }

    private void actualizarIULocalizacionGps(double latitud, double longitud) {

        tvLatitud.setText(String.format("%.4f", latitud));
        tvLongitud.setText(String.format("%.4f", longitud));

    }

    private void actualizarIUMeteorlogiaGPS(float temperatura, float velocidadViento){

        String texto = String.format("Temperatura: %.1f ºC, Velocidad del viento: %.1f Km/h", temperatura, velocidadViento);
        tvClima.setText(texto);

    }

    private void inicializarLocationListener() {

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                actualizarInfoGps(location);

            }
        };

    }


    private void pedirPermisosLocalizacion() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);

        }

    }


    private void configurarPoliticaThreads() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    private void pedirPermisoInternet(){
        if (ContextCompat. checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new
                    String[]{Manifest.permission.INTERNET}, 0);
    }

    private void encenderListenerLocalizacion() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, locationListener);

    }

    private  void apagarListenerLocalizacion(){

        locationManager.removeUpdates(locationListener);

    }

    @Override
    protected void onPause() {
        db.close();
        guardarPreferencias();
        apagarListenerLocalizacion();
        System.out.println("Estoy en el on pause");
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("Estoy en el on resume");
        db = new GestorBBDD(this, 1).getWritableDatabase();
       cargarSpinnerComarcas();
        cargarPreferencias();
        if(comarcaPreferida != null){
            seleccionarComarca(comarcaPreferida);
        }
        encenderListenerLocalizacion();

    }

    public void onNavegar(View view){

       String comarca = getComarcaSeleccionada();
        Toast.makeText(this,"Navegando a " + getComarcaSeleccionada(), Toast.LENGTH_SHORT).show();
       navegarComarca(comarca);

    }


    private void navegarComarca(String comarca){

        String comarcaUrl = "https://" + getComarcaUrl(comarca);
        navegar(comarcaUrl);

    }


    private void navegar(String url) {

        Intent abrirWeb = new Intent();

        abrirWeb.setAction(Intent.ACTION_VIEW);

        abrirWeb.setData(Uri.parse(url));

        this.startActivity(abrirWeb);

    }

    private String getComarcaUrl(String comarca){

        String consultaComarca = "SELECT url FROM Comarques WHERE nom = ?";

        String[] argumentos = {comarca};

        Cursor cursor = db.rawQuery(consultaComarca, argumentos);

        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            String comarcaUrl = cursor.getString(0);
            cursor.close();
            return comarcaUrl;
        }
        return null;

    }



    private int provinciaFiltro = SeleccionProvinciaActivity.TODAS;
    private static final int REQUEST_CODE = 1;

    public void onSeleccionarProvincia(View view){

        Intent intent = new Intent(this, SeleccionProvinciaActivity.class);

        intent.putExtra(SeleccionProvinciaActivity.PROVINCIA, provinciaFiltro);

        Toast.makeText(this,"Seleccionando provincia", Toast.LENGTH_SHORT).show();

        startActivityForResult(intent, REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            if(resultCode == RESULT_OK){
                provinciaFiltro = data.getIntExtra(SeleccionProvinciaActivity.PROVINCIA, SeleccionProvinciaActivity.TODAS);
            }
        }
    }

    private void onBorrar(View view){

        String comarcaSeleccionada = getComarcaSeleccionada();

        borrarComarca(comarcaSeleccionada);
        cargarSpinnerComarcas();
        Toast.makeText(this, "La comarca " + comarcaSeleccionada + " a sido eliminada con exito", Toast.LENGTH_SHORT).show();


    }

    private void borrarComarca(String comarca){

        String consultaComarca = "DELETE FROM Comarques WHERE nom = ?";

        Object[] argumentos = {comarca};

        db.execSQL(consultaComarca, argumentos);

    }

    private String[] getComarcas(String filtroProvincia){

        String consulta = "SELECT nom FROM Comarques WHERE Provincia = ? OR ? IS NULL AND Provincia = Provincia";
        String[] argumentos = filtroProvincia != null && !filtroProvincia.isEmpty() ? new String[] {filtroProvincia, filtroProvincia} : null;
        if(db == null){
            return null;
        }
        Cursor cur = db.rawQuery(consulta, argumentos);

        List<String> comarcas = null;

        if(cur.moveToFirst()){

            comarcas = new ArrayList<>();
            do {

                String nombreComarca = cur.getString(0);
                comarcas.add(nombreComarca);

            }while (cur.moveToNext());

        }
        cur.close();
        return comarcas == null ? null:comarcas.toArray(new String[0]);
    }

    private SpinnerAdapter crearSpinnerComarcasAdapter(){

        String filtro = getFiltroFromSelectedProvincia(provinciaFiltro);

        String[] comarcas = getComarcas(filtro);
        if (comarcas == null) {
            return null;
        }
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

        String consultaComarca = "SELECT * FROM Comarques WHERE nom = ?";

        String[] argumentos = {comarca};

        Cursor cursor = db.rawQuery(consultaComarca, argumentos);

        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            String provincia = cursor.getString(1);
            String capital = cursor.getString(2);
            int poblacion = cursor.getInt(3);
            float latitud = cursor.getFloat(4);
            float longitud = cursor.getFloat(5);
            cursor.close();
            MeteorologiaWS meteo = new MeteorologiaWS();
            meteo.consultarDatosMeteo(latitud, longitud);
            float temperatura = meteo.getTemperatura();
            float velocidadViento = meteo.getVelocidadViento();
            tvProvincia.setText(provincia);
            tvCapital.setText(capital);
            tvPoblacion.setText(Integer.toString(poblacion));
            tvTemperatura.setText(Float.toString(temperatura) + " ºC");
            tvVelocidadViento.setText(Float.toString(velocidadViento) + " Km/h");
        }


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

        if(comarca == null){
            return;
        }

        ArrayAdapter<String> adaptador = (ArrayAdapter<String>) spComarcas.getAdapter();

        if(adaptador == null){
            return;
        }

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

    private void onRepoblarBBDD(View view){

        repoblarBBDD();
        cargarSpinnerComarcas();
        seleccionarComarca(comarcaPreferida);
        Toast.makeText(this,"Base de datos repoblada", Toast.LENGTH_SHORT).show();

    }

    private void repoblarBBDD(){

        db.execSQL("DELETE FROM Comarques");
        for (String comandoInsert: getComandosInsertRepoblarBBDD()) {
            db.execSQL(comandoInsert);
        }
        
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