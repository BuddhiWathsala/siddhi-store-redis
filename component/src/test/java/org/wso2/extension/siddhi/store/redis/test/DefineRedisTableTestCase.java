/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.extension.siddhi.store.redis.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.exception.ConnectionUnavailableException;
import org.wso2.siddhi.core.stream.input.InputHandler;


public class DefineRedisTableTestCase {
    private static final Logger log = LoggerFactory.getLogger(DefineRedisTableTestCase.class);
    private static final String TABLE_NAME = "fooTable";

    @BeforeClass
    public static void startTest() {
        log.info("== Define Redis Table tests started ==");
    }

    @AfterClass
    public static void shutdown() {
        log.info("== Define Redis Table tests completed ==");
    }

    @BeforeMethod
    public void init() throws ConnectionUnavailableException {
        RedisTestUtils.cleanRedisDatabase();
    }

    @Test
    public void defineRedisTableTest1() throws InterruptedException, ConnectionUnavailableException {
        log.info("defineRedisTableTestCase 1 - Table with a primary key");
        SiddhiManager siddhiManager = new SiddhiManager();
        String streams = "" +
                "define stream StockStream (name string, amount double);" +
                "@store(type='redis', host='localhost', " +
                "port='6379', table.name='fooTable', password= 'root')" +
                "@PrimaryKey('name')" +
                "define table fooTable(name string, amount double); ";
        String query = "" +
                "@info(name = 'query1') " +
                "from StockStream " +
                "insert into fooTable; ";
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("StockStream");
        siddhiAppRuntime.start();

        stockStream.send(new Object[]{"WSO2", 100.0});
        stockStream.send(new Object[]{"IBM", 1001});

        int totalRowsInTable = RedisTestUtils.getRowsFromTable(TABLE_NAME);
        Assert.assertEquals(totalRowsInTable, 2, "Definition/Insertion failed");
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void defineRedisTableTest2() throws InterruptedException, ConnectionUnavailableException {
        log.info("defineRedisTableTestCase 2 - Table with a primary key and an index column");
        SiddhiManager siddhiManager = new SiddhiManager();
        String streams = "" +
                "define stream StockStream (name string, amount double);" +
                "@store(type='redis', host='localhost', " +
                "port='6379', table.name='fooTable', password= 'root')" +
                "@PrimaryKey('name')" +
                "@index('amount')" +
                "define table fooTable(name string, amount double); ";
        String query = "" +
                "@info(name = 'query1') " +
                "from StockStream " +
                "insert into fooTable; ";
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("StockStream");
        siddhiAppRuntime.start();

        stockStream.send(new Object[]{"WSO2", 100.0});
        stockStream.send(new Object[]{"IBM", 1001});

        int totalRowsInTable = RedisTestUtils.getRowsFromTable(TABLE_NAME);
        Assert.assertEquals(totalRowsInTable, 4, "Definition/Insertion failed");
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void defineRedisTableTest3() throws InterruptedException, ConnectionUnavailableException {
        log.info("defineRedisTableTestCase 3 - Table without a primary key");
        SiddhiManager siddhiManager = new SiddhiManager();
        String streams = "" +
                "define stream StockStream (name string, amount double);" +
                "@store(type='redis', host='localhost', " +
                "port='6379', table.name='fooTable', password= 'root')" +
                "define table fooTable(name string, amount double); ";
        String query = "" +
                "@info(name = 'query1') " +
                "from StockStream " +
                "insert into fooTable; ";
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("StockStream");
        siddhiAppRuntime.start();

        stockStream.send(new Object[]{"WSO2", 100.0});
        stockStream.send(new Object[]{"IBM", 1001});

        int totalRowsInTable = RedisTestUtils.getRowsFromTable(TABLE_NAME);
        Assert.assertEquals(totalRowsInTable, 2, "Definition/Insertion failed");
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void defineRedisTableTest4() throws InterruptedException, ConnectionUnavailableException {
        log.info("defineRedisTableTestCase 4 - Table with an index column");
        SiddhiManager siddhiManager = new SiddhiManager();
        String streams = "" +
                "define stream StockStream (name string, amount double);" +
                "@store(type='redis', host='localhost', " +
                "port='6379', table.name='fooTable', password= 'root')" +
                "@index('amount')" +
                "define table fooTable(name string, amount double); ";
        String query = "" +
                "@info(name = 'query1') " +
                "from StockStream " +
                "insert into fooTable; ";
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("StockStream");
        siddhiAppRuntime.start();

        stockStream.send(new Object[]{"WSO2", 100.0});
        stockStream.send(new Object[]{"IBM", 1001});

        int totalRowsInTable = RedisTestUtils.getRowsFromTable(TABLE_NAME);
        Assert.assertEquals(totalRowsInTable, 4, "Definition/Insertion failed");
        siddhiAppRuntime.shutdown();
    }
}
