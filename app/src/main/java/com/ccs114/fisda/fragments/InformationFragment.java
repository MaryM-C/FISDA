package com.ccs114.fisda.fragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;
import com.ccs114.fisda.manager.FishDataManager;
import com.ccs114.fisda.R;
//import com.ccs114.fisda.utils.FishInfoHelper;
import com.ccs114.fisda.utils.FishInfoHelper;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import com.ccs114.fisda.databinding.InformationLayoutBinding;
import com.ccs114.fisda.models.Fish;
import com.codebyashish.autoimageslider.Enums.ImageAnimationTypes;
import com.codebyashish.autoimageslider.Enums.ImageScaleType;
import com.codebyashish.autoimageslider.ExceptionsClass;
import com.codebyashish.autoimageslider.Models.ImageSlidesModel;


public class InformationFragment extends AppCompatActivity{
    InformationLayoutBinding bindData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindData = DataBindingUtil.setContentView(this, R.layout.information_layout);
        String commonName = getIntent().getStringExtra("commonname");

        FishInfoHelper fishInfoHelper = new FishInfoHelper();
        fishInfoHelper.displayAllInfo(this, bindData, commonName);

        bindData.btnBack.setOnClickListener(view1 -> finish());
        bindData.scrlInfo.btnMoreInfo.setOnClickListener(view1-> openDialog());
        bindData.scrlInfo.btnMoreInfo2.setOnClickListener(view1-> openOptimumDialog());
        bindData.scrlInfo.btnMoreInfo3.setOnClickListener(view1 -> openMaximumDialog());

    }
    private void openDialog() {
        MoreInfoDialog dialogFragment = new MoreInfoDialog();
        dialogFragment.show(getFragmentManager(), "More Info");


    }
    private void openOptimumDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.optimal_size)
                .setMessage(R.string.optimal_size_text)
                .setPositiveButton(R.string.close, ((dialogInterface, i) -> {

                }));
        builder.create();
        builder.show();
    }

    private void openMaximumDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.maximum_size)
                .setMessage(R.string.maximum_size_text)
                .setPositiveButton(R.string.close, ((dialogInterface, i) -> {

                }));
        builder.create();
        builder.show();
    }
}