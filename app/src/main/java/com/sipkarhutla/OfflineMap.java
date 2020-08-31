package com.sipkarhutla;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.core.constants.Constants;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.OnCameraTrackingChangedListener;
import com.mapbox.mapboxsdk.location.OnLocationClickListener;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.instruction.InstructionView;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.location.replay.ReplayRouteLocationEngine;
import com.mapbox.services.android.navigation.v5.milestone.Milestone;
import com.mapbox.services.android.navigation.v5.milestone.MilestoneEventListener;
import com.mapbox.services.android.navigation.v5.milestone.VoiceInstructionMilestone;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.NavigationEventListener;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.offroute.OffRouteListener;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;

import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

/**
 * Download and view an offline map using the Mapbox Android SDK.
 */
public class OfflineMap extends AppCompatActivity implements OnMapReadyCallback, Callback<DirectionsResponse>, MapboxMap.OnMapClickListener, NavigationEventListener,
        OffRouteListener, ProgressChangeListener, MilestoneEventListener, OnLocationClickListener, PermissionsListener, OnCameraTrackingChangedListener {

    private boolean isEndNotified;
    private ProgressBar progressBar;
    private OfflineManager offlineManager;
    LatLng tower,mylocation;

    // JSON encoding/decoding
    public static final String JSON_CHARSET = "UTF-8";
    public static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";

    private String BEARING_TOLERANCE = "90.0";
    private MapboxMap mapboxMap;

    @BindView(R.id.mapView)
    MapView mapView = null;
    @BindView(android.R.id.content)
    View contentLayout;
    @BindView(R.id.instructionView)
    InstructionView instructionView;

    private Point origin;
    private Point destination;
    private Polyline polyline;

    private final OfflineMap.RerouteActivityLocationCallback callback = new OfflineMap.RerouteActivityLocationCallback(OfflineMap.this);
    private Location lastLocation;
    private ReplayRouteLocationEngine mockLocationEngine;
    private MapboxNavigation navigation;
    private boolean running;
    private boolean tracking;
    private boolean wasInTunnel = false;
    // variables for adding location layer
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private boolean isInTrackingMode;

    FloatingActionButton fbtnTower,fbtnAllTower,fbtnNavigate;
    //navigation
    // variables for calculating and drawing a route
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;
    // variables needed to initialize navigation
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.access_token));

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_offline_map);

        mapView = findViewById(R.id.mapView);

        fbtnNavigate = findViewById(R.id.btnNavigate);
        fbtnTower = findViewById(R.id.btnTower);
        fbtnNavigate.setEnabled(false);

        mapView.onCreate(savedInstanceState);
       mapView.getMapAsync(this);

       fbtnTower.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               setTower();
           }
       });

        /*   btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllTower();
            }
        });
        */

    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.OUTDOORS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                // Set up the OfflineManager
                offlineManager = OfflineManager.getInstance(OfflineMap.this);
                if (offlineManager != null) {

                    enableLocationComponent(style);
                    final String nLat = getIntent().getStringExtra("lat");
                    final String nLng = getIntent().getStringExtra("lng");

                    double lat = Double.parseDouble(nLat);
                    double lng = Double.parseDouble(nLng);

                    tower = new LatLng(lat, lng);



                    mapboxMap.addMarker(new MarkerOptions()
                            .position(new LatLng(tower))
                            .title("Lokasi Kebakaran"));
                    mapboxMap.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(@NonNull Marker marker) {
                            // Toast.makeText(getApplicationContext(), "tower ", Toast.LENGTH_SHORT).show();
                            double latOrigin = locationComponent.getLastKnownLocation().getLatitude();
                            double lngOrigin = locationComponent.getLastKnownLocation().getLongitude();

                            //mylocation = new LatLng(latOrigin, lngOrigin);

                            // Set the origin waypoint to the devices location
                            // origin = Point.fromLngSLat(lngOrigin,latOrigin);


                            Point destinationPoint = Point.fromLngLat(lng,lat);
                            Point originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                                    locationComponent.getLastKnownLocation().getLatitude());
                            if (originPoint == null){
                                Toast.makeText(OfflineMap.this,"Aktifkan GPS Untuk Navigasi", Toast.LENGTH_SHORT).show();
                                fbtnNavigate.setEnabled(false);
                            }else {
                                GeoJsonSource source = mapboxMap.getStyle().getSourceAs("destination-source-id");
                                if (source != null) {
                                    source.setGeoJson(Feature.fromGeometry(destinationPoint));
                                }

                                getRoute(originPoint, destinationPoint);
                                fbtnNavigate.setBackgroundColor(Color.parseColor("#268DBA"));
                                fbtnNavigate.setEnabled(true);

                            }
                            // Set the destination waypoint to the location point long clicked by the user
                            //destination = Point.fromLngLat(lng,lat);

                            // Get route from API
                            //getRoute(origin, destination);
                            return false;
                        }
                    });

                    // Add a marker in tower and move the camera
                    mapboxMap.moveCamera(CameraUpdateFactory.newLatLng(tower));
                    mapboxMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);

                    addDestinationIconSymbolLayer(style);
                    fbtnNavigate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean simulateRoute = true;
                            NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                    .directionsRoute(currentRoute)
                                    //.shouldSimulateRoute(simulateRoute)
                                    .offlineRoutingTilesPath(obtainOfflineDirectory())
                                    .offlineRoutingTilesVersion(obtainOfflineTileVersion())
                                    .build();
// Call this method with Context from within an Activity
                            NavigationLauncher.startNavigation(OfflineMap.this, options);
                        }
                    });
                }
                else {

                    // Create a bounding box for the offline region
                    LatLngBounds latLngBounds = new LatLngBounds.Builder()
                            .include(new LatLng(-5.5609, 95.0265)) // Northeast
                            .include(new LatLng(5.7395, 107.1691)) // Southwest
                            .build();

                    // Define the offline region
                    OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                            style.getUrl(),
                            latLngBounds,
                            1,
                            20,
                            OfflineMap.this.getResources().getDisplayMetrics().density);

                    // Set the metadata
                    byte[] metadata;
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put(JSON_FIELD_REGION_NAME, "Peta Sumatra");
                        String json = jsonObject.toString();
                        metadata = json.getBytes(JSON_CHARSET);
                    } catch (Exception exception) {
                        Timber.e("Failed to encode metadata: %s", exception.getMessage());
                        metadata = null;
                    }

                    // Create the region asynchronously
                    offlineManager.createOfflineRegion(
                            definition,
                            metadata,
                            new OfflineManager.CreateOfflineRegionCallback() {
                                @Override
                                public void onCreate(OfflineRegion offlineRegion) {
                                    offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);

                                    // Display the download progress bar
                                    progressBar = findViewById(R.id.progress_bar);
                                    startProgress();

                                    // Monitor the download progress using setObserver
                                    offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
                                        @Override
                                        public void onStatusChanged(OfflineRegionStatus status) {

                                            // Calculate the download percentage and update the progress bar
                                            double percentage = status.getRequiredResourceCount() >= 0
                                                    ? (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) :
                                                    0.0;

                                            if (status.isComplete()) {
                                                // Download complete
                                                endProgress(getString(R.string.simple_offline_end_progress_success));
                                            } else if (status.isRequiredResourceCountPrecise()) {
                                                // Switch to determinate state
                                                setPercentage((int) Math.round(percentage));
                                            }
                                        }

                                        @Override
                                        public void onError(OfflineRegionError error) {
                                            // If an error occurs, print to logcat
                                            Timber.e("onError reason: %s", error.getReason());
                                            Timber.e("onError message: %s", error.getMessage());
                                        }

                                        @Override
                                        public void mapboxTileCountLimitExceeded(long limit) {
                                            // Notify if offline region exceeds maximum tile count
                                            Timber.e("Mapbox tile count limit exceeded: %s", limit);
                                        }
                                    });
                                }

                                @Override
                                public void onError(String error) {
                                    Timber.e("Error: %s", error);
                                }
                            });


                }

            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
       /* if (offlineManager != null) {
            offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
                @Override
                public void onList(OfflineRegion[] offlineRegions) {
                    if (offlineRegions.length > 0) {
                        // delete the last item in the offlineRegions list which will be yosemite offline map
                        offlineRegions[(offlineRegions.length - 1)].delete(new OfflineRegion.OfflineRegionDeleteCallback() {
                            @Override
                            public void onDelete() {
                                Toast.makeText(
                                        OfflineMap.this,
                                        getString(R.string.basic_offline_deleted_toast),
                                        Toast.LENGTH_LONG
                                ).show();
                            }

                            @Override
                            public void onError(String error) {
                                Timber.e("On delete error: %s", error);
                            }
                        });
                    }
                }

                @Override
                public void onError(String error) {
                    Timber.e("onListError: %s", error);
                }
            });
        }*/
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    private String obtainOfflineDirectory() {
        File offline = Environment.getExternalStoragePublicDirectory("Offline");
        if (!offline.exists()) {
            Timber.d("Offline directory does not exist");
            offline.mkdirs();
        }
        return offline.getAbsolutePath();
    }

    private String obtainOfflineTileVersion() {
        return PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.offline_version_key), "");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    // Progress bar methods
    private void startProgress() {

        // Start and show the progress bar
        isEndNotified = false;
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setPercentage(final int percentage) {
        progressBar.setIndeterminate(false);
        progressBar.setProgress(percentage);
    }

    private void endProgress(final String message) {
        // Don't notify more than once
        if (isEndNotified) {
            return;
        }

        // Stop and hide the progress bar
        isEndNotified = true;
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.GONE);

        // Show a toast
        Toast.makeText(OfflineMap.this, message, Toast.LENGTH_LONG).show();
    }

    public void setTower(){

        // Add a marker in tower and move the camera
        mapboxMap.moveCamera(CameraUpdateFactory.newLatLng(tower));
        mapboxMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);

    }


    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        return false;
    }

    @Override
    public void onMilestoneEvent(RouteProgress routeProgress, String instruction, Milestone milestone) {
        if (milestone instanceof VoiceInstructionMilestone) {
            Snackbar.make(contentLayout, instruction, Snackbar.LENGTH_SHORT).show();
        }
        instructionView.updateBannerInstructionsWith(milestone);
        Timber.d("onMilestoneEvent - Current Instruction: %s", instruction);
    }

    @Override
    public void onRunning(boolean running) {
        this.running = running;
        if (running) {
            navigation.addOffRouteListener(this);
            navigation.addProgressChangeListener(this);
        }

    }

    @Override
    public void userOffRoute(Location location) {
        origin = Point.fromLngLat(lastLocation.getLongitude(), lastLocation.getLatitude());
        getRoute(origin, destination);
        Snackbar.make(contentLayout, "User Off Route", Snackbar.LENGTH_SHORT).show();
        mapboxMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())));


    }

    @Override
    public void onProgressChange(Location location, RouteProgress routeProgress) {
        boolean isInTunnel = routeProgress.inTunnel();
        lastLocation = location;
        if (!wasInTunnel && isInTunnel) {
            wasInTunnel = true;
            Snackbar.make(contentLayout, "Enter tunnel!", Snackbar.LENGTH_SHORT).show();
        }
        if (wasInTunnel && !isInTunnel) {
            wasInTunnel = false;
            Snackbar.make(contentLayout, "Exit tunnel!", Snackbar.LENGTH_SHORT).show();
        }
        if (tracking) {
            mapboxMap.getLocationComponent().forceLocationUpdate(location);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .zoom(15)
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .bearing(location.getBearing())
                    .build();
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000);
        }
        instructionView.updateDistanceWith(routeProgress);
    }

    @Override
    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
        Timber.d(call.request().url().toString());
        if (response.body() != null) {
            if (!response.body().routes().isEmpty()) {
                DirectionsRoute route = response.body().routes().get(0);
                drawRoute(route);
                resetLocationEngine(route);
                navigation.startNavigation(route);
                mapboxMap.addOnMapClickListener(this);
                tracking = true;
            }
        }
    }

    @Override
    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
        Timber.e(t);
    }
    void updateLocation(Location location) {
        if (!tracking) {
            mapboxMap.getLocationComponent().forceLocationUpdate(location);
        }
    }


    private void getRoute(Point origin, Point destination) {


        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
// You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            Toast.makeText(OfflineMap.this, "Tidak Ditemukan Rute ke Tower", Toast.LENGTH_LONG).show();
                            fbtnNavigate.setEnabled(false);

                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            Toast.makeText(OfflineMap.this, "Tidak Ditemukan Rute ke Tower", Toast.LENGTH_LONG).show();
                            fbtnNavigate.setEnabled(false);

                            return;
                        }

                        currentRoute = response.body().routes().get(0);

// Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }

    private void drawRoute(DirectionsRoute route) {
        List<LatLng> points = new ArrayList<>();
        List<Point> coords =  LineString.fromPolyline(route.geometry(), Constants.PRECISION_6).coordinates();

        for (Point point : coords) {
            points.add(new LatLng(point.latitude(), point.longitude()));
        }

        if (!points.isEmpty()) {
            if (polyline != null) {
                mapboxMap.removePolyline(polyline);
            }
            polyline = mapboxMap.addPolyline(new PolylineOptions()
                    .addAll(points)
                    .color(Color.parseColor(getString(R.string.blue)))
                    .width(5));
        }
    }

    private void resetLocationEngine(Point point) {
        mockLocationEngine.moveTo(point);
        navigation.setLocationEngine(mockLocationEngine);
    }

    private void resetLocationEngine(DirectionsRoute directionsRoute) {
        mockLocationEngine.assign(directionsRoute);
        navigation.setLocationEngine(mockLocationEngine);
    }

    private void shutdownLocationEngine() {
        if (mockLocationEngine != null) {
            mockLocationEngine.removeLocationUpdates(callback);
        }
    }

    private void shutdownNavigation() {
        navigation.removeNavigationEventListener(this);
        navigation.removeProgressChangeListener(this);
        navigation.onDestroy();
    }


    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private static class RerouteActivityLocationCallback implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<OfflineMap> activityWeakReference;

        RerouteActivityLocationCallback(OfflineMap activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(LocationEngineResult result) {
            OfflineMap activity = activityWeakReference.get();
            if (activity != null) {
                Location location = result.getLastLocation();
                if (location == null) {
                    return;
                }
                activity.updateLocation(location);
            }
        }

        @Override
        public void onFailure(@NonNull Exception exception) {
            Timber.e(exception);
        }
    }
    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Create and customize the LocationComponent's options
            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(this)
                    .elevation(5)
                    .accuracyAlpha(.6f)
                    .accuracyColor(Color.CYAN)
                    .build();

            // Get an instance of the component
            locationComponent = mapboxMap.getLocationComponent();

            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .locationComponentOptions(customLocationComponentOptions)
                            .build();

            // Activate with options
            locationComponent.activateLocationComponent(locationComponentActivationOptions);

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

            // Add the location icon click listener
            locationComponent.addOnLocationClickListener(this);

            // Add the camera tracking listener. Fires if the map camera is manually moved.
            locationComponent.addOnCameraTrackingChangedListener(this);

            findViewById(R.id.btnMyLocation).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isInTrackingMode) {
                        isInTrackingMode = true;
                        locationComponent.setCameraMode(CameraMode.TRACKING);
                        locationComponent.zoomWhileTracking(6f);
                        Toast.makeText(OfflineMap.this, getString(R.string.tracking_enabled),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(OfflineMap.this, getString(R.string.tracking_already_enabled),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings( {"MissingPermission"})
    @Override
    public void onLocationComponentClick() {
        if (locationComponent.getLastKnownLocation() != null) {
            Toast.makeText(this, String.format(getString(R.string.current_location),
                    locationComponent.getLastKnownLocation().getLatitude(),
                    locationComponent.getLastKnownLocation().getLongitude()), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCameraTrackingDismissed() {
        isInTrackingMode = false;
    }

    @Override
    public void onCameraTrackingChanged(int currentMode) {
        // Empty on purpose
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }
}

