package com.example.learners;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;
import android.os.AsyncTask;
import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private double fromlongitude;
    private double fromlatitude;
    private double tolongitude;
    private double tolatitude;
    private LatLng mOrigin;
    private LatLng mDestination;
    private Polyline mPolyline;
    ArrayList<LatLng> mMarkerPoints;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        Button buttonSubmit = (Button) findViewById(R.id.submit);
        TextView afstand = (TextView) findViewById(R.id.Afstand);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
                } else {
                    getCurrentLocation();
                }
            }
        });
        getCurrentLocation();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mMarkerPoints = new ArrayList<>();

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
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(0, 0);
       // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                Log.d("Map","Map clicked");
                mMap.clear();
                tolatitude = point.latitude;
                tolongitude = point.longitude;

                LatLng plek1 = new LatLng(fromlatitude, fromlongitude);
                mMap.addMarker(new MarkerOptions().position(plek1).title("From"));
                LatLng plek2 = new LatLng(tolatitude, tolongitude);

                mMap.addMarker(new MarkerOptions().position(plek2).draggable(true).title("To").
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                float[] results = new float[1];
                Location.distanceBetween(plek1.latitude, plek1.longitude,
                        plek2.latitude, plek2.longitude,
                        results);
                float rond = Math.round(results[0]);
                String toets = String.valueOf(rond);
                TextView afstand = (TextView) findViewById(R.id.Afstand);
                afstand.setText(toets);
                mMarkerPoints.clear();
                mMarkerPoints.add(plek1);
                mMarkerPoints.add(point);
                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();
                // Setting the position of the marker
                options.position(point);
                if(mMarkerPoints.size()==1){
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }else if(mMarkerPoints.size()==2){
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }
                //String hier = String.valueOf(mMarkerPoints.size());
                //Toast.makeText(MapsActivity.this,hier, Toast.LENGTH_SHORT).show();
                if(mMarkerPoints.size() >= 2){
                    mOrigin = mMarkerPoints.get(0);
                    mDestination = mMarkerPoints.get(1);
                    drawRoute();
                }
                 /**if(plek1!= null &&  plek2!=null){
                    mOrigin =  plek1;
                    mDestination =  plek2;
                    drawRoute();
                }*/
                //Toast.makeText(MapsActivity.this,Double.toString(fromlatitude), Toast.LENGTH_SHORT).show();
                //Toast.makeText(MapsActivity.this,Double.toString(fromlongitude), Toast.LENGTH_SHORT).show();
                //Toast.makeText(MapsActivity.this,Double.toString(tolatitude), Toast.LENGTH_SHORT).show();
                //Toast.makeText(MapsActivity.this,Double.toString(tolongitude), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onLocationChanged(Location location) {
        if (location != null) {
            LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


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
        LocationServices.getFusedLocationProviderClient(MapsActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(MapsActivity.this)
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            LatLng nuwe = new LatLng(latitude, longitude);
                            fromlatitude = nuwe.latitude;
                            fromlongitude = nuwe.longitude;
                            mMap.addMarker(new MarkerOptions().position(nuwe).title("Your location"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(nuwe));
                            float zoom = 15 ;
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nuwe, zoom));
                            //mMap.moveCamera(CameraUpdateFactory.zoomIn());
                        }
                    }

                }, Looper.getMainLooper());
    }
    private void drawRoute(){
        //Toast.makeText(MapsActivity.this, "drawRoute",Toast.LENGTH_SHORT).show();
        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(mOrigin, mDestination);

        DownloadTask downloadTask = new DownloadTask();

        // Begin download van Google Directions API
        downloadTask.execute(url);

    }
    private String getDirectionsUrl(LatLng origin,LatLng dest){
        //Toast.makeText(MapsActivity.this, "getDirectionsUrl",Toast.LENGTH_SHORT).show();
        // Origin van route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination van route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Key
        String key = "key=AIzaSyAa4lavKgkCW4glgDCXFOuW1CrsUomCTSw";
        //String key = "key=" + getString(R.string.google_maps_key);

        // Bou parameter string
        String parameters = str_origin+"&"+str_dest+"&"+key;

        // Output format
        String output = "json";

        // bou url vir api
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }
    /** Download method vir json data van url*/
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            //Maak http connection Om te communicate met url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connect met url
            urlConnection.connect();

            // lees url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception on download", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    /** Class om die data te download van Google Directions Url*/
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // download data in die non ui thread
        @Override
        protected String doInBackground(String... url) {

            // om die data van web server te stoor
            String data = "";

            try{
                // Kry data van web server
                data = downloadUrl(url[0]);
                Log.d("DownloadTask","DownloadTask : " + data);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }


/**Class om die Google Directions te parse in JSON format*/
private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

    // Parsing the data in non-ui thread
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try{
            jObject = new JSONObject(jsonData[0]);
            DirectionsJSONParser parser = new DirectionsJSONParser();

            // Begin parse data

            routes = parser.parse(jObject);
        }catch(Exception e){
            e.printStackTrace();
        }
        return routes;
    }

    // Executes UI thread na die parseing van data
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;
        Toast.makeText(getApplicationContext(),result.toString(), Toast.LENGTH_LONG).show();
        // gaan deur al die routes
        for(int i=0;i<result.size();i++){
            //points = new ArrayList<Latlng>();
            points = new ArrayList<>();
            lineOptions = new PolylineOptions();

            // Kry i-th route
            List<HashMap<String, String>> path = result.get(i);
            // kry all die punte in i-th route
            for(int j=0;j<path.size();j++){
                HashMap<String,String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            // Add al die punte na  LineOptions
            lineOptions.addAll(points);
             lineOptions.width(8);
             lineOptions.color(Color.RED);
        }

        // teken al die punte in google maps vir die i-th route
        if(lineOptions != null) {
         if(mPolyline != null){
         mPolyline.remove();
         }
         mPolyline = mMap.addPolyline(lineOptions);
         //Toast.makeText(getApplicationContext(),points.toString(), Toast.LENGTH_LONG).show();
         }else

         Toast.makeText(getApplicationContext(),"No route is found", Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(),points, Toast.LENGTH_LONG).show();
    }
}

}