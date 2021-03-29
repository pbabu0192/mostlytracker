package com.mosltyai.mostlytracker.mapper;

import com.mosltyai.mostlytracker.contract.ApiProject;
import com.mosltyai.mostlytracker.contract.ApiProjectSummary;
import com.mosltyai.mostlytracker.model.Project;
import com.mosltyai.mostlytracker.service.type.ServiceProject;
import com.mosltyai.mostlytracker.service.type.ServiceProjectSummary;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
@Validated
public interface ProjectMapper {

  @Mapping(source = "projectId", target = "id")
  ServiceProject toServiceUpdate(ApiProject source, UUID projectId);

  ServiceProject toService(ApiProject source);

  @Mapping(target = "id", ignore = true)
  Project toModel(ServiceProject source);

  ApiProject toApi(Project source);

  List<ApiProject> toApi(List<Project> source);

  ApiProjectSummary toApi(ServiceProjectSummary source);

  default Double roundOff(Double value) {
    return BigDecimal.valueOf(value)
        .round(new MathContext(3, RoundingMode.HALF_EVEN))
        .doubleValue();
  }
}
