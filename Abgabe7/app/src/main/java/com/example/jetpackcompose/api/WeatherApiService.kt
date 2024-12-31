package com.example.jetpackcompose.api

import android.util.Log
import com.example.jetpackcompose.data.ForecastData
import com.example.jetpackcompose.data.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object WeatherApiService {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    private val client = OkHttpClient.Builder().build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(WeatherApi::class.java)

    interface WeatherApi {
        @GET("weather")
        suspend fun fetchWeather(
            @Query("q") city: String,
            @Query("appid") apiKey: String,
            @Query("units") units: String = "metric"
        ): retrofit2.Response<WeatherData>

        @GET("forecast")
        suspend fun fetchForecast(
            @Query("q") city: String,
            @Query("appid") apiKey: String,
            @Query("units") units: String = "metric"
        ): retrofit2.Response<ForecastData>
    }

    suspend fun fetchWeather(city: String, apiKey: String): WeatherData? {
        return try {
            withContext(Dispatchers.Default) {
                val response = api.fetchWeather(city, apiKey)
                if (response.isSuccessful) {
                    response.body()
                } else {
                    Log.e("WeatherApiService", "Failed to fetch data: ${response.code()}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("WeatherApiService", "Error fetching data: ${e.message}")
            null
        }
    }


    ////////////////////////////////////

    // TODO: Methode fetchForecast implementieren, um die Wettervorhersage abzurufen.

    /**
     * Fetches the weather forecast data for a given city from the API.
     * This function performs a network call using Retrofit to fetch forecast data asynchronously.
     * It uses `Dispatchers.IO` to ensure that the operation is executed on a thread optimized for
     * I/O-intensive operations, keeping the main thread responsive.
     * @param city The name of the city for which the forecast is to be fetched.
     * @param apiKey The API key required to authenticate the request with the weather service.
     * @return A [ForecastData] object containing the weather forecast information if the request is successful;
     *         otherwise, returns `null` in case of failure or an exception.
     */
    suspend fun fetchForecast(city: String, apiKey: String): ForecastData? {
        return try {
            // Switch to a background thread optimized for I/O operations
            withContext(Dispatchers.IO) {
                // Make the API call to fetch the forecast data
                val response = api.fetchForecast(city, apiKey)

                // Check if the response was successful
                if (response.isSuccessful) {
                    // Return the body of the response
                    response.body()
                } else {
                    // Log an error message if the response is not successful
                    Log.e("WeatherApiService", "Failed to fetch forecast data: ${response.code()}")
                    null
                }
            }
        } catch (e: Exception) {
            // Catch and log any exceptions that occur during the API call
            Log.e("WeatherApiService", "Error fetching forecast data: ${e.message}")
            // Return null in case of an exception
            null
        }
    }

    ////////////////////////////////////
}
