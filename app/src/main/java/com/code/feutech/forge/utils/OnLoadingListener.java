package com.code.feutech.forge.utils;

public interface OnLoadingListener {
    void onHasData();
    void onNoData();
    void onNoData(String msg);
    void onNoData(int resid);
    void onLoading();
}
