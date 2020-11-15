package com.proshiftteam.proshift.Interfaces

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiLogger : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        val logName = "ApiLogger"
        if (message.startsWith("{") || message.startsWith("[")) {
            try {
                val prettyPrintJson = GsonBuilder().setPrettyPrinting()
                    .create().toJson(JsonParser().parse(message))
                Log.d(logName, prettyPrintJson)
            } catch (m: JsonSyntaxException) {
                Log.d(logName, message)
            }
        } else if(message.startsWith("<")) {
            // Catch html responses to not flood logcat
            Log.d(logName, "Detected HTML Response! Use Postman or Swagger to debug")
        } else{
            Log.d(logName, message)
            return
        }
    }
}

object RetrofitBuilderObject {
    val okHttpClient = OkHttpClient.Builder().addInterceptor(
        HttpLoggingInterceptor(ApiLogger()).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    ).build()

    val retrofitBuilder = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("http://proshiftonline.com/api/")
        .client(okHttpClient)
        .build()

    val connectJsonApiCalls = retrofitBuilder.create(ApiCalls::class.java)
}