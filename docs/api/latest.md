# API Docs - v3.1.0

## Store

### redis *<a target="_blank" href="https://siddhi.io/en/v5.0/docs/query-guide/#store">(Store)</a>*

<p style="word-wrap: break-word">This extension assigns data source and connection instructions to event tables. It also implements read write operations on connected datasource. This extension only can be used to read the data which persisted using the same extension since unique implementation has been used to map the relational data in to redis's key and value representation</p>

<span id="syntax" class="md-typeset" style="display: block; font-weight: bold;">Syntax</span>
```
@Store(type="redis", table.name="<STRING>", cluster.mode="<BOOL>", nodes="<STRING>", ttl.seconds="<LONG>", ttl.on.update="<BOOL>", ttl.on.read="<BOOL>")
@PrimaryKey("PRIMARY_KEY")
@Index("INDEX")
```

<span id="query-parameters" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">QUERY PARAMETERS</span>
<table>
    <tr>
        <th>Name</th>
        <th style="min-width: 20em">Description</th>
        <th>Default Value</th>
        <th>Possible Data Types</th>
        <th>Optional</th>
        <th>Dynamic</th>
    </tr>
    <tr>
        <td style="vertical-align: top">table.name</td>
        <td style="vertical-align: top; word-wrap: break-word">The name with which the event table should be persisted in the store. If noname is specified via this parameter, the event table is persisted with the same name as the Siddhi table.</td>
        <td style="vertical-align: top">The tale name defined in the siddhi app</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">cluster.mode</td>
        <td style="vertical-align: top; word-wrap: break-word">This will decide the redis mode. if this is false, client will connect to a single redis node.</td>
        <td style="vertical-align: top">false</td>
        <td style="vertical-align: top">BOOL</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">nodes</td>
        <td style="vertical-align: top; word-wrap: break-word">host, port and the password of the node(s).In single node mode node details can be provided as follows- "node='hosts:port@password'" <br>In clustered mode host and port of all the master nodes should be provided separated by a comma(,). As an example "nodes = 'localhost:30001,localhost:30002'".</td>
        <td style="vertical-align: top">localhost:6379@root</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">ttl.seconds</td>
        <td style="vertical-align: top; word-wrap: break-word">Time to live in seconds for each record</td>
        <td style="vertical-align: top">-1</td>
        <td style="vertical-align: top">LONG</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">ttl.on.update</td>
        <td style="vertical-align: top; word-wrap: break-word">Set ttl on row update</td>
        <td style="vertical-align: top">false</td>
        <td style="vertical-align: top">BOOL</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">ttl.on.read</td>
        <td style="vertical-align: top; word-wrap: break-word">Set ttl on read rows</td>
        <td style="vertical-align: top">false</td>
        <td style="vertical-align: top">BOOL</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
</table>

<span id="examples" class="md-typeset" style="display: block; font-weight: bold;">Examples</span>
<span id="example-1" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 1</span>
```
@store(type='redis',nodes='localhost:6379@root',table.name='fooTable',cluster.mode=false)define table fooTable(time long, date String)
```
<p style="word-wrap: break-word">Above example will create a redis table with the name fooTable and work on asingle redis node.</p>

<span id="example-2" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 2</span>
```
@Store(type='redis', table.name='SweetProductionTable', nodes='localhost:30001,localhost:30002,localhost:30003', cluster.mode='true')
@primaryKey('symbol')
@index('price') 
define table SweetProductionTable (symbol string, price float, volume long);
```
<p style="word-wrap: break-word">Above example demonstrate how to use the redis extension to connect in to redis cluster. Please note that, as nodes all the master node's host and port should be provided in order to work correctly. In clustered node password will not besupported</p>

<span id="example-3" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 3</span>
```
@store(type='redis',nodes='localhost:6379@root',table.name='fooTable', ttl.seconds='30', ttl.onUpdate='true', ttl.onRead='true')define table fooTable(time long, date String)
```
<p style="word-wrap: break-word">Above example will create a redis table with the name fooTable and work on asingle redis node.  All rows inserted, updated or read will have its ttl set to 30 seconds</p>

