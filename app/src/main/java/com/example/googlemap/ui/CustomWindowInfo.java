package com.example.googlemap.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.googlemap.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomWindowInfo implements GoogleMap.InfoWindowAdapter {
    private View view;
    private Context context;

    public CustomWindowInfo(Context context){
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.custom_info_window,null,false);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        TextView title = view.findViewById(R.id.title);
        TextView magnitude = view.findViewById(R.id.magnitude);

        title.setText(marker.getTitle());
        magnitude.setText(marker.getSnippet());
        return view;
    }


}
