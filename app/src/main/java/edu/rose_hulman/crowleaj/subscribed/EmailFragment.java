package edu.rose_hulman.crowleaj.subscribed;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import edu.rose_hulman.crowleaj.subscribed.models.Email;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EmailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EmailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmailFragment extends android.support.v4.app.Fragment {
    private static final String ARG_PARAM = "email";

    private OnFragmentInteractionListener mListener;
    private Email mEmail;

    public EmailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EmailFragment.
     */
    public static EmailFragment newInstance(Email email) {
        EmailFragment fragment = new EmailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEmail = getArguments().getParcelable(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_email, container, false);
        WebView webview = (WebView) view.findViewById(R.id.webview);
        String url = "";
        String current = mEmail.getSubject();
        for (int i = 0; i < current.length(); i++) {
            if (current.substring(i, i + 1).equals(" ")) {
                url += "+";
            } else {
                url += current.substring(i, i + 1);
            }
        }
        Log.d("URL", url);
//        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//        intent.putExtra(SearchManager.QUERY, mEmail.getSubject());
//        startActivity(intent);
       // webview.loadUrl("www.google.com/");
        return view;
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
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onEmailCallback();
    }
}
