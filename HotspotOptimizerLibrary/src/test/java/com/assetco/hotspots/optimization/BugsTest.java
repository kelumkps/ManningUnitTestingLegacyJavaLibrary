package com.assetco.hotspots.optimization;

import com.assetco.search.results.Asset;
import com.assetco.search.results.AssetVendor;
import com.assetco.search.results.AssetVendorRelationshipLevel;
import com.assetco.search.results.HotspotKey;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class BugsTest {
    @Test
    public void precedingPartnerWithLongTrailingAssetsDoesNotWin() {
        var partnerVendor = makeVendor(AssetVendorRelationshipLevel.Partner);
        var anotherPartnerVendor = makeVendor(AssetVendorRelationshipLevel.Partner);
        var missing = givenAssetInResultsWithVendor(partnerVendor);
        var assetFromAnother = givenAssetInResultsWithVendor(anotherPartnerVendor);
        List<Asset> expected = new ArrayList<>();
        expected.add(givenAssetInResultsWithVendor(partnerVendor));
        expected.add(givenAssetInResultsWithVendor(partnerVendor));
        expected.add(givenAssetInResultsWithVendor(partnerVendor));
        expected.add(givenAssetInResultsWithVendor(partnerVendor));

        whenOptimize();

        thenHotspotDoesNotHave(HotspotKey.Showcase, missing);
        thenHotspotHasExactly(HotspotKey.Showcase, expected);
    }

    private void thenHotspotHasExactly(HotspotKey showcase, List<Asset> assets) {

    }

    private void thenHotspotDoesNotHave(HotspotKey showcase, Asset... assets) {

    }

    private void whenOptimize() {

    }

    private Asset givenAssetInResultsWithVendor(AssetVendor partnerVendor) {
        return null;
    }

    private AssetVendor makeVendor(AssetVendorRelationshipLevel relationshipLevel) {
        return null;
    }
}
