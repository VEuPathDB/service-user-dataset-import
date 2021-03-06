package org.veupathdb.service.userds.repo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.veupathdb.service.userds.model.JobRow;
import org.veupathdb.service.userds.model.ProjectCache;
import org.veupathdb.service.userds.model.StatusCache;
import org.veupathdb.service.userds.model.handler.DatasetOriginCache;
import org.veupathdb.service.userds.util.DbMan;

public class InsertJobQuery
{
  public static void run(JobRow row) throws SQLException {
    try (
      var cn = DbMan.getImportDb().getConnection();
      var ps = prepare(cn, row)
    ) {
      ps.execute();
    }
  }

  private static PreparedStatement prepare(Connection cn, JobRow row)
  throws SQLException {
    var out = cn.prepareStatement(SQL.Insert.Job);

    out.setString(1, row.getJobId());
    out.setLong(2, row.getUserId());
    out.setShort(3, StatusCache.getInstance().get(row.getStatus()));
    out.setString(4, row.getName());
    out.setString(5, row.getDescription().orElse(null));
    out.setString(6, row.getSummary().orElse(null));
    out.setShort(7, DatasetOriginCache.getInstance()
      .get(row.getOrigin())
      .orElseThrow());
    out.setString(8, row.getType());

    out.setArray(9, cn.createArrayOf("SMALLINT", row.getProjects()
      .stream()
      .map(ProjectCache.getInstance()::get)
      .toArray(Object[]::new)));

    return out;

  }
}
