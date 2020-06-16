package ldansorean.s6map;

import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private static final int LOCATION_UPDATE_MIN_TIME = 1000;
    private static final int LOCATION_UPDATE_MIN_DISTANCE = 1;

    private GoogleMap mMap;
    private BitmapDescriptor markerIcon;
    private String locationProvider;
    private LocationManager locationManager;
    private Location initialLocation;
    private Geocoder geocoder;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //get last known user location
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationProvider = locationManager.getBestProvider(new Criteria(), true);
        initialLocation = locationManager.getLastKnownLocation(locationProvider);

        markerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
        geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);
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

        if (initialLocation != null) {
            onLocationChanged(initialLocation);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(locationProvider, LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        //get new position coordinates
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        Log.i("myapp", "New latitude = " + lat + " longitude = " + lng);
        LatLng position = new LatLng(lat, lng);

        //the marker text will show the address or a default text if the address is not available
        String markerText = "You are here";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null && !addresses.isEmpty())
                markerText = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            ; //just use the default text
        }

        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(position).icon(markerIcon).title(markerText));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 16));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
