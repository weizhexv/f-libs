package com.jkqj.excel.annotations;

/**
 * 绑定模式，
 *
 * <p>
 *     <ul>
 *         <ui>1--从excel解析出数据</ui>
 *         <ui>2--输出到excel</ui>
 *         <ui>3--都支持</ui>
 *     </ul>
 * </p>
 *
 */
public enum BinderMode {
    IN(1),
    OUT(2),
    ALL(3)
    ;


    private final int mode;

    BinderMode(int mode) {
        this.mode = mode;
    }

    public int getMode(){
        return mode;
    }

    public boolean isMatch(BinderMode binderMode) {
        return (this.mode & binderMode.mode) == binderMode.mode;
    }
}
