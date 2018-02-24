package com.test.retrofit.utils;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import com.test.retrofit.R;
import com.test.retrofit.adapter.UserAdapter;

/**
 * Created by lcom151-one on 2/22/2018.
 */

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {
    private RecyclerItemTouchHelperListener listener;

    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.UP|ItemTouchHelper.DOWN,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if(viewHolder!=null){
            final View foregroundView=((UserAdapter.UserHolder) viewHolder).itemView;
            final View v=foregroundView.findViewById(R.id.ll_show_detail);
            getDefaultUIUtil().onSelected(v);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        final View foregroundView=((UserAdapter.UserHolder) viewHolder).itemView;
        final View v=foregroundView.findViewById(R.id.ll_show_detail);
        getDefaultUIUtil().onDrawOver(c, recyclerView, v, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final View foregroundView = ((UserAdapter.UserHolder) viewHolder).itemView;
        final View v=foregroundView.findViewById(R.id.ll_show_detail);
        getDefaultUIUtil().clearView(v);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((UserAdapter.UserHolder) viewHolder).itemView;
        final View v=foregroundView.findViewById(R.id.ll_show_detail);
        getDefaultUIUtil().onDraw(c, recyclerView, v, dX/3, dY/3, actionState, isCurrentlyActive);
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }



    public interface RecyclerItemTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}
