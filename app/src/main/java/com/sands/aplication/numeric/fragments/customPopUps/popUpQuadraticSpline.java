package com.sands.aplication.numeric.fragments.customPopUps;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.sands.aplication.numeric.R;

public class popUpQuadraticSpline extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_quadratic_spline);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width * 0.97), (int) (height * 0.32));

    }

}

//                 android:text="This method generates a continuous polynomial function defined by parts of degree almost 1.  The input points must belong to the polynomial P. But, there no exits the first derivate in the connections points. "
