package dizzy.only.state;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Dizzy
 * 2019/6/6 16:16
 * 简介：OnlyStateView
 */
public class OnlyStateView extends FrameLayout {

    public static final int LOADING = 0;
    public static final int CONTENT = 1;
    public static final int EMPTY = 2;
    public static final int ERROR = 3;
    private int mShowState = -1;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ViewGroup.LayoutParams mLayoutParams;
    private View mLoadingView;
    private View mContentView;
    private View mEmptyView;
    private View mErrorView;
    private OnFocusListener mOnFocusListener;
    private OnEmptyListener mOnEmptyListener;
    private OnErrorListener mOnErrorListener;

    public OnlyStateView(Context context) {
        this(context, null);
    }

    public OnlyStateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OnlyStateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mLayoutParams = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    public void setLoadingView(int loadingViewId) {
        if (mLoadingView == null && loadingViewId != 0) {
            mLoadingView = mLayoutInflater.inflate(loadingViewId, null);
            mLoadingView.setVisibility(View.GONE);
            addView(mLoadingView, mLayoutParams);
        }
    }

    public void setContentView(int contentViewId) {
        if (mContentView == null && contentViewId != 0) {
            mContentView = mLayoutInflater.inflate(contentViewId, null);
            mContentView.setVisibility(View.GONE);
            if (mOnFocusListener != null) {
                mContentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int height = mContentView.getHeight();
                        int width = mContentView.getWidth();
                        if (height != 0 && width != 0) {
                            ViewTreeObserver viewTreeObserver = mContentView.getViewTreeObserver();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                viewTreeObserver.removeOnGlobalLayoutListener(this);
                            } else {
                                viewTreeObserver.removeGlobalOnLayoutListener(this);
                            }
                            mOnFocusListener.onFocus();
                        }
                    }
                });
            }
            addView(mContentView, mLayoutParams);
        }
    }

    public void setEmptyView(int emptyViewId) {
        if (mEmptyView == null && emptyViewId != 0) {
            mEmptyView = mLayoutInflater.inflate(emptyViewId, null);
            mEmptyView.setVisibility(View.GONE);
            if (mOnEmptyListener != null) {
                View view = mEmptyView.findViewById(R.id.only_empty_view);
                if (view != null) {
                    view.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnEmptyListener.onEmpty();
                        }
                    });
                } else {
                    mEmptyView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnEmptyListener.onEmpty();
                        }
                    });
                }
            }
            addView(mEmptyView, mLayoutParams);
        }
    }

    public void setErrorView(int errorViewId) {
        if (mErrorView == null && errorViewId != 0) {
            mErrorView = mLayoutInflater.inflate(errorViewId, null);
            mErrorView.setVisibility(View.GONE);
            if (mOnErrorListener != null) {
                View view = mErrorView.findViewById(R.id.only_error_view);
                if (view != null) {
                    view.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mOnErrorListener.onError();
                        }
                    });
                } else {
                    mErrorView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mOnErrorListener.onError();
                        }
                    });
                }
            }
            addView(mErrorView, mLayoutParams);
        }
    }

    public void showLoading() {
        showStateView(LOADING);
    }

    public void showContent() {
        showStateView(CONTENT);
    }

    public void showEmpty() {
        showStateView(EMPTY);
    }

    public void showEmpty(int id, int stringId) {
        showEmpty(id, mContext.getString(stringId));
    }

    public void showEmpty(int id, String string) {
        View view = mEmptyView.findViewById(id);
        if (view != null) {
            if (view instanceof TextView) {
                ((TextView) view).setText(string);
            }
        }
        showStateView(EMPTY);
    }

    public void showError() {
        showStateView(ERROR);
    }

    public void showError(int id, int stringId) {
        showError(id, mContext.getString(stringId));
    }

    public void showError(int id, String string) {
        View view = mErrorView.findViewById(id);
        if (view != null) {
            if (view instanceof TextView) {
                ((TextView) view).setText(string);
            }
        }
        showStateView(ERROR);
    }

    private void showStateView(int showState) {
        if (showState == getShowState()) {
            return;
        }
        switch (showState) {
            case LOADING:
                hideAnimation(mContentView);
                hideAnimation(mEmptyView);
                hideAnimation(mErrorView);
                showAnimation(mLoadingView);
                break;
            case CONTENT:
                hideAnimation(mLoadingView);
                hideAnimation(mEmptyView);
                hideAnimation(mErrorView);
                showAnimation(mContentView);
                break;
            case EMPTY:
                hideAnimation(mLoadingView);
                hideAnimation(mContentView);
                hideAnimation(mErrorView);
                showAnimation(mEmptyView);
                break;
            case ERROR:
                hideAnimation(mLoadingView);
                hideAnimation(mContentView);
                hideAnimation(mEmptyView);
                showAnimation(mErrorView);
                break;
        }
        mShowState = showState;
    }

    public int getShowState() {
        return mShowState;
    }

    private void showAnimation(View view) {
        if (view == null) {
            return;
        }
        if (view.getVisibility() == View.VISIBLE) {
            return;
        }
        view.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in);
        animation.setDuration(mContext.getResources().getInteger(android.R.integer.config_shortAnimTime));
        view.startAnimation(animation);
    }

    private void hideAnimation(View view) {
        if (view == null) {
            return;
        }
        if (view.getVisibility() == View.GONE) {
            return;
        }
        view.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_out);
        animation.setDuration(mContext.getResources().getInteger(android.R.integer.config_shortAnimTime));
        view.startAnimation(animation);
    }

    public void setOnFocusListener(OnFocusListener onFocusListener) {
        this.mOnFocusListener = onFocusListener;
    }

    public void setOnEmptyListener(OnEmptyListener onEmptyListener) {
        this.mOnEmptyListener = onEmptyListener;
    }

    public void setOnErrorListener(OnErrorListener onErrorListener) {
        this.mOnErrorListener = onErrorListener;
    }

    public interface OnFocusListener {
        void onFocus();
    }

    public interface OnEmptyListener {
        void onEmpty();
    }

    public interface OnErrorListener {
        void onError();
    }

}
