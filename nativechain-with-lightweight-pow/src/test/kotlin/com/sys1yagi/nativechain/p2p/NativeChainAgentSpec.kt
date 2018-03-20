package com.sys1yagi.nativechain.p2p

import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.*
import com.sys1yagi.nativechain.NativeChain
import com.sys1yagi.nativechain.p2p.connection.ConnectionFactory
import com.sys1yagi.nativechain.p2p.connection.TryusConnection
import com.sys1yagi.nativechain.util.DefaultTimeProvider
import com.sys1yagi.nativechain.util.GsonConverter
import kotlinx.coroutines.experimental.channels.Channel
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

class NativeChainAgentSpec : Spek({

    describe("connect to peer") {
        on("one peer") {
            val nativeChain = NativeChain(DefaultTimeProvider)
            val jsonConverter = GsonConverter(Gson())
            val connection: TryusConnection = mock()
            val connectionFactory: ConnectionFactory = mock()

            whenever(connectionFactory.createTyrusConnection(any())).thenReturn(connection)
            whenever(connection.connect(any())).thenAnswer {
                val onOpen = it.arguments[0] as (Channel<String>) -> Unit
                onOpen(mock())
                Unit
            }

            val agent = NativeChainAgent(nativeChain, jsonConverter, connectionFactory)
            agent.connectToPeers(listOf(Peer("test")))

            it("send QUERY_LATEST message") {
                verify(connection).send(eq("{'type': 0}"))
            }
        }
    }

    // receive transaction


})
