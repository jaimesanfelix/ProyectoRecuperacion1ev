package com.example.proyectorecuperacion1ev;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

// Esta clase es la que maneja la BBDD. La podríamos reusar para cualquier proyecto de bbdd con SQLite
public class GestorBBDD extends SQLiteOpenHelper {

   // private String sqlCreacion;
    public static final String NOMBRE_BBDD = "comarcas";

    // El constructor simplemente llama al constructor padre. Podemos generarlo automáticamente con el menú "Generate-Constructor"
    public GestorBBDD(@Nullable Context context, @Nullable String name, int version, String sqlCreacion) {
        super(context, name, null, version); // Le pasamos null en el parámetro SQLiteDatabase.CursorFactory para que use uno por defecto
       // this.sqlCreacion = sqlCreacion;
    }

    public GestorBBDD(@Nullable Context context, int version){
        super(context, NOMBRE_BBDD, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Comarques (nom TEXT, provincia TEXT, capital TEXT, poblacio INTEGER, latitud FLOAT, longitud FLOAT, url TEXT)");
      //  db.execSQL(sqlCreacion);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Cuando cambiamos de versión, modificaciones a hacer en el esquema de BBDD
        // No lo implementaremos
    }
}
