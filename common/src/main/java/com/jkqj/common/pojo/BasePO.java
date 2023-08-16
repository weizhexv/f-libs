package com.jkqj.common.pojo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class BasePO {
    protected Long createdBy;
    protected LocalDateTime createdAt;
    protected Long modifiedBy;
    protected LocalDateTime modifiedAt;
    protected Byte mask;
    protected Long vsn;

    public static void initFields(BasePO domain) {
        initFields(domain, 0L);
    }

    public static void initFields(BasePO domain, Long operatorId) {
        if (domain == null) {
            throw new RuntimeException("domain不能为空");
        }

        LocalDateTime now = LocalDateTime.now();
        domain.setCreatedAt(now);
        domain.setCreatedBy(operatorId);
        domain.setModifiedAt(now);
        domain.setModifiedBy(operatorId);
        domain.setMask((byte) 0);
        domain.setVsn(0L);
    }
}
