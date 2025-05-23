package deakin.gopher.guardian.services.api

import deakin.gopher.guardian.model.BaseModel
import deakin.gopher.guardian.model.CaretakerProfile
import deakin.gopher.guardian.model.register.AuthResponse
import deakin.gopher.guardian.model.register.RegisterRequest
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // ----------- AUTH APIs -----------

    @POST("auth/register")
    fun register(
        @Body request: RegisterRequest
    ): Call<AuthResponse>

    @FormUrlEncoded
    @POST("auth/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<AuthResponse>

    @FormUrlEncoded
    @POST("auth/send-pin")
    fun sendPin(
        @Field("email") email: String
    ): Call<BaseModel>

    @FormUrlEncoded
    @POST("auth/verify-pin")
    fun verifyPin(
        @Field("email") email: String,
        @Field("otp") pin: String
    ): Call<BaseModel>

    @FormUrlEncoded
    @POST("auth/reset-password-request")
    fun requestPasswordReset(
        @Field("email") email: String
    ): Call<BaseModel>

    // ----------- CARETAKER PROFILE APIs -----------

    // GET caretaker profile by caretakerId only (no email param)
    @GET("caretaker")
    fun getCaretakerProfile(
        @Query("caretakerId") caretakerId: String
    ): Call<CaretakerProfile>

    // PUT update caretaker profile by ID
    @PUT("caretaker/{id}")
    fun updateCaretakerProfile(
        @Path("id") id: String,
        @Body profile: CaretakerProfile
    ): Call<BaseModel>
}
