package com.sys1yagi.nativechain

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.on

class TransactionSpec : Spek({

    describe("transaction") {
        val user = Address("tom")
        val fee = Coin(50L)
        on("mining fee") {
            val input = emptyList<Transaction.Input>()
            val output = listOf(Transaction.Output(user, fee))
            val transaction = Transaction(input, output)
        }
    }
})
