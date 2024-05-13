package mergetree

import chisel3._
import chisel3.util._

class TwoInput(data_size: Int) extends Bundle {
  val a = UInt(data_size.W)
  val b = UInt(data_size.W)
}
class SinglePe(data_size: Int) extends Module {
  val io = IO(new Bundle {
    val in = Input(new TwoInput(data_size))
    val mode = Input(Bool())
    val out = Output(UInt(data_size.W))
  })

  val diff = Mux(io.in.a > io.in.b, io.in.a - io.in.b, io.in.b - io.in.a)
  val square = diff * diff

  val product = io.in.a * io.in.b

  io.out := Mux(io.mode, product, square)
}
