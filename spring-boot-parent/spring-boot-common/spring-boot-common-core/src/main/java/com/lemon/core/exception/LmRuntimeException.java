package com.lemon.core.exception;

/**
 * @author ：lemon
 * @description：
 * @date ：Created in 2020/5/28 17:27
 */
public class LmRuntimeException extends RuntimeException {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 0L;

    /**
     * 严重级别
     */
    protected int severity = LmExceptionSeverity.NORMAL;

    /**
     * 空构造器。
     */
    public LmRuntimeException() {
        super();
    }

    /**
     * 构造器。
     *
     * @param severity 严重级别
     */
    public LmRuntimeException(int severity) {
        super();

        this.severity = severity;
    }

    /**
     * 构造器。
     *
     * @param message 消息
     */
    public LmRuntimeException(String message) {
        super(message);
    }

    /**
     * 构造器。
     *
     * @param message  消息
     * @param severity 严重级别
     */
    public LmRuntimeException(String message, int severity) {
        super(message);

        this.severity = severity;
    }

    /**
     * 构造器。
     *
     * @param cause 原因
     */
    public LmRuntimeException(Throwable cause) {
        super(cause);
    }

    /**
     * 构造器。
     *
     * @param cause    原因
     * @param severity 严重级别
     */
    public LmRuntimeException(Throwable cause, int severity) {
        super(cause);

        this.severity = severity;
    }

    /**
     * 构造器。
     *
     * @param message 消息
     * @param cause   原因
     */
    public LmRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造器。
     *
     * @param message  消息
     * @param cause    原因
     * @param severity 严重级别
     */
    public LmRuntimeException(String message, Throwable cause, int severity) {
        super(message, cause);

        this.severity = severity;
    }

    /**
     * @return Returns the severity.
     */
    public int getSeverity() {
        return severity;
    }

    /**
     * @see java.lang.Throwable#toString()
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append(super.toString()).append(" - severity: ");

        switch (severity) {
            case LmExceptionSeverity.MINOR:
                buffer.append("MINOR");
                break;

            case LmExceptionSeverity.NORMAL:
                buffer.append("NORMAL");
                break;

            case LmExceptionSeverity.MAJOR:
                buffer.append("MAJOR");
                break;

            case LmExceptionSeverity.CRITICAL:
                buffer.append("CRITICAL");
                break;

            default:
                buffer.append("UNKNOWN");
        }

        buffer.append("(").append(severity).append(")");

        return buffer.toString();
    }
}