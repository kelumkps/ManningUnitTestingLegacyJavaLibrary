package com.assetco.hotspots.optimization;

import com.assetco.search.results.AssetPurchaseInfo;
import com.assetco.search.results.AssetTopic;
import com.assetco.search.results.Money;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class TestHelper {
    private static final Random random = new Random();

    public static Money money() {
        return new Money(new BigDecimal(anyLong()));
    }

    public static URI URI() {
        return URI.create("https://" + string());
    }

    public static String string() {
        return UUID.randomUUID().toString();
    }

    public static long anyLong() {
        return random.nextInt();
    }

    public static AssetPurchaseInfo assetPurchaseInfo() {
        return new AssetPurchaseInfo(anyLong(), anyLong(), money(), money());
    }

    public static List<AssetTopic> setOfTopics() {
        var result = new ArrayList<AssetTopic>();
        for (var count = 1 + random.nextInt(4); count > 0; --count)
            result.add(anyTopic());

        return result;
    }

    public static AssetTopic anyTopic() {
        return new AssetTopic(string(), string());
    }
}
