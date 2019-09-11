package com.alternative.cap.restmindv3.manager.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alternative.cap.restmindv3.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class MediaListAdapter extends RecyclerView.Adapter<MediaListAdapter.MediaListViewHolder> {

    MediaListAdapterListener listener;
    private ArrayList<String> nameMediaList;
    private ArrayList<String> artistMediaList;
    private ArrayList<String> uriMediaList;

    public MediaListAdapter(MediaListAdapterListener passingListener,ArrayList<String> nameMediaList, ArrayList<String> artistMediaList, ArrayList<String> uriMediaList) {
        this.listener = passingListener;

        this.nameMediaList = nameMediaList;
        this.artistMediaList = artistMediaList;
        this.uriMediaList = uriMediaList;
    }

    @Override
    public MediaListAdapter.MediaListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media_list, parent, false);
        return new MediaListViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaListAdapter.MediaListViewHolder holder, int position) {
       if (position < nameMediaList.size()) {
           holder.nameMediaListTv.setText(nameMediaList.get(position));
           holder.nameArtistMediaListTv.setText(artistMediaList.get(position));
           holder.mediaItem.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    listener.onItemClicked(nameMediaList.get(position),artistMediaList.get(position),uriMediaList.get(position));
               }
           });
       }else{
           holder.mediaItem.setVisibility(View.GONE);
       }
    }

    @Override
    public int getItemCount() {
        return nameMediaList.size()+1;
    }

    public class MediaListViewHolder extends RecyclerView.ViewHolder {

        private View mediaItem;
        private CircularImageView mediaListCover;
        private TextView nameMediaListTv;
        private TextView nameArtistMediaListTv;

        public MediaListViewHolder(@NonNull View itemView) {
            super(itemView);
            mediaItem = itemView;
            mediaListCover = mediaItem.findViewById(R.id.mediaListCover);
            nameMediaListTv = mediaItem.findViewById(R.id.nameMediaListTv);
            nameArtistMediaListTv = mediaItem.findViewById(R.id.nameArtistMediaListTv);
        }
    }

    public interface MediaListAdapterListener{
        void onItemClicked(String passName, String passArtist, String passUri);
    }
}
