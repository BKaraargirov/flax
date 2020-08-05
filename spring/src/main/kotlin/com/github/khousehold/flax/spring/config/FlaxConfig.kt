package com.github.khousehold.flax.spring.config

import com.github.khousehold.flax.spring.RepositoryFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open abstract class FlaxConfig {
  //TODO: try to get from
  open abstract fun scanPath(): String

  @Bean
  open fun repositoryFactory(): RepositoryFactory {
    throw Exception("Not implemented")
  }
}