package com.koolz.kietcookery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    public SliderAdapter(Context context){
        this.context=context;
    }
    public int[] images={
            R.drawable.order,R.drawable.cashback,R.drawable.distance
    };
    public String[] heading={
            "Order Easily","Easy Payment","Social Distancing"
    };
    public String[] Description={
            "Welcome, to your one STOP destination of KIET's food courts. Take your orders while sitting at your counters. Hassle-free, crowd-free happiness on campus! Adapt to this fresh lifestyle along with us.",
            "Let's always practice hygiene along with fulfilling our cravings. Now, take easy payments for your orders through our secure payment alternatives. Debit/Credit card or e-Wallet payment is encouraged to lessen contact.",
            "Our objective is to provide services with the satisfactory maintenance of physical distance among customers and sellers. This is a complete trouble-free and limitless service for everyone."
    }
            ;
    @Override
    public int getCount() {
        return Description.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==(ConstraintLayout) object;

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater =(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout,container,false);
        ImageView IV =(ImageView) view.findViewById(R.id.Image);
        TextView Heading =(TextView) view.findViewById(R.id.heading);
        TextView Desc =(TextView) view.findViewById(R.id.desc);

        IV.setImageResource(images[position]);
        Heading.setText(heading[position]);
        Desc.setText(Description[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}

