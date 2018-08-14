package org.aksw.limes.core.measures.measure.semantic.edgecounting.preprocessing.DB;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import edu.mit.jwi.item.ISynsetID;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class DBImplementation {
    static Logger logger = LoggerFactory.getLogger(DBImplementation.class.getName());

    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> redisConnection;
    private RedisCommands<String, String> redisSyncCommands;

    public final static String SYNSETS_PATHS_TO_ROOT = "synsetsPathsToRoot";
    public final static String SYNSETS_MIN_LENGTHS = "synsetsMinLengths";
    public final static String SYNSETS_MAX_LENGTHS = "synsetsMaxLengths";

    public DBImplementation() {
    }

    public void init() {

        InetAddress ip = null;
        try {

            ip = InetAddress.getLocalHost();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String host = "redis://" + ip.getHostAddress()+":6379";
        //String host = "redis://localhost:6379";
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
        return (redisSyncCommands.randomkey() == null) ? true : false;
    }

    private int decodeLength(String json) {
        if (json == null)
            return -1;
        Gson gson = new Gson();
        return gson.fromJson(json, Integer.class);
    }

    ///////////////////////////////////////////////////////////
    //////////// LARGEST DEPTH SIZE//////////////////////////
    public int getMaxDepth(String synsetID) {

        return decodeLength(redisSyncCommands.hget(SYNSETS_MAX_LENGTHS, synsetID.toString()));
    }

    public void addMaxDepth(String synsetID, int len) {
        Gson gson = new Gson();
        String typeKey = SYNSETS_MAX_LENGTHS;
        String length = gson.toJson(len, Integer.class);
        // add to experiment data store
        redisSyncCommands.hset(typeKey, synsetID, length);
    }

    ///////////////////////////////////////////////////////////
    //////////// SHORTEST DEPTH SIZE//////////////////////////
    public int getMinDepth(String synsetID) {

        return decodeLength(redisSyncCommands.hget(SYNSETS_MIN_LENGTHS, synsetID.toString()));
    }

    public void addMinDepth(String synsetID, int len) {
        Gson gson = new Gson();
        String typeKey = SYNSETS_MIN_LENGTHS;
        String length = gson.toJson(len, Integer.class);
        // add to experiment data store
        redisSyncCommands.hset(typeKey, synsetID, length);
    }

    ///////////////////////////////////////////////////////////
    //////////// ALL PATHS TO ROOT//////////////////////////

    public String serialize(ArrayList<ArrayList<ISynsetID>> myObject) {
        String serializedObject = "";

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutput out = new ObjectOutputStream(bos)) {
            try {
                out.writeObject(myObject);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            final byte[] byteArray = bos.toByteArray();
            serializedObject = Base64.getEncoder().encodeToString(byteArray);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return serializedObject;

    }

    public ArrayList<ArrayList<ISynsetID>> deserialize(String serializedObject) {
        if (serializedObject == null)
            return new ArrayList<ArrayList<ISynsetID>>();

        ArrayList<ArrayList<ISynsetID>> obj = new ArrayList<ArrayList<ISynsetID>>();

        final byte[] bytes = Base64.getDecoder().decode(serializedObject);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInput in = new ObjectInputStream(bis)) {
            obj = (ArrayList<ArrayList<ISynsetID>>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return obj;
    }

    private ArrayList<ArrayList<ISynsetID>> decodeSynsets(String json) {
        if (json == null)
            return new ArrayList<ArrayList<ISynsetID>>();

        return deserialize(json);

    }

    public ArrayList<ArrayList<ISynsetID>> getSynsetPaths(String synsetID) {

        return decodeSynsets(redisSyncCommands.hget(SYNSETS_PATHS_TO_ROOT, synsetID));

    }

    public void addSysnetPaths(String synsetID, ArrayList<ArrayList<ISynsetID>> trees) {

        String typeKey = SYNSETS_PATHS_TO_ROOT;

        String synsetTrees = serialize(trees);

        // add to experiment data store
        redisSyncCommands.hset(typeKey, synsetID, synsetTrees);
    }
    ///////////////////////////////////////////////////////////

    public Map<String, String> getAllPaths() {
        return redisSyncCommands.hgetall(SYNSETS_PATHS_TO_ROOT);
    }

    public void flush() {
        redisSyncCommands.flushall();
    }

    public void close() {
        // redisSyncCommands.flushdb();
        redisConnection.close();
        redisClient.shutdown();
    }
}
