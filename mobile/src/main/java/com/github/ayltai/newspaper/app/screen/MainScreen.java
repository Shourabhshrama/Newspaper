package com.github.ayltai.newspaper.app.screen;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.google.auto.value.AutoValue;

import com.github.ayltai.newspaper.R;
import com.github.ayltai.newspaper.util.Irrelevant;
import com.github.ayltai.newspaper.widget.Screen;
import com.jakewharton.rxbinding2.support.v4.view.RxViewPager;
import com.jakewharton.rxbinding2.view.RxView;

import flow.ClassKey;
import io.reactivex.Flowable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

public final class MainScreen extends Screen implements MainPresenter.View {
    @AutoValue
    public abstract static class Key extends ClassKey implements Parcelable {
        static MainScreen.Key create() {
            return new AutoValue_MainScreen_Key();
        }
    }

    public static final MainScreen.Key KEY = MainScreen.Key.create();

    //region Subscriptions

    private final FlowableProcessor<Irrelevant> goTopActions   = PublishProcessor.create();
    private final FlowableProcessor<Irrelevant> refreshActions = PublishProcessor.create();
    private final FlowableProcessor<Irrelevant> filterActions  = PublishProcessor.create();
    private final FlowableProcessor<Integer>    pageSelections = PublishProcessor.create();

    //endregion

    //region Components

    private Toolbar              toolbar;
    private FloatingActionButton goTopAction;
    private FloatingActionButton refreshAction;
    private FloatingActionButton filterAction;
    private FloatingActionButton moreAction;
    private TabLayout            tabLayout;
    private ViewPager            viewPager;

    //endregion

    private MainAdapter adapter;
    private boolean     isMoreActionsShown;

    //region Constructors

    public MainScreen(@NonNull final Context context) {
        super(context);
        this.init();
    }

    public MainScreen(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public MainScreen(@NonNull final Context context, @Nullable final AttributeSet attrs, @AttrRes final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public MainScreen(@NonNull final Context context, @Nullable final AttributeSet attrs, @AttrRes final int defStyleAttr, @StyleRes final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init();
    }

    //endregion

    //region Events

    @NonNull
    @Override
    public Flowable<Irrelevant> goTopActions() {
        return this.goTopActions;
    }

    @NonNull
    @Override
    public Flowable<Irrelevant> refreshActions() {
        return this.refreshActions;
    }

    @NonNull
    @Override
    public Flowable<Irrelevant> filterActions() {
        return this.filterActions;
    }

    @NonNull
    @Override
    public Flowable<Integer> pageSelections() {
        return this.pageSelections;
    }

    //endregion

    @Override
    protected void onAttachedToWindow() {
        if (this.isFirstTimeAttachment) {
            this.manageDisposable(RxView.clicks(this.moreAction).subscribe(irrelevant -> {
                if (this.isMoreActionsShown) {
                    this.hideMoreActions();
                } else {
                    this.showMoreActions();
                }
            }));

            this.manageDisposable(RxView.clicks(this.goTopAction).subscribe(irrelevant -> {
                this.hideMoreActions();

                this.goTopActions.onNext(Irrelevant.INSTANCE);
            }));

            this.manageDisposable(RxView.clicks(this.refreshAction).subscribe(irrelevant -> {
                this.hideMoreActions();

                this.refreshActions.onNext(Irrelevant.INSTANCE);
            }));

            this.manageDisposable(RxView.clicks(this.filterAction).subscribe(irrelevant -> {
                this.hideMoreActions();

                this.filterActions.onNext(Irrelevant.INSTANCE);
            }));

            this.adapter = new MainAdapter(this.getContext());

            final LifecycleOwner lifecycleOwner = this.getLifecycleOwner();
            if (lifecycleOwner != null) lifecycleOwner.getLifecycle().addObserver(this.adapter);

            this.viewPager.setAdapter(this.adapter);
            this.manageDisposable(RxViewPager.pageSelections(this.viewPager).subscribe(this.pageSelections::onNext));

            this.pageSelections.onNext(0);
        }

        super.onAttachedToWindow();
    }

    private void init() {
        final View view = LayoutInflater.from(this.getContext()).inflate(R.layout.screen_main, this, true);

        this.toolbar       = view.findViewById(R.id.toolbar);
        this.goTopAction   = view.findViewById(R.id.action_go_top);
        this.refreshAction = view.findViewById(R.id.action_refresh);
        this.filterAction  = view.findViewById(R.id.action_filter);
        this.moreAction    = view.findViewById(R.id.action_more);
        this.tabLayout     = view.findViewById(R.id.tabLayout);
        this.viewPager     = view.findViewById(R.id.viewPager);

        this.toolbar.inflateMenu(R.menu.main);

        this.tabLayout.setupWithViewPager(this.viewPager, true);
    }

    private void showMoreActions() {
        this.isMoreActionsShown = true;

        this.moreAction.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.rotate_clockwise));
        this.goTopAction.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.fab_open));
        this.refreshAction.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.fab_open));
        this.filterAction.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.fab_open));
    }

    private void hideMoreActions() {
        this.isMoreActionsShown = false;

        this.moreAction.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.rotate_anti_clockwise));
        this.goTopAction.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.fab_close));
        this.refreshAction.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.fab_close));
        this.filterAction.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.fab_close));
    }
}
