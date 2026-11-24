package com.cleanride.cleanride.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/** Lets @Async methods (e.g. sending emails) run on a background thread. */
@Configuration
@EnableAsync
public class AsyncConfig {
}
