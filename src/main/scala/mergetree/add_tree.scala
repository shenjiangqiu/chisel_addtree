package mergetree
import chisel3._
import circt.stage.ChiselStage

class RawAddTree(num_inputs: Int, data_size: Int) extends Module {
  require(isPow2(num_inputs), "num_inputs must be a power of 2")

  val io = IO(new Bundle {
    val input = Input(Vec(num_inputs, UInt(data_size.W)))
    val output = Output(UInt(data_size.W))
  })

  def mergeTree(inputs: Seq[UInt]): UInt = {
    if (inputs.length == 1) {
      inputs.head
    } else {
      val mid = inputs.length / 2
      val left = RegNext(mergeTree(inputs.take(mid)))
      val right = RegNext(mergeTree(inputs.drop(mid)))
      left + right
    }
  }

  io.output := mergeTree(io.input)
  def isPow2(n: Int): Boolean = {
    (n & (n - 1)) == 0 && n != 0
  }
}
class AddTree(num_inputs: Int, data_size: Int) extends Module {
  require(isPow2(num_inputs), "num_inputs must be a power of 2")

  val io = IO(new Bundle {
    val input = Input(Vec(num_inputs, new TwoInput(data_size)))
    val mode = Input(Bool())
    val output = Output(UInt(data_size.W))
  })

  val pes = Seq.fill(num_inputs)(Module(new SinglePe(data_size)))

  pes.zipWithIndex.foreach { case (pe, i) =>
    pe.io.in := io.input(i)
    pe.io.mode := io.mode
  }

  def mergeTree(inputs: Seq[UInt]): UInt = {
    if (inputs.length == 1) {
      inputs.head
    } else {
      val mid = inputs.length / 2
      val left = RegNext(mergeTree(inputs.take(mid)))
      val right = RegNext(mergeTree(inputs.drop(mid)))
      left + right
    }
  }

  io.output := mergeTree(pes.map(_.io.out))
  def isPow2(n: Int): Boolean = {
    (n & (n - 1)) == 0 && n != 0
  }
}
