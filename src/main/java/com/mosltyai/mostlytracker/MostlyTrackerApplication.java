package com.mosltyai.mostlytracker;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

@SpringBootApplication
public class MostlyTrackerApplication {

  public static void main(String[] args) {
    SpringApplication.run(MostlyTrackerApplication.class, args);
  }

  @Primary
  @Bean
  public BeanUtilsBean beanUtilsBean() {
    return new NonNullBeanUtils();
  }

  static class NonNullBeanUtils extends BeanUtilsBean {
    @Override
    public void copyProperty(final Object bean, final String name, final Object value)
        throws IllegalAccessException, InvocationTargetException {
      if (Objects.isNull(value)) return;
      super.copyProperty(bean, name, value);
    }
  }
}
