// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

// This suit test the `backends` tvf
suite("test_local_tvf") {
    List<List<Object>> table =  sql """ select * from backends(); """
    assertTrue(table.size() > 0)
    def be_id = table[0][0]

    table = sql """
        select count(*) from local(
            "file_path" = "log/be.out",
            "backend_id" = "${be_id}",
            "format" = "csv")
        where c1 like "%start_time%";"""

    assertTrue(table.size() > 0)
    assertTrue(Long.valueOf(table[0][0]) > 0)

    table = sql """
        select count(*) from local(
            "file_path" = "log/*.out",
            "backend_id" = "${be_id}",
            "format" = "csv")
        where c1 like "%start_time%";"""

    assertTrue(table.size() > 0)
    assertTrue(Long.valueOf(table[0][0]) > 0)

    test {
        sql """
        select count(*) from local(
            "file_path" = "../log/be.out",
            "backend_id" = "${be_id}",
            "format" = "csv")
        where c1 like "%start_time%";
        """
        // check exception message contains
        exception "can not contain '..' in path"
    }

    test {
        sql """
        select count(*) from local(
            "file_path" = "./log/xx.out",
            "backend_id" = "${be_id}",
            "format" = "csv")
        where c1 like "%start_time%";
        """
        // check exception message contains
        exception "No matches found"
    }
}
