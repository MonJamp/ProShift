package com.proshiftteam.proshift.Interfaces

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilderObject {
    val retrofitBuilder = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("http://proshiftonline.com/api/")
        .build()

    val connectJsonApiCalls = retrofitBuilder.create(ApiCalls::class.java)
}