package com.dku.council.util.test;

import com.dku.council.debug.service.ErrorLogService;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * WebMvcTest에 필요한 일부 필요한 컴포넌트(ErrorLogService)들을 Import합니다.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ErrorLogService.class)
public @interface ImportsForMvc {
}
