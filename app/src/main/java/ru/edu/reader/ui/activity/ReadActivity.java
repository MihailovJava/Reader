package ru.edu.reader.ui.activity;

import ru.edu.reader.R;
import ru.edu.reader.ui.fragments.PageFragment;
import ru.edu.reader.util.EBookParser;
import ru.edu.reader.util.SystemUiHider;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class ReadActivity extends FragmentActivity {

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
    private static final String PATH_TO_FILE = "path_to_file";
    private static final String CURRENT_PAGE = "current_page";

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;
    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    String pathToFile;
    TextView progressText;
    SeekBar seekBar;
    EBookParser book;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_read);
        preferences = getPreferences(MODE_PRIVATE);
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            pathToFile = intent.getExtras().getString(getString(R.string.file_name));
            if (!pathToFile.equals(preferences.getString(PATH_TO_FILE, ""))) {
                SharedPreferences.Editor ed = preferences.edit();
                ed.putString(PATH_TO_FILE, pathToFile);
                ed.putInt(CURRENT_PAGE, 0);
                ed.commit();
            }
        }
        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.viewPager);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        // findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOnPageChangeListener(pageChangeListener);
        progressText = (TextView) findViewById(R.id.progress_text);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        loadBook();
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
    }

    private void loadBook(){
        Display display = getWindowManager().getDefaultDisplay();
        final TypedArray styledAttributes = this.getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize });
        int mActionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        String pathToFile = preferences.getString(PATH_TO_FILE, "");
        if (pathToFile.equals("")){
            SharedPreferences.Editor ed = preferences.edit();
            ed.putInt(CURRENT_PAGE, 0);
            ed.commit();
            pathToFile = "file:///android_asset/promo.fb2";
            StringBuilder buf=new StringBuilder();
            InputStream json;
            try {
                json = getAssets().open("promo.fb2");
                BufferedReader in=
                        new BufferedReader(new InputStreamReader(json, "UTF-8"));
                String str;

                while ((str=in.readLine()) != null) {
                    buf.append(str);
                }

                in.close();
                pathToFile = "/sdcard/promo.fb2";
                File file = new File(pathToFile);
                FileOutputStream stream = new FileOutputStream(file);
                OutputStreamWriter myOutWriter =
                        new OutputStreamWriter(stream);
                myOutWriter.append(buf.toString());
                myOutWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        book = new EBookParser(new File(pathToFile),
                display.getWidth(),display.getHeight()-mActionBarSize);
        seekBar.setMax(book.getPageCount()-1);

        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),book);
        viewPager.removeAllViews();
        viewPager.setAdapter(null);
        viewPager.invalidate();
        pagerAdapter.notifyDataSetChanged();
        viewPager.setAdapter(pagerAdapter);
        setBookProgress(preferences.getInt(CURRENT_PAGE, 0));
    }

    public void setBookProgress(int pageIndex){
        SharedPreferences.Editor ed = preferences.edit();
        ed.putInt(CURRENT_PAGE, pageIndex);
        ed.commit();
        viewPager.setCurrentItem(pageIndex);
        progressText.setText((pageIndex+1)+"/"+book.getPageCount());
        seekBar.setProgress(pageIndex);
    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            setBookProgress(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setBookProgress(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
        EBookParser book;

        public MyFragmentPagerAdapter(FragmentManager fm, EBookParser book) {
            super(fm);
            this.book = book;
        }

        @Override
        public Fragment getItem(int position) {
            PageFragment pageFragment = PageFragment.newInstance(position);
            pageFragment.setPageText(book.getPage(position));
            return pageFragment;
        }

        @Override
        public int getCount() {
            return book.getPageCount();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        loadBook();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_read,menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.folder_action) {
            Intent intent = new Intent(this,OpenFileActivity.class);
            startActivity(intent);
        }
        return true;
    }
}
