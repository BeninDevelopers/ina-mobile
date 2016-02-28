package org.benindevelopers.webservices;


import org.benindevelopers.webservices.model.EtatCourant;
import org.benindevelopers.webservices.model.EtatZone;
import org.benindevelopers.webservices.model.Zone;

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

    public static final String BASE_URL = "host_name/service";

    //1-enregistrement d'un utilisateur
    @FormUrlEncoded
    @POST("/register")
    Call<String> enregistrerUtilisateur(
            @Field("email") String email,
            @Field("phoneId") String phoneId,
            @Field("gcmId") String gcmId
    );

    //2-envoi de l'état du courant
    @FormUrlEncoded
    @POST("/setlightstatus")
    Call<EtatCourant> renseignerEtatCourant(
            @Field("phoneId") String phoneId,
            @Field("etat") boolean etat,
            @Field("lat") float lat,
            @Field("lon") float lon
    );

    //3-recherche d'une zone
    @GET("/searcharea")
    Call<List<Zone>> rechercherZone(
            @Query("zone") String zone
    );

    //4-état du courant dans une zone
    @GET("/getlightstatus")
    Call<EtatCourant> consulterEtatCourant(
            @Query("zoneId") String zoneId
    );

    //5-liste des zones électrifiées ou non
    Call<List<EtatZone>> getEtatZone(
            @Query("lat") float lat,
            @Query("lon") float lon
    );

}
