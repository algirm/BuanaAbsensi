package com.papb.buanaabsensi.ui.presensi

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.firebase.Timestamp
import com.papb.buanaabsensi.R
import com.papb.buanaabsensi.data.model.PresensiState
import com.papb.buanaabsensi.databinding.FragmentPresensiBinding
import com.papb.buanaabsensi.ui.base.BaseFragment
import com.papb.buanaabsensi.util.Constants
import com.papb.buanaabsensi.util.Constants.Companion.ALPHA
import com.papb.buanaabsensi.util.Constants.Companion.BELUM_PRESENSI
import com.papb.buanaabsensi.util.Constants.Companion.MAP_ZOOM
import com.papb.buanaabsensi.util.Constants.Companion.REQUEST_CODE_LOCATION_PERMISSION
import com.papb.buanaabsensi.util.Constants.Companion.SELESAI
import com.papb.buanaabsensi.util.PermissionUtil.hasLocationPermissions
import com.papb.buanaabsensi.util.UiUtility
import com.papb.buanaabsensi.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class PresensiFragment : BaseFragment(R.layout.fragment_presensi),
    EasyPermissions.PermissionCallbacks {

    private val binding by viewBinding(FragmentPresensiBinding::bind)
    private val viewModel: PresensiViewModel by activityViewModels()
    private var map: GoogleMap? = null
    private var lastLocation: LatLng? = null
    private lateinit var lastViewState: PresensiViewState

    @Inject
    lateinit var geofencingClient: GeofencingClient

    @Inject
    lateinit var officeLocation: LatLng

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync {
            map = it
            markerLocationOffice()
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(officeLocation, MAP_ZOOM))
        }

        init()
        viewModel.getPresensiPegawai()
        lifecycleScope.launchWhenCreated {
            viewModel.state.collect { state ->
                lastViewState = state
                Timber.d("last state:$lastViewState\ncurrent State: $state")
                bindUi(state)
                if (lastViewState.isSuccess == true) {
                    viewModel.updateState(lastViewState.copy(isSuccess = null,
                        enableButton = false))
                    viewModel.getPresensiPegawai()
                    snackLong(getString(R.string.berhasil_presensi))
                }
            }
        }
        lifecycleScope.launchWhenCreated { viewModel.errorEvent.collect { toastShort(it) } }
    }

    @SuppressLint("SetTextI18n")
    private fun bindUi(viewState: PresensiViewState) {
        binding.btPresensi.isEnabled = viewState.enableButton
        when {
            viewState.isLoadingText -> binding.textProgressbar.show()
            !viewState.isLoadingText -> binding.textProgressbar.hide()
            viewState.isLoadingPresensi == true -> binding.progressBar.show()
            viewState.isLoadingPresensi != true -> binding.progressBar.hide()
            viewState.presensiState == null || viewState.presensiState is PresensiState.NoPresensi -> {
                binding.container1.visibility = View.INVISIBLE
            }
        }

        viewState.presensiState?.let { presensiState ->
            if (presensiState is PresensiState.Available) {
                presensiState.daftarPresensi.tanggal?.let { timestamp ->
                    val tanggal =
                        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(timestamp.toDate())
                    binding.textTanggal.text = "Tanggal : $tanggal"
                }
                when (presensiState.presensi.statusPresensi) {
                    BELUM_PRESENSI -> binding.textStatus.text = "Status : Belum Melakukan Presensi"
                    ALPHA -> binding.textStatus.text = "Status : ALPHA"
                    SELESAI -> binding.textStatus.text = "Status : Sudah Melakukan Presensi"
                }
                binding.textNoPresensi.visibility = View.INVISIBLE
                binding.container1.visibility = View.VISIBLE
                if (presensiState.daftarPresensi.isActive!!) {
                    binding.textPresensiTutup.visibility = View.INVISIBLE
                } else {
                    binding.textPresensiTutup.visibility = View.VISIBLE
                }
            } else { // No Presensi
                binding.container1.visibility = View.INVISIBLE
                binding.textNoPresensi.visibility = View.VISIBLE
                binding.textPresensiTutup.visibility = View.INVISIBLE
            }

        }
    }

    private fun init() {
        binding.btPresensi.setOnClickListener {
            if (it.isEnabled) {
                if (lastViewState.presensiState is PresensiState.Available) {
                    val presensiState = lastViewState.presensiState as PresensiState.Available
                    viewModel.presensi(
                        presensiState.presensi.copy(
                            tanggalPresensi = Timestamp.now(),
                            statusPresensi = SELESAI
                        ),
                        presensiState.idPresensi
                    )
                }
            } else {
                if (hasLocationPermissions(requireContext())) {
                    toastShort(getString(R.string.anda_tidak_dapat_presensi_bila))
                } else {
                    requestPermissions()
                }
            }
        }
        getLocation()
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions()
        } else {
            toastShort("Getting Location..")
            val request = LocationRequest.create().apply {
                interval = Constants.LOCATION_UPDATE_INTERVAL
                fastestInterval = Constants.FASTEST_LOCATION_UPDATE_INTERVAL
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            fusedLocationProviderClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            lastLocation = LatLng(
                result.lastLocation.latitude,
                result.lastLocation.longitude
            )
            lastLocation?.let { markerLocation(it) }
            map?.let { moveCameraToUser() }
            val dist = FloatArray(1)
            Location.distanceBetween(
                result.lastLocation.latitude,
                result.lastLocation.longitude,
                officeLocation.latitude,
                officeLocation.longitude,
                dist
            )
            viewModel.updateState(lastViewState.copy(atOffice = dist[0] <= 100))
        }
    }

    private var locationMarker: Marker? = null

    private fun markerLocationOffice() {
        Timber.i("markerLocationOffice")
        markerForGeofence(officeLocation)
    }

    private fun markerLocation(latLng: LatLng) {
        Timber.i("markerLocation($latLng)")
        val markerOptions = MarkerOptions()
            .position(latLng)
            .title("You")

        if (map != null) {
            // Remove the anterior marker
            if (locationMarker != null) locationMarker!!.remove()
            locationMarker = map!!.addMarker(markerOptions)
        }
    }

    private var geoFenceMarker: Marker? = null

    // Create a marker for the geofence creation
    private fun markerForGeofence(latLng: LatLng) {
        Timber.i("markerForGeofence($latLng)")
        // Define marker options
        val markerOptions = MarkerOptions()
            .position(latLng)
            .icon(
                UiUtility.bitmapFromVector(
                    requireContext(),
                    R.drawable.ic_baseline_location_city_24,
                    Color.BLUE
                )
            )
            .title("Office")
        if (map != null) {
            // Remove last geoFenceMarker
            if (geoFenceMarker != null) geoFenceMarker!!.remove()
            geoFenceMarker = map!!.addMarker(markerOptions)
            drawGeofence()
        }
    }

    private var geoFenceLimits: Circle? = null
    private fun drawGeofence() {
        Timber.d("drawGeofence()")
        if (geoFenceLimits != null) geoFenceLimits!!.remove()
        val circleOptions = CircleOptions()
            .center(geoFenceMarker!!.position)
            .strokeColor(Color.argb(50, 70, 70, 70))
            .fillColor(Color.argb(100, 150, 150, 150))
            .radius(100.0)
        geoFenceLimits = map?.addCircle(circleOptions)
    }

    private fun moveCameraToUser() {
        val bounds = LatLngBounds.Builder()
            .include(locationMarker!!.position)
            .include(geoFenceMarker!!.position)
            .build()
        map?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        locationMarker?.showInfoWindow()

    }

    private fun requestPermissions() {
        if (hasLocationPermissions(requireContext())) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app.",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app.",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        getLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onResume() {
        super.onResume()
        requestPermissions()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

}