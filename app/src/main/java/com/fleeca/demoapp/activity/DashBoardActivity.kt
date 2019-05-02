package com.fleeca.demoapp.activity

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import com.fleeca.demoapp.R
import com.fleeca.demoapp.util.LatLogAdapter
import com.fleeca.demoapp.util.MarkerDataDto
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnSuccessListener
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class DashBoardActivity : BaseActvity(), OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,com.google.android.gms.location.LocationListener,View.OnClickListener {


    private var mMap: GoogleMap? = null
    private lateinit var mGoogleApiClient: GoogleApiClient
    private var mLocationManager: LocationManager? = null
    lateinit var mLocation: Location
    private var mLocationRequest: LocationRequest? = null
    private val listener: LocationListener? = null
    private val UPDATE_INTERVAL = (2 * 2000).toLong()
    private val FASTEST_INTERVAL: Long = 4000
    lateinit var locationManager: LocationManager
    var markerData = ArrayList<MarkerDataDto>()
    lateinit var options:PolylineOptions
    lateinit var recylerView:RecyclerView
    lateinit var lineLaManager:LinearLayoutManager
     lateinit var latLogAdapter:LatLogAdapter
    lateinit var etDate1:EditText
    lateinit var etDate2:EditText
    var cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)
        uiBind()

    }

    private fun uiBind() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        recylerView=findViewById(R.id.recy_view)
        etDate1=findViewById(R.id.et_date)
        etDate2=findViewById(R.id.et_date1)
        etDate1.setOnClickListener(this)
        etDate2.setOnClickListener(this)
        lineLaManager= LinearLayoutManager(this)
        recylerView.layoutManager=lineLaManager
        mapFragment.getMapAsync(this)

        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()

        mLocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        options = PolylineOptions()
        options.color(Color.RED)
        options.width(5f)
        checkLocation()
        addMarkerData()
        setData()

    }

    private fun setData() {

        latLogAdapter=LatLogAdapter(this,markerData)
        recylerView.adapter=latLogAdapter
    }

    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0
        mMap!!.getUiSettings().isZoomControlsEnabled = true

    }

    protected fun startLocationUpdates() {

        mLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL)
            .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private fun showAlert() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Enable Location")
            .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " + "use this app")
            .setPositiveButton("Location Settings", DialogInterface.OnClickListener { paramDialogInterface, paramInt ->
                val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(myIntent)
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { paramDialogInterface, paramInt -> })
        dialog.show()
    }

    private fun isLocationEnabled(): Boolean {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkLocation(): Boolean {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    override fun onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    override fun onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    override fun onConnectionSuspended(p0: Int) {

        Log.d("TAG", "Connection Suspended");
        mGoogleApiClient.connect();
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.d("TAG", "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    override fun onLocationChanged(location: Location) {

        val dhaka = LatLng(location.latitude, location.longitude)
        mMap?.let {
            it.addMarker(MarkerOptions().position(dhaka).title("Current Location"))
            it.moveCamera(CameraUpdateFactory.newLatLng(dhaka))
            it.setMaxZoomPreference(30F)
            it.mapType = GoogleMap.MAP_TYPE_TERRAIN
            val cameraPosition = CameraPosition.Builder().target(LatLng(location.latitude, location.longitude)).zoom(13f).build()
            mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }


    }

    override fun onConnected(p0: Bundle?) {

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return;
        }

        startLocationUpdates();
        var fusedLocationProviderClient:
                FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation()
            .addOnSuccessListener(this, OnSuccessListener<Location> { location ->
                if (location != null) {
                    addMarkerData()
                    mLocation = location;

                    val currentLocation = LatLng(mLocation.latitude, mLocation.longitude)
                    mMap?.let {
                        it.mapType = GoogleMap.MAP_TYPE_TERRAIN
                        it.addMarker(MarkerOptions().position(currentLocation).title("Current Location"))
                        it.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))

               for ( point in markerData) {
                   var la=LatLng(point.lat,point.lon)
                   it.addMarker(MarkerOptions().position(la).title(point.title))

                   it.moveCamera(CameraUpdateFactory.newLatLng(la))

               }
                        val cameraPosition = CameraPosition.Builder().target(LatLng(mLocation.latitude, mLocation.longitude)).zoom(13f).build()
                        mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                        var one=LatLng(mLocation.latitude,mLocation.longitude)
                        var two=LatLng(26.85447, 75.81202)
                        Log.d("urll",getURL(one,two))
                        getURL(one,two)

                    }

                }
            })
    }



    fun addMarkerData() {

        var markerDataDto1 = MarkerDataDto(26.85447, 75.81202, R.drawable.marker_flag)
        markerData.add(markerDataDto1)
        markerDataDto1 = MarkerDataDto(26.9055, 75.8159, R.drawable.marker_flag)
        markerData.add(markerDataDto1)
        markerDataDto1 = MarkerDataDto(26.9239, 75.8267, R.drawable.marker_flag)
        markerData.add(markerDataDto1)
        markerDataDto1 = MarkerDataDto(26.85694, 75.8321, R.drawable.marker_flag)
        markerData.add(markerDataDto1)
        markerDataDto1 = MarkerDataDto(26.85994, 75.8521, R.drawable.marker_flag)
        markerData.add(markerDataDto1)

    }
    private fun getURL(from : LatLng, to : LatLng) : String {
        val origin = "origin=" + from.latitude + "," + from.longitude
        val dest = "destination=" + to.latitude + "," + to.longitude
        val sensor = "sensor=false"
        val params = "$origin&$dest&$sensor"
        return "https://maps.googleapis.com/maps/api/directions/json?$params"
    }
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.et_date ->
            {
                setDateTime()
                updateDateInView()

            }
            R.id.et_date1 ->
            {
                setDateTime()
                updateDateInView1()
            }

        }

    }
    fun  setDateTime()
    {


        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            }
        }
        DatePickerDialog(this, dateSetListener,
            // set DatePickerDialog to point to today's date when it loads up
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)).show()

    }
    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        var date=sdf.format(cal.getTime())
       etDate1.setText(date)
    }
    private fun updateDateInView1() {
        val myFormat = "MM/dd/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        var date=sdf.format(cal.getTime())
        etDate2.setText(date)
    }

}
