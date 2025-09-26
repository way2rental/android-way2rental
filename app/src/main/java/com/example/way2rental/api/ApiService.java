package com.example.way2rental.api;

import com.example.way2rental.model.OtpRequest;
import com.example.way2rental.model.*; // Includes APIResponse, UserProfile, StatusResponse, FileUploadRequest, EditProfileRequest etc.

import okhttp3.MultipartBody;
// import okhttp3.RequestBody; // No longer needed for requestPart if using an object
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    @POST("public/otp/send")
    Call<APIResponse<OtpResponse>> sendOtp(@Body OtpRequest request);

    @POST("public/customers/loginSignup")
    Call<APIResponse<LoginResponse>> loginWithOtp(@Body LoginRequest request);

    @POST("public/customers/register")
    Call<APIResponse<Object>> registerUser(@Body RegistrationRequest registrationRequest);

    @POST("auth/refresh") // <<<====== VERIFY THIS PATH with your backend team
    Call<APIResponse<LoginResponse>> refreshToken(@Body RefreshTokenRequest request);

    @GET("public/customers/get/{identifier}")
    Call<APIResponse<UserProfile>> getUserProfile(@Path("identifier") String identifier);

    @Multipart
    @POST("fileManager/PUBLIC/upload")
    Call<APIResponse<StatusResponse>> uploadProfilePicture(
            @Part("requestPart") FileUploadRequest requestPartObject,
            @Part MultipartBody.Part filePart
    );

    @FormUrlEncoded
    @POST("public/customers/change/profile-picture")
    Call<APIResponse<StatusResponse>> changeProfilePictureOnBackend(
            @Field("identifier") String identifier,
            @Field("profilePicture") String profilePictureUrl
    );

    @POST("public/customers/edit/profile/{identifier}")
    Call<APIResponse<StatusResponse>> editUserProfile(
            @Path("identifier") String identifier,
            @Body EditProfileRequest request
    );
}
