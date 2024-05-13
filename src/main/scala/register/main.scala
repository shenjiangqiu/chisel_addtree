package register
import java.io._
import circt.stage.ChiselStage
import topk.heappriorityqueue.PriorityQueue
import chisel3._

class ReadWriteSmem(count: Int, data_size: Int) extends Module {
  val io = IO(new Bundle {
    val enable = Input(Bool())
    val write = Input(Bool())
    val addr = Input(UInt(10.W))
    val dataIn = Input(UInt(data_size.W))
    val dataOut = Output(UInt(data_size.W))
  })

  val mem = SyncReadMem(count, UInt(data_size.W))
  // Create one write port and one read port
  mem.write(io.addr, io.dataIn)
  io.dataOut := mem.read(io.addr, io.enable)
}

object Main extends App {
  // Param sizes to generate
  val sizes = List(2, 4, 8, 16, 32, 64, 128, 256)
  val data_sizes = List(8, 16, 32)

  for (size <- sizes) {
    for (data_size <- data_sizes) {
      // Generate the Verilog output
      val verilogCode = ChiselStage.emitSystemVerilog(
        new ReadWriteSmem(size, data_size),
        firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
      )

      // Define the filename
      val fileName = s"ReadWriteSmem-${size}-${data_size}.v"

      // Write the Verilog code to a file
      val pw = new PrintWriter(new File(fileName))
      pw.write(verilogCode)
      pw.close

    }

  }
}
