/*
 * Copyright 2018 Comcast Cable Communications Management, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.comcast.ip4s

import scalajs._
import scala.scalajs.js.annotation._
import scala.util.Try

private[ip4s] trait IDNCompanionPlatform {
  private[ip4s] def toAscii(value: String): Option[String] =
    Some(url.domainToASCII(value)).filter(_.nonEmpty)

  private[ip4s] def toUnicode(value: String): String =
    Some(url.domainToUnicode(value)).filter(_.nonEmpty).getOrElse(value)
}

@js.native
@JSImport("url", JSImport.Default)
private[ip4s] object url extends js.Any {
  def domainToASCII(domain: String): String = js.native
  def domainToUnicode(value: String): String = js.native
}
