package org.benindevelopers.ina.webservice.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

//import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 * @author Seth-Pharès Gnavo (sethgnavo)
 *
 * Modified by Joane SETANGNI on 29/02/2016
 *      - make EtatZone implements SearchSuggestion
 *      - add necessary methodes for SearchSuggestion implementation
 */
public class EtatZone implements SearchSuggestion {

    public static final int ETAT_SURREMENT_PAS = 1;
    public static final int ETAT_PROBABLEMENT_PAS = 2;
    public static final int ETAT_PROBABLEMENT_A = 3;
    public static final int ETAT_SURREMENT_A = 4;

    private long id;
    private float lat;
    private float lon;
    private boolean etat;
    private String libelle;
    private String description;
    private int etatDescription;
//    private int etatDescription = (new Random()).nextInt(4) + 1;

    public EtatZone() {
    }

    protected EtatZone(Parcel in) {
        id = in.readLong();
        lat = in.readFloat();
        lon = in.readFloat();
        etat = in.readByte() != 0x00;
        libelle = in.readString();
        description = in.readString();
        etatDescription = in.readInt();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public boolean isEtat() {
        return etat;
    }

    public void setEtat(boolean etat) {
        this.etat = etat;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEtatDescription() {
        return etatDescription;
    }

    public void setEtatDescription(int etatDescription) {
        this.etatDescription = etatDescription;
    }

    @Override
    public String getBody() {
        return libelle;
    }

    @Override
    public Creator getCreator() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeFloat(lat);
        dest.writeFloat(lon);
        dest.writeByte((byte) (etat ? 0x01 : 0x00));
        dest.writeString(libelle);
        dest.writeString(description);
        dest.writeInt(etatDescription);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<EtatZone> CREATOR = new Parcelable.Creator<EtatZone>() {
        @Override
        public EtatZone createFromParcel(Parcel in) {
            return new EtatZone(in);
        }

        @Override
        public EtatZone[] newArray(int size) {
            return new EtatZone[size];
        }
    };



}
