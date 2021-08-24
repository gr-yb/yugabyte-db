// Copyright (c) YugaByte, Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.  You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software distributed under the License
// is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
// or implied.  See the License for the specific language governing permissions and limitations
// under the License.
//

package org.yb.pgsql;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yb.util.YBTestRunnerNonTsanOnly;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

import static org.yb.AssertionWrappers.*;

@RunWith(value=YBTestRunnerNonTsanOnly.class)
public class TestPgSavepoints extends BasePgSQLTest {
  private static final Logger LOG = LoggerFactory.getLogger(TestPgSavepoints.class);
  private static final int LARGE_BATCH_ROW_THRESHOLD = 100;

  private void createTable() throws SQLException {
    try (Statement statement = connection.createStatement()) {
      statement.execute("CREATE TABLE t (k INT, v INT)");
    }
  }

  private OptionalInt getSingleValue(Connection c, int k) throws SQLException {
    String query = String.format("SELECT * FROM t WHERE k = %d", k);
    try (ResultSet rs = c.createStatement().executeQuery(query)) {
      if (!rs.next()) {
        return OptionalInt.empty();
      }
      Row row = Row.fromResultSet(rs);
      assertFalse("Found more than one result", rs.next());
      return OptionalInt.of(row.getInt(1));
    }
  }

  @Override
  protected Map<String, String> getTServerFlags() {
    // TODO(savepoints) -- enable by default.
    Map<String, String> flags = super.getTServerFlags();
    flags.put("enable_pg_savepoints", "true");
    flags.put("txn_max_apply_batch_records", String.format("%d", LARGE_BATCH_ROW_THRESHOLD));
    return flags;
  }

  @Test
  public void testSavepointCreation() throws Exception {
    createTable();

    try (Connection conn = getConnectionBuilder()
                                .withAutoCommit(AutoCommit.DISABLED)
                                .connect()) {
      Statement statement = conn.createStatement();
      statement.execute("INSERT INTO t VALUES (1, 2)");
      statement.execute("SAVEPOINT a");
      statement.execute("INSERT INTO t VALUES (3, 4)");
      statement.execute("SAVEPOINT b");

      assertEquals(getSingleValue(conn, 1), OptionalInt.of(2));
      assertEquals(getSingleValue(conn, 3), OptionalInt.of(4));
    }
  }

  @Test
  public void testSavepointRollback() throws Exception {
    createTable();

    try (Connection conn = getConnectionBuilder()
                                .withAutoCommit(AutoCommit.DISABLED)
                                .connect()) {
      Statement statement = conn.createStatement();
      statement.execute("INSERT INTO t VALUES (1, 2)");
      statement.execute("SAVEPOINT a");
      statement.execute("INSERT INTO t VALUES (3, 4)");
      statement.execute("ROLLBACK TO a");

      assertEquals(getSingleValue(conn, 1), OptionalInt.of(2));
      assertEquals(getSingleValue(conn, 3), OptionalInt.empty());
    }
  }

  @Test
  public void testSavepointUpdateAbortedRow() throws Exception {
    createTable();

    try (Connection conn = getConnectionBuilder()
                                .withAutoCommit(AutoCommit.DISABLED)
                                .connect()) {
      Statement statement = conn.createStatement();
      statement.execute("INSERT INTO t VALUES (1, 2)");
      statement.execute("SAVEPOINT a");
      statement.execute("INSERT INTO t VALUES (3, 4)");
      statement.execute("ROLLBACK TO a");
      statement.execute("UPDATE t SET v = 5 WHERE k = 3");
      assertEquals(statement.getUpdateCount(), 0);

      assertEquals(getSingleValue(conn, 1), OptionalInt.of(2));
      assertEquals(getSingleValue(conn, 3), OptionalInt.empty());
    }
  }

  @Test
  public void testAbortsIntentOfReleasedSavepoint() throws Exception {
    createTable();

    try (Connection conn = getConnectionBuilder()
                                .withAutoCommit(AutoCommit.DISABLED)
                                .connect()) {
      Statement statement = conn.createStatement();
      statement.execute("SAVEPOINT a");
      statement.execute("SAVEPOINT b");
      statement.execute("INSERT INTO t VALUES (3, 4)");
      statement.execute("RELEASE SAVEPOINT b");
      statement.execute("ROLLBACK TO a");

      assertEquals(getSingleValue(conn, 3), OptionalInt.empty());
    }
  }

  @Test
  public void testSavepointCommitWithAbort() throws Exception {
    createTable();

    try (Connection conn = getConnectionBuilder()
                                .withAutoCommit(AutoCommit.DISABLED)
                                .connect()) {
      Statement statement = conn.createStatement();
      statement.execute("INSERT INTO t VALUES (1, 2)");
      statement.execute("SAVEPOINT a");
      statement.execute("INSERT INTO t VALUES (3, 4)");
      statement.execute("ROLLBACK TO a");
      statement.execute("INSERT INTO t VALUES (5, 6)");
      conn.commit();

      assertEquals(getSingleValue(conn, 1), OptionalInt.of(2));
      assertEquals(getSingleValue(conn, 3), OptionalInt.empty());
      assertEquals(getSingleValue(conn, 5), OptionalInt.of(6));
    }
  }

  @Test
  public void testSavepointLargeApplyWithAborts() throws Exception {
    final int NUM_BATCHES_TO_WRITE = 5;
    final int ROW_MOD_TO_ABORT = 7;
    createTable();

    try (Connection conn = getConnectionBuilder()
                                .withAutoCommit(AutoCommit.DISABLED)
                                .connect()) {
      Statement statement = conn.createStatement();
      for (int i = 0; i < NUM_BATCHES_TO_WRITE; ++i) {
        for (int j = 0; j < LARGE_BATCH_ROW_THRESHOLD; ++j) {
          int val = i * LARGE_BATCH_ROW_THRESHOLD + j;
          if (val % ROW_MOD_TO_ABORT == 0) {
            statement.execute("SAVEPOINT a");
          }
          statement.execute(String.format("INSERT INTO t VALUES (%d, %d)", val, val));
          if (val % ROW_MOD_TO_ABORT == 0) {
            statement.execute("ROLLBACK TO a");
          }
        }
      }
      conn.commit();

      for (int i = 0; i < NUM_BATCHES_TO_WRITE; ++i) {
        for (int j = 0; j < LARGE_BATCH_ROW_THRESHOLD; ++j) {
          int val = i * NUM_BATCHES_TO_WRITE + j;
          if (val % ROW_MOD_TO_ABORT == 0) {
            assertEquals(getSingleValue(conn, val), OptionalInt.empty());
          } else {
            assertEquals(getSingleValue(conn, val), OptionalInt.of(val));
          }
        }
      }
    }
  }

  @Test
  public void testSavepointLargeApplyWithAbortsAndNodeKills() throws Exception {
    // TODO(savepoints): Add test which forces node restarts to ensure transaction loader is
    // properly loading savepoint metadata.
  }
}