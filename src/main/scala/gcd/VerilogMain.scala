package gcd

import circt.stage.ChiselStage

object VerilogMain extends App {
  print("Hello World")
  val verilog = ChiselStage.emitSystemVerilogFile(new DecoupledGcd(16))
  println(verilog)
}
