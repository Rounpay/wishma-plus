package com.infotech.wishmaplus.Utils;

import com.infotech.wishmaplus.Adapter.FriendSuggestionResponse;
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
import com.infotech.wishmaplus.Api.Request.AddFriendsRequest;
import com.infotech.wishmaplus.Api.Request.BasicRequest;
import com.infotech.wishmaplus.Api.Request.BlockUserRequest;
import com.infotech.wishmaplus.Api.Request.CommentRequest;
import com.infotech.wishmaplus.Api.Request.ComplaintRequest;
import com.infotech.wishmaplus.Api.Request.InitiateBoostRequest;
import com.infotech.wishmaplus.Api.Request.LikeRequest;
import com.infotech.wishmaplus.Api.Request.ReportPostRequest;
import com.infotech.wishmaplus.Api.Request.SharePostRequest;
import com.infotech.wishmaplus.Api.Request.SignUpRequest;
import com.infotech.wishmaplus.Api.Request.UpdateGroupMemberRequest;
import com.infotech.wishmaplus.Api.Request.UpdateUserRequest;
import com.infotech.wishmaplus.Api.Response.AddPeopleResponse;
import com.infotech.wishmaplus.Api.Response.AnalyticsDetailsResponse;
import com.infotech.wishmaplus.Api.Response.AnalyticsResponse;
import com.infotech.wishmaplus.Api.Response.BasicListResponse;
import com.infotech.wishmaplus.Api.Response.BasicObjectResponse;
import com.infotech.wishmaplus.Api.Response.BasicResponse;
import com.infotech.wishmaplus.Api.Response.BlockUserResponse;
import com.infotech.wishmaplus.Api.Response.BlockedUserListResponse;
import com.infotech.wishmaplus.Api.Response.BoostBillingResponse;
import com.infotech.wishmaplus.Api.Response.BoostResponse;
import com.infotech.wishmaplus.Api.Response.BoostedPostStatusChangeResponse;
import com.infotech.wishmaplus.Api.Response.CategoryResponse;
import com.infotech.wishmaplus.Api.Response.CompanyDetailResponse;
import com.infotech.wishmaplus.Api.Response.ComplaintResponse;
import com.infotech.wishmaplus.Api.Response.ComplaintSubmitResponse;
import com.infotech.wishmaplus.Api.Response.ContentResponse;
import com.infotech.wishmaplus.Api.Response.CreateGroupResponse;
import com.infotech.wishmaplus.Api.Response.DeleteAccountResponse;
import com.infotech.wishmaplus.Api.Response.EligibilityModel;
import com.infotech.wishmaplus.Api.Response.EnableDashboardResponse;
import com.infotech.wishmaplus.Api.Response.EstimateResponse;
import com.infotech.wishmaplus.Api.Response.FollowersResponse;
import com.infotech.wishmaplus.Api.Response.FriendListResponse;
import com.infotech.wishmaplus.Api.Response.GetContentDetailsToBoostResponse;
import com.infotech.wishmaplus.Api.Response.GetRoomIdResponse;
import com.infotech.wishmaplus.Api.Response.GetUserListResponse;
import com.infotech.wishmaplus.Api.Response.GroupDetailsResponse;
import com.infotech.wishmaplus.Api.Response.GroupListResponse;
import com.infotech.wishmaplus.Api.Response.GroupMembersResponse;
import com.infotech.wishmaplus.Api.Response.GroupMembersUpdateResponse;
import com.infotech.wishmaplus.Api.Response.Income;
import com.infotech.wishmaplus.Api.Response.InsightResponse;
import com.infotech.wishmaplus.Api.Response.InsightsStatsResponse;
import com.infotech.wishmaplus.Api.Response.LikeResponse;
import com.infotech.wishmaplus.Api.Response.LinkClickResponse;
import com.infotech.wishmaplus.Api.Response.LoginResponse;
import com.infotech.wishmaplus.Api.Response.NotificationResponse;
import com.infotech.wishmaplus.Api.Response.PagesResponse;
import com.infotech.wishmaplus.Api.Response.PostsResponse;
import com.infotech.wishmaplus.Api.Response.ReadNotificationResponse;
import com.infotech.wishmaplus.Api.Response.SentRequestResponse;
import com.infotech.wishmaplus.Api.Response.SignUpResponse;
import com.infotech.wishmaplus.Api.Response.SupportCategoryResponse;
import com.infotech.wishmaplus.Api.Response.UnfriendResponse;
import com.infotech.wishmaplus.Api.Response.UpgradePackageResponse;
import com.infotech.wishmaplus.Api.Response.UploadGroupCoverResponse;
import com.infotech.wishmaplus.Api.Response.UserDetailResponse;
import com.infotech.wishmaplus.Api.Response.UserListFriends;
import com.infotech.wishmaplus.GetReelResponse;
import com.infotech.wishmaplus.HashtagResponse;
import com.infotech.wishmaplus.SaveReelResponse;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
            @Part("GroupId") RequestBody GroupId,
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
                                     @Query("GroupId") String GroupId,
                                     @Query("ContentTypeID") int contentTypeID,
                                     @Query("IsFromNotification") boolean IsFromNotification );

    @GET("api/Content/GetStory")
    Call<BasicListResponse<StoryResult>> getStory(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @POST("api/Like/Do")
    Call<LikeResponse> likePost(@Header("Authorization") String token,
                                @Body LikeRequest likeRequest);
    @Headers("Content-Type: application/json")
    @POST("api/AddInsight")
    Call<InsightResponse> addInsight(@Header("Authorization") String token,
                                     @Query("AccountId") String AccountId,
                                     @Query("PostID") String PostID,
                                     @Query("AccountType") int AccountType,
                                     @Query("InsightTypeID") int InsightTypeID);


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
                                           @Query("UserId") String UserId,
                                           @Query("GroupId") String GroupId
                                           );


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


    @GET("api/UserProfile/GetContentNotification")
    Call<NotificationResponse> getNotifications(@Header("Authorization") String authorization);


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

    @GET("api/UserProfile/GetFriendList")
    Call<FriendListResponse> getFriendList(@Header("Authorization") String authorization);

    @GET("api/UserProfile/GetFriendSuggestionList")
    Call<FriendSuggestionResponse> getFriendSuggestionList(@Header("Authorization") String authorization);

    @GET("api/UserProfile/GetSentRequestFriendList")
    Call<SentRequestResponse> getFriendRequests(@Header("Authorization") String authorization);

    @POST("api/UserProfile/CheckProfessionalDashboardEligibility")
    Call<EligibilityModel> checkEligibilityForProfessional(@Header("Authorization") String authorization);
    @POST("api/UserProfile/EnableProfessionalDashBoard")
    Call<EnableDashboardResponse> enableProfessionalDashBoard(@Header("Authorization") String authorization);

    @GET("api/UserProfile/getPage")
    Call<PagesResponse> getPagesResponse(@Header("Authorization") String authorization);

    @POST("api/UserProfile/DeleteAccount")
    Call<DeleteAccountResponse> deleteAccount(@Header("Authorization") String authorization,
                                              @Query("AccountType") int AccountType,
                                              @Query("AccountId") String AccountId);

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

    @GET("api/GetContentToBoost")
    Call<PostsResponse> getContentToBoost(
            @Query("PageId") String pageId,
            @Query("DateRange") int DateRange,
            @Query("ContentType") int ContentType,
            @Header("Authorization") String token
    );
    @GET("api/UserProfile/IsReadContentNotification")
    Call<ReadNotificationResponse> getMarkNotificationRead(
            @Query("NotificationId") int NotificationId,
            @Header("Authorization") String token
    );
    @GET("api/GetContentDetailsToBoost")
    Call<GetContentDetailsToBoostResponse> getContentDetailsToBoost(
            @Query("PostId") String PostId,
            @Header("Authorization") String token
    );

    @GET("api/GetEstimateBoostReach")
    Call<EstimateResponse> getEstimateBoostReach(
            @Query("Budget") double Budget,
            @Query("DurationDays") int DurationDays,
            @Query("AudienceId") int AudienceId,
            @Header("Authorization") String token
    );

    @POST("api/InitiateBoostPost")
    Call<BoostResponse> initiateBoostPost(
            @Header("Authorization") String token,
            @Body InitiateBoostRequest request
    );

    @POST("api/UserProfile/BlockUser")
    Call<BlockUserResponse> blockUser(
            @Header("Authorization") String token,
            @Body BlockUserRequest request
    );

    @POST("api/UpdateBoostStatus")
    Call<BoostedPostStatusChangeResponse> updateBoostStatus(
            @Header("Authorization") String authorization,
            @Query("BoostId") int BoostId,
            @Query("BoostStatus") int BoostStatus);

    @POST("api/Group/CreateUpdateGroup")
    Call<CreateGroupResponse> createUpdateGroup(
            @Header("Authorization") String authorization,
            @Query("GroupId") String GroupId,
            @Query("Title") String Title,
            @Query("Description") String Description,
            @Query("IsPrivate") boolean IsPrivate,
            @Query("IsVisible") Boolean IsVisible);

    @GET("api/Group/GetUserList")
    Call<GetUserListResponse> getUsersList(
            @Header("Authorization") String token
    );
    @GET("api/Group/GetGroupById")
    Call<GroupDetailsResponse> getGroupById(
            @Header("Authorization") String token,
            @Query("GroupId") String groupId
    );

    @Multipart
    @POST("api/Group/UpdateGroupProfilePicture")
    Call<UploadGroupCoverResponse> updateGroupProfilePicture(
            @Header("Authorization") String token,
            @Query("GroupId") String groupId,
            @Query("IsCoverPicture") boolean isCoverPicture,
            @Part MultipartBody.Part model);

    @POST("api/Group/AddMultipleFriendsToGroup")
    Call<AddPeopleResponse> addMultipleFriendsToGroup(
            @Header("Authorization") String authorization,
            @Body AddFriendsRequest request
    );

    @GET("api/Group/GetGroup")
    Call<GroupListResponse> getGroupsListing(
            @Header("Authorization") String token,
            @Query("OnlyMyGroups") boolean OnlyMyGroups,
            @Query("OrderByName") Boolean OrderByName,
            @Query("OrderByJoinDate") Boolean OrderByJoinDate
    );


    @GET("api/Group/GetGroupMembers")
    Call<GroupMembersResponse> getGroupsMembers(
            @Header("Authorization") String token,
            @Query("GroupId") String GroupId
    );

    @GET("api/Content/PostStats")
    Call<InsightsStatsResponse> getPostStats(
            @Header("Authorization") String token,
            @Query("PostId") String PostId,
            @Query("DateRange") int DateRange
    );
    @GET("api/GetBoostBillingInfo")
    Call<BoostBillingResponse> getBoostBillingInfo(
            @Header("Authorization") String token,
            @Query("PostId") String PostId
    );
    @GET("api/Support/GetComplaintCategory")
    Call<SupportCategoryResponse> getComplaintCategory(
            @Header("Authorization") String token
    );
    @GET("api/DownloadBillingPdf")
    Call<ResponseBody> getDownloadBillingPdf(
            @Header("Authorization") String token,
            @Query("BoostId") int BoostId
    );
    @GET("api/Support/GetMyComplaint")
    Call<ComplaintResponse> getMyComplaint(
            @Header("Authorization") String token
    );
    @GET("api/UserReport/ProfessionalDahboardAnalytic")
    Call<AnalyticsResponse> getProfessionalDahboardAnalytic(
            @Header("Authorization") String token,
            @Query("DateRange") int DateRange
    );

    @GET("api/UserReport/DateWiseAnalytic")
    Call<AnalyticsDetailsResponse> getDateWiseAnalytic(
            @Header("Authorization") String token,
            @Query("DateRange") int DateRange
    );

    @POST("api/Group/UpdateGroupMembers")
    Call<GroupMembersUpdateResponse> updateGroupMembers(
            @Header("Authorization") String authorization,
            @Body UpdateGroupMemberRequest request);
    @POST("api/UserProfile/BlockedUserList")
    Call<BlockedUserListResponse> getBlockedUserList(
            @Header("Authorization") String authorization);
    @POST("api/Support/SubmitComplaint")
    Call<ComplaintSubmitResponse> submitComplaint(
            @Header("Authorization") String authorization,
            @Body ComplaintRequest request);
    @POST("api/UserProfile/UnFriendUser/{ToUserId}")
    Call<UnfriendResponse> unFriendUser(
            @Header("Authorization") String authorization,
            @Path("ToUserId") String ToUserId);

    @POST("api/InsertLinkClick")
    Call<LinkClickResponse> insertLinkClick(
            @Header("Authorization") String authorization,
            @Query("PostId") String PostId,
            @Query("ClickType") int ClickType);

    @GET("api/zego/getroomId")
    Call<GetRoomIdResponse> getRoomId(
            @Header("Authorization") String authorization);

    @GET("api/zego/startlive")
    Call<BasicResponse> startLive(
            @Header("Authorization") String authorization,
            @Query("RoomId") String RoomId
    );

    @GET("api/zego/endlive")
    Call<BasicResponse> endLive(
            @Header("Authorization") String authorization,
            @Query("RoomId") String RoomId
    );
    // ─── Hashtag Suggestions ───────────────────────────────────────────────────
    @GET("api/Content/GetReelHasTagSuggestions")
    Call<HashtagResponse> getHashtagSuggestions(
            @Header("Authorization") String authorization,
            @Query("q") String query,
            @Query("topN") int topN
    );

    // ─── Save Reel ─────────────────────────────────────────────────────────────
    @Multipart
    @POST("api/Content/SaveReel")
    Call<SaveReelResponse> saveReel(
            @Header("Authorization") String authorization,
            @Query("Caption") String caption,
            @Query("Duration") int duration,
            @Query("Hashtags") String hashtags,
            @Part MultipartBody.Part video,
            @Part MultipartBody.Part thumbnail
    );

    //-------------GetReel---------------------------
    @GET("api/content/GetReel")
    Call<GetReelResponse> getReels(
            @Header("Authorization") String authorization,
            @Query("PageNumber") int page,
            @Query("PageSize") int size,
            @Query("SortBy") String sortBy
    );
}
