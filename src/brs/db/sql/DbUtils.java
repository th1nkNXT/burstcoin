package brs.db.sql;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Insert;
import org.jooq.SelectQuery;
import org.jooq.UpdatableRecord;

public final class DbUtils {

  private DbUtils() {
  } // never

  public static void close(AutoCloseable... closeables) {
    for (AutoCloseable closeable : closeables) {
      if (closeable != null) {
        try {
          closeable.close();
        } catch (Exception ignored) {
        }
      }
    }
  }

  public static void applyLimits(SelectQuery query, int from, int to ) {
    int limit = to >= 0 && to >= from && to < Integer.MAX_VALUE ? to - from + 1 : 0;
    if (limit > 0 && from > 0) {
      query.addLimit(from, limit);
    }
    else if (limit > 0) {
      query.addLimit(limit);
    }
    else if (from > 0) {
      query.addOffset(from);
    }
  }

  public static Insert<?> upsert(DSLContext ctx, UpdatableRecord<?> record, Field<?> ... keys) {
    return ctx.insertInto(record.getTable())
      .set(record)
      .onConflict(keys)
      .doUpdate()
      .set(record);
  }

}
