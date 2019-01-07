package led

import chisel3._
import chisel3.core.withClockAndReset
import chisel3.experimental.RawModule
import chisel3.util.Counter

/**
  * Blink built-in LED.
  */
class Blink extends RawModule {
  val clock = IO(Input(Clock()))
  val io = IO(new Bundle {
    val led = Output(Bool())
  })

  withClockAndReset(clock, false.B) {
    val blinker = Module(new Blinker(16E6))
    io.led := blinker.io.led
  }
}

class Blinker(clocksBetweenToggle: Double) extends Module {
  val io = IO(new Bundle {
    val led = Output(Bool())
  })

  val counter = Counter(clocksBetweenToggle.toInt)
  val blink_state = RegInit(0.U(1.W))

  counter.inc()
  when(counter.value === 0.U) {
    blink_state := ~blink_state
  }
  io.led := blink_state
}

object BlinkVerilog extends App {
  chisel3.Driver.execute(Array("--target-dir", "target/led"), () => new Blink)
}