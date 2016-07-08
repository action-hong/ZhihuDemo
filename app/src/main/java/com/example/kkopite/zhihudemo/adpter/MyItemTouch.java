package com.example.kkopite.zhihudemo.adpter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by forever 18 kkopite on 2016/7/8 23:46.
 */
public class MyItemTouch extends ItemTouchHelper.Callback {

    private final NewsAdapter adapter;
    private OnItemMoveListener listener;


    public MyItemTouch(NewsAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlag = ItemTouchHelper.START;

        return makeMovementFlags(dragFlag,swipeFlag);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    public void setListener(OnItemMoveListener listener) {
        this.listener = listener;
    }

    public interface OnItemMoveListener{
        void onItemDismiss(int position);

        void onItemMove(int from,int to);
    }


}
