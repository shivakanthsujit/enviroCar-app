package org.envirocar.app.views.onboarding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.transition.Fade;
import androidx.transition.TransitionManager;
import androidx.viewpager.widget.ViewPager;

import android.animation.ArgbEvaluator;
import android.animation.FloatEvaluator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import org.envirocar.app.R;
import org.envirocar.app.handler.PreferencesHandler;
import org.envirocar.app.main.BaseMainActivityBottomBar;
import org.envirocar.app.views.login.SigninActivity;
import org.envirocar.app.views.login.SignupActivity;
import org.envirocar.core.logging.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OnboardingActivity extends AppCompatActivity implements OnboardingFragment4.OBButtonInterface {

    private static final Logger LOGGER = Logger.getLogger(OnboardingActivity.class);
    public static final String ONBOARDING_COMPLETE = "Onboarding_Complete";

    @BindView(R.id.onboarding_viewpager)
    protected ViewPager viewPager;

    @BindView(R.id.onboarding_viewpagertab)
    protected SmartTabLayout smartTabLayout;

    @BindView(R.id.onboarding_next)
    protected Button nextButton;

    @BindView(R.id.onboarding_skip)
    protected Button skipButton;

    @BindView(R.id.onboarding_layout_basic)
    protected ConstraintLayout onBoardingLayout;

    protected int[] colorList;
    protected OBPageAdapter obPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isOnboardingComplete = PreferencesHandler.getSharedPreferences(getApplicationContext()).getBoolean(OnboardingActivity.ONBOARDING_COMPLETE,false);
        boolean isTest = false;

        try {
            Intent intent = getIntent();
            isTest = intent.getBooleanExtra("test-call", false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LOGGER.info("Onboarding: "+ isOnboardingComplete);
        if (isOnboardingComplete && !isTest ) {
            Intent intent = new Intent(OnboardingActivity.this, BaseMainActivityBottomBar.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.onboarding_basic);
        ButterKnife.bind(this);
        int color0 = Color.WHITE;
        int color1 = Color.parseColor("#FCFFFF");
        int color2 = Color.parseColor("#FDFEFE");
        int color3 = Color.WHITE;

        colorList = new int[]{color0, color1, color2, color3};
        obPageAdapter = new OBPageAdapter(getSupportFragmentManager());

        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(obPageAdapter);
        smartTabLayout.setViewPager(viewPager);

        TransitionManager.beginDelayedTransition(onBoardingLayout, new Fade()
                                                                    .setDuration(OnboardingFragment1.animationStart));
        //The next and skip button at the bottom of each onboarding page
        nextButton.setVisibility(View.VISIBLE);
        skipButton.setVisibility(View.VISIBLE);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() < obPageAdapter.getCount()-1) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem()+1,true);
                }
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(3);
            }
        });

        smartTabLayout.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                FloatEvaluator floatEvaluator = new FloatEvaluator();
                // To hide the background image present in the first page, i.e. the blurred map
                // as you scroll from page 1 to 2
                if (position == 0 || position == 1) {
                    Float alpha = floatEvaluator.evaluate(positionOffset, 1f, 0f);
                    if(viewPager.getAdapter() != null)
                        ((OBPageAdapter) viewPager.getAdapter()).setPageBackVisibility(alpha, 0);
                }

                // To hide the background image in the final page as you scroll
                if (position == 2) {
                    Float alpha = floatEvaluator.evaluate(positionOffset, 0f, 1f);
                    if (viewPager.getAdapter() != null)
                        ((OBPageAdapter) viewPager.getAdapter()).setPageBackVisibility(alpha, 3);
                }

                // The background color of each page changes as you scroll
                // The colors smoothly transition from one to the next
                int colorUpdate;
                ArgbEvaluator evaluator = new ArgbEvaluator();
                if (position!=3) {
                    colorUpdate = (Integer) evaluator.evaluate(positionOffset, colorList[position], colorList[position + 1]);
                } else {
                    colorUpdate = (Integer) evaluator.evaluate(positionOffset, colorList[position], colorList[position]);
                }

                viewPager.setBackgroundColor(colorUpdate);
            }

            @Override
            public void onPageSelected(int position) {
                // If on the last page, hide the bottom buttons
                if (position == 3) {
                    skipButton.setVisibility(View.GONE);
                    nextButton.setVisibility(View.GONE);
                } else {
                    skipButton.setVisibility(View.VISIBLE);
                    nextButton.setVisibility(View.VISIBLE);
                }

                switch (position) {
                    case 0:
                        viewPager.setBackgroundColor(color0);
                        break;
                    case 1:
                        viewPager.setBackgroundColor(color1);
                        break;
                    case 2:
                        viewPager.setBackgroundColor(color2);
                        break;
                    case 3:
                        viewPager.setBackgroundColor(color3);
                        break;
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem()!=0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
        }
    }

    @Override
    public void signInButtonPressed() {
        PreferencesHandler.getSharedPreferences(getApplicationContext()).edit().putBoolean(ONBOARDING_COMPLETE,true).apply();
        Intent main = new Intent(OnboardingActivity.this, SigninActivity.class);
        main.putExtra("from","login");
        startActivity(main);
        finish();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    @Override
    public void signUpButtonPressed() {
        PreferencesHandler.getSharedPreferences(getApplicationContext()).edit().putBoolean(ONBOARDING_COMPLETE,true).apply();
        Intent main = new Intent(OnboardingActivity.this, SignupActivity.class);
        main.putExtra("from","register");
        startActivity(main);
        finish();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    // This is for the skip button present on the final page of Onboarding
    @Override
    public void skipButtonPressed() {
        PreferencesHandler.getSharedPreferences(getApplicationContext()).edit().putBoolean(ONBOARDING_COMPLETE,true).apply();
        Intent main = new Intent(OnboardingActivity.this, BaseMainActivityBottomBar.class);
        startActivity(main);
        finish();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }
}
