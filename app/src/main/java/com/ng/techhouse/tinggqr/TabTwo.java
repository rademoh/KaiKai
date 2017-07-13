package com.ng.techhouse.tinggqr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ng.techhouse.tinggqr.helper.DBhelper;
import com.ng.techhouse.tinggqr.model.AirtimeBeneficiaryPojo;
import com.ng.techhouse.tinggqr.util.M;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rabiu on 02/04/2017.
 */



public class TabTwo extends Fragment implements LoaderManager.LoaderCallbacks<List<AirtimeBeneficiaryPojo>> {

    ListView lvbeneficiary;
    DBhelper controller = new DBhelper(getActivity());
    BeneficiaryAdapter beneficiaryAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.tabtwo, container, false);
        lvbeneficiary=(ListView)rootView.findViewById(R.id.lvbeneficiary);

        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
       // getBeneficiaryList();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {

        getBeneficiaryList();
      //  M.showToastS(getActivity(), "start");
        super.onStart();
    }

    @Override
    public void onStop() {
       // M.showToastS(getActivity(), "stop");
        super.onStop();
    }

    private void getBeneficiaryList() {
      //  getLoaderManager().restartLoader(0, null, this);
        beneficiaryAdapter = new BeneficiaryAdapter(getActivity(), new ArrayList<AirtimeBeneficiaryPojo>());
        lvbeneficiary.setAdapter(beneficiaryAdapter);
        getActivity().getSupportLoaderManager().initLoader(1, null,this ).forceLoad();
    }

    @Override
    public Loader<List<AirtimeBeneficiaryPojo>> onCreateLoader(int id, Bundle args) {
        return new BeneficiaryLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<AirtimeBeneficiaryPojo>> loader, List<AirtimeBeneficiaryPojo> data) {
        beneficiaryAdapter.setBeneficiary(data);
    }

    @Override
    public void onLoaderReset(Loader<List<AirtimeBeneficiaryPojo>> loader) {
        beneficiaryAdapter.setBeneficiary(new ArrayList<AirtimeBeneficiaryPojo>());
    }

}
