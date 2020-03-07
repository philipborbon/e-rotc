package com.erotc.learning.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.erotc.learning.R;
import com.erotc.learning.dao.DictionaryEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 11/12/2018.
 */
public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {
    private List<DictionaryEntry> mResultList;
    private OnClickListener mOnClickListener;


    public interface OnClickListener {
        void onClick(View view);
    }

    public ResultAdapter(OnClickListener onClickListener) {
        this.mResultList = new ArrayList<>();
        this.mOnClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_result, parent, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( mOnClickListener != null ){
                    mOnClickListener.onClick(view);
                }
            }
        });

        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        DictionaryEntry entry = mResultList.get(position);

        if ( entry != null ){
            holder.bindTo(entry);
        } else {
            holder.clear();
        }
    }

    @Override
    public int getItemCount() {
        return mResultList.size();
    }

    public void setResultList(List<DictionaryEntry> resultList) {
        this.mResultList = resultList;
    }

    public static class ResultViewHolder extends RecyclerView.ViewHolder {
        private TextView mTagalog;
        private TextView mHiligaynon;
        private TextView mIlocano;

        public ResultViewHolder(View view){
            super(view);

            mTagalog = view.findViewById(R.id.tagalog);
            mHiligaynon = view.findViewById(R.id.hiligaynon);
            mIlocano = view.findViewById(R.id.ilocano);
        }

        public void bindTo(DictionaryEntry entry){
            mTagalog.setText(entry.getTagalog());
            mHiligaynon.setText(entry.getHiligaynon());
            mIlocano.setText(entry.getIlocano());
        }

        public void clear(){
            mTagalog.setText("");
            mHiligaynon.setText("");
            mIlocano.setText("");
        }
    }
}
