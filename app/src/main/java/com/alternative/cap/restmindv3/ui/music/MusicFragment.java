package com.alternative.cap.restmindv3.ui.music;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alternative.cap.restmindv3.R;
import com.alternative.cap.restmindv3.manager.adapter.MusicAdapter;
import com.alternative.cap.restmindv3.util.MusicItem;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Random;

public class MusicFragment extends Fragment implements MusicAdapter.MediaListAdapterListener {

    private static final int REQUEST = 1;
    private ArrayList<String> nameArrayList;
    private ArrayList<String> artistArrayList;
    private ArrayList<String> uriArrayList;

    private MusicAdapter musicAdapter;
    private RecyclerView mediaListRecyclerView;

    private TextView nameMediaTv;
    private TextView artistMediaTv;
    private ImageButton playMediaBtn;
    private ImageButton pauseMediaBtn;
    private CircularImageView coverMediaCircularImageView;
    private RelativeLayout layout;

    private MediaPlayer player;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseDatabase database;
    private DatabaseReference musicRef;
    private Task<Void> tempRef;

    private boolean isSongPlay = false;

    public MusicFragment() {
    }

    public static MusicFragment newInstance() {

        Bundle args = new Bundle();

        MusicFragment fragment = new MusicFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        nameArrayList = new ArrayList<>();
        artistArrayList = new ArrayList<>();
        uriArrayList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_music, container, false);
        initInstance(rootView, savedInstanceState);
        workbench(rootView, savedInstanceState);
        return rootView;
    }

    private void initInstance(View rootView, Bundle savedInstanceState) {
        mediaListRecyclerView = rootView.findViewById(R.id.mediaListRecyclerView);

        nameMediaTv = rootView.findViewById(R.id.nameMediaTv);
        artistMediaTv = rootView.findViewById(R.id.artistMediaTv);
        playMediaBtn = rootView.findViewById(R.id.playMediaBtn);
        pauseMediaBtn = rootView.findViewById(R.id.pauseMediaBtn);
        coverMediaCircularImageView = rootView.findViewById(R.id.coverMediaCircularImageView);
        layout = rootView.findViewById(R.id.musicLayout);

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        musicRef = database.getReference().child("music");
    }

    private void workbench(View rootView, Bundle savedInstanceState) {
        checkPermissioon();
        rootView.findViewById(R.id.mediaListBackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        playMediaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player != null) {
                    player.start();
                }

                playMediaBtn.setVisibility(View.GONE);
                pauseMediaBtn.setVisibility(View.VISIBLE);
            }
        });
        pauseMediaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player != null) {
                    player.pause();
                    isSongPlay = false;
                }

                playMediaBtn.setVisibility(View.VISIBLE);
                pauseMediaBtn.setVisibility(View.GONE);
            }
        });
    }

    private void checkPermissioon() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST);
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST);
            }
        } else {
            getMusic(0);

        }
    }

    public void doStuff() {
        musicAdapter = new MusicAdapter(MusicFragment.this, nameArrayList, artistArrayList, uriArrayList);
        mediaListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mediaListRecyclerView.setAdapter(musicAdapter);
    }

    public void getMusic(int i) {
//        ContentResolver contentResolver = getActivity().getContentResolver();
//        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);
//
//        if (songCursor != null && songCursor.moveToFirst()) {
//            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
//            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
//            int songLocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
//
//            do {
//                String curentTitle = songCursor.getString(songTitle);
//                String currentArtist = songCursor.getString(songArtist);
//                String currentLocation = songCursor.getString(songLocation);
//                nameArrayList.add(curentTitle);
//                artistArrayList.add(currentArtist);
//                uriArrayList.add(currentLocation);
//            } while (songCursor.moveToNext());
//        }

        Random random = new Random();
        int x = random.nextInt(1000);
        tempRef = musicRef.child("" + i).child("temp_steam").setValue(x);

        musicRef.child("" + i).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                MusicItem item = dataSnapshot.getValue(MusicItem.class);

                Log.d("dodo", "onDataChange: " + item.link);
                nameArrayList.add(item.name);
                artistArrayList.add(item.artist);
                uriArrayList.add(item.link);

//                    player = new MediaPlayer();
//                    player.setDataSource("link");
                doStuff();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST: {
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getContext(), "Permission granted!", Toast.LENGTH_SHORT).show();

                        getMusic(0);

                    }
                } else {
                    Toast.makeText(getContext(), "No permission granted!", Toast.LENGTH_SHORT).show();
                    getFragmentManager().popBackStack();
                }
                return;
            }
        }
    }

    @Override
    public void onStop() {
        if (player != null) {
            player.pause();
            isSongPlay = false;
        }

        playMediaBtn.setVisibility(View.VISIBLE);
        pauseMediaBtn.setVisibility(View.GONE);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (player != null) {
            player.pause();
            isSongPlay = false;
        }

        playMediaBtn.setVisibility(View.VISIBLE);
        pauseMediaBtn.setVisibility(View.GONE);
        super.onDestroy();
    }

    @Override
    public void onItemClicked(String passName, String passArtist, String passUri) {
        if (!isSongPlay) {
            if (player != null) {
                player.stop();
                player.release();
            }
            player = MediaPlayer.create(getContext(), Uri.parse(passUri));
            nameMediaTv.setText(passName);
            artistMediaTv.setText(passArtist);
            playMediaBtn.setVisibility(View.GONE);
            pauseMediaBtn.setVisibility(View.VISIBLE);
            player.start();
            isSongPlay = true;
        } else {
            player.stop();
            player.release();

            player = MediaPlayer.create(getContext(), Uri.parse(passUri));
            nameMediaTv.setText(passName);
            artistMediaTv.setText(passArtist);
            playMediaBtn.setVisibility(View.GONE);
            pauseMediaBtn.setVisibility(View.VISIBLE);
            player.start();
            isSongPlay = true;
        }
        layout.setVisibility(View.VISIBLE);
    }
}