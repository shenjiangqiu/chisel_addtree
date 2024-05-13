package mergetree

import circt.stage.ChiselStage
import chisel3._
import java.io.PrintWriter
import java.io.File

class Full(left_size: Int, middle_size: Int, right_size: Int, data_size: Int)
    extends Module {
  val io = IO(
    new Bundle {
      val inputLeft = Input(Vec(left_size, UInt(data_size.W)))
      val inputMiddle = Input(Vec(middle_size, UInt(data_size.W)))
      val inputRight = Input(Vec(right_size, UInt(data_size.W)))
      val output = Output(UInt(data_size.W))
    }
  )
  val left_add_tree = Module(new RawAddTree(left_size, data_size))
  val right_add_tree = Module(new RawAddTree(right_size, data_size))
  val middle = Module(new RawAddTree(middle_size, data_size))
  val mul = Module(new AddMul(data_size))
  mul.io.input.input_a := left_add_tree.io.output
  mul.io.input.input_b := middle.io.output
  mul.io.input.input_c := right_add_tree.io.output

  left_add_tree.io.input := io.inputLeft
  right_add_tree.io.input := io.inputRight
  middle.io.input := io.inputMiddle

  io.output := mul.io.output
}

class XInputBundle(data_size: Int) extends Bundle {
  val input_a = UInt(data_size.W)
  val input_b = UInt(data_size.W)
  val input_c = UInt(data_size.W)
}

class XandAddTree(size: Int, data_size: Int) extends Module {
  val io = IO(new Bundle {
    val input = Input(Vec(size, new XInputBundle(data_size)))
    val output = Output(UInt(data_size.W))
  })
  val X = VecInit(Seq.fill(size)(Module(new AddMul(data_size)).io))
  val y = Module(new RawAddTree(size, data_size))
  for (i <- 0 until size) {
    X(i).input := io.input(i)
    y.io.input(i) := X(i).output
  }
  io.output := y.io.output
}
object Main extends App {
  // Param sizes to generate
  for (size <- Seq(16, 32, 64)) {
    // Your code here, using 'size'
    println(s"Size is $size")
    val verilogCode = ChiselStage.emitSystemVerilog(
      new XandAddTree(size, 16),
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
    // Define the filename
    val fileName = s"XandAddTree-${size}-16.sv"
    // Write the Verilog code to a file
    val pw = new PrintWriter(new File(fileName))
    pw.write(verilogCode)
    pw.close
  }

}
