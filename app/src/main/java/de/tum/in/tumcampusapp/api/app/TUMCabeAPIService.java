package de.tum.in.tumcampusapp.api.app;

import java.util.List;

import de.tum.in.tumcampusapp.api.app.model.DeviceRegister;
import de.tum.in.tumcampusapp.api.app.model.DeviceUploadGcmToken;
import de.tum.in.tumcampusapp.component.ui.news.model.NewsAlert;
import de.tum.in.tumcampusapp.api.app.model.TUMCabeStatus;
import de.tum.in.tumcampusapp.component.other.locations.model.BuildingToGps;
import de.tum.in.tumcampusapp.component.other.wifimeasurement.model.WifiMeasurement;
import de.tum.in.tumcampusapp.component.tumui.feedback.model.Feedback;
import de.tum.in.tumcampusapp.component.tumui.feedback.model.Success;
import de.tum.in.tumcampusapp.component.tumui.roomfinder.model.RoomFinderCoordinate;
import de.tum.in.tumcampusapp.component.tumui.roomfinder.model.RoomFinderMap;
import de.tum.in.tumcampusapp.component.tumui.roomfinder.model.RoomFinderRoom;
import de.tum.in.tumcampusapp.component.tumui.roomfinder.model.RoomFinderSchedule;
import de.tum.in.tumcampusapp.component.ui.alarm.model.GCMNotification;
import de.tum.in.tumcampusapp.component.ui.alarm.model.GCMNotificationLocation;
import de.tum.in.tumcampusapp.component.ui.barrierfree.model.BarrierfreeContact;
import de.tum.in.tumcampusapp.component.ui.barrierfree.model.BarrierfreeMoreInfo;
import de.tum.in.tumcampusapp.component.ui.cafeteria.model.Cafeteria;
import de.tum.in.tumcampusapp.component.ui.chat.model.ChatMember;
import de.tum.in.tumcampusapp.component.ui.chat.model.ChatMessage;
import de.tum.in.tumcampusapp.component.ui.chat.model.ChatPublicKey;
import de.tum.in.tumcampusapp.component.ui.chat.model.ChatRegistrationId;
import de.tum.in.tumcampusapp.component.ui.chat.model.ChatRoom;
import de.tum.in.tumcampusapp.component.ui.chat.model.ChatVerification;
import de.tum.in.tumcampusapp.component.ui.curricula.model.Curriculum;
import de.tum.in.tumcampusapp.component.ui.studycard.model.StudyCard;
import de.tum.in.tumcampusapp.component.ui.tufilm.model.Kino;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

import static de.tum.in.tumcampusapp.api.app.TUMCabeClient.API_BARRIER_FREE;
import static de.tum.in.tumcampusapp.api.app.TUMCabeClient.API_BARRIER_FREE_BUILDINGS_TO_GPS;
import static de.tum.in.tumcampusapp.api.app.TUMCabeClient.API_BARRIER_FREE_CONTACT;
import static de.tum.in.tumcampusapp.api.app.TUMCabeClient.API_BARRIER_FREE_LIST_OF_ELEVATORS;
import static de.tum.in.tumcampusapp.api.app.TUMCabeClient.API_BARRIER_FREE_LIST_OF_TOILETS;
import static de.tum.in.tumcampusapp.api.app.TUMCabeClient.API_BARRIER_FREE_MORE_INFO;
import static de.tum.in.tumcampusapp.api.app.TUMCabeClient.API_BARRIER_FREE_NERBY_FACILITIES;
import static de.tum.in.tumcampusapp.api.app.TUMCabeClient.API_CAFETERIAS;
import static de.tum.in.tumcampusapp.api.app.TUMCabeClient.API_CARD;
import static de.tum.in.tumcampusapp.api.app.TUMCabeClient.API_CHAT_MEMBERS;
import static de.tum.in.tumcampusapp.api.app.TUMCabeClient.API_CHAT_ROOMS;
import static de.tum.in.tumcampusapp.api.app.TUMCabeClient.API_CURRICULA;
import static de.tum.in.tumcampusapp.api.app.TUMCabeClient.API_DEVICE;
import static de.tum.in.tumcampusapp.api.app.TUMCabeClient.API_FEEDBACK;
import static de.tum.in.tumcampusapp.api.app.TUMCabeClient.API_KINOS;
import static de.tum.in.tumcampusapp.api.app.TUMCabeClient.API_LOCATIONS;
import static de.tum.in.tumcampusapp.api.app.TUMCabeClient.API_NEWS;
import static de.tum.in.tumcampusapp.api.app.TUMCabeClient.API_NOTIFICATIONS;
import static de.tum.in.tumcampusapp.api.app.TUMCabeClient.API_ROOM_FINDER;
import static de.tum.in.tumcampusapp.api.app.TUMCabeClient.API_ROOM_FINDER_AVAILABLE_MAPS;
import static de.tum.in.tumcampusapp.api.app.TUMCabeClient.API_ROOM_FINDER_COORDINATES;
import static de.tum.in.tumcampusapp.api.app.TUMCabeClient.API_ROOM_FINDER_SCHEDULE;
import static de.tum.in.tumcampusapp.api.app.TUMCabeClient.API_ROOM_FINDER_SEARCH;
import static de.tum.in.tumcampusapp.api.app.TUMCabeClient.API_WIFI_HEATMAP;

public interface TUMCabeAPIService {

    //Group chat
    @POST(API_CHAT_ROOMS)
    Call<ChatRoom> createRoom(@Body ChatVerification verification);

    @GET(API_CHAT_ROOMS + "{room}")
    Call<ChatRoom> getChatRoom(@Path("room") int id);

    @POST(API_CHAT_ROOMS + "{room}/leave/")
    Call<ChatRoom> leaveChatRoom(@Path("room") int roomId, @Body ChatVerification verification);

    //Get/Update single message
    @PUT(API_CHAT_ROOMS + "{room}/message/")
    Observable<ChatMessage> sendMessage(@Path("room") int roomId, @Body ChatMessage message);

    @PUT(API_CHAT_ROOMS + "{room}/message/{message}/")
    Observable<ChatMessage> updateMessage(@Path("room") int roomId, @Path("message") int messageId, @Body ChatMessage message);

    //Get all recent messages or older ones
    @POST(API_CHAT_ROOMS + "{room}/messages/{page}/")
    Observable<List<ChatMessage>> getMessages(@Path("room") int roomId, @Path("page") long messageId, @Body ChatVerification verification);

    @POST(API_CHAT_ROOMS + "{room}/messages/")
    Observable<List<ChatMessage>> getNewMessages(@Path("room") int roomId, @Body ChatVerification verification);

    @POST(API_CHAT_MEMBERS)
    Call<ChatMember> createMember(@Body ChatMember chatMember);

    @GET(API_CHAT_MEMBERS + "{lrz_id}/")
    Call<ChatMember> getMember(@Path("lrz_id") String lrzId);

    @POST(API_CHAT_MEMBERS + "{memberId}/rooms/")
    Call<List<ChatRoom>> getMemberRooms(@Path("memberId") int memberId, @Body ChatVerification verification);

    @GET(API_CHAT_MEMBERS + "{memberId}/pubkeys/")
    Call<List<ChatPublicKey>> getPublicKeysForMember(@Path("memberId") int memberId);

    @POST(API_CHAT_MEMBERS + "{memberId}/registration_ids/add_id")
    Call<ChatRegistrationId> uploadRegistrationId(@Path("memberId") int memberId, @Body ChatRegistrationId regId);

    //Curricula
    @GET(API_CURRICULA)
    Call<List<Curriculum>> getAllCurriculas();

    @GET(API_NOTIFICATIONS + "{notification}/")
    Call<GCMNotification> getNotification(@Path("notification") int notification);

    @GET(API_NOTIFICATIONS + "confirm/{notification}/")
    Call<String> confirm(@Path("notification") int notification);

    //Locations
    @GET(API_LOCATIONS)
    Call<List<GCMNotificationLocation>> getAllLocations();

    @GET(API_LOCATIONS + "{locationId}/")
    Call<GCMNotificationLocation> getLocation(@Path("locationId") int locationId);

    //Device
    @POST(API_DEVICE + "register/")
    Call<TUMCabeStatus> deviceRegister(@Body DeviceRegister verification);

    @POST(API_DEVICE + "addGcmToken/")
    Call<TUMCabeStatus> deviceUploadGcmToken(@Body DeviceUploadGcmToken verification);

    //WifiHeatmap
    @POST(API_WIFI_HEATMAP + "create_measurements/")
    Call<TUMCabeStatus> createMeasurements(@Body List<WifiMeasurement> wifiMeasurementList);

    // Barrier free contacts
    @GET(API_BARRIER_FREE + API_BARRIER_FREE_CONTACT)
    Call<List<BarrierfreeContact>> getBarrierfreeContactList();

    // Barrier free More Info
    @GET(API_BARRIER_FREE + API_BARRIER_FREE_MORE_INFO)
    Call<List<BarrierfreeMoreInfo>> getMoreInfoList();

    // Barrier free toilets list
    @GET(API_BARRIER_FREE + API_BARRIER_FREE_LIST_OF_TOILETS)
    Call<List<RoomFinderRoom>> getListOfToilets();

    // Barrier free elevator list
    @GET(API_BARRIER_FREE + API_BARRIER_FREE_LIST_OF_ELEVATORS)
    Call<List<RoomFinderRoom>> getListOfElevators();

    // Barrier free nearby list
    @GET(API_BARRIER_FREE + API_BARRIER_FREE_NERBY_FACILITIES + "{buildingId}/")
    Call<List<RoomFinderRoom>> getListOfNearbyFacilities(@Path("buildingId") String buildingId);

    // building to gps information
    @GET(API_BARRIER_FREE + API_BARRIER_FREE_BUILDINGS_TO_GPS)
    Call<List<BuildingToGps>> getBuilding2Gps();

    //RoomFinder maps
    @GET(API_ROOM_FINDER + API_ROOM_FINDER_AVAILABLE_MAPS + "{archId}")
    Call<List<RoomFinderMap>> fetchAvailableMaps(@Path("archId") String archId);

    //RoomFinder maps
    @GET(API_ROOM_FINDER + API_ROOM_FINDER_SEARCH + "{searchStrings}")
    Call<List<RoomFinderRoom>> fetchRooms(@Path("searchStrings") String searchStrings);

    //RoomFinder cordinates
    @GET(API_ROOM_FINDER + API_ROOM_FINDER_COORDINATES + "{archId}")
    Call<RoomFinderCoordinate> fetchCoordinates(@Path("archId") String archId);

    //RoomFinder schedule
    @GET(API_ROOM_FINDER + API_ROOM_FINDER_SCHEDULE + "{roomId}" + "/" + "{start}" + "/" + "{end}")
    Call<List<RoomFinderSchedule>> fetchSchedule(@Path("roomId") String archId,
                                                 @Path("start") String start, @Path("end") String end);

    @POST(API_FEEDBACK)
    Call<Success> sendFeedback(@Body Feedback feedback);

    @Multipart
    @POST(API_FEEDBACK + "{id}/{image}/")
    Call<Success> sendFeedbackImage(@Part MultipartBody.Part image, @Path("image") int imageNr, @Path("id") String feedbackId);

    @GET(API_CAFETERIAS)
    Observable<List<Cafeteria>> getCafeterias();

    @GET(API_KINOS + "{lastId}")
    Observable<List<Kino>> getKinos(@Path("lastId") String lastId);

    @GET(API_CARD)
    Call<List<StudyCard>> getStudyCards();

    @PUT(API_CARD)
    Call<StudyCard> addStudyCard(@Body ChatVerification verification);

    @GET(API_NEWS + "alert")
    Observable<NewsAlert> getNewsAlert();
}