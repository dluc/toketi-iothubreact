// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iot.iothubreact

import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Date

import com.microsoft.azure.iot.service.sdk.{DeliveryAcknowledgement, Message}

import scala.collection.JavaConverters._
import scala.collection.mutable.Map

object MessageToDevice {

  def apply(content: Array[Byte]) = new MessageToDevice("", content)

  def apply(content: String) = new MessageToDevice("", content.getBytes(StandardCharsets.UTF_8))

  def apply(deviceId: String, content: Array[Byte]) = new MessageToDevice(deviceId, content)

  def apply(deviceId: String, content: String) = new MessageToDevice(deviceId, content.getBytes(StandardCharsets.UTF_8))
}

class MessageToDevice(var deviceId: String, val content: Array[Byte]) {

  private[this] var ack          : DeliveryAcknowledgement     = DeliveryAcknowledgement.None
  private[this] var correlationId: Option[String]              = None
  private[this] var expiry       : Option[java.util.Date]      = None
  private[this] var userId       : Option[String]              = None
  private[this] var properties   : Option[Map[String, String]] = None

  /** Set the acknowledgement level for message delivery: None, NegativeOnly, PositiveOnly, Full
    *
    * @param ack Acknowledgement level
    *
    * @return The object for method chaining
    */
  def ack(ack: DeliveryAcknowledgement): MessageToDevice = {
    this.ack = ack
    this
  }

  /** Set the device ID
    *
    * @param deviceId Device ID
    *
    * @return The object for method chaining
    */
  def to(deviceId: String): MessageToDevice = {
    this.deviceId = deviceId
    this
  }

  /** Set the correlation ID, used in message responses and feedback
    *
    * @param correlationId Correlation ID
    *
    * @return The object for method chaining
    */
  def correlationId(correlationId: String): MessageToDevice = {
    this.correlationId = Option(correlationId)
    this
  }

  /** Set the expiration time in UTC, interpreted by hub on C2D messages
    *
    * @param expiry UTC expiration time
    *
    * @return The object for method chaining
    */
  def expiry(expiry: Instant): MessageToDevice = {
    this.expiry = Some(new Date(expiry.toEpochMilli))
    this
  }

  /** Set the user ID, used to specify the origin of messages generated by device hub
    *
    * @param userId User ID
    *
    * @return The object for method chaining
    */
  def userId(userId: String): MessageToDevice = {
    this.userId = Some(userId)
    this
  }

  /** Replace the current set of properties with a new set
    *
    * @param properties Set of properties
    *
    * @return The object for method chaining
    */
  def properties(properties: Map[String, String]): MessageToDevice = {
    this.properties = Option(properties)
    this
  }

  /** Add a new property to the message
    *
    * @param name  Property name
    * @param value Property value
    *
    * @return The object for method chaining
    */
  def addProperty(name: String, value: String): MessageToDevice = {
    if (properties == None) {
      properties = Some(Map[String, String]())
    }
    this.properties.get += ((name, value))
    this
  }

  /** Returns a message ready to be sent to a device
    *
    * @return Message for the device
    */
  def message: Message = {
    val message = new Message(content)
    message.setTo(deviceId)

    message.setDeliveryAcknowledgement(ack)
    message.setMessageId(java.util.UUID.randomUUID().toString())
    if (correlationId != None) message.setCorrelationId(correlationId.get)
    if (expiry != None) message.setExpiryTimeUtc(expiry.get)
    if (userId != None) message.setUserId(userId.get)
    if (properties != None) message.setProperties(properties.get.asJava)

    message
  }
}
