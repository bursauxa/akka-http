/*
 * Copyright (C) 2009-2022 Lightbend Inc. <https://www.lightbend.com>
 */

package akka.http.impl.util

import akka.io.{ Tcp, Inet }

import scala.collection.immutable

import akka.io.Inet.SocketOption
import com.typesafe.config.Config

private[http] object SocketOptionSettings {
  def fromSubConfig(root: Config, c: Config): immutable.Seq[SocketOption] = {
    def so[T](setting: String)(f: (Config, String) => T)(cons: T => SocketOption): List[SocketOption] =
      c.getString(setting) match {
        case "undefined" => Nil
        case x           => cons(f(c, setting)) :: Nil
      }

    so("so-receive-buffer-size")(_ getIntBytes _)(Inet.SO.ReceiveBufferSize.apply) :::
      so("so-send-buffer-size")(_ getIntBytes _)(Inet.SO.SendBufferSize.apply) :::
      so("so-reuse-address")(_ getBoolean _)(Inet.SO.ReuseAddress.apply) :::
      so("so-traffic-class")(_ getInt _)(Inet.SO.TrafficClass.apply) :::
      so("tcp-keep-alive")(_ getBoolean _)(Tcp.SO.KeepAlive.apply) :::
      so("tcp-oob-inline")(_ getBoolean _)(Tcp.SO.OOBInline.apply) :::
      so("tcp-no-delay")(_ getBoolean _)(Tcp.SO.TcpNoDelay.apply)
  }
}
