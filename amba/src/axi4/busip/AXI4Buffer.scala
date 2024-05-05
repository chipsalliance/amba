// SPDX-License-Identifier: Apache-2.0
// SPDX-FileCopyrightText: 2024 Jiuyang Liu <liu@jiuyang.me>
package org.chipsalliance.amba.axi4.busip

import chisel3._
import chisel3.experimental.SerializableModule

/** The AXI4 Buffer.
  * @note
  *   this is a demo for my way of bus ip designing methodology. There will be
  *   multiple implementations, including RTL, DV, and in the future maybe BFM.
  *   Different files owned to different developers, and even can live in
  *   different package for access control. this [[AXI4Buffer]] is the
  *   [[FixedIORawModule]] that observe all package and combine them together.
  *   this is because Chisel doesn't support SystemVerilog bind natively.
  * @todo
  *   - Probe + LTL Irrevocable Assertion support;
  *   - Support IP-XACT and Documentation via OM;
  *   - Better Naming for DV;
  *   - Queue Name issue: chipsalliance/chisel#4055(maybe creating our own Queue?);
  */
class AXI4Buffer(val parameter: AXI4BufferParameter)
    extends FixedIORawModule[AXI4BufferInterface](
      new AXI4BufferInterface(parameter)
    )
    with SerializableModule[AXI4BufferParameter]
    with AXI4BufferRTL
    with AXI4BufferDV
    with Public {
  lazy val interface: AXI4BufferInterface = io
}
