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

import java.nio.charset.StandardCharsets
import scala.scalanative.unsafe._
import scala.scalanative.unsigned._

private[ip4s] trait IDNCompanionPlatform {
  private[ip4s] def toAscii(value: String): Option[String] = {
    val src = stackalloc[CUnsignedInt](MaxLength)
    var i = 0
    while (i < value.length) {
      src(i) = value.codePointAt(i).toUInt
      i += 1
    }

    val dest = stackalloc[CChar](MaxLength)
    val destLength = stackalloc[CSize]()
    !destLength = MaxLength

    val status = punycode.punycode_encode(src, value.length.toUInt, dest, destLength)

    if (status == value.length.toUInt) {
      val chars = new Array[Char]((!destLength).toInt)
      var i = 0
      while (i < chars.length) {
        chars(i) = dest(i).toChar
        i += 1
      }
      Some(new String(chars))
    } else None
  }

  private[ip4s] def toUnicode(value: String): String = Zone { implicit z =>
    val src = stackalloc[CChar](MaxLength)
    var i = 0
    while (i < value.length) {
      src(i) = value.charAt(i).toByte
      i += 1
    }

    val dest = stackalloc[CUnsignedInt](MaxLength)
    val destLength = stackalloc[CSize]()
    !destLength = MaxLength

    val status = punycode.punycode_decode(src, value.length.toUInt, dest, destLength)

    if (status == value.length.toUInt) {
      val codePoints = new Array[Int]((!destLength).toInt)
      var i = 0
      while (i < codePoints.length) {
        codePoints(i) = dest(i).toChar
        i += 1
      }
      new String(codePoints, 0, codePoints.length)
    } else {
      throw new RuntimeException("punycode_decode")
    }
  }

  private[this] final val MaxLength: CSize = 256.toUInt
}

@extern
private object punycode {

  @name("ip4s_punycode_encode")
  def punycode_encode(
      src: Ptr[CUnsignedInt],
      srclen: CSize,
      dst: Ptr[CChar],
      dstlen: Ptr[CSize]
  ): CSize = extern

  @name("ip4s_punycode_decode")
  def punycode_decode(
      src: Ptr[CChar],
      srclen: CSize,
      dst: Ptr[CUnsignedInt],
      dstlen: Ptr[CSize]
  ): CSize = extern

}
