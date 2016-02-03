// Generated code from Butter Knife. Do not modify!
package com.etb.app.adapter;

import android.view.View;

import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class RoomViewHolder$$ViewBinder<T extends com.etb.app.adapter.RoomViewHolder> implements ViewBinder<T> {
    @Override
    public void bind(final Finder finder, final T target, Object source) {
        View view;
        view = finder.findRequiredView(source, 16908310, "field 'mTitle'");
        target.mTitle = finder.castView(view, 16908310, "field 'mTitle'");
        view = finder.findRequiredView(source, 2131624111, "field 'mPrice'");
        target.mPrice = finder.castView(view, 2131624111, "field 'mPrice'");
        view = finder.findRequiredView(source, 2131624203, "field 'mTriangle'");
        target.mTriangle = finder.castView(view, 2131624203, "field 'mTriangle'");
        view = finder.findRequiredView(source, 2131624110, "field 'mBaseRate'");
        target.mBaseRate = finder.castView(view, 2131624110, "field 'mBaseRate'");
        view = finder.findRequiredView(source, 2131624204, "field 'mDiscount'");
        target.mDiscount = finder.castView(view, 2131624204, "field 'mDiscount'");
        view = finder.findRequiredView(source, 2131624107, "field 'mExtra'");
        target.mExtra = finder.castView(view, 2131624107, "field 'mExtra'");
        view = finder.findRequiredView(source, 2131624108, "field 'mRefundable'");
        target.mRefundable = finder.castView(view, 2131624108, "field 'mRefundable'");
        view = finder.findOptionalView(source, 2131624300, null);
        target.mMaxGuest = finder.castView(view, 2131624300, "field 'mMaxGuest'");
        view = finder.findRequiredView(source, 2131624171, "field 'mBookButton'");
        target.mBookButton = finder.castView(view, 2131624171, "field 'mBookButton'");
    }

    @Override
    public void unbind(T target) {
        target.mTitle = null;
        target.mPrice = null;
        target.mTriangle = null;
        target.mBaseRate = null;
        target.mDiscount = null;
        target.mExtra = null;
        target.mRefundable = null;
        target.mMaxGuest = null;
        target.mBookButton = null;
    }
}
