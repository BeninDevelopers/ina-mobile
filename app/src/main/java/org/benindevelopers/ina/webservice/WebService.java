package org.benindevelopers.ina.webservice;


import org.benindevelopers.ina.webservice.model.EtatCourant;
import org.benindevelopers.ina.webservice.model.EtatZone;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @author Seth-Pharès Gnavo (sethgnavo)
 */
public interface WebService {

    public static final String BASE_URL = "http://ina.benindevelopers.org/web/app_dev.php/service/";
    public static final String REP_OK = "OK";

    //1-enregistrement d'un utilisateur
    @FormUrlEncoded
    @POST("register")
    Call<String> enregistrerUtilisateur(
            @Field("email") String email,
            @Field("phoneid") String phoneId,
            @Field("gcmId") String gcmId
    );

    //2-envoi de l'état du courant
    @FormUrlEncoded
    @POST("setlightstatus")
    Call<String> renseignerEtatCourant(
            @Field("phoneid") String phoneId,
            @Field("etat") boolean etat,
            @Field("lat") double latitude,
            @Field("long") double longitude
    );

    //3-recherche d'une zone
    @GET("searcharea")
    Call<List<EtatZone>> rechercherZone(
            @Query("zone") String zone
    );

    //4-état du courant dans une zone
    @GET("getlightstatus")
    Call<EtatCourant> consulterEtatCourant(
            @Query("zoneId") String zoneId
    );

    //5-liste des zones électrifiées ou non
    @GET("lightstatus")
    Call<List<EtatZone>> getEtatZones(
            @Query("lat") double lat,
            @Query("lon") double lon
    );

}
