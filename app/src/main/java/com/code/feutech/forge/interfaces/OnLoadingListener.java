package com.code.feutech.forge.interfaces;

public interface OnLoadingListener {
    void onHasData();
    void onNoData();
    void onNoData(String msg);
    void onNoData(int resid);
    void onLoading();
}
