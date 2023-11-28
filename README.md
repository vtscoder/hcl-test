# Excercise implementation with jUnits and ITs
**Exercises**

_Exercise 1_

Update the client to push a block of data via an existing HTTP API on our test server. The end point to connect to is exposed in the ServerController.
Package com.db.dataplatform.techtest.client.component.impl
Class Method Package Class

_Exercise 2_

ClientImpl pushData com.db.dataplatform.techtest.server.api.controller ServerController
Add functionality to the server to calculate and persist a MD5 checksum of the body of an incoming data block. The end point must return to the client whether the hash matches a client provided checksum. The system policy is that invalid data is wasted space.
_Exercise 3_
Expand server functionality to expose a new GET end point to obtain all persisted blocks that have a given block type. Update the client to call this new end point.

_Exercise 4_

Add a new end point to the server with an appropriate verb, to update an existing block’s block type. The block to update can be identified uniquely in the persistence store by its block name. Add at least API input validation for the block name. Update the client to call the new update end point.

_Exercise 5_

In the existing server push data end point, in addition to persistence, add functionality to push data to the bank’s Hadoop data lake. The URL for the data lake service end point is http://localhost:8090/hadoopserver/pushbigdata and it takes a payload string as the POST request body. The data lake has just gone live in the bank and we are their first customer; instability and long running calls can be expected.