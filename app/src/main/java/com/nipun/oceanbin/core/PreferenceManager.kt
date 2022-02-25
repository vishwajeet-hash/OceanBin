package com.nipun.oceanbin.core

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.gson.Gson
import com.nipun.oceanbin.feature_oceanbin.feature_home.data.remote.dto.WeatherDto
import com.nipun.oceanbin.feature_oceanbin.feature_home.local.models.HourlyDataModel
import com.nipun.oceanbin.feature_oceanbin.feature_home.local.models.WeatherModel
import java.util.*

/*
 * Class for managing shared preferences
 */
class PreferenceManager(private val context: Context) {
    companion object {
        const val IS_INSTALLED = "is_installed"
        const val HOURLY_KEY = "hourly_key"
    }

    private val sharedPreference = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)

    fun saveBoolean(key: String = IS_INSTALLED, value: Boolean) {
        with(sharedPreference.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    fun getBoolean(key: String = IS_INSTALLED, default: Boolean = true): Boolean {
        return sharedPreference.getBoolean(key, default)
    }

    fun saveInteger(key: String, value: Int) {
        with(sharedPreference.edit()) {
            putInt(key, value)
            apply()
        }
    }

    fun getInt(key: String, default: Int = 0): Int {
        return sharedPreference.getInt(
            key, default
        )
    }

    fun saveWeather(key: String = Constant.WEATHER_KEY, value: WeatherDto) {
        with(sharedPreference.edit()) {
            val gson = Gson()
            putString(key, gson.toJson(value))
            commit()
        }
    }

    fun getWeather(key: String = Constant.WEATHER_KEY): WeatherDto? {
        val gson = Gson()
        val str = sharedPreference.getString(key, "")
        try {
            if (str.isNullOrEmpty()) return null
            return gson.fromJson(str, WeatherDto::class.java)
        } catch (e: Exception) {
            return null
        }
    }

    fun saveHourlyUpdate(key: String = HOURLY_KEY,value: HourlyDataModel){
        with(sharedPreference.edit()){
            val gson = Gson()
            putString(key, gson.toJson(value))
            commit()
        }
    }

    fun getHourlyUpdate(key: String = HOURLY_KEY) : HourlyDataModel {
        val gson = Gson()
        val str = sharedPreference.getString(key, "")
        try {
            if (str.isNullOrEmpty()) return HourlyDataModel(
                emptyList()
            )
            return gson.fromJson(str, HourlyDataModel::class.java)
        } catch (e: Exception) {
            return HourlyDataModel(emptyList())
        }
    }

    fun getAddress(longitude: Double, latitude: Double): String {
        val geoCoder = Geocoder(context, Locale.getDefault())
        val addressList = geoCoder.getFromLocation(latitude, longitude, 1)
        return if (addressList.isNullOrEmpty()) ""
        else {
            val address: Address = addressList[0]
            var res = ""
            for (i in 0 until address.maxAddressLineIndex) {
                res += address.getAddressLine(i) + "\n"
            }
            res += address.locality.capitalize(Locale.getDefault()) + ", " + address.subAdminArea.capitalize(
                Locale.getDefault()
            )
            res
        }
    }
}

