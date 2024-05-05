// SPDX-License-Identifier: Apache-2.0
// SPDX-FileCopyrightText: 2024 Jiuyang Liu <liu@jiuyang.me>
package org.chipsalliance.amba.axi4.busip

import chisel3.layer.block
import chisel3.ltl.Sequence.BoolSequence
import chisel3.ltl._
import chisel3.probe.{ProbeValue, define}
import chisel3.withClockAndReset
import org.chipsalliance.amba.axi4.bundle._

// This is what Verification engineer need to implement
trait AXI4BufferDV extends HasAXI4BufferInterface {
  withClockAndReset(clock, reset) {
    block(Verification) {
      (in, out) match {
        case (in: HasAW, out: HasAW) =>
          val awFull = in.aw.valid && !in.aw.ready
          val awFullProbe = ProbeValue(awFull)
          define(interface.probe.awFull, awFullProbe)
          val awEmpty = out.aw.ready && !out.aw.valid
          val awEmptyProbe = ProbeValue(awEmpty)
          define(interface.probe.awEmpty, awEmptyProbe)
          block(Verification.Cover) {
            CoverProperty(awFull, label = Some("AW_FULL"))
            CoverProperty(awEmpty, label = Some("A_WEMPTY"))
          }
      }
      (in, out) match {
        case (in: HasW, out: HasW) =>
          val wFull = in.w.valid && !in.w.ready
          val wFullProbe = ProbeValue(wFull)
          define(interface.probe.wFull, wFullProbe)
          val wEmpty = out.w.ready && !out.w.valid
          val wEmptyProbe = ProbeValue(wEmpty)
          define(interface.probe.wEmpty, wEmptyProbe)
          block(Verification.Cover) {
            CoverProperty(wFull, label = Some("W_FULL"))
            CoverProperty(wEmpty, label = Some("W_EMPTY"))
          }
      }
      (in, out) match {
        case (in: HasB, out: HasB) =>
          val bFull = in.b.valid && !in.b.ready
          val bFullProbe = ProbeValue(bFull)
          define(interface.probe.bFull, bFullProbe)
          val bEmpty = out.b.ready && !out.b.valid
          val bEmptyProbe = ProbeValue(bEmpty)
          define(interface.probe.bEmpty, bEmptyProbe)
          block(Verification.Cover) {
            CoverProperty(bFull, label = Some("B_FULL"))
            CoverProperty(bEmpty, label = Some("B_EMPTY"))
          }
      }
      (in, out) match {
        case (in: HasAR, out: HasAR) =>
          val arFull = in.ar.valid && !in.ar.ready
          val arFullProbe = ProbeValue(arFull)
          define(interface.probe.arFull, arFullProbe)
          val arEmpty = out.ar.ready && !out.ar.valid
          val arEmptyProbe = ProbeValue(arEmpty)
          define(interface.probe.arEmpty, arEmptyProbe)
          block(Verification.Cover) {
            CoverProperty(arFull, label = Some("AR_FULL"))
            CoverProperty(arEmpty, label = Some("A_REMPTY"))
          }
      }
      (in, out) match {
        case (in: HasR, out: HasR) =>
          val rFull = in.r.valid && !in.r.ready
          val rFullProbe = ProbeValue(rFull)
          define(interface.probe.rFull, rFullProbe)
          val rEmpty = out.r.ready && !out.r.valid
          val rEmptyProbe = ProbeValue(rEmpty)
          define(interface.probe.rEmpty, rEmptyProbe)
          block(Verification.Cover) {
            CoverProperty(rFull, label = Some("R_FULL"))
            CoverProperty(rEmpty, label = Some("R_EMPTY"))
          }
      }
    }
  }
}
