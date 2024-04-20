package com.tfgorganizadortorneos.proyecto.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.tfgorganizadortorneos.proyecto.R;

public class UFragmentTitle extends Fragment {

    public UFragmentTitle() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.u_fragment_title, container, false);
    }
}