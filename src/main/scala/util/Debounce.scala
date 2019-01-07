package util

import chisel3._
import chisel3.util._

/**
 * Debounce module for converting a "sloppy" button press
 * into a clean, single pulse.
 *
 * Borrowed from Tze-Chien Chu, aka "PowerChu" at tzechienchu@gmail.com .
 * https://tzechienchu.typepad.com/scala_to_fpga/2018/09/chisel-3-day-8-stopwatch.html
 */
class Debounce extends Module {
  val io = IO(new Bundle {
    val in = Input(Bool())
    val out = Output(Bool())
  })

  val (count,enable) = Counter(true.B,1000000)
  val reg0 = RegEnable(io.in,false.B,enable)
  val reg1 = RegEnable(reg0,false.B,enable)

  io.out := reg0 && !reg1 & enable
}

object Debounce {
  def apply(in:Bool):Bool = {
    val debounce = Module(new Debounce)
    debounce.io.in := in
    debounce.io.out
  }
}
