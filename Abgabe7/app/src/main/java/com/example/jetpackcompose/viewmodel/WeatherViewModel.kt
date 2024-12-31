package com.example.jetpackcompose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcompose.api.WeatherApiService
import com.example.jetpackcompose.data.ForecastItem
import com.example.jetpackcompose.data.WeatherData
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WeatherViewModel : ViewModel() {

    private val _currentWeather = MutableStateFlow<WeatherData?>(null)
    val currentWeather: StateFlow<WeatherData?> = _currentWeather

    private val _forecast = MutableStateFlow<List<ForecastItem>>(emptyList())
    val forecast: StateFlow<List<ForecastItem>> = _forecast

    private val _iconUrl = MutableStateFlow<String?>(null)
    val iconUrl: StateFlow<String?> get() = _iconUrl

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    fun fetchWeatherData(city: String, apiKey: String) {
        viewModelScope.launch {
            try {
                val weatherResponse = WeatherApiService.fetchWeather(city, apiKey)
                if (weatherResponse != null) {
                    _currentWeather.value = weatherResponse
                    fetchWeatherIcon(weatherResponse.weather.firstOrNull()?.icon.orEmpty())
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "Failed to fetch weather. Please check your API key or city name."
                }
            } catch (e: Exception) {
                _errorMessage.value = "An error occurred: ${e.localizedMessage}"
            }
        }
    }

    /**
     * Fetches the weather forecast data for a given city and updates the LiveData properties.
     * This function launches a coroutine in the ViewModel scope to fetch the forecast data using the WeatherApiService.
     * It handles errors and updates the appropriate LiveData properties to reflect the result.
     * @param city The name of the city for which the forecast is to be fetched.
     * @param apiKey The API key required to authenticate the request with the weather service.
     */
    fun fetchForecastData(city: String, apiKey: String) {

        ////////////////////////////////////

        //Todo

        // Launch a coroutine in the ViewModel scope for lifecycle-aware execution
        viewModelScope.launch {
            try {
                // Attempt to fetch the forecast data using WeatherApiService
                val forecastResponse = WeatherApiService.fetchForecast(city, apiKey)

                if (forecastResponse != null) {
                    // Update LiveData with the list of forecast items on successful response
                    _forecast.value = forecastResponse.list
                    _errorMessage.value = null
                } else {
                    // Set an error message if the forecast data could not be fetched
                    _errorMessage.value =
                        "Failed to fetch forecast. Please check your API key or city name."
                }
            } catch (e: Exception) {
                // Handle any exceptions that occur during the data fetching process
                _errorMessage.value = "An error occurred: ${e.localizedMessage}"
            }
        }


        ////////////////////////////////////

    }

    private fun fetchWeatherIcon(iconId: String) {
        if (iconId.isNotEmpty()) {
            _iconUrl.value = "https://openweathermap.org/img/wn/$iconId@2x.png"
        }
    }
}
