package com.joelcamargo.mybakingapp;


import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.joelcamargo.mybakingapp.model.Recipe;
import com.joelcamargo.mybakingapp.model.Step;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("ConstantConditions")
public class StepInfoFragment extends Fragment implements Player.EventListener {

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.description_textView)
    TextView mDescriptionTextView;
    @SuppressWarnings("WeakerAccess")
    @Nullable
    @BindView(R.id.previous_button)
    Button mPreviousButton;
    @SuppressWarnings("WeakerAccess")
    @Nullable
    @BindView(R.id.next_button)
    Button mNextButton;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.simple_exo_player)
    SimpleExoPlayerView mSimpleExoPlayerView;
    @BindView(R.id.thumbnailIV)
    ImageView mThumnbnailIV;
    private SimpleExoPlayer mPlayer;
    private Recipe mReceivedRecipe;
    private int mClickedPosition;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private MediaSource mMediaSource;
    private StepInfoFragment mOldFragmentToHide;
    private boolean mHasVideo;
    private boolean mIsLandscape;
    private long mPlayerPosition;
    private Uri mMediaUri;


    public StepInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // inflates layout
        View view = inflater.inflate(R.layout.step_info_with_player_layout, container, false);
        ButterKnife.bind(this, view);

        // retains fragment across orientation change so it doesn't re-create the fragment on change
        setRetainInstance(true);

        // doesn't allow for views in background fragment to be clickable
        view.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        // retrieves the info needed from the bundle to inflate the views
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mReceivedRecipe = bundle.getParcelable("recipe");
            mClickedPosition = bundle.getInt("position");
            Log.d("clickedPosition: ", String.valueOf(mClickedPosition));

            updateStepInfoViews(mReceivedRecipe, mClickedPosition);

        } else {
            Toast.makeText(getContext(), "bundle was null/ error", Toast.LENGTH_SHORT).show();
        }

        // sets text on buttons and hides when necessary
        if (!MainActivity.mTwoPane) {
            assert mPreviousButton != null;
            mPreviousButton.setText(R.string.prev_step_label);
            mNextButton.setText(R.string.next_step_label);
            if (mClickedPosition == 0) {
                mPreviousButton.setVisibility(View.GONE);
            } else if (mClickedPosition == mReceivedRecipe.getSteps().size() - 1) {
                mNextButton.setVisibility(View.GONE);
            }
        }


        // make video fullscreen if current step has video and screen is landscape
        if (mHasVideo && mIsLandscape) {
            // hides views that arent needed
            mDescriptionTextView.setVisibility(View.GONE);
            if (mNextButton != null){
                mNextButton.setVisibility(View.GONE);
            }
            if (mPreviousButton !=  null) {
                mPreviousButton.setVisibility(View.GONE);
            }

            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

            // creates the new layout params  for player and sets them
            RelativeLayout.LayoutParams params =
                    (RelativeLayout.LayoutParams) mSimpleExoPlayerView.getLayoutParams();
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.topMargin = 0;
            mSimpleExoPlayerView.setLayoutParams(params);
        }


        // Creates media session used for ExoPlayer
        initializeMediaSession();


        return view;
    }

    // helper method that inputs data to views
    private void updateStepInfoViews(Recipe recipe, final int clickedPosition) {
        final ArrayList<Step> steps = (ArrayList<Step>) recipe.getSteps();
        Step currentStep = steps.get(clickedPosition);
        String stepDescription = currentStep.getDescription();

        // update boolean to tell whether there's a video/thumbnail or not
        mHasVideo = !currentStep.getVideoURL().isEmpty();
        boolean mHasThumbnail = !currentStep.getThumbnailURL().isEmpty();

        mDescriptionTextView.setText(stepDescription);

        // check if there's a video, if so add video to exoplayer; if not, hide view
        if (!currentStep.getVideoURL().isEmpty()) {
            // sets global Uri to set up player
            mSimpleExoPlayerView.setVisibility(View.VISIBLE);
            mMediaUri = Uri.parse(currentStep.getVideoURL());
            initializePlayer(mMediaUri);
        } else {
            // Hides the player in case there is no video media to display
            mSimpleExoPlayerView.setVisibility(View.GONE);
            mDescriptionTextView.setTextSize(28);
        }

        // check if there's a thumbnail or not. if so, adds image to imageview
        if (mHasThumbnail) {
            mThumnbnailIV.setVisibility(View.VISIBLE);
            String url = currentStep.getThumbnailURL();
            Picasso.with(getContext()).load(url).into(mThumnbnailIV);
        } else {
            mThumnbnailIV.setVisibility(View.GONE);
        }

        if (!MainActivity.mTwoPane) {
            mPreviousButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickedPosition != 0) {
                        releasePlayer();
                        mSimpleExoPlayerView.setVisibility(View.GONE);
                        mClickedPosition = clickedPosition - 1;
                        displayNewStepInfoFragment(mClickedPosition);
                    }
                }
            });

            mNextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickedPosition != steps.size() - 1) {
                        releasePlayer();
                        mSimpleExoPlayerView.setVisibility(View.GONE);
                        mClickedPosition = clickedPosition + 1;
                        displayNewStepInfoFragment(mClickedPosition);
                    }
                }
            });
        }


    }

    // helper method to set up simpleExoPlayer
    private void initializePlayer(Uri mediaUri) {
        if (mPlayer == null) {
            // create instance of exoPlayer with default settings
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            RenderersFactory renderers = new DefaultRenderersFactory(getContext());
            mPlayer = ExoPlayerFactory.newSimpleInstance(renderers, trackSelector, loadControl);
            // sets to recent position if orientation changed mid-session
            if (mPlayerPosition != 0) {
                mPlayer.seekTo(mPlayerPosition);
            }
            mSimpleExoPlayerView.setPlayer(mPlayer);

            // builds media source then prepares player with media
            mMediaSource = buildMediaSource(mediaUri);
            mPlayer.prepare(mMediaSource);
            mPlayer.setPlayWhenReady(true);
            mPlayer.addListener(this);
        } else {
            Log.d("initializePlayer: ", "NOT called");
        }
    }

    // method to release player and free up resources
    private void releasePlayer() {
        if (mPlayer != null) {
            mPlayerPosition = mPlayer.getCurrentPosition();
            mMediaSource.releaseSource();
            mMediaSession.release();
            mPlayer.clearVideoSurface();
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            mMediaSession.setActive(false);
        }
    }

    // helper method that builds media source
    private MediaSource buildMediaSource(Uri uri) {

        // initializes some player defaults to use in initialization of media source
        DataSource.Factory mMediaDataSourceFactory = new DefaultDataSourceFactory(getActivity().getApplicationContext(),
                Util.getUserAgent(getContext(),
                        "bakingApp"));

        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        return new ExtractorMediaSource(uri,
                mMediaDataSourceFactory,
                extractorsFactory,
                null,
                null);
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
        mMediaSession.release();
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
        mMediaSession.release();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMediaUri != null) {
            initializePlayer(mMediaUri);
        }
    }

    // EXOPLAYER listener stuff
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
        if ((playbackState == Player.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == Player.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mPlayer.getCurrentPosition(), 1f);
        }

        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    // Sets up media session
    private void initializeMediaSession() {

        mMediaSession = new MediaSessionCompat(getActivity().getApplicationContext(), "TAG");
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
        mMediaSession.setMediaButtonReceiver(null);
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mMediaSession.setPlaybackState(mStateBuilder.build());
        mMediaSession.setCallback(new mySessionCallback());
        mMediaSession.setActive(true);
    }

    // helper method that opens the step fragment for clicked step item
    private void displayNewStepInfoFragment(int clickedPosition) {

        // For some reason i was getting some lag when adding fragments so i needed to use the
        // Show/Hide methods and a timer to remove the fragment i didn't need anymore
        FragmentManager fm = getActivity().getSupportFragmentManager();
        // get current fragment that is showing in the container
        mOldFragmentToHide = (StepInfoFragment) fm.findFragmentById(R.id.mainFragmentContainer);

        // make the new fragment and set it's arguments
        StepInfoFragment mNewStepInfoFragment = new StepInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable("recipe", mReceivedRecipe);
        args.putInt("position", clickedPosition);
        mNewStepInfoFragment.setArguments(args);


        // new fragment transaction
        FragmentTransaction ft = fm.beginTransaction()
                .add(R.id.mainFragmentContainer, mNewStepInfoFragment)
                .show(mNewStepInfoFragment)
                .hide(mOldFragmentToHide);
        ft.commit();

        // timer to remove old fragment
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .remove(mOldFragmentToHide).commit();
            }
        }, 1000);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // checking the orientation of screen
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("ORIENTATION", " LANDSCAPE");

            // sets the boolean tracker used to adjust layout on orientation
            mIsLandscape = true;

        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d("ORIENTATION", " PORTRAIT");

            // sets the boolean tracker used to adjust layout on orientation
            mIsLandscape = false;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // saves recipe to inflate views on orientation change && pause/stop
        outState.putParcelable("recipe", mReceivedRecipe);
        outState.putBoolean("hasVideo", mHasVideo);
        outState.putBoolean("isLandscape", mIsLandscape);
        outState.putLong("playerPosition", mPlayerPosition);
    }

    private class mySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mPlayer.seekTo(0);
        }
    }
}