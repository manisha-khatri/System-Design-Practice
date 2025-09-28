package com.example.systemdesignpracticeapps

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import retrofit2.Response
import retrofit2.http.GET
import javax.inject.Inject
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
Que:
User will select the pickup date, return date, and pickup location, return location. And let's say there is a button at the
bottom and something like, you know, see all vehicles or see vehicle, something of that sort. Once the user clicks on that button,
they will navigate to the next screen. This is where we have to implement the requirements of the product site. So the product wants
to show a recommended vehicle at the top and all the other vehicles just below it. So, for example, you know, depending on the time and
location, maybe we recommend a different kind of a vehicle to the user. Hey, this is more suitable for you in this weather. Maybe it has
a winter tires or something of that sort. So, and it's like an upsell for the user. So the product wants to upsell some vehicle in the
user booking journey. And along with, we'll also show all the other vehicles available for that pickup branch, doing those dates. So our
task is to show the recommended vehicle at the top with some banners saying, hey, this is, you know, this is more suitable or something
of that sort of a text. And just below it, all the other vehicles. To achieve this, the backend has provided us two endpoints. One is
recommendation. Other one is vehicles. So the recommendation will return only one vehicle if it exists. Otherwise, it will not return
anything. The vehicle endpoint will always return the list of vehicles. So now our job is to show the recommendation at the top, recommended
vehicle at the top and the vehicles just below on this. You might want to know why the recommendation may or may not available. For example,
let's say if we both are traveling on the same date and on the same location, maybe you go for the recommended vehicle. And there is no other
recommended vehicle available for me when I enter the same flow. So that's why the recommendation API will return one if the vehicle exists
or the recommended vehicle exists. Otherwise, it will not return anything. Hope this is clear. Okay. So for this, I need to use clean architecture
and... It's your wish. What I would like to say is, how do you get the data from your network model and pass it to your UI layer, right? You
can use any architecture, any pattern you want, MVV and MVP. It's up to you. Okay. Yeah. And we will be more concentrated on the... I mean,
it depends on like, are you going to... Which architecture are you proposing to follow? MVV and clean architecture. Perfect. And we will deep
dive more into the view model part then. So first, we will start with the network and then go to the view model. This is where we spend most
of our time.

pickup: date, location
return: date, location
btn -> see all vehicle

btn --> new page

Recommended Vehicle + Banners(text = this is more suitable) : (depending time + location) or upsell
other vehicles

2 endpoints:
-- /recommendation --> only 1 vehicle if exist else nothing
-- /vehicles --> will always return list of vehicles

chatgpt:
1. why used 1 use-case and not 2 use-cases? as we have 2 api calls to make, 2 reason 2 change
2. why used coroutineScope inside the getVehiclesWithRecommendation()
3. In getVehiclesWithRecommendation() returning Result, but Result class is not defined anywhere
4. In VehiclesViewModel used result.fold() why? how does it works? is there's any simpler way to do it?


changes
1. use supervisor scope removing coroutineScope
2. use try/catch block only in the recommendation call
 */


// ---------------- Data ----------------

interface ApiService {
    @GET("recommendation")
    suspend fun getRecommendation(
        /*  @Query("pickupDate") pickupDate: String,
          @Query("pickupLocation") pickupLocation: String,
          @Query("returnDate") returnDate: String,
          @Query("returnLocation") returnLocation: String,*/
    ): Response<RecommendationDto>

    @GET("vehicles")
    suspend fun getVehicles(
        /*  @Query("pickupDate") pickupDate: String,
          @Query("pickupLocation") pickupLocation: String,
          @Query("returnDate") returnDate: String,
          @Query("returnLocation") returnLocation: String,*/
    ): VehicleListDto
}

data class VehicleDto(
    val id: String,
    val name: String,
    val price: Double
)

data class RecommendationDto(
    val bannerText: String?,
    val vehicle: VehicleDto?
)

data class VehicleListDto(
    val vehicles: List<VehicleDto>
)

// mapper
fun VehicleDto.toDomain() = Vehicle(
    id = id,
    name = name,
    price = price
)

fun RecommendationDto.toDomain(): Vehicle? = this.vehicle?.toDomain()

class VehicleRepositoryImpl @Inject constructor(val api: ApiService): VehicleRepository {
    override suspend fun getVehiclesWithRecommendation(): Result<VehiclesWithRecommendation> {
        return runCatching {
            supervisorScope {
                val recommendCall = async{
                    try {
                        val response = api.getRecommendation()
                        if (response.isSuccessful) {
                            response.body()?.toDomain()
                        } else {
                            null
                        }
                    } catch(e: Exception) {
                        null
                    }
                }

                val vehiclesCall = async {
                    api.getVehicles().vehicles.map { it.toDomain() }
                }

                VehiclesWithRecommendation(
                    recommend = recommendCall.await(),
                    vehicles = vehiclesCall.await()
                )
            }
        }
    }
}


// ---------------- Domain ----------------

data class Vehicle(
    val id: String,
    val name: String,
    val price: Double
)

data class VehiclesWithRecommendation(
    val recommend: Vehicle?,
    val vehicles: List<Vehicle>
)

interface VehicleRepository {
    suspend fun getVehiclesWithRecommendation(): Result<VehiclesWithRecommendation>
}

class GetVehiclesWithRecommendationUseCase @Inject constructor(val repo: VehicleRepository) {
    suspend operator fun invoke(): Result<VehiclesWithRecommendation> = repo.getVehiclesWithRecommendation()
}


// ---------------- Presentation ----------------

data class VehicleUiState(
    val isLoading: Boolean = false,
    val recommended: Vehicle ?= null,
    val vehicles: List<Vehicle> = emptyList(),
    val isError: Boolean = false
)

@HiltViewModel
class VehicleViewModel @Inject constructor(
    val getVehiclesUseCase: GetVehiclesWithRecommendationUseCase
): ViewModel() {
    val _uiState = MutableStateFlow<VehicleUiState>(VehicleUiState())
    val uiState: StateFlow<VehicleUiState> = _uiState

    fun load() {
        _uiState.update {it.copy(isLoading = true, isError = false)}

        viewModelScope.launch {
            val result = getVehiclesUseCase()
            result.fold(
                onSuccess = { data ->
                    _uiState.update {it.copy(isLoading = false, isError = false, recommended = data.recommend, vehicles = data.vehicles )}
                },
                onFailure = {
                    _uiState.update {it.copy(isLoading = false, isError = true)}
                }
            )
        }
    }
}

@Composable
fun VehiclesScreen(
    viewModel:VehicleViewModel = hiltViewModel(),
    pickupDate: String,
    /*  pickupLocation: String,
     returnDate: String,
     returnLocation: String,*/
    onVehicleClicked: (Vehicle) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect( pickupDate
        //pickupLocation, returnDate, returnLocation
    ) {
        viewModel.load()
    }

    when {
        state.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        }
        state.isError -> {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(text = "Error occured")
            }
        }
        else -> {
            if(state.recommended != null) {
                state.recommended?.let { VehicleRow(it, {onVehicleClicked(it)} ) }
            }

            Spacer(Modifier.height(8.dp))

            if (state.vehicles.isNotEmpty()) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.vehicles) { vehicle ->
                        VehicleRow(vehicle) { onVehicleClicked(vehicle) }
                    }
                }
            }

        }
    }
}

@Composable
fun VehicleRow(vehicle: Vehicle, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            Text(text = vehicle.name)
            Text(text = "₹${vehicle.price}")
        }
    }
}

@AndroidEntryPoint
class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VehiclesScreen(
                pickupDate = "123",
                onVehicleClicked = {  }
            )
        }
    }
}

@HiltAndroidApp
class VehicleApp: Application()

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://example.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providesVehicleRepository(api: ApiService): VehicleRepository = VehicleRepositoryImpl(api)

    @Provides
    @Singleton
    fun providesApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
}
