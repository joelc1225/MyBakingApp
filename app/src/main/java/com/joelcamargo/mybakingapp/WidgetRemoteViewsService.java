package com.joelcamargo.mybakingapp;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by joelcamargo on 2/23/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class WidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}
