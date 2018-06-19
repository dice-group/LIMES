package org.aksw.limes.core.measures.measure.semantic.edgecounting.preprocessing.DB;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class DBImplementation {
    static Logger logger = LoggerFactory.getLogger(DBImplementation.class.getName());

    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> redisConnection;
    private RedisCommands<String, String> redisSyncCommands;

    public final static String SYNSETS_TREES = "synsetsTrees";

    public DBImplementation() {
    }

    public void init() {
        // init redis redisConnection
        InetAddress ip = null;
        try {

            ip = InetAddress.getLocalHost();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // String host = "redis://" + ip.getHostAddress()+":6379";
        String host = "redis://localhost:6379";
        logger.info("host: " + host);
        redisClient = RedisClient.create(host);
        logger.info("Client created");
        redisConnection = redisClient.connect();
        logger.info("Client connected");
        redisSyncCommands = redisConnection.sync();
        logger.info("Client synced");

        logger.info("REDIS init is over");
        // redisSyncCommands.flushdb();
    }

    public boolean isEmpty() {
        return (redisSyncCommands.randomkey() != null) ? true : false;
    }

    private List<List<ISynset>> decodeSynsets(String json) {
        if (json == null)
            return new ArrayList<List<ISynset>>();
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<List<List<ISynset>>>() {
        }.getType());
    }

    public List<List<ISynset>> getSynsetsTrees(ISynsetID synsetID) {
        return decodeSynsets(redisSyncCommands.hget(SYNSETS_TREES, synsetID.toString()));

    }

    public void addSysnetTrees(ISynsetID synsetID, List<List<ISynset>> trees) {
        Gson gson = new Gson();
        String typeKey = SYNSETS_TREES;
        // add to good sequences

        String synsetTrees = gson.toJson(trees);

        // add to experiment data store
        redisSyncCommands.hset(typeKey, synsetID.toString(), synsetTrees);

    }

    public void close() {
        // redisSyncCommands.flushdb();

        redisConnection.close();
        redisClient.shutdown();
    }
}
