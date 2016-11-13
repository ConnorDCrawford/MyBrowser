package edu.temple.mybrowser;

/**
 * Created by connorcrawford on 11/11/16.
 */

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * A placeholder fragment containing a simple view.
 */
public class WebViewFragment extends Fragment {

    interface OnWebViewLoadListener {
        void onWebViewLoad(String url, int pageNumber);
    }

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_URL = "url";
    private static final String ARG_PAGE_NUM= "page_num";
    private OnWebViewLoadListener onWebViewLoadListener;

    public WebViewFragment() {}

    /**
     * Returns a new instance of this fragment for the given URL.
     */
    public static WebViewFragment newInstance(String URL, int pageNumber) {
        WebViewFragment fragment = new WebViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, URL);
        args.putInt(ARG_PAGE_NUM, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        this.activity = activity;
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            onWebViewLoadListener = (OnWebViewLoadListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnWebViewLoadListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_web, container, false);
        WebView webView = (WebView) rootView.findViewById(R.id.web_view);
        String url = getArguments().getString(ARG_URL);
        final int pageNumber = getArguments().getInt(ARG_PAGE_NUM);

        if (webView != null && url != null && url.length() > 0) {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebChromeClient(new WebChromeClient());
            // Load links opened from the WebView in the WebView, not in another activity
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    String url = request.getUrl().toString();
                    view.loadUrl(url);
                    onWebViewLoadListener.onWebViewLoad(url, pageNumber);
                    return true;
                }
            });

            webView.loadUrl(url);
            onWebViewLoadListener.onWebViewLoad(url, pageNumber);
        }
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }

}