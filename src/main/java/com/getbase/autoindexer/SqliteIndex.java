/*
 * Copyright (C) 2013 Jerzy Chalupski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.getbase.autoindexer;

import com.google.common.base.Objects;

import java.util.Arrays;

public class SqliteIndex {
  public final String mTable;
  public final String[] mColumns;

  public SqliteIndex(String table, String... columns) {
    mTable = table;
    mColumns = columns;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SqliteIndex that = (SqliteIndex) o;

    return Objects.equal(mTable, that.mTable) &&
        Objects.equal(mColumns, that.mColumns);
  }

  @Override
  public String toString() {
    return "SQLiteIndex on " + mTable + Arrays.toString(mColumns);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(mTable, mColumns);
  }
}
