package com.koolz.kietcookery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Intro_Activity extends AppCompatActivity {
    private ViewPager VP;
    private LinearLayout Dots;
    private TextView[] dots;
    private SliderAdapter sliderAdapter;
    private Button Next;
    private Button Previous;
    private int Page_no;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intro_);
        if(Intro_Check()){
            to_authentication();
        }
        VP=(ViewPager) findViewById(R.id.Viewer);
        Dots=(LinearLayout) findViewById(R.id.Dot);
        sliderAdapter=new SliderAdapter(this);
        VP.setAdapter(sliderAdapter);
        Dotsindicator(0);
        VP.addOnPageChangeListener(pageChangeListener);
        Next =(Button) findViewById(R.id.next);
        Previous =(Button) findViewById(R.id.previous);
        Previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                to_authentication();
                Intro_preference();
            }
        });
        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VP.setCurrentItem(Page_no+1);
            }
        });
    }

    private boolean Intro_Check() {
        SharedPreferences pref=getApplicationContext().getSharedPreferences("Mypref",MODE_PRIVATE);
        boolean Intro_checker=pref.getBoolean("IsOpenFirstTime", false);
        return Intro_checker;
    }

    private void Dotsindicator(int position){
        dots=new TextView[3];
        for(int i=0;i<3;i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226; "));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.color_background));
            Dots.addView(dots[i]);
        }
        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.colorwhite));
        }
    }
    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        @Override
        public void onPageSelected(int position) {
            Dots.removeAllViews();
            Dotsindicator(position);
            Page_no=position;
            if(position==0) {
                Previous.setText("SKIP");
                Next.setText("NEXT");
                Previous.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        to_authentication();
                        Intro_preference();
                    }
                });
            }
            else if(position==1){
                Previous.setText("PREV");
                Previous.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VP.setCurrentItem(Page_no-1);
                    }
                });

                Next.setText("NEXT");
                Next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VP.setCurrentItem(Page_no+1);
                    }
                });
            }
            else if(position==2){
                Previous.setText("PREV");
                Next.setText("FINISH");
                Next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        to_authentication();
                        Intro_preference();
                    }
                });
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    private void to_authentication(){
        Intent i= new Intent(Intro_Activity.this , Authentication.class);
        startActivity(i);
        finish();
    }
    private void Intro_preference(){
        SharedPreferences Spref=getApplicationContext().getSharedPreferences("Mypref",MODE_PRIVATE);
        SharedPreferences.Editor editor=Spref.edit();
        editor.putBoolean("IsOpenFirstTime",true);
        editor.commit();
    }
}