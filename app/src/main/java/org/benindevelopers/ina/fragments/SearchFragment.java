package org.benindevelopers.ina.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.afollestad.materialdialogs.MaterialDialog;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.arlib.floatingsearchview.util.view.BodyTextView;
import com.arlib.floatingsearchview.util.view.IconImageView;

import org.benindevelopers.ina.R;
import org.benindevelopers.ina.power.PowerConnectionReceiver;
import org.benindevelopers.ina.utils.MyUtils;
import org.benindevelopers.ina.webservice.WebService;
import org.benindevelopers.ina.webservice.model.EtatZone;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.arlib.floatingsearchview.FloatingSearchView;


/**
 *
 * Created by Joane SETANGNI on 28/02/2016.
 *
 * OSM Map integration code by Ramadan SOUMAILA, SALAMI Abdel-Faiçal , Tiburce, on 28/02/16.
 * FloatingActionButton code by Seth-Pharès Gnavo (sethgnavo) on 16/05/2016.
 *
 */
public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";
    private static final int MAP_ZOOM_LEVEL = 16;
    public final static int CONTENT_INDEX = 0;
    public final static int LOADER_INDEX = 1;
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 001;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private PowerConnectionReceiver batteryReceiver;
    private Intent intent;
    Context cxt;

    private OnFragmentInteractionListener mListener;
    private WebService retrofit;
    private static final String ABOUT_URL2 = "https://inadesignteam.slack.com/messages/general/files/F0PC6PW3E/";
    private static final String ABOUT_URL = "https://ina.benindevelopers.org/apropos";

    private LocationManager lm;
    private double latitude;
    private double longitude;
    private double altitude;
    private float accuracy;
    private View rootView;

    @Bind(R.id.map)
    MapView mapV;
    @Bind(R.id.search_bar)
    FloatingSearchView searchBar;
    @Bind(R.id.loadingRl)
    RelativeLayout loadingBar;
    @Bind(R.id.map_error_LlV)
    RelativeLayout mapErrorLlv;
    @Bind(R.id.fab_etat_courant_dialog)
    FloatingActionButton fabShowPowerDialog;
    private AlertDialog materialDialog;
    private ProgressDialog loadingDialog;
    private Toast errorToast;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cxt = getContext();
        // initialisation du loading dialog
        loadingDialog = new ProgressDialog(cxt, R.style.AppCompatAlertDialogStyle);

        // customisation du snackbar affiché quand une erreur suivient
        errorToast = Toast.makeText(cxt, R.string.erreur_serveur, Toast.LENGTH_LONG);
//        errorToast = Snackbar.make(rootView, R.string.erreur_serveur, Snackbar.LENGTH_LONG);
//        Snackbar.SnackbarLayout snackBarView = (Snackbar.SnackbarLayout) errorToast.getView();
//        TextView snackBarTextView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
//        snackBarTextView.setTextColor(getResources().getColor(R.color.black));
//        snackBarView.setBackgroundColor(getResources().getColor(R.color.white));


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, rootView);
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP)
            checkLocationPermissionIfNeeded();
        else
        {
            // Inflate the layout for this fragment
            // affichage du dialog d'activation de la localisation si necessaire
            boolean isProviderEnabled = SmartLocation.with(cxt).location().state().locationServicesEnabled();
            if(isProviderEnabled){
                // si localisation actif
                showAskPowerDialog();
            }else {
                // sinon demander activation
                showSettingsAlert();
            }
        }
        initialiseSearchBar();
        setupAskPowerDialog();
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Initialise le button de définition de l'état du courant
     */
    private void setupAskPowerDialog() {
        fabShowPowerDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAskPowerDialog();
            }
        });
    }

    /**
     * Initialise la barre de recherche
     */
    private void initialiseSearchBar() {
        searchBar.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.action_about) {
                    startAboutDialog();
                }
            }
        });
        searchBar.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                if (!oldQuery.equals("") && newQuery.equals("")) {
                    searchBar.clearSuggestions();

                } else if (newQuery.length() > 2) {
                    // on recupere la liste des EtatZone depuis le serveur
                    Call<List<EtatZone>> call = MyUtils.getInstance().getGsonWebServiceManager().rechercherZone(newQuery);
                    searchBar.showProgress();
                    call.enqueue(new Callback<List<EtatZone>>() {
                        @Override
                        public void onResponse(Call<List<EtatZone>> call, Response<List<EtatZone>> response) {
                            List<EtatZone> list = response.body();
                            Log.i(TAG, list.size() + " etatZone recu");
                            for (EtatZone etatZone : list) {
                                Log.i(TAG, etatZone.getId() + " " + etatZone.getLibelle() + " " + etatZone.getDescription() + " etat " + (etatZone.isEtat() ? 1 : 0) + " type " + etatZone.getEtatDescription());
                            }
                            if (list != null && !list.isEmpty()) {
                                searchBar.swapSuggestions(list);
                            }
                            searchBar.hideProgress();
                        }

                        @Override
                        public void onFailure(Call<List<EtatZone>> call, Throwable t) {
                            // TODO que faire quand aucun element dans la liste
                            searchBar.hideProgress();
                        }
                    });
                }
            }
        });

        // un listener pour reagir au click sur les SuggestionItem
        searchBar.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

                EtatZone etatZone = (EtatZone) searchSuggestion;
                Log.d(TAG, "zone choisi: " + etatZone.getLibelle());
                showEtatZoneResult(etatZone);
            }

            @Override
            public void onSearchAction() {
                // on ne fait rien ici car le user doit obligatoirement choisir une suggestion
            }
        });

        // ici nous customisons l'icon gauche des suggestionItem
        searchBar.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(IconImageView leftIcon, BodyTextView bodyText, SearchSuggestion item, int itemPosition) {
                leftIcon.setImageDrawable(leftIcon.getResources().getDrawable(R.drawable.ic_history_black_24dp));
                leftIcon.setAlpha(.36f);
            }

        });
    }

    private void startAboutDialog() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.action_about)
                .customView(R.layout.dialog_about, false)
                .positiveText(R.string.quitter_dialog)
                .show();
        initDialogviews(materialDialog.getCustomView());

    }

    private void initDialogviews(View customView) {
        final WebView webview = (WebView) customView.findViewById(R.id.webview);
        final ViewAnimator viewAnimator = (ViewAnimator) customView.findViewById(R.id.view_animator);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                viewAnimator.setDisplayedChild(LOADER_INDEX);
                super.onPageStarted(view, url, favicon);
            }

            public void onPageFinished(WebView view, String url) {
                viewAnimator.setDisplayedChild(CONTENT_INDEX);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

            }
        });
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(ABOUT_URL);
    }

    /**
     * Affiche le dialog de questionnement
     */
    private void showAskPowerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(cxt, R.style.AppCompatAlertDialogStyle);
//        builder.setTitle(R.string.success);
        builder.setMessage(R.string.demande_etat_courant)
                .setPositiveButton(R.string.oui, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // envoie

                        envoieEtatCourant(true);
                        materialDialog.dismiss();

                    }
                })
                .setNegativeButton(R.string.non, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        envoieEtatCourant(false);
                        materialDialog.dismiss();
                    }
                })
                .setCancelable(false);
        materialDialog = builder.create();

        materialDialog.show();

    }

    private void getLastLocation() {
        Location lastLocation = SmartLocation.with(getActivity()).location().getLastLocation();
        if (lastLocation != null) {
            latitude = lastLocation.getLatitude();
            longitude = lastLocation.getLongitude();
        }
    }

    /**
     * Methode permettant d'envoyer l'état du courant au serveur
     * et permettant au user de continuer l'usage de l'app
     *
     * @param siCourant
     */
    private void envoieEtatCourant(final boolean siCourant) {
        retrofit = MyUtils.getInstance().getScalarWebServiceManager();
        //on cache le dialog de questionement
        //materialDialog.dismiss();
        // on affiche le sending dialog
        showLoadingSendingDialog();
        //getLastLocation();

        SmartLocation.with(cxt).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {

                    @Override
                    public void onLocationUpdated(Location location) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        // une fois la localisation réussie
                        // on stop la localisation
                        SmartLocation.with(cxt).location().stop();
                        // et on continue le processus
                        Call<String> call = retrofit.renseignerEtatCourant(
                                MyUtils.getPhoneID(cxt),
                                siCourant,
                                latitude,
                                longitude
                        );

                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                String rep = response.body();
                                Log.i(TAG, "Call: " + latitude + " - " + longitude + " " + MyUtils.getPhoneID(cxt));
                                Log.i(TAG, "REP: " + rep);
                                if (rep != null && rep.equals(WebService.REP_OK)) {
                                    displayMap();
                                    //materialDialog.dismiss();
                                } else {
                                    //materialDialog.show();
                                    errorToast.show();
                                }
                                hideLoadingDialog();
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Log.d(TAG, "ERREUR: " + t.getMessage());
                                hideLoadingDialog();
                                // on reaffiche le dialog de questionnement
                                //materialDialog.show();
                                errorToast.show();
                            }
                        });

                    }
                });

    }

    /**
     * Affiche un Progressdialog
     */
    private void showLoadingSendingDialog() {
        loadingDialog.setCancelable(false);
        loadingDialog.setIndeterminate(true);
        loadingDialog.setMessage(getString(R.string.communication_serveur));
        loadingDialog.show();
    }

    /**
     * Cache le ProgressDialog
     */
    private void hideLoadingDialog() {
        loadingDialog.dismiss();
    }


    /**
     * Methode pour afficher un dialog forcant l'utilisateur à activer la localisation
     */
    public void showSettingsAlert() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(cxt, R.style.AppCompatAlertDialogStyle);
        AlertDialog alertDialog = dialogBuilder.create();
        // Setting Dialog Title
        alertDialog.setTitle(R.string.gpsDisabled);
        // Setting Dialog Message
        alertDialog.setMessage(cxt.getText(R.string.askActivateGPS));
        // On pressing Settings button
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, cxt.getText(R.string.menu_settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                cxt.startActivity(intent);
            }
        });
        alertDialog.setCancelable(false);
        // Showing Alert Message
        alertDialog.show();
    }


    /**
     * Méthode permettant d'initaialiser le Map
     */
    private void displayMap() {
        mapV.setTileSource(TileSourceFactory.MAPNIK);
        mapV.setBuiltInZoomControls(true);
        mapV.setMultiTouchControls(true);
        IMapController mapController = mapV.getController();
        mapController.setZoom(MAP_ZOOM_LEVEL);
        GeoPoint startPoint = new GeoPoint(latitude, longitude);
        mapController.setCenter(startPoint);

        Call<ArrayList<EtatZone>> getEtatZonesCall = MyUtils.getInstance().getGsonWebServiceManager().getEtatZones(latitude, longitude);
        getEtatZonesCall.enqueue(new Callback<ArrayList<EtatZone>>() {
            @Override
            public void onResponse(Call<ArrayList<EtatZone>> call, Response<ArrayList<EtatZone>> response) {
                List<EtatZone> list = response.body();
                // ajout des points au le map
                addPoints(list);
                mapErrorLlv.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ArrayList<EtatZone>> call, Throwable t) {
                Log.d(TAG, "error map: " + t.getMessage());
                mapErrorLlv.setVisibility(View.VISIBLE);
            }
        });

    }

    /**
     * Méthode pour afficher les points électrifiés ou non sur la carte
     *
     * @param zones
     */
    public void addPoints(final List<EtatZone> zones) {

//        if(zones==null){
//            zones= new ArrayList<EtatZone>();
//            EtatZone zone = new EtatZone();
//            zone.setLibelle("Zogbadjè");
//            zone.setEtat(true);
//            zone.setDescription("Vous avez sûrement le courant");
//            zone.setLat(6.37780f);
//            zone.setLon(2.44306f);
//            zones.add(zone);
//
//            EtatZone zone1 = new EtatZone();
//            zone1.setLibelle("Zogbadjè");
//            zone1.setEtat(false);
//            zone1.setDescription("Vous n'avez pas le courant");
//            zone1.setLat(6.37980f);
//            zone1.setLon(2.44390f);
//            zones.add(zone1);
//        }

        if (zones != null && !zones.isEmpty()) {
            Log.i(TAG, zones.size() + " Points affichés");

            ArrayList<OverlayItem> items = new ArrayList<>();
            Drawable icon;

            // nous creons une liste de markers qui seront affichés sur la carte
            for (EtatZone etatZone : zones) {
                Log.i(TAG, etatZone.getId() + " " + etatZone.getLibelle() + " " + etatZone.getDescription() + " etat " + (etatZone.isEtat() ? 1 : 0) + " type " + etatZone.getEtatDescription());
                if (etatZone.isEtat() == true) {
                    icon = getResources().getDrawable(R.drawable.marker_on);
                } else {
                    icon = getResources().getDrawable(R.drawable.marker_off);
                }

//                OverlayItem current= new OverlayItem(etatZone.getLibelle(), etatZone.getDescription(), new GeoPoint(etatZone.getLat(), etatZone.getLon()));
                OverlayItem current = new OverlayItem("", "", new GeoPoint(etatZone.getLat(), etatZone.getLon()));
                current.setMarker(icon);
                items.add(current);

            }

            // ajout des marker clickable
            ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,
                    new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                        @Override
                        public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
//                            item.setMarkerHotspot(OverlayItem.HotspotPlace.NONE);
                            //affichage de resultDialog
                            showEtatZoneResult(zones.get(index));
                            return true;
                        }

                        @Override
                        public boolean onItemLongPress(final int index, final OverlayItem item) {
//                            item.setMarkerHotspot(OverlayItem.HotspotPlace.NONE);
                            //affichage de resultDialog
                            showEtatZoneResult(zones.get(index));
                            return false;
                        }
                    }, new DefaultResourceProxyImpl(cxt));
            mOverlay.setFocusItemsOnTap(true);

            mapV.getOverlays().add(mOverlay);

        }

    }


    /**
     * Méthode permettant d'afficher le dialog de résultat correspondant a l'étatZone
     *
     * @param etatZone
     */
    private void showEtatZoneResult(EtatZone etatZone) {
        AlertDialog.Builder builder = new AlertDialog.Builder(cxt, R.style.AppCompatAlertDialogStyle);
        View dialogView = LayoutInflater.from(cxt).inflate(R.layout.show_etatzone_result_dialog, null);

        ImageView etatImgV = (ImageView) dialogView.findViewById(R.id.etatImgV);
        TextView descriptionTxtV = (TextView) dialogView.findViewById(R.id.descriptionTxtV);

        switch (etatZone.getEtatDescription()) {
            case EtatZone.ETAT_SURREMENT_PAS:
                etatImgV.setImageResource(R.drawable.ic_surement_pas);
                break;
            case EtatZone.ETAT_PROBABLEMENT_PAS:
                etatImgV.setImageResource(R.drawable.ic_probablement_pas);
                break;
            case EtatZone.ETAT_PROBABLEMENT_A:
                etatImgV.setImageResource(R.drawable.ic_probablement_a);
                break;
            case EtatZone.ETAT_SURREMENT_A:
                etatImgV.setImageResource(R.drawable.ic_surement_a);
                break;
            default:
                etatImgV.setImageResource(R.drawable.ic_not_enought_info);
        }

        String libelle = etatZone.getLibelle();
        SpannableString span1 = new SpannableString(libelle);
        span1.setSpan(new StyleSpan(Typeface.BOLD), 0, libelle.length(), 0);

        descriptionTxtV.setText(TextUtils.concat(etatZone.getDescription(), " sur ", span1));

        builder.setView(dialogView);
        builder.create().show();
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
    }

    private void checkLocationPermissionIfNeeded() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ACCESS_FINE_LOCATION);
        } else {
            showAskPowerDialog();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showAskPowerDialog();
                } else {
                    //User deny access
                }

                break;
        }
    }

}
