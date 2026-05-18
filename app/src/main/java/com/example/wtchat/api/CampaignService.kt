package com.example.wtchat.api

import com.example.wtchat.models.CampaignModel
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Body
import retrofit2.http.Path

interface CampaignService {
    @GET("api/campaigns")
    suspend fun getCampaigns(): List<CampaignModel>

    @GET("api/campaigns/all")
    suspend fun getAllCampaigns(): List<CampaignModel>

    @POST("api/campaigns")
    suspend fun createCampaign(@Body campaign: CampaignModel): CampaignModel

    @PUT("api/campaigns/{id}")
    suspend fun updateCampaign(@Path("id") id: String, @Body campaign: CampaignModel): CampaignModel

    @DELETE("api/campaigns/{id}")
    suspend fun deleteCampaign(@Path("id") id: String)
}

