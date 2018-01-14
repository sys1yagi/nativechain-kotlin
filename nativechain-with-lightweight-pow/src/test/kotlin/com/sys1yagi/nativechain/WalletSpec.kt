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
        val nativeChain = NativeChain(DefaultTimeProvider)
        val user = User("012345")
        val wallet = Wallet(user, nativeChain)

        on("there is no transactions related to a address") {
            it("return empty coin") {
                val balance = wallet.balance()
                assertThat(balance.amount).isEqualTo(0L)
            }
        }
    }

})
