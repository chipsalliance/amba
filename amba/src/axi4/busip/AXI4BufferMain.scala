// SPDX-License-Identifier: Apache-2.0
// SPDX-FileCopyrightText: 2024 Jiuyang Liu <liu@jiuyang.me>
package org.chipsalliance.amba.axi4.busip

import mainargs._
import org.chipsalliance.amba.Elaborator
import org.chipsalliance.amba.axi4.bundle.AXI4BundleParameter

/** This is used for other tools, IP-XACT will also live here in the future. */
object AXI4BufferMain extends Elaborator {
  @main
  case class AXI4BundleParameterMain(
      @arg(name = "idWidth") idWidth: Int,
      @arg(name = "dataWidth") dataWidth: Int,
      @arg(name = "addrWidth") addrWidth: Int,
      @arg(name = "userReqWidth") userReqWidth: Int = 0,
      @arg(name = "userDataWidth") userDataWidth: Int = 0,
      @arg(name = "userRespWidth") userRespWidth: Int = 0,
      @arg(name = "hasAW") hasAW: Boolean = true,
      @arg(name = "hasW") hasW: Boolean = true,
      @arg(name = "hasB") hasB: Boolean = true,
      @arg(name = "hasAR") hasAR: Boolean = true,
      @arg(name = "hasR") hasR: Boolean = true
  ) {
    def convert: AXI4BundleParameter = AXI4BundleParameter(
      idWidth,
      dataWidth,
      addrWidth,
      userReqWidth,
      userDataWidth,
      userRespWidth,
      hasAW,
      hasW,
      hasB,
      hasAR,
      hasR
    )
  }

  implicit def AXI4BundleParameterMainParser
      : ParserForClass[AXI4BundleParameterMain] =
    ParserForClass[AXI4BundleParameterMain]
  @main
  case class BufferParameterMain(
      @arg(name = "depth") depth: Int,
      @arg(name = "flow") flow: Boolean = true,
      @arg(name = "pipe") pipe: Boolean = true
  ) {
    def convert: BufferParameter = BufferParameter(depth, flow, pipe)
  }
  implicit def BufferParameterMainParser: ParserForClass[BufferParameterMain] =
    ParserForClass[BufferParameterMain]

  @main
  case class AXI4BufferParameterMain(
      @arg(name = "bundle") bundle: AXI4BundleParameterMain,
      @arg(name = "buffer") buffer: BufferParameterMain
  ) {
    def convert: AXI4BufferParameter =
      AXI4BufferParameter(bundle.convert, buffer.convert)
  }
  implicit def AXI4BufferParameterMainParser
      : ParserForClass[AXI4BufferParameterMain] =
    ParserForClass[AXI4BufferParameterMain]

  @main
  def config(@arg(name = "parameter") parameter: AXI4BufferParameterMain) =
    configImpl(parameter.convert)

  @main
  def design(
      @arg(name = "parameter") parameter: os.Path =
        os.pwd / s"${getClass.getSimpleName.replace("$", "")}.json",
      @arg(name = "run-firtool") runFirtool: mainargs.Flag
  ) = designImpl[AXI4Buffer, AXI4BufferParameter](parameter, runFirtool.value)
  def main(args: Array[String]): Unit = ParserForMethods(this).runOrExit(args)
}
