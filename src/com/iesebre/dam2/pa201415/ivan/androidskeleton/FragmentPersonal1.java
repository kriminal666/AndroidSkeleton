package com.iesebre.dam2.pa201415.ivan.androidskeleton;

import com.google.android.gms.plus.Plus;
import com.iesebre.dam2.pa201415.ivan.androidskeleton.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link FragmentPersonal1.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link FragmentPersonal1#newInstance} factory method to create an instance of
 * this fragment.
 * 
 */
public class FragmentPersonal1 extends Fragment implements View.OnClickListener {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;
	//We need to know where are we logged in
	private int request;

	private OnFragmentInteractionListener mListener;

	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @param param1
	 *            Parameter 1.
	 * @param param2
	 *            Parameter 2.
	 * @return A new instance of fragment FragmentPersonal1.
	 */
	// TODO: Rename and change types and number of parameters
	public static FragmentPersonal1 newInstance(String param1, String param2) {
		FragmentPersonal1 fragment = new FragmentPersonal1();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public FragmentPersonal1() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}
    //HERE WE LOAD THE LOGOUT BUTTON AND ACTIONS
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		  // Inflate the layout for this fragment
		View v =inflater.inflate(R.layout.fragment_personal1, container, false);
		//Get the button
		Button btnLogout = (Button)v.findViewById(R.id.btnLogout);
		//DONT NEED THIS RIGHT NOW
		//btnLogout.setOnClickListener(this);
		//SET INVISIBLE WE ARE GOING TO USE MENU SECTION TO LOGOUT
		btnLogout.setVisibility(View.GONE);
		
		return v;
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
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
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(Uri uri);
	}

	@Override
	public void onClick(View v) {
		/*KEEP THIS SWITCH FOR FUTURE NEW BUTTONS
		*OR FOR OTHERS IMPLEMENTATIONS
		/*switch (v.getId()) {
		case R.id.btnLogout:
	    	Intent logoutAll = new Intent();
		    getActivity().setResult(9999,logoutAll);
		    getActivity().finish(); 
		 	break;
		}*/
	}
		
	}
	
	


