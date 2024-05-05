// SPDX-License-Identifier: Apache-2.0
// SPDX-FileCopyrightText: 2024 Jiuyang Liu <liu@jiuyang.me>
package org.chipsalliance.amba.axi4.busip

import chisel3._
import chisel3.experimental.SerializableModuleParameter
import chisel3.experimental.dataview.DataViewable
import chisel3.layer.{Convention, Layer}
import chisel3.probe.Probe
import org.chipsalliance.amba.axi4.bundle._
import upickle.default

import scala.collection.immutable.SeqMap

object Verification extends Layer(Convention.Bind) {
  object Cover extends Layer(Convention.Bind)
  object Assert extends Layer(Convention.Bind)
  object Debug extends Layer(Convention.Bind)
}

object BufferParameter {
  implicit def rw: default.ReadWriter[BufferParameter] =
    upickle.default.macroRW[BufferParameter]
}

case class BufferParameter(depth: Int, flow: Boolean, pipe: Boolean) {
  require(
    depth > 0,
    "0 depth is forbid to generate, you may need directly connect."
  )
}

object AXI4BufferParameter {
  implicit def rw: default.ReadWriter[AXI4BufferParameter] =
    upickle.default.macroRW[AXI4BufferParameter]
}

case class AXI4BufferParameter(
    bundle: AXI4BundleParameter,
    buffer: BufferParameter
) extends SerializableModuleParameter

class AXI4BufferProbeInterface(val parameter: AXI4BufferParameter)
    extends Bundle {
  val awFull: Bool = Probe(Output(Bool()), Verification)
  val wFull: Bool = Probe(Output(Bool()), Verification)
  val bFull: Bool = Probe(Output(Bool()), Verification)
  val arFull: Bool = Probe(Output(Bool()), Verification)
  val rFull: Bool = Probe(Output(Bool()), Verification)
  val awEmpty: Bool = Probe(Output(Bool()), Verification)
  val wEmpty: Bool = Probe(Output(Bool()), Verification)
  val bEmpty: Bool = Probe(Output(Bool()), Verification)
  val arEmpty: Bool = Probe(Output(Bool()), Verification)
  val rEmpty: Bool = Probe(Output(Bool()), Verification)
}

// TODO: from this example, I observe the necessity of TypeParameter for Bundle.
//       I think this is possible for bus ip developing convince.
//       Let's finish this IP first.
class AXI4BufferInterface(val parameter: AXI4BufferParameter) extends Record {
  // format: off
  override def typeName: String = super.typeName + Seq(
    Some(s"DEPTH${parameter.buffer.depth}"), Option.when(parameter.buffer.flow)("FLOW"), Option.when(parameter.buffer.pipe)("PIPE"),
  ).mkString("_")
  // format: on

  // A stable interface of IO with Verilog IO
  // TODO: add some Probe for verification? e.g. entries counts, stall, etc.
  val elements: SeqMap[String, Data] = SeqMap.from(
    Seq(
      "ACLK" -> Input(Clock()),
      // TODO: we only support sync reset for now.
      "ARESETn" -> Input(Bool()),
      "IN" -> Flipped(verilog.irrevocable(parameter.bundle)),
      "OUT" -> verilog.irrevocable(parameter.bundle),
      "Probe" -> new AXI4BufferProbeInterface(parameter)
    )
  )

  // RTL engineer should be able to access the Chisel type.
  // format: off
  lazy val inRW: Option[AXI4RWIrrevocable] = if (parameter.bundle.isRW) Some(elements("IN").asInstanceOf[AXI4RWIrrevocableVerilog].viewAs[AXI4RWIrrevocable]) else None
  lazy val inRO: Option[AXI4ROIrrevocable] = if (parameter.bundle.isRO) Some(elements("IN").asInstanceOf[AXI4ROIrrevocableVerilog].viewAs[AXI4ROIrrevocable]) else None
  lazy val inWO: Option[AXI4WOIrrevocable] = if (parameter.bundle.isWO) Some(elements("IN").asInstanceOf[AXI4WOIrrevocableVerilog].viewAs[AXI4WOIrrevocable]) else None
  lazy val outRW: Option[AXI4RWIrrevocable] = if (parameter.bundle.isRW) Some(elements("OUT").asInstanceOf[AXI4RWIrrevocableVerilog].viewAs[AXI4RWIrrevocable]) else None
  lazy val outRO: Option[AXI4ROIrrevocable] = if (parameter.bundle.isRO) Some(elements("OUT").asInstanceOf[AXI4ROIrrevocableVerilog].viewAs[AXI4ROIrrevocable]) else None
  lazy val outWO: Option[AXI4WOIrrevocable] = if (parameter.bundle.isWO) Some(elements("OUT").asInstanceOf[AXI4WOIrrevocableVerilog].viewAs[AXI4WOIrrevocable]) else None
  // format: on

  lazy val in: AXI4ChiselBundle = Seq(inRW, inRO, inWO).flatten.head
  lazy val out: AXI4ChiselBundle = Seq(outRW, outRO, outWO).flatten.head
  lazy val probe: AXI4BufferProbeInterface = elements("Probe").asInstanceOf[AXI4BufferProbeInterface]
  lazy val clock: Clock = elements("ACLK").asInstanceOf[Clock]
  lazy val reset: Bool = elements("ARESETn").asInstanceOf[Bool]
}

trait HasAXI4BufferInterface {
  val parameter: AXI4BufferParameter
  val interface: AXI4BufferInterface
  lazy val clock = interface.clock
  lazy val reset = !interface.reset
  lazy val in: AXI4ChiselBundle = interface.in
  lazy val out: AXI4ChiselBundle = interface.out
}
