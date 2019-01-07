package util

import chisel3._

class PWM(width: Int) extends Module {
  val io = IO(new Bundle {
    val duty_cycle = Input(UInt(width.W))
    val period = Input(UInt(width.W))
    val enable = Input(Bool())
    val out = Output(Bool())
  })

  val count = RegInit(0.U(width.W))
  count := count + 1.U

  when (count >= io.period) { count := 0.U }
  io.out := io.enable && (count <= io.duty_cycle)
}

object PWM {
  def apply(width: Int): PWM = Module(new PWM(width))
}
