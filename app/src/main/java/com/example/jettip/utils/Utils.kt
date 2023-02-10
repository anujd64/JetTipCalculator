package com.example.jettip.utils

import java.time.temporal.TemporalAmount

fun calculateTotalTip(totalBill: Double, tipPercentage: Int): Double =(totalBill * tipPercentage/100)

fun calculatePerPerson(totalBill: Double, noOfParticipants:Int, tipPercentage: Int) :Double = (totalBill+calculateTotalTip(totalBill= totalBill,tipPercentage= tipPercentage))/noOfParticipants