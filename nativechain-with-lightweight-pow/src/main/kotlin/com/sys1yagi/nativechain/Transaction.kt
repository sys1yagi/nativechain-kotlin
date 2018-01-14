package com.sys1yagi.nativechain

class Transaction(val input: List<Input>, val output: List<Output>, var status: Status = Status.UnApproved) {

    enum class Status {
        UnApproved,
        Approved
    }

    data class Input(val user: User, val coin: Coin)

    data class Output(val user: User, val coin: Coin)
}
