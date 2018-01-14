package com.sys1yagi.nativechain

class Wallet(val address: Address, private val nativeChain: NativeChain) {

    fun balance(): Coin {
        val inputs = nativeChain.blockchain.flatMap {
            it.transactions.flatMap {
                it.input
                    .filter {
                        it.source.address == address
                    }
                    .map {
                        it.source
                    }
            }
        }
        val outputs = nativeChain.blockchain.flatMap {
            it.transactions.flatMap {
                it.output.filter {
                    it.address == address
                }
            }
        }
        return (outputs - inputs).fold(Coin(0), { a, b ->
            Coin(a.amount + b.coin.amount)
        })
    }
}
