/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.braisdom.objsql.pagination;

import com.github.braisdom.objsql.BeanModelDescriptor;
import com.github.braisdom.objsql.DatabaseType;
import com.github.braisdom.objsql.Databases;
import com.github.braisdom.objsql.relation.Relationship;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

public class DefaultPaginator<T> implements Paginator<T> {

    @Override
    public PagedList<T> paginate(Page page, Paginatable paginatable,
                                 Relationship... relationships) throws SQLException {
        final String sql = paginatable.getQuerySQL();
        final Class domainModelClass = paginatable.getDomainClass();

        Databases.execute(((connection, sqlExecutor) -> {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            DatabaseType databaseType = DatabaseType.create(databaseMetaData.getDatabaseProductName(),
                    databaseMetaData.getDatabaseMajorVersion());
            PagedSQLBuilder sqlBuilder = Databases.getPagedSQLBuilderFactory()
                    .createPagedSQLBuilder(databaseType);

            String countSQL = sqlBuilder.buildCountSQL(sql);
            String querySQL = sqlBuilder.buildQuerySQL(page, sql);

            List result = sqlExecutor.query(connection, countSQL, new BeanModelDescriptor(domainModelClass));

            return null;
        }));

        return null;
    }
}