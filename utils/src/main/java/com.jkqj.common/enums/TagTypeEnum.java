package com.jkqj.common.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author xuweizhe@reta-inc.com
 * @date 2022/1/5
 * @description
 */
public enum TagTypeEnum {
    //试题标签
    QUESTION(false, "试题点标签"),
    QUESTION_CUSTOMIZE(true, "试题点标签自定义"),
    //职位标签
    JOB(false, "职位标签"),
    //职位福利标签
    JOB_BENEFITS(false, "职位福利标签"),
    // 风险审核
    AUDIT_RISK(false, "风险视频标签"),
    AUDIT_RISK_CUSTOMIZE(true, "风险视频标签自定义"),
    // 拍摄质量标注
    AUDIT_QUALITY_VOICE(false, "视频声音质量标签"),
    AUDIT_QUALITY_VOICE_CUSTOMIZE(true, "视频声音质量标签自定义"),
    AUDIT_QUALITY_FRAME(false, "视频画面质量标签"),
    AUDIT_QUALITY_FRAME_CUSTOMIZE(true, "视频画面质量标签自定义"),
    AUDIT_QUALITY_OTHER(false, "视频质量其他标签"),
    AUDIT_QUALITY_OTHER_CUSTOMIZE(true, "视频质量其他标签自定义"),

    PROGRAM_LANGUAGE(false, "编程语言"),
    CODING_LANGUAGE(false, "编程模版语言"),
    //技能
    SKILL(false, "试题技能"),
    SKILL_CUSTOMIZE(true, "试题技能自定义");

    TagTypeEnum(boolean isCustomize, String description) {
        this.isCustomize = isCustomize;
        this.description = description;
    }

    private final boolean isCustomize;

    private final String description;

    public boolean isCustomize() {
        return isCustomize;
    }

    public String getDescription() {
        return description;
    }

    public static Optional<TagTypeEnum> of(String name) {
        return Arrays.stream(TagTypeEnum.values()).filter(t -> t.name().equalsIgnoreCase(name)).findFirst();
    }
}
