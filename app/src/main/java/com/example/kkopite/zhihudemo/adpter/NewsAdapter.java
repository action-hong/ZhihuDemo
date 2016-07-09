package com.example.kkopite.zhihudemo.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.kkopite.zhihudemo.R;
import com.example.kkopite.zhihudemo.db.NewsListDB;
import com.example.kkopite.zhihudemo.model.NewsBean;
import com.example.kkopite.zhihudemo.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by forever 18 kkopite on 2016/6/26 18:46.
 */
public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected List<NewsBean> storiesBeanList;
    protected Context mContext;
    protected CardClickListener mCardClickListener;//item监听器
    protected NewsListDB db;

    protected static final int WITH_DATA = 0;//有日期的card
    protected static final int NO_DATA = 1;//没有日期的card
    protected static final int FOOT_ITEM = 2;

    public static final int PULL_LOAD_MORE = 0;//加载完毕
    public static final int LOADING_MORE = 1;//正在加载
    public static final int NOT_SHOW = 2;//当recyclerView为空时,不显示

    protected int loadStatus = 0;

    public NewsAdapter(List<NewsBean> storiesBeanList, Context mContext) {
        this.storiesBeanList = storiesBeanList;
        this.mContext = mContext;
        db = NewsListDB.getInstance(mContext);
    }

    public void onRefreshList(List<NewsBean> list) {
        addNews(list);
        notifyDataSetChanged();
    }

    public void clearAll() {
        this.storiesBeanList = new ArrayList<>();
        notifyDataSetChanged();
    }

    /**
     * 添加新数据到list中
     *
     * @param list 添加数据
     */
    protected void addNews(List<NewsBean> list) {
        this.storiesBeanList.addAll(list);
    }

    public void addNewsInFront(List<NewsBean> list) {
        //将新list插在前面
        List<NewsBean> mList = storiesBeanList;
        storiesBeanList = list;
        storiesBeanList.addAll(mList);
        notifyDataSetChanged();
    }

    public void setCardClickListener(CardClickListener mCardClickListener) {
        this.mCardClickListener = mCardClickListener;
    }

    public List<NewsBean> getStoriesBeanList() {
        return this.storiesBeanList;
    }

    public void setLoadStatus(int status) {
        this.loadStatus = status;
        notifyItemChanged(storiesBeanList.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final Context context = parent.getContext();
        LayoutInflater mInflater = LayoutInflater.from(context);
        if (viewType == WITH_DATA) {
            return new DateCarViewHolder(mInflater.inflate(R.layout.item_with_data, null), mCardClickListener);
        } else if (viewType == NO_DATA) {
            return new CardViewHolder(mInflater.inflate(R.layout.news_list_item, null), mCardClickListener);
        } else {
            return new FooterViewHolder(mInflater.inflate(R.layout.footer_layout, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof FooterViewHolder) {
            switch (loadStatus) {
                case PULL_LOAD_MORE:
                    ((FooterViewHolder) holder).text.setVisibility(View.VISIBLE);
                    ((FooterViewHolder) holder).pb.setVisibility(View.GONE);
                    break;
                case LOADING_MORE:
                    ((FooterViewHolder) holder).text.setVisibility(View.GONE);
                    ((FooterViewHolder) holder).pb.setVisibility(View.VISIBLE);
                    break;
                case NOT_SHOW:
                    ((FooterViewHolder) holder).text.setVisibility(View.GONE);
                    ((FooterViewHolder) holder).pb.setVisibility(View.GONE);
                    break;
            }

        } else {
            NewsBean bean = storiesBeanList.get(position);

            ((CardViewHolder) holder).dailyTitle.setText(bean.getTitle());
            ((CardViewHolder) holder).questionTitle.setText(bean.getTitle());

            String url = bean.getImages();

            Picasso.with(mContext).load(url).into(((CardViewHolder) holder).newsImage);

            if (db.isFavourite(bean)) {
                ((CardViewHolder) holder).overflow.setImageResource(R.drawable.ic_favorite_red_24dp1);
            } else {
                ((CardViewHolder) holder).overflow.setImageResource(R.drawable.ic_favorite_gray_24dp1);
            }

            if (holder instanceof DateCarViewHolder) {
                //每天的第一条,需要加上日期
                ((DateCarViewHolder) holder).mData.setText(Utils.getNormalDate(bean.getDate()));
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return FOOT_ITEM;
        } else if (storiesBeanList.get(position).getDate() != null) {
            return WITH_DATA;
        } else {
            return NO_DATA;
        }
    }

    @Override
    public int getItemCount() {
        return storiesBeanList.size() + 1;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView newsImage;
        public TextView questionTitle;
        public TextView dailyTitle;
        public ImageView overflow;
        public CardClickListener cardClickListener;

        public CardViewHolder(View v, CardClickListener cardClickListener) {
            super(v);
            newsImage = (ImageView) v.findViewById(R.id.thumbnail_image);
            questionTitle = (TextView) v.findViewById(R.id.question_title);
            dailyTitle = (TextView) v.findViewById(R.id.daily_title);
            overflow = (ImageView) v.findViewById(R.id.card_share_overflow);
            this.cardClickListener = cardClickListener;
            v.setOnClickListener(this);
            overflow.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (cardClickListener == null) return;
            if (view.getId() == R.id.card_share_overflow) {
                cardClickListener.onOverflowClick(getAdapterPosition());
            } else {
                cardClickListener.onContentClick(getAdapterPosition());
            }

        }
    }

    public static class DateCarViewHolder extends CardViewHolder {

        private TextView mData;

        public DateCarViewHolder(View v, CardClickListener cardClickListener) {
            super(v, cardClickListener);
            mData = (TextView) v.findViewById(R.id.date);
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {

        public TextView text;
        public ProgressBar pb;


        public FooterViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.foot_text);
            pb = (ProgressBar) itemView.findViewById(R.id.progress_view);
        }
    }


    public interface CardClickListener {

        /**
         * 点击item内容
         *
         * @param position 点击位置
         */
        void onContentClick(int position);


        /**
         * 点击overflow
         *
         * @param position 点击位置
         */
        void onOverflowClick(int position);
    }


}
