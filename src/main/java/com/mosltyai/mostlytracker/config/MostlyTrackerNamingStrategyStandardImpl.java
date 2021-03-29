package com.mosltyai.mostlytracker.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;

import java.util.StringJoiner;

public class MostlyTrackerNamingStrategyStandardImpl extends SpringPhysicalNamingStrategy {
  private static final StringJoiner schema = new StringJoiner("", "`", "`");

  @Override
  public Identifier toPhysicalTableName(
      final Identifier name, final JdbcEnvironment jdbcEnvironment) {
    //return super.toPhysicalTableName(name, jdbcEnvironment);
    return appendQuotes(name, jdbcEnvironment);
  }

  @Override
  public Identifier toPhysicalCatalogName(
      final Identifier name, final JdbcEnvironment jdbcEnvironment) {
   // return super.toPhysicalCatalogName(name, jdbcEnvironment);
    return appendQuotes(name, jdbcEnvironment);
  }

  @Override
  public Identifier toPhysicalSchemaName(
      final Identifier name, final JdbcEnvironment jdbcEnvironment) {

    return appendQuotes(name, jdbcEnvironment);
  }

  private Identifier appendQuotes(Identifier name, JdbcEnvironment jdbcEnvironment) {
    if (name == null) {
      return null;
    } else {
      StringBuilder builder = new StringBuilder(name.getText().replace('.', '_'));

      for (int i = 1; i < builder.length() - 1; ++i) {
        if (isUnderscoreRequired(builder.charAt(i - 1), builder.charAt(i), builder.charAt(i + 1))) {
          builder.insert(i++, '_');
        }
      }

      return this.getIdentifier(
          builder.toString(), true, jdbcEnvironment);
    }
  }

  private boolean isUnderscoreRequired(char before, char current, char after) {
    return Character.isLowerCase(before)
        && Character.isUpperCase(current)
        && Character.isLowerCase(after);
  }
}
