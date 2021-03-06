package com.thinkpalm.happen.ui.activities.notification.channels;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.thinkpalm.happen.R;

public class ChannelsActivity extends AppCompatActivity implements ChannelsView {

    private ChannelsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channels);

		presenter = new ChannelsPresenterImpl(this);

    }
}
