// SPDX-License-Identifier: Apache-2.0
// SPDX-FileCopyrightText: 2024 Jiuyang Liu <liu@jiuyang.me>
package org.chipsalliance.amba.axi4.busip

import chisel3.util.Queue
import chisel3.withClockAndReset
import org.chipsalliance.amba.axi4.bundle._

// This is what RTL designer need to implement, as well as necessary verification signal definitions.
trait AXI4BufferRTL extends HasAXI4BufferInterface {
  withClockAndReset(clock, reset) {
    (in, out) match {
      case (in: HasAW, out: HasAW) =>
        out.aw :<>= Queue.irrevocable(
          in.aw,
          parameter.buffer.depth,
          parameter.buffer.flow,
          parameter.buffer.pipe
        )
    }
    (in, out) match {
      case (in: HasW, out: HasW) =>
        out.w :<>= Queue.irrevocable(
          in.w,
          parameter.buffer.depth,
          parameter.buffer.flow,
          parameter.buffer.pipe
        )
    }
    (in, out) match {
      case (in: HasB, out: HasB) =>
        in.b :<>= Queue.irrevocable(
          out.b,
          parameter.buffer.depth,
          parameter.buffer.flow,
          parameter.buffer.pipe
        )
    }
    (in, out) match {
      case (in: HasAR, out: HasAR) =>
        out.ar :<>= Queue.irrevocable(
          in.ar,
          parameter.buffer.depth,
          parameter.buffer.flow,
          parameter.buffer.pipe
        )
    }
    (in, out) match {
      case (in: HasR, out: HasR) =>
        in.r :<>= Queue.irrevocable(
          out.r,
          parameter.buffer.depth,
          parameter.buffer.flow,
          parameter.buffer.pipe
        )
    }
  }
}
