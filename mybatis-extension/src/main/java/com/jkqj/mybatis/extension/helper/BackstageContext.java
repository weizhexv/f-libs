package com.jkqj.mybatis.extension.helper;

import com.jkqj.mybatis.extension.annotations.AutoFillBackstage;
import com.jkqj.mybatis.extension.backstage.AuoBackstageProvider;

public class BackstageContext {
    public final AutoFillBackstage autoFillBackstage;
    public final AuoBackstageProvider auoBackstageProvider;

    public BackstageContext(AutoFillBackstage autoFillBackstage, AuoBackstageProvider auoBackstageProvider) {
        this.autoFillBackstage = autoFillBackstage;
        this.auoBackstageProvider = auoBackstageProvider;
    }

    public boolean canAutoBackstage() {
        return auoBackstageProvider != null && autoFillBackstage != null;
    }
}
