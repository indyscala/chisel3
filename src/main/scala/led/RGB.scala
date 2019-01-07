package led

import chisel3._
import chisel3.core.withClockAndReset
import chisel3.util.log2Ceil
import chisel3.experimental.RawModule

/**
  * RGB LED stuff
  */
class RGB extends RawModule {
  val clock = IO(Input(Clock()))
  val io = IO(new Bundle {
    val pin_10 = Output(Bool())  // RGB LED: red
    val pin_11 = Output(Bool())  // RGB LED: green
    val pin_12 = Output(Bool())  // RGB LED: blue
  })

  withClockAndReset(clock, false.B) {
    val rgbPins = List(io.pin_10, io.pin_11, io.pin_12)
    val rgbPwms = toList(RGB.pwms(RGBColor(0, 255, 0 )))
    (rgbPins, rgbPwms).zipped
      .foreach( (pin, pwm) => pin := pwm.io.out)
  }

  def toList[T](t3: (T, T, T)): List[T] = List(t3._1, t3._2, t3._3)
}

object RGB {
  val pwm_period = 255
  val pwm_width = log2Ceil(pwm_period)
  println(s"width: $pwm_width")

  def pwm(duty_cycle: Int): PWM = {
    val p = Module(new PWM(pwm_width))
    p.io.period := pwm_period.U
    p.io.duty_cycle := duty_cycle.U
    p.io.enable := true.B
    p
  }

  def pwms(color: RGBColor): (PWM, PWM, PWM) = (pwm(color.r), pwm(color.g), pwm(color.b))
}

case class RGBColor(r: Int, g: Int, b: Int)

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

object RGBVerilog extends App {
  chisel3.Driver.execute(Array("--target-dir", "target/led"), () => new RGB)
}
