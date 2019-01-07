package button

import util.{Debounce, PWM}

import chisel3._
import chisel3.core.withClockAndReset
import chisel3.util._
import chisel3.experimental.RawModule

/**
  * Use 3 buttons to control 3 PWMs of RGB LED in order to mix
  * the colors.
  */
class ColorMixing extends RawModule {
  val clock = IO(Input(Clock()))
  val io = IO(new Bundle {
    val pin_1 = Input(Bool())    // button: red
    val pin_2 = Input(Bool())    // button: green
    val pin_3 = Input(Bool())    // button: blue

    val pin_10 = Output(Bool())  // RGB LED: red
    val pin_11 = Output(Bool())  // RGB LED: green
    val pin_12 = Output(Bool())  // RGB LED: blue
  })

  val reset = Wire(Bool())

  withClockAndReset(clock, reset) {
    // pressing 1st and 3rd button resets counters and resets R, G, and B to off
    reset := Debounce(io.pin_1) && Debounce(io.pin_3)

    val red = Module(new ColorRamp())
    red.io.button := io.pin_1
    io.pin_10 := red.io.led_out

    val green = Module(new ColorRamp())
    green.io.button := io.pin_2
    io.pin_11 := green.io.led_out

    val blue = Module(new ColorRamp())
    blue.io.button := io.pin_3
    io.pin_12 := blue.io.led_out
  }
}

/**
  * Button increments counter which controls PWM to brighten LED.
  */
class ColorRamp() extends Module {
  val io = IO(new Bundle {
    val button = Input(Bool())
    val led_out = Output(Bool())
  })

  val pwm = PWM(8)
  pwm.io.period := 255.U

  val counter = Counter(8)

  when(Debounce(io.button)) {
    counter.inc()
  }

  pwm.io.duty_cycle := counter.value * 32.U
  pwm.io.enable := !(counter.value === 0.U)  // output completely off when counter at 0
  io.led_out := pwm.io.out
}

object ColorMixingVerilog extends App {
  chisel3.Driver.execute(Array("--target-dir", "target/button"), () => new ColorMixing)
}
