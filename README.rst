=============
Project Valta
=============

Adds a client and server side API wrapper, that adds authentication, authorization, and accounting to HBase. This helps with shared HBase clusters to control - and protect - the scarce server resources.

Goals
=====

Valta adds code that makes use of server side hooks to track resources. The overall goal is to enable resource management to such a degree that cluster will never be overwhelmed by a single "rogue" client. This is a requirement not just for shared, *multitenant* clusters, but also such setups where a single application server could possibly consume all the server resources.

A classic use-case is where HBase serves interactive users, such as a web application, and also is used for batch jobs, using for example MapReduce to process vasts amount of data. Given that the MapReduce framework is colocated on the DataNode, as is the RegionServer, it is possible that it spins up so many threads acting as concurrent clients to HBase that eventually the RegionServers may fail to get enough HDFS resources to complete essential operations, such as Write-Ahead Log appends. If this happens the cluster will shut down one RegionServer after another.

This scenario can be avoided by defining limits per RegionServer and track the resource usage per *user* of the API. This is initially very much like *iptables* in Linux, i.e. a stateful firewalling solution.

Yet, there is a much broader requirement for guarding precious server side resources, no matter what the client is. It is even more important to reach a level of control where HBase can offer *service level objectives* (SLOs). With such objectives, you can fine-tune a system to serve high and low latency clients, mix various workloads, and eventually even adjust the entire system based on changing workload as they happen.

Resource Rules
==============

The definition of *resource limits* in Valta is akin to other resource manager found in database systems. You can define for example:

- User A is allowed to insert {1000|100|10} cells per second at {1K|100K|1M} size
- All users combined are allowed to insert up to {1000|100|10} cells per second at {1K|100K|1M} size
- Default rule for unknown users is to put them in Group Z, the default group
- If user A exceeds the limits, throttle access by 50%
- Group B is allowed to {insert|delete} 1M {cells|rows} per day
- Group C and User D are {not} allowed to modify existing rows
- User E is not allowed to scan more than 1GB of data during {business|peak} hours
- Group F is only allowed to access tables with name matching "*data"
- All groups are using their own namespace

Rules can be addressing groups and users, they can be negated and cover temporal aspects (peak time, weekends etc.) as well as varying sizes and counts of operations.

Valta uses either the Kerberos supplied user details, or makes use of the *attributes* set with each operation (see http://hbase.apache.org/xref/org/apache/hadoop/hbase/client/OperationWithAttributes.html for details). In fact, the *authentication module* is pluggable, and a default is supplied with the said features.

Once the user is authenticated, the *authorization module* is invoked to determine what that user is allowed to do. If the user is unknown, a default rule is chosen so that the request can be denied, or put into a specific resource pool. Again, the authentication module is pluggable, with a supplied default implementation reading the resource rules from an external properties file.

Implementation
==============

The central part of Valta is a serverside coprocessor, that initiates the authorization, authentication, and accounting handlers. These are, as mentioned, all pluggable, but reasonable default implementations are supplied. The coprocessor runs on each RegionServer to track the usage per user and group, and intervenes according to the defined rules, i.e. denies access, or throttles throughput (rate limiting).

As far as the default authorization module is concerned the limits are tracked with in-memory, soft-state counters. So a failure of the server means its local data is reset. This is not so much of an issue for certain counters, but may be for others (e.g. daily rates). There is no reason to not persist the values, apart from performance implications as these counters are updated for every request. An external, persistent storage solution could be plugged in instead, if necessary.

There is also a client side table, called *ValtaTable* that marshals some of the functionality on the client. For example, it can add the user details in a non-kerberized environment. It can also enforce some resource limits directly on the client, avoiding extra costly network round-trips.

Future
======

Once Valta tracks live usage, it could employ heuristics to adapt to changing workloads. This can include reconfiguration of the lower level resource usage, such as enabling read-ahead for long running scan workloads, versus completely random read workloads, where read-ahead is not needed at all.

We also like to hear from you where you see Valta making sense in your current HBase setup. Please feel free to send me email with suggestions (valta@larsgeorge.com).

Building
========

The entire project is based on Maven and self contained, which means after cloning the project you can run Maven like so::

	$ mvn compile

Installing
==========

Valta uses a server-side coprocessor, which needs to be deployed, once the JAR file containing th code has been build.


