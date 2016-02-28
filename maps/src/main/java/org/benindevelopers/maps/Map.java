package org.benindevelopers.maps;

/**
 * Created by Ramadan SOUMAILA, SALAMI Abdel-Faiçal , Tiburce, on 28/02/16.
 */

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;

import org.benindevelopers.webservices.model.EtatZone;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.SimpleLocationOverlay;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;





import java.util.ArrayList;

public class Map extends Activity{

    protected MapView map;
    protected boolean mTrackingMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        MapView map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        mapController.setZoom(9);
        GeoPoint startPoint = new GeoPoint(48.8583, 2.2944);
        mapController.setCenter(startPoint);
        addPoints(null);

    }

    public void addPoints(ArrayList<EtatZone> zones){

        if(zones==null){
            zones= new ArrayList<EtatZone>();
            EtatZone zone = new EtatZone();
            zone.setZone("Zogbadjè");
            zone.setEtat(true);
            zone.setDescription("Vous avez sûrement le courant");
            zone.setLat(6.37780f);
            zone.setLon(2.44306f);
            zones.add(zone);

            EtatZone zone1 = new EtatZone();
            zone1.setZone("Zogbadjè");
            zone1.setEtat(false);
            zone1.setDescription("Vous n'avez pas le courant");
            zone1.setLat(6.37780f);
            zone1.setLon(2.44306f);
            zones.add(zone1);
        }

        SimpleLocationOverlay overlay=new SimpleLocationOverlay(this);
        ArrayList<OverlayItem> items=new ArrayList<>();
        Drawable icon;


        for(EtatZone etatZone:zones)
        {
            if(etatZone.isEtat() == true){
                icon = getResources().getDrawable(R.drawable.green_marker);
            }
            else{
                icon = getResources().getDrawable(R.drawable.red_marker);
            }

            OverlayItem current= new OverlayItem(etatZone.getZone(), etatZone.getDescription(), new GeoPoint(etatZone.getLat(), etatZone.getLon()));
            current.setMarker(icon);
            items.add(current);

        }

        ItemizedIconOverlay<OverlayItem> itemiseLocOverlay= new ItemizedIconOverlay<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>(){

                    @Override
                    public boolean onItemSingleTapUp(int index, OverlayItem item) {
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(int index, OverlayItem item) {
                        return true;
                    }
                },new DefaultResourceProxyImpl(getApplicationContext()));
        MapView map = (MapView) findViewById(R.id.map);
        map.getOverlays().add(itemiseLocOverlay);
        map.getController().setCenter(new GeoPoint(6.37806, 2.44306));
    }


}
