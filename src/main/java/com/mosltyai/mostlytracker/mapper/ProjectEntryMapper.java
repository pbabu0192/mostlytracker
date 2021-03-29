package com.mosltyai.mostlytracker.mapper;

import com.mosltyai.mostlytracker.contract.ApiProjectEntry;
import com.mosltyai.mostlytracker.model.ProjectEntry;
import com.mosltyai.mostlytracker.service.type.ServiceProjectEntry;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ProjectEntryMapper {

  @Mapping(source = "projectId", target = "projectId")
  ServiceProjectEntry toService(ApiProjectEntry source, UUID projectId);

  ProjectEntry toModel(ServiceProjectEntry source);

  ApiProjectEntry toApi(ProjectEntry source);

  List<ApiProjectEntry> toApi(List<ProjectEntry> source);

  default BigDecimal roundOff(BigDecimal value) {
    return value.round(new MathContext(3, RoundingMode.HALF_EVEN));
  }
}
