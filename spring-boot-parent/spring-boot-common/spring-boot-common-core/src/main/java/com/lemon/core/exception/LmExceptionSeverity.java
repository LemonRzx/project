package com.lemon.core.exception;

/**
 * @author ：lemon
 * @description：
 * @date ：Created in 2020/5/28 17:28
 */

/**
 * 异常严重级别常量。
 *
 * @author
 *
 * @version
 */
public interface LmExceptionSeverity {
    /**
     * 轻微
     */
    int MINOR = 1;

    /**
     * 一般
     */
    int NORMAL = 2;

    /**
     * 重要
     */
    int MAJOR = 3;

    /**
     * 严重
     */
    int CRITICAL = 4;

}
