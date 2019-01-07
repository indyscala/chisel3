package led

import chisel3._
import chisel3.experimental.RawModule

/**
  * Turn on built-in LED.
  */
class On extends RawModule {
  val io = IO(new Bundle {
    val led = Output(Bool())
  })

  io.led := true.B
}

object OnVerilog extends App {
  chisel3.Driver.execute(Array("--target-dir", "target/led"), () => new On)
}
