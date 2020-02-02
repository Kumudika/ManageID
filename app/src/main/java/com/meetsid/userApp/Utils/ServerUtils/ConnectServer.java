package com.meetsid.userApp.Utils.ServerUtils;

public class ConnectServer {
    //    public static String CONNECTED_API = "BASIC";
    public static String CONNECTED_API = "BLOCK_CHAIN";

    public static MeetSIDRequestAPI connect() {
        if (CONNECTED_API.equals("BASIC")) {
//            RequestAPI.ENDPOINT = "http://aa8f5db45392711e9bc690a34dc6d54a-709304120.us-east-1.elb.amazonaws.com/v1.0/";
            return new APIBasicFlow();
        } else {
//            RequestAPI.ENDPOINT = "http://tokenchain-testnet-1155326049.us-east-1.elb.amazonaws.com/api/";
            return new APIBlockchainFlow();
        }
    }
}
