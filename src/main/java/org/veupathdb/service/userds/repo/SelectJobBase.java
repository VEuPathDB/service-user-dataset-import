package org.veupathdb.service.userds.repo;

import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import org.veupathdb.service.userds.generated.model.FormatParam;
import org.veupathdb.service.userds.model.JobRow;
import org.veupathdb.service.userds.model.ProjectCache;
import org.veupathdb.service.userds.model.StatusCache;
import org.veupathdb.service.userds.model.handler.DatasetOriginCache;
import org.veupathdb.service.userds.util.Format;

abstract class SelectJobBase
{
  protected static JobRow rsToJobRow(ResultSet rs) throws Exception {
    var projects = new ArrayList <String>(1);
    try (var subRs = rs.getArray(Schema.Column.PROJECTS).getResultSet()) {
      while (subRs.next()) {
        projects.add(ProjectCache.getInstance().getKey(subRs.getShort(2)));
      }
    }

    var messageStr = rs.getString(Schema.Table.JobMessages.MESSAGE);
    var message = messageStr != null
      ? Format.Json.readTree(messageStr)
      : null;
    var handlerParamsStr = rs.getString(Schema.Table.Jobs.HANDLER_PARAMS);
    var handlerParams = Format.Json.readValue(handlerParamsStr,
        new TypeReference< List < FormatParam > >() {});

    return new JobRow(
      rs.getInt(Schema.Table.Jobs.DB_ID),
      rs.getString(Schema.Table.Jobs.JOB_ID),
      rs.getLong(Schema.Table.Jobs.USER_ID),
      StatusCache.getInstance().getKey(rs.getShort(Schema.Table.Jobs.STATUS)),
      rs.getString(Schema.Table.Jobs.NAME),
      rs.getString(Schema.Table.Jobs.DESCRIPTION),
      rs.getString(Schema.Table.Jobs.SUMMARY),
      rs.getObject(Schema.Table.Jobs.STARTED, OffsetDateTime.class)
        .toLocalDateTime(),
      Optional.ofNullable(
        rs.getObject(Schema.Table.Jobs.FINISHED, OffsetDateTime.class))
          .map(OffsetDateTime::toLocalDateTime)
        .orElse(null),
      message,
      handlerParams,
      projects,
      DatasetOriginCache.getInstance()
        .get(rs.getShort(Schema.Table.Jobs.ORIGIN_ID))
        .orElseThrow(),
      rs.getString(Schema.Table.Jobs.TYPE),
      rs.getInt(Schema.Table.JobIrodsIDs.IRODS_ID)
    );
  }
}
