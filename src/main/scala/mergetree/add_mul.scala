package mergetree
import chisel3._
import circt.stage.ChiselStage
class AddMul(data_size: Int) extends Module {
  val io = IO(new Bundle {
    val input = Input(new XInputBundle(data_size))
    val output = Output(UInt(data_size.W))
  })
  val a_p_b = RegNext(io.input.input_a + io.input.input_b)
  val a_p_b_m_c = RegNext(a_p_b * io.input.input_c)
  val out = a_p_b_m_c + io.input.input_a
  io.output := out
}
