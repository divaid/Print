package com.weixing.application.inter;

import android.content.Context;

/**
 * Created by wb-cuiweixing on 2016/12/16.
 */
public interface IInitializeComponent {
    void registerInitFinishListener(IInitializeComponent.IInitFinishListener var1);

    void unregisterInitFinishListener(IInitializeComponent.IInitFinishListener var1);

    int initialize(Context var1);

    void initializeAsync(Context var1);

    int loadLibrarySync(Context var1);

    void loadLibraryAsync(Context var1);

    int loadLibrarySync(Context var1, String var2);

    void loadLibraryAsync(Context var1, String var2);

    boolean isSoValid(Context var1);

    public interface IInitFinishListener {
        void onSuccess();

        void onError();
    }
}
