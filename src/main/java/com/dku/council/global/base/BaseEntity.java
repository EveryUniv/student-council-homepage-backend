package com.dku.council.global.base;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime lastModifiedAt;


    public String getCreatedDateText() {
        LocalDateTime dateTime = createdAt;
        if (dateTime == null) {
            dateTime = LocalDateTime.MIN;
            log.error("Created date is empty. (null) This is bug!");
        }
        return dateTime.format(DateTimeFormatter.ISO_DATE);
    }
}
