package com.infotech.wishmaplus.Utils;

import com.infotech.wishmaplus.Api.Object.BalanceResult;
import com.infotech.wishmaplus.Api.Object.BankResult;
import com.infotech.wishmaplus.Api.Object.CityResult;
import com.infotech.wishmaplus.Api.Object.CommentResult;
import com.infotech.wishmaplus.Api.Object.ContentResult;
import com.infotech.wishmaplus.Api.Object.LevelCountResult;
import com.infotech.wishmaplus.Api.Object.PackageResult;
import com.infotech.wishmaplus.Api.Object.ReportReasonResult;
import com.infotech.wishmaplus.Api.Object.StateResult;
import com.infotech.wishmaplus.Api.Object.StoryResult;
import com.infotech.wishmaplus.Api.Request.BasicRequest;
import com.infotech.wishmaplus.Api.Request.CommentRequest;
import com.infotech.wishmaplus.Api.Request.LikeRequest;
import com.infotech.wishmaplus.Api.Request.ReportPostRequest;
import com.infotech.wishmaplus.Api.Request.SharePostRequest;
import com.infotech.wishmaplus.Api.Request.SignUpRequest;
import com.infotech.wishmaplus.Api.Request.UpdateUserRequest;
import com.infotech.wishmaplus.Api.Response.BasicListResponse;
import com.infotech.wishmaplus.Api.Response.BasicObjectResponse;
import com.infotech.wishmaplus.Api.Response.BasicResponse;
import com.infotech.wishmaplus.Api.Response.CategoryResponse;
import com.infotech.wishmaplus.Api.Response.CompanyDetailResponse;
import com.infotech.wishmaplus.Api.Response.ContentResponse;
import com.infotech.wishmaplus.Api.Response.FollowersResponse;
import com.infotech.wishmaplus.Api.Response.Income;
import com.infotech.wishmaplus.Api.Response.LikeResponse;
import com.infotech.wishmaplus.Api.Response.LoginResponse;
import com.infotech.wishmaplus.Api.Response.PagesResponse;
import com.infotech.wishmaplus.Api.Response.SignUpResponse;
import com.infotech.wishmaplus.Api.Response.UpgradePackageResponse;
import com.infotech.wishmaplus.Api.Response.UserDetailResponse;
import com.infotech.wishmaplus.Api.Response.UserListFriends;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EndPointInterface {
    @POST("api/Account/Login")
    Call<LoginResponse> secureLogin(@Query("ReferralID") String referralID,
                                    @Query("UserName") String userName,
                                    @Query("Password") String password,
                                    @Query("LoginPlatformID") int loginPlatformID,
                                    @Query("GmailAccessToken") String gmailAccessToken);

    @POST("api/Account/ForgetPassword")
    Call<LoginResponse> forgetPassword(@Query("MobileNo") String mobileNo, @Query("Password") String password, @Query("OTP") String otp);

    @POST("api/Account/CheckUserExists")
    Call<Boolean> checkUserExists(@Query("GmailAccessToken") String gmailAccessToken);

    @Headers("Content-Type: application/json")
    @POST("api/Account/Signup")
    Call<SignUpResponse> userSignup(@Body SignUpRequest signUpRequest);

    @Multipart
    @POST("api/Content")
    Call<BasicResponse> postContent(
            @Header("Authorization") String token,
            @Part("PostId") RequestBody postId,
            @Part("ContentTypeId") RequestBody contentTypeId,
            @Part("PostContent") RequestBody postContent,
            @Part("Caption") RequestBody caption,
            @Part("Height") RequestBody height,
            @Part("Width") RequestBody width,
            @Part("PageId") RequestBody PageId,
            @Part("DurationInMs") RequestBody durationInMs,
            @Part MultipartBody.Part extraParam
    );

    @Multipart
    @POST("api/Content/SaveStory")
    Call<BasicResponse> saveStory(
            @Header("Authorization") String token,
            @Part("StoryId") RequestBody storyId,
            @Part("ContentTypeId") RequestBody contentTypeId,
            @Part("StoryContent") RequestBody storyContent,
            @Part("Caption") RequestBody caption,
            @Part("Height") RequestBody height,
            @Part("Width") RequestBody width,
            @Part("DurationInMs") RequestBody durationInMs,
            @Part MultipartBody.Part extraParam
    );

    @GET("api/Content")
    Call<ContentResponse> getContent(@Header("Authorization") String token,
                                     @Query("PostId") String postId,
                                     @Query("UserId") String userId,
                                     @Query("pageNumber") int pageNumber,
                                     @Query("pageSize") int pageSize,
                                     @Query("IsSelf") boolean IsSelf,
                                     @Query("PageId") String PageId,
                                     @Query("ContentTypeID") int contentTypeID);

    @GET("api/Content/GetStory")
    Call<BasicListResponse<StoryResult>> getStory(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @POST("api/Like/Do")
    Call<LikeResponse> likePost(@Header("Authorization") String token,
                                @Body LikeRequest likeRequest);

    @Headers("Content-Type: application/json")
    @POST("api/Comment/Do")
    Call<BasicObjectResponse<CommentResult>> commentPost(@Header("Authorization") String token,
                                                         @Body CommentRequest commentRequest);

    @GET("api/Comment")
    Call<ArrayList<CommentResult>> getComment(@Header("Authorization") String token,
                                              @Query("PostId") String postId,
                                              @Query("ReplyId") String replyId);

    @DELETE("api/Content")
    Call<BasicResponse> deleteComment(@Header("Authorization") String token,
                                      @Query("PostId") String postId);

    @DELETE("api/Content/DeleteStory")
    Call<BasicResponse> deleteStory(@Header("Authorization") String token,
                                    @Query("StoryId") String storyId);

    @GET("api/GetUserDetails")
    Call<UserDetailResponse> getUserDetail(@Header("Authorization") String token,
                                           @Query("UserId") String UserId);


    @POST("api/DoFollow")
    Call<LikeResponse> DoFollow(@Header("Authorization") String token,
                                @Query("ToFollowUserId") String ToFollowUserId);

    @Multipart
    @POST("api/UpdateProfilePicture")
    Call<SignUpResponse> updateProfilePicture(@Header("Authorization") String authorization,
                                              @Query("IsCoverPicture") int isCoverPicture,
                                              @Part MultipartBody.Part model);

    @POST("api/UpdateUser")
    Call<SignUpResponse> updateUser(@Header("Authorization") String authorization,
                                    @Body UpdateUserRequest request);

    @POST("api/GetState")
    Call<BasicListResponse<StateResult>> getState(@Header("Authorization") String authorization);

    @POST("api/GetCity")
    Call<BasicListResponse<CityResult>> getCity(@Header("Authorization") String authorization,
                                                @Query("StateId") int stateId);

    @POST("api/GetBank")
    Call<BasicListResponse<BankResult>> getBank(@Header("Authorization") String authorization,
                                                @Query("bankId") int bankId);

    @GET("api/GetLevelWiseCount")
    Call<BasicListResponse<LevelCountResult>> getLevelWiseCount(@Header("Authorization") String authorization);

    @GET("api/GetLevelIncomeDetail")
    Call<BasicListResponse<LevelCountResult>> getLevelIncomeDetail(@Header("Authorization") String authorization,
                                                                   @Query("LevelNo") int levelNo);

    @GET("api/UserPackage")
    Call<BasicListResponse<PackageResult>> getUserPackage(@Header("Authorization") String authorization);

    @GET("api/UserPackage/Setting")
    Call<BasicObjectResponse<PackageResult>> getUserPackageSetting(@Header("Authorization") String authorization);

    @GET("api/GetFollower")
    Call<FollowersResponse> getFollower(@Header("Authorization") String authorization);

    @GET("api/GetBalance")
    Call<BasicObjectResponse<BalanceResult>> getBalance(@Header("Authorization") String authorization);

    @GET("api/Content/GetPost")
    Call<BasicObjectResponse<ContentResult>> getPost(@Header("Authorization") String authorization,
                                                     @Query("PostId") String postId);

    @POST("api/Content/SharePost")
    Call<BasicResponse> sharePost(@Header("Authorization") String authorization,
                                  @Body SharePostRequest request);

    @POST("api/UserPackage/UpGrade")
    Call<UpgradePackageResponse> upgradePackage(@Header("Authorization") String authorization,
                                                @Query("PackageId") int packageId,
                                                @Query("TID") String tid,
                                                @Query("Salt") String salt);

    @POST("api/PGCallback/PayUTransactionUpdate")
    Call<UpgradePackageResponse> payUTransactionUpdate(@Header("Authorization") String authorization,
                                                       @Query("TID") String tid);

    @GET("api/Content/ReportReason")
    Call<BasicListResponse<ReportReasonResult>> getReportReason(@Header("Authorization") String authorization);

    @POST("api/Content/ReportPost")
    Call<BasicResponse> reportPost(@Header("Authorization") String authorization,
                                   @Body ReportPostRequest request);

    @GET("api/CompanyDetails")
    Call<CompanyDetailResponse> getCompanyDetails(@Header("Authorization") String authorization);

    @GET("api/IncomeReport")
    Call<BasicListResponse<Income>> getIncomeResponse(@Header("Authorization") String authorization);


    @GET("api/UserProfile/GetFriendRequest")
    Call<List<UserListFriends>> getFriendRequest(@Header("Authorization") String authorization);


    @POST("api/UserProfile/CreateRequest/{ToUserId}")
    Call<BasicResponse> createRequest(@Header("Authorization") String authorization,
                                      @Path("ToUserId") String ToUserId);

    @POST("api/UserProfile/RemoveRequest/{ToUserId}")
    Call<BasicResponse> removeRequest(@Header("Authorization") String authorization,
                                      @Path("ToUserId") String ToUserId);

    @POST("api/UserProfile/AcceptOrRejectRequest")
    Call<BasicResponse> AcceptOrRejectRequest(@Header("Authorization") String authorization,
                                              @Body BasicRequest request);

    @GET("api/UserProfile/getPageCategories")
    Call<List<CategoryResponse>> getPageCategories(@Header("Authorization") String authorization);

    @GET("api/UserProfile/getPage")
    Call<PagesResponse> getPagesResponse(@Header("Authorization") String authorization);

    @POST("api/UserProfile/SetProfileType")
    Call<BasicResponse> setProfileType(@Header("Authorization") String authorization,
                                       @Body BasicRequest request);

    @Multipart
    @POST("api/UserProfile/createPage")
    Call<BasicResponse> createPage(
            @Header("Authorization") String authorization,
            @Part("PageName") RequestBody pageName,
            @Part("CategoryId") RequestBody categoryId,
            @Part("Bio") RequestBody bio,
            @Part("Website") RequestBody website,
            @Part("Email") RequestBody email,
            @Part("Phone") RequestBody phone,
            @Part("Address") RequestBody address,
            @Part MultipartBody.Part ProfileImageFile,
            @Part MultipartBody.Part CoverImageFile
    );

    @GET("api/UserProfile/getPageDetails/{PageId}")
    Call<UserDetailResponse> getPageDetails(@Header("Authorization") String token,
                                            @Path("PageId") String PageId);
}
