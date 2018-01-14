package com.sys1yagi.nativechain.util

import com.sys1yagi.nativechain.Block
import com.sys1yagi.nativechain.Transaction
import com.sys1yagi.nativechain.Address
import com.sys1yagi.nativechain.Coin

val GenesisAddress = Address("1234567890")

val GenesisBlock = Block(
    0,
    "0",
    1465154705,
    listOf(
        Transaction(
            emptyList(),
            listOf(Transaction.Output(GenesisAddress, Coin(50L)))
        )
    ),
    "nativechain genesis block!!",
    "816534932c2b7154836da6afc367695e6337db8a921823784c14378abed4f7d7"
)
