package org.benindevelopers.ina.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.benindevelopers.ina.R;
import org.benindevelopers.ina.utils.MyUtils;
import org.benindevelopers.power.PowerConnectionReceiver;
import org.benindevelopers.webservices.WebService;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private PowerConnectionReceiver batteryReceiver;
    private MaterialDialog materialDialog;
    private Intent intent;

    private OnFragmentInteractionListener mListener;
    private WebService retrofit;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
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

    @Override
    public void onStart() {
        super.onStart();

    }

    private void dyPower(){
        //just for git
        materialDialog = new MaterialDialog(getContext());
        materialDialog.setMessage("Avez vous le courant dans votre zone?");
        materialDialog.setPositiveButton("Oui", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // envoie
                envoieEtatCourant(true);
                materialDialog.dismiss();
            }
        });
        materialDialog.setNegativeButton("Non", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                envoieEtatCourant(false);
                materialDialog.dismiss();
            }
        });
        materialDialog.setCanceledOnTouchOutside(false);
        materialDialog.show();
    }

    private void envoieEtatCourant(boolean siCourant){
        retrofit = MyUtils.getInstance().getWebServiceManager();

//        call = retrofit.renseignerEtatCourant(
//                phoneId,
//                etat,
//                lat,
//                lon
//        );
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
        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
    }
}
