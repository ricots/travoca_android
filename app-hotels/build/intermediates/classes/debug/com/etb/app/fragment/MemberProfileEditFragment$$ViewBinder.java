// Generated code from Butter Knife. Do not modify!
package com.etb.app.fragment;

import android.view.View;

import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MemberProfileEditFragment$$ViewBinder<T extends com.etb.app.fragment.MemberProfileEditFragment> implements ViewBinder<T> {
    @Override
    public void bind(final Finder finder, final T target, Object source) {
        View view;
        view = finder.findRequiredView(source, 2131624216, "field 'mFirstName'");
        target.mFirstName = finder.castView(view, 2131624216, "field 'mFirstName'");
        view = finder.findRequiredView(source, 2131624217, "field 'mLastName'");
        target.mLastName = finder.castView(view, 2131624217, "field 'mLastName'");
        view = finder.findRequiredView(source, 2131624218, "field 'mDateOfBirth'");
        target.mDateOfBirth = finder.castView(view, 2131624218, "field 'mDateOfBirth'");
        view = finder.findRequiredView(source, 2131624219, "field 'mGenderFemale'");
        target.mGenderFemale = finder.castView(view, 2131624219, "field 'mGenderFemale'");
        view = finder.findRequiredView(source, 2131624220, "field 'mGenderMale'");
        target.mGenderMale = finder.castView(view, 2131624220, "field 'mGenderMale'");
        view = finder.findRequiredView(source, 2131624221, "field 'mGenderOther'");
        target.mGenderOther = finder.castView(view, 2131624221, "field 'mGenderOther'");
        view = finder.findRequiredView(source, 2131624144, "field 'mSaveButton'");
        target.mSaveButton = finder.castView(view, 2131624144, "field 'mSaveButton'");
    }

    @Override
    public void unbind(T target) {
        target.mFirstName = null;
        target.mLastName = null;
        target.mDateOfBirth = null;
        target.mGenderFemale = null;
        target.mGenderMale = null;
        target.mGenderOther = null;
        target.mSaveButton = null;
    }
}
