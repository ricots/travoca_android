// Generated code from Butter Knife. Do not modify!
package com.etb.app.fragment;

import android.view.View;

import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class LoginFragment$$ViewBinder<T extends com.etb.app.fragment.LoginFragment> implements ViewBinder<T> {
    @Override
    public void bind(final Finder finder, final T target, Object source) {
        View view;
        view = finder.findRequiredView(source, 2131624208, "field 'mFacebookLoginButton'");
        target.mFacebookLoginButton = finder.castView(view, 2131624208, "field 'mFacebookLoginButton'");
        view = finder.findRequiredView(source, 2131624209, "field 'mGoogleLoginButton'");
        target.mGoogleLoginButton = finder.castView(view, 2131624209, "field 'mGoogleLoginButton'");
        view = finder.findRequiredView(source, 2131624122, "field 'mEmailView'");
        target.mEmailView = finder.castView(view, 2131624122, "field 'mEmailView'");
        view = finder.findRequiredView(source, 2131624126, "field 'mPasswordView'");
        target.mPasswordView = finder.castView(view, 2131624126, "field 'mPasswordView'");
        view = finder.findRequiredView(source, 2131624154, "field 'mLoginButton'");
        target.mLoginButton = finder.castView(view, 2131624154, "field 'mLoginButton'");
    }

    @Override
    public void unbind(T target) {
        target.mFacebookLoginButton = null;
        target.mGoogleLoginButton = null;
        target.mEmailView = null;
        target.mPasswordView = null;
        target.mLoginButton = null;
    }
}
