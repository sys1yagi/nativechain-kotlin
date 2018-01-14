package com.sys1yagi.nativechain

import com.sys1yagi.nativechain.util.DefaultTimeProvider
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
object WalletSpec : Spek({

    describe("get a balance of a address") {
        on("there is no transactions related to a address") {
            val nativeChain = NativeChain(DefaultTimeProvider)
            val address = Address("012345")
            val wallet = Wallet(address, nativeChain)
            it("return empty coin") {
                val balance = wallet.balance()
                assertThat(balance.amount).isEqualTo(0L)
            }
        }

        on("there is a transaction related to a address") {
            val nativeChain = NativeChain(DefaultTimeProvider)
            val address = Address("012345")
            val wallet = Wallet(address, nativeChain)
            val transactions = listOf(
                Transaction(
                    emptyList(),
                    listOf(Transaction.Output(address, Coin(50)))
                )
            )
            val block = nativeChain.generateNextBlock(transactions, "yey!")
            nativeChain.addBlock(block)

            assertThat(wallet.balance().amount).isEqualTo(50L)
        }

        on("there is same transactions related to a address") {
            val nativeChain = NativeChain(DefaultTimeProvider)
            val address1 = Address("012345")
            val output = Transaction.Output(address1, Coin(50))

            val address2 = Address("012346")

            val wallet = Wallet(address1, nativeChain)
            val transactions = listOf(
                Transaction(
                    emptyList(),
                    listOf(output)
                ),
                Transaction(
                    listOf(Transaction.Input(output)),
                    listOf(
                        Transaction.Output(address2, Coin(20L)),
                        Transaction.Output(address1, Coin(30L))
                    )
                )
            )
            val block = nativeChain.generateNextBlock(transactions, "yey!")
            nativeChain.addBlock(block)

            assertThat(wallet.balance().amount).isEqualTo(30L)
        }
    }

})
