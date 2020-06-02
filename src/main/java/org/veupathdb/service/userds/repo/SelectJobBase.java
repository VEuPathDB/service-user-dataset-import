package org.veupathdb.service.userds.repo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.veupathdb.service.userds.model.JobRow;
import org.veupathdb.service.userds.model.ProjectCache;
import org.veupathdb.service.userds.model.StatusCache;

abstract class SelectJobBase
{
  protected static JobRow rsToJobRow(ResultSet rs) throws SQLException {

    var projects = new ArrayList <String>(1);
    try (var subRs = rs.getArray(Schema.Column.PROJECTS).getResultSet()) {
      while (subRs.next()) {
        projects.add(ProjectCache.getInstance().getKey(rs.getShort(1)));
      }
    }

    return new JobRow(
      rs.getInt(Schema.Column.DB_ID),
      rs.getString(Schema.Column.JOB_ID),
      rs.getLong(Schema.Column.USER_ID),
      StatusCache.getInstance().getKey(rs.getShort(Schema.Column.STATUS)),
      rs.getString(Schema.Column.NAME),
      rs.getString(Schema.Column.DESCRIPTION),
      rs.getString(Schema.Column.SUMMARY),
      rs.getObject(Schema.Column.STARTED, OffsetDateTime.class)
        .toLocalDateTime(),
      Optional.ofNullable(
        rs.getObject(Schema.Column.FINISHED, OffsetDateTime.class))
          .map(OffsetDateTime::toLocalDateTime)
        .orElse(null),
      rs.getString(Schema.Column.MESSAGE),
      projects
    );
  }
}
