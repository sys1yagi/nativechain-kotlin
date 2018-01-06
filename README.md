# nativechain-kotlin

https://github.com/lhartikk/naivechain by kotlin

# QuickStart

(set up two connected nodes and mine 1 block)

```
./gradlew jar
java -jar build/libs/nativechain-kotlin-1.0.jar 3001 6001
java -jar build/libs/nativechain-kotlin-1.0.jar 3002 6002 ws://localhost:6001
curl -H "Content-type:application/json" --data '{"data" : "Some data to the first block"}' http://localhost:3001/mineBlock
```

You can use `serverA.sh`, `serverB.sh`

