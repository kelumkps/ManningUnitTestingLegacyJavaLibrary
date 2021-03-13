package com.assetco.hotspots.optimization;

import com.assetco.search.results.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.URI;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class BugsTest {
    private static final Random random = new Random();
    private SearchResults searchResults;

    @BeforeEach
    public void setup() {
        searchResults = new SearchResults();
    }

    @Test
    public void precedingPartnerWithLongTrailingAssetsDoesNotWin() {
        var partnerVendor = makeVendor(AssetVendorRelationshipLevel.Partner);
        var anotherPartnerVendor = makeVendor(AssetVendorRelationshipLevel.Partner);
        var missing = givenAssetInResultsWithVendor(partnerVendor);
        var assetFromAnother = givenAssetInResultsWithVendor(anotherPartnerVendor);
        List<Asset> expected = new ArrayList<>();
        expected.add(missing);
        expected.add(givenAssetInResultsWithVendor(partnerVendor));
        expected.add(givenAssetInResultsWithVendor(partnerVendor));
        expected.add(givenAssetInResultsWithVendor(partnerVendor));
        expected.add(givenAssetInResultsWithVendor(partnerVendor));

        whenOptimize();

       // thenHotspotDoesNotHave(HotspotKey.Showcase, missing);
        thenHotspotHasExactly(HotspotKey.Showcase, expected);
    }

    private void thenHotspotHasExactly(HotspotKey showcase, List<Asset> expected) {
        Hotspot hotspot = searchResults.getHotspot(showcase);
        Asset[] fromHotspot = hotspot.getMembers().toArray(new Asset[0]);
        Asset[] fromExpected = expected.toArray(new Asset[0]);
        assertArrayEquals(fromExpected, fromHotspot);
    }

    private void thenHotspotDoesNotHave(HotspotKey showcase, Asset... assets) {
        Hotspot hotspot = searchResults.getHotspot(showcase);
        List<Asset> members = hotspot.getMembers();
        for (Asset asset : assets) {
            assertFalse(members.contains(asset));
        }
    }

    private void whenOptimize() {
        SearchResultHotspotOptimizer optimizer = new SearchResultHotspotOptimizer();
        optimizer.optimize(searchResults);
    }

    private Asset givenAssetInResultsWithVendor(AssetVendor vendor) {
        Asset asset = new Asset(string(), string(), URI(), URI(), assetPurchaseInfo(), assetPurchaseInfo(), setOfTopics(), vendor);
        searchResults.addFound(asset);
        return asset;
    }

    private AssetVendor makeVendor(AssetVendorRelationshipLevel relationshipLevel) {
        return new AssetVendor(string(), string(), relationshipLevel, anyLong());
    }

    private static Money money() {
        return new Money(new BigDecimal(anyLong()));
    }

    private static URI URI() {
        return URI.create("https://" + string());
    }

    private static String string() {
        return UUID.randomUUID().toString();
    }

    private static long anyLong() {
        return random.nextInt();
    }

    private static AssetPurchaseInfo assetPurchaseInfo() {
        return new AssetPurchaseInfo(anyLong(), anyLong(), money(), money());
    }

    private static List<AssetTopic> setOfTopics() {
        var result = new ArrayList<AssetTopic>();
        for (var count = 1 + random.nextInt(4); count > 0; --count)
            result.add(anyTopic());

        return result;
    }

    static AssetTopic anyTopic() {
        return new AssetTopic(string(), string());
    }
}
