package com.nipun.oceanbin.feature_oceanbin.feature_map.data

import android.content.Context
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.nipun.oceanbin.core.GpsProvider
import com.nipun.oceanbin.core.PreferenceManager
import com.nipun.oceanbin.core.Resource
import com.nipun.oceanbin.feature_oceanbin.feature_map.local.MapModel
import com.nipun.oceanbin.feature_oceanbin.feature_map.local.MapRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject

class MapRepositoryImpl(
    private val context: Context
) : MapRepository{
    private val geocoder = Geocoder(context)
    private val prefManager = PreferenceManager(context)
    override fun getLatLong(query: String): Flow<Resource<LatLng>> = flow{
        emit(Resource.Loading<LatLng>())
        try {
            val addressList = geocoder.getFromLocationName(query, 1)
            val address = addressList[0]
            emit(Resource.Success<LatLng>(
                data = LatLng(address.latitude, address.longitude)
            ))
        }catch (e : Exception){
            emit(Resource.Error<LatLng>(
                message = "Something went wrong"
            ))
        }
    }

    override fun getInitLocation(): Flow<Resource<MapModel>> = flow{
        emit(Resource.Loading<MapModel>(data = null))
        val gpsProvider = GpsProvider(context)
        if(gpsProvider.canGetLocation){
            gpsProvider.getLocation()
            val latLng = gpsProvider.latLang!!
            emit(Resource.Success<MapModel>(
                data = MapModel(
                    latLng,
                    prefManager.getAddress(
                        longitude = latLng.longitude,
                        latitude = latLng.latitude
                    )
                )
            ))
        }else{
            emit(Resource.Error<MapModel>(
                message = "Location service is disabled"
            ))
        }
    }
}