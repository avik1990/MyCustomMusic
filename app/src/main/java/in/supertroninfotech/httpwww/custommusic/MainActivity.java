package in.supertroninfotech.httpwww.custommusic;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;

import java.io.File;
import java.util.Formatter;
import java.util.Locale;

import in.supertroninfotech.httpwww.custommusic.databinding.ActivityMainBinding;

import static android.support.v4.content.FileProvider.getUriForFile;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding binding;
    private boolean isPlaying = false;
    private static final String TAG = "MainActivity";
    private Handler handler;
    //about Exoplayer
    //https://developer.android.com/guide/topics/media/exoplayer.html
    private SimpleExoPlayer exoPlayer;
    private ExoPlayer.EventListener eventListener;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initview();
        fetchfile();
    }

    private void fetchfile() {
        try {
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/song");


           /*Uri uri = RawResourceDataSource.buildRawResourceUri(R.raw.song);*/
            prepareExoPlayerFromFileUri(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepareExoPlayerFromFileUri(Uri uri) {
        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector(null),
                new DefaultLoadControl());
        exoPlayer.addListener(eventListener);

        DataSpec dataSpec = new DataSpec(uri);
        final FileDataSource fileDataSource = new FileDataSource();
        try {
            fileDataSource.open(dataSpec);
        } catch (FileDataSource.FileDataSourceException e) {
            e.printStackTrace();
        }

        DataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return fileDataSource;
            }
        };
        MediaSource audioSource = new ExtractorMediaSource(fileDataSource.getUri(),
                factory, new DefaultExtractorsFactory(), null, null);

        exoPlayer.prepare(audioSource);
        initMediaControls();
    }

    private void initMediaControls() {
        initSeekBar();
    }

    private void initSeekBar() {
        binding.seekbar.requestFocus();
        binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    // We're not interested in programmatically generated changes to
                    // the progress bar's position.
                    return;
                }

                exoPlayer.seekTo(progress * 1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.seekbar.setMax(0);
        binding.seekbar.setMax((int) exoPlayer.getDuration() / 1000);

    }


    private void initview() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.ivPlay.setOnClickListener(this);
        binding.ivNext.setOnClickListener(this);
        binding.ivPrevious.setOnClickListener(this);
        binding.ivShuffle.setOnClickListener(this);
        binding.ivRepeat.setOnClickListener(this);

        eventListener = new ExoPlayer.EventListener() {

            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case ExoPlayer.STATE_ENDED:
                        Log.i(TAG, "Playback ended!");
                        //Stop playback and return to start position
                        setPlayPause(false);
                        exoPlayer.seekTo(0);
                        break;
                    case ExoPlayer.STATE_READY:
                        /*Log.i(TAG, "ExoPlayer ready! pos: " + exoPlayer.getCurrentPosition()
                                + " max: " + stringForTime((int) exoPlayer.getDuration()));*/
                        setProgress();
                        break;
                    case ExoPlayer.STATE_BUFFERING:
                        Log.i(TAG, "Playback buffering!");
                        break;
                    case ExoPlayer.STATE_IDLE:
                        Log.i(TAG, "ExoPlayer idle!");
                        break;
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
            }

            @Override
            public void onPositionDiscontinuity() {

            }
        };
    }

    @Override
    public void onClick(View view) {
        if (view == binding.ivPlay) {
            binding.ivPlay.requestFocus();
            setPlayPause(!isPlaying);
        }
        if (view == binding.ivNext) {

        }
        if (view == binding.ivPrevious) {

        }
        if (view == binding.ivShuffle) {

        }
        if (view == binding.ivRepeat) {

        }
    }

    private void setPlayPause(boolean play) {
        isPlaying = play;
        exoPlayer.setPlayWhenReady(play);
        if (!isPlaying) {
            binding.ivPlay.setImageResource(R.drawable.ic_play);
        } else {
            setProgress();
            binding.ivPlay.setImageResource(R.drawable.ic_pause);
        }
    }

    private void setProgress() {
        binding.seekbar.setProgress(0);
        binding.seekbar.setMax((int) exoPlayer.getDuration() / 1000);
        binding.tvStart.setText(stringForTime((int) exoPlayer.getCurrentPosition()));
        binding.tvEnd.setText(stringForTime((int) exoPlayer.getDuration()));

        if (handler == null) handler = new Handler();
        //Make sure you update Seekbar on UI thread
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (exoPlayer != null && isPlaying) {
                    binding.seekbar.setMax((int) exoPlayer.getDuration() / 1000);
                    int mCurrentPosition = (int) exoPlayer.getCurrentPosition() / 1000;
                    binding.seekbar.setProgress(mCurrentPosition);
                    binding.tvStart.setText(stringForTime((int) exoPlayer.getCurrentPosition()));
                    binding.tvEnd.setText(stringForTime((int) exoPlayer.getDuration()));

                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    private String stringForTime(int timeMs) {
        StringBuilder mFormatBuilder;
        Formatter mFormatter;
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

}
