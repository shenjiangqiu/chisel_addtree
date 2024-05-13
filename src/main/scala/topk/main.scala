import java.io._
import circt.stage.ChiselStage
import topk.heappriorityqueue.PriorityQueue

object Main extends App {
  // Param sizes to generate
  val sizes = List(16, 32, 64, 128, 256)

  for (size <- sizes) {
    // Generate the Verilog output
    val verilogCode = ChiselStage.emitSystemVerilog(
      new PriorityQueue(size, 2, 32, 1, false),
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )

    // Define the filename
    val fileName = s"PriorityQueue-${size}-2-32-1-false.v"

    // Write the Verilog code to a file
    val pw = new PrintWriter(new File(fileName))
    pw.write(verilogCode)
    pw.close
  }
}
