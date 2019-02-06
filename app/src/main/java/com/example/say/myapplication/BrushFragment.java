package com.example.say.myapplication;


import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import com.example.say.myapplication.Interfaces.BrushFragmentListener;
import com.example.say.myapplication.adapter.ColorAdapter;

import java.util.ArrayList;
import java.util.List;


public class BrushFragment extends BottomSheetDialogFragment implements ColorAdapter.ColorAdapterListner {

    SeekBar seekBar_brush_size, seekBar_opacity_size;
    RecyclerView recycler_color;
    ToggleButton btn_brush_state;
    ColorAdapter colorAdapter;


    BrushFragmentListener listener;

     static BrushFragment instance;

    public static BrushFragment getInstance(){
        if (instance==null)
            instance = new BrushFragment();
        return instance;
    }

    public void setListener(BrushFragmentListener listener) {
        this.listener = listener;
    }

    public BrushFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_brush, container, false);

        seekBar_brush_size = (SeekBar)view.findViewById(R.id.seekbar_brush_size);
        seekBar_opacity_size = (SeekBar)view.findViewById(R.id.seekbar_opacity);
        btn_brush_state = (ToggleButton) view.findViewById(R.id.btn_brush_state);
        recycler_color = (RecyclerView)view.findViewById(R.id.recyclerview_color);
        recycler_color.setHasFixedSize(true);
        recycler_color.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        colorAdapter = new ColorAdapter(getContext(),this);
        recycler_color.setAdapter(colorAdapter);


        //Events
        seekBar_opacity_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                listener.onBrushOpacityChangedListener(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar_brush_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                listener.onBrushSizeChangedListener(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        btn_brush_state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                listener.onBrushStateChangedListener(isChecked);
            }
        });



        return view;
    }



    @Override
    public void onColorSelected(int color) {
        listener.onBrushColorChangedListener(color);
    }
}
