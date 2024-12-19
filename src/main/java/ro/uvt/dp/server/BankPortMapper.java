package ro.uvt.dp.server;

import java.util.HashMap;
import java.util.Map;

public class BankPortMapper {
    private static final Map<String, Integer> bankPortMap = new HashMap<>();

    static {
        bankPortMap.put("UVT_DP", 12345);
        bankPortMap.put("TEST", 12346);
    }

    public static Integer getPortForBank(String bankCode) {
        return bankPortMap.get(bankCode);
    }

    public static void addBankPortMapping(String bankCode, int port) {
        bankPortMap.put(bankCode, port);
    }
}
