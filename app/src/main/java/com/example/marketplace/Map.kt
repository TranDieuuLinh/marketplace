package com.example.marketplace

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.NoOpUpdate
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.example.marketplace.ui.theme.marketplace_light_primary
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.viewport


class Map {
    @Composable
    fun CreateMapBoxView(
        modifier: Modifier,
        point: Point?,
    ) {
        var pointAnnotationManager: PointAnnotationManager? by remember {
            mutableStateOf(null)
        }
        AndroidView(
            factory = {
                MapView(it).also { mapView ->
                    val annotationApi = mapView.annotations
                    pointAnnotationManager =
                        annotationApi.createPointAnnotationManager()
                }
            },
            update = { mapView ->
                if (point != null) {
                    pointAnnotationManager?.let {
                        //in this scope {} it refers to pointAnnotationManager
                        it.deleteAll()
                        val pointAnnotationOptions = PointAnnotationOptions()
                            .withPoint(point)
                        it.create(pointAnnotationOptions)
                        mapView.mapboxMap.setCamera(
                            CameraOptions.Builder()
                                .center(point)
                                .pitch(0.0)
                                .zoom(16.0)
                                .bearing(0.0)
                                .build()
                        )
                        with(mapView) {
                            location.locationPuck = createDefault2DPuck(withBearing = true)
                            location.enabled = true
                            location.puckBearing = PuckBearing.COURSE
                            viewport.transitionTo(
                                targetState = viewport.makeFollowPuckViewportState(),
                                transition = viewport.makeImmediateViewportTransition()
                            )
                        }
                    }
                }
                NoOpUpdate
            },
            modifier = modifier
        )}

@Composable
fun MapScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        RequestLocationPermission(
            onPermissionGranted = {
                Box(modifier = Modifier.fillMaxSize()) {
                    CreateMapBoxView(
                        modifier = Modifier.matchParentSize(),
                        point = Point.fromLngLat(145.1347, -37.9142)
                    )


                    Button(
                        onClick = {  navController.popBackStack()},
                        colors = ButtonDefaults.buttonColors(containerColor = marketplace_light_primary),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 32.dp, start = 16.dp)
                    ) {
                        Text("Confirm")
                    }
                }
            },
            onPermissionDenied = { },
            onPermissionsRevoked = { }
        )
    }
}


    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun RequestLocationPermission(
        onPermissionGranted: @Composable () -> Unit,
        onPermissionDenied: @Composable () -> Unit,
        onPermissionsRevoked: @Composable () -> Unit
    ) {
        val permissionState = rememberMultiplePermissionsState(
            listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        )
        val permissionStatus = remember { mutableStateOf("Pending") }

        LaunchedEffect(key1 = permissionState) {
            if (permissionState.allPermissionsGranted) {
                permissionStatus.value = "Granted"
            } else if (permissionState.permissions.any { !it.status.isGranted }) {
                permissionState.launchMultiplePermissionRequest()
            } else if (permissionState.revokedPermissions.isNotEmpty()) {
                permissionStatus.value = "Revoked"
            } else {
                permissionStatus.value = "Denied"
            }
        }

        when (permissionStatus.value) {
            "Granted" -> onPermissionGranted()
            "Denied" -> onPermissionDenied()
            "Revoked" -> onPermissionsRevoked()
        }
    }
}


