package com.android.server.pm;

import android.content.pm.parsing.component.ParsedIntentInfo;
import android.util.ArraySet;
import android.util.Slog;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class IntentFilterVerificationState {
    public static final int STATE_UNDEFINED = 0;
    public static final int STATE_VERIFICATION_FAILURE = 3;
    public static final int STATE_VERIFICATION_PENDING = 1;
    public static final int STATE_VERIFICATION_SUCCESS = 2;
    static final String TAG = IntentFilterVerificationState.class.getName();
    private String mPackageName;
    private int mRequiredVerifierUid;
    private int mUserId;
    private ArrayList<ParsedIntentInfo> mFilters = new ArrayList<>();
    private ArraySet<String> mHosts = new ArraySet<>();
    private int mState = 0;
    private boolean mVerificationComplete = false;

    public IntentFilterVerificationState(int verifierUid, int userId, String packageName) {
        this.mRequiredVerifierUid = 0;
        this.mRequiredVerifierUid = verifierUid;
        this.mUserId = userId;
        this.mPackageName = packageName;
    }

    public void setState(int state) {
        if (state > 3 || state < 0) {
            this.mState = 0;
        } else {
            this.mState = state;
        }
    }

    public int getState() {
        return this.mState;
    }

    public void setPendingState() {
        setState(1);
    }

    public ArrayList<ParsedIntentInfo> getFilters() {
        return this.mFilters;
    }

    public boolean isVerificationComplete() {
        return this.mVerificationComplete;
    }

    public boolean isVerified() {
        return this.mVerificationComplete && this.mState == 2;
    }

    public int getUserId() {
        return this.mUserId;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String getHostsString() {
        StringBuilder sb = new StringBuilder();
        int count = this.mHosts.size();
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                sb.append(" ");
            }
            String host = this.mHosts.valueAt(i);
            if (host.startsWith("*.")) {
                host = host.substring(2);
            }
            sb.append(host);
        }
        return sb.toString();
    }

    public boolean setVerifierResponse(int callerUid, int code) {
        if (this.mRequiredVerifierUid == callerUid) {
            int state = 0;
            if (code == 1) {
                state = 2;
            } else if (code == -1) {
                state = 3;
            }
            this.mVerificationComplete = true;
            setState(state);
            return true;
        }
        String str = TAG;
        Slog.d(str, "Cannot set verifier response with callerUid:" + callerUid + " and code:" + code + " as required verifierUid is:" + this.mRequiredVerifierUid);
        return false;
    }

    public void addFilter(ParsedIntentInfo filter) {
        this.mFilters.add(filter);
        this.mHosts.addAll(filter.getHostsList());
    }
}