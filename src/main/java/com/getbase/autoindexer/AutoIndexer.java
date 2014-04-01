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

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

import org.chalup.thneed.ManyToManyRelationship;
import org.chalup.thneed.ModelGraph;
import org.chalup.thneed.OneToManyRelationship;
import org.chalup.thneed.OneToOneRelationship;
import org.chalup.thneed.PolymorphicRelationship;
import org.chalup.thneed.RecursiveModelRelationship;
import org.chalup.thneed.RelationshipVisitor;

import java.util.Set;

public final class AutoIndexer {

  public static final String AUTO_INDEX_PREFIX = "auto_index";

  private AutoIndexer() {
  }

  private static final Joiner COLUMN_JOINER = Joiner.on(",");

  public static String getCreateStatement(SqliteIndex index) {
    return "CREATE INDEX " + index.getName() + " ON " + index.mTable + "(" + COLUMN_JOINER.join(index.mColumns) + ")";
  }

  public static Predicate<SqliteIndex> isIndexOnColumn(final String columnName) {
    return new Predicate<SqliteIndex>() {
      @Override
      public boolean apply(SqliteIndex index) {
        return index.mColumns.length == 1 && index.mColumns[0].equalsIgnoreCase(columnName);
      }
    };
  }

  public static Set<SqliteIndex> generateIndexes(ModelGraph<? extends DbTableModel> modelGraph) {
    final Set<SqliteIndex> indexes = Sets.newHashSet();

    modelGraph.accept(new RelationshipVisitor<DbTableModel>() {
      @Override
      public void visit(OneToManyRelationship<? extends DbTableModel> relationship) {
        DbTableModel model = relationship.mModel;
        DbTableModel referencedModel = relationship.mReferencedModel;

        indexes.add(new SqliteIndex(model.getDbTable(), relationship.mLinkedByColumn));
        indexes.add(new SqliteIndex(referencedModel.getDbTable(), relationship.mReferencedModelIdColumn));
      }

      @Override
      public void visit(OneToOneRelationship<? extends DbTableModel> relationship) {
        DbTableModel model = relationship.mModel;
        DbTableModel linkedModel = relationship.mLinkedModel;

        indexes.add(new SqliteIndex(linkedModel.getDbTable(), relationship.mLinkedByColumn));
        indexes.add(new SqliteIndex(model.getDbTable(), relationship.mParentModelIdColumn));
      }

      @Override
      public void visit(RecursiveModelRelationship<? extends DbTableModel> relationship) {
        DbTableModel model = relationship.mModel;

        indexes.add(new SqliteIndex(model.getDbTable(), relationship.mModelIdColumn));
        indexes.add(new SqliteIndex(model.getDbTable(), relationship.mGroupByColumn));
      }

      @Override
      public void visit(ManyToManyRelationship<? extends DbTableModel> relationship) {
        // no implementation necessary
      }

      @Override
      public void visit(PolymorphicRelationship<? extends DbTableModel> relationship) {
        DbTableModel model = relationship.mModel;
        indexes.add(new SqliteIndex(model.getDbTable(), relationship.mTypeColumnName, relationship.mIdColumnName));

        for (DbTableModel dbTableModel : relationship.mPolymorphicModels.values()) {
          indexes.add(new SqliteIndex(dbTableModel.getDbTable(), relationship.mPolymorphicModelIdColumn));
        }
      }
    });

    return indexes;
  }
}
