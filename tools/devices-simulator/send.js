// Copyright (c) Microsoft. All rights reserved.

"use strict";

// How frequently to send data
var frequency = 1000;

// Available protocols
//var Protocol = require("azure-iot-device-amqp").Amqp;
var Protocol = require("azure-iot-device-http").Http;

// Load credentials
require("vm").runInThisContext(require("fs").readFileSync(__dirname + "/credentials.js"));
var hubName = connString.substring(connString.indexOf("=") + 1, connString.indexOf(".azure-devices.net"));

// Instantiate simulators
var TemperatureSimulator = require("./temperature_simulator.js");
var HumiditySimulator = require("./humidity_simulator.js");

// Connect simulators to Azure IoT hub
var devices = [];
var j = 0;
for (var i = 0; i < hubDevices.length ; i++) {
  var deviceId = hubDevices[i][0].deviceId;
  var accessKey = hubDevices[i][0].authentication.SymmetricKey.primaryKey;

  devices[j] = new TemperatureSimulator(hubName, deviceId, accessKey, Protocol, frequency);
  devices[j++].connect();
  devices[j] = new HumiditySimulator(hubName, deviceId, accessKey, Protocol, frequency);
  devices[j++].connect();
}

// Start sending data generated by the simulators
for (var i = 0; i < devices.length ; i++) {
  devices[i].startSending();
}
