package com.example.systemdesignpracticeapps.sixt

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


// Data

interface ApiService {
    @GET("recommendation")
}








// Domain

data class Vehicle(
    val id: String,
    val name: String,
    val price: Double
)

data class VehiclesWithRecommendation(
    val recommend: Vehicle,
    val vehicles: List<Vehicle>
)








// Presentation












// DI


