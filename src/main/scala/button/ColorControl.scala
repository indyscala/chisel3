package button

import chisel3._
import chisel3.experimental.RawModule

/**
  * Use 3 buttons to control on/off state of colors in RGB LED.
  */
class ColorControl extends RawModule {
  val clock = IO(Input(Clock()))
  val io = IO(new Bundle {
    val pin_1 = Input(Bool())    // button: red
    val pin_2 = Input(Bool())    // button: green
    val pin_3 = Input(Bool())    // button: blue

    val pin_10 = Output(Bool())  // RGB LED: red
    val pin_11 = Output(Bool())  // RGB LED: green
    val pin_12 = Output(Bool())  // RGB LED: blue

    val led    = Output(Bool())  // built-in LED
  })

  val rgbButtons = List(io.pin_1, io.pin_2, io.pin_3)
  val rgbPins = List(io.pin_10, io.pin_11, io.pin_12)

  // indicate any button press on the built-in LED
  io.led := io.pin_1 || io.pin_2 || io.pin_3

  (rgbButtons, rgbPins).zipped
      .foreach( (button, pin) => pin := button )
}

object ColorControlVerilog extends App {
  chisel3.Driver.execute(Array("--target-dir", "target/button"), () => new ColorControl)
}
