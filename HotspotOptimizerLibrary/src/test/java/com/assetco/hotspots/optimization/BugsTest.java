package com.assetco.hotspots.optimization;

import com.assetco.search.results.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static com.assetco.hotspots.optimization.TestHelper.*;
import static org.junit.jupiter.api.Assertions.*;

public class BugsTest {
    private SearchResults searchResults;
    private SearchResultHotspotOptimizer optimizer;

    @BeforeEach
    public void setup() {
        searchResults = new SearchResults();
        optimizer = new SearchResultHotspotOptimizer();
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

        thenHotspotHasExactly(HotspotKey.Showcase, expected);
    }

    @Test
    public void lowRankedAssetsAlmostNeverHighlighted() {
        var vendor = makeVendor(AssetVendorRelationshipLevel.Basic);
        var topic1 = anyTopic();
        var topic2 = anyTopic();
        optimizer.setHotTopics(() -> Arrays.asList(topic1, topic2));
        var expectedAssets = givenAssetsWithTopics(vendor, 2, topic2);
        givenAssetsWithTopics(vendor, 3, topic1);
        expectedAssets.add(givenAssetWithTopics(vendor, topic2));

        whenOptimize();

        thenHotspotHas(HotspotKey.Highlight, expectedAssets);
    }

    @Test
    public void itemsWithSellRatesLastDayLastMonthShouldTakeOneSlot() {
        var vendor = makeVendor(AssetVendorRelationshipLevel.Basic);
        var asset = new Asset(string(), string(), URI(), URI(),
                getPurchaseInfo(50000, 50000),
                getPurchaseInfo(4000, 4000),
                setOfTopics(), vendor);
        searchResults.addFound(asset);

        whenOptimize();

        thenHotspotHasExactly(HotspotKey.HighValue, Arrays.asList(asset));
    }

    private void thenHotspotHasExactly(HotspotKey showcase, List<Asset> expected) {
        Hotspot hotspot = searchResults.getHotspot(showcase);
        Asset[] fromHotspot = hotspot.getMembers().toArray(new Asset[0]);
        Asset[] fromExpected = expected.toArray(new Asset[0]);
        assertArrayEquals(fromExpected, fromHotspot);
    }

    private void thenHotspotHas(HotspotKey hotspotKey, List<Asset> expectedAssets) {
        for (var expectedAsset : expectedAssets) {
            assertTrue(searchResults.getHotspot(hotspotKey).getMembers().contains(expectedAsset));
        }
    }

    private void thenHotspotDoesNotHave(HotspotKey showcase, Asset... assets) {
        Hotspot hotspot = searchResults.getHotspot(showcase);
        List<Asset> members = hotspot.getMembers();
        for (Asset asset : assets) {
            assertFalse(members.contains(asset));
        }
    }

    private void whenOptimize() {
        optimizer.optimize(searchResults);
    }

    private Asset givenAssetInResultsWithVendor(AssetVendor vendor) {
        Asset asset = new Asset(string(), string(), URI(), URI(), assetPurchaseInfo(), assetPurchaseInfo(), setOfTopics(), vendor);
        searchResults.addFound(asset);
        return asset;
    }

    private List<Asset> givenAssetsWithTopics(AssetVendor vendor, int count, AssetTopic... topics) {
        var result = new ArrayList<Asset>();
        for (var i = 0; i < count; ++i) {
            result.add(givenAssetWithTopics(vendor, topics));
        }
        return result;
    }

    private Asset givenAssetWithTopics(AssetVendor vendor, AssetTopic... topics) {
        var actualTopics = new ArrayList<AssetTopic>();
        for (var topic : topics) {
            actualTopics.add(new AssetTopic(topic.getId(), topic.getDisplayName()));
        }
        var result = new Asset(string(), string(), URI(), URI(), assetPurchaseInfo(), assetPurchaseInfo(), actualTopics, vendor);
        searchResults.addFound(result);
        return result;
    }

    private AssetVendor makeVendor(AssetVendorRelationshipLevel relationshipLevel) {
        return new AssetVendor(string(), string(), relationshipLevel, anyLong());
    }

    private AssetPurchaseInfo getPurchaseInfo(int timesShown, int timesPurchased) {
        return new AssetPurchaseInfo(timesShown, timesPurchased, new Money(new BigDecimal("0")), new Money(new BigDecimal("0")));
    }
}
