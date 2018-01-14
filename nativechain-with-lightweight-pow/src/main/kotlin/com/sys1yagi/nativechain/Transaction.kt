package com.sys1yagi.nativechain

import com.sys1yagi.nativechain.util.Sha256Hash

data class Transaction(
    val input: List<Input>,
    val output: List<Output>,
    val hash: String = Sha256Hash.digest("$input$output")
) {
    data class Input(val source: Output)

    data class Output(val address: Address, val coin: Coin)
}
