package me.linx.vchat.app.net

import io.reactivex.Observable
import me.linx.vchat.app.common.net.JsonResult
import me.linx.vchat.app.db.entity.User
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface NetApi {

    @FormUrlEncoded
    @POST("app/register")
    fun signUp(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("deviceId") deviceId: String
    ): Observable<JsonResult<User>>

    @FormUrlEncoded
    @POST("app/login")
    fun signIn(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("deviceId") deviceId: String
    ): Observable<JsonResult<User>>

    /*

    @GET
    Call<String> get(@Url String url, @QueryMap WeakHashMap<String, Object> params);

    @FormUrlEncoded
    @POST
    Call<String> post(@Url String url, @FieldMap WeakHashMap<String, Object> params);

    @POST
    Call<String> postRaw(@Url String url, @Body RequestBody body);

    @FormUrlEncoded
    @PUT
    Call<String> put(@Url String url, @FieldMap WeakHashMap<String, Object> params);

    @PUT
    Call<String> putRaw(@Url String url, @Body RequestBody body);

    @DELETE
    Call<String> delete(@Url String url, @QueryMap WeakHashMap<String, Object> params);

    @Streaming
    @GET
    Call<ResponseBody> download(@Url String url, @QueryMap WeakHashMap<String, Object> params);

    @Multipart
    @POST
    Call<String> upload(@Url String url, @Part MultipartBody.Part file);

     */
}