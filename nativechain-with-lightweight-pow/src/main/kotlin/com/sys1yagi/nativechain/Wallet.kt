package com.sys1yagi.nativechain

class Wallet(val user: User, val nativeChain: NativeChain) {

    fun balance(): Coin {
        return Coin(user, 0)
    }
}
