package com.jesusvillarroya.example.Sweets;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static java.sql.DriverManager.println;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    ArrayList<Double> latitudes = new ArrayList<Double>();
    ArrayList<Double> longitudes = new ArrayList<Double>();
    ArrayList<Double> latitudesRead = new ArrayList<Double>();
    ArrayList<Double> longitudesRead = new ArrayList<Double>();
    LocationManager mlocManager;
    Localizacion Local;
    Double la = null;
    Double lo = null;
    String fileName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager() .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent i = getIntent();
        fileName = i.getStringExtra(MainActivity.EXTRA_MESSAGE);

        //Comprobar permisos
       if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationStart();
        }

    }

    //Persistir datos para rotación pantalla
   @Override
    protected void onSaveInstanceState(Bundle guardarEstado){
        super.onSaveInstanceState(guardarEstado);
            ArrayList<String> lat = new ArrayList<String>();
            ArrayList<String> lon = new ArrayList<String>();
            for (int i = 0; i < latitudes.size(); i++) {
                lat.add(latitudes.get(i).toString());
                lon.add(longitudes.get(i).toString());
            }
            lat.add(la.toString());
            lon.add(lo.toString());
            guardarEstado.putStringArrayList("lat", lat);
            guardarEstado.putStringArrayList("lon", lon);


    }

    //Recuperar datos en rotación
    @Override
    protected void onRestoreInstanceState(Bundle recuperarEstado){
        super.onRestoreInstanceState(recuperarEstado);
        if(!recuperarEstado.isEmpty()) {
            ArrayList<String> lat = new ArrayList<String>();
            ArrayList<String> lon = new ArrayList<String>();

           lat = recuperarEstado.getStringArrayList("lat");
           lon = recuperarEstado.getStringArrayList("lon");

            Iterator<String> latNombre = lat.iterator();
            while(latNombre.hasNext()){
                String elemento = latNombre.next();
                latitudes.add(Double.parseDouble(elemento));
            }
            Iterator<String> lonNombre = lon.iterator();
            while(lonNombre.hasNext()){
                String elemento = lonNombre.next();
                longitudes.add(Double.parseDouble(elemento));
            }
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng lugar = null;

        //Localizaciones
        //latitudes.add(41.9034534); longitudes.add(-1.7254545);
        if(latitudes.isEmpty()&&longitudes.isEmpty()){
            fileRead();
        }else{
            fileSave();
        }

        //marcas
        if (!latitudes.isEmpty() && !longitudes.isEmpty()){
            for(int i=0;i<latitudes.size();i++){
                lugar = new LatLng(longitudes.get(i), latitudes.get(i));
                mMap.addMarker(new MarkerOptions().position(lugar).title("¡Caramelos!"));
                //Asignamos el listener
                mMap.setOnMarkerClickListener(this);
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(lugar));
        }

       /* Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(41.9059825, -1.7344895), new LatLng(41.9012312, -1.7265656))
                .width(5)
                .color(Color.RED));*/

    }

    public void fileSave(){

        File file1 = new File(getApplicationContext().getFilesDir(), fileName);
        file1.delete();
        File file = new File(getApplicationContext().getFilesDir(), fileName);
        FileOutputStream outputStream;


        try {
            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);

            for(int i=0;i<longitudes.size();i++) {
                String key = latitudes.get(i).toString();
                String value = longitudes.get(i).toString();
                String place = key + "/" + value + "@";
                outputStream.write(place.getBytes());
            }
            outputStream.close();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void fileRead(){
        try{
            BufferedReader fin = new BufferedReader(new InputStreamReader(openFileInput(fileName)));
            String texto = fin.readLine();
            String [] lugaresPareados = texto.split("@");

                for (int i=0; i<lugaresPareados.length; i++){
                    String [] separado = lugaresPareados[i].split("/");
                    //Toast.makeText(this, separado[0]+" Y " + separado[1], Toast.LENGTH_SHORT).show();
                    //localizacionesRead.put(Double.parseDouble(separado[0]),Double.parseDouble(separado[1]));
                    longitudesRead.add(Double.parseDouble(separado[0]));
                    longitudes = longitudesRead;
                    latitudesRead.add(Double.parseDouble(separado[1]));
                    latitudes = latitudesRead;
                }
            fin.close();
           // Toast.makeText(this, texto , Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex)
        {
            Log.e("Ficheros", "Error al leer fichero desde memoria interna");
        }
    }


    public void startGPS(View view){
        locationStart();

    }
    public void stopGPS(){
        //Paramos el listener para que no vuelva a actualizar la información
        mlocManager.removeUpdates(Local);
        mlocManager = null;
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
       // Toast.makeText(this,marker.getTitle() + "\n",Toast.LENGTH_SHORT).show();

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    public void locationStart(){
        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Local = new Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);



       // Toast.makeText(getApplicationContext(),"¡Graba un nuevo lugar!",Toast.LENGTH_LONG).show();

    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }

    public void setLocation(final Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            //Paramos el listener para que no vuelva a actualizar la información
            stopGPS();
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    //Preguntamos al usuario si quiere guardar o no la localización.
                    AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
                    alertDialog.setTitle("¿¿Te han dado caramelos??");
                    alertDialog.setMessage("Estás en: \n"
                            + DirCalle.getAddressLine(0));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Siiiii",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    //Guardamos la localización
                                    la=loc.getLatitude();
                                    lo=loc.getLongitude();


                                    //Podríamos añadir comentarios ahora.
                                    Toast.makeText(MapsActivity.this, "Genial!!\nEncontraste un nuevo lugar! " , Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(MapsActivity.this, loc.getLatitude() +" & " + loc.getLongitude() , Toast.LENGTH_SHORT).show();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //No guardamos nada.
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public class Localizacion implements LocationListener {
        MapsActivity mainActivity;

        public MapsActivity getMainActivity() {

            return mainActivity;
        }

        public void setMainActivity(MapsActivity mainActivity) {

            this.mainActivity = mainActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion

            loc.getLatitude();
            loc.getLongitude();

            //String Text = "Mi ubicacion actual es: " + "\n Lat = " + loc.getLatitude() + "\n Long = " + loc.getLongitude();
            //Toast.makeText(getApplicationContext(), Text, Toast.LENGTH_LONG).show();
            this.mainActivity.setLocation(loc);

        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            println("GPS Desactivado");
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            println("GPS Activado");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }

}
