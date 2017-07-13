package com.ng.techhouse.tinggqr;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.ng.techhouse.tinggqr.helper.DBhelper;
import com.ng.techhouse.tinggqr.model.AirtimeBeneficiaryPojo;

import java.util.List;

/**
 * Created by rabiu on 03/04/2017.
 */

public class BeneficiaryLoader extends AsyncTaskLoader<List<AirtimeBeneficiaryPojo>> {

    DBhelper controller = null;

    public BeneficiaryLoader(Context context) {
        super(context);
        controller = new DBhelper(context);
    }

    @Override
    public List<AirtimeBeneficiaryPojo> loadInBackground() {
        return controller.getBeneficiaryList();
    }
}
