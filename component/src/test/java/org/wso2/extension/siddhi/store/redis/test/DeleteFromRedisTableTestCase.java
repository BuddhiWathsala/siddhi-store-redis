/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
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
import org.wso2.siddhi.core.exception.SiddhiAppCreationException;
import org.wso2.siddhi.core.stream.input.InputHandler;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class DeleteFromRedisTableTestCase {
    private static final Logger log = LoggerFactory.getLogger(DeleteFromRedisTableTestCase.class);
    private static final String TABLE_NAME = "fooTable";

    @BeforeClass
    public static void startTest() {
        log.info("== Redis Table DELETE tests started ==");
    }

    @AfterClass
    public static void shutdown() {
        log.info("== Redis Table DELETE tests completed ==");
    }

    @BeforeMethod
    public void init() throws ConnectionUnavailableException {
        RedisTestUtils.cleanRedisDatabase();
    }

    @Test(description = "deleteFromRedisTableTest1")
    public void deleteFromRedisTableTest1() throws InterruptedException, ConnectionUnavailableException {
        // Testing simple deletion with primary keys.
        log.info("deleteFromRedisTableTest1 - simple deletion with a primary key");
        SiddhiManager siddhiManager = new SiddhiManager();
        String streams = "" +
                "define stream StockStream (symbol string, price float, volume long); " +
                "define stream DeleteStockStream (symbol string, price float, volume long); " +
                "define stream CheckStockStream (symbol string, price float, volume long); " +
                "@Store(type='redis', table.name='" + TABLE_NAME + "', host= 'localhost',port='6379'," +
                "password='root') " +
                "@PrimaryKey('symbol') " +
                "@index('price') " +
                "define table StockTable (symbol string, price float, volume long); ";
        String query = "" +
                "@info(name = 'query1') " +
                "from StockStream " +
                "insert into StockTable; " +
                " " +
                "@info(name = 'query2') " +
                "from DeleteStockStream " +
                "delete StockTable " +
                "  on StockTable.symbol == symbol; ";
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("StockStream");
        InputHandler deleteStockStream = siddhiAppRuntime.getInputHandler("DeleteStockStream");
        siddhiAppRuntime.start();

        stockStream.send(new Object[]{"WSO2", 55.6F, 100L});
        stockStream.send(new Object[]{"IBM", 75.6F, 100L});
        stockStream.send(new Object[]{"HTC", 57.6F, 100L});
        deleteStockStream.send(new Object[]{"IBM", 57.6F, 100L});
        deleteStockStream.send(new Object[]{"HTC", 57.6F, 100L});
        await().atMost(5, TimeUnit.SECONDS);

        int totalRowsInTable = RedisTestUtils.getRowsFromTable(TABLE_NAME);
        Assert.assertEquals(totalRowsInTable, 2, "Deletion failed");
        siddhiAppRuntime.shutdown();
    }

    @Test(dependsOnMethods = "deleteFromRedisTableTest1")
    public void deleteFromRedisTableTest2() throws InterruptedException, ConnectionUnavailableException {
        // Testing simple deletion with primary keys, operands in different order.
        log.info("deleteFromRedisTableTest2 - simple deletion with primary keys, operands in different order.");
        SiddhiManager siddhiManager = new SiddhiManager();
        String streams = "" +
                "define stream StockStream (symbol string, price float, volume long); " +
                "define stream DeleteStockStream (symbol string, price float, volume long); " +
                "define stream CheckStockStream (symbol string, price float, volume long); " +
                "@Store(type='redis', table.name='" + TABLE_NAME + "', host= 'localhost',port='6379'," +
                "password='root') " +
                "@PrimaryKey('symbol') " +
                "define table StockTable (symbol string, price float, volume long); ";
        String query = "" +
                "@info(name = 'query1') " +
                "from StockStream " +
                "insert into StockTable ;" +
                "" +
                "@info(name = 'query2') " +
                "from DeleteStockStream " +
                "delete StockTable " +
                "   on symbol == StockTable.symbol ;";
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("StockStream");
        InputHandler deleteStockStream = siddhiAppRuntime.getInputHandler("DeleteStockStream");
        siddhiAppRuntime.start();

        stockStream.send(new Object[]{"WSO2", 55.6F, 100L});
        stockStream.send(new Object[]{"IBM", 75.6F, 100L});
        stockStream.send(new Object[]{"HTC", 57.6F, 100L});
        deleteStockStream.send(new Object[]{"IBM", 57.6F, 100L});
        deleteStockStream.send(new Object[]{"HTC", 57.6F, 100L});
        await().atMost(5, TimeUnit.SECONDS);

        int totalRowsInTable = RedisTestUtils.getRowsFromTable(TABLE_NAME);
        Assert.assertEquals(totalRowsInTable, 1, "Deletion failed");
        siddhiAppRuntime.shutdown();
    }

    @Test(dependsOnMethods = "deleteFromRedisTableTest1")
    public void deleteFromRedisTableTest3() throws InterruptedException, ConnectionUnavailableException {
        // Testing simple deletion with primary keys with one operand as a constant.
        log.info("deleteFromRedisTableTest3 - simple deletion with primary keys with one operand as a constant.");
        SiddhiManager siddhiManager = new SiddhiManager();
        String streams = "" +
                "define stream StockStream (symbol string, price float, volume long); " +
                "define stream DeleteStockStream (symbol string, price float, volume long); " +
                "define stream CheckStockStream (symbol string, price float, volume long); " +
                "@Store(type='redis', table.name='" + TABLE_NAME + "', host= 'localhost',port='6379'," +
                "password='root') " +
                "@PrimaryKey('symbol') " +
                "define table StockTable (symbol string, price float, volume long); ";
        String query = "" +
                "@info(name = 'query1') " +
                "from StockStream " +
                "insert into StockTable ;" +
                "" +
                "@info(name = 'query2') " +
                "from DeleteStockStream " +
                "delete StockTable " +
                "   on StockTable.symbol == 'IBM'  ;";
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("StockStream");
        InputHandler deleteStockStream = siddhiAppRuntime.getInputHandler("DeleteStockStream");
        siddhiAppRuntime.start();

        stockStream.send(new Object[]{"WSO2", 55.6F, 100L});
        stockStream.send(new Object[]{"IBM", 75.6F, 100L});
        stockStream.send(new Object[]{"HTC", 57.6F, 100L});
        deleteStockStream.send(new Object[]{"IBM", 57.6F, 100L});
        await().atMost(5, TimeUnit.SECONDS);

        int totalRowsInTable = RedisTestUtils.getRowsFromTable(TABLE_NAME);
        Assert.assertEquals(totalRowsInTable, 2, "Deletion failed");
        siddhiAppRuntime.shutdown();
    }

    @Test(dependsOnMethods = "deleteFromRedisTableTest3")
    public void deleteFromRedisTableTest4() throws InterruptedException, ConnectionUnavailableException {
        // Testing simple deletion with primary keys with one operand as a constant, with the operand orders reversed.
        log.info("deleteFromRedisTableTest4 - simple deletion with primary keys with one operand as a constant, " +
                "with the operand orders reversed.");
        SiddhiManager siddhiManager = new SiddhiManager();
        String streams = "" +
                "define stream StockStream (symbol string, price float, volume long); " +
                "define stream DeleteStockStream (symbol string, price float, volume long); " +
                "define stream CheckStockStream (symbol string, price float, volume long); " +
                "@Store(type='redis', table.name='" + TABLE_NAME + "', host= 'localhost',port='6379'," +
                "password='root') " +
                "@PrimaryKey('symbol') " +
                "define table StockTable (symbol string, price float, volume long); ";
        String query = "" +
                "@info(name = 'query1') " +
                "from StockStream " +
                "insert into StockTable ;" +
                "" +
                "@info(name = 'query2') " +
                "from DeleteStockStream " +
                "delete StockTable " +
                "   on 'IBM' == StockTable.symbol  ;";
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("StockStream");
        InputHandler deleteStockStream = siddhiAppRuntime.getInputHandler("DeleteStockStream");
        siddhiAppRuntime.start();

        stockStream.send(new Object[]{"WSO2", 55.6F, 100L});
        stockStream.send(new Object[]{"IBM", 75.6F, 100L});
        stockStream.send(new Object[]{"HTC", 57.6F, 100L});
        deleteStockStream.send(new Object[]{"IBM", 57.6F, 100L});
        await().atMost(5, TimeUnit.SECONDS);

        int totalRowsInTable = RedisTestUtils.getRowsFromTable(TABLE_NAME);
        Assert.assertEquals(totalRowsInTable, 2, "Deletion failed");
        siddhiAppRuntime.shutdown();
    }

    @Test(dependsOnMethods = "deleteFromRedisTableTest4", expectedExceptions = SiddhiAppCreationException.class)
    public void deleteFromRedisTableTest5() throws InterruptedException, ConnectionUnavailableException {
        // Testing simple deletion with conditions. Expected to throw an exception since it is not supported.
        log.info("deleteFromRedisTableTest5 - simple deletion with conditions. Expected to throw an exception since " +
                "it is not supported.");
        SiddhiManager siddhiManager = new SiddhiManager();
        String streams = "" +
                "define stream StockStream (symbol string, price float, volume long); " +
                "define stream DeleteStockStream (symbol string, price float, volume long); " +
                "define stream CheckStockStream (symbol string, price float, volume long); " +
                "@Store(type='redis', table.name='" + TABLE_NAME + "', host= 'localhost',port='6379'," +
                "password='root') " +
                "@PrimaryKey('symbol')" +
                "define table StockTable (symbol string, price float, volume long); ";
        String query = "" +
                "@info(name = 'query1') " +
                "from StockStream " +
                "insert into StockTable ;" +
                "" +
                "@info(name = 'query2') " +
                "from DeleteStockStream " +
                "delete StockTable " +
                "   on StockTable.symbol==symbol and StockTable.price > price and  StockTable.volume == volume  ;";
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("StockStream");
        InputHandler deleteStockStream = siddhiAppRuntime.getInputHandler("DeleteStockStream");
        siddhiAppRuntime.start();

        stockStream.send(new Object[]{"WSO2", 55.6F, 100L});
        stockStream.send(new Object[]{"IBM", 75.6F, 100L});
        stockStream.send(new Object[]{"HTC", 57.6F, 100L});
        deleteStockStream.send(new Object[]{"HTC", 57.6F, 100L});
        await().atMost(5, TimeUnit.SECONDS);
        siddhiAppRuntime.shutdown();
    }

    @Test(dependsOnMethods = "deleteFromRedisTableTest4")
    public void deleteFromRedisTableTest6() throws InterruptedException, ConnectionUnavailableException {
        // Testing simple deletion with true condition. This will delete all the records in the table with indexed
        // values
        log.info("deleteFromRedisTableTest6 - simple deletion with true condition. This will delete all the records" +
                " in the table with indexed");
        SiddhiManager siddhiManager = new SiddhiManager();
        String streams = "" +
                "define stream StockStream (symbol string, price float, volume long); " +
                "define stream DeleteStockStream (symbol string, price float, volume long); " +
                "define stream CheckStockStream (symbol string, price float, volume long); " +
                "@Store(type='redis', table.name='" + TABLE_NAME + "', host= 'localhost',port='6379'," +
                "password='root') " +
                "@PrimaryKey('symbol')" +
                "define table StockTable (symbol string, price float, volume long); ";
        String query = "" +
                "@info(name = 'query1') " +
                "from StockStream " +
                "insert into StockTable ;" +
                "" +
                "@info(name = 'query2') " +
                "from DeleteStockStream " +
                "delete StockTable " +
                "on true; ";
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("StockStream");
        InputHandler deleteStockStream = siddhiAppRuntime.getInputHandler("DeleteStockStream");
        siddhiAppRuntime.start();

        stockStream.send(new Object[]{"WSO2", 55.6F, 100L});
        stockStream.send(new Object[]{"IBM", 75.6F, 100L});
        stockStream.send(new Object[]{"HTC", 57.6F, 100L});
        deleteStockStream.send(new Object[]{"HTC", 57.6F, 100L});
        await().atMost(5, TimeUnit.SECONDS);

        int totalRowsInTable = RedisTestUtils.getRowsFromTable(TABLE_NAME);
        Assert.assertEquals(totalRowsInTable, 0, "Deletion failed");
        siddhiAppRuntime.shutdown();
    }

    @Test(dependsOnMethods = "deleteFromRedisTableTest6")
    public void deleteFromRedisTableTest7() throws InterruptedException, ConnectionUnavailableException {
        // Testing simple deletion with true condition. This will delete all the records in the table with indexed
        // values
        log.info("deleteFromRedisTableTest7 - simple deletion with true condition. This will delete all the records " +
                "in the table with indexed values");
        SiddhiManager siddhiManager = new SiddhiManager();
        String streams = "" +
                "define stream StockStream (symbol string, price float, volume long); " +
                "define stream DeleteStockStream (symbol string, price float, volume long); " +
                "define stream CheckStockStream (symbol string, price float, volume long); " +
                "@Store(type='redis', table.name='" + TABLE_NAME + "', host= 'localhost',port='6379'," +
                "password='root') " +
                "@PrimaryKey('symbol') " +
                "@index('price') " +
                "define table StockTable (symbol string, price float, volume long); ";
        String query = "" +
                "@info(name = 'query1') " +
                "from StockStream " +
                "insert into StockTable ;" +
                "" +
                "@info(name = 'query2') " +
                "from DeleteStockStream " +
                "delete StockTable " +
                "on StockTable.price == price; ";
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        InputHandler stockStream = siddhiAppRuntime.getInputHandler("StockStream");
        InputHandler deleteStockStream = siddhiAppRuntime.getInputHandler("DeleteStockStream");
        siddhiAppRuntime.start();

        stockStream.send(new Object[]{"WSO2", 57.6F, 100L});
        stockStream.send(new Object[]{"IBM", 75.6F, 100L});
        stockStream.send(new Object[]{"HTC", 57.6F, 100L});
        deleteStockStream.send(new Object[]{"HTC", 57.6F, 100L});
        await().atMost(5, TimeUnit.SECONDS);

        int totalRowsInTable = RedisTestUtils.getRowsFromTable(TABLE_NAME);
        Assert.assertEquals(totalRowsInTable, 2, "Deletion failed");
        siddhiAppRuntime.shutdown();
    }
}
