/*
 * Copyright (c) 2014-present, salesforce.com, inc.
 * All rights reserved.
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * - Neither the name of salesforce.com, inc. nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission of salesforce.com, inc.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.salesforce.androidsdk.reactnative.app;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.salesforce.androidsdk.mobilesync.app.MobileSyncSDKManager;
import com.salesforce.androidsdk.reactnative.bridge.MobileSyncReactBridge;
import com.salesforce.androidsdk.reactnative.bridge.SalesforceNetReactBridge;
import com.salesforce.androidsdk.reactnative.bridge.SalesforceOauthReactBridge;
import com.salesforce.androidsdk.reactnative.bridge.SmartStoreReactBridge;
import com.salesforce.androidsdk.reactnative.ui.SalesforceReactActivity;
import com.salesforce.androidsdk.ui.LoginActivity;
import com.salesforce.androidsdk.util.EventsObservable;
import com.salesforce.androidsdk.util.EventsObservable.EventType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * SDK Manager for all react native applications
 */
public class SalesforceReactSDKManager extends MobileSyncSDKManager {

    /**
     * Protected constructor.
     *
     * @param context       Application context.
     * @param mainActivity  Activity that should be launched after the login flow.
     * @param loginActivity Login activity.
     */
    protected SalesforceReactSDKManager(Context context, Class<? extends Activity> mainActivity,
                                        Class<? extends Activity> loginActivity) {
        super(context, mainActivity, loginActivity);
    }

    private static void init(Context context, Class<? extends Activity> mainActivity,
                             Class<? extends Activity> loginActivity) {
        if (INSTANCE == null) {
            INSTANCE = new SalesforceReactSDKManager(context, mainActivity, loginActivity);
        }

        // Upgrade to the latest version.
        SalesforceReactUpgradeManager.getInstance().upgrade();
        initInternal(context);
        EventsObservable.get().notifyEvent(EventType.AppCreateComplete);
    }

    /**
     * Initializes components required for this class
     * to properly function. This method should be called
     * by react native apps using the Salesforce Mobile SDK.
     *
     * @param context      Application context.
     * @param mainActivity Activity that should be launched after the login flow.
     */
    public static void initReactNative(Context context, Class<? extends Activity> mainActivity) {
        SalesforceReactSDKManager.init(context, mainActivity, LoginActivity.class);
    }

    /**
     * Initializes components required for this class
     * to properly function. This method should be called
     * by react native apps using the Salesforce Mobile SDK.
     *
     * @param context       Application context.
     * @param mainActivity  Activity that should be launched after the login flow.
     * @param loginActivity Login activity.
     * @noinspection unused
     */
    public static void initReactNative(Context context, Class<? extends Activity> mainActivity,
                                       Class<? extends Activity> loginActivity) {
        SalesforceReactSDKManager.init(context, mainActivity, loginActivity);
    }

    /**
     * Returns a singleton instance of this class.
     *
     * @return Singleton instance of SalesforceReactSDKManager.
     */
    @NonNull
    public static SalesforceReactSDKManager getInstance() {
        if (INSTANCE != null) {
            return (SalesforceReactSDKManager) INSTANCE;
        } else {
            throw new RuntimeException("Applications need to call SalesforceReactSDKManager.init() first.");
        }
    }

    @NonNull
    @Override
    public String getAppType() {
        return "ReactNative";
    }

    /**
     * Call this method when setting up ReactInstanceManager
     *
     * @return ReactPackage for this application
     */
    public ReactPackage getReactPackage() {
        return new ReactPackage() {

            @NonNull
            @Override
            public List<NativeModule> createNativeModules(
                    @NonNull ReactApplicationContext reactContext
            ) {
                List<NativeModule> modules = new ArrayList<>();
                modules.add(new SalesforceOauthReactBridge(reactContext));
                modules.add(new SalesforceNetReactBridge(reactContext));
                modules.add(new SmartStoreReactBridge(reactContext));
                modules.add(new MobileSyncReactBridge(reactContext));
                return modules;
            }

            /** @noinspection unused*/
            public List<Class<? extends JavaScriptModule>> createJSModules() {
                return Collections.emptyList();
            }

            @NonNull
            @Override
            public List<ViewManager> createViewManagers(
                    @NonNull ReactApplicationContext reactContext
            ) {
                return Collections.emptyList();
            }
        };
    }

    @NonNull
    @Override
    protected Map<String, DevActionHandler> getDevActions(
            @NonNull final Activity frontActivity
    ) {
        Map<String, DevActionHandler> devActions = super.getDevActions(frontActivity);
        devActions.put(
                "React Native Dev Support",
                () -> ((SalesforceReactActivity) frontActivity).showReactDevOptionsDialog()
        );

        return devActions;
    }
}
