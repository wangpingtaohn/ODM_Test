package com.quectel.agingtest.common;

/**
 * 用于记录当前Case的测试结果
 */
public class CaseResult {
    public String id;// 相当于唯一id，和item
    public int rightTime; // 正确次数
    public int failTime; // 错误次数

    public CaseResult(String id, int rightTime, int failTime) {
        this.id = id;
        this.rightTime = rightTime;
        this.failTime = failTime;
    }

    @Override
    public String toString() {
        return "CaseResult{" +
                "id='" + id + '\'' +
                ", rightTime=" + rightTime +
                ", failTime=" + failTime +
                '}';
    }
}
