package gcd

import circt.stage.ChiselStage

object VerilogMain extends App {
  val verilog = ChiselStage.emitSystemVerilogFile(new DecoupledGcd(16))
}
