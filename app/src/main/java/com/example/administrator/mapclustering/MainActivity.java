package com.example.administrator.mapclustering;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONException;

import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,ClusterManager.OnClusterClickListener,ClusterManager.OnClusterItemClickListener {
    private ClusterManager<MyClusterItem> mClusterManager;
    private GoogleMap mMap;
    private Toast mToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        mMap=googleMap;
        LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_ico);

        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in Sydney").snippet("Hiiiiii...!").icon(icon));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        try {
            setUpClusterer(googleMap);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setUpClusterer(GoogleMap googleMap) throws JSONException {
        // Position the map.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<>(this, googleMap);

        // Point the map's listeners at the listeners implemented by the clustermClusterManager.cluster();
        // manager.
        googleMap.setOnCameraIdleListener(mClusterManager);
        googleMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setRenderer(new MyClusterRenderer(this, mMap,
                mClusterManager));
        readItems();

        // Add cluster items (markers) to the cluster manager.
        //addItems();
        //mClusterManager.cluster();

    }
    private void readItems() throws JSONException {
        InputStream inputStream = getResources().openRawResource(R.raw.radar_search);
        List<MyClusterItem> items = new MyItemReader().read(inputStream);
        mClusterManager.addItems(items);
        /*mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(
                new MyCustomAdapterForItems());*/
    }

    private void addItems() {
        // Set some lat/lng coordinates to start with.
        double lat = 51.5145160;
        double lng = -0.1270060;

        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < 10; i++) {
            double offset = i / 60d;
            lat = lat + offset;
            lng = lng + offset;
            MyClusterItem offsetItem = new MyClusterItem(lat, lng, null, null);
            mClusterManager.addItem(offsetItem);
        }
    }
    protected GoogleMap getMap() {
        return mMap;
    }

    @Override
    public boolean onClusterClick(Cluster cluster) {
        showToast(this, "You clicked a cluster.");
        return true;
    }

    @Override
    public boolean onClusterItemClick(ClusterItem clusterItem) {
        showToast(this, "You clicked an individual marker of the cluster.");
        return true;
    }

    public void showToast(Context context,String msg) {
        if(mToast!=null) {
            mToast.cancel();
        }
        mToast=Toast.makeText(context,msg,Toast.LENGTH_SHORT);
        mToast.show();
    }

    public class MyClusterRenderer extends DefaultClusterRenderer<MyClusterItem> {

        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());

        public MyClusterRenderer(Context context, GoogleMap map,
                                 ClusterManager<MyClusterItem> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(MyClusterItem item,
                                                   MarkerOptions markerOptions) {
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_ico);
            markerOptions.icon(icon);
        }

        @Override
        protected void onClusterItemRendered(MyClusterItem clusterItem, Marker marker) {
            super.onClusterItemRendered(clusterItem, marker);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<MyClusterItem> cluster, MarkerOptions markerOptions){
            //for default marker just simply call super.onBeforeClusterRendered(cluster,markerOptions)
            //super.onBeforeClusterRendered(cluster,markerOptions);

            //BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA);
            //BitmapDescriptor mIcon=BitmapDescriptorFactory.fromResource(R.drawable.shape_circle);

            final Drawable clusterIcon = getResources().getDrawable(R.drawable.shape_circle);
            clusterIcon.setColorFilter(getResources().getColor(android.R.color.holo_orange_light), PorterDuff.Mode.SRC_ATOP);

            mClusterIconGenerator.setBackground(clusterIcon);

            //modify padding for one or two digit numbers
            if (cluster.getSize() < 10) {
                mClusterIconGenerator.setContentPadding(20, 10, 20, 10);
            }
            else {
                mClusterIconGenerator.setContentPadding(20, 20, 20, 20);
            }
            //markerOptions.icon(markerDescriptor);

            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }
    }
}
