package com.chen.androidtools.acessbility;

import android.content.Context;

/**
 * Created by CHEN on 2016/12/19.
 */

public abstract class BaseAccessbilityJob implements AccessbilityJob {

    private BaseAccessibilityService service;

    @Override
    public void onCreateJob(BaseAccessibilityService service) {
        this.service = service;
    }

    public Context getContext() {
        return service.getApplicationContext();
    }

    public GrabMoneyConfig getConfig() {
        return service.getConfig();
    }

    public BaseAccessibilityService getService() {
        return service;
    }

}
