package com.example.journalapp.ui;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class MyRecyclerScroll extends RecyclerView.OnScrollListener {

    int scrollDistance=0;
    boolean isVisible=true;
    static final float MINIMUM = 25;

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (isVisible && scrollDistance>MINIMUM){

            hide();
            scrollDistance=0;
            isVisible=false;
        }
        else if (!isVisible && scrollDistance<-MINIMUM){
            show();
            scrollDistance=0;
            isVisible=true;
        }

        if ((isVisible&&dy>0)||(!isVisible&&dy<0)){
            scrollDistance=scrollDistance+dy;
        }


    }

    public abstract void show();
    protected abstract void hide();
}
