package com.example.apple.viewpager2;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

public class MainActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    ViewPager2.OnPageChangeCallback callback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.viewpager2);

        viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        viewPager.setAdapter(new TextAdapter());

        viewPager.registerOnPageChangeCallback(callback);

    }

    class TextAdapter extends RecyclerView.Adapter<TextHolder> {

        @NonNull
        @Override
        public TextHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(MainActivity.this);
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            return new TextHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull TextHolder holder, int position) {
            holder.textView.setText(position + "");
            holder.textView.setBackgroundColor(Color.argb(255, (int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
        }

        @Override
        public int getItemCount() {
            return 100;
        }
    }

    static class TextHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public TextHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = (TextView) itemView;
        }
    }
}
